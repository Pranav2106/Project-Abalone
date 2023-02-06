package game.board;

public enum Marble {

    /*
     * Y = Yellow W = White B = Blue R = Red E = Empty
     */

    Y, W, B, R, E;

    private int counter = 0;
    private int pushed = 0;

    // Intellij
    private String white = "\033[0;37m";
    private String red = "\033[0;31m";
    private String yellow = "\033[0;33m";
    private String blue = "\033[0;94m";
    private String empty = "\033[1;90m";

    /**getAnsi.
     * Eclipse private String white = "\033[0;37m"; private String red =
     * "\033[0;31m"; private String yellow = "\033[0;33m"; private String blue =
     * "\033[0;94m"; private String empty = "\033[1;90m";
     * @param marble marble
     * @return
     */
    public String getAnsi(Marble marble) {
        switch (marble) {
            case Y:
                return yellow;
            case B:
                return blue;
            case W:
                return white;
            case R:
                return red;
            case E:
                return empty;
            default:
                break;
        }
        return null;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void pushed() {
        pushed++;
    }

    public int getPushed() {
        return pushed;
    }

    /** .
     * getString
     * @param Marble marble
     * @return String that represents the color
     */
    public static String getString(Marble m) {
        switch (m) {
            case Y:
                return "Yellow";
            case B:
                return "Blue";
            case W:
                return "White";
            case R:
                return "Red";
            case E:
                return "Empty";
            default:
                break;
        }
        return null;
    }

}
