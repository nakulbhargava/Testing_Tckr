package Backtesting;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This program will take the input and split it into multiple files with daily, weekly and monthly splits
 */
public class HighlyVolatileStocks {

	static List<String> ticker = null;

	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		
	public static void main(String[] args) throws FileNotFoundException {
		HighlyVolatileStocks demo = new HighlyVolatileStocks();
		List<String> highlyVolatileStocks = demo.readAndWriteFileAsList();

	} 

	private List<String> readAndWriteFileAsList() {
		String fileName = "/test/MasterOkVolatileAssets.txt";
		String fileName1= "/test/MasterHighAllAssets.txt";
		String fileName2 = "/test/MasterVolatileAssets.txt";
		List<String> highlyVolatileStocks = new ArrayList<String>();
		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(uri),
					Charset.defaultCharset());
			HashMap<String, Integer> volatilityCount = new HashMap<String, Integer>();

			for (String line : lines) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				volatilityCount.put(splitLine.get(0), volatilityCount.getOrDefault(splitLine.get(0), 0) + 1);
			}
			
			URI uri1 = this.getClass().getResource(fileName1).toURI();
			List<String> lines1 = Files.readAllLines(Paths.get(uri1),
					Charset.defaultCharset());

			for (String line : lines1) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				volatilityCount.put(splitLine.get(0), volatilityCount.getOrDefault(splitLine.get(0), 0) + 1);
			}
			
			URI uri2 = this.getClass().getResource(fileName2).toURI();
			List<String> lines2 = Files.readAllLines(Paths.get(uri2),
					Charset.defaultCharset());

			for (String line : lines2) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				volatilityCount.put(splitLine.get(0), volatilityCount.getOrDefault(splitLine.get(0), 0) + 1);
			}
			
			System.out.println("Highly Volatile Stocks for Period of 3 years");
			
			for(Map.Entry<String, Integer> entry : volatilityCount.entrySet()) {
				if(entry.getValue() >= 100 && !entry.getKey().equals("Symbol")) {
//					System.out.println(entry.getKey() + " " + entry.getValue());
					System.out.println(entry.getKey() + "=Y");
					highlyVolatileStocks.add(entry.getKey());
				}
					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return highlyVolatileStocks;
	}
	
	
}

