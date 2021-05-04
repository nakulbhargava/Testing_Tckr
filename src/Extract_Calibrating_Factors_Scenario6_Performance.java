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
 * Below program deals with scenario6
 */
public class Extract_Calibrating_Factors_Scenario6_Performance {
//	static Double profitMultiplier = 1.001;
//	static boolean profitMet = false;
	static Double totalLoss = 0.00, totalProfit = 0.00;
	static Double upTrendMultiplier = 1.0005;
	static int maxShares = 10;
	static Double maxProfit = 0.0;
	static Double stableLowPrice = 0.0;
	static List<String> ticker = null;
	static Double entryMultiplier = 0.9995;
	static Double exitMultiplier = .997;
	static int releaseQuantity = 0, totalCount = 0;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		

	static File fileTrue = new File("C:\\Users\\nakul\\Documents\\US1.SNAP_210405_210409_" + stableLowPrice + " - " + localTime + ".txt");
	static PrintWriter pw;
	static FileOutputStream fo;
	
	public static void main(String[] args) throws FileNotFoundException {
		pw = new PrintWriter(new FileOutputStream(fileTrue));
		fo = new FileOutputStream(fileTrue);
		Extract_Calibrating_Factors_Scenario6_Performance demo = new Extract_Calibrating_Factors_Scenario6_Performance();
		ticker = demo.readFileAsList();
		
		//Not to have entryAt more than 10 as the number of occurrence will reduce, 
		//we can assume that a stock price wont be changed by 1.5 times and hence exitAt count is 500
		for (int i = 5 ; i < 40; i=i+5)
		{
			for(int j = 1; j < 100; j=j+2) {
				entryMultiplier = 1 - Double.valueOf(i)/10000;
				exitMultiplier = 1 - Double.valueOf(j)/1000;
				returnMultiplierProfitLoss();
				totalLoss = 0.00; totalProfit = 0.00; releaseQuantity = 0; totalCount = 0;
			}
		}
//		returnMultiplierProfitLoss();
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

	private static Positions returnMultiplierPosition (int position) {
		int ticksLength = ticker.size();
		Extract_Calibrating_Factors_Scenario6_Performance extract = new Extract_Calibrating_Factors_Scenario6_Performance();
		Positions positions = extract.new Positions();
		Boolean entryPositionMet = false;
		Boolean exitPositionMet = false;
		Boolean uptrendPositionMet = false;
		Double startingValue = Double.valueOf(ticker.get(position));
		Double statingValueForExit = null;
		Double currentValue;
		for (int i = position; i < ticksLength; i++) {
			currentValue = Double.valueOf(ticker.get(i));
			if(currentValue.compareTo(startingValue*entryMultiplier) <= 0 && !entryPositionMet){
				positions.setEntryPosition(i);
				statingValueForExit = currentValue;
				entryPositionMet = true;
				continue;
			}
			if(statingValueForExit != null && currentValue.compareTo(statingValueForExit*exitMultiplier) <= 0 && entryPositionMet && !exitPositionMet){
				positions.setExitPosition(i);
				exitPositionMet = true;
			}
			if(currentValue.compareTo(startingValue*upTrendMultiplier) >= 0 && !uptrendPositionMet){
				positions.setUptrendPosition(i);
				uptrendPositionMet = true;
			}
			if (entryPositionMet && exitPositionMet && uptrendPositionMet) break;
		}
		if(!entryPositionMet) positions.setEntryPosition(-1);
		if(!exitPositionMet) positions.setExitPosition(-1);
		if(!uptrendPositionMet) positions.setUptrendPosition(-1);
		return positions;
		
//		Double startingValue = Double.valueOf(ticker.get(position));
//		Double currentValue;
//		for (int i = position; i < ticksLength; i++) {
//			currentValue = Double.valueOf(ticks.get(i));
//			if(currentValue.compareTo(startingValue*multiplier) > 0 || currentValue.compareTo(startingValue*multiplier) == 0){
//				return i;
//			}
//		}
//		return -1;
	}
	
	private static void returnMultiplierProfitLoss() {
		int tickerLength = ticker.size();

		List<Integer> repeatEntry = new ArrayList<Integer>();
		List<Double> exitPositions = new ArrayList<Double>();
		int entryAt = 1, exitAt = 1;
		int upTrendAt = 1;
		int maxCount = 0;
		Boolean firstLossReceived = false;
		
		for(int i = 1; i < tickerLength; i++) {
			Positions position = returnMultiplierPosition(i);
			entryAt = position.getEntryPosition();
			exitAt = position.getExitPosition();
			upTrendAt = position.getUptrendPosition();
			if (entryAt < 0) {
				//System.out.println("Multiplier:" + entryMultiplier + " doesnot exist for position: " + i + " with value: " + ticker.get(i) + "\n");
				continue;
			}

			if(upTrendAt < entryAt && upTrendAt != -1) {
				exitPositions = calculateTotalCount(i, upTrendAt, exitPositions);
				i = upTrendAt;
				continue;
			}
			else {
				exitPositions = calculateTotalCount(i, entryAt, exitPositions);
				i = entryAt;
				if(totalCount >= maxShares || Double.valueOf(ticker.get(i)).compareTo(stableLowPrice) < 0) {
					continue;
				}
			}
			if(repeatEntry.contains(entryAt)) continue;
			repeatEntry.add(entryAt);
			
			if (exitAt < 0) {
				exitAt = tickerLength - 1;
				if(!firstLossReceived) {
					stableLowPrice = Double.valueOf(ticker.get(entryAt));
					firstLossReceived = true;
				}
				totalLoss = totalLoss + Double.valueOf(ticker.get(exitAt)) - Double.valueOf(ticker.get(entryAt)) ;
			}
			else {
				totalProfit = totalProfit + Double.valueOf(ticker.get(entryAt))*(1 - exitMultiplier);
				exitPositions.add(Double.valueOf(exitAt));
			}
			totalCount++;
			if (totalCount > maxCount) maxCount = totalCount;
//			System.out.println(entryAt + "\t" + ticker.get(entryAt) + "\t" + exitAt + "\t" + ticker.get(exitAt) + "\t" + totalLoss + "\t" + totalProfit + "\t" + totalCount);
		}

		Double netProfit = totalProfit - totalLoss;
		//Double netProfit = profitCount*0.001 - lossCount*0.004;
		Double profitPerShare = netProfit/maxCount;
		if (profitPerShare.compareTo(maxProfit) > 0) maxProfit = profitPerShare;
		
		System.out.println(entryMultiplier + "\t" + exitMultiplier + "\t" +  "NetProfit:" + "\t" + netProfit + "\tTotal Count: " + totalCount + "\tMax Count: " + maxCount + "\tProfitPerShare: " + profitPerShare + "\tProfitStopper: " + stableLowPrice);
		pw.write(entryMultiplier + "\t" + exitMultiplier + "\t" +  "NetProfit:" + "\t" + netProfit + "\tTotal Count: " + totalCount + "\tMax Count: " + maxCount + "\tProfitPerShare: " + profitPerShare + "\tProfitStopper: " + stableLowPrice + "\n");
	}
	public static List<Double> calculateTotalCount(int initialPosition, int finalPosition, List<Double> exitPositions) {
		for (int j = initialPosition; j <= finalPosition; j++) {
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
//		System.out.println(initialPosition + "\t" + ticker.get(initialPosition) + "\tEntryAt: " + finalPosition + "\t" + ticker.get(finalPosition) + "\t" + totalCount + "\t" + releaseQuantity);
		//				pw.write(i + "\t" + ticker.get(i) + "\t" + entryAt + "\t" + ticker.get(entryAt) + "\t" + totalCount + "\t" + releaseQuantity + "\n");
		releaseQuantity = 0;
		return exitPositions;
	}
	
	public class Positions{
		int entryPosition;
		int uptrendPosition;
		public int getUptrendPosition() {
			return uptrendPosition;
		}
		public void setUptrendPosition(int downtrendPosition) {
			this.uptrendPosition = downtrendPosition;
		}
		int exitPosition;
		public int getEntryPosition() {
			return entryPosition;
		}
		public void setEntryPosition(int entryPosition) {
			this.entryPosition = entryPosition;
		}
		public int getExitPosition() {
			return exitPosition;
		}
		public void setExitPosition(int exitPosition) {
			this.exitPosition = exitPosition;
		}
	}
}