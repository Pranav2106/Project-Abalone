package game.game;
import exceptions.NoWinnerException;
import game.board.Board;
import game.player.Player;
import game.player.Team;
import network.server.MessageType;

public class Game {
  private Board board;
  private Room room;
  private Team[] teams;
  private Player[] players;
  private int turnCounter;
  private boolean moveInProgress;
  private boolean stopGame;
  private Player playerAtTurn;
  private int prevTeam1PlayerAtTurnIndex;
  private int prevTeam2PlayerAtTurnIndex;

  public Game(Player[] players, Room room) {
    this.board = new Board(players);
    this.turnCounter = 96;
    this.players = players;
    this.room = room;
  }

  public Game(Team[] teams, Room room) {
    this.board = new Board(teams);
    this.turnCounter = 96;
    this.teams = teams;
    this.room = room;
  }

  // ------------------ Getters and setters -----------

  public Board getBoard() {
    return board;
  }

  public Player getPlayer(String username) {
    Player player = null;
    if (players != null) {
      for (Player p : players) {
        if (p.getName().equals(username)) {
          player = p;
          break;
        }
      }
    }
    if (teams != null) {
      for (Team t : teams) {
        for (Player p : t.getPlayerArray()) {
          if (p.getName().equals(username)) {
            player = p;
            break;
          }
        }
      }
    }
    return player;
  }

  /**
   * 
   * @param player
   * @return int
   */
  private int getPlayerIndex(Player player) {
    for (int i = 0; i < players.length; i++) {
      if (players[i].equals(player)) {
        return i;
      }
    }
    return -1;
  }

  private int getTeamIndex(Player player) {
    for (int i = 0; i < 2; i++) {
      for (int k = 0; k < 2; k++) {
        if (teams[i].getPlayerArray()[k].equals(player)) {
          return i;
        }
      }
    }
    return -1;
  }

  private int[] getIndexArray(int field1, int field2, int field3) {
    int[] indexes;
    if (field2 == 0) {
      indexes = new int[]{field1};
    } else if (field3 == 0) {
      indexes = new int[]{field1, field2};
    } else {
      indexes = new int[]{field1, field2, field3};
    }
    return indexes;
  }

  private String getColor(String username) {
    return getPlayer(username).getMarble().toString();
  }

  public void doMove(int pos1, int pos2, int pos3, String direction,
      String username) {
    if (moveInProgress) {
      board.move(board.getFieldArray(getIndexArray(pos1, pos2, pos3)),
          direction);
      moveInProgress = false;
      turnCounter--;
      // Send update to all players
      room.sendUpdate(pos1, pos2, pos3, direction, getColor(username));
    }
  }

  private void nextPlayer() {
    // 2 or 3 player mode
    if (players != null) {
      if (playerAtTurn == null) {
        playerAtTurn = players[0
            + (int) (Math.random() * (((players.length - 1) - 0) + 1))];
      } else {
        int newIndex = (getPlayerIndex(playerAtTurn) + 1) % players.length;
        playerAtTurn = players[newIndex];
      }
    }
    // 4 player mode
    else {
      if (playerAtTurn == null) {
        int randTeam = 0 + (int) (Math.random() * ((1 - 0) + 1));
        int randPlayer = 0 + (int) (Math.random() * ((1 - 0) + 1));
        playerAtTurn = teams[randTeam].getPlayerArray()[randPlayer];
        if (randTeam == 0) {
          prevTeam1PlayerAtTurnIndex = randPlayer;
        } else {
          prevTeam2PlayerAtTurnIndex = randPlayer;
        }
      } else {
        int newTeamIndex = 1 - getTeamIndex(playerAtTurn);
        int newPlayerIndex;
        if (getTeamIndex(playerAtTurn) == 0) {
          newPlayerIndex = 1 - prevTeam2PlayerAtTurnIndex;
        } else {
          newPlayerIndex = 1 - prevTeam1PlayerAtTurnIndex;
        }
        playerAtTurn = teams[newTeamIndex].getPlayerArray()[newPlayerIndex];
        if (newTeamIndex == 0) {
          prevTeam1PlayerAtTurnIndex = newPlayerIndex;
        } else {
          prevTeam2PlayerAtTurnIndex = newPlayerIndex;
        }
      }
    }
  }

  private boolean hasWinner() {
    if (players != null) {
      return board.hasWinner(this.players);
    } else {
      return board.hasWinner(this.teams);
    }
  }

  private void findWinner() {
    String username1 = null;
    String username2 = null;
    Boolean draw = false;

    // 2 or 3 player game
    if (players != null) {
      if (board.hasWinner(players)) {
        try {
          username1 = board.getWinner(players).getName();
        } catch (NoWinnerException e) {
          e.printStackTrace();
        }
      } else {
        int scorePlayer1 = players[0].getMarble().getPushed();
        int scorePlayer2 = players[1].getMarble().getPushed();
        int scorePlayer3;
        if (players.length == 2) {
          if (scorePlayer1 > scorePlayer2) {
            username1 = players[0].getName();
          } else if (scorePlayer2 > scorePlayer1) {
            username1 = players[1].getName();
          } else {
            draw = true;
          }
        } else {
          scorePlayer3 = players[2].getMarble().getPushed();
          if ((scorePlayer1 > scorePlayer2) && (scorePlayer1 > scorePlayer3)) {
            username1 = players[0].getName();
          } else if ((scorePlayer2 > scorePlayer1)
              && (scorePlayer2 > scorePlayer3)) {
            username1 = players[1].getName();
          } else if ((scorePlayer3 > scorePlayer1)
              && (scorePlayer3 > scorePlayer2)) {
            username1 = players[2].getName();
          } else {
            draw = true;
          }
        }
      }
    }
    // 4 player game
    else {
      if (board.hasWinner(teams)) {
        try {
          username1 = board.getWinner(teams).getPlayerArray()[0].getName();
          username2 = board.getWinner(teams).getPlayerArray()[1].getName();
        } catch (NoWinnerException e) {
          e.getMessage();
        }
      } else {

      }
    }
    room.sendGover(username1, username2, draw);
  }

  public void play() {
    while (!hasWinner() && !stopGame && turnCounter > 0) {
      nextPlayer();
      room.getServer().getView().showMessage("Game inside room "
          + room.getName() + " / Turncounter: " + turnCounter,
          MessageType.DEBUG);
      room.giveTurn(playerAtTurn.getName(), getColor(playerAtTurn.getName()));
      moveInProgress = true;

      // Wait for player to do move
      while (moveInProgress) {
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    // Game finished normally
    if (!stopGame) {
      findWinner();
    }
    // Game interrupted
    else {
      room.sendGover(null, null, true);
    }
  }

  public void stop() {
    stopGame = true;
  }
}