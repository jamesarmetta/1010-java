package tenten;

import java.util.ArrayList;

/**
 * This class models the 1010! set-up.
 */
public class GameState
{
    public static final int NUMBER_OF_SQUARES = 10; // the extent of the board in both directions
    public static final int NUMBER_OF_BOXES   = 3;  // the number of boxes in the game

    private Square[][] board; // the current state of the board
    private Box[] boxes;      // the current state of the boxes
    private int score;        // the current score

    // initialise the instance variables for board
    // all squares and all boxes are initially empty
    /**
     * Constructor; takes no parameters. 
     *
     * Sets up 10x10 array as representation of board state, with each square initially empty.
     *
     * Sets up 3 boxes to hold pieces that are waiting to be placed, initially empty
     */
    public GameState()
    {
        // Sets up 10x10 board gs.getBoard()
        board = new Square[NUMBER_OF_SQUARES][NUMBER_OF_SQUARES];
        //Sets up 3 boxes to hold pieces waiting to be placed
        boxes = new Box[3];
        //Initializes each square as empty
        for (int i = 0; i < NUMBER_OF_SQUARES; i++) {
            for (int j = 0; j < NUMBER_OF_SQUARES; j++) {
                board[i][j] = new Square();
            }
        }
        //Initializes each box as empty
        for (int k = 0; k < NUMBER_OF_BOXES; k++) {
            boxes[k] = new Box();
        }
        score = 0;
    }

    /** Return the current state of the board */
    public Square[][] getBoard()
    {
        return board;
    }

    /** Return the current contents of Box i*/
    public Box getBox(int i)
    {
        if (0 <= i && i < NUMBER_OF_BOXES) {
            return boxes[i];
        }
        else {
            return null;
        }
    }

    /** Return the current score*/
    public int getScore()
    {
        return score;
    }

    /** Return "ok" if p can be legally placed with its (notional) top-left corner at Square x,y
     * Otherwise return "offboard" if any part of p would be off the board
     * Otherwise return "occupied" if any part of p would sit on an occupied square */
    public String canPlacePiece(Piece p, int x, int y)
    {
        int xOffset = 0;
        int yOffset = 0;
        for (int[] pieceSquare : p.getOffsets()) {
            xOffset = pieceSquare[0];
            yOffset = pieceSquare[1];
            try{
                if(board[x + xOffset][y + yOffset].status()) {
                    return "occupied";
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                return "offboard";
            }
        }
        return "ok";
    }

    /** return a list holding all legal moves using the current set of boxes on the current board
    * each move is represented as an int[] of length 3:
    * {i, x, y} denotes that the piece in Box i can be legally placed with its (notional) top-left corner at Square x,y */
    public ArrayList<int[]> allLegalMoves()
    {
        ArrayList<int[]> legalMoves = new ArrayList<int[]>();
        Piece piece;
        //loops over the three boxes
        for (int boxIndex = 0; boxIndex < NUMBER_OF_BOXES; boxIndex++) {
            if (boxes[boxIndex].occupied()) {
                piece = boxes[boxIndex].getPiece();
                //iterates over each [x,y] position on board
                for (int i = 0; i < NUMBER_OF_SQUARES;i++) {
                    for (int j = 0; j < NUMBER_OF_SQUARES; j++) {
                        //if it is okay to be placed there, adds to legalMoves
                        if (canPlacePiece(piece, i,j).equals("ok")) {
                            legalMoves.add(new int[]{boxIndex, i,j});
                        }
                    }
                }
            }
        }
        return legalMoves;
    }
    
    /** Returns the number of full colums on the board */
    private ArrayList<Integer> getFullColumns()
    {
        ArrayList<Integer> fullColumns = new ArrayList<Integer>();
        //Loops through each column
        for (int x = 0; x < NUMBER_OF_SQUARES; x++) {
            for (int y = 0; y < NUMBER_OF_SQUARES; y++) {
                if (!(board[x][y].status())) {
                    break;
                }
                //If all square including 9 occupied, adds column num to fullColums
                else if (board[x][y].status() && y == 9) {
                    fullColumns.add(x);
                }
            }
        }
        return fullColumns;
    }
    
    /** Returns the number of full rows on the board */
    private ArrayList<Integer> getFullRows()
    {
        ArrayList<Integer> fullRows = new ArrayList<Integer>();
        for (int y = 0; y < NUMBER_OF_SQUARES; y++) {
            for (int x = 0; x < NUMBER_OF_SQUARES; x++) {
                if (!(board[x][y].status())) {
                    break;
                }
                else if ((board[x][y].status()) && x == 9) {
                    fullRows.add(y);
                }
            }
        }
        return fullRows;
    }
    
    /** Clear row */
    private void clearRow(int row)
    {
        for (int x = 0; x < NUMBER_OF_SQUARES; x++) {
            board[x][row].unset();
        }
        score = score + 10;
    }
    
    /** Clear column */
    private void clearColumn(int column)
    {
        for (int y = 0; y < NUMBER_OF_SQUARES; y++) {
            board[column][y].unset();
        }
        score = score + 10;
    }

    /** Place p on the board with its (notional) top-left corner at Square x,y
    * clear columns and rows as appropriate */
    public void placePiece(Piece p, int x, int y)
    {
        ArrayList<int[]> offsets = p.getOffsets();
        int xOccupied, yOccupied;
        ArrayList<Integer> fullRows, fullColumns;

        //add x,y to each position in offsets and sets that square to occupied
        if (canPlacePiece(p,x,y).equals("ok")) {
            for (int[] offset : offsets) {
                xOccupied = x + offset[0];
                yOccupied = y + offset[1];
                board[xOccupied][yOccupied].set(p.getColour());
                score = score + 1;
            }
        }
        fullRows = getFullRows();
        fullColumns = getFullColumns();
        for (int row : fullRows) {
            clearRow(row);
        }
        for (int column : fullColumns) {
            clearColumn(column);
        }
    }
}
