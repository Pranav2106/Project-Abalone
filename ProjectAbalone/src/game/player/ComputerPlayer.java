package game.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import game.board.Board;

public class ComputerPlayer extends Player {

  private Board moves;
  private int[] indexes;
  private String direction;
  public static final String[] DIRECTION = new String[]{"UR", "UL", "DR", "DL",
      "L", "R"};

  public ComputerPlayer(String name, String marble, Board moves) {
    super(name, marble);
    this.moves = moves;
  }

  public void setBoard(Board board) {
    this.moves = board;
  }

  public void makeMove() {
    Map<int[], String> values = new HashMap<>();
    this.indexes = null;
    this.direction = null;

    while (values.size() == 0) { // values can be null
      if (canknock() != null) {
        values = canknock();
        break;
      } else if (canPushEnemyMarble() != null) {
        values = canPushEnemyMarble();
        break;
      } else if (can3Push() != null) {
        values = can3Push();
        break;
      }
      int random = (int) (Math.random() * (2));
      switch (random) {
        // case 0:
        // if(can3Push() != null) {
        // values = can3Push();
        // }
        // break;
        case 0 :
          if (canTwoPush() != null) {
            values = canTwoPush();
          }
          break;
        case 1 :
          if (canOnePush() != null) {
            values = canOnePush();
          }
          break;
      }
    }

    int random = (int) (Math.random() * (values.size()));
    this.indexes = (int[]) values.keySet().toArray()[random];
    this.direction = values.get(indexes);

    if (!moves.validMove(indexes, direction, this)) {
      makeMove();
    }

    // while (valid == null) {
    // if (canknock() != null) {
    //
    // break;
    // } else if (canPushEnemyMarble()) {
    // break;
    //
    // }
    // int random = (int) (Math.random() * (3));
    // switch (random){
    // case 0:
    // System.out.println("two push");
    // canTwoPush();
    // break;
    // case 1:
    // System.out.println("one push");
    // canOnePush();
    // break;
    // case 2:
    // System.out.println("Three push");
    // can3Push();
    // break;
    // }
    // valid = false;
    // }
  }

  public Map<int[], String> canknock() {
    Map<int[], String> vertices = new HashMap<>();
    HashSet<String> hash = new HashSet<>();

    for (int i = 1; i <= 61; i++) {
      for (int j = 1; j <= 61; j++) {
        for (int k = 1; k <= 61; k++) {
          int[] array = new int[]{i, j, k};
          Arrays.sort(array);
          String s = array[0] + "" + array[1] + "" + array[2];
          if (canKnockDirection(array))
            if (hash.contains(s)) {
              continue;
            } else {
              hash.add(s);
              vertices.put(array, Direction(array));
            }
        }
      }
    }

    for (int i = 1; i <= 61; i++) {
      for (int j = 1; j <= 61; j++) {
        int[] array = new int[]{i, j};
        Arrays.sort(array);
        String s = array[0] + "" + array[1];
        if (canKnockDirection(array))
          if (hash.contains(s)) {
            continue;
          } else {
            hash.add(s);
            vertices.put(array, Direction(array));
          }
      }
    }

    if (vertices.size() != 0) {
      return vertices;
    }

    return null;
  }

  public Map<int[], String> canPushEnemyMarble() {
    Map<int[], String> vertices = new HashMap<>();
    HashSet<String> hash = new HashSet<>();
    for (int i = 1; i <= 61; i++) {
      for (int j = 1; j <= 61; j++) {
        for (int k = 1; k <= 61; k++) {
          int[] array = new int[]{i, j, k};
          Arrays.sort(array);
          String s = array[0] + "" + array[1] + "" + array[2];
          if (canPushMarbleDirection(array))
            if (hash.contains(s)) {
              continue;
            } else {
              hash.add(s);
              vertices.put(array, Direction(array));
            }
        }
      }
    }

    for (int i = 1; i <= 61; i++) {
      for (int j = 1; j <= 61; j++) {
        int[] array = new int[]{i, j};
        Arrays.sort(array);
        String s = array[0] + "" + array[1];
        if (canPushMarbleDirection(array))
          if (hash.contains(s)) {
            continue;
          } else {
            hash.add(s);
            vertices.put(array, Direction(array));
          }
      }
    }

    if (vertices.size() != 0) {
      return vertices;
    }
    return null;

  }

  public Map<int[], String> can3Push() {
    Map<int[], String> vertices = new HashMap<>();
    HashSet<String> hash = new HashSet<>();

    for (int i = 1; i <= 61; i++) {
      for (int j = 1; j <= 61; j++) {
        for (int k = 1; k <= 61; k++) {
          int[] array = new int[]{i, j, k};
          Arrays.sort(array);
          String s = array[0] + "" + array[1] + "" + array[2];
          String direction = Direction(array);
          if (direction != null) {
            s += direction;
            if (hash.contains(s)) {
              continue;
            } else {
              hash.add(s);
              vertices.put(array, direction);
            }
          }
        }
      }
    }

    if (vertices.size() != 0) {
      return vertices;
    }
    return null;
  }

  public Map<int[], String> canTwoPush() {
    Map<int[], String> vertices = new HashMap<>();
    HashSet<String> hash = new HashSet<>();

    for (int i = 1; i <= 61; i++) {
      for (int j = 1; j <= 61; j++) {
        int[] array = new int[]{i, j};
        Arrays.sort(array);
        String s = array[0] + "" + array[1];
        String direction = Direction(array);
        if (direction != null) {
          s += direction;
          if (hash.contains(s)) {
            continue;
          } else {
            hash.add(s);
            vertices.put(array, direction);
          }
        }
      }
    }

    if (vertices.size() != 0) {
      return vertices;
    }

    return null;
  }

  public Map<int[], String> canOnePush() {
    Map<int[], String> vertices = new HashMap<>();
    HashSet<String> hash = new HashSet<>();

    for (int i = 1; i <= 61; i++) {
      String s = i + "";
      String direction = Direction(new int[]{i});
      if (direction != null) {
        s += direction;
        if (hash.contains(s)) {
          continue;
        } else {
          hash.add(s);
          vertices.put(new int[]{i}, direction);
        }
      }
    }

    if (vertices.size() != 0) {
      return vertices;
    }

    return null;
  }

  public boolean canKnockDirection(int[] indexes) {
    for (int i = 0; i < 6; i++) {
      if (moves.validMove(indexes, DIRECTION[i], this) && moves.can_knock()) {
        return true;
      }
    }
    return false;
  }

  public boolean canPushMarbleDirection(int[] indexes) {

    for (int i = 0; i < 6; i++) {
      if (moves.validMove(indexes, DIRECTION[i], this)
          && moves.canpushEnemy()) {
        return true;
      }
    }
    return false;
  }

  public String Direction(int[] indexes) {
    List<String> directions = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      if (moves.validMove(indexes, DIRECTION[i], this)) {
        directions.add(DIRECTION[i]);
      }
    }

    if (directions.size() == 0) {
      return null;
    } else {
      int random = (int) (Math.random() * directions.size());
      return directions.get(random);
    }

  }

  public int[] getIndexes() {
    return indexes;
  }

  public String getDirection() {
    return direction;
  }

  /*
   * public static void main(String[] args) { Player p1 = new HumanPlayer("Pranav","W"); Player p2 = new ComputerPlayer("Computer","B"); Board moves =
   * new Board(new Player[] {p1,p2}); ComputerPlayer testai2 = new ComputerPlayer(moves, p1); ComputerPlayer testai = new ComputerPlayer(moves,p2);
   * int counter = 0; while( !moves.hasWinner(new Player[] {p1,p2})) { testai2.makeMove(); testai.makeMove(); System.out.println(moves.toString());
   * System.out.println(Marble.W.getPushed()); System.out.println(Marble.B.getPushed()); counter++; } System.out.println("Game played " + counter +
   * " times "); System.out.println(moves.isWinner(new Player[] {p1,p2})); }
   */
}
