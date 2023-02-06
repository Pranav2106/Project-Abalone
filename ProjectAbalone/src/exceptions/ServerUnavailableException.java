package exceptions;

public class ServerUnavailableException extends Exception{

	private static final long serialVersionUID = -1207009873596120108L;

	public ServerUnavailableException(String msg) {
		super(msg);
	}
}
