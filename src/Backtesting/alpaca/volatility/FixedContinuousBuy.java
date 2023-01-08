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

public class FixedContinuousBuy {

	public static void main(String[] args) throws IOException {
		/*
		 * Get Volatile Stocks, could be daily, hourly
		 * Get the average mean price of volatile stocks, if list was daily then last 3 months average price, if list was hourly then mean price of last 1 month
		 * If for any day the price is below average price then buy
		 * Sell at a constant/random profit
		 * Max buy is configurable for the day
		 */
		LocalDate start = LocalDate.now().minusMonths(36);
		LocalDate end = LocalDate.now().minusMonths(0);
		ExtractVolatileStocks demo = new ExtractVolatileStocks();
		AlpacaApiCaller alpacaAccount = TickerUtils.getAlpacaAccountObject();
		File file = new File("logs/outputReportFixed.txt" + start + "_" + end + LocalDateTime.now().getNano());
		PrintWriter pw0 = new PrintWriter(new FileOutputStream(file), true);
		int stocks = 15;
		pw0.write("Stocks at a time: " + stocks);
		try {
			for(int i = 0 ; i < 50 ; i++) {
				pw0.write("Run Instance: " + i+1 + "\n");
				start = LocalDate.now().minusMonths(48);
				File file1 = new File("logs/output5.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getHour());
				PrintWriter pw1 = new PrintWriter(new FileOutputStream(file1), true);
				File file2 = new File("logs/output10.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getMinute());
				PrintWriter pw2 = new PrintWriter(new FileOutputStream(file2), true);

				HashMap<String, Double> lastPriceMap = new HashMap<String, Double>();
				HashMap<String, Double> startingPriceMap = new HashMap<String, Double>();
				Double maxBuyPrice = 100.0;
				int maxTotalBuys = 4000;
				int maxBuys5 = 0;
				int maxBuys10 = 0;
				int totalBuy25And5 = 0;
				int totalSell25And5 = 0;
				int totalBuy25And10 = 0;
				int totalSell25And10 = 0;

				Double profitPercent10 = 1.1;
				Double profitPercent05 = 1.05;
				DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
				HashMap<String, ArrayList<Double>> sellMap25And5 = new HashMap<String, ArrayList<Double>>();
				HashMap<String, ArrayList<Double>> sellMap25And10 = new HashMap<String, ArrayList<Double>>();

				int totalBuyCount25And5 = 0;
				int totalBuyCount25And10 = 0;

				List<String> highlyVolatileStocks = demo.extractVolatileStocksFromFile("");
				Collections.shuffle(highlyVolatileStocks);
				highlyVolatileStocks = highlyVolatileStocks.subList(0, 15);
				List<TimeframePojo> multiDailyTime = alpacaAccount.getMultiTimeframe(highlyVolatileStocks.toString().substring(1, highlyVolatileStocks.toString().length()-1).replaceAll(" ", ""), start.toString(), end.toString(), "1Day", "");
				HashMap<LocalDateTime, ArrayList<BarsPojo>> dailyTimeMap = new HashMap<LocalDateTime, ArrayList<BarsPojo>>();
				for(TimeframePojo dailyTime : multiDailyTime) {
					BarsPojo[] dailyBars = dailyTime.getBars();
					lastPriceMap.put(dailyTime.getSymbol(), Double.valueOf(dailyBars[dailyBars.length-1].getVwap()));
					startingPriceMap.put(dailyTime.getSymbol(), Double.valueOf(dailyBars[0].getVwap()));
					for(BarsPojo bar : dailyBars) {
						bar.setSymbol(dailyTime.getSymbol());
						LocalDateTime dayTime = LocalDateTime.parse(bar.getTime(), FORMATTER);
						ArrayList<BarsPojo> dayListSoFar = dailyTimeMap.getOrDefault(dayTime, new ArrayList<BarsPojo>());
						dayListSoFar.add(bar);
						dailyTimeMap.put(dayTime, dayListSoFar);
					}
				}
				//					if(!(start.getDayOfWeek() == DayOfWeek.TUESDAY)) {
				//						System.out.println(start + " is not Tuesday!");
				//						start = start.plusDays(1);
				//						continue;
				//					}

				ArrayList<LocalDateTime> sortedKeys= new ArrayList<LocalDateTime>(dailyTimeMap.keySet());
				Collections.sort(sortedKeys);
				for(LocalDateTime sortedKey : sortedKeys) {
					ArrayList<BarsPojo> allSymbolBars = dailyTimeMap.get(sortedKey);
					for(BarsPojo bar: allSymbolBars) {
						String symbol = bar.getSymbol();
						Double highPrice = Double.valueOf(bar.getHighPrice());
						Double lowPrice = Double.valueOf(bar.getLowPrice());
						Double vwap = Double.valueOf(bar.getVwap());
						ArrayList<Double> sells25And5 = sellMap25And5.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sells25And10 = sellMap25And10.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sellExecuted25And5 = new ArrayList<Double>();
						ArrayList<Double> sellExecuted25And10 = new ArrayList<Double>();

						for(Double sell : sells25And5) {
							if(highPrice.compareTo(sell*profitPercent05) > 0) {
								sellExecuted25And5.add(sell);
								System.out.println("25And5Sold " + symbol + " at: " + sell*profitPercent05 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount25And5);
								pw1.write("25And5Sold " + symbol + " at: " + sell*profitPercent05 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount25And5 + "\n");
								totalSell25And5++;
								totalBuyCount25And5--;
							}
						}
						sells25And5.removeAll(sellExecuted25And5);

						if(totalBuyCount25And5 < maxTotalBuys && (sortedKey.getDayOfWeek() == DayOfWeek.TUESDAY)) { 
							sells25And5.add(vwap);
							totalBuy25And5++;
							totalBuyCount25And5++;
							if(maxBuys5 < totalBuyCount25And5) maxBuys5 = totalBuyCount25And5;
							System.out.println("25And5Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " - " + bar.getTime() + " " + sortedKey);
							pw1.write("25And5Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " - " + bar.getTime() + " " + sortedKey + "\n");
						}
						if(!sells25And5.isEmpty())
							sellMap25And5.put(symbol, sells25And5);
						if(sells25And5.isEmpty())
							sellMap25And5.remove(symbol);

						for(Double sell : sells25And10) {
							if(highPrice.compareTo(sell*profitPercent10) > 0) {
								sellExecuted25And10.add(sell);
								System.out.println("25And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount25And10);
								pw2.write("25And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount25And10 + "\n");
								totalSell25And10++;
								totalBuyCount25And10--;
							}
						}
						sells25And10.removeAll(sellExecuted25And10);

						if(totalBuyCount25And10 < maxTotalBuys && sortedKey.getDayOfWeek() == DayOfWeek.TUESDAY) { 
							sells25And10.add(vwap);
							totalBuy25And10++;
							totalBuyCount25And10++;
							if(maxBuys10 < totalBuyCount25And10) maxBuys10 = totalBuyCount25And10;
							System.out.println("25And10Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " - " + bar.getTime() + " " + sortedKey);
							pw2.write("25And10Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount25And10 +  "\n");
						}
						if(!sells25And10.isEmpty())
							sellMap25And10.put(symbol, sells25And10);
						if(sells25And10.isEmpty())
							sellMap25And10.remove(symbol);
					}
				}
				if(start.getDayOfWeek() == DayOfWeek.FRIDAY)start = start.plusDays(2);
				start = start.plusDays(1);
				System.out.println("25And5Current Holdings: " + sellMap25And5.entrySet());
				System.out.println("25And10Current Holdings: " + sellMap25And10.entrySet());
				pw1.write("25And5Current Holdings: " + sellMap25And5.entrySet() + "\n");
				pw2.write("25And10Current Holdings: " + sellMap25And10.entrySet() + "\n");

				Double holdingValue25And5 = 0.0;
				Double holdingValue25And10 = 0.0;

				int holdingCount25And5 = 0;
				int holdingCount25And10 = 0;

				for (Map.Entry<String, ArrayList<Double>> entry : sellMap25And5.entrySet()) {
					Double holdingPrice = lastPriceMap.get(entry.getKey());
					Double startingPrice = startingPriceMap.get(entry.getKey());
					System.out.println(entry.getKey() + "," + startingPrice + "," + holdingPrice);
					pw1.write(entry.getKey() + "," + startingPrice + "," + holdingPrice);
					pw0.write(entry.getKey() + "," + startingPrice + "," + holdingPrice);
					Double totalShares = 0.0;

					for(Double holding : entry.getValue()) {
						//					Double buyPrice = holding*0.82;
						totalShares = totalShares + maxBuyPrice/holding;
					}
					holdingValue25And5 = holdingValue25And5 + holdingPrice*totalShares;
					holdingCount25And5 = holdingCount25And5 + entry.getValue().size();
				}

				for (Map.Entry<String, ArrayList<Double>> entry : sellMap25And10.entrySet()) {
					Double holdingPrice = lastPriceMap.get(entry.getKey());
					Double startingPrice = startingPriceMap.get(entry.getKey());
					System.out.println(entry.getKey() + "," + startingPrice + "," + holdingPrice);
					pw1.write(entry.getKey() + "," + startingPrice + "," + holdingPrice);
					pw0.write(entry.getKey() + "," + startingPrice + "," + holdingPrice);
					Double totalShares = 0.0;
					for(Double holding : entry.getValue()) {
						//					Double buyPrice = holding*0.82;
						totalShares = totalShares + maxBuyPrice/holding;
					}
					holdingValue25And10 = holdingValue25And10 + holdingPrice*totalShares;
					holdingCount25And10 = holdingCount25And10 + entry.getValue().size();
				}

				Double netProfit25And5 = (totalSell25And5*profitPercent05*maxBuyPrice) + holdingValue25And5 - (totalBuy25And5*maxBuyPrice);
				Double netProfit25And10 = (totalSell25And10*profitPercent10*maxBuyPrice) + holdingValue25And10 - (totalBuy25And10*maxBuyPrice);

				System.out.println("25And5Total Buy: " + totalBuy25And5*maxBuyPrice + ", Total Sell: " + totalSell25And5*profitPercent05*maxBuyPrice + ", Holding Value: " + holdingValue25And5 + ", Holding Count: " + holdingCount25And5 + " , NetProfit: " + netProfit25And5 + " , Ratio: " + netProfit25And5/holdingCount25And5 + " , MaxBuys: " + maxBuys5);
				System.out.println("25And10Total Buy: " + totalBuy25And10*maxBuyPrice + ", Total Sell: " + totalSell25And10*profitPercent10*maxBuyPrice + ", Holding Value: " + holdingValue25And10 + ", Holding Count: " + holdingCount25And10 + " , NetProfit: " + netProfit25And10 + " , Ratio: " + netProfit25And10/holdingCount25And10 + " , MaxBuys: " + maxBuys10);
				pw1.write("25And5Total Buy: " + totalBuy25And5*maxBuyPrice + ", Total Sell: " + totalSell25And5*profitPercent05*maxBuyPrice + ", Holding Value: " + holdingValue25And5 + ", Holding Count: " + holdingCount25And5 + " , NetProfit: " + netProfit25And5 + " , Ratio: " + netProfit25And5/holdingCount25And5 + " , MaxBuys: " + maxBuys5);
				pw2.write("25And10Total Buy: " + totalBuy25And10*maxBuyPrice + ", Total Sell: " + totalSell25And10*profitPercent10*maxBuyPrice + ", Holding Value: " + holdingValue25And10 + ", Holding Count: " + holdingCount25And10 + " , NetProfit: " + netProfit25And10 + " , Ratio: " + netProfit25And10/holdingCount25And10 + " , MaxBuys: " + maxBuys10);

				pw0.write("25And5Total Buy: " + totalBuy25And5*maxBuyPrice + ", Total Sell: " + totalSell25And5*profitPercent05*maxBuyPrice + ", Holding Value: " + holdingValue25And5 + ", Holding Count: " + holdingCount25And5 + " , NetProfit: " + netProfit25And5 + " , Ratio: " + netProfit25And5/holdingCount25And5 + " , MaxBuys: " + maxBuys5 + "\n");
				pw0.write("25And10Total Buy: " + totalBuy25And10*maxBuyPrice + ", Total Sell: " + totalSell25And10*profitPercent10*maxBuyPrice  + ", Holding Value: " + holdingValue25And10 + ", Holding Count: " + holdingCount25And10 + " , NetProfit: " + netProfit25And10 + " , Ratio: " + netProfit25And10/holdingCount25And10 + " , MaxBuys: " + maxBuys10 + "\n");
				pw2.close();
				pw1.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Exception Caught: " + e);
		}
		pw0.close();
	}
}
