package tenten;

import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;

/**
 * This class models pieces in 1010!.
 */
public class Piece
{
    // the maximum extent of any piece-type
    public static final int MAX_PIECE_SIZE = 5;
    private static final Color[] PIECE_COLOURS = {Color.red, Color.blue, 
                                                  Color.green, Color.yellow, 
                                                  Color.pink, Color.magenta,
                                                  Color.cyan};
    private Random r;
    private ArrayList<int[]> offsets; // the offsets for the actual squares in this piece 
    private Color colour;             // the colour used for this piece 
    private int xSize, ySize;         // the extent of this piece on each axis

    /** Initialise this piece by setting up the instance variables 
    *  Each entry on xs will be either:
    * - a two-digit number MN representing a square with x-offset = M and y-offset = N or;
    * - a one-digit number  N representing a square with x-offset = 0 and y-offset = N */
    public Piece(int[] xs)
    {
        r = new Random();
        //Offsets is an arraylist of arrays of form <x,y>
        offsets = new ArrayList<int[]>();
        //Variables representing x and y offsets of each square in piece
        int x;
        int y;
        int xMax = 0;
        int yMax = 0;
        //Loops through xs, adds array of [x,y] to offsets
        for (int i = 0; i < xs.length; i++) {
            x = xs[i] / 10;
            y = xs[i] % 10;
            offsets.add(i, new int[]{x,y});
            if (x > xMax) {
                xMax = x;
            }
            if (y > yMax) {
                yMax = y;
            }
        }
        xSize = xMax + 1;
        ySize = yMax + 1;
        colour = PIECE_COLOURS[r.nextInt(PIECE_COLOURS.length)];
    }

    /**Return piece offsets */
    public ArrayList<int[]> getOffsets()
    {
        return offsets;
    }
    
    /**Return piece colour */
    public Color getColour()
    {
        return colour;
    }
    
    /**Return piece x size */
    public int getxSize()
    {
        return xSize;
    }

    /**Return piece y size */
    public int getySize()
    {
        return ySize;
    }
}
