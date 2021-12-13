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
 * Fixed Down 5% and up 20% factors
 */
public class Extract_Calibrating_Factors_Long_Term {
	//	static Double profitMultiplier = 1.001;
	//	static boolean profitMet = false;
	static Double totalLoss = 0.00, totalProfit = 0.00;
	static Double upTrendMultiplier = 1.01;
	static int maxShares = 100000;
	static Double maxProfit = 0.0;
	static Double stableHighPrice = 2000.00;
	static List<String> ticker = null;
	static Double entryMultiplier = .95;
	static Double exitMultiplier = 1.1;
	static int releaseQuantity = 0, totalCount = 0, totalReleaseQuantity = 0;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		


	static PrintWriter pw;
	static FileOutputStream fo;
	public static void main(String[] args) throws FileNotFoundException {
		File sourceFolder = new File("C:\\Users\\nakul\\Documents\\Historical Data");
		for(File file : sourceFolder.listFiles()) {
			File fileTrue = new File("C:\\Users\\nakul\\Documents\\TickerFactors_LongTerm\\" + file.getName() + " - " + localTime + ".txt");
			pw = new PrintWriter(new FileOutputStream(fileTrue, true));
			fo = new FileOutputStream(fileTrue, true);
			Extract_Calibrating_Factors_Long_Term demo = new Extract_Calibrating_Factors_Long_Term();
			ticker = demo.readFileAsList(file.getName());
			//		System.out.println(exitMultiplier.intValue());
			//Not to have entryAt more than 10 as the number of occurrence will reduce, 
			//we can assume that a stock price wont be changed by 1.5 times and hence exitAt count is 500
			exitMultiplier = 1.1;
			maxProfit = 0.0;
			for(int k =1 ; k < 7; k++) {
				for(int i = 0; i < 6; i++) {
					returnMultiplierProfitLoss();
					totalLoss = 0.00; totalProfit = 0.00; releaseQuantity = 0; totalCount = 0; totalReleaseQuantity = 0; stableHighPrice = 2000.00;
					
					upTrendMultiplier = upTrendMultiplier + 0.01;
				}
				upTrendMultiplier = 1.01;
				exitMultiplier = exitMultiplier + 0.02;
				
			}
			//		returnMultiplierProfitLoss();
			System.out.println("Start Price: " + ticker.get(1) + "\tLast Price:" + ticker.get(ticker.size()-1));
			pw.write("Start Price: " + ticker.get(1) + "\tLast Price:" + ticker.get(ticker.size()-1) + "\n");

			System.out.println("Max Profit: " + maxProfit);
			pw.write("Max Profit: " + maxProfit);
			pw.close();
		}
	} 

	private List<String> readFileAsList(String file) {
		String fileName = "/test/" + file;

		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(uri),
					Charset.defaultCharset());
			List<String> ticker = new ArrayList<String>();
			String priceToCompare = "0.0";
			for (String line : lines) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				if(!priceToCompare.equals(splitLine.get(3))) {
					ticker.add(splitLine.get(3));
					priceToCompare = splitLine.get(3);
				}
				//								ticker.add(splitLine.get(3));
			}
			return ticker;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Positions returnMultiplierPositions (int position) {
		int ticksLength = ticker.size();
		Extract_Calibrating_Factors_Long_Term extract = new Extract_Calibrating_Factors_Long_Term();
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
			if(statingValueForExit != null && currentValue.compareTo(statingValueForExit*exitMultiplier) >= 0 && entryPositionMet && !exitPositionMet){
				positions.setExitPosition(i);
				exitPositionMet = true;
			}
			if(currentValue.compareTo(startingValue*upTrendMultiplier) >= 0 && !uptrendPositionMet){
				positions.setuptrendPosition(i);
				uptrendPositionMet = true;
			}
			if(uptrendPositionMet && !entryPositionMet) break;
			if (entryPositionMet && exitPositionMet && uptrendPositionMet) break;
		}
		if(!entryPositionMet) positions.setEntryPosition(-1);
		if(!exitPositionMet) positions.setExitPosition(-1);
		if(!uptrendPositionMet) positions.setuptrendPosition(-1);
		return positions;
	}

	private static void returnMultiplierProfitLoss() {
		int tickerLength = ticker.size();
		List<Integer> repeatEntry = new ArrayList<Integer>();
		List<Double> exitPositions = new ArrayList<Double>();
		Boolean firstLossReceived = false;

		int entryAt = 1, exitAt = 1;
		int uptrendAt = 1;
		int maxCount = 0;
		int evalPos = 0;

		for(int i = 1; i < tickerLength-1; i++) {
			Positions position = returnMultiplierPositions(i);
			entryAt = position.getEntryPosition();
			exitAt = position.getExitPosition();
			uptrendAt = position.getuptrendPosition();
			evalPos = i;

			if((uptrendAt < entryAt || (uptrendAt > 0 && entryAt < 0)) && uptrendAt != -1) {
				exitPositions = calculateTotalCount(i, uptrendAt, exitPositions);
				//				System.out.println(i + "\t" + uptrendAt);
				//				System.out.println("UpTrend Met First for position:" + i + "\twith value: " + ticker.get(i) + "\tat: " + uptrendAt + "\twith value: "+ ticker.get(uptrendAt));
				//				pw.write("UpTrend Met First for position:" + i + "\twith value: " + ticker.get(i) + "\tat: " + uptrendAt + "\twith value: "+ ticker.get(uptrendAt) + "\n");

				i = uptrendAt-1;
				continue;
			}
			if (entryAt < 0) {
				//				System.out.println("Multiplier:" + entryMultiplier + " doesnot exist for position: " + i + " with value: " + ticker.get(i));
				//				pw.write("Multiplier:" + entryMultiplier + " doesnot exist for position: " + i + " with value: " + ticker.get(i) + "\n");
				continue;
			}
			else {
				exitPositions = calculateTotalCount(i, entryAt, exitPositions);
				i = entryAt-1;
				if(totalCount >= maxShares || Double.valueOf(ticker.get(i)).compareTo(stableHighPrice) > 0) {
					continue;
				}
			}

			if(repeatEntry.contains(entryAt)) continue;
			repeatEntry.add(entryAt);

			if (exitAt < 0) {
				exitAt = tickerLength - 1;
				if(!firstLossReceived) {
					stableHighPrice = Double.valueOf(ticker.get(entryAt));
					firstLossReceived = true;
				}
				totalLoss = totalLoss + Double.valueOf(ticker.get(exitAt)) - Double.valueOf(ticker.get(entryAt)) ;

			}
			else {
				totalProfit = totalProfit + Double.valueOf(ticker.get(entryAt))*(exitMultiplier - 1);
				exitPositions.add(Double.valueOf(exitAt));
			}
			totalCount++;
			if (totalCount > maxCount) maxCount = totalCount;
			//			System.out.println(ticker.get(entryAt) + "\t" + ticker.get(exitAt));
			//			System.out.println("Initial Pos: " + evalPos + "\t" + ticker.get(evalPos) + "\tEntryAt: " + entryAt + "\t" + ticker.get(entryAt) + "\tExitAt: " + exitAt + "\t" + ticker.get(exitAt) + "\t" + totalLoss + "\t" + totalProfit + "\t" + totalCount);
			//			pw.write("Initial Pos: " + evalPos + "\t" + ticker.get(evalPos) + "\tEntryAt: " + entryAt + "\t" + ticker.get(entryAt) + "\t" + exitAt + "\t" + ticker.get(exitAt) + "\t" + totalLoss + "\t" + totalProfit + "\t" + totalCount + "\n");
		}

		Double netProfit = totalProfit + totalLoss;

		Double profitPerShare = netProfit/maxCount;
		if (profitPerShare.compareTo(maxProfit) > 0) maxProfit = profitPerShare;

		System.out.println(upTrendMultiplier + "\t" + entryMultiplier + "\t" + exitMultiplier + "\t" +  "NetProfit: " + netProfit + "\tTotal Count: " + totalCount + "\tMax Count: " + maxCount + "\tReleased Shares: " + totalReleaseQuantity + "\tProfitPerShare: " + profitPerShare + "\tProfitStopper: " + stableHighPrice);
		pw.write(upTrendMultiplier + "\t" + entryMultiplier + "\t" + exitMultiplier + "\t" +  "NetProfit: " + netProfit + "\tTotal Count: " + totalCount + "\tMax Count: " + maxCount + "\tReleased Shares: " + totalReleaseQuantity + "\tProfitPerShare: " + profitPerShare + "\tProfitStopper: " + stableHighPrice + "\n");
	}
	public static List<Double> calculateTotalCount(int initialPosition, int finalPosition, List<Double> exitPositions) {
		for (int j = initialPosition; j < finalPosition; j++) {
			int jsQuantity = 0;
			for(Double exitPosition : exitPositions) {
				if ( exitPosition.intValue() == j) {
					jsQuantity++;
					releaseQuantity++;
					totalReleaseQuantity++;
				}	
			}
			for(int k=0; k < jsQuantity; k++) {
				exitPositions.remove(Double.valueOf(j));
			}
		}
		totalCount = totalCount - releaseQuantity;
		if(releaseQuantity != 0) {
			//			System.out.println("Initial Pos: " + initialPosition + "\t" + ticker.get(initialPosition) + "\tEntryAt: " + finalPosition + "\t" + ticker.get(finalPosition) + "\t" + totalCount + "\t" + releaseQuantity);
			//			pw.write("Initial Pos: " + initialPosition + "\t" + ticker.get(initialPosition) + "\tEntryAt: " + finalPosition + "\t" + ticker.get(finalPosition) + "\t" + totalCount + "\t" + releaseQuantity + "\n");
		}
		releaseQuantity = 0;
		return exitPositions;
	}

	public class Positions{
		int entryPosition;
		int uptrendPosition;
		int exitPosition;

		public int getuptrendPosition() {
			return uptrendPosition;
		}
		public void setuptrendPosition(int uptrendPosition) {
			this.uptrendPosition = uptrendPosition;
		}

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

