package network.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.stream.IntStream;

import game.board.Direction;
import game.board.Field;
import game.game.Room;
import network.protocols.PM;

public class ClientHandler implements Runnable {

  /** Socket and In- and OutputStreams */
  private Socket sock;
  private BufferedReader in;
  private BufferedWriter out;
  /** Connected server */
  private Server server;
  private Room room;
  private String clientName;
  private String userName;
  private boolean ready;
  private boolean myTurn;

  /**
   * Constructs a new ClientHandler. Initializes the socket and In- and
   * OutputStreams.
   */
  public ClientHandler(Socket sock, Server server) {
    try {
      in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
      this.sock = sock;
      this.server = server;
    } catch (IOException e) {
      shutdown();
    }
  }

  // ------------------ Run --------------------------

  @Override
  public void run() {
    String msg = null;
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

  // ------------------ Getters and setters -----------

  public void setClientName(String name) {
    this.clientName = name;
  }

  public String getClientName() {
    return this.clientName;
  }

  public String getUserName() {
    return this.userName;
  }

  public boolean isReady() {
    return this.ready;
  }

  public boolean isInRoom() {
    if (this.room != null) {
      return true;
    }
    return false;
  }

  public Room getRoom() {
    return this.room;
  }

  public void setMyTurn(boolean myTurn) {
    this.myTurn = myTurn;
  }

  // ------------------ Check methods ------------------

  private boolean checkConnected() {
    if (this.userName == null) {
      send(PM.error(PM.E_ILLEGAL_REQUEST));
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

  private boolean checkUsername(String name) {
    if (this.userName != null) {
      send(PM.error(PM.E_ALREADY_CONNECTED));
      return false;
    }
    if (name == null || PM.containsIllegalCharacter(name)
        || !(name.length() >= 3 && name.length() <= 10)) {
      send(PM.error(PM.E_INVALID_USERNAME));
      return false;
    }
    if (server.checkUsernameExists(name)) {
      send(PM.error(PM.E_DUPLICATE_USERNAME));
      return false;
    }
    return true;
  }

  private boolean checkRoomName(String name) {
    if (name == null || PM.containsIllegalCharacter(name)
        || !(name.length() >= 3 && name.length() <= 10)) {
      send(PM.error(PM.E_INVALID_ROOM_NAME));
      return false;
    }
    return true;
  }

  private boolean checkPassword(String password) {
    if (password == null || PM.containsIllegalCharacter(password)) {
      send(PM.error(PM.E_INVALID_PASSWORD));
      return false;
    }
    return true;
  }

  private int checkCapacity(String capacity) {
    int cap = 0;
    try {
      cap = Integer.parseInt(capacity);
    } catch (NumberFormatException e) {
    }

    if (!(cap >= 2 && cap <= 4)) {
      send(PM.error(PM.E_INVALID_CAPACITY));
      return -1;
    }
    return cap;
  }

  private boolean checkMyTurn() {
    if (!myTurn) {
      send(PM.error(PM.E_NOT_YOUR_TURN));
      return false;
    }
    return true;
  }

  private boolean isValidDirection(String direc) {
    if (direc.equals("L") || direc.equals("R") || direc.equals("UL")
        || direc.equals("UR") || direc.equals("DL") || direc.equals("DR")) {
      return true;
    }
    return false;
  }

  private boolean isValidField(int index) {
    if (index >= 1 && index <= 61) {
      return true;
    }
    return false;
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

  // ---------------- Other methods ------------------

  private void handleCommand(String msg) {
    if (msg != null) {
      if (userName != null)
        server.getView().showMessage(
            "Received from " + userName + ": '" + msg + "'", MessageType.DEBUG);
      String[] split = msg.split(PM.LDL);
      switch (PM.getKeyWord(msg)) {
        // Connect to server
        case PM.CONNECT :
          if (checkLength(split, 2)) {
            String userName = split[1];

            if (checkUsername(userName)) {
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              this.userName = userName;
              server.getView().showMessage(
                  clientName + " is now registered as " + this.userName,
                  MessageType.INFO);
              send(PM.ok());
            }
          }
          break;
        // Disconnect from server
        case PM.DISCONNECT :
          if (checkLength(split, 1) && checkConnected()) {
            shutdown();
            if (isInRoom()) {
              // Remove player from game !?!?!?!
            }
            send(PM.ok());
          }
          break;
        // Create a room
        case PM.CREATE :
          if (checkLength(split, 4) && checkConnected()) {
            String roomName = split[1];
            String password = split[2];
            int capacity = checkCapacity(split[3]);

            if (checkRoomName(roomName)) {
              if (!server.checkRoomExists(roomName)) {
                if (checkPassword(password) && (capacity != -1)) {
                  this.room = server.createRoom(roomName, password, capacity,
                      this);
                  send(PM.ok());
                }
              } else {
                send(PM.error(PM.E_DUPLICATE_ROOM_NAME));
              }
            }
          }
          break;
        // Join a room
        case PM.JOIN :
          if (split.length == 2 || split.length == 3) {
            if (checkConnected()) {
              String roomName = split[1];
              String password = "";

              if (split.length == 3) {
                password = split[2];
              }

              if (!isInRoom()) {
                if (server.checkRoomExists(roomName)) {
                  if (server.checkRoomPassword(roomName, password)) {
                    if (!server.checkGameInProgress(roomName)) {
                      if (!server.checkRoomIsFull(roomName)) {
                        this.room = server.addClientToRoom(roomName, this);
                        send(PM.ok());
                      } else {
                        send(PM.error(PM.E_ROOM_FULL));
                      }
                    } else {
                      send(PM.error(PM.E_GAME_IN_PROGRESS));
                    }
                  } else {
                    send(PM.error(PM.E_INCORRECT_PASSWORD));
                  }
                } else {
                  send(PM.error(PM.E_ROOM_NOT_EXISTING));
                }
              } else {
                send(PM.error(PM.E_ALREADY_IN_ROOM));
              }
            }
          } else {
            send(PM.error(PM.E_UNKNOWN_FORMAT));
          }
          break;
        // List all open rooms
        case PM.LIST :
          if (checkLength(split, 1) && checkConnected()) {
            send(server.listRooms());
            server.getView().showMessage(
                userName + " requested a list of open rooms", MessageType.INFO);
          }
          break;
        // Set client to ready so the game can start
        case PM.READY :
          if (checkLength(split, 1) && checkConnected()) {
            if (isInRoom()) {
              if (!room.gameInProgress()) {
                if (!this.ready) {
                  this.ready = true;
                  server.getView().showMessage(
                      userName + " is ready to start his game inside room "
                          + room.getName(),
                      MessageType.INFO);
                  send(PM.ok());
                } else {
                  send(PM.error(PM.E_ILLEGAL_REQUEST));
                }
              } else {
                send(PM.error(PM.E_GAME_IN_PROGRESS));
              }
            } else {
              send(PM.error(PM.E_NOT_IN_ROOM));
            }
          }
          break;
        // Client requests a move
        case PM.MOVE :
          if (split.length >= 3 && split.length <= 5) {
            if (checkConnected()) {
              if (isInRoom()) {
                if (room.gameInProgress()) {
                  if (checkMyTurn()) {
                    int field1 = 0;
                    int field2 = 0;
                    int field3 = 0;

                    if (!isValidDirection(split[4])) {
                      send(PM.error(PM.E_INVALID_DIRECTION));
                      break;
                    }

                    Direction dir = Direction.valueOf(split[4]);

                    // Parse positions from message string
                    try {
                      if (!split[1].equals("")) {
                        field1 = Integer.parseInt(split[1]);
                        if (!isValidField(field1)) {
                          send(PM.error(PM.E_INVALID_POSITION));
                          break;
                        }
                      }
                      if (!split[2].equals("")) {
                        field2 = Integer.parseInt(split[2]);
                        if (!isValidField(field2)) {
                          send(PM.error(PM.E_INVALID_POSITION));
                          break;
                        }
                      }
                      if (!split[3].equals("")) {
                        field3 = Integer.parseInt(split[3]);
                        if (!isValidField(field3)) {
                          send(PM.error(PM.E_INVALID_POSITION));
                          break;
                        }
                      }
                    } catch (NumberFormatException e) {
                      send(PM.error(PM.E_INVALID_POSITION));
                      break;
                    }

                    if (!room.getGame().getBoard().validMove(
                        getIndexArray(field1, field2, field3), dir.toString(),
                        room.getGame().getPlayer(this.userName))) {
                      send(PM.error(PM.E_ILLEGAL_MOVE));
                      break;
                    }

                    room.getGame().doMove(field1, field2, field3,
                        dir.toString(), this.userName);
                    // server.getView().showMessage(userName + " inside room " +
                    // room.getName() + " did the following move -> fields: " +
                    // field1 + "," + field2 + "," + field3 + " direction: " +
                    // dir.toString(), MessageType.INFO);
                    this.myTurn = false;
                    send(PM.ok());
                  }
                } else {
                  send(PM.error(PM.E_NOT_IN_GAME));
                }
              } else {
                send(PM.error(PM.E_NOT_IN_ROOM));
              }
            }
          } else {
            send(PM.error(PM.E_UNKNOWN_FORMAT));
          }
          break;
        // Clients asks for players in room
        case PM.GETPLAYERS :
          if (checkLength(split, 1) && checkConnected()) {
            if (isInRoom()) {
              send(PM.PLAYERDATA + server.getPlayerData(this.room.getName()));
            } else {
              send(PM.error(PM.E_NOT_IN_ROOM));
            }
          }
          break;
        // Client wants to quit, so leave the room
        case PM.QUIT :
          if (checkLength(split, 1) && checkConnected()) {
            if (isInRoom()) {
              String roomName = room.getName();
              room.removeClient(this);
              room = null;
              send(PM.ok());
              server.getView().showMessage(userName + " left room " + roomName,
                  MessageType.INFO);
            } else {
              send(PM.error(PM.E_NOT_IN_ROOM));
            }
          }
          break;
        // Client wants to say something
        case PM.SAY :
          if (checkLength(split, 2) && checkConnected()) {
            String message = split[1];

            if (!PM.containsIllegalCharacter(message)) {
              server.broadCastMessage(message, this);
              send(PM.ok());
            } else {
              send(PM.error(PM.E_ILLEGAL_CHARACTER));
            }
          }
          break;
        case PM.ASK_BOARD :
          if (checkLength(split, 1) && checkConnected()) {
            if (isInRoom()) {
              if (room.gameInProgress()) {
                String result = "";
                Field[] fields = room.getGame().getBoard()
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
          break;
        case PM.FAIL :
          server.getView()
              .showMessage("Client " + clientName
                  + " responded with the following fail message: " + split[1],
                  MessageType.ERROR);
          break;
        default :
          send(PM.error(PM.E_UNKNOWN_FORMAT));
          server.getView().showMessage("Unknown command received: " + msg,
              MessageType.ERROR);
          break;
      }
    }
  }

  private void shutdown() {
    try {
      in.close();
      out.close();
      sock.close();
    } catch (IOException e) {
      server.getView().showMessage(
          "IOException occured while shutting down ClientHandler of "
              + this.clientName,
          MessageType.ERROR);
    }
    server.removeClient(this, true);
  }

  public void send(String msg) {
    try {
      out.write(msg);
      out.newLine();
      out.flush();
      if (userName != null) {
        server.getView().showMessage("Sent to " + userName + ": '" + msg + "'",
            MessageType.DEBUG);
      }
    } catch (IOException e) {
      server.getView().showMessage(
          "IOException occured while sending a message to " + this.clientName,
          MessageType.ERROR);
    }
  }
}
