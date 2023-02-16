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

public class RandomVolatileBuyWithLiquidation {

	public static void main(String[] args) throws IOException {
		/*
		 * Get Volatile Stocks, could be daily, hourly
		 * Get the average mean price of volatile stocks, if list was daily then last 3 months average price, if list was hourly then mean price of last 1 month
		 * If for any day the price is below average price then buy
		 * Sell at a constant/random profit
		 * Max buy is configurable for the day
		 */
		int startMonth = 6;
		LocalDate start = LocalDate.now().minusMonths(startMonth);
		LocalDate end = LocalDate.now().minusMonths(0);
		ExtractVolatileStocks demo = new ExtractVolatileStocks();
		AlpacaApiCaller alpacaAccount = TickerUtils.getAlpacaAccountObject();
		File file = new File("logs/outputReport.txt" + start + "_" + end);
		PrintWriter pw0 = new PrintWriter(new FileOutputStream(file), true);
		try {
		for(int i = 0 ; i < 10 ; i++) {
			pw0.write("Run Instance: " + i+1 + "\n");
			start = LocalDate.now().minusMonths(startMonth);
			Double equity = 40000.0;
			Double oneTimeProfit = 600.0;
			File file1 = new File("logs/output100And5.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getHour());
			PrintWriter pw1 = new PrintWriter(new FileOutputStream(file1), true);
			File file2 = new File("logs/output100And10.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getMinute());
			PrintWriter pw2 = new PrintWriter(new FileOutputStream(file2), true);
			File file3 = new File("logs/output50And5.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getSecond());
			PrintWriter pw3 = new PrintWriter(new FileOutputStream(file3), true);
			File file4 = new File("logs/output50And10.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getYear());
			PrintWriter pw4 = new PrintWriter(new FileOutputStream(file4), true);;
//			HashMap<String, Double> meanPriceMap = new HashMap<String, Double>();
			HashMap<String, Double> lastPriceMap = new HashMap<String, Double>();
			Double maxBuyPrice = 100.0;
			int maxDailyBuyCount100 = 100;
			int maxDailyBuyCount50 = 50;
			int maxTotalBuys100 = 400;
			int maxTotalBuys50 = 800;
			int totalBuy100And5 = 0;
			int totalSell100And5 = 0;
			int totalBuy50And5 = 0;
			int totalSell50And5 = 0;
			int totalBuy100And10 = 0;
			int totalSell100And10 = 0;
			int totalBuy50And10 = 0;
			int totalSell50And10 = 0;
			Double profitPercent10 = 1.1;
			Double profitPercent05 = 1.05;
			DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
			HashMap<String, ArrayList<Double>> sellMap100And5 = new HashMap<String, ArrayList<Double>>();
			HashMap<String, ArrayList<Double>> sellMap50And5 = new HashMap<String, ArrayList<Double>>();
			HashMap<String, ArrayList<Double>> sellMap100And10 = new HashMap<String, ArrayList<Double>>();
			HashMap<String, ArrayList<Double>> sellMap50And10 = new HashMap<String, ArrayList<Double>>();

			ArrayList<Double> incompleteSell100And5 = new ArrayList<Double>();
			ArrayList<Double> incompleteSell100And10 = new ArrayList<Double>();
			ArrayList<Double> incompleteSell50And5 = new ArrayList<Double>();
			ArrayList<Double> incompleteSell50And10 = new ArrayList<Double>();

			int totalBuyCount100And5 = 0;
			int totalBuyCount100And10 = 0;
			int totalBuyCount50And5 = 0;
			int totalBuyCount50And10 = 0;
			
			Double holdingValue100And5 = 0.0;
			Double holdingValue100And10 = 0.0;
			Double holdingValue50And5 = 0.0;
			Double holdingValue50And10 = 0.0;

			int holdingCount100And5 = 0;
			int holdingCount100And10 = 0;
			int holdingCount50And5 = 0;
			int holdingCount50And10 = 0;

			Double partialProfit100And5 = 0.0;
			Double partialProfit100And10 = 0.0;
			Double partialProfit50And5 = 0.0;
			Double partialProfit50And10 = 0.0;
			
			Double netProfit100And5 = 0.0;
			Double netProfit100And10 = 0.0;
			Double netProfit50And5 = 0.0;
			Double netProfit50And10 = 0.0;
			
			int liquidationCount100And5 = 0;
			int liquidationCount100And10 = 0;
			int liquidationCount50And5 = 0;
			int liquidationCount50And10 = 0;
			

			ArrayList<String> highlyVolatileStocksBackUp = new ArrayList<String>();
			while(start.isBefore(end)) {
				if(!(start.getDayOfWeek() == DayOfWeek.MONDAY || start.getDayOfWeek() == DayOfWeek.WEDNESDAY)) {
					System.out.println(start + " is not monday or wednesday!");
					start = start.plusDays(1);
					continue;
				}
				int dailyBuyCount100And5 = 0;
				int dailyBuyCount100And10 = 0;
				int dailyBuyCount50And5 = 0;
				int dailyBuyCount50And10 = 0;
				List<String> highlyVolatileStocks = new ArrayList<String>();
				if(start.getDayOfMonth() ==1 || highlyVolatileStocks.isEmpty()) {
					highlyVolatileStocks =demo.extractVolatileStocks(start);
					highlyVolatileStocksBackUp.clear();
					highlyVolatileStocksBackUp.addAll(highlyVolatileStocks);
				}
//				List<String> highlyVolatileStocks = demo.extractVolatileStocksFromFile("");
				Collections.shuffle(highlyVolatileStocksBackUp);
				highlyVolatileStocks = highlyVolatileStocksBackUp.subList(0, highlyVolatileStocks.size() < 300 ? highlyVolatileStocks.size() : 300);
				ArrayList<String> heldSymbol100And5 = new ArrayList<String>(sellMap100And5.keySet());
				ArrayList<String> heldSymbol50And5 = new ArrayList<String>(sellMap50And5.keySet());
				ArrayList<String> heldSymbol100And10 = new ArrayList<String>(sellMap100And10.keySet());
				ArrayList<String> heldSymbol50And10 = new ArrayList<String>(sellMap50And10.keySet());
				highlyVolatileStocks.removeAll(heldSymbol100And5);
				highlyVolatileStocks.removeAll(heldSymbol50And5);
				highlyVolatileStocks.removeAll(heldSymbol100And10);
				highlyVolatileStocks.removeAll(heldSymbol50And10);
				highlyVolatileStocks.addAll(heldSymbol100And5);
				highlyVolatileStocks.addAll(heldSymbol50And5);
				highlyVolatileStocks.addAll(heldSymbol100And10);
				highlyVolatileStocks.addAll(heldSymbol50And10);
//				List<TimeframePojo> multiMonthTime = alpacaAccount.getMultiTimeframe(highlyVolatileStocks.toString().substring(1, highlyVolatileStocks.toString().length()-1).replaceAll(" ", ""), start.minusMonths(3).toString(), start.toString(), "1Month", "");
//				for(TimeframePojo monthTime : multiMonthTime) {
//					BarsPojo[] monthBars = monthTime.getBars();
//					int barsLength = monthBars.length;
//					if(barsLength == 0) continue;
//					Double meanPrice = 0.0;
//
//					for(BarsPojo bar : monthBars) {
//						meanPrice = meanPrice + Double.valueOf(bar.getVwap());
//					}
//					meanPriceMap.put(monthTime.getSymbol(), meanPrice/barsLength) ;
//					lastPriceMap.put(monthTime.getSymbol(), Double.valueOf(monthBars[barsLength-1].getClosePrice()));
//				}
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
				pw1.write("\nExecuting for: " + start + "\n");
				pw2.write("\nExecuting for: " + start+ "\n");
				pw3.write("\nExecuting for: " + start+ "\n");
				pw4.write("\nExecuting for: " + start+ "\n");
				ArrayList<LocalDateTime> sortedKeys= new ArrayList<LocalDateTime>(dailyTimeMap.keySet());
				Collections.sort(sortedKeys);
				for(LocalDateTime sortedKey : sortedKeys) {
					ArrayList<BarsPojo> allSymbolBars = dailyTimeMap.get(sortedKey);
					for(BarsPojo bar: allSymbolBars) {
						lastPriceMap.put(bar.getSymbol(), Double.valueOf(bar.getClosePrice()));
						String symbol = bar.getSymbol();
						Double highPrice = Double.valueOf(bar.getHighPrice());
						Double lowPrice = Double.valueOf(bar.getLowPrice());
						Double vwap = Double.valueOf(bar.getVwap());
						ArrayList<Double> sells100And5 = sellMap100And5.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sells100And10 = sellMap100And10.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sells50And5 = sellMap50And5.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sells50And10 = sellMap50And10.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sellExecuted100And5 = new ArrayList<Double>();
						ArrayList<Double> sellExecuted100And10 = new ArrayList<Double>();
						ArrayList<Double> sellExecuted50And5 = new ArrayList<Double>();
						ArrayList<Double> sellExecuted50And10 = new ArrayList<Double>();

						Double lowestSell100And5 = 100000.0;
						Double lowestSell100And10 = 100000.0;
						Double lowestSell50And5 = 100000.0;
						Double lowestSell50And10 = 100000.0;

						for(Double sell : sells100And5) {
							if(sell.compareTo(lowestSell100And5) < 0 ) lowestSell100And5 = sell;
							if(highPrice.compareTo(sell*profitPercent05) > 0) {
								sellExecuted100And5.add(sell);
								System.out.println("100And5Sold " + symbol + " at: " + sell*profitPercent05 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount100And5);
								pw1.write("100And5Sold " + symbol + " at: " + sell*profitPercent05 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount100And5 + "\n");
								totalSell100And5++;
								totalBuyCount100And5--;
							}
							else if(sortedKey.getMinute() > 49 && sortedKey.getMinute() <= 59 && vwap.compareTo(sell*1.005) > 0){
								sellExecuted100And5.add(sell);
								incompleteSell100And5.add(vwap/sell);
								System.out.println("100And5Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount100And5);
								pw1.write("100And5Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount100And5 + "\n");
								totalSell100And5++;
								totalBuyCount100And5--;
							}
						}
						sells100And5.removeAll(sellExecuted100And5);
						//					sellMap.put(symbol, sells);
						dailyBuyCount100And5 = dailyBuyCount100And5 - sellExecuted100And5.size();
						//					System.out.println("Daily Buy Count: " + dailyBuyCount);
						if(dailyBuyCount100And5 < maxDailyBuyCount100 && totalBuyCount100And5 < maxTotalBuys100 && !(sortedKey.getMinute() > 49 && sortedKey.getMinute() <= 59)) { 
//							Double mean = meanPriceMap.get(symbol);
//							if(lowPrice.compareTo(mean) < 0 && mean.compareTo(maxBuyPrice) < 0 && mean.compareTo(lowestSell100And5*0.82) < 0 ) {
							if(lowPrice.compareTo(lowestSell100And5*0.82) < 0 && lowPrice.compareTo(maxBuyPrice) < 0 ) {
								//						ArrayList<Double> sellList = sellMap.getOrDefault(symbol, new ArrayList<Double>());
								sells100And5.add(vwap);
								dailyBuyCount100And5++;
								totalBuy100And5++;
								totalBuyCount100And5++;
								System.out.println("100And5Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount100And5 + " - " + bar.getTime() + " " + sortedKey);
								pw1.write("100And5Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount100And5 + " - " + bar.getTime() + " " + sortedKey + "\n");
							}
						}
						if(!sells100And5.isEmpty())
							sellMap100And5.put(symbol, sells100And5);
						if(sells100And5.isEmpty())
							sellMap100And5.remove(symbol);

						for(Double sell : sells100And10) {
							if(sell.compareTo(lowestSell100And10) < 0 ) lowestSell100And10 = sell;
							if(highPrice.compareTo(sell*profitPercent10) > 0) {
								sellExecuted100And10.add(sell);
								System.out.println("100And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount100And10);
								pw2.write("100And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount100And10 + "\n");
								totalSell100And10++;
								totalBuyCount100And10--;
							}
							else if(sortedKey.getMinute() > 49 && sortedKey.getMinute() <= 59 && vwap.compareTo(sell*1.005) > 0){
								sellExecuted100And10.add(sell);
								incompleteSell100And10.add(vwap/sell);
								System.out.println("100And10Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey  + " Net Count: " + totalBuyCount100And10);
								pw2.write("100And10Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount100And10 + "\n");
								totalSell100And10++;
								totalBuyCount100And10--;
							}
						}
						sells100And10.removeAll(sellExecuted100And10);
						//					sellMap.put(symbol, sells);
						dailyBuyCount100And10 = dailyBuyCount100And10 - sellExecuted100And10.size();
						//					System.out.println("Daily Buy Count: " + dailyBuyCount);
						if(dailyBuyCount100And10 < maxDailyBuyCount100 && totalBuyCount100And10 < maxTotalBuys100 && !(sortedKey.getMinute() > 49 && sortedKey.getMinute() <= 59)) { 
//							Double mean = meanPriceMap.get(symbol);
//							if(lowPrice.compareTo(mean) < 0 && mean.compareTo(maxBuyPrice) < 0 && mean.compareTo(lowestSell100And10*0.82) < 0 ) {
							if(lowPrice.compareTo(lowestSell100And10*0.82) < 0 && lowPrice.compareTo(maxBuyPrice) < 0) {
								//						ArrayList<Double> sellList = sellMap.getOrDefault(symbol, new ArrayList<Double>());
								sells100And10.add(vwap);
								dailyBuyCount100And10++;
								totalBuy100And10++;
								totalBuyCount100And10++;
								System.out.println("100And10Bought " + symbol + " at: " + vwap + " Low Price: " + 
										lowPrice + " Daily Buy Count: " + dailyBuyCount100And10 + " - " + bar.getTime() + " " + sortedKey);
								pw2.write("100And10Bought " + symbol + " at: " + vwap + " Low Price: " + 
										lowPrice + " Daily Buy Count: " + dailyBuyCount100And10 + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount100And10 +  "\n");
							}
						}
						if(!sells100And10.isEmpty())
							sellMap100And10.put(symbol, sells100And10);
						if(sells100And10.isEmpty())
							sellMap100And10.remove(symbol);

						for(Double sell : sells50And5) {
							if(sell.compareTo(lowestSell50And5) < 0 ) lowestSell50And5 = sell;
							if(highPrice.compareTo(sell*profitPercent05) > 0) {
								sellExecuted50And5.add(sell);
								System.out.println("50And5Sold " + symbol + " at: " + sell*profitPercent05 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And5);
								pw3.write("50And5Sold " + symbol + " at: " + sell*profitPercent05 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And5 + "\n");
								totalSell50And5++;
								totalBuyCount50And5--;
							}
							else if(sortedKey.getMinute() > 49 && sortedKey.getMinute() <= 59 && vwap.compareTo(sell*1.005) > 0){
								sellExecuted50And5.add(sell);
								incompleteSell50And5.add(vwap/sell);
								System.out.println("50And5Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And5);
								pw3.write("50And5Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And5 + "\n");
								totalSell50And5++;
								totalBuyCount50And5--;
							}
						}
						sells50And5.removeAll(sellExecuted50And5);
						//					sellMap.put(symbol, sells);
						dailyBuyCount50And5 = dailyBuyCount50And5 - sellExecuted50And5.size();
						//					System.out.println("Daily Buy Count: " + dailyBuyCount);
						if(dailyBuyCount50And5 < maxDailyBuyCount50 && totalBuyCount50And5 < maxTotalBuys50 && !(sortedKey.getMinute() > 49 && sortedKey.getMinute() <= 59)) {
//							Double mean = meanPriceMap.get(symbol);
//							if(lowPrice.compareTo(mean) < 0 && mean.compareTo(maxBuyPrice) < 0 && mean.compareTo(lowestSell50And5*0.82) < 0 ) {
							if(lowPrice.compareTo(lowestSell50And5*0.82) < 0 && lowPrice.compareTo(maxBuyPrice) < 0) {
								//						ArrayList<Double> sellList = sellMap.getOrDefault(symbol, new ArrayList<Double>());
								sells50And5.add(vwap);
								dailyBuyCount50And5++;
								totalBuy50And5++;
								totalBuyCount50And5++;
								System.out.println("50And5Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And5 + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And5);
								pw3.write("50And5Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And5 + " - " + bar.getTime() + " " + sortedKey  + " Net Count: " + totalBuyCount50And5 + "\n");
							}
						}
						if(!sells50And5.isEmpty())
							sellMap50And5.put(symbol, sells50And5);
						if(sells50And5.isEmpty())
							sellMap50And5.remove(symbol);

						for(Double sell : sells50And10) {
							if(sell.compareTo(lowestSell50And10) < 0 ) lowestSell50And10 = sell;
							if(highPrice.compareTo(sell*profitPercent10) > 0) {
								sellExecuted50And10.add(sell);
								System.out.println("50And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10);
								pw4.write("50And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10 + "\n");
								totalSell50And10++;
								totalBuyCount50And10--;
							}
							else if(sortedKey.getMinute() > 49 && sortedKey.getMinute() <= 59 && vwap.compareTo(sell*1.005) > 0){
								sellExecuted50And10.add(sell);
								incompleteSell50And10.add(vwap/sell);
								System.out.println("50And10Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10);
								pw4.write("50And10Sold " + symbol + " at: " + vwap + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10 + "\n");
								totalSell50And10++;
								totalBuyCount50And10--;
							}
						}
						sells50And10.removeAll(sellExecuted50And10);
						//					sellMap.put(symbol, sells);
						dailyBuyCount50And10 = dailyBuyCount50And10 - sellExecuted50And10.size();
						//					System.out.println("Daily Buy Count: " + dailyBuyCount);
						if(dailyBuyCount50And10 < maxDailyBuyCount50 && totalBuyCount50And10 < maxTotalBuys50 && !(sortedKey.getMinute() > 49 && sortedKey.getMinute() <= 59)) { 
//							Double mean = meanPriceMap.get(symbol);
//							if(lowPrice.compareTo(mean) < 0 && mean.compareTo(maxBuyPrice) < 0 && mean.compareTo(lowestSell50And10*0.82) < 0 ) {
							if(lowPrice.compareTo(lowestSell50And10*0.82) < 0 && lowPrice.compareTo(maxBuyPrice) < 0) {
								//						ArrayList<Double> sellList = sellMap.getOrDefault(symbol, new ArrayList<Double>());
								sells50And10.add(vwap);
								dailyBuyCount50And10++;
								totalBuy50And10++;
								totalBuyCount50And10++;
								System.out.println("50And10Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And10 + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10);
								pw4.write("50And10Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And10 + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10 + "\n");
							}
						}
						if(!sells50And10.isEmpty())
							sellMap50And10.put(symbol, sells50And10);
						if(sells50And10.isEmpty())
							sellMap50And10.remove(symbol);
					}
				}
				if(start.getDayOfWeek() == DayOfWeek.FRIDAY)start = start.plusDays(2);
				start = start.plusDays(1);
				System.out.println("100And5Current Holdings: " + sellMap100And5.entrySet());
				System.out.println("100And10Current Holdings: " + sellMap100And10.entrySet());
				System.out.println("50And5Current Holdings: " + sellMap50And5.entrySet());
				System.out.println("50And10Current Holdings: " + sellMap50And10.entrySet());
				pw1.write("100And5Current Holdings: " + sellMap100And5.entrySet() + "\n");
				pw2.write("100And10Current Holdings: " + sellMap100And10.entrySet() + "\n");
				pw3.write("50And5Current Holdings: " + sellMap50And5.entrySet() + "\n");
				pw4.write("50And10Current Holdings: " + sellMap50And10.entrySet() + "\n");
//				Double holdingPrice = Double.valueOf(dailyTimeMap.get(sortedKeys.get(sortedKeys.size()-1)).get(0).getClosePrice());
				for (Map.Entry<String, ArrayList<Double>> entry : sellMap100And5.entrySet()) {
//					System.out.println(entry.getKey() + "," + holdingPrice);
					Double holdingPrice = lastPriceMap.get(entry.getKey());
					Double totalShares = 0.0;

					for(Double holding : entry.getValue()) {
//						Double buyPrice = holding*0.82;
						totalShares = totalShares + maxBuyPrice/holding;
					}
					holdingValue100And5 = holdingValue100And5 + holdingPrice*totalShares;
					holdingCount100And5 = holdingCount100And5 + entry.getValue().size();
				}

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

				for (Map.Entry<String, ArrayList<Double>> entry : sellMap50And5.entrySet()) {					
//					System.out.println(entry.getKey() + "," + holdingPrice);
					Double holdingPrice = lastPriceMap.get(entry.getKey());
					Double totalShares = 0.0;
					for(Double holding : entry.getValue()) {
//						Double buyPrice = holding*0.82;
						totalShares = totalShares + maxBuyPrice/holding;
					}
					holdingValue50And5 = holdingValue50And5 + holdingPrice*totalShares;
					holdingCount50And5 = holdingCount50And5 + entry.getValue().size();
				}

				for (Map.Entry<String, ArrayList<Double>> entry : sellMap50And10.entrySet()) {
//					System.out.println(entry.getKey() + "," + holdingPrice);
					Double holdingPrice = lastPriceMap.get(entry.getKey());
					Double totalShares = 0.0;
					for(Double holding : entry.getValue()) {
//						Double buyPrice = holding*0.82;
						totalShares = totalShares + maxBuyPrice/holding;
					}
					holdingValue50And10 = holdingValue50And10 + holdingPrice*totalShares;
					holdingCount50And10 = holdingCount50And10 + entry.getValue().size();
				}

				for(Double partialSell : incompleteSell100And5) {
					partialProfit100And5 = partialProfit100And5 + partialSell*maxBuyPrice;
				}
				for(Double partialSell : incompleteSell100And10) {
					partialProfit100And10 = partialProfit100And10 + partialSell*maxBuyPrice;
				}
				for(Double partialSell : incompleteSell50And5) {
					partialProfit50And5 = partialProfit50And5 + partialSell*maxBuyPrice;
				}
				for(Double partialSell : incompleteSell50And10) {
					partialProfit50And10 = partialProfit50And10 + partialSell*maxBuyPrice;
				}
				int currenttotalSell100And5 = totalSell100And5 - incompleteSell100And5.size();
				int currenttotalSell100And10 = totalSell100And10 - incompleteSell100And10.size();
				int currenttotalSell50And5 = totalSell50And5 - incompleteSell50And5.size();
				int currenttotalSell50And10 = totalSell50And10 - incompleteSell50And10.size();
				System.out.println("100And5Total Sell: " + currenttotalSell100And5 + " , Partial Sell: " + incompleteSell100And5.size());
				System.out.println("100And10Total Sell: " + currenttotalSell100And10 + " , Partial Sell: " + incompleteSell100And10.size());
				System.out.println("50And5Total Sell: " + currenttotalSell50And5 + " , Partial Sell: " + incompleteSell50And5.size());
				System.out.println("50And10Total Sell: " + currenttotalSell50And10 + " , Partial Sell: " + incompleteSell50And10.size());

				netProfit100And5 = (currenttotalSell100And5*profitPercent05*maxBuyPrice) + partialProfit100And5 + holdingValue100And5 - (totalBuy100And5*maxBuyPrice);
				netProfit100And10 = (currenttotalSell100And10*profitPercent10*maxBuyPrice) + partialProfit100And10 + holdingValue100And10 - (totalBuy100And10*maxBuyPrice);
				netProfit50And5 = (currenttotalSell50And5*profitPercent05*maxBuyPrice) + partialProfit50And5 + holdingValue50And5 - (totalBuy50And5*maxBuyPrice);
				netProfit50And10 = (currenttotalSell50And10*profitPercent10*maxBuyPrice) + partialProfit50And10 + holdingValue50And10 - (totalBuy50And10*maxBuyPrice);
				
				pw0.write("100And5Total Buy: " + totalBuy100And5*maxBuyPrice + ", Total Sell: " + currenttotalSell100And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And5  + ", Holding Value: " + holdingValue100And5 + ", Holding Count: " + holdingCount100And5 + " , NetProfit: " + netProfit100And5 + ", Liquidation Count: " + liquidationCount100And5 +"\n");
				pw1.write("100And5Total Buy: " + totalBuy100And5*maxBuyPrice + ", Total Sell: " + currenttotalSell100And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And5  + ", Holding Value: " + holdingValue100And5 + ", Holding Count: " + holdingCount100And5 + " , NetProfit: " + netProfit100And5 + ", Liquidation Count: " + liquidationCount100And5 +"\n");
				System.out.println("100And5Total Buy: " + totalBuy100And5*maxBuyPrice + ", Total Sell: " + currenttotalSell100And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And5 + ", Holding Value: " + holdingValue100And5 + ", Holding Count: " + holdingCount100And5 + " , NetProfit: " + netProfit100And5 + ", Liquidation Count: " + liquidationCount100And5 +"\n");
				pw0.write("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + currenttotalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + ", Liquidation Count: " + liquidationCount100And10 +"\n");
				pw2.write("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + currenttotalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + ", Liquidation Count: " + liquidationCount100And10 +"\n");
				System.out.println("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + currenttotalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + ", Liquidation Count: " + liquidationCount100And10 +"\n");
				pw0.write("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + currenttotalSell50And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And5  + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5 + ", Liquidation Count: " + liquidationCount50And5 +"\n");
				pw3.write("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + currenttotalSell50And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And5  + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5 + ", Liquidation Count: " + liquidationCount50And5 +"\n");
				System.out.println("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + currenttotalSell50And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And5  + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5 + ", Liquidation Count: " + liquidationCount50And5 +"\n");
				pw0.write("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + currenttotalSell50And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And10  + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10 + ", Liquidation Count: " + liquidationCount50And10 +"\n");
				pw4.write("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + currenttotalSell50And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And10  + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10 + ", Liquidation Count: " + liquidationCount50And10 +"\n");
				System.out.println("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + currenttotalSell50And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And10  + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10 + ", Liquidation Count: " + liquidationCount50And10 +"\n");

				//Some Constant Profit for now 2% of 40000
				if(netProfit100And5.compareTo(oneTimeProfit) > 0) {
					liquidationCount100And5++;
					pw0.write("100And5Total Buy: " + totalBuy100And5*maxBuyPrice + ", Total Sell: " + currenttotalSell100And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And5  + ", Holding Value: " + holdingValue100And5 + ", Holding Count: " + holdingCount100And5 + " , NetProfit: " + netProfit100And5 + ", Liquidation Count: " + liquidationCount100And5 +"\n");
					pw1.write("100And5Total Buy: " + totalBuy100And5*maxBuyPrice + ", Total Sell: " + currenttotalSell100And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And5  + ", Holding Value: " + holdingValue100And5 + ", Holding Count: " + holdingCount100And5 + " , NetProfit: " + netProfit100And5 + ", Liquidation Count: " + liquidationCount100And5 +"\n");
					System.out.println("100And5Total Buy: " + totalBuy100And5*maxBuyPrice + ", Total Sell: " + currenttotalSell100And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And5 + ", Holding Value: " + holdingValue100And5 + ", Holding Count: " + holdingCount100And5 + " , NetProfit: " + netProfit100And5 + ", Liquidation Count: " + liquidationCount100And5 +"\n");
					pw1.close();
					totalBuy100And5 = 0;
					totalSell100And5 = 0;
					sellMap100And5 = new HashMap<String, ArrayList<Double>>();					
					incompleteSell100And5 = new ArrayList<Double>();
					totalBuyCount100And5 = 0;

					netProfit100And5 = 0.0;
					file1 = new File("logs/output100And5.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getHour());
					pw1 = new PrintWriter(new FileOutputStream(file1), true);
				}
				//Below factors Ill have to reset everyday as they roll over over in the whole map
				holdingValue100And5 = 0.0;
				holdingCount100And5 = 0;					
				partialProfit100And5 = 0.0;
				if(netProfit100And10.compareTo(oneTimeProfit) > 0) {
					if(netProfit100And10.compareTo(oneTimeProfit) > 0)
						liquidationCount100And10++;
					pw0.write("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + currenttotalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + ", Liquidation Count: " + liquidationCount100And10 +"\n");
					pw2.write("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + currenttotalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + ", Liquidation Count: " + liquidationCount100And10 +"\n");
					System.out.println("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + currenttotalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + ", Liquidation Count: " + liquidationCount100And10 +"\n");
					pw2.close();
					totalBuy100And10 = 0;
					totalSell100And10 = 0;
					sellMap100And10 = new HashMap<String, ArrayList<Double>>();
					incompleteSell100And10 = new ArrayList<Double>();
					totalBuyCount100And10 = 0;
					netProfit100And10 = 0.0;
					file2 = new File("logs/output100And10.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getMinute());
					pw2 = new PrintWriter(new FileOutputStream(file2), true);
				}
				holdingValue100And10 = 0.0;
				holdingCount100And10 = 0;					
				partialProfit100And10 = 0.0;
				if(netProfit50And5.compareTo(oneTimeProfit) > 0) {
					if(netProfit50And5.compareTo(oneTimeProfit) > 0)
						liquidationCount50And5++;
					pw0.write("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + currenttotalSell50And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And5  + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5 + ", Liquidation Count: " + liquidationCount50And5 +"\n");
					pw3.write("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + currenttotalSell50And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And5  + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5 + ", Liquidation Count: " + liquidationCount50And5 +"\n");
					System.out.println("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + currenttotalSell50And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And5  + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5 + ", Liquidation Count: " + liquidationCount50And5 +"\n");
					pw3.close();
					totalBuy50And5 = 0;
					totalSell50And5 = 0;
					sellMap50And5 = new HashMap<String, ArrayList<Double>>();
					incompleteSell50And5 = new ArrayList<Double>();
					totalBuyCount50And5 = 0;
					netProfit50And5 = 0.0;
					file3 = new File("logs/output50And5.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getSecond());
					pw3 = new PrintWriter(new FileOutputStream(file3), true);
				}
				holdingValue50And5 = 0.0;
				holdingCount50And5 = 0;					
				partialProfit50And5 = 0.0;
				if(netProfit50And10.compareTo(oneTimeProfit) > 0) {
					if(netProfit50And10.compareTo(oneTimeProfit) > 0)
						liquidationCount50And10++;
					pw0.write("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + currenttotalSell50And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And10  + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10 + ", Liquidation Count: " + liquidationCount50And10 +"\n");
					pw4.write("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + currenttotalSell50And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And10  + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10 + ", Liquidation Count: " + liquidationCount50And10 +"\n");
					System.out.println("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + currenttotalSell50And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And10  + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10 + ", Liquidation Count: " + liquidationCount50And10 +"\n");
					pw4.close();
					totalBuy50And10 = 0;
					totalSell50And10 = 0;
					sellMap50And10 = new HashMap<String, ArrayList<Double>>();
					incompleteSell50And10 = new ArrayList<Double>();
					totalBuyCount50And10 = 0;
					netProfit50And10 = 0.0;
					file4 = new File("logs/output50And10.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getYear());
					pw4 = new PrintWriter(new FileOutputStream(file4), true);
				}
				holdingValue50And10 = 0.0;
				holdingCount50And10 = 0;					
				partialProfit50And10 = 0.0;
			}

			System.out.println("100And5Total Buy: " + totalBuy100And5*maxBuyPrice + ", Total Sell: " + totalSell100And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And5 + ", Holding Value: " + holdingValue100And5 + ", Holding Count: " + holdingCount100And5 + " , NetProfit: " + netProfit100And5);
			System.out.println("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + totalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10);
			System.out.println("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + totalSell50And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And5  + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5);
			System.out.println("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + totalSell50And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And10  + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10);
			pw1.write("100And5Total Buy: " + totalBuy100And5*maxBuyPrice + ", Total Sell: " + totalSell100And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And5  + ", Holding Value: " + holdingValue100And5 + ", Holding Count: " + holdingCount100And5 + " , NetProfit: " + netProfit100And5);
			pw2.write("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + totalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10);
			pw3.write("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + totalSell50And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And5  + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5);
			pw4.write("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + totalSell50And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And10  + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10);

			pw0.write("100And5Total Buy: " + totalBuy100And5*maxBuyPrice + ", Total Sell: " + totalSell100And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And5  + ", Holding Value: " + holdingValue100And5 + ", Holding Count: " + holdingCount100And5 + " , NetProfit: " + netProfit100And5 + "\n");
			pw0.write("100And10Total Buy: " + totalBuy100And10*maxBuyPrice + ", Total Sell: " + totalSell100And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit100And10  + ", Holding Value: " + holdingValue100And10 + ", Holding Count: " + holdingCount100And10 + " , NetProfit: " + netProfit100And10 + "\n");
			pw0.write("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + totalSell50And5*profitPercent05*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And5  + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5 + "\n");
			pw0.write("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + totalSell50And10*profitPercent10*maxBuyPrice + ", Incomplete Sell: " + partialProfit50And10  + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10 + "\n");

			pw1.close();
			pw2.close();
			pw3.close();
			pw4.close();
		}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Exception Caught: " + e);
		}
		pw0.close();
	}
}
