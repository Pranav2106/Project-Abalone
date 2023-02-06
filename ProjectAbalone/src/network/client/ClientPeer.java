package network.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.stream.IntStream;

import exceptions.ServerUnavailableException;
import game.board.Board;
import game.board.Direction;
import game.board.Field;
import game.board.Marble;
import game.player.ComputerPlayer;
import game.player.HumanPlayer;
import game.player.Player;
import game.player.Team;
import network.protocols.ClientProtocol;
import network.protocols.PM;

public class ClientPeer implements ClientProtocol, Runnable {
  private Client client;
  private Socket ssock;
  private BufferedReader in;
  private BufferedWriter out;
  private Board board;

  // Status of this client
  private boolean connectedToServer;
  private boolean inRoom;
  private String roomName;
  private boolean gameInProgress;
  private boolean myTurn;
  private boolean ready;
  private boolean initBoard;
  private boolean computerPlayer;
  private ComputerPlayer player;

  // Ongoing requests to server
  private boolean connectSend;
  private boolean disconnectSend;
  private boolean saySend;
  private boolean moveSend;
  private boolean listSend;
  private boolean playersSend;
  private boolean joinSend;
  private boolean createSend;
  private boolean quitSend;
  private boolean readySend;

  public ClientPeer(Socket sock, Client client) {
    ssock = sock;
    this.client = client;
  }

  // ------------------ Run --------------------------

  @Override
  public void run() {
    String msg;
    try {
      msg = in.readLine();
      while (msg != null) {
        handleCommand(msg);
        msg = in.readLine();
      }
      shutdown();
    } catch (IOException e) {
      shutdown();
    }
  }

  // -------------- Getters and setters -----------------

  public boolean getConnectedToServer() {
    return this.connectedToServer;
  }

  public boolean getConnectSend() {
    return this.connectSend;
  }

  // ---------------- Check methods ---------------------

  public boolean requestOngoing() {
    if (connectSend || saySend || moveSend || listSend || joinSend || createSend
        || quitSend || disconnectSend || readySend) {
      client.getView().showMessage(
          "Still handling another command, please wait till that command is processed");
      return true;
    }
    return false;
  }

  private boolean connectedToServer() {
    if (!connectedToServer) {
      client.getView().showMessage(
          "Unable to process command. You are not connected to any server");
      return false;
    }
    return true;
  }

  private boolean inRoom() {
    if (!inRoom) {
      client.getView()
          .showMessage("Unable to process command. You are not in a room");
      return false;
    }
    return true;
  }

  private boolean notInRoom() {
    if (inRoom) {
      client.getView()
          .showMessage("Unable to process command. You are already in a room");
      return false;
    }
    return true;
  }

  private boolean gameNotInProgress() {
    if (!gameInProgress) {
      client.getView().showMessage(
          "Unable to process command. The game is not yet started");
      return true;
    }
    return false;
  }

  private boolean gameInProgress() {
    if (gameInProgress) {
      client.getView()
          .showMessage("Unable to process command. The game already started");
      return true;
    }
    return false;
  }

  private boolean ready() {
    if (ready) {
      client.getView()
          .showMessage("Unable to process command. You are already ready");
      return true;
    }
    return false;
  }

  private boolean myTurn() {
    if (!myTurn) {
      client.getView()
          .showMessage("Unable to process command. It is not your turn");
      return false;
    }
    return true;
  }

  private boolean checkLength(String[] split, int len) {
    if (split.length != len) {
      send(PM.error(PM.E_UNKNOWN_FORMAT));
      return false;
    }
    return true;
  }

  private boolean validMove(int pos1, int pos2, int pos3, String dir) {
    int[] indexes;
    if (pos2 == 0) {
      indexes = new int[]{pos1};
    } else if (pos3 == 0) {
      indexes = new int[]{pos1, pos2};
    } else {
      indexes = new int[]{pos1, pos2, pos3};
    }

    if (!board.validMove(indexes, dir, player)) {
      client.getView()
          .showMessage("Unable to process command. That move is not valid");
      return false;
    }
    return true;
  }

  // ----------------- Other methods --------------------

  public void initialise() throws ServerUnavailableException {
    try {
      in = new BufferedReader(new InputStreamReader(ssock.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(ssock.getOutputStream()));
    } catch (IOException e) {
      shutdown();
      throw new ServerUnavailableException("Server is unreachable.");
    }
  }

  private void handleCommand(String msg) {
    if (msg != null) {
      String[] split = msg.split(PM.LDL);
      switch (PM.getKeyWord(msg)) {
        case PM.TURN :
          if (checkLength(split, 3)) {
            String username = split[1];
            if (username.equals(client.getUsername())) {
              myTurn = true;
              client.getView().showMessage("It's your turn!");

              if (computerPlayer) {
                client.getView().showMessage("AI will now make a move");
                player.makeMove();
                String dir = player.getDirection();
                int field1 = 0;
                int field2 = 0;
                int field3 = 0;
                int[] indexes = player.getIndexes();
                if (indexes.length >= 1) {
                  field1 = indexes[0];
                }
                if (indexes.length >= 2) {
                  field2 = indexes[1];
                }
                if (indexes.length == 3) {
                  field3 = indexes[2];
                }
                makeMove(field1, field2, field3, Direction.valueOf(dir));
              }
            } else {
              client.getView().showMessage("It's " + username + "'s turn");
            }
            send(PM.ok());
          }
          break;
        case PM.START :
          if (checkLength(split, 1)) {
            gameInProgress = true;
            client.getView().showMessage(
                "Your game inside room " + roomName + " just started");
            send(PM.ok());
            send(PM.requestPlayerData());
            initBoard = true;
          }
          break;
        case PM.ROOMS :
          if (split.length > 1) {
            client.getView().showMessage(
                "-------------------------- Open rooms --------------------------",
                false);
            client.getView().showMessage(
                "Name                Current #players   Capacity   Has password",
                false);

            String line = "";
            for (int i = 1; i < split.length; i++) {
              line = "";
              String[] data = split[i].split(PM.DL);
              line += data[0];
              // Add spacing
              for (int k = 0; k < (20 - data[0].length()); k++) {
                line += " ";
              }
              line += data[1];
              line += "                  ";
              line += data[2];
              line += "          ";
              line += data[3];
              if (i == split.length - 1) {
                client.getView().showMessage(line);
              } else {
                client.getView().showMessage(line, false);
              }
            }
          } else {
            client.getView()
                .showMessage("There are no open rooms on the server");
          }
          send(PM.ok());
          listSend = false;
          break;
        case PM.PLAYERDATA :
          if (split.length > 1) {
            client.getView().showMessage(
                "----------- Players inside room -----------", false);
            client.getView().showMessage("Username                      Color",
                false);

            String line = "";
            for (int i = 1; i < split.length; i++) {
              line = "";
              String[] data = split[i].split(PM.DL);
              line += data[0];
              // Add spacing
              for (int k = 0; k < (30 - data[0].length()); k++) {
                line += " ";
              }
              line += Marble.getString(Marble.valueOf(data[1]));
              if (i == split.length - 1) {
                client.getView().showMessage(line);
              } else {
                client.getView().showMessage(line, false);
              } ;
            }

            if (initBoard) {
              Player[] players = new Player[split.length - 1];

              for (int i = 0; i < split.length - 1; i++) {
                String[] data = split[i + 1].split(PM.DL);

                if (data[0].equals(client.getUsername())) {
                  ComputerPlayer AI = new ComputerPlayer(data[0], data[1],
                      board);
                  players[i] = AI;
                  player = AI;
                } else {
                  players[i] = new HumanPlayer(data[0], data[1]);
                }
              }

              // Create players
              if (split.length - 1 == 2 || split.length - 1 == 3) {
                board = new Board(players);
              }
              // Create teams
              else {
                Team[] teams = new Team[2];
                Player player1 = null;
                Player player2 = null;
                Player player3 = null;
                Player player4 = null;

                for (Player p : players) {
                  switch (p.getMarble()) {
                    case Y :
                      player1 = p;
                      break;
                    case W :
                      player2 = p;
                    case B :
                      player3 = p;
                      break;
                    case R :
                      player4 = p;
                      break;
                    default :
                      break;
                  }
                }

                Team team1 = new Team(player1, player2);
                Team team2 = new Team(player3, player4);
                teams[0] = team1;
                teams[1] = team2;

                board = new Board(teams);
              }
              this.player.setBoard(board);

              client.getView().showMessage(
                  "Use the indexes of the board below to enter your move during the game");
              client.getView().showMessage(board.toStringNumbers(), false);
              printBoardAndScore();
              initBoard = false;
            }
          }
          break;
        case PM.UPDATE :
          if (checkLength(split, 6)) {
            int field1 = 0;
            int field2 = 0;
            int field3 = 0;
            Direction dir = Direction.valueOf(split[4]);

            // Parse positions from message string
            try {
              if (!split[1].equals("")) {
                field1 = Integer.parseInt(split[1]);
              }
              if (!split[2].equals("")) {
                field2 = Integer.parseInt(split[2]);
              }
              if (!split[3].equals("")) {
                field3 = Integer.parseInt(split[3]);
              }
            } catch (NumberFormatException e) {
              send(PM.error(PM.E_UNKNOWN_FORMAT));
              break;
            }

            updateBoard(field1, field2, field3, dir);
            send(PM.ok());
          }
          break;
        case PM.GOVER :
          // There is a winner
          if (split.length == 2) {
            if (split[1].equals(client.getUsername())) {
              client.getView().showMessage("Nice job! You won the game.");
            } else {
              client.getView()
                  .showMessage("You lost... " + split[1] + " won the game.");
            }
          }
          // There are two winners
          else if (split.length == 3) {
            if (split[1].equals(client.getUsername())
                || split[2].equals(client.getUsername())) {
              client.getView().showMessage(
                  "Nice job! You and you teammate have won the game.");
            } else {
              client.getView().showMessage("You and your teammate lost... "
                  + split[1] + " and " + split[2] + " won the game.");
            }
          }
          // Draw
          else {
            client.getView().showMessage("Nobody won the game! It is a draw.");
          }
          client.getView().showMessage(
              "If you want to play another game you can stay in the room, otherwise type leave to exit the room");

          // Reset status variables
          gameInProgress = false;
          myTurn = false;
          ready = false;
          board = null;
          player = null;
          break;
        case PM.SAYALL :
          if (checkLength(split, 3)) {
            String sender = split[1];
            String message = split[2];

            if (sender.equals("")) {
              client.getView().showMessage("<SERVER>: " + message);
            } else {
              client.getView().showMessage(sender + ": " + message);
            }
            send(PM.ok());
          }
          break;
        case PM.ASK_BOARD :
          if (checkLength(split, 1)) {
            if (inRoom) {
              if (gameInProgress) {
                String result = "";
                Field[] fields = board
                    .getFieldArray(IntStream.rangeClosed(1, 61).toArray());
                for (Field f : fields) {
                  result += f.getMarble() + PM.DL;
                }
                send(PM.giveBoard(result.substring(0, result.length() - 1)));
              } else {
                send(PM.error(PM.E_NOT_IN_GAME));
              }
            } else {
              send(PM.error(PM.E_NOT_IN_ROOM));
            }
          }
          break;
        case PM.OK :
          if (connectSend) {
            connectedToServer = true;
            connectSend = false;
            client.getView().showMessage("Succesfully connected to server");
            client.getView().showHelpMenu();
          } else if (disconnectSend) {
            connectedToServer = false;
            disconnectSend = false;
            client.getView().showMessage("Succesfully disconnected to server");
          } else if (saySend) {
            saySend = false;
          } else if (moveSend) {
            moveSend = false;
            myTurn = false;
          } else if (joinSend) {
            inRoom = true;
            joinSend = false;
            if (computerPlayer) {
              client.getView().showMessage(
                  "You are now in room " + roomName + " with AI enabled");
            } else {
              client.getView().showMessage(
                  "You are now in room " + roomName + " with AI disabled");
            }
          } else if (createSend) {
            inRoom = true;
            createSend = false;
            if (computerPlayer) {
              client.getView().showMessage("Succesfully created room "
                  + roomName + " and added you to it with AI enabled");
            } else {
              client.getView().showMessage("Succesfully created room "
                  + roomName + " and added you to it with AI disabled");
            }
          } else if (quitSend) {
            inRoom = false;
            quitSend = false;
            gameInProgress = false;
            myTurn = false;
            client.getView().showMessage("You left room " + roomName);
            roomName = null;
            computerPlayer = false;
          } else if (readySend) {
            ready = true;
            readySend = false;
            client.getView().showMessage(
                "You are ready to start the game inside room " + roomName);
          }
          break;
        case PM.FAIL :
          if (split[1].equals(PM.E_DUPLICATE_USERNAME)) {
            client.getView().showMessage(
                "That username is already taken. Please enter another username");
          } else {
            client.getView().showMessage(
                "ERROR - Server responded with the following fail message: "
                    + split[1]);
          }

          connectSend = false;
          disconnectSend = false;
          saySend = false;
          moveSend = false;
          listSend = false;
          playersSend = false;
          joinSend = false;
          createSend = false;
          quitSend = false;
          readySend = false;
          break;
        default :
          client.getView()
              .showMessage("Unknown command received from the server : " + msg);
          break;
      }
    }
  }

  public void send(String msg) {
    try {
      out.write(msg);
      out.newLine();
      out.flush();
    } catch (IOException e) {
      client.getView().showMessage(
          "Something went wrong while sending a message to the server... Error information: "
              + e.getMessage());
    }
  }

  private void shutdown() {
    try {
      in.close();
      out.close();
      ssock.close();
    } catch (IOException e) {
      client.getView().showMessage(
          "Something went wrong while shutting down the ClientPeer... Error information: "
              + e.getMessage());
    }
    client.getView().showMessage("Connection with server lost...");
  }

  // Connect with server using username
  @Override
  public void connectToServer(String username) {
    if (!requestOngoing()) {
      client.getView().showMessage(
          "Registering on server as " + client.getUsername() + "...", false);
      send(PM.requestConnectToServer(username));
      connectSend = true;
    }
  }

  @Override
  public void disconnectFromServer() {
    if (!requestOngoing() && connectedToServer()) {
      send(PM.requestDisconnectFromServer());
      disconnectSend = true;
    }
  }

  @Override
  public void createRoom(String name, String password, int capacity,
      boolean AI) {
    if (!requestOngoing() && connectedToServer() && notInRoom()) {
      this.roomName = name;
      send(PM.requestCreateRoom(name, password, capacity));
      computerPlayer = AI;
      createSend = true;
    }
  }

  @Override
  public void joinRoom(String name, String password, boolean AI) {
    if (!requestOngoing() && connectedToServer() && notInRoom()) {
      this.roomName = name;
      send(PM.requestJoinRoom(name, password));
      computerPlayer = AI;
      joinSend = true;
    }
  }

  @Override
  public void quitRoom() {
    if (!requestOngoing() && connectedToServer() && inRoom()) {
      send(PM.requestQuitRoom());
      quitSend = true;
    }
  }

  @Override
  public void listRooms() {
    if (!requestOngoing() && connectedToServer()) {
      send(PM.requestListRooms());
      listSend = true;
    }
  }

  @Override
  public void listPlayers() {
    if (!requestOngoing() && connectedToServer() && inRoom()) {
      send(PM.requestPlayerData());
      playersSend = true;
    }
  }

  @Override
  public void makeMove(int pos1, int pos2, int pos3, Direction dir) {
    if (!requestOngoing() && connectedToServer() && inRoom() && myTurn()
        && validMove(pos1, pos2, pos3, dir.toString())) {
      send(PM.requestMove(pos1, pos2, pos3, dir.toString()));
      moveSend = true;
    }
  }

  @Override
  public void readyToGo() {
    if (!requestOngoing() && connectedToServer() && inRoom() && !ready()
        && !gameInProgress()) {
      send(PM.requestReady());
      readySend = true;
    }
  }

  @Override
  public void hint() {
    if (!requestOngoing() && connectedToServer() && inRoom()
        && !gameNotInProgress()) {
      player.makeMove();
      String dir = player.getDirection();
      int[] indexes = player.getIndexes();
      if (indexes.length == 1) {
        client.getView().showMessage(
            "Try the following move: " + indexes[0] + " " + dir, false);
      } else if (indexes.length == 2) {
        client.getView().showMessage("Try the following move: " + indexes[0]
            + "," + indexes[1] + " " + dir, false);
      } else {
        client.getView().showMessage("Try the following move: " + indexes[0]
            + "," + indexes[1] + "," + indexes[2] + " " + dir, false);
      }
      client.getView().showMessage("", false);
    }
  }

  @Override
  public void updateBoard(int pos1, int pos2, int pos3, Direction dir) {
    int arrayLength = 1;
    if (pos2 == 0) {
      arrayLength = 2;
    } else if (pos3 == 0) {
      arrayLength = 3;
    }

    int[] indexes;
    if (pos2 == 0) {
      indexes = new int[]{pos1};
    } else if (pos3 == 0) {
      indexes = new int[]{pos1, pos2};
    } else {
      indexes = new int[]{pos1, pos2, pos3};
    }

    board.move(board.getFieldArray(indexes), dir.toString());
    if (computerPlayer) {
      client.getView().showMessage("AI did the following move: " + pos1 + ","
          + pos2 + "," + pos3 + " " + dir.toString());
    }
    client.getView().showMessage("", false);
    printBoardAndScore();
  }

  @Override
  public void sendChatMessage(String message) {
    if (!requestOngoing() && connectedToServer()) {
      send(PM.requestChat(message));
      saySend = true;
    }
  }

  public void printBoardAndScore() {
    client.getView().showMessage("--------- Current board ---------");
    client.getView().showMessage(board.toString(), false);
    client.getView().showMessage("--------- Current score ---------");
    Player[] players = board.getPlayerArray();
    // Players
    if (players != null) {
      String line = "";
      for (Player p : players) {
        line = "";
        line += p.getName() + " (" + Marble.getString(p.getMarble()) + "): "
            + p.getMarble().getPushed();
        client.getView().showMessage(line, false);
      }
    }
    // Teams
    else {
      Team[] teams = board.getTeamArray();
      Player player1 = teams[0].getPlayerArray()[0];
      Player player2 = teams[0].getPlayerArray()[1];
      Player player3 = teams[1].getPlayerArray()[0];
      Player player4 = teams[1].getPlayerArray()[1];

      client.getView()
          .showMessage("Team1: " + player1.getName() + " ("
              + Marble.getString(player1.getMarble()) + ") "
              + player1.getMarble().getPushed() + " & " + player2.getName()
              + " (" + Marble.getString(player2.getMarble()) + ") "
              + player2.getMarble().getPushed(), false);
      client.getView()
          .showMessage("Team2: " + player3.getName() + " ("
              + Marble.getString(player3.getMarble()) + ") "
              + player3.getMarble().getPushed() + " & " + player4.getName()
              + " (" + Marble.getString(player4.getMarble()) + ") "
              + player4.getMarble().getPushed(), false);
    }
    client.getView().showMessage("", false);
  }

  public void printNumberBoard() {
    if (!requestOngoing() && connectedToServer() && inRoom()
        && !gameNotInProgress()) {
      client.getView().showMessage("", false);
      client.getView().showMessage(board.toStringNumbers(), false);
    }
  }
}
