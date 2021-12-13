package Backtesting;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BackTestingHighVolatilitySetFactors {
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		
	static List<String> ticker = null;
	static String filename = "DayData - PETZ_2018-08-01_2021-11-05";
	static File fileTrue = new File("C:\\Users\\nakul\\Documents\\" + filename + " - " + localTime + ".txt");
	static PrintWriter pw;
	static FileOutputStream fo;
	static List<Double> tickerOpen = new ArrayList<Double>();
	static List<Double> tickerClose = new ArrayList<Double>();
	static List<Double> tickerLow = new ArrayList<Double>();
	static List<Double> tickerHigh = new ArrayList<Double>();
	static List<String> tickerDate = new ArrayList<String>();
	static Double[] tickerMovingAverages = null;
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	static Date marketStartDate, marketEndDate;
	static int movingAverageDays = 45;
	static Double maxAmount = 500.00;
	static Double oneTimeInvestment = 10.00;
	static Double profitPercent = 1.13;
	static Double lossPercent = 0.8;
	static Double maxProfit = 0.0;
	static Double maxRoundProfit = 0.0;

	public static void main(String[] args) throws FileNotFoundException, ParseException {
		//		pw = new PrintWriter(new FileOutputStream(fileTrue, true));
		//		fo = new FileOutputStream(fileTrue, true);
		BackTestingHighVolatilitySetFactors demo = new BackTestingHighVolatilitySetFactors();
		demo.readFileAsList();
		tickerMovingAverages = new Double[tickerLow.size()];
		demo.calculateLowMovingAverage();
		demo.investInVolatility();
		//		pw.close();
	} 

	private void investInVolatility() {
		Double totalInvestment = 0.0;
		Double maxInvestment = 0.0;
		Double averageInvestedPrice = 0.0;
		Double profitPrice = 0.0;
		Double netProfit = 0.0;
		int numberOfInvestments = 0;
		boolean alreadyInvested = false;
		Double totalShares = 0.0;
		for(int i = movingAverageDays; i< tickerOpen.size(); i++) {
			if(tickerLow.get(i).compareTo(tickerMovingAverages[i]) < 0 && !alreadyInvested) {
				//TimeToBuy;
				//				Double temp = oneTimeInvestment/tickerMovingAverages[i];
				//				int qty = temp.intValue();
				alreadyInvested = true;
				System.out.println("First Time Buying: " + tickerDate.get(i) + " High: " + tickerHigh.get(i) + " Low: " + tickerLow.get(i) + " Open: " + tickerOpen.get(i) + " Close: " + tickerClose.get(i) + " Buying Price: " + tickerMovingAverages[i]);
				totalInvestment = oneTimeInvestment;
				averageInvestedPrice = tickerMovingAverages[i];
				profitPrice = profitPercent*tickerMovingAverages[i];
				totalShares = oneTimeInvestment/tickerMovingAverages[i];
				numberOfInvestments = 1;
			}
			while(alreadyInvested && tickerLow.get(i).compareTo(lossPercent*averageInvestedPrice) < 0
					&& totalInvestment.compareTo(maxAmount) < 0) {
				Double sharesInvestedNow = oneTimeInvestment/(lossPercent*averageInvestedPrice);
				totalShares = totalShares + sharesInvestedNow;
				numberOfInvestments++;
				totalInvestment = totalInvestment + oneTimeInvestment;
				System.out.println("TickerLow: " + tickerLow.get(i) + "AverageInvestedPrice: " + averageInvestedPrice);
				System.out.println("Bought again due to loss: " + tickerDate.get(i) + " High: " + tickerHigh.get(i) + " Low: " + tickerLow.get(i) + " Open: " + tickerOpen.get(i) + " Close: " + tickerClose.get(i) + " Buying Price: " + lossPercent*averageInvestedPrice);
				averageInvestedPrice = ((numberOfInvestments - 1)*averageInvestedPrice + (lossPercent*averageInvestedPrice))/numberOfInvestments;
			}

			if(alreadyInvested && tickerHigh.get(i).compareTo(profitPrice) > 0) {
				Double profitThisRound = 0.0;
				profitThisRound = totalShares * (profitPrice - averageInvestedPrice);
				netProfit = netProfit + profitThisRound;
				if(maxInvestment.compareTo(totalInvestment) < 0 ) maxInvestment = totalInvestment;
				System.out.println("Selling at Profit: "  + tickerDate.get(i) + " High: " + tickerHigh.get(i) + " Low: " + tickerLow.get(i) + " Open: " + tickerOpen.get(i) + " Close: " + tickerClose.get(i) + " Profit Price: " + profitPrice);
				System.out.println("Profit This Round: " + profitThisRound + " Net Profit So far: " + netProfit );
				System.out.println("Investment This Round: " + totalInvestment + " Sold Price: " + profitPrice + " Average Invested Price: " + averageInvestedPrice + " Total Shares: " + totalShares);
				totalInvestment = 0.0;
				numberOfInvestments = 0;
				alreadyInvested = false;
				profitPrice = 0.0;
				averageInvestedPrice = 0.0;
				totalShares = 0.0;
			}
		}
		if(maxProfit.compareTo(netProfit) < 0) maxProfit = netProfit;
		String loss = lossPercent.toString().length() < 5 ? lossPercent.toString() : lossPercent.toString().substring(0, 5);
		String profit = netProfit.toString().length() < 8 ? netProfit.toString() : netProfit.toString().substring(0, 8);
		
		System.out.println("Loss Percent: " + loss + " Net Profit: " + profit + " Max Investment: " + maxInvestment + " OneTimeInvestment: " + oneTimeInvestment);
		//netProfit = 0.0;
		System.out.println("Max Profit: " + maxProfit + " Profit Factor: " + profitPercent + "\n");
	}

	private void calculateLowMovingAverage() {
		for(int i = 0 ; i< movingAverageDays; i++) {
			tickerMovingAverages[i] = 0.0;
		}
		for(int i = movingAverageDays; i < tickerOpen.size() ; i++) {
			Double tempSum = 0.0;
			for(int j = i - movingAverageDays; j < i ; j++) {
				tempSum = tempSum + tickerLow.get(j);
			}
			tickerMovingAverages[i] = tempSum/movingAverageDays;
		}
	}

	private void readFileAsList() {
		String fileName = "/test/" + filename + ".txt";

		boolean firstLine = false;
		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(uri),
					Charset.defaultCharset());
			for (String line : lines) {
				if(!firstLine) {
					firstLine = true;
					continue;
				}

				List<String> splitLine = Arrays.asList(line.split(",", -1));
				Date date = format.parse(splitLine.get(1));
				tickerDate.add(splitLine.get(1));
				tickerOpen.add(Double.valueOf(splitLine.get(2)));
				tickerClose.add(Double.valueOf(splitLine.get(3)));
				tickerHigh.add(Double.valueOf(splitLine.get(4)));
				tickerLow.add(Double.valueOf(splitLine.get(5)));
				//					tickerVolume.add(Double.valueOf(splitLine.get(6)));
				//					tickerVwap.add(Double.valueOf(splitLine.get(8)));
			}
			//								ticker.add(splitLine.get(3));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
