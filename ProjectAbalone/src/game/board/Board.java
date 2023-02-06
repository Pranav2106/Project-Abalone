package game.board;

import exceptions.InvalidInputException;
import exceptions.NoWinnerException;
import game.player.Player;
import game.player.Team;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Board {

  // @invariant index >=1 && index <=61
  // @invariant (row >= 0 && row <=9) && (column >=0 && columm <= 9 -
  // Math.abs(4-row))

  private BoardMap boardMap = new BoardMap();
  private Field[] originalfields = new Field[61];
  private Team[] teams;
  private Player[] players;
  private Marble initialmarble = null;
  private int playercount = 0;
  private boolean canpushenemymarble = false;
  private boolean canknock = false;

  // --------X-----------------------X-------------------------X
  // INDEX System(Main System)
  // 1 2 3 4 5
  // 6 7 8 9 10 11
  // 12 13 14 15 16 17 18
  // 19 20 21 22 23 24 25 26
  // 27 28 29 30 31 32 33 34 35
  // 36 37 38 39 40 41 42 43
  // 44 45 46 47 48 49 50
  // 51 52 53 54 55 56
  // 57 58 59 60 61

  // ----------X--------------X------------X---------X----------
  // NormalCoordinateAxis
  // (0,0) ,(0,1) ,(0,2) ,(0,3) (0,4)
  // (1,0) (1,1) ,(1,2) ,(1,3) ,(1,4) (1,5)
  // (2,0) (2,1) (2,2) ,(2,3) ,(2,4) ,(2,5) , (2,6)
  // (3,0) (3,1) (3,2) ,(3,3) ,(3,4) ,(3,5) (3,6), (3,7)
  // (4,0) (4,1) (4,2) ,(4,3) ,(4,4) ,(4,5) (4,6), (4,7),(4,8)
  // (5,0) (5,1) (5,2) ,(5,3) ,(5,4) ,(5,5) (5,6), (5,7)
  // (6,0) (6,1) (6,2) ,(6,3) ,(6,4) ,(6,5) (6,6)
  // (7,0) (7,1) ,(7,2) ,(7,3) ,(7,4) ,(7,5)
  // (8,0) ,(8,1) ,(8,2) ,(8,3) ,(8,4)
  //
  // --------------X--------------------------X--------------------X------

  /**
   * Constructor.
   *
   * @param players
   *          requires an array of players to make a board layout and set
   *          marbles.
   */

  public Board(Player[] players) {
    this.playercount = players.length;
    this.players = players;
    for (int i = 0; i < 61; i++) {
      // Assigns each originalfield a new valid field in range of
      // [1..61] index with empty marbles
      originalfields[i] = new Field(i + 1, Marble.E);
    }
    boardlayout(players); // Takes in the player[] array and makes a
    // boardlayout for 2 or 3 players
    // respectively
  }

  /**
   * Constructor 2.
   *
   * @param team
   *          if there are 4 players , it requires array of teams which contains
   *          team 1 and team 2 and makes a boardlayout for 4 players
   */

  public Board(Team[] team) {
    playercount = 4;
    this.teams = team;

    for (int i = 0; i < 61; i++) {
      // Assigns each originalfield a new valid field in range of
      // [1..61] index with empty marbles
      originalfields[i] = new Field(i + 1, Marble.E);
    }

    Player[] player1 = team[0].getPlayerArray(); // Gets Players of team1
    Player[] player2 = team[1].getPlayerArray(); // Gets Players of team2
    // Assigns number of marbles(counter) to each marble of the player
    Marble m1 = player1[0].getMarble();
    Marble m2 = player1[1].getMarble();
    Marble m3 = player2[0].getMarble();

    m1.setCounter(9);
    m2.setCounter(9);
    m3.setCounter(9);

    // Setting the boardlayout for 4 players.
    for (int i = 1; i <= 5; i++) {
      originalfields[i - 1].setMarble(m1);
      originalfields[4].setMarble(Marble.E);
    }
    for (int i = 6; i <= 11; i++) {
      originalfields[i - 1].setMarble(m1);
      originalfields[5].setMarble(Marble.E);
      originalfields[9].setMarble(Marble.E);
      originalfields[10].setMarble(m3);
    }
    for (int i = 12; i <= 18; i++) {
      originalfields[i - 1].setMarble(player1[0].getMarble());
      originalfields[11].setMarble(Marble.E);
      originalfields[12].setMarble(Marble.E);
      originalfields[15].setMarble(Marble.E);
      originalfields[16].setMarble(m3);
      originalfields[17].setMarble(m3);
    }

    for (int i = 19; i <= 26; i++) {
      if (i > 23) {
        originalfields[i - 1].setMarble(m3);
      } else {
        originalfields[i - 1].setMarble(Marble.E);
      }
    }
    Marble m4 = player2[1].getMarble();
    m4.setCounter(9);
    for (int i = 27; i <= 35; i++) {
      if (i < 30) {
        originalfields[i - 1].setMarble(m4);
      } else if (i > 32) {
        originalfields[i - 1].setMarble(m3);
      } else {
        originalfields[i - 1].setMarble(Marble.E);
      }
    }

    for (int i = 36; i <= 43; i++) {
      if (i > 38) {
        originalfields[i - 1].setMarble(Marble.E);
      } else {
        originalfields[i - 1].setMarble(m4);
      }
    }

    for (int i = 44; i <= 50; i++) {
      originalfields[i - 1].setMarble(m2);
      originalfields[48].setMarble(Marble.E);
      originalfields[49].setMarble(Marble.E);
      originalfields[45].setMarble(Marble.E);
      originalfields[43].setMarble(m4);
      originalfields[44].setMarble(m4);
    }

    for (int i = 51; i <= 56; i++) {
      originalfields[i - 1].setMarble(player1[1].getMarble());
      originalfields[51].setMarble(Marble.E);
      originalfields[55].setMarble(Marble.E);
      originalfields[50].setMarble(m4);
    }

    for (int i = 57; i <= 61; i++) {
      originalfields[i - 1].setMarble(m2);
      originalfields[56].setMarble(Marble.E);
    }

  }

  public Player[] getPlayerArray() {
    return this.players;
  }

  /**
   * .
   * 
   * @return Team of Players
   */
  public Team[] getTeamArray() {
    return teams;
  }

  /**
   * .
   * 
   * @param players
   *          requires an array of players playing the game
   */
  public void boardlayout(Player[] players) {
    if (players.length == 2) {
      // Assigns each marble of the player , number of marbles (14)
      Marble m1 = players[0].getMarble();
      Marble m2 = players[1].getMarble();
      m1.setCounter(14);
      m2.setCounter(14);

      // Making the board layout for 2 players
      for (int i = 1; i <= 11; i++) {
        originalfields[i - 1].setMarble(m1);
      }

      for (int i = 51; i <= 61; i++) {
        originalfields[i - 1].setMarble(m2);
      }
      originalfields[13].setMarble(m1);
      originalfields[14].setMarble(m1);
      originalfields[15].setMarble(m1);
      originalfields[45].setMarble(m2);
      originalfields[46].setMarble(m2);
      originalfields[47].setMarble(m2);

    } else if (players.length == 3) {
      // Assigns each marble of the player , number of marbles (11)
      Marble m1 = players[0].getMarble();
      Marble m2 = players[1].getMarble();
      Marble m3 = players[2].getMarble();

      m1.setCounter(11);
      m2.setCounter(11);
      m3.setCounter(11);

      // Since in 3 player boardlayout each marble is completely spread along a
      // line
      // in x,y,z axis
      // we can find the lines of the marbles and iterate and place the marbles
      // in
      // that line
      // getLine() methods gets the entire line along that axis
      // For eg {1,6,12} is a line along x

      int[] l1 = boardMap.getLine(getFieldArray(new int[]{1, 6, 12}));
      // axis , getLine returns an array of
      // [1,6,12,19,27]
      int[] l2 = boardMap.getLine(getFieldArray(new int[]{2, 7, 13}));
      int[] l3 = boardMap.getLine(getFieldArray(new int[]{4, 10, 17}));

      // Iterating through each line and setting the marbles accordingly

      for (int i = 0; i < l1.length; i++) {
        originalfields[l1[i] - 1].setMarble(m1);
      }
      for (int i = 0; i < l2.length; i++) {
        originalfields[l2[i] - 1].setMarble(m1);
      }
      for (int i = 0; i < l3.length; i++) {
        originalfields[l3[i] - 1].setMarble(m2);
      }
      int[] l4 = boardMap.getLine(getFieldArray(new int[]{5, 11, 18}));
      for (int i = 0; i < l4.length; i++) {
        originalfields[l4[i] - 1].setMarble(m2);
      }
      int[] l5 = boardMap.getLine(getFieldArray(new int[]{51, 52, 53}));
      for (int i = 0; i < l5.length; i++) {
        originalfields[l5[i] - 1].setMarble(m3);
      }
      int[] l6 = boardMap.getLine(getFieldArray(new int[]{57, 58, 59}));
      for (int i = 0; i < l6.length; i++) {
        originalfields[l6[i] - 1].setMarble(m3);
      }
    }
  }

  /**
   * .
   * 
   * @param marbles
   *          marbles that need to be pushed
   * @param direction
   *          direction along which it should be pushed
   */

  // @requires marbles != null
  // @requires direction != null
  // @requires ValidMoves(marbles,direction,player) == true
  // @requires getDirection(direction) == true
  // @ensures move(marbles,direction) == true , marbles are moved in that
  // direction
  public void move(Field[] marbles, String direction) {

    // If there is one marble that needs to pushed , it goes to pushOneMarble()
    // method which handles pushing of that one marble in that direction
    if (marbles.length == 1) {
      Field field = marbles[0];
      pushOneMarble(field, direction);
    } else {
      // If there are 2 or 3 marbles that need be pushed , its find the axis
      // along
      // which the marbles lie and then deals with pushing
      // in that axis with pushX(),pushY(),pushZ()
      boolean xrow = boardMap.isXRow(marbles);
      boolean yrow = boardMap.isYRow(marbles);
      boolean zrow = boardMap.isZRow(marbles);
      if (xrow) {
        pushX(marbles, direction);
      } else if (yrow) {
        pushY(marbles, direction);
      } else if (zrow) {
        pushZ(marbles, direction);
      }
    }
  }

  /**
   * .
   *
   * @param indexes
   *          requires indexes of the marbles that need to be pushed
   * @param direction
   *          direction of the move
   * @param player
   *          requires player of whose marbles are being pushed
   * @throws InvalidInputException
   *           if the input is invalid it will throw invalid input exception
   * @returns ValidMove == true || ValidMove == false
   */

  // @requires indexes.size() !=0
  // @requires isDirection(direction) = true
  // @requires player != null
  // @ensures ValidMove(move) == true || ValidMove(move) == false

  public boolean validMove(int[] indexes, String direction, Player player) {
    // Sorts the indexes
    Arrays.sort(indexes);
    // Checks if indexes and direction are valid

    if (isValidIndexes(indexes) && isDirection(direction)) {

      // Converts indexes into Fields
      Field[] marbles = getFieldArray(indexes);
      // Checks if the marbles selected are of same field or not
      if (validMarbles(marbles, player)) {

        if (marbles.length > 1) {
          if (boardMap.isLine(marbles)) {
            // Checks if the marbles selected are in a line or not

            return canMove(marbles, direction);
            // Checks if the marbles can be moved in that direction or not
            // moved
            // in that direction or not

          }
        } else if (marbles.length == 1) {
          Field adjacent1 = getAdjacent(marbles[0], direction);

          if (adjacent1 == null) {
            // If adjacent field is null , then it is invalid to move as it
            // would be committing suicide
            return false;
          } else if (isEmpty(getAdjacent(marbles[0], direction).getIndex())) {
            // If adjacent field in that direction is empty , then its valid

            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * .
   *
   * @param direction
   *          takes in the direction to check if it is valid direction or not
   * @returns isDirection == true || isDirection == false
   */
  private boolean isDirection(String direction) {
    // L is for left , R is for Right , UL for for UpperLeft,DR for Downright,
    // DL
    // for downleft , UR for UpRight
    return direction.equals("L") || direction.equals("R")
        || direction.equals("UL") || direction.equals("DR")
        || direction.equals("DL") || direction.equals("UR");
  }

  /**
   * .
   *
   * @param marbles
   *          takes in the marbles that need to checked
   * @param player
   *          takes in the player of whose marbles selected need to be checked
   * @returns validmarbles == true || validmarbles == false
   */
  // @requires marbles != null
  // @requires player != null
  // @ensures ValidMarbles(marbles,player) == true ||
  // ValidMarbles(marbles,player)
  // == false

  private boolean validMarbles(Field[] marbles, Player player) {

    if (playercount == 4) {
      Team[] teamarray = getTeamArray();
      Set<Marble> player1 = teamarray[0].getMarbleSet();
      // Gets set of marbles of team 1

      Set<Marble> player2 = teamarray[1].getMarbleSet();
      // Gets set of marbles of team 2

      Set<Marble> totalset = new HashSet<>();
      for (int i = 0; i < marbles.length; i++) {
        totalset.add(marbles[i].getMarble());
      }
      if (!player1.containsAll(totalset) && !player2.containsAll(totalset)) {
        // All marbles should be in either of the sets else false

        return false;
      }
    } else if (playercount <= 3) {
      for (int i = 0; i < marbles.length; i++) {
        // If the marble selected by the player is not equal players marbles
        // then its
        // false
        if (marbles[i].getMarble() != player.getMarble()) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * .
   *
   * @param indexes
   *          takes in the indexes of the marbles
   * @return field of selected indexes
   */
  // @requires indexes.size() != 0; && indexes[i] >= 1 && indexes[i] <=61
  // @ensures getFieldArray() != null

  public Field[] getFieldArray(int[] indexes) {
    Arrays.sort(indexes);
    Field[] marbles = new Field[indexes.length];
    for (int i = 0; i < indexes.length; i++) {
      marbles[i] = getField(indexes[i]);
    }
    return marbles;
  }

  /**
   * .
   *
   * @param marbles
   *          marbles that need to be checked
   * @param direction
   *          direction in which needs to be checked
   * @return if marbles user selected can move that in direction or not
   */

  // @requires marbles != null
  // @requries direction != null;
  // @requires isDirection(direction) == true
  // @ensures canMove(marbles,direction) == true || canMove(marbles,direction)
  // ==
  // false
  private boolean canMove(Field[] marbles, String direction) {
    // First it finds which axis , marbles are lying along
    // Offline directions are dealt with canPushOffline()
    boolean xrow = boardMap.isXRow(marbles);
    boolean yrow = boardMap.isYRow(marbles);
    boolean zrow = boardMap.isZRow(marbles);
    if (xrow) {
      // Inline and offline moves are checked seperately, CanPushX() deals with
      // it

      // In X axis , inline directions are DR and UL
      if (direction.equals("DR") || direction.equals("UL")) {
        return canPushX(marbles, direction);
      } else {
        return canPushOffline(marbles, direction);
      }
      // In Y axis , inline directions are L and R , CanPushY() deals with it
    } else if (yrow) {
      if (direction.equals("L") || direction.equals("R")) {
        return canPushY(marbles, direction);
      } else {
        return canPushOffline(marbles, direction);
      }
    } else if (zrow) {
      // In Z axis , inline directions are UR and DL , CanPushZ() deals with it
      if (direction.equals("UR") || direction.equals("DL")) {
        return canPushZ(marbles, direction);
      } else {
        return canPushOffline(marbles, direction);
      }
    }
    return false;
  }

  // Array of marbles should be sorted , String should be UL or DR

  /**
   * .
   *
   * @param marbles
   *          marbles that need to be checked
   * @param direction
   *          direction along which it needs to be checked
   * @returns If you can push marbles lying along the x axis in that specific
   *          direction
   */

  private boolean canPushX(Field[] marbles, String direction) {
    Field startingmarble = null;
    if (marbles.length == 3) {
      // If UL direction in x axis , marble should start pushing from first
      // marble
      // with smallest index
      // If DR direction in x axis , marble should start pushing from last
      // marble with
      // largest index
      // Also adjacent marble should not be null in that direction , to prevent
      // suicide
      if (direction.equals("UL")) {
        startingmarble = marbles[0];
      } else if (direction.equals("DR")) {
        startingmarble = marbles[2];
      }
      return (getAdjacent(startingmarble, direction) != null) && canPush(
          startingmarble, startingmarble.getMarble(), direction, 3, 0);

    } else { // marble length == 2
      // If adjacent marble is null , cant commit suicide hence
      // If adjacent marble in that marble turns out to be our there can be two
      // more
      // cases:
      // - Either we can move it
      // - Either we are going to commit suicide with it

      if (direction.equals("UL")) {
        startingmarble = marbles[0];
      } else if (direction.equals("DR")) {
        startingmarble = marbles[1];

      }
      Field adjacent = getAdjacent(startingmarble, direction);
      // adjacent marble is null

      if (adjacent == null) {
        return false;
      } else {
        return canPush(startingmarble, startingmarble.getMarble(), direction, 2,
            0);

      }
    }
  }

  /**
   * .
   *
   * @param marbles
   *          marbles that need to be pushed.
   * @param direction
   *          direction in which it needs to push
   * @return If you can push marbles lying along the y axis in that specific
   *         direction
   */
  private boolean canPushY(Field[] marbles, String direction) {
    Field startingmarble = null;
    if (marbles.length == 3) {
      // If UL direction in x axis , marble should start pushing from first
      // marble
      // with smallest index
      // If DR direction in x axis , marble should start pushing from last
      // marble with
      // largest index
      // Also adjacent marble should not be null in that direction , to prevent
      // suicide
      if (direction.equals("L")) {
        startingmarble = marbles[0];
      } else if (direction.equals("R")) {
        startingmarble = marbles[2];
      }
      return (getAdjacent(startingmarble, direction) != null) && canPush(
          startingmarble, startingmarble.getMarble(), direction, 3, 0);

    } else { // marble length == 2
      // If adjacent marble is null , cant commit suicide hence
      // If adjacent marble in that marble turns out to be our there can be two
      // more
      // cases:
      // - Either we can move it
      // - Either we are going to commit suicide with it

      if (direction.equals("L")) {
        startingmarble = marbles[0];
      } else if (direction.equals("R")) {
        startingmarble = marbles[1];

      }
      Field adjacent = getAdjacent(startingmarble, direction);
      // adjacent marble is null
      if (adjacent == null) {
        return false;
      } else {
        return canPush(startingmarble, startingmarble.getMarble(), direction, 2,
            0);

      }
    }
  }

  /**
   * .
   *
   * @param marbles
   *          marbles that need to be pushed
   * @param direction
   *          in which it needs to be pushed
   * @return If you can push marbles lying along the z axis in that specific
   *         direction
   */

  private boolean canPushZ(Field[] marbles, String direction) {
    Field startingmarble = null;
    if (marbles.length == 3) {
      // If UL direction in x axis , marble should start pushing from first
      // marble
      // with smallest index
      // If DR direction in x axis , marble should start pushing from last
      // marble with
      // largest index
      // Also adjacent marble should not be null in that direction , to prevent
      // suicide
      if (direction.equals("UR")) {
        startingmarble = marbles[0];
      } else if (direction.equals("DL")) {
        startingmarble = marbles[2];
      }
      return (getAdjacent(startingmarble, direction) != null) && canPush(
          startingmarble, startingmarble.getMarble(), direction, 3, 0);

    } else { // marble length == 2
      // If adjacent marble is null , cant commit suicide hence
      // If adjacent marble in that marble turns out to be our there can be two
      // more
      // cases:
      // - Either we can move it
      // - Either we are going to commit suicide with it

      if (direction.equals("UR")) {
        startingmarble = marbles[0];
      } else if (direction.equals("DL")) {
        startingmarble = marbles[1];

      }
      // adjacent is null
      Field adjacent = getAdjacent(startingmarble, direction);

      if (adjacent == null) {
        return false;
      } else {
        return canPush(startingmarble, startingmarble.getMarble(), direction, 2,
            0);

      }
    }
  }

  /**
   * .
   *
   * @param start
   *          starting position to iterate from
   * @param originalmarble
   *          the marble of the starting position
   * @param direction
   *          direction in which it needs to go
   * @param marblelength
   *          length of balls that need to be pushed
   * @param enemycounter
   *          counts the number of enemy marbles along the path
   * @return If you can push the marble in that direction or not
   */

  // @requires originalmarble != null
  // @requires originalmarble != Marble.O (Empty marble)
  // @requires direction != null
  // @requires isDirection(direction) == true
  // @requires marblelength > 0 && marblelength <=3
  // @requires enemycounter >= 0
  // @ensures canPush(start,originalmarble,direction,marblelength,enemycounter)
  // ==
  // true || canPush(start,originalmarble,direction,marblelength,enemycounter)
  // ==
  // false
  private boolean canPush(Field start, Marble originalmarble, String direction,
      int marblelength, int enemycounter) {

    while (start != null) {
      // Till it reaches end of the line
      canpushenemymarble = false;
      canknock = false;
      Field adjacentfield = getAdjacent(start, direction);

      if (enemycounter == marblelength) {
        // If enenemy marbles equals selected marble length then false
        return false;
      } else if (adjacentfield == null) {
        // If adjacent field has an enemy marble
        if (enemycounter > 0) {
          canknock = true;
        }
        return true;
      } else if (playercount <= 3
          && adjacentfield.getMarble() == originalmarble) {
        // Checks if same player marble is blocking the push
        return false;
      } else if (playercount == 4
          && isSameTeamMarble(originalmarble, adjacentfield.getMarble())) {
        // Checks if same team marble is blocking the push
        return false;
      } else if (adjacentfield.getMarble() == Marble.E) {
        // If there is an empty field then it can push
        if (enemycounter > 0) {
          canpushenemymarble = true;
        }
        return true;
      }

      if (adjacentfield.getMarble() != originalmarble) {
        // If it encounters an enemy marble then it // increases counter
        enemycounter++;
      }
      start = adjacentfield; // Moves to next marble
    }
    return true;
  }

  /**
   * .
   *
   * @param marbles
   *          that need to pushed
   * @param direction
   *          in which it needs to be pushed
   * @return If marbles can be pushed offline
   */

  // @requires marbles != null
  // @requires direction != null
  // @requires isDirection(direction) == true
  // @ensures canPushOffline(marbles,direction) == true ||
  // canPushOffline(marbles,direction) == false
  private boolean canPushOffline(Field[] marbles, String direction) {

    canknock = false;
    canpushenemymarble = false;
    for (int i = 0; i < marbles.length; i++) {
      if (getAdjacent(marbles[i], direction) == null) {
        return false;
      } else if (getAdjacent(marbles[i], direction).getMarble() != Marble.E) {
        return false;
      }

    }
    return true;
  }

  /**
   * .
   *
   * @param marbles
   *          that need to pushed
   * @param direction
   *          in which it needs to be pushed
   */

  private void pushX(Field[] marbles, String direction) {
    // INLINE MOVE in X axis are along Upleft and DownRight direction
    switch (direction) {
      case "DR" :
        Field startingfield = marbles[0];
        Marble startingmarble = startingfield.getMarble();
        initialmarble = startingmarble;
        originalfields[startingfield.getIndex() - 1].setMarble(Marble.E);
        pushInLine(getAdjacent(startingfield, "DR"), "DR", startingmarble);
        break;
      case "UL" :
        startingfield = marbles[marbles.length - 1];
        startingmarble = startingfield.getMarble();
        initialmarble = startingmarble;
        originalfields[startingfield.getIndex() - 1].setMarble(Marble.E);
        pushInLine(getAdjacent(startingfield, "UL"), "UL", startingmarble);
        break;
      case "UR" :
        pushOffLine(marbles, direction);
        break;
      case "DL" :
        pushOffLine(marbles, direction);
        break;
      case "L" :
        pushOffLine(marbles, direction);
        break;
      case "R" :
        pushOffLine(marbles, direction);
        break;
      default :
    }

  }

  /**
   * .
   *
   * @param marbles
   *          that need to pushed lying along y axis
   * @param direction
   *          in which it needs to be pushed
   */

  private void pushY(Field[] marbles, String direction) {
    // INLINE MOVES in Y axis are Left and Right direction
    switch (direction) {
      case "DR" :
        pushOffLine(marbles, direction);
        break;
      case "UL" :
        pushOffLine(marbles, direction);
        break;
      case "UR" :
        pushOffLine(marbles, direction);
        break;
      case "DL" :
        pushOffLine(marbles, direction);
        break;
      case "L" :
        Field startingfield = marbles[marbles.length - 1];
        Marble startingmarble = startingfield.getMarble();
        initialmarble = startingmarble;
        originalfields[startingfield.getIndex() - 1].setMarble(Marble.E);
        pushInLine(getAdjacent(startingfield, direction), direction,
            startingmarble);
        break;
      case "R" :
        startingfield = marbles[0];
        startingmarble = startingfield.getMarble();
        initialmarble = startingmarble;
        originalfields[startingfield.getIndex() - 1].setMarble(Marble.E);
        pushInLine(getAdjacent(startingfield, direction), direction,
            startingmarble);
        break;
      default :

    }
  }

  /**
   * .
   *
   * @param marbles
   *          that need to pushed lying along z axis
   * @param direction
   *          in which it needs to be pushed
   */
  private void pushZ(Field[] marbles, String direction) {
    // INLINE MOVES IN Z AXIS are Upright & Downleft direction
    switch (direction) {
      case "DR" :
        pushOffLine(marbles, direction);
        break;
      case "UL" :
        pushOffLine(marbles, direction);
        break;
      case "UR" :
        Field startingfield = marbles[marbles.length - 1];
        Marble startingmarble = startingfield.getMarble();
        initialmarble = startingmarble;
        originalfields[startingfield.getIndex() - 1].setMarble(Marble.E);
        pushInLine(getAdjacent(startingfield, direction), direction,
            startingmarble);
        break;
      case "DL" :
        startingfield = marbles[0];
        startingmarble = startingfield.getMarble();
        initialmarble = startingmarble;
        originalfields[startingfield.getIndex() - 1].setMarble(Marble.E);
        pushInLine(getAdjacent(startingfield, direction), direction,
            startingmarble);
        break;
      case "L" :
        pushOffLine(marbles, direction);
        break;
      case "R" :
        pushOffLine(marbles, direction);
        break;
      default :

    }

  }

  /**
   * .
   *
   * @param marbles
   *          that need to pushed offline
   * @param direction
   *          in which it needs to be pushed
   */

  // @requires marbles != null
  // @requires direction != null
  // @requires isDirection(direction) == true;
  // @ensures marbles are pushed in offline in the selected direction

  private void pushOffLine(Field[] marbles, String direction) {
    // Pushes each marble one by one
    for (int i = 0; i < marbles.length; i++) {
      Field field = marbles[i];
      pushOneMarble(field, direction);
    }
  }

  /**
   * .
   *
   * @param field
   *          that needs to be pushed in that direction
   * @param direction
   *          in which it needs to be pushed
   */
  // @requires Field != null
  // @requires direction != null
  // @requires isDirection(direction) == true;
  // @ensures marble is pushed in the selected direction
  private void pushOneMarble(Field field, String direction) {
    // Pushes that marble according to the direction and sets original fied to
    // Empty(Marble.O)

    Marble marble = field.getMarble();
    originalfields[field.getIndex() - 1].setMarble(Marble.E);
    originalfields[getAdjacent(field, direction).getIndex() - 1]
        .setMarble(marble);
  }

  /**
   * .
   * 
   * @param field
   *          that can be pushed in line
   * @param direction
   *          in which it needs to be pushed
   * @param marble
   *          marble of that field
   */
  // @requires field != null
  // @requires direction != null
  // @requires isDirection(direction) == true;
  // @requires marble != null
  // @ensures marbles are pushed in inline in the selected direction

  public void pushInLine(Field field, String direction, Marble marble) {
    if (field == null) { // Reaches end of the line
      initialmarble.pushed(); // Has pushed atleast one marble of the grid
      return;
    } else if (field.getMarble() == Marble.E) { // Reaches an empty space
      field.setMarble(marble);
      return;
    } else { // Keeps iterating
      Marble temp = field.getMarble();
      originalfields[field.getIndex() - 1].setMarble(marble);
      pushInLine(getAdjacent(field, direction), direction, temp);
    }
  }

  /**
   * .
   * 
   * @param field
   *          to find adjacent field to
   * @param direction
   *          min which it needs to be pushed
   * @return adjacent field to that field
   */

  // @requires field != null
  // @requires direction != null
  // @requires isDirection(direction) == true;
  // @ensures getAdjacent(field,direction) == field ||
  // getAdjacent(field,direction) == null

  private Field getAdjacent(Field field, String direction) {

    int row = getRow(field.getIndex());
    int column = getColumn(field.getIndex());

    switch (direction) {
      case "UR" : // Done
        row = row - 1;
        if (row >= 4) {
          column = column + 1;
        }
        break;
      case "DR" :
        row = row + 1;
        if (row < 5) {
          column = column + 1;
        }
        break;
      case "R" :
        column = column + 1;
        break;
      case "UL" :
        row = row - 1;
        if (row < 4) {
          column = column - 1;
        }
        break;
      case "DL" : // Done
        row = row + 1;
        if (row > 4) {
          column = column - 1;
        }
        break;
      case "L" :
        column = column - 1;
        break;
      default :

    }
    if (isRowCol(row, column)) {
      try {
        return getField(row, column);
      } catch (InvalidInputException e) {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * .
   * 
   * @param indexes
   *          which need to be checked
   * @return if the indexes are valid or not
   */

  private boolean isValidIndexes(int[] indexes) {
    for (int i = 0; i < indexes.length; i++) {
      if (isIndex(indexes[i]) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * .
   * 
   * @param index
   *          index which needs to found
   * @return field associated with that index
   */

  public Field getField(int index) {
    return originalfields[index - 1];
  }

  /**
   * .
   * 
   * @param row
   *          takes in the row associated with that field
   * @param col
   *          takes in the column associated with that field
   * @return field associated with that row and col
   */

  private Field getField(int row, int col) throws InvalidInputException {
    int index = 0;
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9 - Math.abs(4 - i); j++) {
        index++;
        if (i == row && j == col) {
          return getField(index);
        }
      }
    }
    throw new InvalidInputException("Field cannot be found");
  }

  /**
   * .
   * 
   * @param index
   *          of which row needs to be found
   * @return an integer value of row
   */

  private int getRow(int index) {
    int counter = 0;
    int row = 0;
    if (isIndex(index)) {
      for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9 - Math.abs(4 - i); j++) {
          counter++;
          if (counter == index) {
            row = i;
            return row;
          }
        }
      }
    }
    return -1;
  }

  /**
   * .
   * 
   * @param index
   *          of which column needs to be found
   * @return integer value of the column
   */
  private int getColumn(int index) {
    int counter = 0;
    int column = 0;
    if (isIndex(index)) {
      for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9 - Math.abs(4 - i); j++) {
          counter++;
          if (counter == index) {
            column = j;
            return column;
          }
        }
      }
    }
    return -1;
  }

  /**
   * .
   * 
   * @param index
   *          of which field needs to be checked
   * @return if the field is empty or not
   */
  private boolean isEmpty(int index) {
    if (isIndex(index)) {
      Field field = getField(index);
      return field.getMarble() == Marble.E;
    }
    return false;
  }

  /**
   * .
   * 
   * @param row
   *          which needs to be checked
   * @param col
   *          which needs to be checked
   * @return if the rol and col are valid or not
   */

  private boolean isRowCol(int row, int col) {
    return (row >= 0 && row < 9) && (col >= 0 && col < 9 - Math.abs(4 - row));
  }

  /**
   * .
   * 
   * @param index
   *          that needs to be checked
   * @return if the index is valid or not
   */

  private boolean isIndex(int index) {
    return index >= 1 && index <= 61;
  }

  /**
   * .
   * 
   * @return representation of the board
   */
  public String toString() {
    int counter = -1;
    StringBuilder board = new StringBuilder();
    for (int i = 0; i < 9; i++) {
      String space = "";
      String numbers = "";
      if (i <= 4) {
        for (int k = 0; k < 4 - i; k++) {
          space += "  ";
        }
        for (int j = 0; j < 5 + i; j++) {
          counter++;
          numbers = numbers
              + originalfields[counter].getMarble()
                  .getAnsi(originalfields[counter].getMarble())
              + "•" + "\u001B[30m" + "   ";
        }
      } else {
        for (int k = 0; k < i - 4; k++) {
          space += "  ";
        }
        for (int j = 0; j < 13 - i; j++) {
          counter++;
          numbers = numbers
              + originalfields[counter].getMarble()
                  .getAnsi(originalfields[counter].getMarble())
              + "•" + "\033[0m" + "   ";
        }
      }
      board.append(space + numbers + "\n");
    }
    return board.toString();
  }

  /**
   * .
   * 
   * @return String that prints indexes in board form
   */
  public String toStringNumbers() {
    int counter = 0;
    StringBuilder board = new StringBuilder();
    for (int i = 0; i < 9; i++) {
      String space = "";
      String numbers = "";
      if (i <= 4) {
        for (int k = 0; k < 4 - i; k++) {
          space += "  ";
        }
        for (int j = 0; j < 5 + i; j++) {
          counter++;
          numbers = numbers + counter + "   ";
        }
      } else {
        for (int k = 0; k < i - 4; k++) {
          space += "  ";
        }
        for (int j = 0; j < 13 - i; j++) {
          counter++;
          numbers = numbers + counter + "   ";
        }
      }
      board.append(space + numbers + "\n");
    }
    return board.toString();
  }

  /**
   * .
   * 
   * @param players
   *          that are playing
   * @return if there is a winner in the game or not
   */
  public boolean hasWinner(Player[] players) {
    // Either player manages to push 6 enemy marbles of the grid
    if (players.length == 2) {
      Marble m1 = players[0].getMarble();
      Marble m2 = players[1].getMarble();
      if (m1.getPushed() == 6 || m2.getPushed() == 6) {
        return true;
      }
    } else {
      Marble m1 = players[0].getMarble();
      Marble m2 = players[1].getMarble();
      Marble m3 = players[2].getMarble();
      return m1.getPushed() == 6 || m2.getPushed() == 6 || m3.getPushed() == 6;
    }
    return false;
  }

  /**
   * .
   * 
   * @param team
   *          that are playing
   * @return if there is a winner in the game or not
   */
  public boolean hasWinner(Team[] team) {
    // If a players in a team manages to push 6 balls together, then that team
    // wins
    Player[] players1 = team[0].getPlayerArray();
    Player[] players2 = team[1].getPlayerArray();
    if (players1[0].getMarble().getPushed()
        + players1[1].getMarble().getPushed() == 6) {
      return true;
    } else if (players2[0].getMarble().getPushed()
        + players2[1].getMarble().getPushed() == 6) {
      return true;
    }
    return false;
  }

  /**
   * .
   * 
   * @param players
   *          that are playing the game
   * @return the name of the player that won the game
   */
  public Player getWinner(Player[] players) throws NoWinnerException {
    if (players.length >= 2) {
      if (players[0].getMarble().getPushed() == 6) {
        return players[0];
      } else if (players[1].getMarble().getPushed() == 6) {
        return players[1];
      } else if (players[2].getMarble().getPushed() == 6) {
        return players[2];
      }
    }
    throw new NoWinnerException("No Winner yet");
  }

  /**
   * .
   * 
   * @param team
   *          which are playing
   * @return Name of team which won
   */
  public Team getWinner(Team[] team) throws NoWinnerException {

    Player[] players1 = team[0].getPlayerArray();
    Player[] players2 = team[1].getPlayerArray();
    if (players1[0].getMarble().getPushed()
        + players1[1].getMarble().getPushed() == 6) {
      return team[0];
    } else if (players2[0].getMarble().getPushed()
        + players2[1].getMarble().getPushed() == 6) {
      return team[1];
    }
    throw new NoWinnerException("No Winner yet");
  }

  /**
   * .
   * 
   * @param originalmarble
   *          takes a teams marble
   * @param enemy
   *          takes potential enemy marble
   * @return if the marbles in the same team or not
   */

  public boolean isSameTeamMarble(Marble originalmarble, Marble enemy) {
    Team[] teamarray = getTeamArray();
    // Gets set of marbles of team 1
    Set<Marble> player1 = teamarray[0].getMarbleSet();
    // Gets set of marbles of team2
    Set<Marble> player2 = teamarray[1].getMarbleSet();
    // If both of are the same team
    if (player1.contains(originalmarble) && player1.contains(enemy)) {
      return true;
    } else if (player2.contains(originalmarble) && player2.contains(enemy)) {

      return true;
    }
    return false;
  }

  /**
   * . For AI
   *
   * @return if it can push an enemymarble or not
   */

  public boolean canpushEnemy() {
    return canpushenemymarble;
  }

  /**
   * . For AI
   *
   * @return if it can knock an enemy marble or not
   */
  public boolean can_knock() {
    return canknock;
  }

  /**
   * . For Testing
   *
   * @param index
   *          takes an index which needs to be set
   * @param marble
   *          marble which needs to be set
   */
  public void setMarble(int index, Marble marble) {
    originalfields[index - 1].setMarble(marble);
  }

  /**
   * . For Testing Resets the complete board to empty so we can place the
   * marbles anywhere
   */
  public void reset() {

    for (int i = 1; i <= 61; i++) {
      originalfields[i - 1].setMarble(Marble.E);
    }

    // Marble.W.setPushed(0);
    // Marble.B.setPushed(0);
    // Marble.R.setPushed(0);
    // Marble.b.setPushed(0);
  }

  /*
   * public static void main(String[] args) { Player p1 = new Player("Pranav",
   * "W"); Player p2 = new Player("Max", "B"); Board moves = new Board(new
   * Player[]{p1, p2}); moves.setMarble(22,Marble.W);
   * moves.setMarble(31,Marble.W); moves.setMarble(40,Marble.W);
   * moves.setMarble(61,Marble.O); moves.ValidMove(new int[]
   * {22,31,40},"DR",p1); System.out.println(moves.canpushEnemy());
   * System.out.println(moves.String()); }
   */
}
