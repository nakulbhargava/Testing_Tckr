import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReplaceStablePrices {
	 public static void main(String args[])
     {
     try
         {
         File file = new File("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\REAL\\ConfigConstants.java");
         BufferedReader reader = new BufferedReader(new FileReader(file));
         String line = "", oldtext = "";
         while((line = reader.readLine()) != null)
             {
             oldtext += line + "\r\n";
         }
         reader.close();
         // replace a word in a file
         //String newtext = oldtext.replaceAll("drink", "Love");
        
         //To replace a line in a file
         String newtext = oldtext.replaceAll("UBER*", "UBER = 54.0");
        
         FileWriter writer = new FileWriter("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\REAL\\ConfigConstants.java");
         writer.write(newtext);writer.close();
     }
     catch (IOException ioe)
         {
         ioe.printStackTrace();
     }
 }
}
