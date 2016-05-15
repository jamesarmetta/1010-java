package tenten;
/*

 * Reads a file into an array of strings
 */

import java.io.*;
import java.util.*;

class FileIO
{
   public String file;
   public ArrayList<String> lines;

   public FileIO (String f)
   {file = f;
    try {// Open the file
         FileInputStream fstream = new FileInputStream(file);
         // Convert fstream to a DataInputStream
         BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
	       // Initialise lines
         lines = new ArrayList<>();
         // Read lines while they keep coming
         
         while (in.ready()) {
           lines.add(in.readLine());
         }
         // Close the data stream
         in.close();
        }
    catch (Exception e) {System.err.println("File input error");}
   }
}
