import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
 * high high: entry 1 at high and exit 2 at higher, stoploss/exitAt some point lower than 1
 * low low: entry 1 at low and exit 2 at lower, stoploss/exitAt some point higher than 1
 * 
 * Below program deals with scenario1
 */
public class Extract_Calibrating_Factors_Scenario6 {
//	static Double profitMultiplier = 1.001;
//	static boolean profitMet = false;
	static Double totalLoss = 0.00, totalProfit = 0.00;
	static Double upTrendMultiplier = 1.0005;
	static int maxShares = 10;
	static Double maxProfit = 0.0;
	static Double stableLowPrice = 57.59;
	public static void main(String[] args) {
		Extract_Calibrating_Factors_Scenario6 demo = new Extract_Calibrating_Factors_Scenario6();
		List<String> ticker = demo.readFileAsList();
		
		//Not to have entryAt more than 10 as the number of occurrence will reduce, 
		//we can assume that a stock price wont be changed by 1.5 times and hence exitAt count is 500
		for (int i = 5 ; i < 40; i=i+5)
		{
			for(int j = 1; j < 100; j=j+2) {
				Double entryMultiplier = 1 - Double.valueOf(i)/10000;
				Double exitMultiplier = 1 - Double.valueOf(j)/1000;
				returnMultiplierProfitLoss(ticker, entryMultiplier, exitMultiplier);
				totalLoss = 0.00; totalProfit = 0.00;
			}
		}
//		returnMultiplierProfitLoss(ticker, .999, .95);
		System.out.println("Max Profit: " + maxProfit);
	} 

	private List<String> readFileAsList() {
		String fileName = "/test/US1.ZI_210419_210423.txt";

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
			if(currentValue.compareTo(startingValue*multiplier) < 0 || currentValue.compareTo(startingValue*multiplier) == 0){
				return i;
			}
		}
		return -1;
	}
	private static Double returnMultiplierProfitLoss(List<String> ticker, Double entryMultiplier, Double exitMultiplier) {
		int tickerLength = ticker.size();
//		Double entryPoint, exitPoint;
		List<Integer> repeatEntry = new ArrayList<Integer>();
		List<Integer> exitPositions = new ArrayList<Integer>();
		int releaseQuantity = 0;
		int entryAt = 1, exitAt = 1;
		int upTrendAt = 1;
		int maxCount = 0;
//		Double minima = 0.00;
//		List<Double> allfactors = new ArrayList<Double>();
//		Double currentFactor = 0.00;
		int totalCount = 0;
		
		for(int i = 1; i < tickerLength; i++) {
			entryAt = returnLowerMultiplierPosition(ticker, i, entryMultiplier);
			if (entryAt < 0) {
				//System.out.println("Multiplier:" + entryMultiplier + " doesnot exist for position: " + i + " with value: " + ticker.get(i) + "\n");
				continue;
			}
			if (Double.valueOf(ticker.get(i)).compareTo(stableLowPrice) > 0 || Double.valueOf(ticker.get(entryAt)).compareTo(stableLowPrice) > 0) {
				continue;
			}
			upTrendAt = returnMultiplierPosition(ticker, i, upTrendMultiplier);
			if(upTrendAt < entryAt && upTrendAt != -1) {
				i = upTrendAt;
				continue;
			}
			else {
				for (int j = i+1; j <= entryAt; j++) {
					for(int exitPosition : exitPositions) {
						if ( exitPosition == j) 
							releaseQuantity++;
					}
				}
				totalCount = totalCount - releaseQuantity;
//				System.out.println(i + "\t" + entryAt + "\t" + totalCount + "\t" + releaseQuantity);
				releaseQuantity = 0;
				i = entryAt;
				if(totalCount >= maxShares || Double.valueOf(ticker.get(i)).compareTo(stableLowPrice) > 0) {
					continue;
				}
			}
			if(repeatEntry.contains(entryAt)) continue;
			repeatEntry.add(entryAt);
			exitAt = returnLowerMultiplierPosition(ticker, entryAt, exitMultiplier);
			if (exitAt < 0) {
				exitAt = tickerLength - 1;
				totalLoss = totalLoss + Double.valueOf(ticker.get(exitAt)) - Double.valueOf(ticker.get(entryAt)) ;
			}
			else {
				totalProfit = totalProfit + Double.valueOf(ticker.get(entryAt))*(1 - exitMultiplier);
				exitPositions.add(exitAt);
			}
			totalCount++;
			if (totalCount > maxCount) maxCount = totalCount;
//			System.out.println(entryAt + "\t" + ticker.get(entryAt) + "\t" + exitAt + "\t" + ticker.get(exitAt) + "\t" + totalLoss + "\t" + totalProfit + "\t" + totalCount);
		}

		Double netProfit = totalProfit - totalLoss;
		//Double netProfit = profitCount*0.001 - lossCount*0.004;
		Double profitPerShare = netProfit/maxCount;
		if (profitPerShare.compareTo(maxProfit) > 0) maxProfit = profitPerShare;
		
		System.out.println(entryMultiplier + "\t" + exitMultiplier + "\t" +  "NetProfit:" + "\t" + netProfit + "\tTotal Count: " + totalCount + "\tMax Count: " + maxCount + "\tProfitPerShare: " + profitPerShare);
		return 0.00;
	}
}