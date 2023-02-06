package game.player;

import game.board.Marble;

public abstract class Player {
  private Marble marble;
  private String name;

  public Player(String name, String marble) {
    assignMarble(marble);
    setName(name);
  }

  /**
   * .
   * 
   * @param marble
   *          assigns marble to the player
   */
  public void assignMarble(String marble) {
    switch (marble) {
      case "W" :
        this.marble = Marble.W;
        break;
      case "R" :
        this.marble = Marble.R;
        break;
      case "B" :
        this.marble = Marble.B;
        break;
      case "Y" :
        this.marble = Marble.Y;
        break;
      default :
        break;
    }
  }

  public Marble getMarble() {
    return marble;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}