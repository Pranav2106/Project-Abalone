package game.board;

public class Field {
    private Marble marble;
    private int index;

    public Field(int index, Marble marble) {
        this.index = index;
        this.marble = marble;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setMarble(Marble marble) {
        this.marble = marble;
    }

    public int getIndex() {
        return index;
    }

    public Marble getMarble() {
        return marble;
    }
}