package Strategies;
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
public class Extract_Calibrating_Factors_Scenario4 {
	static Double profitMultiplier = 1.002;
	static boolean profitMet = false;
	public static void main(String[] args) {
		Extract_Calibrating_Factors_Scenario4 demo = new Extract_Calibrating_Factors_Scenario4();
		List<String> ticker = demo.readFileAsList();
		
		//Not to have entryAt more than 10 as the number of occurrence will reduce, 
		//we can assume that a stock price wont be changed by 0.5 times and hence exitAt count is 500
		for (int i = 0 ; i < 5; i++)
		{
			for(int j = 1; j < 40; j=j+1) {
				Double entryMultiplier = 1 - Double.valueOf(i)/1000;
				Double exitMultiplier = 1 + Double.valueOf(j)/1000;
				returnMultiplierProfitLoss(ticker, entryMultiplier, exitMultiplier);
			}
		}
//		returnMultiplierProfitLoss(ticker, 1.0, 1.001);
	} 

	private List<String> readFileAsList() {
		String fileName = "/SNAP.txt";

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
		Double entryPoint, exitPoint;
		List<Integer> repeatEntry = new ArrayList<Integer>();
		int entryAt = 1, exitAt = 1;
		Double minima = 0.00;
		List<Double> allfactors = new ArrayList<Double>();
		Double currentFactor = 0.00;
		int totalCount = 0;
		
		for(int i = 1; i < tickerLength; i++) {
			entryAt = returnLowerMultiplierPosition(ticker, i, entryMultiplier);
			if (entryAt < 0) {
				//System.out.println("Multiplier:" + entryMultiplier + " doesnot exist for position: " + i + " with value: " + ticker.get(i) + "\n");
				continue;
			}
			if(repeatEntry.contains(entryAt)) continue;
			repeatEntry.add(entryAt);
			exitAt = returnMultiplierPosition(ticker, entryAt, exitMultiplier);
			if (exitAt < 0)exitAt = tickerLength - 1;
			minima = Double.valueOf(ticker.get(entryAt));
			for (int k = entryAt; k < exitAt; k++)
			{
				Double currentvalue = Double.valueOf(ticker.get(k))*profitMultiplier;
				if ((currentvalue.compareTo(minima)) < 0) {
					minima = Double.valueOf(ticker.get(k));
					i=k;
					profitMet = true;
					break;
				} 		
			}
			if(!profitMet) {
				i=exitAt;
			}
			profitMet = false;
			currentFactor = Double.valueOf(ticker.get(entryAt))/minima;
			allfactors.add(currentFactor);
			totalCount++;
//			System.out.println(totalCount + "\t" + i + "\t" + ticker.get(i) + "\t" + entryAt + "\t" + ticker.get(entryAt) + "\t" + exitAt + "\t" + ticker.get(exitAt) + "\t" + minima + "\t" + currentFactor);
//			if (currentFactor < profitMultiplier && exitAt == tickerLength - 1 )
//				break;
				//System.out.println(totalCount + "\t" + i + "\t" + ticker.get(i) + "\t" + entryAt + "\t" + ticker.get(entryAt) + "\t" + exitAt + "\t" + ticker.get(exitAt) + "\t" + minima + "\t" + currentFactor);
		}
		int profitCount = 0, lossCount = 0;
		Double minfactor = allfactors.get(0);
		allfactors.remove(allfactors.size()-1);
		for(Double factor:allfactors) {
			if (factor.floatValue() != 1) {
				profitCount++;
			}
			else {
//				System.out.println(lossCount + "\t" + factor + "\n");
				lossCount++;
			}
		}
		Double netProfit = profitCount*(profitMultiplier - 1) - lossCount*(exitMultiplier - 1);
		//Double netProfit = profitCount*0.001 - lossCount*0.004;
		System.out.println(entryMultiplier + "\t" + exitMultiplier + "\t" + totalCount + "\t" +  "NetProfit:" + "\t" + netProfit);
		return 0.00;
	}
}