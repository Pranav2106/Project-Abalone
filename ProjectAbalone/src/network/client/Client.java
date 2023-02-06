package network.client;

import game.views.ClientTUI;
import game.views.ClientView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import exceptions.ExitProgramException;
import exceptions.ServerUnavailableException;

public class Client implements Runnable {
  private ClientView view;
  private ClientPeer peer;
  private String username;
  private Socket ssock;

  public Client() {
    view = new ClientTUI(this);
  }

  // ------------------ Main and Run --------------------------

  /** Start a new client */
  public static void main(String[] args) {
    Client client = new Client();
    new Thread(client).start();
  }

  @Override
  public void run() {
    try {
      initialise();
      view.start();
    } catch (ExitProgramException e) {

    } catch (ServerUnavailableException e) {

    }
  }

  // ------------------ Getters and setters -----------

  protected String getUsername() {
    return this.username;
  }

  protected ClientView getView() {
    return this.view;
  }

  public ClientPeer getPeer() {
    return this.peer;
  }

  // ------------------ Other methods -----------------

  private void initialise() throws ExitProgramException {
    view.showMessage("Welcome to the Abalone Game client!");
    this.username = view.getString(
        "Enter a username that you want to use (don't use : or ,): ");

    InetAddress addr = null;
    int port = 0;
    boolean connected = false;

    while (!connected) {
      while (ssock == null) {
        try {
          // addr = InetAddress.getByName("localhost");
          addr = view.getIp(
              "Enter IP address of the server that you want to connect to: ");
          // port = 8888;
          port = view.getPort("Enter port number (1000-9999): ");

          ssock = new Socket(addr, port);
        } catch (IOException e) {
          view.showMessage("Could not create a socket on " + addr.toString()
              + " and port " + port);

          boolean answer = view.getBoolean("Do you want to try it again?");
          if (!answer) {
            throw new ExitProgramException("User indicated to exit");
          }
          view.showMessage("Please enter the information again.");
        }
      }

      view.showMessage(
          "\nAttempting to connect to " + addr.toString() + ":" + port + "...",
          false);

      peer = new ClientPeer(ssock, this);

      try {
        peer.initialise();
      } catch (ServerUnavailableException e) {
        view.showMessage(e.getMessage());
        boolean answer = view.getBoolean("Do you want to try it again?");
        if (!answer) {
          throw new ExitProgramException("User indicated to exit");
        }
        view.showMessage(" Please enter the information again.");
      }

      connected = true;
      peer.connectToServer(username);
      new Thread(peer).start();

      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      // Connecting failed. Try again
      while (!peer.getConnectedToServer()) {
        this.username = view.getString(
            "Enter a username that you want to use (don't use : or ,): ");
        peer.connectToServer(username);
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        while (peer.getConnectSend()) {
        }
      }
    }
  }
}
