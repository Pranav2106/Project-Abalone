package network.protocols;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class PM {

  /*
   * Terminology: 
   * Keyword: The keyword is the first part of a message that shows what function it will have. 
   * Example: "c:TheLegend27", here is "c" the keyword.
   * Direction: This is the direction the marbles move. 
   * This should be one of the following: L, R, UL, UR, DL, DR. DL : This is the delimiter
   * for seperating different values in a message. 
   * Example: "c:TheLegend27" LDL: This is the delimiter for seperating items that are listed For
   * example, a list of all games: "game1:4:4:true,game2:1:3:false, ..."
   */

  // Delimiters
  public static final String DL = ","; // A delimiter for seperating data in objects
  public static final String LDL = ":"; // A delimiter for seperating objects

  // Directions
  public static final String LEFT = "L";
  public static final String RIGHT = "R";
  public static final String UP_LEFT = "UL";
  public static final String UP_RIGHT = "UR";
  public static final String DOWN_LEFT = "DL";
  public static final String DOWN_RIGHT = "DR";

  // Colors
  public static final String BLACK = "Y"; // Represents a yellow marble
  public static final String WHITE = "W"; // Represents a white marble
  public static final String RED = "R"; // Represents a red marble
  public static final String BLUE = "B"; // Represents a blue marble
  public static final String EMPTY = "E"; // Represents an empty field

  // Keywords for the client
  public static final String CONNECT = "c"; // For saying you want to connect to the server
  public static final String DISCONNECT = "d"; // For saying you want to disconnect from the server
  public static final String CREATE = "create"; // For saying you want to create/ a room
  public static final String JOIN = "join"; // For saying you want to join a room
  public static final String LIST = "list"; // For saying you want to get a list of rooms
  public static final String READY = "ready"; // For stating you are ready to start the game
  public static final String MOVE = "move"; // For moving marbles
  public static final String GETPLAYERS = "getplayers"; // For getting the players in that room
  public static final String QUIT = "quit"; // For saying you exit the room
  public static final String SAY = "say"; // For saying a chat message

  // Keywords for the server
  public static final String START = "start"; // For telling clients the game starts
  public static final String TURN = "turn"; // For giving a client the turn to move
  public static final String UPDATE = "update"; // For updating a move to all clients
  public static final String GOVER = "gover"; // For telling clients the game has stopped
  public static final String SAYALL = "sayall"; // For sending a chat message
  public static final String ROOMS = "rooms"; // For responding with a list of current rooms
  public static final String PLAYERDATA = "playerdata"; // For responding with player data

  // Keywords for both the server and the client

  // For asking the board from either client or server
  public static final String ASK_BOARD = "askboard";

  public static final String GIVE_BOARD = "giveboard"; // For responding with the board data

  // Error responses
  public static final String OK = "ok";
  public static final String FAIL = "fail";

  // If the request is sent at a strange moment //
  public static final String E_ILLEGAL_REQUEST = "illegal_request";
  // If the format of the message is unknown/incorrect
  public static final String E_UNKNOWN_FORMAT = "unknown_format";
  // If a player wants to connect with a duplicate name
  public static final String E_DUPLICATE_USERNAME = "duplicate_name";
  // If a player wants to connect with an illegal name
  public static final String E_INVALID_USERNAME = "invalid_name";
  // If the player is already connected
  public static final String E_ALREADY_CONNECTED = "already_connected";
  // If a player creates a room with a name that already exists
  public static final String E_DUPLICATE_ROOM_NAME = "duplicate_game_name";
  // If a player creates a room with an illegal name
  public static final String E_INVALID_ROOM_NAME = "invalid_game_name";
  // If a player creates a room with an illegal password
  public static final String E_INVALID_PASSWORD = "invalid_password";
  // If a player creates a room with an invalid player capacity
  public static final String E_INVALID_CAPACITY = "invalid_capacity";
  // If a player tries to join a non-existing room
  public static final String E_ROOM_NOT_EXISTING = "not_existing";
  // If a player sends an incorrect password
  public static final String E_INCORRECT_PASSWORD = "incorrect_password";
  // If a player tries to join a room that is full
  public static final String E_ROOM_FULL = "room_full";
  // If a player wants to join a room while already being in one
  public static final String E_ALREADY_IN_ROOM = "already_in_room";
  // If a player wants to start while
  public static final String E_GAME_IN_PROGRESS = "game_in_progress";
  // If a player tries to make marbles move from an invalid position
  public static final String E_INVALID_POSITION = "invalid_position";
  // If a player tries to move marbles in an invalid direction;
  public static final String E_INVALID_DIRECTION = "invalid_direction";
  // If a player breaks the rules by executing this move
  public static final String E_ILLEGAL_MOVE = "illegal_move";
  // If a player tries to make a move while it's not his turn
  public static final String E_NOT_YOUR_TURN = "not_your_turn";
  // If a player tries to send a chat message containing an illegal character
  public static final String E_ILLEGAL_CHARACTER = "illegal_character";
  // If a player tries to leave a game while not being in a game
  public static final String E_NOT_IN_GAME = "not_in_game";
  // If a player tries to be ready while not in a room
  public static final String E_NOT_IN_ROOM = "not_in_room";

  // =============== Helpful functions =============== //
  /**
   * . Check if a string contains an illegal character.
   * 
   * @param text
   *          about the message for illegal character
   * @return true if text contains an illegal character, false otherwise
   */
  public static boolean containsIllegalCharacter(String text) {
    String txt = (text == null) ? "" : text;
    return (txt.contains(DL) || txt.contains(LDL));
  }

  /**
   * . Return the keyword of a message
   * 
   * @param message
   *          is the message that contains a keyword
   * @return the keyword
   */
  public static String getKeyWord(String message) {
    String msg = (message == null) ? "" : message;
    return msg.split(LDL)[0];
  }

  /**
   * . Return data of a room in the right format to put it in a list
   * 
   * @param roomName
   *          is the name of the room
   * @param amountOfPlayers
   *          is the amount of players that currently are in the room
   * @param capacity
   *          is the maximum amount of players in that room
   * @param hasPassword
   *          is the boolean value that tells if the room has a password
   * @return a string version of a room E.g. "room1,2,3,true" or "room2,4,4,false"
   */
  public static String roomDataToString(String roomName, int amountOfPlayers,
      int capacity, boolean hasPassword) {
    String name = (roomName == null) ? "" : roomName;
    return name + DL + amountOfPlayers + DL + capacity + DL + hasPassword;
  }
  /**
   * . Returns a string that contains a list of string representations of rooms
   * 
   * @param rooms
   *          is a list of rooms in string format
   * @return a string version of a list of rooms
   *     E.g. "room1,2,3,true:room2,4,4,false: ... :room69,1,2,true"
   */
  public static String roomsListToString(ArrayList<String> rooms) {
    StringBuilder bs = new StringBuilder();
    String del = "";
    for (String room : rooms) {
      bs.append(del);
      bs.append(room);
      del = LDL;
    }
    return bs.toString();
  }
  /**
   * Returns a string that contains a list of colors in a field.
   * 
   * @param fields
   *          is an arraylist of color strings. Use the string in this class for the correct color
   * @return a string that can be built with for sending the board data through a socket 
   *      E.g. B,B,B,W,E,E,E,b,E,b,b,b,b,R,R,R ... , R,B
   */
  public static String fieldListToString(ArrayList<String> fields) {
    StringBuilder bs = new StringBuilder();
    String del = "";
    for (String field : fields) {
      bs.append(del);
      bs.append(field);
      del = DL;
    }
    return bs.toString();
  }
  // =============== Client messages =============== //
  /**
   * . Builds a string according to the protocol for sending a connectionRequest to the server
   * 
   * @param username
   * @return a string in the correct format that can be sent through the socket E.g. "c:TheLegend27"
   */
  public static String requestConnectToServer(String username) {
    String name = (username == null) ? "" : username;
    return CONNECT + LDL + name;
  }
  /**
   * . Builds a string according to the protocol for sending a disconnectRequest to the server
   * 
   * @return a string in the correct format that can be sent through the socket E.g. "d"
   */
  public static String requestDisconnectFromServer() {
    return DISCONNECT;
  }
  /**
   * . Builds a string according to the protocol for sending a createGame request to the server
   * 
   * @param gameName
   *          is the name of the room
   * @param password
   *          is the password for joining the room. Leave empty String for no password
   * @param capacity
   *          is the maximum amount of players for that room
   * @return a string in the correct format that can be sent through the socket 
   *      E.g. "create:room1:qwerty:4" or "create:room1::4"
   */
  public static String requestCreateRoom(String gameName, String password,
      int capacity) {
    String name = (gameName == null) ? "" : gameName;
    String pass = (password == null) ? "" : password;
    return CREATE + LDL + name + LDL + pass + LDL + capacity;
  }
  /**
   * . Builds a string according to the protocol for sending a joinRoom request to the server
   * 
   * @param gameName
   *          is the name of the room you want to join
   * @param password
   *          is the password of the room. Give empty string if there is no password
   * @return a string in the correct format that can be sent through the socket 
   *     E.g. "join:room1:qwerty" or "join:room1:"
   */
  public static String requestJoinRoom(String gameName, String password) {
    String name = (gameName == null) ? "" : gameName;
    String pass = (password == null) ? "" : password;
    return JOIN + LDL + name + LDL + pass;
  }
  /**
   * . Builds a string according to the protocol for sending a roomList request to the server
   * 
   * @return a string in the correct format that can be sent through the socket E.g. "list"
   */
  public static String requestListRooms() {
    return LIST;
  }
  /**
   * . Builds a string according to the protocol for sending a ready message to the server
   * 
   * @return a string in the correct format that can be sent through the socket E.g. "ready"
   */
  public static String requestReady() {
    return READY;
  }
  /**
   * . Builds a string according to the protocol for making a move
   * 
   * @param pos1
   *          is the position of the first marble. Leave pos empty if no marble is moved
   * @param pos2
   *          is the position of the second marble
   * @param pos3
   *          is the position of the third marble
   * @param direction
   *          is the direction the marbles should move. Use a direction constant from this class
   * @return a string in the correct format that can be sent through the socket
   *       E.g. "move:3:4:5:L" or "move:3:::UR"
   */
  public static String requestMove(int pos1, int pos2, int pos3,
      String direction) {
    String dir = (direction == null) ? "" : direction;
    if (pos2 < 1 || pos2 > 61) {
      return MOVE + LDL + pos1 + LDL + LDL + LDL + dir;
    } else if (pos3 < 1 || pos3 > 61) {
      return MOVE + LDL + pos1 + LDL + pos2 + LDL + LDL + dir;
    }
    return MOVE + LDL + pos1 + LDL + pos2 + LDL + pos3 + LDL + dir;
  }
  /**
   * . Builds a string according to the protocol for requesting data of players in a room
   * 
   * @return a string in the correct format that can be sent through the socket E.g. "getplayers"
   */
  public static String requestPlayerData() {
    return GETPLAYERS;
  }
  /**
   * . Builds a string according to the protocol for leaving your current room
   * 
   * @return a string in the correct format that can be sent through the socket E.g. "quit"
   * 
   */
  public static String requestQuitRoom() {
    return QUIT;
  }
  /**
   * . Builds a string according to the protocol for sending a chat message
   * 
   * @param message
   * @return a string in the correct format that can be sent through the socket
   *       E.g. "say:Knock knock"
   */
  public static String requestChat(String message) {
    String msg = (message == null) ? "" : message;
    return SAY + LDL + msg;
  }

  // =============== Server messages =============== //
  /**
   * . Builds a string according to the protocol for notifying clients the game starts
   * 
   * @return a string in the correct format that can be sent through the socket E.g. "start"
   */
  public static String startGame() {
    return START;
  }
  /**
   * . Builds a string according to the protocol for giving the turn to a player
   * 
   * @param username
   *          is the user to which the turn is handend
   * @return a string in the correct format that can be sent through the socket 
   *       E.g. "turn:TheLegend27"
   */
  public static String giveTurn(String username, String color) {
    String name = (username == null) ? "" : username;
    String c = (color == null) ? "" : color;
    return TURN + LDL + name + LDL + c;
  }
  /**
   * . Builds a string according to the protocol for updating a move to all players
   * 
   * @param pos1
   *          is the position of the first marble. Enter -1 for no position
   * @param pos2
   *          is the position of the second marble. Enter -1 for no position
   * @param pos3
   *          is the position of the third marble. Enter -1 for no position
   * @param direction
   *          is the direction of the move
   * @param color
   *          is the color of the update
   * @return a string in the correct format that can be sent through the socket
   *       E.g. "update:TheLegend27:2:3:4:L" or "update:TheLegend27:4:::UL"
   */
  
  public static String updateMove(int pos1, int pos2, int pos3,
      String direction, String color) {
    String dir = (direction == null) ? "" : direction;
    String m1 = (pos1 <= 0) ? "" : Integer.toString(pos1);
    String m2 = (pos2 <= 0) ? "" : Integer.toString(pos2);
    String m3 = (pos3 <= 0) ? "" : Integer.toString(pos3);
    String c = (color == null) ? "" : color;
    return UPDATE + LDL + m1 + LDL + m2 + LDL + m3 + LDL + dir + LDL + c;
  }
  
  /**
   * . Builds a string according to the protocol for informing the player that the game stopped
   * 
   * @param username
   *          is the name of the user that won. Leave empty if it is a draw
   * @return a string in the correct format that can be sent through the socket 
   *       E.g. "gover:TheLegend27" or "gover:"
   */
  public static String gameOver(String username1, String username2,
      boolean draw) {
    String name1 = (username1 == null) ? "" : username1;
    String name2 = (username2 == null) ? "" : username2;
    if (draw) {
      return GOVER + LDL + "";
    }
    return GOVER + LDL + name1 + LDL + name2;
  }
  /**
   * . Builds a string according to the protocol for broadcasting 
   * a chat message to allplayers in a room
   * 
   * @param username
   *          is the name of the user that sent the message
   * @param message
   *          is the message that has been sent
   * @return a string in the correct format that can be sent through the socket
   *       E.g. "sayall:TheLegend27:GG ez, git gud u n00bs"
   */
  public static String sayAll(String username, String message) {
    String name = (username == null) ? "" : username;
    String msg = (message == null) ? "" : message;
    return SAYALL + LDL + name + LDL + msg;
  }
  /**
   * . Builds a string according to the protocol for giving a list of all current rooms
   * 
   * @param rooms
   *          is the list of rooms and their data
   * @return a string in the correct format that can be sent through the socket 
   *        E.g. "rooms:room1,2,4,true:room2,3,3,false: ... :room69,1,2,true"
   */
  public static String giveRooms(String rooms) {
    String rms = (rooms == null) ? "" : rooms;
    return ROOMS + LDL + rms;
  }
  /**
   * . Builds a string according to the protocol for responding with an ok to the client
   * 
   * @return a string in the correct format that can be sent through the socket E.g. "ok"
   */
  public static String ok() {
    return OK;
  }
  /**
   * . Builds a string according to the protocol for responding with a fail message to the client
   * 
   * @param error
   *          is the error that happened
   * @return a string in the correct format that can be sent through the socket 
   *         E.g. "fail:unknown_format"
   */
  public static String error(String error) {
    String er = (error == null) ? "" : error;
    return FAIL + LDL + er;
  }

  // =============== Server/Client messages =============== //
  /**
   * . Builds a string according to the protocol for asking the board of the client or server
   * 
   * @return a string in the correct format that can be sent through the socket E.g. "askboard"
   */
  public static String askBoard() {
    return ASK_BOARD;
  }
  /**
   * . Builds a string according to the protocol for giving the board data to the server or client
   * 
   * @param boardData
   *          is the data of the board. Use
   * @return a string in the correct format that can be sent through the socket 
   *     E.g. "giveboard:R,R,B,b,W,E,E,E, ... ,W,W"
   */
  public static String giveBoard(String boardData) {
    return GIVE_BOARD + LDL + boardData;
  }
}