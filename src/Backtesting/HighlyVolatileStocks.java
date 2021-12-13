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
		String fileName = "/test/MasterHighAllAssets.txt";
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
			System.out.println("Highly Volatile Stocks for Period 2018-08 to 2021-10");
			
			for(Map.Entry<String, Integer> entry : volatilityCount.entrySet()) {
				if(entry.getValue() >= 36 && !entry.getKey().equals("Symbol")) {
					System.out.println(entry.getKey() + " " + entry.getValue());
					highlyVolatileStocks.add(entry.getKey());
				}
					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return highlyVolatileStocks;
	}
	
	
}

