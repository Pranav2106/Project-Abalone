package game.views;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import exceptions.ExitProgramException;
import network.protocols.PM;
import network.server.MessageType;
import network.server.Server;

public class ServerTUI implements ServerView, Runnable {

  // The PrintWriter to write messages to
  private PrintWriter console;
  private Scanner input;
  private Server server;

  // Create a ServerTUI. Initialize the console.
  public ServerTUI(Server server) {
    console = new PrintWriter(System.out, true);
    input = new Scanner(System.in);
    this.server = server;
  }

  @Override
  public void run() {
    try {
      input.nextLine();
      while (true) {
        String response = input.nextLine();
        handleInput(response);
      }
    } catch (ExitProgramException e) {
      try {
        showMessage(e.getMessage());
        showMessage("Shutting down server...");
        Thread.sleep(3000);
        System.exit(0);
      } catch (InterruptedException ex) {
        System.exit(0);
      }
    }
  }

  private void handleInput(String input) throws ExitProgramException {
    if (input != null) {
      String[] split = input.split(PM.LDL);
      switch (PM.getKeyWord(input)) {
        case "exit" :
          throw new ExitProgramException("User indicated to exit");
        case "help" :
          showCommands();
          break;
        case "start" :
          if (checkLength(split, 2)) {
            server.startGame(split[1]);
          }
          break;
        case "rooms" :
          server.printRoomList();
          break;
        case "clients" :
          server.printClientList();
          break;
        case "say" :
          if (checkLength(split, 2)) {
            server.sendBroadcastMessage(split[1]);
          }
          break;
        default :
          showMessage("Invalid input. Please use on of the commands below.");
          showCommands();
          break;
      }
    }
  }

  public void showMessage(String message) {
    console.println(message);
  }

  public void showMessage(String message, MessageType type) {
    boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean()
        .getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

    if (type.equals(MessageType.DEBUG) && !isDebug) {
      return;
    }

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    console.println(dtf.format(now) + " [" + type.toString() + "] " + message);
  }

  private boolean checkLength(String[] split, int len) {
    if (split.length != len) {
      showMessage("Invalid input. Please use one of the commands below.",
          MessageType.WARN);
      showCommands();
      return false;
    }
    return true;
  }

  public String getString(String question) {
    input = new Scanner(System.in);
    String line = null;
    while (line == null || PM.containsIllegalCharacter(line)) {
      if (question != null) {
        System.out.println(question);
      }
      line = input.nextLine();
    }
    System.out.println();
    return line;
  }

  public String getString() {
    input = new Scanner(System.in);
    String line = null;
    while (line == null || PM.containsIllegalCharacter(line)) {
      line = input.nextLine();
    }
    return line;
  }

  public boolean getBoolean(String question) {
    showMessage(question + " (Y/N)");;
    Scanner scan = new Scanner(System.in);
    String answer = scan.nextLine();
    if (answer.equals("yes") || answer.equals("y") || answer.equals("Yes")
        || answer.equals("Y")) {
      return true;
    } else if (answer.equals("no") || answer.equals("No") || answer.equals("n")
        || answer.equals("N")) {
      return false;
    } else {
      showMessage("That input is not correct!");
      return getBoolean(question);
    }
  }

  public InetAddress getIp(String question) {
    input = new Scanner(System.in);
    String line = null;
    InetAddress ip = null;

    while (ip == null) {
      System.out.println(question);
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
    input = new Scanner(System.in);
    int line = 0;
    int port = 0;

    while (port == 0) {
      System.out.println(question);
      line = input.nextInt();

      if (line > 999 && line < 10000) {
        port = line;
      }
    }
    System.out.println();
    return port;
  }

  public void showCommands() {
    showMessage("------------------------- Commands -------------------------");
    showMessage("help             -   Show this list of commands");
    showMessage("exit             -   Close the server");
    showMessage("start:[room]     -   Start game inside room");
    showMessage("rooms            -   List all existing rooms");
    showMessage("clients          -   List all connected clients");
    showMessage("say:[message]    -   Say something to all connected clients");
    // showMessage("delete:[room] - Delete room");
    // showMessage("kick:[username] - Kick player");
    showMessage("");
  }
}
