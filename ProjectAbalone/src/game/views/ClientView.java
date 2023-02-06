package game.views;

import java.net.InetAddress;

import exceptions.ExitProgramException;
import exceptions.ServerUnavailableException;

public interface ClientView {

  /**
   * .
   * Asks for user input continuously and handles communication accordingly 
   * using the {@link #handleUserInput(String input)} method. 
   * If an ExitProgram exception is thrown, stop asking for input
   * , send an exit message to the server according to the protocol 
   * and close the connection.
   * 
   * @throws ServerUnavailableException
   *           in case of IO exceptions.
   */
  public void start() throws ServerUnavailableException;

  /**
   * Split the user input on a space and handle it accordingly.
   *  - If the input is valid, take the corresponding action 
   * (for example, when "i Name" is called, send a checkIn request for Name) 
   * - If the input is invalid, show a message to the user and print the help menu.
   * 
   * @param input
   *          The user input.
   * @throws ExitProgramException
   *           When the user has indicated to exit the program.
   * @throws ServerUnavailableException
   *           if an IO error occurs in taking the corresponding actions.
   */
  public void handleInput(String input)
      throws ExitProgramException, ServerUnavailableException;

  /**
   * Writes the given message to standard output.
   * 
   * @param msg
   *          the message to write to the standard output.
   */
  public void showMessage(String message);

  /**
   * Writes the given message to standard output.
   * 
   * @param msg
   *          the message to write to the standard output.
   * @param newLine
   *          indicates if a newline must be added after the message
   */
  public void showMessage(String message, boolean newLine);

  /**
   * Ask the user to input a valid IP. If it is not valid, show a message and ask again.
   * 
   * @param question
   *          The question to show to the user
   * @return a valid IP
   */
  public InetAddress getIp(String question);

  /**
   * Ask the user to input a valid port. If it is not valid, show a message and ask again.
   * 
   * @param question
   *          The question to show to the user
   * @return a valid port
   */
  public int getPort(String question);

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
   * Prints the help menu with available input options.
   */
  public void showHelpMenu();
}
