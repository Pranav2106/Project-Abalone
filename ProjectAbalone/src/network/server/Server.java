package network.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import game.game.Room;
import game.views.ServerTUI;
import game.views.ServerView;
import network.protocols.PM;
import network.protocols.ServerProtocol;

public class Server implements ServerProtocol, Runnable {

  /** Name of the server. */
  private String name;
  /** ServerSocket of this server. */
  private ServerSocket ssock;
  /** List of ClientHandlers, one for each connected client. */
  private HashMap<String, ClientHandler> clients;
  /** List of rooms that are hosted by this server. */
  private HashMap<String, Room> rooms;
  /** View of this server. */
  private ServerTUI view;

  /**
   * Constructs a new server. Initializes the clients list, the view and the
   * next_client_no.
   */
  public Server() {
    clients = new HashMap<String, ClientHandler>();
    view = new ServerTUI(this);
    rooms = new HashMap<String, Room>();
  }

  // ------------------ Main and Run --------------------------

  /** Start a new server. */
  public static void main(String[] args) {
    Server server = new Server();
    new Thread(server).start();
  }

  @Override
  public void run() {
    try {
      initialise();

      while (true) {
        Socket sock = ssock.accept();
        ClientHandler handler = new ClientHandler(sock, this);
        new Thread(handler).start();

        InetSocketAddress sockaddr = (InetSocketAddress) sock
            .getRemoteSocketAddress();

        String name = "Client " + sockaddr.getAddress().toString().substring(1)
            + "/" + sockaddr.getPort();
        String finalName = name;
        boolean foundName = false;
        int counter = 1;

        while (foundName = false) {
          if (clients.containsKey(finalName)) {
            finalName = name + "(" + counter + ")";
          } else {
            foundName = true;
          }
        }

        handler.setClientName(finalName);
        addClient(handler, true);
      }
    } catch (IOException e) {
      view.showMessage("IO Exception. Error message: " + e.getMessage(),
          MessageType.ERROR);
    }
  }

  // ------------------ Getters and setters -----------

  public String getName() {
    return this.name;
  }

  public ServerView getView() {
    return this.view;
  }

  public Room getRoom(String roomName) {
    return rooms.get(roomName);
  }

  // ------------------ Check methods ------------------

  public boolean checkUsernameExists(String username) {
    // Check clients on server
    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      if (e.getValue().getUserName() != null) {
        if (e.getValue().getUserName().equals(username)) {
          return true;
        }
      }
    }

    // Check clients in rooms
    for (Map.Entry<String, Room> e : rooms.entrySet()) {
      if (e.getValue().usernameInRoom(username)) {
        return true;
      }
    }
    return false;
  }

  public boolean checkRoomExists(String name) {
    if (rooms.containsKey(name)) {
      return true;
    }
    return false;
  }

  public boolean checkRoomPassword(String roomName, String password) {
    return rooms.get(roomName).checkPassword(password);
  }

  public boolean checkRoomIsFull(String roomName) {
    return rooms.get(roomName).isFull();

  }

  public boolean checkGameInProgress(String roomName) {
    return rooms.get(roomName).gameInProgress();
  }

  public boolean checkAllPlayersReady(String roomName) {
    return rooms.get(roomName).checkAllReady();
  }

  public String getPlayerData(String roomName) {
    return getRoom(roomName).getPlayerData();
  }

  // ------------------ Other methods ------------------

  private void initialise() {
    view.showMessage("Welcome to the Abalone Game server!\n");
    // this.name = "Max-Server";
    this.name = view.getString("Enter server name (don't use : or ,): ");

    int port = 0;
    boolean exit = false;
    while (!exit) {
      // port = 8888;
      port = view.getPort("Enter port number (1000-9999): ");

      try {
        ssock = new ServerSocket(port);
        exit = true;
      } catch (IOException e) {
        view.showMessage("Port is not available", MessageType.WARN);
      }

      if (ssock == null) {
        if (!view.getBoolean("Do you want to try it again?")) {
          exit = true;
          System.exit(0);
        } ;
      }
    }
    view.showMessage("Server is now running on port " + port + "\n");
    new Thread(view).start();
  }

  // Assuming the client is not already connected
  @Override
  public void addClient(ClientHandler handler, boolean showMessage) {
    clients.put(handler.getClientName(), handler);
    if (showMessage) {
      view.showMessage(handler.getClientName() + " connected",
          MessageType.INFO);
    }
  }

  // Assuming the client is connected to the server
  @Override
  public void removeClient(ClientHandler handler, boolean showMessage) {
    clients.remove(handler.getClientName());
    if (showMessage) {
      view.showMessage(handler.getClientName() + " disconnected",
          MessageType.INFO);
    }
  }

  // Create a room
  // Put clienthandler into it
  // Remove clienthandler from clients inside server
  @Override
  public Room createRoom(String name, String password, int capacity,
      ClientHandler handler) {
    Room room = new Room(name, capacity, password, handler, this);
    room.addClient(handler);
    removeClient(handler, false);
    rooms.put(name, room);
    view.showMessage(handler.getUserName() + " created room " + name
        + " with a capacity of " + capacity, MessageType.INFO);
    return room;
  }

  // Add a ClientHandler to a specific room and remove the ClientHandler from
  // the server
  @Override
  public Room addClientToRoom(String roomName, ClientHandler handler) {
    Room room = getRoom(roomName);
    // Add client to the room
    room.addClient(handler);
    // Remove client from server
    removeClient(handler, false);
    view.showMessage(handler.getUserName() + " joined room " + roomName,
        MessageType.INFO);
    return room;
  }

  @Override
  public void deleteRoom(Room room) {
    String roomName = room.getName();
    rooms.remove(roomName);
    view.showMessage("Deleted room " + roomName, MessageType.INFO);
  }

  // Return a string that contains all the open rooms of the server
  @Override
  public String listRooms() {
    ArrayList<String> rooms = new ArrayList<String>();
    for (Map.Entry<String, Room> e : this.rooms.entrySet()) {
      Room room = e.getValue();
      rooms.add(PM.roomDataToString(room.getName(), room.getAmountOfPlayers(),
          room.getCapacity(), room.hasPassword()));
    }
    return PM.giveRooms(PM.roomsListToString(rooms));
  }

  @Override
  public void broadCastMessage(String message, ClientHandler sender) {
    // Send message to all clients in room
    if (sender.isInRoom()) {
      sender.getRoom().sendMessage(message, sender.getUserName());
      view.showMessage(sender.getUserName() + " send the message " + message
          + " in his room", MessageType.INFO);
    }
    // Send message to all clients on server
    else {
      for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
        e.getValue().send(PM.sayAll(sender.getUserName(), message));
      }
      view.showMessage(
          sender.getUserName() + " globally send the message " + message,
          MessageType.INFO);
    }
  }

  public void startGame(String roomName) {
    if (checkRoomExists(roomName)) {
      if (checkRoomIsFull(roomName)) {
        if (!checkGameInProgress(roomName)) {
          if (checkAllPlayersReady(roomName)) {
            getRoom(roomName).startGame();
            view.showMessage("Game inside room " + roomName + " has started",
                MessageType.INFO);
          } else {
            view.showMessage(
     "Unable to process command. Not all players inside the room are ready to start the game",
                MessageType.WARN);
          }
        } else {
          view.showMessage(
              "Unable to process command. Game inside the room is already started",
              MessageType.WARN);
        }
      } else {
        view.showMessage(
            "Unable to process command. Room has not yet reached capacity",
            MessageType.WARN);
      }
    } else {
      view.showMessage("Unable to process command. Room does not exist",
          MessageType.WARN);
    }
  }

  public void printRoomList() {
    String result = "";

    if (rooms.size() > 0) {
      result += "------------------------------------- Open rooms -------------------------------------\n";
      result += "Name                Current #players   Capacity   InGame   Has password   Password\n";
      for (Map.Entry<String, Room> e : rooms.entrySet()) {
        Room r = e.getValue();

        result += r.getName();
        for (int k = 0; k < (20 - r.getName().length()); k++) {
          result += " ";
        }

        result += r.getAmountOfPlayers();
        result += "                  ";
        result += r.getCapacity();
        result += "          ";
        result += (r.gameInProgress()) ? "true " : "false";
        result += "    ";
        result += (r.hasPassword()) ? "true " : "false";
        result += "          ";
        result += r.getPassword();
        result += "\n";
      }
    } else {
      result = "No open rooms";
    }
    view.showMessage(result);
  }

  public void printClientList() {
    String result = "";

    if (clients.size() > 0 || rooms.size() > 0) {
      result += "--------------------- Clients ---------------------\n";
      result += "Clientname               Username       InRoom\n";

      // For all clients on server
      for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
        ClientHandler c = e.getValue();

        result += c.getClientName();
        for (int k = 0; k < (25 - c.getClientName().length()); k++) {
          result += " ";
        }
        result += c.getUserName();
        for (int k = 0; k < (15 - c.getUserName().length()); k++) {
          result += " ";
        }
        result += c.isInRoom();
        result += "\n";
      }

      // For all rooms
      for (Map.Entry<String, Room> e : rooms.entrySet()) {
        for (Map.Entry<String, ClientHandler> ch : e.getValue().getClients()
            .entrySet()) {
          ClientHandler c = ch.getValue();

          result += c.getClientName();
          for (int k = 0; k < (25 - c.getClientName().length()); k++) {
            result += " ";
          }
          result += c.getUserName();
          for (int k = 0; k < (15 - c.getUserName().length()); k++) {
            result += " ";
          }
          result += c.isInRoom();
          result += "\n";
        }
      }
    }

    if (result.equals("")) {
      result = "No connected clients";
    }

    view.showMessage(result);
  }

  public void sendBroadcastMessage(String message) {
    // Send to clients in rooms
    for (Map.Entry<String, Room> e : rooms.entrySet()) {
      e.getValue().sendMessage(message, "");
    }
    // Send to clients on server
    for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
      e.getValue().send(PM.sayAll("", message));
    }
    view.showMessage("Message " + message + " is sent to all connected clients",
        MessageType.INFO);
  }
}