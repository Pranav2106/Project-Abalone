package network.protocols;

import game.game.Room;

import java.util.ArrayList;

import network.server.ClientHandler;

public interface ServerProtocol {

  public void addClient(ClientHandler handler, boolean showMessage);

  public void removeClient(ClientHandler handler, boolean showMessage);

  public Room createRoom(String name, String password, int capacity,
      ClientHandler handler);
  
  public void deleteRoom(Room room);

  public Room addClientToRoom(String roomName, ClientHandler handler);

  public String listRooms();

  public void broadCastMessage(String message, ClientHandler sender);
}