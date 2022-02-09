package ContinuousDayBuyVolatile;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import alpacaPojo.TickerOhlc;

public class BackTestingContinuousDayBuyNTick {
	/*
	 * Read Day data of 'n' Tickers
	 * Calculate Stable price in the data frame for each day - (Separate function that could be altered to have different Stable prices)
	 * Validate ticker based on maximum individual investment is reached and may be the next investment is based on fix down percent
	 * Select top 'm' Tickers that have fallen most for the next day after validating all tickers as per last step
	 * Print all the buys for everyday for the timeframe and execute till maximum overall investment is reached
	 * Add back the equity amount on sell based on the profitFactor 
	 */
	static String filename = "DayData - AIHS_2018-08-01_2021-11-05";
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	static List<TickerOhlc> tickerData = new ArrayList<TickerOhlc>();
	static DecimalFormat df = new DecimalFormat("#.##"); 
	//static Map<String, TickerOhlc> tickerMap = new HashMap<String, TickerOhlc>();
	static List<HashMap<String, TickerOhlc>> allTickers = new ArrayList<HashMap<String, TickerOhlc>>();
	static Double maxInvestment = 10000.00;
	static Double maxInvestmentperTicker = 500.00;
	static Double oneTimeInvestment = 50.00;
	static Double maxNumberOfBuy = (maxInvestmentperTicker/oneTimeInvestment);
	static HashMap<String, List<Double>> sellTickerPrices = new HashMap<String, List<Double>>();
	static List<Double> sellPrices = new ArrayList<Double>();
	static Double buyAgainFactor = 0.92;
	static Double profitFactor = 1.1;
	static String[] tickerDate;
	static int maxBuys = 1;
	static int totalBuys = 0;
	static int maxTickersPerDay = 5;
	static File targetFolder = new File("C:\\Users\\nakul\\Documents\\HistoricalDataDay\\New Folder");

	public static void main(String args[]) {
		BackTestingContinuousDayBuyNTick demo = new BackTestingContinuousDayBuyNTick();
		for(File file : targetFolder.listFiles()) {
			demo.readFileAsList(file.getName());
		}
		demo.executeContinuousDayBuy();
	}
	public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm)
	{
		// Create a list from elements of HashMap
		List<Map.Entry<String, Double> > list =
				new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
		int sortedMapCurrentSize = 0;
		for (Map.Entry<String, Double> aa : list) {
			if(sortedMapCurrentSize == maxTickersPerDay) break;
			temp.put(aa.getKey(), aa.getValue());
			sortedMapCurrentSize++;
		}
		return temp;
	}

	private void executeContinuousDayBuy() {
		Double netProfit = 0.0;
		for(int i = 1 ; i < tickerDate.length; i++) {
			//Have to maintain date separately so that sorting is not required for Map
			HashMap<String, Double> tickerPrices = new HashMap<String, Double>();
			HashMap<String, Double> sortedTicker = new HashMap<String, Double>();
			for(HashMap<String, TickerOhlc> tickerMap : allTickers) {
				TickerOhlc ticker = tickerMap.get(tickerDate[i]);
				if(ticker == null) continue;
				Double dayDrop = (ticker.open - ticker.mean)/ticker.open;
				tickerPrices.put(ticker.symbol, dayDrop);		
			}
			sortedTicker = sortByValue(tickerPrices);
			System.out.println("Sorted Day Buy List: " + sortedTicker.toString());
			for(HashMap<String, TickerOhlc> tickerMap : allTickers) {
				TickerOhlc ticker = tickerMap.get(tickerDate[i]);
				if(ticker == null) continue;
				List<Double> currentSellPrices = sellTickerPrices.get(ticker.symbol);
				for (Map.Entry<String,Double> entry : sortedTicker.entrySet()) {
					if(entry.getKey().equals(ticker.symbol)) {
						currentSellPrices.add(Double.valueOf(df.format(ticker.open*profitFactor)));
						sellTickerPrices.put(ticker.symbol, currentSellPrices);
						System.out.println(/*tickerDate[i] + */ticker.toString());
						System.out.println("Buying at: " + ticker.open + " & Will be selling this at: " + df.format(ticker.open*profitFactor));
						totalBuys++;

						int currentBuys = 0;
						for(HashMap.Entry<String, List<Double>> entrySell : sellTickerPrices.entrySet()) {
							currentBuys = currentBuys + entrySell.getValue().size();
						}
						if(maxBuys < currentBuys) maxBuys = currentBuys;
					}
				}
				currentSellPrices = sellTickerPrices.get(ticker.symbol);
				List<Double> soldOnes = new ArrayList<Double>();
				for (Double sellPrice : currentSellPrices) {
					if (ticker.high.compareTo(sellPrice) > 0) {
						soldOnes.add(sellPrice);
					}
				}
				if(soldOnes.size() != 0) {
					System.out.println(ticker.toString());
					System.out.println(" Selling a total of " + soldOnes.size() + " Buy Transactions with a total profit of: " + (profitFactor - 1)* oneTimeInvestment * soldOnes.size());
					netProfit = netProfit + (profitFactor - 1)* oneTimeInvestment * soldOnes.size();
					System.out.println("Profit So Far: " + df.format(netProfit));
					System.out.println("Unsold Stocks before selling: " + currentSellPrices);
					currentSellPrices.removeAll(soldOnes);
					sellTickerPrices.put(ticker.symbol, currentSellPrices);
					System.out.println("Unsold Stocks after selling: " + currentSellPrices);
				}
			}
			//New Day Operations
			System.out.println("\n");
		}
		int remainingBuys = 0;
		for(HashMap.Entry<String, List<Double>> entrySell : sellTickerPrices.entrySet()) {
			remainingBuys = remainingBuys + entrySell.getValue().size();
		}
		System.out.println("Net Profit due to sell: " + df.format(netProfit));
		System.out.println("Total Investment still in the game: " + oneTimeInvestment*remainingBuys);


		for(HashMap.Entry<String, List<Double>> entrySell : sellTickerPrices.entrySet()) {
			System.out.println(entrySell.getKey() + " - Unsold Stocks at profitPrice: " + entrySell.getValue());
			Double investedShares = 0.0;
			for (Double sellPrice : entrySell.getValue()) {
				Double boughtPrice = sellPrice/profitFactor;
				Double sharesForCurrentSell = oneTimeInvestment/boughtPrice;
				investedShares = investedShares + sharesForCurrentSell;
			}
			for(HashMap<String, TickerOhlc> tickerMap : allTickers) {
				TickerOhlc lastTicker = tickerMap.get(tickerDate[tickerDate.length - 1]);
				if(lastTicker.symbol.equals(entrySell.getKey())) {
					Double currentValueOfInvestment = lastTicker.dayAverage*investedShares;
					Double lossDueToHeldShares = oneTimeInvestment*entrySell.getValue().size() - currentValueOfInvestment;

					System.out.println("Loss Due to Held Shares: " + df.format(lossDueToHeldShares));
					netProfit = netProfit - lossDueToHeldShares;
				}
			}
		}
		System.out.println("Net Profit After Liquidating at last Bar: " + df.format(netProfit));
		System.out.println("Max Investment at any point: " + maxBuys*oneTimeInvestment);
		System.out.println("Profit Percent: " + df.format(netProfit*100/(maxBuys*oneTimeInvestment)));
		System.out.println("Total Buys: " + totalBuys);
	}


	private void readFileAsList(String file) {
		String fileName = "C:\\Users\\nakul\\Documents\\HistoricalDataDay\\New Folder\\" + file;

		boolean firstLine = false;
		try {
			List<String> lines = Files.readAllLines(Paths.get(fileName),
					Charset.defaultCharset());
			Double prevDayHigh = 0.0;
			tickerDate = new String[lines.size() - 1];
			int i = 0;
			HashMap<String, TickerOhlc> tickerMap = new HashMap<String, TickerOhlc>();
			for (String line : lines) {
				if(!firstLine) {
					firstLine = true;
					continue;
				}

				List<String> splitLine = Arrays.asList(line.split(",", -1));
				Date date = format.parse(splitLine.get(1));
				TickerOhlc ticker = new TickerOhlc();
				ticker.symbol = splitLine.get(0);
				ticker.date = splitLine.get(1);
				tickerDate[i] = splitLine.get(1);
				i++;
				ticker.open = Double.valueOf(splitLine.get(2));

				ticker.close = (Double.valueOf(splitLine.get(3)));
				ticker.high = (Double.valueOf(splitLine.get(4)));
				ticker.low = (Double.valueOf(splitLine.get(5)));
				ticker.dayAverage = (Double.valueOf(splitLine.get(2)) + Double.valueOf(splitLine.get(3)) + Double.valueOf(splitLine.get(4)) + Double.valueOf(splitLine.get(5)))/4;
				ticker.mean = prevDayHigh;
				tickerMap.put(ticker.date, ticker);
				//					tickerVolume.add(Double.valueOf(splitLine.get(6)));
				//					tickerVwap.add(Double.valueOf(splitLine.get(8)));
				prevDayHigh = ticker.high;
				if(sellTickerPrices.get(ticker.symbol) == null) {
					sellTickerPrices.put(ticker.symbol, new ArrayList<Double>());
				}
			}
			//								ticker.add(splitLine.get(3));
			allTickers.add(tickerMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//	private Double calculateMean(TickerOhlc ticker) {
	//		return ticker.high;
	//	}
}
