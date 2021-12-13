import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Testing_General {
	public static void main(String[] args) {

		List<Double> ticker = new ArrayList<Double>()	;
	ticker.add(1.0);ticker.add(1.2); ticker.add(2.0); ticker.add(1.2); ticker.add(1.20);
	for(Double tick:ticker) {
		System.out.println(tick + "\n");
	}
	ticker.remove(1.2);
	System.out.println("UpdatedTicker" + "\n");
	for(Double tick:ticker) {
		System.out.println(tick + "\n");
	}
		
	} 

	private List<String> readFileAsList() {
		String fileName = "/ZG.txt";

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
			if(currentValue.compareTo(startingValue*multiplier) > 0){
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
			entryAt = returnMultiplierPosition(ticker, i, entryMultiplier);
			if (entryAt < 0 || repeatEntry.contains(entryAt)) continue;
			repeatEntry.add(entryAt);
			exitAt = returnMultiplierPosition(ticker, entryAt, exitMultiplier);
			if (exitAt < 0) exitAt = tickerLength -1;
			i = exitAt;
			minima = Double.valueOf(ticker.get(entryAt));
			for (int k = entryAt; k < exitAt; k++)
			{
				Double currentvalue = Double.valueOf(ticker.get(k));
				if (currentvalue.compareTo(minima) < 0) {
					minima = currentvalue;
				} 		
			}
			currentFactor = Double.valueOf(ticker.get(entryAt))/minima;
			allfactors.add(currentFactor);
			totalCount++;
			System.out.println(totalCount + "\t" + entryAt + "\t" + ticker.get(entryAt) + "\t" + exitAt + "\t" + ticker.get(exitAt) + "\t" + minima + "\t" + currentFactor);
		}
		int profitCount = 0, lossCount = 0;
		Double minfactor = allfactors.get(0);
		for(Double factor:allfactors) {
			if (factor.floatValue() >= 1.001) 
				profitCount++;
			else lossCount++;
		}
		Double netProfit = profitCount*.001 - lossCount*(1-exitMultiplier);
		System.out.println(entryMultiplier + "\t" + exitMultiplier + "\t" + totalCount + "\t" +  "NetProfit:" + "\t" + netProfit);
		return 0.00;
	}
}