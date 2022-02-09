package ContinuousDayBuyVolatile;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import alpacaPojo.TickerOhlc;

public class BackTestingContinuousDayBuyNoLimit_Mult {
	/*
	 * Read Day data of 'n' Tickers
	 * Calculate Stable price in the data frame for each day - (Separate function that could be altered to have different Stable prices)
	 * Validate ticker based on maximum individual investment is reached and may be the next investment is based on fix down percent
	 * Select top 'm' Tickers that have fallen most for the next day after validating all tickers as per last step
	 * Print all the buys for everyday for the timeframe and execute till maximum overall investment is reached
	 * Add back the equity amount on sell based on the profitFactor 
	 */
	static String filename = "DayData - AAPL_2018-08-01_2021-11-05";
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	static List<TickerOhlc> tickerData = new ArrayList<TickerOhlc>();
	static DecimalFormat df = new DecimalFormat("#.##"); 
	static Map<String, TickerOhlc> tickerMap = new HashMap<String, TickerOhlc>();
	static Double maxInvestment = 10000.00;
	static Double maxInvestmentperTicker = 500.00;
	static Double oneTimeInvestment = 50.00;
	static Double maxNumberOfBuy = (maxInvestmentperTicker/oneTimeInvestment);
	static List<Double> sellPrices = new ArrayList<Double>();
//	static Double buyAgainFactor = 0.92;
//	static Double profitFactor = 1.05;
	static String[] tickerDate;
	static int maxBuys = 1;
	static Double maxProfitPercent = 0.0;
	public static void main(String args[]) {
		BackTestingContinuousDayBuyNoLimit_Mult demo = new BackTestingContinuousDayBuyNoLimit_Mult();
		demo.readFileAsList();
		demo.executeContinuousDayBuy();
	}
	private void executeContinuousDayBuy() {
		
		for(Double profitFactor = 1.01; profitFactor.compareTo(1.12) < 0 ; profitFactor = profitFactor + 0.02 ) {
//			for (Double buyAgainFactor = 0.98 ; buyAgainFactor.compareTo(0.85) > 0 ; buyAgainFactor = buyAgainFactor - 0.02) {
				Double netProfit = 0.0;
				for(int i = 0 ; i < tickerDate.length; i++) {
					//Have to maintain date separately so that sorting is not required for Map
					TickerOhlc ticker = tickerMap.get(tickerDate[i]);

//					if(sellPrices.size() == 0 ) {
						sellPrices.add(Double.valueOf(df.format(ticker.open*profitFactor)));
//						System.out.println(/*tickerDate[i] + */ticker.toString());
//						System.out.println("Buying at: " + ticker.open + " & Will be selling this at: " + ticker.open*profitFactor);
//					}
//					else {
//						if(ticker.open < buyAgainFactor*ticker.mean) {
//							sellPrices.add(Double.valueOf(df.format(ticker.open*profitFactor)));
////							System.out.println(/*tickerDate[i] + */ticker.toString());
////							System.out.println("Buying again at: " + ticker.open + " & Will be selling this at: " + ticker.open*profitFactor);
//						}
//					}
					if(maxBuys < sellPrices.size()) maxBuys = sellPrices.size();
					List<Double> soldOnes = new ArrayList<Double>();
					for (Double sellPrice : sellPrices) {
						if (ticker.high.compareTo(sellPrice) > 0) {
							soldOnes.add(sellPrice);
						}
					}
					if(soldOnes.size() != 0) {
//						System.out.println(ticker.toString());
//						System.out.println(" Selling a total of " + soldOnes.size() + " Buy Transactions with a total profit of: " + (profitFactor - 1)* oneTimeInvestment * soldOnes.size());
						netProfit = netProfit + (profitFactor - 1)* oneTimeInvestment * soldOnes.size();
//						System.out.println("Profit So Far: " + netProfit);
//						System.out.println("Unsold Stocks before selling: " + sellPrices);
						sellPrices.removeAll(soldOnes);
//						System.out.println("Unsold Stocks after selling: " + sellPrices);
					}
				}
				profitFactor = Double.valueOf(df.format(profitFactor));
				System.out.println("Profit Factor: " + df.format(profitFactor));/* + " Buy Again Factor: " + df.format(buyAgainFactor));*/
				System.out.println("Net Profit due to sell: " + df.format(netProfit));
				System.out.println("Total Investment still in the game: " + oneTimeInvestment*sellPrices.size());
				System.out.println("Unsold Stocks at profitPrice: " + sellPrices);
				Double investedShares = 0.0;
				for (Double sellPrice : sellPrices) {
					Double boughtPrice = sellPrice/profitFactor;
					Double sharesForCurrentSell = oneTimeInvestment/boughtPrice;
					investedShares = investedShares + sharesForCurrentSell;
				}
				TickerOhlc lastTicker = tickerMap.get(tickerDate[tickerDate.length - 1]);
				Double currentValueOfInvestment = lastTicker.dayAverage*investedShares;
				Double lossDueToHeldShares = oneTimeInvestment*sellPrices.size() - currentValueOfInvestment;
				System.out.println("Loss Due to Held Shares: " + df.format(lossDueToHeldShares));
				netProfit = netProfit - lossDueToHeldShares;
				System.out.println("Net Profit After Liquidating at last Bar: " + df.format(netProfit));
				System.out.println("Max Investment at any point: " + df.format(maxBuys*oneTimeInvestment));
				Double profitPercent = (netProfit*100)/(maxBuys*oneTimeInvestment);
				if(maxProfitPercent.compareTo(profitPercent) < 0) maxProfitPercent = profitPercent;
				System.out.println("Profit Percent: " + df.format(profitPercent) + "\n");
				sellPrices.clear();
				maxBuys = 0;
//			}
		}
		System.out.println("Max Profit Percent: " + df.format(maxProfitPercent) + "\n");
	}
	private void readFileAsList() {
		String fileName = "/test/" + filename + ".txt";

		boolean firstLine = false;
		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(uri),
					Charset.defaultCharset());
			Double prevDayHigh = 0.0;
			tickerDate = new String[lines.size() - 1];
			int i = 0;
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
			}
			//								ticker.add(splitLine.get(3));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Double calculateMean(TickerOhlc ticker) {
		return ticker.high;
	}
}
