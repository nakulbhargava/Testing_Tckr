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
import java.util.Map.Entry;
import java.util.Properties;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.BarsPojo;
import alpacaPojo.TimeframePojo;

public class BlockVolatileStrategy {

	static PrintWriter pw;
	public static void main(String[] args) throws IOException {
		/*
		 * Get Volatile Stocks, could be daily, hourly
		 * Get the average mean price of volatile stocks, if list was daily then last 3 months average price, if list was hourly then mean price of last 1 month
		 * If for any day the price is below average price then buy
		 * Sell at a constant/random profit
		 * Max buy is configurable for the day
		 */
		LocalDate start = LocalDate.now().minusMonths(55);
		LocalDate end = LocalDate.now();
		AlpacaApiCaller alpacaAccount = TickerUtils.getAlpacaAccountObject();
		File file = new File("logs/outputReport.txt" + start + "_" + end + "_" + LocalDateTime.now().getNano());
		pw = new PrintWriter(new FileOutputStream(file), true);
		pw.write("Symbol,Start Date,End Date,Start Price,Close Price,Net Buy,Net Sell,Holding Value,Holding Count,Max Count,Max Daily Count,Net Profit,Holdings\n");
//		Properties movingStocks = TickerUtils.loadProperties("resources/bestMovingStocks.properties");
//		for(Entry<Object, Object> symbol : movingStocks.entrySet()) {
//			executeBlock(symbol.getKey().toString(), start, end, alpacaAccount);
//		}
		executeBlock("AAPL", start, end, alpacaAccount);
		pw.close();

	}

	private static void executeBlock(String symbol, LocalDate start, LocalDate end, AlpacaApiCaller alpacaAccount) {
		try {
			File file1 = new File("logs/output50And3.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getHour());
			PrintWriter pw1 = new PrintWriter(new FileOutputStream(file1), true);
			//			File file2 = new File("logs/output50And5.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getMinute());
			//			PrintWriter pw2 = new PrintWriter(new FileOutputStream(file2), true);
			File file3 = new File("logs/output50And7.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getSecond());
			PrintWriter pw3 = new PrintWriter(new FileOutputStream(file3), true);
			//			File file4 = new File("logs/output50And10.txt" + LocalDateTime.now().getNano() + LocalDateTime.now().getYear());
			//			PrintWriter pw4 = new PrintWriter(new FileOutputStream(file4), true);
			String startPrice = "", endPrice = "";
			Double maxBuyPrice = 100.0;
			int maxHolding3 = 0;
			int maxHolding5 = 0;
			int maxHolding7 = 0;
			int maxHolding10 = 0;
			int maxDailyBuyCount50 = 50;
			int maxDailyBuyCount3 = 0;
			int maxDailyBuyCount5 = 0;
			int maxDailyBuyCount7 = 0;
			int maxDailyBuyCount10 = 0;
			int totalBuy50And3 = 0;
			int totalSell50And3 = 0;
			int extraBuy3 = 0;
			int extraBuy7 = 0;
			//			int totalBuy50And5 = 0;
			//			int totalSell50And5 = 0;
			int totalBuy50And7 = 0;
			int totalSell50And7 = 0;
			//			int totalBuy50And10 = 0;
			//			int totalSell50And10 = 0;
			//			Double profitPercent10 = 1.01;
			//			Double profitPercent05 = 1.02;
			Double profitPercent03 = 1.015;
			Double profitPercent07 = 1.025;
			DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
			HashMap<String, ArrayList<Double>> sellMap50And3 = new HashMap<String, ArrayList<Double>>();
			//			HashMap<String, ArrayList<Double>> sellMap50And5 = new HashMap<String, ArrayList<Double>>();
			HashMap<String, ArrayList<Double>> sellMap50And7 = new HashMap<String, ArrayList<Double>>();
			//			HashMap<String, ArrayList<Double>> sellMap50And10 = new HashMap<String, ArrayList<Double>>();

			int totalBuyCount50And3 = 0;
			int totalBuyCount50And7 = 0;
			//			int totalBuyCount50And5 = 0;
			//			int totalBuyCount50And10 = 0;
			List<String> highlyVolatileStocks = new ArrayList<String>();
			highlyVolatileStocks.add(symbol);
			//			highlyVolatileStocks.add("TOPS");
			//			highlyVolatileStocks.add("HKD");
			//			highlyVolatileStocks.add("EDBL");
			//			ArrayList<String> highlyVolatileStocksBackUp = new ArrayList<String>();
			LocalDate origStart = start.minusDays(0);
			HashMap<LocalDate, BarsPojo> dayTimeMap = new HashMap<LocalDate, BarsPojo>();
			List<TimeframePojo> multiDayime = alpacaAccount.getMultiTimeframe(highlyVolatileStocks.toString().substring(1, highlyVolatileStocks.toString().length()-1).replaceAll(" ", ""), start.toString(), end.toString(), "1Day", "");
			for(TimeframePojo dayTime : multiDayime) {
				BarsPojo[] dayBars = dayTime.getBars();
				for(BarsPojo bar : dayBars) {
					bar.setSymbol(dayTime.getSymbol());
					LocalDate day = LocalDate.parse(bar.getTime(), FORMATTER);
//					BarsPojo dayBar = dayTimeMap.getOrDefault(day, new BarsPojo());
					dayTimeMap.put(day, bar);
				}
			}
			while(start.isBefore(end)) {
				int dailyBuyCount50And3 = 0;
				int dailyBuyCount50And7 = 0;
				//				int dailyBuyCount50And5 = 0;
				//				int dailyBuyCount50And10 = 0;

				HashMap<LocalDateTime, ArrayList<BarsPojo>> dailyTimeMap = new HashMap<LocalDateTime, ArrayList<BarsPojo>>();
				List<TimeframePojo> multiDailyTime = alpacaAccount.getMultiTimeframe(highlyVolatileStocks.toString().substring(1, highlyVolatileStocks.toString().length()-1).replaceAll(" ", ""), start.toString(), start.toString(), "1Min", "");
				for(TimeframePojo dailyTime : multiDailyTime) {
					BarsPojo[] dailyBars = dailyTime.getBars();
					for(BarsPojo bar : dailyBars) {
						bar.setSymbol(dailyTime.getSymbol());
						LocalDateTime minuteTime = LocalDateTime.parse(bar.getTime(), FORMATTER);
//						if(TickerUtils.isDaylightSaving(minuteTime) && (minuteTime.toLocalTime().isAfter(TickerConstants.dayLightSavingsDayEnd) || minuteTime.toLocalTime().isBefore(TickerConstants.dayLightSavingsDayStart))) {
//							continue;
//						}
//						if(!TickerUtils.isDaylightSaving(minuteTime) && (minuteTime.toLocalTime().isAfter(TickerConstants.nonDaylightSavingsDayEnd) || minuteTime.toLocalTime().isBefore(TickerConstants.nonDaylightSavingsDayStart))) {
//							continue;
//						}
						ArrayList<BarsPojo> minuteListSoFar = dailyTimeMap.getOrDefault(minuteTime, new ArrayList<BarsPojo>());
						minuteListSoFar.add(bar);
						dailyTimeMap.put(minuteTime, minuteListSoFar);
					}
				}

				//				System.out.println("Executing for: " + start);
				pw1.write("\nExecuting for: " + start + "\n");
				//				pw2.write("\nExecuting for: " + start+ "\n");
				//				pw3.write("\nExecuting for: " + start+ "\n");
				//				pw4.write("\nExecuting for: " + start+ "\n");
				ArrayList<LocalDateTime> sortedKeys= new ArrayList<LocalDateTime>(dailyTimeMap.keySet());
				Collections.sort(sortedKeys);
				for(LocalDateTime sortedKey : sortedKeys) {
					ArrayList<BarsPojo> allSymbolBars = dailyTimeMap.get(sortedKey);
					for(BarsPojo bar: allSymbolBars) {
						if(startPrice == null || "".equals(startPrice)) startPrice = bar.getOpenPrice();
						endPrice = bar.getClosePrice();
						//						String symbol = bar.getSymbol();
						Double highPrice = Double.valueOf(bar.getHighPrice());
						Double lowPrice = Double.valueOf(bar.getLowPrice());
						Double vwap = Double.valueOf(bar.getVwap());
						ArrayList<Double> sells50And3 = sellMap50And3.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sells50And7 = sellMap50And7.getOrDefault(symbol, new ArrayList<Double>());
						//						ArrayList<Double> sells50And5 = sellMap50And5.getOrDefault(symbol, new ArrayList<Double>());
						//						ArrayList<Double> sells50And10 = sellMap50And10.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sellExecuted50And3 = new ArrayList<Double>();
						ArrayList<Double> sellExecuted50And7 = new ArrayList<Double>();
						//						ArrayList<Double> sellExecuted50And5 = new ArrayList<Double>();
						//						ArrayList<Double> sellExecuted50And10 = new ArrayList<Double>();

						Double lowestSell50And3 = 10000.0;
						Double lowestSell50And7 = 10000.0;
						//						Double lowestSell50And5 = 10000.0;
						//						Double lowestSell50And10 = 10000.0;

						for(Double sell : sells50And3) {
							if(sell.compareTo(lowestSell50And3) < 0 ) lowestSell50And3 = sell;
							if(highPrice.compareTo(sell*profitPercent03) > 0) {
								sellExecuted50And3.add(sell);
								System.out.println("50and3Sold " + symbol + " at: " + sell*profitPercent03 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And3);
								pw1.write("50and3Sold " + symbol + " at: " + sell*profitPercent03 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And3 + "\n");
								totalSell50And3++;
								totalBuyCount50And3--;
							}
						}
						sells50And3.removeAll(sellExecuted50And3);
						//					sellMap.put(symbol, sells);
						dailyBuyCount50And3 = dailyBuyCount50And3 - sellExecuted50And3.size();
						if(dailyBuyCount50And3 < 0) dailyBuyCount50And3 =0;
						//					System.out.println("Daily Buy Count: " + dailyBuyCount);
						LocalDate currentDay = sortedKey.toLocalDate();
						int i = 1;
						while(dayTimeMap.get(currentDay.minusDays(i)) == null && currentDay.minusDays(i).isAfter(origStart)) i++;
						Double prevDayClose = dayTimeMap.get(currentDay.minusDays(i)) == null ? 10000.0 : Double.valueOf(dayTimeMap.get(currentDay.minusDays(i)).getClosePrice());
						i++;
						while(dayTimeMap.get(currentDay.minusDays(i)) == null && currentDay.minusDays(i).isAfter(origStart)) i++;
						Double prevPrevDayClose = dayTimeMap.get(currentDay.minusDays(i)) == null ? 10000.0 : Double.valueOf(dayTimeMap.get(currentDay.minusDays(i)).getClosePrice());
						i++;
						while(dayTimeMap.get(currentDay.minusDays(i)) == null && currentDay.minusDays(i).isAfter(origStart)) i++;
						Double prev3DayClose = dayTimeMap.get(currentDay.minusDays(i)) == null ? 10000.0 : Double.valueOf(dayTimeMap.get(currentDay.minusDays(i)).getClosePrice());

//						if(vwap.compareTo(prevDayClose*1.2) > 0 || vwap.compareTo(prevPrevDayClose*1.2) > 0 || vwap.compareTo(prev3DayClose*1.2) > 0) {
//							System.out.println(symbol + " - Skipping " + sortedKey + " as the price is greater than 20% from prev day close!" + prevDayClose + "," + prevPrevDayClose + "," + vwap);
//							continue;
//						}
						
						boolean buyExtra = false;
//						if(vwap.compareTo(prevDayClose/1.1) < 0 || vwap.compareTo(prevPrevDayClose/1.1) < 0 || vwap.compareTo(prev3DayClose/1.1) < 0) {
//							buyExtra = true;
//						}
						if(dailyBuyCount50And3 < maxDailyBuyCount50 && isBlockCheck(symbol, vwap, sells50And3)  && vwap.compareTo(lowPrice) > 0 ) { 
							sells50And3.add(vwap);
							dailyBuyCount50And3++;
							if(maxDailyBuyCount3 < dailyBuyCount50And3) maxDailyBuyCount3 = dailyBuyCount50And3;
							totalBuy50And3++;
							totalBuyCount50And3++;
							if(maxHolding3 < totalBuyCount50And3) maxHolding3 = totalBuyCount50And3;
							if(buyExtra) {
								sells50And3.add(vwap);
								dailyBuyCount50And3++;
								if(maxDailyBuyCount3 < dailyBuyCount50And3) maxDailyBuyCount3 = dailyBuyCount50And3;
								totalBuy50And3++;
								totalBuyCount50And3++;
								if(maxHolding3 < totalBuyCount50And3) maxHolding3 = totalBuyCount50And3;
								extraBuy3++;
							}
							System.out.println("50And3Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And3 + " - " + bar.getTime() + " " + sortedKey);
							pw1.write("50And3Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And3 + " - " + bar.getTime() + " " + sortedKey + "\n");
						}
						if(!sells50And3.isEmpty())
							sellMap50And3.put(symbol, sells50And3);
						if(sells50And3.isEmpty())
							sellMap50And3.remove(symbol);

						for(Double sell : sells50And7) {
							if(sell.compareTo(lowestSell50And7) < 0 ) lowestSell50And7 = sell;
							if(highPrice.compareTo(sell*profitPercent07) > 0) {
								sellExecuted50And7.add(sell);
								System.out.println("50And7Sold " + symbol + " at: " + sell*profitPercent07 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And7);
								pw3.write("50And7Sold " + symbol + " at: " + sell*profitPercent07 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And7 + "\n");		
								totalSell50And7++;
								totalBuyCount50And7--;
							}
						}
						sells50And7.removeAll(sellExecuted50And7);
						//					sellMap.put(symbol, sells);
						dailyBuyCount50And7 = dailyBuyCount50And7 - sellExecuted50And7.size();
						if(dailyBuyCount50And7 < 0) dailyBuyCount50And7 =0;
						//					System.out.println("Daily Buy Count: " + dailyBuyCount);
						if(dailyBuyCount50And7 < maxDailyBuyCount50 && isBlockCheck(symbol, vwap, sells50And7)  && vwap.compareTo(lowPrice) > 0 ) { 
							sells50And7.add(vwap);
							dailyBuyCount50And7++;
							if(maxDailyBuyCount7 < dailyBuyCount50And7) maxDailyBuyCount7 = dailyBuyCount50And7;
							totalBuy50And7++;
							totalBuyCount50And7++;
							if(maxHolding7 < totalBuyCount50And7) maxHolding7 = totalBuyCount50And7;
							if(buyExtra) {
								sells50And7.add(vwap);
								dailyBuyCount50And7++;
								if(maxDailyBuyCount7 < dailyBuyCount50And7) maxDailyBuyCount7 = dailyBuyCount50And7;
								totalBuy50And7++;
								totalBuyCount50And7++;
								if(maxHolding7 < totalBuyCount50And7) maxHolding7 = totalBuyCount50And7;
								extraBuy7++;
							}
							System.out.println("50And7Bought " + symbol + " at: " + vwap + " Low Price: " + 
									lowPrice + " Daily Buy Count: " + dailyBuyCount50And7 + " - " + bar.getTime() + " " + sortedKey);
							pw3.write("50And7Bought " + symbol + " at: " + vwap + " Low Price: " + 
									lowPrice + " Daily Buy Count: " + dailyBuyCount50And7 + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And7 +  "\n");
						}
						if(!sells50And7.isEmpty())
							sellMap50And7.put(symbol, sells50And7);
						if(sells50And7.isEmpty())
							sellMap50And7.remove(symbol);
						//
						//						for(Double sell : sells50And5) {
						//							if(sell.compareTo(lowestSell50And5) < 0 ) lowestSell50And5 = sell;
						//							if(highPrice.compareTo(sell*profitPercent05) > 0) {
						//								sellExecuted50And5.add(sell);
						//								System.out.println("50And5Sold " + symbol + " at: " + sell*profitPercent05 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And5);
						//								pw2.write("50And5Sold " + symbol + " at: " + sell*profitPercent05 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And5 + "\n");
						//								totalSell50And5++;
						//								totalBuyCount50And5--;
						//							}
						//						}
						//						sells50And5.removeAll(sellExecuted50And5);
						//						//					sellMap.put(symbol, sells);
						//						dailyBuyCount50And5 = dailyBuyCount50And5 - sellExecuted50And5.size();
						//						if(dailyBuyCount50And5 < 0) dailyBuyCount50And5 =0;
						//						//					System.out.println("Daily Buy Count: " + dailyBuyCount);
						//						if(dailyBuyCount50And5 < maxDailyBuyCount50 && isBlockCheck(symbol, vwap, sells50And5)) {
						//							sells50And5.add(vwap);
						//							dailyBuyCount50And5++;
						//							if(maxDailyBuyCount5 < dailyBuyCount50And5) maxDailyBuyCount5 = dailyBuyCount50And5;
						//							totalBuy50And5++;
						//							totalBuyCount50And5++;
						//							if(maxHolding5 < totalBuyCount50And5) maxHolding5 = totalBuyCount50And5;
						//							System.out.println("50And5Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And5 + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And5);
						//							pw2.write("50And5Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And5 + " - " + bar.getTime() + " " + sortedKey  + " Net Count: " + totalBuyCount50And5 + "\n");
						//						}
						//						if(!sells50And5.isEmpty())
						//							sellMap50And5.put(symbol, sells50And5);
						//						if(sells50And5.isEmpty())
						//							sellMap50And5.remove(symbol);
						//
						//						for(Double sell : sells50And10) {
						//							if(sell.compareTo(lowestSell50And10) < 0 ) lowestSell50And10 = sell;
						//							if(highPrice.compareTo(sell*profitPercent10) > 0) {
						//								sellExecuted50And10.add(sell);
						//								System.out.println("50And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10);
						//								pw4.write("50And10Sold " + symbol + " at: " + sell*profitPercent10 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10 + "\n");
						//								totalSell50And10++;
						//								totalBuyCount50And10--;
						//							}
						//						}
						//						sells50And10.removeAll(sellExecuted50And10);
						//						//					sellMap.put(symbol, sells);
						//						dailyBuyCount50And10 = dailyBuyCount50And10 - sellExecuted50And10.size();
						//						if(dailyBuyCount50And10 < 0) dailyBuyCount50And10 =0;
						//						//					System.out.println("Daily Buy Count: " + dailyBuyCount);
						//						if(dailyBuyCount50And10 < maxDailyBuyCount50 && isBlockCheck(symbol, vwap, sells50And10)) { 
						//							sells50And10.add(vwap);
						//							dailyBuyCount50And10++;
						//							if(maxDailyBuyCount10 < dailyBuyCount50And10) maxDailyBuyCount10 = dailyBuyCount50And10;
						//							totalBuy50And10++;
						//							totalBuyCount50And10++;
						//							if(maxHolding10 < totalBuyCount50And10) maxHolding10 = totalBuyCount50And10;
						//							System.out.println("50And10Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And10 + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10);
						//							pw4.write("50And10Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " Daily Buy Count: " + dailyBuyCount50And10 + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And10 + "\n");
						//						}
						//						if(!sells50And10.isEmpty())
						//							sellMap50And10.put(symbol, sells50And10);
						//						if(sells50And10.isEmpty())
						//							sellMap50And10.remove(symbol);
					}
				}
				if(start.getDayOfWeek() == DayOfWeek.FRIDAY)start = start.plusDays(2);
				start = start.plusDays(1);
				//				System.out.println("50And3Current Holdings: " + sellMap50And3.entrySet());
				//				System.out.println("50And7Current Holdings: "  + sellMap50And7.entrySet());
				//				System.out.println("50And5Current Holdings: " + sellMap50And5.entrySet());
				//				System.out.println("50And10Current Holdings: " + sellMap50And10.entrySet());
				pw1.write("50And3Current Holdings: " + sellMap50And3.entrySet() + "\n");
				pw3.write("50And7Current Holdings: " + sellMap50And7.entrySet() + "\n");
				//				pw2.write("50And5Current Holdings: " + sellMap50And5.entrySet() + "\n");
				//				pw4.write("50And10Current Holdings: " + sellMap50And10.entrySet() + "\n");
				for(Entry<String, ArrayList<Double>> entry : sellMap50And3.entrySet()) {
					//					System.out.println(entry.getKey() + "," + entry.getValue().size());
					pw1.write(entry.getKey() + "," + entry.getValue().size() + "\n");
				}
				//				for(Entry<String, ArrayList<Double>> entry : sellMap50And7.entrySet()) {
				//					System.out.println(entry.getKey() + "," + entry.getValue().size());
				//					pw2.write(entry.getKey() + "," + entry.getValue().size() + "\n");
				//				}
				//				for(Entry<String, ArrayList<Double>> entry : sellMap50And7.entrySet()) {
				//					System.out.println(entry.getKey() + "," + entry.getValue().size());
				//					pw3.write(entry.getKey() + "," + entry.getValue().size() + "\n");
				//				}
				//				for(Entry<String, ArrayList<Double>> entry : sellMap50And10.entrySet()) {
				//					System.out.println(entry.getKey() + "," + entry.getValue().size());
				//					pw4.write(entry.getKey() + "," + entry.getValue().size() + "\n");
				//				}
			}

			Double holdingValue50And3 = 0.0;
			Double holdingValue50And7 = 0.0;
			//			Double holdingValue50And5 = 0.0;
			//			Double holdingValue50And10 = 0.0;

			int holdingCount50And3 = 0;
			int holdingCount50And7 = 0;
			//			int holdingCount50And5 = 0;
			//			int holdingCount50And10 = 0;
			HashMap<String, Double> lastPriceMap = new HashMap<String,Double>();
			List<TimeframePojo> lastPrice = alpacaAccount.getMultiTimeframe(highlyVolatileStocks.toString().substring(1, highlyVolatileStocks.toString().length()-1).replaceAll(" ", ""), end.minusDays(3).toString(), end.toString(), "1Day", "");
			for(TimeframePojo lastDayFrame : lastPrice) {
				//				String symbol = lastDayFrame.getSymbol();
				BarsPojo lastDayBar= lastDayFrame.getBars()[0];
				lastPriceMap.put(symbol, Double.valueOf(lastDayBar.getClosePrice()));
			}
			for (Map.Entry<String, ArrayList<Double>> entry : sellMap50And3.entrySet()) {
				Double holdingPrice = lastPriceMap.get(entry.getKey());
				//				System.out.println(entry.getKey() + "," + holdingPrice);
				Double totalShares = 0.0;

				for(Double holding : entry.getValue()) {
					//					Double buyPrice = holding*0.82;
					totalShares = totalShares + maxBuyPrice/holding;
				}
				holdingValue50And3 = holdingValue50And3 + holdingPrice*totalShares;
				holdingCount50And3 = holdingCount50And3 + entry.getValue().size();
			}

			for (Map.Entry<String, ArrayList<Double>> entry : sellMap50And7.entrySet()) {
				Double holdingPrice = lastPriceMap.get(entry.getKey());
				System.out.println(entry.getKey() + "," + holdingPrice);
				Double totalShares = 0.0;
				for(Double holding : entry.getValue()) {
					//					Double buyPrice = holding*0.82;
					totalShares = totalShares + maxBuyPrice/holding;
				}
				holdingValue50And7 = holdingValue50And7 + holdingPrice*totalShares;
				holdingCount50And7 = holdingCount50And7 + entry.getValue().size();
			}
			//
			//			for (Map.Entry<String, ArrayList<Double>> entry : sellMap50And5.entrySet()) {
			//				Double holdingPrice = lastPriceMap.get(entry.getKey());
			//				System.out.println(entry.getKey() + "," + holdingPrice);
			//				Double totalShares = 0.0;
			//				for(Double holding : entry.getValue()) {
			//					//					Double buyPrice = holding*0.82;
			//					totalShares = totalShares + maxBuyPrice/holding;
			//				}
			//				holdingValue50And5 = holdingValue50And5 + holdingPrice*totalShares;
			//				holdingCount50And5 = holdingCount50And5 + entry.getValue().size();
			//			}
			//
			//			for (Map.Entry<String, ArrayList<Double>> entry : sellMap50And10.entrySet()) {
			//				Double holdingPrice = lastPriceMap.get(entry.getKey());
			//				System.out.println(entry.getKey() + "," + holdingPrice);
			//				Double totalShares = 0.0;
			//				for(Double holding : entry.getValue()) {
			//					//					Double buyPrice = holding*0.82;
			//					totalShares = totalShares + maxBuyPrice/holding;
			//				}
			//				holdingValue50And10 = holdingValue50And10 + holdingPrice*totalShares;
			//				holdingCount50And10 = holdingCount50And10 + entry.getValue().size();
			//			}


			Double netProfit50And3 = (totalSell50And3*profitPercent03*maxBuyPrice) + holdingValue50And3 - (totalBuy50And3*maxBuyPrice);
			Double netProfit50And7 = (totalSell50And7*profitPercent07*maxBuyPrice) + holdingValue50And7 - (totalBuy50And7*maxBuyPrice);
			//			Double netProfit50And5 = (totalSell50And5*profitPercent05*maxBuyPrice) + holdingValue50And5 - (totalBuy50And5*maxBuyPrice);
			//			Double netProfit50And10 = (totalSell50And10*profitPercent10*maxBuyPrice) + holdingValue50And10 - (totalBuy50And10*maxBuyPrice);

			System.out.println("50And3Total Buy: " + totalBuy50And3*maxBuyPrice + ", Total Sell: " + totalSell50And3*profitPercent03*maxBuyPrice + ", Holding Value: " + holdingValue50And3 + ", Holding Count: " + holdingCount50And3 + " , NetProfit: " + netProfit50And3);
			System.out.println("50And7Total Buy: " + totalBuy50And7*maxBuyPrice + ", Total Sell: " + totalSell50And7*profitPercent07*maxBuyPrice + ", Holding Value: " + holdingValue50And7 + ", Holding Count: " + holdingCount50And7 + " , NetProfit: " + netProfit50And7);
			//			System.out.println("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + totalSell50And5*profitPercent05*maxBuyPrice + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5);
			//			System.out.println("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + totalSell50And10*profitPercent10*maxBuyPrice + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10);
			pw1.write("50And3Total Buy: " + totalBuy50And3*maxBuyPrice + ", Total Sell: " + totalSell50And3*profitPercent03*maxBuyPrice + ", Holding Value: " + holdingValue50And3 + ", Holding Count: " + holdingCount50And3 + " , NetProfit: " + netProfit50And3);
			pw3.write("50And7Total Buy: " + totalBuy50And7*maxBuyPrice + ", Total Sell: " + totalSell50And7*profitPercent07*maxBuyPrice + ", Holding Value: " + holdingValue50And7 + ", Holding Count: " + holdingCount50And7 + " , NetProfit: " + netProfit50And7);
			//			pw2.write("50And5Total Buy: " + totalBuy50And5*maxBuyPrice + ", Total Sell: " + totalSell50And5*profitPercent05*maxBuyPrice + ", Holding Value: " + holdingValue50And5 + ", Holding Count: " + holdingCount50And5 + " , NetProfit: " + netProfit50And5);
			//			pw4.write("50And10Total Buy: " + totalBuy50And10*maxBuyPrice + ", Total Sell: " + totalSell50And10*profitPercent10*maxBuyPrice + ", Holding Value: " + holdingValue50And10 + ", Holding Count: " + holdingCount50And10 + " , NetProfit: " + netProfit50And10);
			System.out.println("Max Daily Buy Count: " + maxDailyBuyCount3 + "," + maxDailyBuyCount5 + "," + maxDailyBuyCount7 + "," + maxDailyBuyCount10);
			System.out.println("Max Holding Count: " + maxHolding3 + "," + maxHolding5 + "," + maxHolding7 + "," + maxHolding10);
			pw.write(symbol + "," + origStart + "," + end + "," + startPrice + "," + endPrice + "," + totalBuy50And3 + "," + totalSell50And3 + "," + holdingValue50And3 + "," + holdingCount50And3 + "," + maxHolding3 + "," + maxDailyBuyCount3 + "," + netProfit50And3 + "," + extraBuy3 + "," + profitPercent03 + "\n");
			pw.write(symbol + "," + origStart + "," + end + "," + startPrice + "," + endPrice + "," + totalBuy50And7 + "," + totalSell50And7 + "," + holdingValue50And7 + "," + holdingCount50And7 + "," + maxHolding7 + "," + maxDailyBuyCount7 + "," + netProfit50And7 + "," + extraBuy7 + "," + profitPercent07 + "\n");
			System.out.println(symbol + "," + origStart + "," + end + "," + startPrice + "," + endPrice + "," + totalBuy50And7 + "," + totalSell50And7 + "," + holdingValue50And7 + "," + holdingCount50And7 + "," + maxHolding7 + "," + maxDailyBuyCount7 + "," + netProfit50And7 + "," + sellMap50And7.entrySet() + "\n");
			System.out.println(symbol + "," + origStart + "," + end + "," + startPrice + "," + endPrice + "," + totalBuy50And3 + "," + totalSell50And3 + "," + holdingValue50And3 + "," + holdingCount50And3 + "," + maxHolding3 + "," + maxDailyBuyCount3 + "," + netProfit50And3 + "," + sellMap50And3.entrySet());
			pw1.close();
			//			pw2.close();
			pw3.close();
			//			pw4.close();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Exception Caught: " + e);
		}
	}
	private static boolean isBlockCheck(String symbol, Double lastPrice, ArrayList<Double> listOfBuys) {
		boolean isOnePercentAway = true;
		int orderCountPerBlock = 20;
		Double maxSellPrice = 0.0;
		if(listOfBuys.size() == 0) return true;
		int currentBlockNumber = listOfBuys.size()/orderCountPerBlock;
		for(Double buyPrice : listOfBuys ) {
			if(buyPrice.compareTo(maxSellPrice) > 0) maxSellPrice = Double.valueOf(buyPrice);
			if(lastPrice.compareTo(buyPrice*.98) > 0 && lastPrice.compareTo(buyPrice*1.02) < 0) isOnePercentAway = false;
		}
		//		System.out.println(symbol + " - Blocks: " + maxSellPrice*0.9 + "," +  maxSellPrice*0.8 + "," + maxSellPrice*0.7 + "," + maxSellPrice*0.6 + "," +
		//				maxSellPrice*0.5 + "," + maxSellPrice*0.4 + "," + maxSellPrice*0.3 + "," + maxSellPrice*0.2 + "," + maxSellPrice*0.1);
		Double higherBlockLowPrice  = maxSellPrice*(1 - currentBlockNumber/10);
		if((lastPrice.compareTo(higherBlockLowPrice) < 0 || currentBlockNumber == 0) && isOnePercentAway){
			//			System.out.println(symbol + " - Block Check Passed, " + "Higher Block Low Price: " + higherBlockLowPrice + ", Last Price: " + lastPrice + ", ListOfBuys: " + listOfBuys);
			return true;
		}
		else {
			//			System.out.println(symbol + " - Block Check Failed, " + "Higher Block Low Price: " + higherBlockLowPrice + ", Last Price: " + lastPrice + ", ListOfBuys: " + listOfBuys);
			return false;
		}
	}
}
