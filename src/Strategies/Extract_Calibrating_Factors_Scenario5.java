package Strategies;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * There are 4 scenarios to be implemented here.
 * Scenario1: Entry 1 high and stoploss/exitAt 2 higher than entry 1, Make profit by short selling at 1
 * Scenario2: Entry 1 high and stopLoss/exitAt 2 lower than entry 1, Make profit by buy at 1 and sell at price higher than 1
 * Scenario3: Entry 1 low and stopLoss/exitAt 2 lower than entry 1, Make profit by buy at 1 and sell at price higher than 1
 * Scenario4: Entry 1 low and stopLoss/exitAt 2 higher than entry 1, Make profit by short selling at 1
 * 
 * Other scenarios to consider:
 * Scenario 5: high high: entry 1 at high and exit 2 at higher, downtrend at some point lower than 1 to do nothing
 * Scenario 6: low low: entry 1 at low and exit 2 at lower, uptrend some point higher than 1 to do nothing
 * 
 * Below program deals with scenario5
 */
public class Extract_Calibrating_Factors_Scenario5 {
	//	static Double profitMultiplier = 1.001;
	//	static boolean profitMet = false;
	static Double totalLoss = 0.00, totalProfit = 0.00;
	static Double downTrendMultiplier = 0.995;
	static int maxShares = 100;
	static Double maxProfit = 0.0;
	static Double stableHighPrice = 61.9;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		

	static File fileTrue = new File("C:\\Users\\nakul\\Documents\\US1.SNAP_210405_210409_" + stableHighPrice + " - " + localTime + ".txt");
	static PrintWriter pw;
	static FileOutputStream fo;
	public static void main(String[] args) throws FileNotFoundException {
		pw = new PrintWriter(new FileOutputStream(fileTrue));
		fo = new FileOutputStream(fileTrue);
		Extract_Calibrating_Factors_Scenario5 demo = new Extract_Calibrating_Factors_Scenario5();
		List<String> ticker = demo.readFileAsList();

		//Not to have entryAt more than 10 as the number of occurrence will reduce, 
		//we can assume that a stock price wont be changed by 1.5 times and hence exitAt count is 500
				for (int i = 5 ; i < 40; i=i+5)
				{
					for(int j = 10; j < 100; j=j+3) {
						Double entryMultiplier = 1 + Double.valueOf(i)/10000;
						Double exitMultiplier = 1 + Double.valueOf(j)/1000;
						returnMultiplierProfitLoss(ticker, entryMultiplier, exitMultiplier);
						totalLoss = 0.00; totalProfit = 0.00;
					}
				}

//		returnMultiplierProfitLoss(ticker, 1.0005, 1.01);
		System.out.println("Max Profit: " + maxProfit);
		pw.write("Max Profit: " + maxProfit);
	} 

	private List<String> readFileAsList() {
		String fileName = "/test/US1.SNAP_210405_210409.txt";

		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(uri),
					Charset.defaultCharset());
			List<String> ticker = new ArrayList();

			for (String line : lines) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				ticker.add(splitLine.get(3));
			}
			return ticker;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static int returnMultiplierPosition (List<String> ticks, int position, Double multiplier) {
		int ticksLength = ticks.size();
		Double startingValue = Double.valueOf(ticks.get(position));
		Double currentValue;
		for (int i = position; i < ticksLength; i++) {
			currentValue = Double.valueOf(ticks.get(i));
			if(currentValue.compareTo(startingValue*multiplier) > 0 || currentValue.compareTo(startingValue*multiplier) == 0){
				return i;
			}
		}
		return -1;
	}

	private static int returnLowerMultiplierPosition (List<String> ticks, int position, Double multiplier) {
		int ticksLength = ticks.size();
		Double startingValue = Double.valueOf(ticks.get(position));
		Double currentValue;
		for (int i = position; i < ticksLength; i++) {
			currentValue = Double.valueOf(ticks.get(i));
			if(currentValue.compareTo(startingValue*multiplier) <= 0){
				return i;
			}
		}
		return -1;
	}
	private static Double returnMultiplierProfitLoss(List<String> ticker, Double entryMultiplier, Double exitMultiplier) {
		int tickerLength = ticker.size();
		//		Double entryPoint, exitPoint;
		List<Integer> repeatEntry = new ArrayList<Integer>();
		List<Double> exitPositions = new ArrayList<Double>();
		int releaseQuantity = 0;
		int entryAt = 1, exitAt = 1;
		int downTrendAt = 1;
		int maxCount = 0;
		//		Double minima = 0.00;
		//		List<Double> allfactors = new ArrayList<Double>();
		//		Double currentFactor = 0.00;
		int totalCount = 0;

		for(int i = 1; i < tickerLength; i++) {
			entryAt = returnMultiplierPosition(ticker, i, entryMultiplier);
			if (entryAt < 0) {
				//				System.out.println("Multiplier:" + entryMultiplier + " doesnot exist for position: " + i + " with value: " + ticker.get(i) + "\n");
				continue;
			}
			downTrendAt = returnLowerMultiplierPosition(ticker, i, downTrendMultiplier);
			if(downTrendAt < entryAt && downTrendAt != -1) {
				for (int j = i; j <= downTrendAt; j++) {
					int jsQuantity = 0;
					for(Double exitPosition : exitPositions) {
						if ( exitPosition.intValue() == j) {
							jsQuantity++;
							releaseQuantity++;
						}	
					}
					for(int k=0; k < jsQuantity; k++) {
						exitPositions.remove(Double.valueOf(j));
					}
				}
				totalCount = totalCount - releaseQuantity;
//								System.out.println(i + "\t" + ticker.get(i) + "\tEntryAt: " + entryAt + "\t" + ticker.get(entryAt) + "\t" + totalCount + "\t" + releaseQuantity);
//								pw.write(i + "\t" + ticker.get(i) + "\t" + entryAt + "\t" + ticker.get(entryAt) + "\t" + totalCount + "\t" + releaseQuantity + "\n");
				releaseQuantity = 0;
				i = downTrendAt;
				continue;
			}
			else {
				for (int j = i; j <= entryAt; j++) {
					int jsQuantity = 0;
					for(Double exitPosition : exitPositions) {
						if ( exitPosition.intValue() == j) {
							jsQuantity++;
							releaseQuantity++;
						}	
					}
					for(int k=0; k < jsQuantity; k++) {
						exitPositions.remove(Double.valueOf(j));
					}
				}
				totalCount = totalCount - releaseQuantity;
//								System.out.println(i + "\t" + ticker.get(i) + "\tEntryAt: " + entryAt + "\t" + ticker.get(entryAt) + "\t" + totalCount + "\t" + releaseQuantity);
				//				pw.write(i + "\t" + ticker.get(i) + "\t" + entryAt + "\t" + ticker.get(entryAt) + "\t" + totalCount + "\t" + releaseQuantity + "\n");
				releaseQuantity = 0;
				i = entryAt;
				if(totalCount >= maxShares || Double.valueOf(ticker.get(i)).compareTo(stableHighPrice) > 0) {
					continue;
				}
			}

			if(repeatEntry.contains(entryAt)) continue;
			repeatEntry.add(entryAt);
			exitAt = returnMultiplierPosition(ticker, entryAt, exitMultiplier);
			if (exitAt < 0) {
				exitAt = tickerLength - 1;
				totalLoss = totalLoss + Double.valueOf(ticker.get(exitAt)) - Double.valueOf(ticker.get(entryAt)) ;
			}
			else {
				totalProfit = totalProfit + Double.valueOf(ticker.get(entryAt))*(exitMultiplier - 1);
				exitPositions.add(Double.valueOf(exitAt));
			}
			totalCount++;
			if (totalCount > maxCount) maxCount = totalCount;
//						System.out.println("EntryAt: " + entryAt + "\t" + ticker.get(entryAt) + "\tExitAt: " + exitAt + "\t" + ticker.get(exitAt) + "\t" + totalLoss + "\t" + totalProfit + "\t" + totalCount);
//						pw.write(entryAt + "\t" + ticker.get(entryAt) + "\t" + exitAt + "\t" + ticker.get(exitAt) + "\t" + totalLoss + "\t" + totalProfit + "\t" + totalCount + "\n");
		}

		Double netProfit = totalProfit + totalLoss;

		Double profitPerShare = netProfit/maxCount;
		if (profitPerShare.compareTo(maxProfit) > 0) maxProfit = profitPerShare;

		System.out.println(entryMultiplier + "\t" + exitMultiplier + "\t" +  "NetProfit:" + "\t" + netProfit + "\tTotal Count: " + totalCount + "\tMax Count: " + maxCount + "\tProfitPerShare: " + profitPerShare);
		pw.write(entryMultiplier + "\t" + exitMultiplier + "\t" +  "NetProfit:" + "\t" + netProfit + "\tTotal Count: " + totalCount + "\tMax Count: " + maxCount + "\tProfitPerShare: " + profitPerShare + "\n");
		return 0.00;
	}
}