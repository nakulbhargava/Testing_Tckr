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

public class MovingAverageCrossoverContinous {
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		
	static List<String> ticker = null;
	static String filename = "DayData - AES_2018-08-01_2021-11-05";
	static File fileTrue = new File("C:\\Users\\nakul\\Documents\\" + filename + " - " + localTime + ".txt");
	static PrintWriter pw;
	static FileOutputStream fo;
	static List<Double> tickerOpen = new ArrayList<Double>();
	static List<Double> tickerClose = new ArrayList<Double>();
	static List<Double> tickerLow = new ArrayList<Double>();
	static List<Double> tickerHigh = new ArrayList<Double>();
	static List<String> tickerDate = new ArrayList<String>();
	static List<Double> allBuys = new ArrayList<Double>();
	//static Double[] tickerMovingAverages = null;
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	static Date marketStartDate, marketEndDate;
	//static int movingAverageDays = 45;
	static Double maxAmount = 500.00;
	static Double oneTimeInvestment = 50.0;

	public static void main(String[] args) throws FileNotFoundException, ParseException {
		//		pw = new PrintWriter(new FileOutputStream(fileTrue, true));
		//		fo = new FileOutputStream(fileTrue, true);
		MovingAverageCrossoverContinous demo = new MovingAverageCrossoverContinous();
		demo.readFileAsList();
		demo.investInCrossover();
		//		pw.close();
	} 

	private Double[] calculateLowMovingAverage(int movingAverageDays) {
		Double[] tickerMovingAverages = new Double[tickerLow.size()];
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
		return tickerMovingAverages;
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

	private void investInCrossover() {
		Double totalBuy = 0.0;
		Double totalSell = 0.0;
		Double netProfit = 0.0;
		//Double totalInvestment = 0.0;
		boolean alreadyInvested = false;

		Double endOfRoundDifference = 0.0;

		for(int x = 10; x < 51; x= x+10) {
			for(int y = 50; y < 401; y = y + 50) {
				Double[] movingAverageSmall = this.calculateLowMovingAverage(x);
				Double[] movingAverageBig = this.calculateLowMovingAverage(y);
				int maxShares = 0;
				Double maxInvestment = 0.0;
				System.out.println("Calculating for Moving Averages: " + x + " & " + y);
				for(int i = y; i< tickerOpen.size(); i++) {
					if(movingAverageSmall[i].compareTo(movingAverageBig[i]) > 0 && maxInvestment.compareTo(maxAmount) < 0) {
						//numberOfInvestments++;
						alreadyInvested = true;
						Double numberOfShares = oneTimeInvestment/tickerLow.get(i);
						for(int k = 0; k < numberOfShares.intValue() ; k++)
							allBuys.add(tickerLow.get(i));
						//numberOfBuy++;
						System.out.println("Buying Price: " + tickerLow.get(i) + " Date: " + tickerDate.get(i) + " Total Shares: " + allBuys.size());
						//averageInvestedPrice = totalInvestment/numberOfInvestments;
						//						if(maxInvestment.compareTo(totalInvestment) < 0) {
						//							maxInvestment = totalInvestment;
						//						}
						if(maxShares < allBuys.size()) maxShares = allBuys.size();
						for ( Double buy : allBuys) {
							totalBuy = totalBuy + buy;
						}
						if(totalBuy.compareTo(maxInvestment) > 0) maxInvestment = totalBuy;
						totalBuy = 0.0;
					}
					if(movingAverageSmall[i].compareTo(movingAverageBig[i]) < 0 && alreadyInvested) {
						List<Double> tempSell = new ArrayList<Double>();
						totalBuy = 0.0;
						int thisRoundSell = 0;
						for ( Double buy : allBuys) {
							if(buy.compareTo(tickerLow.get(i)) < 0) {
								tempSell.add(buy);
								thisRoundSell++;
								totalBuy = totalBuy + buy;
								maxInvestment = maxInvestment - buy;
							}
						}
						if(tempSell.isEmpty()) continue;
						allBuys.removeAll(tempSell);
						if(allBuys.size() == 0) {
							alreadyInvested = false;
						}
						totalSell = thisRoundSell*tickerLow.get(i);

						netProfit = netProfit + (totalSell - totalBuy);
						System.out.println("Selling These Shares: " + tempSell + "\tTotal Sell Shares: " + tempSell.size());
						System.out.println("Remaining Shares: " + allBuys + "\tTotal remaining Shares: " + allBuys.size());
						System.out.println("Selling All shares At Price: " + tickerLow.get(i)  + " Date: " + tickerDate.get(i) + " Profit: " + (totalSell - totalBuy));
						totalBuy = 0.0;
						totalSell = 0.0;
					}
				}
				for ( Double buy : allBuys) {
					totalBuy = totalBuy + buy;
				}
				endOfRoundDifference = allBuys.size() * tickerLow.get(tickerLow.size() - 1) - totalBuy;
				netProfit = netProfit + endOfRoundDifference;
				System.out.println("EndOfRoundDifference: " + endOfRoundDifference + "Profit Before Difference: " + (netProfit - endOfRoundDifference) + " Net Profit: " + netProfit + "\n");
				System.out.println("Max Number Of Shares at any point: " + maxShares + "\tMax Investment at any point: " + maxInvestment);
				netProfit = 0.0;
				totalSell = 0.0;
				totalBuy = 0.0;
				endOfRoundDifference = 0.0;
				allBuys.clear();
			}
		}
	}
}
