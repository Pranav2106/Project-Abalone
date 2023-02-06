package game.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BoardMap {

  // @invariant index >=1 && index <=61
  // @invariant row >=0 && row<=0 && column >=0 && column <= 8

  public static final int N = 4;
  public static final int TOTALROWS = 9;
  public static final int[] INDEXVALUES = new int[]{1, 5, 10, 16, 23, 31, 38,
      44, 49};
  public static final int[] ROWVALUES = new int[]{5, 11, 18, 26, 35, 43, 50, 56,
      61};
  private Map<Integer, Map> map;

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
  // AxialCoordinateAxis
  // (4,0) ,(5,0) ,(6,0) ,(7,0) (8,0)
  // (3,1) (4,1) ,(5,1) ,(6,1) ,(7,1) (8,1)
  // (2,2) (3,2) (4,2) ,(5,2) ,(6,2) ,(7,2) , (8,2)
  // (1,3) (2,3), (3,3) ,(4,3) ,(5,3) ,(6,3) (7,3), (8,3)
  // (0,4) (1,4) (2,4) ,(3,4) ,(4,4) ,(5,4) (6,4), (7,4),(8,4)
  // (0,5) (1,5) (2,5) ,(3,5) ,(4,5) ,(5,5) (6,5), (7,5)
  // (0,6) (1,6) (2,6) ,(3,6) ,(4,6) ,(5,6) (6,6)
  // (0,7) (1,7) ,(2,7) ,(3,7) ,(4,7) ,(5,7)
  // (0,8) ,(1,8) ,(2,8) ,(3,8) ,(4,8)
  //
  // --------------X--------------------------X--------------------X------

  // X Z
  // X Z
  // Y Y 0 Y Y
  // Z X
  // Z X
  // Z coordinate is sum of X,Y of that coordinate

  /**
   * . Constructor Initializes map with numbers and rowmaps 
   * Initializes each rowmap with axial values of that row
   */
  BoardMap() {
    map = new HashMap<>();
    for (int row = 0; row < TOTALROWS; row++) {
      Map<Integer, Integer> rowmap = new HashMap<>();
      int rowlength = 9 - Math.abs(N - row);
      for (int col = 0; col < rowlength; col++) {
        int q;
        if (row <= N) {
          q = Math.abs(N - row) + col;
        } else {
          q = col;
        }
        rowmap.put(q, row);
      }
      map.put(row, rowmap);
    }
  }

  /**
   * . Returns map
   * 
   * @return map which contains row number and rowmap of axial values
   */
  public Map<Integer, Map> getMap() {
    return map;
  }

  /**
   * .
   * 
   * @param row
   *          of which axial coordinates needs to be found out
   * @return a rowmap containing axial values of that row
   */

  public Map<Integer, Integer> getRowMap(int row) {
    return map.get(row);
  }

  /**
   * . Prints the entire axial coordinate system
   * 
   */

  public void string() {
    for (int i = 0; i < TOTALROWS; i++) {
      Map<Integer, Integer> map = getRowMap(i);
      for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
        System.out.print(entry.getKey() + " -> " + entry.getValue() + " , ");

      }
      System.out.println();
    }
  }

  /**
   * .
   * 
   * @param row
   *          normal row of that coordinate
   * @param col
   *          normal column of that coordinate
   * @return the axial X of that coordinate
   */
  public int getAxialXCoordinate(int row, int col) {
    if (row <= 4) {
      return Math.abs(N - row) + col;
    } else {
      return col;
    }
  }

  /**
   * .
   * 
   * @param index
   *          of which axial X coordinate needs to be found
   * @return AxialX of that coordinate
   */
  public int getAxialXCoordinate(int index) {
    int normalx = -1;
    int normaly = -1;
    for (int i = 0; i < ROWVALUES.length - 1; i++) {
      if (index < 5) {
        normalx = 0;
        normaly = index - INDEXVALUES[0];
        break;
      } else if (index == ROWVALUES[i]) {
        normalx = i;
        normaly = index - INDEXVALUES[i] - normalx;
        break;
      } else if (index > ROWVALUES[i] && index < ROWVALUES[i + 1]) {
        normalx = i + 1;
        normaly = index - INDEXVALUES[i + 1] - normalx;
        break;
      }
    }
    return getAxialXCoordinate(normalx, normaly);
  }

  /**
   * .
   * 
   * @param row
   *          normal row of that coordinate
   * @param col
   *          normal column of that coordinate
   * @return the axial Y of that coordinate
   */

  public int getAxialYCoordinate(int row, int col) {
    return row;
  }

  /**
   * .
   * 
   * @param index
   *          of the coordinate whose Axial Y needs to be found
   * @return Axial Y of that coordinate
   */
  public int getAxialYCoordinate(int index) {
    int normalx = -1;
    int normaly = -1;
    for (int i = 0; i < ROWVALUES.length - 1; i++) {
      if (index < 5) {
        normalx = 0;
        normaly = index - INDEXVALUES[0];
        break;
      } else if (index == ROWVALUES[i]) {
        normalx = i;
        normaly = index - INDEXVALUES[i] - normalx;
        break;
      } else if (index > ROWVALUES[i] && index < ROWVALUES[i + 1]) {
        normalx = i + 1;
        normaly = index - INDEXVALUES[i + 1] - normalx;
        break;
      }
    }
    return getAxialYCoordinate(normalx, normaly);
  }

  /**
   * .
   * 
   * @param arow
   *          axialX of that coordinate
   * @param acol
   *          axialY of that coordinate
   * @return Normal X of that coordinate
   */

  public int getNormalXCoordinate(int arow, int acol) {

    return acol;
  }

  /**
   * .
   * 
   * @param arow
   *          axialX of that coordinate
   * @param acol
   *          axialY of that coordinate
   * @return axialY of that coordinate
   */
  public int getNormalYCoordinate(int arow, int acol) {
    if (acol <= N) {
      int ncol = arow - Math.abs(N - acol);
      return ncol;
    } else {
      return arow;
    }

  }

  /**
   * .
   * 
   * @param index
   *          of the coordinate whose Axial Z needs to be found
   * @return Axial Z of that coordinate
   */
  public int getAxialZCoordinate(int index) {
    int normalx = -1;
    int normaly = -1;
    for (int i = 0; i < ROWVALUES.length - 1; i++) {
      if (index < 5) {
        normalx = 0;
        normaly = index - INDEXVALUES[0];
        break;
      } else if (index == ROWVALUES[i]) {
        normalx = i;
        normaly = index - INDEXVALUES[i] - normalx;
        break;
      } else if (index > ROWVALUES[i] && index < ROWVALUES[i + 1]) {
        normalx = i + 1;
        normaly = index - INDEXVALUES[i + 1] - normalx;
        break;
      }
    }
    return getAxialXCoordinate(index) + getAxialYCoordinate(index);
  }

  /**
   * .
   * 
   * @param arow
   *          axialX of that coordinate
   * @param acol
   *          axialY of that coordinate
   * @return index of that coordinate
   */

  public int getIndex(int arow, int acol) {
    int normalX = getNormalXCoordinate(arow, acol);
    int normalY = getNormalYCoordinate(arow, acol);
    int indexvalue = INDEXVALUES[normalX];
    return normalX + normalY + indexvalue;
  }

  /**
   * .
   * 
   * @param marbles
   *          that need to checked
   * @return if marbles lie along x axis
   */
  public boolean isXRow(Field[] marbles) {
    int totalmarbles = marbles.length;
    int[] x = new int[totalmarbles];
    for (int i = 0; i < totalmarbles; i++) {
      int axialX = getAxialXCoordinate(marbles[i].getIndex());
      x[i] = axialX;
    }
    return Arrays.stream(x).allMatch(s -> s == x[0]);
  }

  /**
   * .
   * 
   * @param marbles
   *          that need to be checked
   * @return if marbles lie along y axis
   */
  public boolean isYRow(Field[] marbles) {
    int totalmarbles = marbles.length;
    int[] y = new int[totalmarbles];
    for (int i = 0; i < totalmarbles; i++) {
      int axialY = getAxialYCoordinate(marbles[i].getIndex());
      y[i] = axialY;
    }
    return Arrays.stream(y).allMatch(s -> s == y[0]);
  }

  /**
   * .
   * 
   * @param marbles
   *          that need to be checked
   * @return if marbles lie along z axis
   */
  public boolean isZRow(Field[] marbles) {
    int totalmarbles = marbles.length;
    int[] z = new int[totalmarbles];
    for (int i = 0; i < totalmarbles; i++) {
      int axialZ = getAxialZCoordinate(marbles[i].getIndex());
      z[i] = axialZ;
    }
    return Arrays.stream(z).allMatch(s -> s == z[0]);
  }

  /**
   * .
   * 
   * @param marbles
   *          that need to be checked
   * @return if marbles are in a line and adjacent to each other or not
   */
  public boolean isLine(Field[] marbles) {
    int x1 = getAxialXCoordinate(marbles[0].getIndex());
    int y1 = getAxialYCoordinate(marbles[0].getIndex());
    int x2 = getAxialXCoordinate(marbles[1].getIndex());
    int y2 = getAxialYCoordinate(marbles[1].getIndex());
    int x3 = 0;
    int y3 = 0;
    if (marbles.length == 2) {
      if (isYRow(marbles) || isXRow(marbles)) {
        return (Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) == 1);
      } else if (isZRow(marbles)) {
        return (Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) == Math
            .sqrt(2));
      } else {
        return false;
      }
    } else {
      x3 = getAxialXCoordinate(marbles[2].getIndex());
      y3 = getAxialYCoordinate(marbles[2].getIndex());
      double x1x2 = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
      double x2x3 = Math.sqrt(Math.pow(x2 - x3, 2) + Math.pow(y2 - y3, 2));
      if (isXRow(marbles) || isYRow(marbles)) {
        return x1x2 == 1 && x2x3 == 1;
      } else if (isZRow(marbles)) {
        return x1x2 == Math.sqrt(2) && x2x3 == Math.sqrt(2);
      }
      return false;
    }
  }

  /**
   * .
   * 
   * @param marbles
   *          whose lines needs to be found
   * @return indexes of marbles along that line
   * @requires marbles whose line needs to be found
   */
  // @requires marbles.length >= 2
  // @requires isLine(marbles) == true
  // @requires marbles != null
  // @ensures result.length != 0

  public int[] getLine(Field[] marbles) {
    int[] lineCoordinates = null;
    int totalmarbles = marbles.length;
    int[] x = new int[totalmarbles];
    int[] y = new int[totalmarbles];
    int[] z = new int[totalmarbles];
    for (int i = 0; i < totalmarbles; i++) {
      int axialX = getAxialXCoordinate(marbles[i].getIndex());
      int axialY = getAxialYCoordinate(marbles[i].getIndex());
      int axialZ = getAxialZCoordinate(marbles[i].getIndex());
      x[i] = axialX;
      y[i] = axialY;
      z[i] = axialZ;
    }
    boolean matchX = Arrays.stream(x).allMatch(s -> s == x[0]);
    boolean matchY = Arrays.stream(y).allMatch(s -> s == y[0]);
    boolean matchZ = Arrays.stream(z).allMatch(s -> s == z[0]);
    if (matchX) {
      lineCoordinates = lineFinder("X", x[0]);
    } else if (matchY) {
      lineCoordinates = lineFinder("Y", y[0]);
    } else if (matchZ) {
      lineCoordinates = lineFinder("Z", z[0]);
    }
    return lineCoordinates;
  }

  /**
   * .
   * 
   * @param axis
   *          of the marbles
   * @param value
   *           of the coordinate along that axis, 
   *           if marbles lie along a line they have same x , y or z coordinate
   * @return indexes of the coordinates along that axis and value
   */

  // @requires axis.equals("X") || axis.equals("Y") || axis.equals("Z")
  // @requires value >=0 && value <= 11

  private int[] lineFinder(String axis, int value) {
    ArrayList<Integer> arraylist = new ArrayList<>();
    for (int i = 0; i < TOTALROWS; i++) {
      Map<Integer, Integer> rowmap = getRowMap(i);
      for (Map.Entry<Integer, Integer> entry : rowmap.entrySet()) {
        if (axis.equals("X")) {
          if (entry.getKey() == value) {
            arraylist.add(getIndex(entry.getKey(), entry.getValue()));
          }
        } else if (axis.equals("Y")) {
          if (entry.getValue() == value) {
            arraylist.add(getIndex(entry.getKey(), entry.getValue()));
          }
        } else if (axis.equals("Z")) {
          if (entry.getValue() + entry.getKey() == value) {
            arraylist.add(getIndex(entry.getKey(), entry.getValue()));
          }
        }
      }
    }
    int[] lineIndexes = new int[arraylist.size()];
    for (int i = 0; i < arraylist.size(); i++) {
      lineIndexes[i] = arraylist.get(i);
    }
    return lineIndexes;
  }


}