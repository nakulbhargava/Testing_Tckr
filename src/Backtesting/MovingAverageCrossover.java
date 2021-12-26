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

public class MovingAverageCrossover {
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		
	static List<String> ticker = null;
	static String filename = "DayData - SNAP_2018-08-01_2021-11-05";
	static File fileTrue = new File("C:\\Users\\nakul\\Documents\\" + filename + " - " + localTime + ".txt");
	static PrintWriter pw;
	static FileOutputStream fo;
	static List<Double> tickerOpen = new ArrayList<Double>();
	static List<Double> tickerClose = new ArrayList<Double>();
	static List<Double> tickerLow = new ArrayList<Double>();
	static List<Double> tickerHigh = new ArrayList<Double>();
	static List<String> tickerDate = new ArrayList<String>();
	//static Double[] tickerMovingAverages = null;
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	static Date marketStartDate, marketEndDate;
	//static int movingAverageDays = 45;
	static Double maxAmount = 500.00;
	static Double oneTimeInvestment = 20.00;
	static Double profitPercent = 1.13;
	static Double lossPercent = 0.95;
	static Double maxProfit = 0.0;
	static Double maxRoundProfit = 0.0;

	public static void main(String[] args) throws FileNotFoundException, ParseException {
		//		pw = new PrintWriter(new FileOutputStream(fileTrue, true));
		//		fo = new FileOutputStream(fileTrue, true);
		MovingAverageCrossover demo = new MovingAverageCrossover();
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
		Double totalInvestment = 0.0;
		Double maxInvestment = 0.0;
		//Double averageInvestedPrice = 0.0;
		Double profitPrice = 0.0;
		Double netProfit = 0.0;
		Double maxProfit = 0.0;
		
		//int numberOfInvestments = 0;
		boolean alreadyInvested = false;
		for(int x = 10; x < 51; x= x+10) {
			for(int y = 50; y < 401; y = y + 50) {
				Double[] movingAverageSmall = this.calculateLowMovingAverage(x);
				Double[] movingAverageBig = this.calculateLowMovingAverage(y);
				System.out.println("Calculating for Moving Averages: " + x + " & " + y);
				for(int i = y; i< tickerOpen.size(); i++) {
					if(movingAverageSmall[i].compareTo(movingAverageBig[i]) > 0 && !alreadyInvested) {
						//numberOfInvestments++;
						alreadyInvested = true;
						totalInvestment = tickerLow.get(i);
						System.out.println("Buying Price: " + tickerLow.get(i) + " Date: " + tickerDate.get(i));
						//averageInvestedPrice = totalInvestment/numberOfInvestments;
						if(maxInvestment.compareTo(totalInvestment) < 0) {
							maxInvestment = totalInvestment;
						}
					}
					if(movingAverageSmall[i].compareTo(movingAverageBig[i]) < 0 && alreadyInvested && totalInvestment.compareTo(tickerLow.get(i)) < 0) {
						System.out.println("Selling 1 share At Price: " + tickerLow.get(i)  + " Date: " + tickerDate.get(i));
						profitPrice = tickerLow.get(i) - totalInvestment;
						netProfit = profitPrice + netProfit;
						System.out.println("Profit on this sell: " + profitPrice + " Net Profit: " + netProfit);
						profitPrice = 0.0;
						//numberOfInvestments = 0;
						//averageInvestedPrice = 0.0;
						totalInvestment = 0.0;
						alreadyInvested = false;
					}
				}
				if(maxProfit.compareTo(netProfit) < 0) {
					maxProfit = netProfit;
				}
				System.out.println("Net Profit this Round: " + netProfit + "\n");
				netProfit = 0.0;
				totalInvestment = 0.0;
				alreadyInvested = false;
				profitPrice = 0.0;
			}
		}
	}
}
