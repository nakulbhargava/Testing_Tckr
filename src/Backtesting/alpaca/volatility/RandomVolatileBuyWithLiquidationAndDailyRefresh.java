package Backtesting.alpaca.volatility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.BarsPojo;
import alpacaPojo.TimeframePojo;

public class RandomVolatileBuyWithLiquidationAndDailyRefresh {

	public static void main(String[] args) throws IOException {
		/*
		 * Get Volatile Stocks, could be daily, hourly
		 * Get the average mean price of volatile stocks, if list was daily then last 3 months average price, if list was hourly then mean price of last 1 month
		 * If for any day the price is below average price then buy
		 * Sell at a constant/random profit
		 * Max buy is configurable for the day
		 */
		int startMonth = 3;
		LocalDate start = LocalDate.now().minusMonths(3).minusDays(0);
		LocalDate end = LocalDate.now().minusMonths(0);
		ExtractVolatileStocks demo = new ExtractVolatileStocks();
		AlpacaApiCaller alpacaAccount = TickerUtils.getAlpacaAccountObject();
		File file = new File("logs/outputReport.txt" + start + "_" + end);
		PrintWriter pw0 = new PrintWriter(new FileOutputStream(file), true);
		try {
			for(int i = 0 ; i < 5; i++) {
				start = LocalDate.now().minusMonths(0).minusDays(0);
				end = LocalDate.now().minusMonths(0).plusDays(1);
				pw0.write("Run Instance: " + i+1 + "\n");
				System.out.println("Run Instance: " + i+1 + "\n");
//				start = LocalDate.now().minusMonths(startMonth);
				Double equity = 30000.0;
				Double oneTimeProfit = 1000.0;
				//				Double oneTimeLoss = 850.0;
				File file2 = new File("logs/output100And10.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getMinute());
				PrintWriter pw2 = new PrintWriter(new FileOutputStream(file2), true);
				HashMap<String, Double> lastPriceMap = new HashMap<String, Double>();
				Double maxBuyPrice = 1000.0;
				Double maxDailyBuyCount100 = 10.0;

				int totalBuy100And10 = 0;
				int totalSell100And10 = 0;
				
				Double profitPercent10 = 1.025;

				DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
				
				HashMap<String, ArrayList<Double>> sellMap100And10 = new HashMap<String, ArrayList<Double>>();

				ArrayList<Double> incompleteSell100And10 = new ArrayList<Double>();

				Double holdingValue100And10 = 0.0;

				int holdingCount100And10 = 0;

				Double partialProfit100And10 = 0.0;

				Double netProfit100And10 = 0.0;

				int liquidationCount100And10 = 0;

				int liquidationLossCount100And10 = 0;

				List<String> highlyVolatileStocksBackUp = new ArrayList<String>();
				while(start.isBefore(end)) {
//										if(!(start.getDayOfWeek() == DayOfWeek.MONDAY || start.getDayOfWeek() == DayOfWeek.WEDNESDAY)) {
//											System.out.println(start + " is not monday or wednesday!");
//											start = start.plusDays(1);
//											continue;
//										}
					int dailyBuyCount100And10 = 0;
					List<String> highlyVolatileStocks = new ArrayList<String>();
					if(start.getDayOfMonth() ==1 || start.getDayOfMonth() ==21 || highlyVolatileStocks.isEmpty()) {
						highlyVolatileStocks =demo.extractVolatileStocks(start);
						highlyVolatileStocksBackUp.clear();
						highlyVolatileStocksBackUp.addAll(highlyVolatileStocks);
					}
//					highlyVolatileStocksBackUp = demo.extractVolatileStocksFromFile("");
					Collections.shuffle(highlyVolatileStocksBackUp);
					highlyVolatileStocks = highlyVolatileStocksBackUp.subList(0, highlyVolatileStocksBackUp.size() < 200 ? highlyVolatileStocksBackUp.size() : 200);
					ArrayList<String> heldSymbol100And10 = new ArrayList<String>(sellMap100And10.keySet());
					highlyVolatileStocks.removeAll(heldSymbol100And10);
					highlyVolatileStocks.addAll(heldSymbol100And10);

					HashMap<LocalDateTime, ArrayList<BarsPojo>> dailyTimeMap = new HashMap<LocalDateTime, ArrayList<BarsPojo>>();
					List<TimeframePojo> multiDailyTime = alpacaAccount.getMultiTimeframe(highlyVolatileStocks.toString().substring(1, highlyVolatileStocks.toString().length()-1).replaceAll(" ", ""), start.toString(), start.toString(), "2Min", "");
					for(TimeframePojo dailyTime : multiDailyTime) {
						BarsPojo[] dailyBars = dailyTime.getBars();
						for(BarsPojo bar : dailyBars) {
							bar.setSymbol(dailyTime.getSymbol());
							LocalDateTime minuteTime = LocalDateTime.parse(bar.getTime(), FORMATTER);
							ArrayList<BarsPojo> minuteListSoFar = dailyTimeMap.getOrDefault(minuteTime, new ArrayList<BarsPojo>());
							minuteListSoFar.add(bar);
							dailyTimeMap.put(minuteTime, minuteListSoFar);
						}
					}
					System.out.println("Executing for: " + start);
					pw2.write("\nExecuting for: " + start+ "\n");
					ArrayList<LocalDateTime> sortedKeys= new ArrayList<LocalDateTime>(dailyTimeMap.keySet());
					Collections.sort(sortedKeys);
					for(LocalDateTime sortedKey : sortedKeys) {
						if((sortedKey.getMinute() > 53 && sortedKey.getMinute() <= 59) || 
								(sortedKey.getMinute() > 29 && sortedKey.getMinute() <= 33)) {
							maxDailyBuyCount100 = maxDailyBuyCount100 + 0.62;
						}
						ArrayList<BarsPojo> allSymbolBars = dailyTimeMap.get(sortedKey);
						for(BarsPojo bar: allSymbolBars) {
							lastPriceMap.put(bar.getSymbol(), Double.valueOf(bar.getClosePrice()));
							String symbol = bar.getSymbol();
							Double highPrice = Double.valueOf(bar.getHighPrice());
							Double lowPrice = Double.valueOf(bar.getLowPrice());
							Double vwap = Double.valueOf(bar.getVwap());
							ArrayList<Double> sells100And10 = sellMap100And10.getOrDefault(symbol, new ArrayList<Double>());
							
							ArrayList<Double> sellExecuted100And10 = new ArrayList<Double>();

							Double lowestSell100And10 = 100000.0;
							for(Double sell : sells100And10) {
								if(sell.compareTo(lowestSell100And10) < 0 ) lowestSell100And10 = sell;
								if(highPrice.compareTo(sell*profitPercent10) > 0) {
									sellExecuted100And10.add(sell);
									System.out.println("100And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey);
									pw2.write("100And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + "\n");
									totalSell100And10++;
								}
								else if(((sortedKey.getMinute() > 53 && sortedKey.getMinute() <= 59) ||
										(sortedKey.getMinute() > 29 && sortedKey.getMinute() <= 33)) && vwap.compareTo(sell*1.005) > 0){
									sellExecuted100And10.add(sell);
									incompleteSell100And10.add(vwap/sell);
									System.out.println("100And10Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey);
									pw2.write("100And10Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + "\n");
									totalSell100And10++;
								}
							}
							sells100And10.removeAll(sellExecuted100And10);
							//					sellMap.put(symbol, sells);
							dailyBuyCount100And10 = dailyBuyCount100And10 - sellExecuted100And10.size();
							//					System.out.println("Daily Buy Count: " + dailyBuyCount);
							if(dailyBuyCount100And10 < maxDailyBuyCount100 && !(sortedKey.getMinute() > 49 && sortedKey.getMinute() <= 59)) { 
								//							Double mean = meanPriceMap.get(symbol);
								//							if(lowPrice.compareTo(mean) < 0 && mean.compareTo(maxBuyPrice) < 0 && mean.compareTo(lowestSell100And10*0.82) < 0 ) {
								if(lowPrice.compareTo(lowestSell100And10*0.82) < 0 && lowPrice.compareTo(maxBuyPrice) < 0) {
									//						ArrayList<Double> sellList = sellMap.getOrDefault(symbol, new ArrayList<Double>());
									sells100And10.add(vwap);
									dailyBuyCount100And10++;
									totalBuy100And10++;
									System.out.println("100And10Bought " + symbol + " at: " + vwap + " Low Price: " + 
											lowPrice + " Daily Buy Count: " + dailyBuyCount100And10 + " - " + bar.getTime() + " " + sortedKey);
									pw2.write("100And10Bought " + symbol + " at: " + vwap + " Low Price: " + 
											lowPrice + " Daily Buy Count: " + dailyBuyCount100And10 + " - " + bar.getTime() + " " + sortedKey +  "\n");
								}
							}
							if(!sells100And10.isEmpty())
								sellMap100And10.put(symbol, sells100And10);
							if(sells100And10.isEmpty())
								sellMap100And10.remove(symbol);
						}
					}
					LocalDate tempDate = start;
					if(start.getDayOfWeek() == DayOfWeek.FRIDAY)start = start.plusDays(2);
					start = start.plusDays(1);
					System.out.println("100And10Current Holdings: " + sellMap100And10.entrySet());
					pw2.write("100And10Current Holdings: " + sellMap100And10.entrySet() + "\n");
					
					for (Map.Entry<String, ArrayList<Double>> entry : sellMap100And10.entrySet()) {
						//					System.out.println(entry.getKey() + "," + holdingPrice);
						Double holdingPrice = lastPriceMap.get(entry.getKey());
						Double totalShares = 0.0;
						for(Double holding : entry.getValue()) {
							//						Double buyPrice = holding*0.82;
							totalShares = totalShares + maxBuyPrice/holding;
						}
						holdingValue100And10 = holdingValue100And10 + holdingPrice*totalShares;
						holdingCount100And10 = holdingCount100And10 + entry.getValue().size();
					}

					for(Double partialSell : incompleteSell100And10) {
						partialProfit100And10 = partialProfit100And10 + partialSell*maxBuyPrice;
					}
					
					int currenttotalSell100And10 = totalSell100And10 - incompleteSell100And10.size();

					System.out.println("100And10Total Sell: " + currenttotalSell100And10 + " , Partial Sell: " + incompleteSell100And10.size());
					netProfit100And10 = (currenttotalSell100And10*profitPercent10*maxBuyPrice) + partialProfit100And10 + holdingValue100And10 - (totalBuy100And10*maxBuyPrice);
					pw0.write(tempDate + ", Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + currenttotalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + ", Liquidation Count: " + liquidationCount100And10 + ", LiquidationLoss Count: " + liquidationLossCount100And10 +"\n");
					pw2.write(tempDate + ", Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + currenttotalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + ", Liquidation Count: " + liquidationCount100And10 + ", LiquidationLoss Count: " + liquidationLossCount100And10 +"\n");
					System.out.println(tempDate + ", Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + currenttotalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + ", Liquidation Count: " + liquidationCount100And10 + ", LiquidationLoss Count: " + liquidationLossCount100And10 +"\n");
					//Some Constant Profit for now 2% of 40000

					sellMap100And10.clear();
					holdingValue100And10 = 0.0;
					holdingCount100And10 = 0;					
					partialProfit100And10 = 0.0;
					netProfit100And10 = 0.0;
					totalBuy100And10 = 0;
					totalSell100And10 = 0;	
					incompleteSell100And10.clear();
					maxDailyBuyCount100 = 10.0;
				}
//				System.out.println("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + totalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10);
//				pw2.write("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + totalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10);
//				pw0.write("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + totalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + "\n");
				pw2.close();	
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Exception Caught: " + e);
		}
		pw0.close();
	}
}
