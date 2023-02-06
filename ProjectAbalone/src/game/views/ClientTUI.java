package game.views;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.ExitProgramException;
import game.board.Direction;
import network.client.Client;
import network.protocols.PM;

public class ClientTUI implements ClientView {

  private Client client;
  private Scanner input;
  private PrintWriter console;

  public ClientTUI(Client client) {
    this.client = client;
    input = new Scanner(System.in);
    console = new PrintWriter(System.out, true);
  }

  public void start() {
    try {
      while (true) {
        String msg = input.nextLine();
        handleInput(msg);
      }
    } catch (ExitProgramException e) {
      try {
        showMessage(e.getMessage(), false);
        showMessage("Shutting down client...", false);
        Thread.sleep(3000);
        showMessage("Still busy shutting down...", false);
        Thread.sleep(2000);
        System.exit(0);
      } catch (InterruptedException ex) {
        System.exit(0);
      }
    }
  }

  @Override
  public void handleInput(String input) throws ExitProgramException {
    if (input != null) {
      String[] split = input.split(PM.LDL);
      switch (PM.getKeyWord(input)) {
        case "exit" :
          throw new ExitProgramException("User indicated to exit");
        case "help" :
          showHelpMenu();
          break;
        case "rooms" :
          client.getPeer().listRooms();
          break;
        case "join" :
          if (checkLength(split, 4)) {
            boolean AI;
            if (split[3].equals("Y")) {
              AI = true;
            } else if (split[3].equals("N")) {
              AI = false;
            } else {
              showMessage("Invalid AI input. AI must be false or true");
              break;
            }
            client.getPeer().joinRoom(split[1], split[2], AI);
          }
          break;
        case "create" :
          if (checkLength(split, 5)) {
            int capacity = 0;
            try {
              capacity = Integer.parseInt(split[3]);
            } catch (NumberFormatException e) {
              wrongInput();
            }

            if (capacity >= 2 && capacity <= 4) {
              boolean AI;
              if (split[4].equals("Y")) {
                AI = true;
              } else if (split[4].equals("N")) {
                AI = false;
              } else {
                showMessage("Invalid AI input. AI must be false or true");
                break;
              }
              client.getPeer().createRoom(split[1], split[2], capacity, AI);
            } else {
              showMessage("Invalid capacity. Capacity should be 2, 3 or 4");
            }
          }
          break;
        case "ready" :
          if (checkLength(split, 1)) {
            client.getPeer().readyToGo();
          }
          break;
        case "move" :
          if (split.length >= 3 && split.length <= 5) {
            int field1 = 0;
            int field2 = 0;
            int field3 = 0;
            String dir = "";

            try {
              field1 = Integer.parseInt(split[1]);
              if (!isValidField(field1)) {
                showMessage(
                    "Invalid position. Position should be a number from 1 up to 61");
                break;
              }
              if (split.length == 4) {
                field2 = Integer.parseInt(split[2]);
                if (!isValidField(field2)) {
                  showMessage(
                      "Invalid position. Position should be a number from 1 up to 61");
                  break;
                }
              }
              if (split.length == 5) {
                field3 = Integer.parseInt(split[3]);
                if (!isValidField(field3)) {
                  showMessage(
                      "Invalid position. Position should be a number from 1 up to 61");
                  break;
                }
              }
            } catch (NumberFormatException e) {
              wrongInput();
            }

            if (split.length == 3) {
              dir = split[2];
            } else if (split.length == 4) {
              dir = split[3];
            } else if (split.length == 5) {
              dir = split[4];
            }

            if (!isValidDirection(dir)) {
              showMessage(
                  "Invalid direction. Direction should be either L, R, UL, UR, DL or DR");
              break;
            }

            client.getPeer().makeMove(field1, field2, field3,
                Direction.valueOf(dir));
          } else {
            wrongInput();
          }
          break;
        case "leave" :
          if (checkLength(split, 1)) {
            client.getPeer().quitRoom();
          }
          break;
        case "say" :
          if (checkLength(split, 2)) {
            client.getPeer().sendChatMessage(split[1]);
          }
          break;
        case "players" :
          if (checkLength(split, 1)) {
            client.getPeer().listPlayers();
          }
          break;
        case "hint" :
          if (checkLength(split, 1)) {
            client.getPeer().hint();
          }
          break;
        case "board" :
          if (checkLength(split, 1)) {
            client.getPeer().printNumberBoard();
          }
          break;
        default :
          wrongInput();
          break;
      }
    }
  }

  private boolean checkLength(String[] split, int len) {
    if (split.length != len) {
      showMessage("Invalid input. Please use one of the commands below.",
          false);
      showHelpMenu();
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

  private void wrongInput() {
    showMessage("Invalid input. Please use one of the commands below.");
    showHelpMenu();
  }

  public void showMessage(String message) {
    console.println(message + "\n");
  }

  public void showMessage(String message, boolean newLine) {
    console.println(message);
  }

  public InetAddress getIp(String question) {
    input = new Scanner(System.in);
    String line = null;
    InetAddress ip = null;

    while (ip == null) {
      showMessage(question, false);
      line = input.nextLine();

      try {
        ip = InetAddress.getByName(line);
      } catch (UnknownHostException e) {
      }
    }
    System.out.println();
    return ip;
  }

  public int getPort(String question) {
    int port = 0;
    boolean found = false;

    while (!found) {
      input = new Scanner(System.in);
      showMessage(question, false);

      try {
        port = input.nextInt();
      } catch (InputMismatchException e) {
      }

      if (port > 999 && port < 10000) {
        found = true;
      }
    }
    return port;
  }

  public String getString(String question) {
    String line = null;
    while (line == null || PM.containsIllegalCharacter(line)) {
      if (question != null) {
        showMessage(question, false);
      }
      line = input.nextLine();
    }
    System.out.println();
    return line;
  }

  public int getInt(String question) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean getBoolean(String question) {
    showMessage(question + " (Y/N)", false);;
    Scanner scan = new Scanner(System.in);
    String answer = scan.nextLine();
    if (answer.equals("yes") || answer.equals("y") || answer.equals("Yes")
        || answer.equals("Y")) {
      return true;
    } else if (answer.equals("no") || answer.equals("No") || answer.equals("n")
        || answer.equals("N")) {
      return false;
    } else {
      showMessage("That input is not correct!", false);
      return getBoolean(question);
    }
  }

  @Override
  public void showHelpMenu() {
    showMessage(
        "-------------------------------------------------- Help Menu --------------------------------------------------",
        false);
    showMessage(
        "help                                             -     Show this menu",
        false);
    showMessage(
        "exit                                             -     Exit program",
        false);
    showMessage(
        "say:[message]                                    -     Say something to other players in same server/room",
        false);
    showMessage(
        "rooms                                            -     Shows a list of open rooms",
        false);
    showMessage(
        "create:[name]:[password]:[capacity]:[AI Y/N]     -     Create a new room and join this room, password is optional",
        false);
    showMessage(
        "join:[room]:[password]:[AI Y/N]                  -     Join an open room",
        false);
    showMessage(
        "ready                                            -     Indicate you are ready to start the game",
        false);
    showMessage(
        "players                                          -     Show all players currently in the room",
        false);
    showMessage(
        "board                                            -     Show the board with numbers",
        false);
    showMessage(
        "hint                                             -     Show the best possible move when playing a game",
        false);
    showMessage(
        "move:[1..61]:[1..61]:[1..61]:[L/R/UL/UR/DL/DR]   -     Do a move, field 2 and 3 are optional",
        false);
    showMessage(
        "leave                                            -     Leave the room/game");
  }
}