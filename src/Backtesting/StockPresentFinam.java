package Backtesting;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StockPresentFinam {
	public static void main(String args[])
	{
		try
		{
			File file1 = new File("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\test\\outputPennyStocks.txt");
			File file2 = new File("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\test\\FINAM_LIST.txt");
			BufferedReader reader1 = new BufferedReader(new FileReader(file1));
			String line = "", oldtext = "";
			String[] fbWords = new String[] {"", "Exchange", "LOW", "Name", "Company", "Inc.", "plc", "Shares", "Corporation",
							"Therapeutics", "Group", "inc.", "Holdings", "Pharmaceuticals", "Limited", "Partners", "Class", "Common", "Ltd.", "Manufacturing", "Co.",
							"Trust", "Holdings", "Inc", "Financial,", "Stock", "Resources,", "Biosciences,", "Group,", "Technologies", "Sciences,", "Corp.", "(The)",
							"Resources,", "Therapeutics,", "International,", "Company,", "Holdings,", "Pharmaceuticals,", "stock", "Financial", "Sciences", "S.A."  };
			List<String> wordList = Arrays.asList(fbWords); 
			
			while((line = reader1.readLine()) != null)
			{
				String[] words = line.split("\\s");
				for(String word: words) {
					if(!wordList.contains(word)  && hasWord(file2, word)) {
						System.out.println("Stock Found on line: " + line + "\t\t" + word);
					}
				}
			}
			reader1.close();
		
//			FileWriter writer = new FileWriter("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\test\\FinamFound.txt");
//			writer.write(newtext);writer.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	public static boolean hasWord(File logFile, String word){ 
		try{
			BufferedReader buff=new BufferedReader(new FileReader(logFile));
			String s;
			while((s=buff.readLine())!=null){ 
				if(s.trim().contains(word)){
					buff.close();
					return true;
				}               
			}
			buff.close();
		}catch(Exception e){e.printStackTrace();}
		
		return false;
	}
}
