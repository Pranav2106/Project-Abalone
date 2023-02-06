package game.game;

import java.util.LinkedHashMap;
import java.util.Map;

import game.board.Marble;
import game.player.HumanPlayer;
import game.player.Player;
import game.player.Team;
import network.protocols.PM;
import network.server.ClientHandler;
import network.server.Server;

public class Room implements Runnable {
  private String name;
  private int capacity;
  private String password;
  private Game game;
  // HasMap of clients. Key = clientName , Value = ClientHandler
  private LinkedHashMap<String, ClientHandler> clients;
  // Name of client that created the room
  private Server server;

  public static void main(String[] args) {
    System.out.println();
  }

  public Room(String name, int capacity, String password, ClientHandler client,
      Server server) {
    this.name = name;
    this.capacity = capacity;
    this.password = password;
    this.server = server;
    clients = new LinkedHashMap<String, ClientHandler>();
  }

  // ---------------------- Run -----------------------

  @Override
  public void run() {
    game.play();
    game = null;
  }

  // ------------------ Getters and setters -----------

  public String getName() {
    return this.name;
  }

  public int getAmountOfPlayers() {
    return this.clients.size();
  }

  public boolean checkPassword(String password) {
    return (this.password).equals(password);
  }

  public int getCapacity() {
    return this.capacity;
  }

  public boolean hasPassword() {
    return !this.password.equals("");
  }

  public boolean isFull() {
    return capacity == clients.size();
  }

  public boolean gameInProgress() {
    return game != null;
  }

  public boolean clientInRoom(String clientName) {
    return clients.containsKey(clientName);
  }

  public Game getGame() {
    return game;
  }

  public String getPassword() {
    return this.password;
  }

  public Server getServer() {
    return this.server;
  }

  public LinkedHashMap<String, ClientHandler> getClients() {
    return this.clients;
  }

  public boolean checkAllReady() {
    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      if (!e.getValue().isReady()) {
        return false;
      }
    }
    return true;
  }

  // ------------------ Other methods -----------------

  public String getPlayerData() {
    String result = "";
    Marble[] marbles = Marble.values();
    int index = Marble.Y.ordinal();
    int i = 0;

    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      result += PM.LDL;
      result += e.getValue().getUserName() + "," + marbles[index + i];
      i++;
    }
    return result;
  }

  public boolean usernameInRoom(String username) {
    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      if (e.getValue().getUserName().equals(username)) {
        return true;
      }
    }
    return false;
  }

  public void addClient(ClientHandler client) {
    clients.put(client.getClientName(), client);
  }

  public void removeClient(ClientHandler client) {
    if (gameInProgress()) {
      game.stop();
      while (game != null) {
      }
    }

    clients.remove(client.getClientName());
    if (clients.size() == 0) {
      server.deleteRoom(this);
    }
  }

  public void startGame() {
    Player[] players = new Player[capacity];
    Team[] teams;

    Marble[] marbles = Marble.values();
    int index = Marble.Y.ordinal();
    int i = 0;

    players = new Player[capacity];

    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      players[i] = new HumanPlayer(e.getValue().getUserName(),
          marbles[index + i].toString());
      i++;
    }

    // Create players
    if (capacity == 2 || capacity == 3) {
      game = new Game(players, this);
    }
    // Create teams
    else {
      teams = new Team[2];
      Team team1 = new Team(players[0], players[1]);
      Team team2 = new Team(players[2], players[3]);
      teams[0] = team1;
      teams[1] = team2;

      game = new Game(teams, this);
    }

    sendStart();
    new Thread(this).start();
  }

  public void sendMessage(String message, String username) {
    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      ClientHandler client = e.getValue();
      client.send(PM.sayAll(username, message));
    }
  }

  public void giveTurn(String username, String color) {
    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      ClientHandler client = e.getValue();
      client.send(PM.giveTurn(username, color));
      client.setMyTurn(true);
    }
  }

  public void sendUpdate(int pos1, int pos2, int pos3, String direction,
      String color) {
    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      ClientHandler client = e.getValue();
      client.send(PM.updateMove(pos1, pos2, pos3, direction, color));
    }
  }

  public void sendGover(String username1, String username2, boolean draw) {
    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      ClientHandler client = e.getValue();
      client.send(PM.gameOver(username1, username2, draw));
    }
  }

  public void sendStart() {
    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      ClientHandler client = e.getValue();
      client.send(PM.startGame());
    }
  }
}
