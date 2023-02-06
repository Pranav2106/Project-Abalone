package network.protocols;

import game.board.Direction;

public interface ClientProtocol {

  public void connectToServer(String username);
  
  public void disconnectFromServer();

  public void createRoom(String name, String password, int capacity,
      boolean AI);
  
  public void joinRoom(String name, String password, boolean AI);
  
  public void quitRoom();
  
  public void readyToGo();
  
  public void listPlayers();
  
  public void hint();

  public void listRooms();

  public void makeMove(int pos1, int pos2, int pos3, Direction dir);
  
  public void updateBoard(int pos1, int pos2, int pos3, Direction dir);

  public void sendChatMessage(String message);
}