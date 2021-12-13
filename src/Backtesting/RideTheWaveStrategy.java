package Backtesting;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RideTheWaveStrategy {
	static List<Double> tickerOpen = new ArrayList<Double>();
	static List<Double> tickerClose = new ArrayList<Double>();
	static List<Double> tickerLow = new ArrayList<Double>();
	static List<Double> tickerHigh = new ArrayList<Double>();
	static List<String> tickerDate = new ArrayList<String>();
	static List<Double> tickerVolume = new ArrayList<Double>();
	static Double[] tickerVwap = null;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		
	static String file = "ADXS - 20210915.txt";
//	static File fileTrue = new File("C:\\Users\\nakul\\Documents\\" + file + " - " + localTime + ".txt");
//	static PrintWriter pw;
//	static FileOutputStream fo;
	public static void main(String[] args) throws FileNotFoundException {
//		pw = new PrintWriter(new FileOutputStream(fileTrue, true));
//		fo = new FileOutputStream(fileTrue, true);
		readFileAsList();
		boolean isBought = false, isSold = false;
		Double buyPrice = 0.0, sellPrice = 0.0;
		
		for (int i=2; i< tickerClose.size(); i++) {
			Double currentTicker = tickerClose.get(i);
			Double currentLowTicker = tickerLow.get(i);
			Double prevLowTicker = tickerLow.get(i-1);
			Double prevprevLowTicker = tickerLow.get(i-2);
			Double currentHighTicker = tickerHigh.get(i);
			Double prevHighTicker = tickerHigh.get(i-1);
			Double prevprevHighTicker = tickerHigh.get(i-2);
			
			if(!isBought && currentLowTicker.compareTo(prevLowTicker) > 0 && prevLowTicker.compareTo(prevprevLowTicker) > 0) {
				buyPrice = buyPrice + currentTicker;
				System.out.println("Bought at: " + tickerDate.get(i)  + ", " + currentTicker);
				isBought = true;
				isSold = false;
			}
			if(!isSold && currentHighTicker.compareTo(prevHighTicker) < 0 && prevHighTicker.compareTo(prevprevHighTicker) < 0) {
				sellPrice = sellPrice + currentTicker;
				System.out.println("Sold   at: " + tickerDate.get(i) + ", " + currentTicker);
				isBought = false;
				isSold = true;
			}
		}

		
		//		returnMultiplierProfitLoss();
		System.out.println("Start Price: " + tickerOpen.get(0) + "\tEnd Price:" + tickerClose.get(tickerClose.size()-1));
		System.out.println("Total Buy Price: " + buyPrice + "\tTotal Sold Price:" + sellPrice);
		System.out.print("Holding: " + isBought + "\t" + "Shorting: " + isSold + "\n");
		if(isBought) {
			Double profit = sellPrice - buyPrice + Double.valueOf(tickerClose.get(tickerClose.size() - 1));
			System.out.println("Total Profit: " + profit);
		}
		if(isSold) {
			Double profit = sellPrice - buyPrice - Double.valueOf(tickerClose.get(tickerClose.size() - 1));
			System.out.println("Total Profit: " + profit);
		}
	} 

	private static void readFileAsList() {
		String fileName = "C:\\Users\\nakul\\Documents\\HistoricalData - VWAP\\" + file;

		try {
			//			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(fileName),
					Charset.defaultCharset());
			for (String line : lines) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				tickerDate.add(splitLine.get(1));
				tickerOpen.add(Double.valueOf(splitLine.get(2)));
				tickerClose.add(Double.valueOf(splitLine.get(3)));
				tickerHigh.add(Double.valueOf(splitLine.get(4)));
				tickerLow.add(Double.valueOf(splitLine.get(5)));
				tickerVolume.add(Double.valueOf(splitLine.get(6)));
				
			}
			//								ticker.add(splitLine.get(3));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
