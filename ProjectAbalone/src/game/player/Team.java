package game.player;

import game.board.Marble;

import java.util.HashSet;
import java.util.Set;

public class Team {
  private Player player1;
  private Player player2;

  public Team(Player player1, Player player2) {
    this.player1 = player1;
    this.player2 = player2;
  }

  public Player[] getPlayerArray() {
    return new Player[]{player1, player2};
  }

  /**
   * .
   * 
   * @return Set of marbles of the team
   */
  public Set<Marble> getMarbleSet() {
    Set<Marble> set = new HashSet<>();
    set.add(player1.getMarble());
    set.add(player2.getMarble());
    return set;
  }
}