package tenten;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintWriter;
import java.io.File;

/**
 * This class plays the 1010!
 */
public class Play1010  implements MouseListener
{

    private static final int NUMBER_OF_PIECES = 19;
    private static final int SQUARE_SIZE = 30;
    private static final int NUMBER_OF_BOXES = 3;

    //Dimensions
    private static final int DISPLAY_WIDTH = 700;
    private static final int DISPLAY_HEIGHT = 500;
    private static final int LEFT_BOARD = 100;
    private static final int RIGHT_BOARD = 400;
    private static final int TOP_BOARD = 100;
    private static final int BOTTOM_BOARD = 400;
    private static final int LOGO_X_POSITION = 156;

    //Box Display Dimensions
    //The x,y coordinates of the top left of the display section for the boxes
    private static final int[] BOX_Y_COORDS = new int[]{50,210,330};
    private static final int BOX_PLACE_X = 500;
    private static final int BOTTOM_OF_BOXES = 520;

    //Design Constants
    private static final Color BORDER_COLOUR = Color.white;
    private static final Color BACKGROUND_COLOUR = Color.black;
    private static final Color OUTLINE = Color.black;
    private static final Color GRID_COLOUR = Color.darkGray;
    @SuppressWarnings("unused")
	private static final Color BOX_COLOUR = Color.darkGray;
    @SuppressWarnings("unused")
	private static final Color FILLED_SQUARE_COLOUR = Color.green;
    private static final Color SCORE_COLOUR = Color.gray;
    private static final Font SCORE_FONT = new Font("Dialog", Font.BOLD, 30);
    private static final Font GAME_OVER_FONT = new Font("Dialog", Font.BOLD, 60);
    private static final Font HIGH_SCORE_FONT = new Font("Dialog",Font.PLAIN, 20);

    private int moveNum;
    private int[] highScores;

    private Piece[] pieces;
    private GameState gameState;
    private SimpleCanvas sc;
    private Random r;
    private boolean isBoxSelect;
    private int boxSelected;
    private boolean updateBoxes;
    private boolean isHumanGame;

    @SuppressWarnings("unused")
	private int boxesOccupied;
    @SuppressWarnings("unused")
	private int score;

    /**
     * Starts a human game
     */
    public static void main(String[] args) {
        new Play1010();
    }
    public Play1010()
    {
        isHumanGame = true;
        setUpGame();
        sc.addMouseListener(this);
        putNewPiecesInBox(new int[] {r.nextInt(pieces.length),r.nextInt(pieces.length),r.nextInt(pieces.length)} );
        ArrayList<int[]> legalMoves = gameState.allLegalMoves();
        moveNum = 0;
        updateBoxes = false;
        
        drawBoard();
        while (!(legalMoves.isEmpty())) {
            //Clears that piece from box
            drawBoard();
            //When all pieces have been played, fills boxes with new pieces
            if (updateBoxes) {
                putNewPiecesInBox(new int[] {r.nextInt(pieces.length),r.nextInt(pieces.length),r.nextInt(pieces.length)});
                updateBoxes = false;
                drawBoard();
            }
            //Updates legal move after move has been played
            legalMoves = gameState.allLegalMoves();
        }
        drawGameOverScreen();
    }

    /**
     * Starts a computer player game, using random moves given an input file.
     *
     * @param file The file name of the input file as a string
     */
    public Play1010(String file)
    {
        isHumanGame = false;
        setUpGame();
        playComputerGame(file);
    }

    /**
     * Plays a computer game
     * @param file The input file - a list of pieces
     */
    private void playComputerGame(String file)
    {
        //Holds lines of computer input
        ArrayList<int[]> compPlayerInput = readInFile(file);
        //Holds all current legal moves
        ArrayList<int[]> legalMoves;
        //Puts first line of pieces in associated boxes
        putNewPiecesInBox(compPlayerInput.get(0));
        //Gets first set of legal moves
        legalMoves = gameState.allLegalMoves();
        drawBoard();
        int inputLineNum = 1;
        moveNum = 1;
        int [] move;
        int box, x,y;
        @SuppressWarnings("unused")
		Piece piece;
        while (!(legalMoves.isEmpty())) {
            //Gets the next move
            move = legalMoves.get(r.nextInt(legalMoves.size()));
            box = move[0];
            x = move[1];
            y = move[2];
            //plays move
            gameState.placePiece(gameState.getBox(box).useBox(), x, y);
            //Clears that piece from box
            clearBox(box);
            drawBoard();
            //When all pieces have been played, fills boxes with new pieces
            if (moveNum % NUMBER_OF_BOXES == 0) {
                try {
                    putNewPiecesInBox(compPlayerInput.get(inputLineNum));
                    inputLineNum++;
                    drawBoard();
                }
                catch (IndexOutOfBoundsException e) {
                    //When no more input lines to be read
                    System.out.println("input complete");
                }
            }
            //Updates legal move after move has been played
            legalMoves = gameState.allLegalMoves();
            moveNum++;
        }
        drawGameOverScreen();
    }

    /**Sets up the board for a new game */
    private void setUpGame()
    {

        pieces = new Piece[NUMBER_OF_PIECES];
        createPieces();
        //Random
        r = new Random();
        //Create Display
        sc = new SimpleCanvas("1010!", DISPLAY_WIDTH, DISPLAY_HEIGHT, BACKGROUND_COLOUR);
        //Instantiate GameState
        gameState = new GameState();
        highScores = new int[3];
        readInCurrentHighScore();
        isBoxSelect = true;
    }

    /**
     * Puts new pieces in boxes
     * @param newPieces int[] of piece numbers
     */
    private void putNewPiecesInBox(int[] newPieces)
    {
        for (int i = 0; i < NUMBER_OF_BOXES; i++) {
            try {
                gameState.getBox(i).fillBox(pieces[newPieces[i]]);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Input contains an invalid piece number.");
            }
        }
    }

    /** Reads in input file for random computer game */
    private ArrayList<int[]> readInFile(String file)
    {
        ArrayList<int[]> compPlayer = new ArrayList<int[]>();
        FileIO inputFile = new FileIO(file);
        try {
            for (String line : inputFile.lines) {
                String[] stringSplit = line.split(" ");
                int[] lineInts = new int[stringSplit.length];
                for (int i = 0; i < lineInts.length; i++) {
                    lineInts[i] = Integer.parseInt(stringSplit[i]);
                }
                compPlayer.add(lineInts);
            }
        }
        catch (NullPointerException e) {
            System.out.println("Incorrect file name");
            System.exit(0);
        }
        return compPlayer;
    }

    /** Creates and defiines the game pieces */
    private void createPieces()
    {
        //Pieces 0-3
        pieces[0] = new Piece(new int[]{0,1});
        pieces[1] = new Piece(new int[]{0,1,2});
        pieces[2] = new Piece(new int[]{0,1,2,3});
        pieces[3] = new Piece(new int[]{0,1,2,3,4});
        //Pieces 4-7
        pieces[4] = new Piece(new int[]{0,10});
        pieces[5] = new Piece(new int[]{0,10,20});
        pieces[6] = new Piece(new int[]{0,10,20,30});
        pieces[7] = new Piece(new int[]{0,10,20,30,40});
        //Pieces 8-10
        pieces[8] = new Piece(new int[]{0});
        pieces[9] = new Piece(new int[]{0,10,11,1});
        pieces[10] = new Piece(new int[]{0,10,20,1,11,21,2,12,22});
        //Pieces 11-14
        pieces[11] = new Piece(new int[]{0,1,2,12,22});
        pieces[12] = new Piece(new int[]{0,1,11});
        pieces[13] = new Piece(new int[]{10,11,1});
        pieces[14] = new Piece(new int[]{20,21,22,12,2});
        //Pieces 15-18
        pieces[15] = new Piece(new int[]{0,10,20,1,2});
        pieces[16] = new Piece(new int[]{0,1,10});
        pieces[17] = new Piece(new int[]{0,10,20,21,22});
        pieces[18] = new Piece(new int[]{0,10,11});

    }

    /** Draws the game board */
    private void drawBoard()
    {
        sc.drawJPEG("logo.jpg",LOGO_X_POSITION,0);
        drawScore();
        drawOccupiedSquares();
        drawGrid();
        drawBoxes();
        sc.wait(100);
    }

    /** Draws the border of the game grid */
    private void drawGrid()
    {
        //Draw Borders of Board
        sc.drawLine(LEFT_BOARD,TOP_BOARD,LEFT_BOARD,BOTTOM_BOARD, BORDER_COLOUR);
        sc.drawLine(RIGHT_BOARD,TOP_BOARD,RIGHT_BOARD,BOTTOM_BOARD, BORDER_COLOUR);
        sc.drawLine(LEFT_BOARD,TOP_BOARD,RIGHT_BOARD,TOP_BOARD, BORDER_COLOUR);
        sc.drawLine(LEFT_BOARD,BOTTOM_BOARD,RIGHT_BOARD,BOTTOM_BOARD, BORDER_COLOUR);
        //Draws grid of squares
        for (int i = LEFT_BOARD + SQUARE_SIZE; i < RIGHT_BOARD; i = i + SQUARE_SIZE) {
            sc.drawLine(i,TOP_BOARD,i,BOTTOM_BOARD,GRID_COLOUR);
        }
        for (int j = TOP_BOARD + SQUARE_SIZE; j < BOTTOM_BOARD; j = j + SQUARE_SIZE) {
            sc.drawLine(LEFT_BOARD,j,RIGHT_BOARD,j,GRID_COLOUR);
        }
    }

    /** Draws the occupied squares */
    @SuppressWarnings("static-access")
	private void drawOccupiedSquares()
    {
        //Loops through each square of the board, draws it if occupied
        for (int i = 0; i < gameState.NUMBER_OF_SQUARES; i++) {
            for (int j = 0; j < gameState.NUMBER_OF_SQUARES; j++) {
                if (gameState.getBoard()[i][j].status()) {
                    drawSquare(LEFT_BOARD + SQUARE_SIZE * i, TOP_BOARD + SQUARE_SIZE * j,gameState.getBoard()[i][j].getColour());
                }
                else {
                    drawSquare(LEFT_BOARD + SQUARE_SIZE * i, TOP_BOARD + SQUARE_SIZE * j,BACKGROUND_COLOUR);
                }
            }
        }
    }

    /** Draws the score */
    private void drawScore()
    {
        sc.setFont(SCORE_FONT);
        sc.drawRectangle(100,400,300,550, BACKGROUND_COLOUR);
        String scoreString = "Score: " + Integer.toString(gameState.getScore());
        sc.drawString(scoreString, 100,50, SCORE_COLOUR );
        sc.setFont(HIGH_SCORE_FONT);
        sc.drawString("High Scores", 260,50, SCORE_COLOUR);
        sc.drawString(Integer.toString(highScores[0]), 260,82, SCORE_COLOUR);
       
    }

    /** Draws the game over screen when the game is finished*/
    private void drawGameOverScreen()
    {
        sc.wait(100);
        sc.drawRectangle(0,0,DISPLAY_WIDTH,DISPLAY_HEIGHT,BACKGROUND_COLOUR);
        sc.drawJPEG("logo.jpg",100,100);
        sc.setFont(GAME_OVER_FONT);
        sc.drawString("Game Over", 100,140, SCORE_COLOUR );
        sc.setFont(SCORE_FONT);
        if (isHumanGame) {
            if(gameState.getScore() > highScores[0]) {
                sc.drawString("New High Score", 0,200, SCORE_COLOUR );
            }
            writeHighScore();
        }
        String scoreString = "Score: " + Integer.toString(gameState.getScore());
        sc.drawString(scoreString, 100,170, SCORE_COLOUR );

    }

    /**
     * Draws one square at <x,y> (pixels) in colour c
     * @param x The x coordinate of the top left corner of where the piece is to be placed
     * @param y The y coordinate of the top left corner of where the piece is to be placed
     * @param c Colour of shape to be drawn
     */
    private void drawSquare(int x,int y, Color c)
    {
        sc.drawRectangle(x, y, x + SQUARE_SIZE, y + SQUARE_SIZE, c);
        //Draws black border around each individual shape
        sc.drawLine(x, y, x + SQUARE_SIZE, y, OUTLINE);
        sc.drawLine(x, y, x, y + SQUARE_SIZE, OUTLINE);
        sc.drawLine(x + SQUARE_SIZE, y, x + SQUARE_SIZE, y + SQUARE_SIZE, OUTLINE);
        sc.drawLine(x, y + SQUARE_SIZE, x + SQUARE_SIZE, y + SQUARE_SIZE, OUTLINE);
    }

    /**
     * Draws a given piece at position <x,y> representing the top corner of the shape
     * @param p The piece to be drawn
     * @param x The x coordinate of the top left corner of where the piece is to be placed
     * @param y The y coordinate of the top left corner of where the piece is to be placed
     *
     */
    private void drawPiece(Piece p, int x, int y)
    {
        @SuppressWarnings("unused")
		int topLeftX, topLeftY, bottomRightX, bottomRightY;
        Color pieceColour = p.getColour();
        //Loops through individual squares of piece, drawing them at their correct positions
        for (int[] xy : p.getOffsets()) {
            topLeftX = x + xy[0] * SQUARE_SIZE;
            topLeftY = y + xy[1] * SQUARE_SIZE;
            drawSquare(topLeftX, topLeftY, pieceColour);

        }
    }

    /** Draws the boxes containing the pieces to be placed */
    private void drawBoxes()
    {
        Piece piece;
        for (int i = 0; i < 3; i++) {
            if (gameState.getBox(i).occupied()){
                piece = gameState.getBox(i).getPiece();
                drawPiece(piece, BOX_PLACE_X, BOX_Y_COORDS[i]);
            }
            else {
                clearBox(i);
            }
        }
    }

    /** Clears the box when a piece is placed*/
    private void clearBox(int b)
    {
        sc.drawRectangle(BOX_PLACE_X, BOX_Y_COORDS[b], BOX_PLACE_X + 5 * SQUARE_SIZE,
            BOX_Y_COORDS[b] + 5 * SQUARE_SIZE, BACKGROUND_COLOUR);
    }

    /** Gets the current high score */
    private void readInCurrentHighScore() 
    {

        int i = 0;
        try {
            FileIO inputFile = new FileIO("highScoreLog.txt");
            for (String line : inputFile.lines) {
                highScores[i] = Integer.parseInt(line);
                i++;
            }
        }

        catch (NullPointerException e) {
            highScores[0] = 100;
       
        }
    }

    /** Update high score record */
    private void writeHighScore() 
    {
        if (gameState.getScore() > highScores[0]) {
            highScores[0] = gameState.getScore();
        }
        else if (gameState.getScore() > highScores[1]) {
            highScores[1] = gameState.getScore();
        }
        try {
            PrintWriter writer = new PrintWriter("highScoreLog.txt");
            writer.println(highScores[0]);
            writer.println(highScores[1]);
            writer.println(highScores[2]);
            writer.close();
        }
        catch (java.io.FileNotFoundException e) {
            @SuppressWarnings("unused")
			File file = new File("highScoreLog.txt");
            writeHighScore();
        }

    }

    /** Handles mouseclicks */
    public void mouseClicked(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if (x > BOX_PLACE_X && y > BOX_Y_COORDS[0]) {
            if ( y < BOX_Y_COORDS[1] ) {
                boxSelected = 0;
                isBoxSelect = false;
            }
            if (y > BOX_Y_COORDS[1] && y < BOX_Y_COORDS[2]) {
                boxSelected = 1;
                isBoxSelect = false;
            }
            if (y > BOX_Y_COORDS[2] && y < BOTTOM_OF_BOXES ) {
                boxSelected = 2;
                isBoxSelect = false;
            }
        }
        
        boolean isOnBoard = LEFT_BOARD < x && x < RIGHT_BOARD && TOP_BOARD < y && y < BOTTOM_BOARD;
        //If click is within board
        if (!isBoxSelect && isOnBoard) 
        {
            //Check if box select inside square, check move okay then place piece
            int squarex = (x - LEFT_BOARD) / SQUARE_SIZE;
            int squarey = (y - TOP_BOARD) / SQUARE_SIZE;
            if (gameState.canPlacePiece(gameState.getBox(boxSelected).getPiece(), squarex, squarey).equals("ok"))
            {
                gameState.placePiece(gameState.getBox(boxSelected).useBox(), squarex, squarey);
                clearBox(boxSelected);
                isBoxSelect = true;
                moveNum++;
                //If all boxes have been used, refills them
                if (moveNum % NUMBER_OF_BOXES == 0) {
                    updateBoxes = true;
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e){}

    public void mouseEntered(MouseEvent e) {}
      
    public void mouseExited(MouseEvent e)  {}
}
