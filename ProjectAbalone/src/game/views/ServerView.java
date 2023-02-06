package game.views;

import java.net.InetAddress;

import network.server.MessageType;

public interface ServerView {
  /**
   * Writes the given message to standard output.
   * 
   * @param message
   *          the message to write to the standard output.
   */
  public void showMessage(String message);

  /**
   * Writes the given message to standard output.
   * 
   * @param message
   *          the message to write to the standard output.
   * @param type
   *          the type of the message.
   */
  public void showMessage(String message, MessageType type);

  /**
   * Prints the question and asks the user to input a String.
   * 
   * @param question
   *          The question to show to the user
   * @return The user input as a String
   */
  public String getString(String question);

  /**
   * Prints the question and asks the user for a yes/no answer.
   * 
   * @param question
   *          The question to show to the user
   * @return The user input as boolean.
   */
  public boolean getBoolean(String question);

  /**
   * Ask the user to input a valid IP. If it is not valid, show a message and ask again.
   * 
   * @param question
   *          The question to show to the user
   * @return a valid IP
   */
  public InetAddress getIp(String question);
}
