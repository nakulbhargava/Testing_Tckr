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
import alpacaPojo.AssetsPojo;
import alpacaPojo.BarsPojo;
import alpacaPojo.TimeframePojo;

public class BlockVolatileDTSDailyRefreshMidCap {

	static PrintWriter pw;
	static PrintWriter pwMega;
	public static void main(String[] args) throws IOException {
		/*
		 * Get Volatile Stocks, could be daily, hourly
		 * Get the average mean price of volatile stocks, if list was daily then last 3 months average price, if list was hourly then mean price of last 1 month
		 * If for any day the price is below average price then buy
		 * Sell at a constant/random profit
		 * Max buy is configurable for the day
		 */
		File fileMega = new File("logs/outputMegaReport.txt" + LocalDateTime.now().getNano());
		pwMega = new PrintWriter(new FileOutputStream(fileMega), true);
		pwMega.write("Symbol,Start Date,End Date,Start Price,Close Price,Net Buy,Net Sell,Holding Value,Max Count,Net Profit,PP,isGrowing,ActualPP,PositiveProfit\n");

		for(int numberOfDays = 1; numberOfDays < 100; numberOfDays++) {
			LocalDate start = LocalDate.now().minusDays(numberOfDays);
			if(start.getDayOfWeek().equals(DayOfWeek.SATURDAY) || start.getDayOfWeek().equals(DayOfWeek.SUNDAY))continue;
			LocalDate prevDay = start;
			if(start.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
				prevDay = prevDay.minusDays(3);
			}
			else {
				prevDay = prevDay.minusDays(1);
			}
			AlpacaApiCaller alpacaAccount = TickerUtils.getAlpacaAccountObject();		
			List<String> movingStocks = extractVolatileStocks(prevDay, prevDay, alpacaAccount);
			File file = new File("logs/outputReport.txt" + start + "_" + LocalDateTime.now().getNano());
			pw = new PrintWriter(new FileOutputStream(file), true);
			pw.write("Symbol,Start Date,End Date,Start Price,Close Price,Net Buy,Net Sell,Holding Value,Max Count,Net Profit,PP,isGrowing,ActualPP,PositiveProfit\n");
			for(String symbol : movingStocks) {
				executeBlock(symbol, start, start.plusDays(1), alpacaAccount);
			}
			pw.close();
		}
		pwMega.close();
		//		File file = new File("logs/outputReport.txt" + start + "_" + end + "_" + LocalDateTime.now().getNano());
		//		pw = new PrintWriter(new FileOutputStream(file), true);
		//		pw.write("Symbol,Start Date,End Date,Start Price,Close Price,Net Buy,Net Sell,Holding Value,Max Count,Net Profit,PP,isGrowing,ActualPP,PositiveProfit\n");
		//		executeBlock("GOTU", start, end, alpacaAccount);
		//		pw.close();

	}

	public static List<String> extractVolatileStocks(LocalDate start, LocalDate end, AlpacaApiCaller alpacaAccount){
		List<String> highlyVolatileStocks = new ArrayList<String>();
		DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		try {
			AssetsPojo[] assets = alpacaAccount.getAssets();
			int volatilityCountMin = 30;
			Double volatilitySpreadMin = 0.75;

			HashMap<String, Integer> volatilityMinCount = new HashMap<String, Integer>();
			String symbols = null;
			String tempSymbols = null;
			List<String> tempStocks = new ArrayList<String>();
			for(int i = 0 ; i < assets.length; i++) {
				if(!"active".equals(assets[i].getStatus())) continue;
				tempStocks.add(assets[i].getSymbol());
				//Send 100 stocks at a time for getting bars
				if(i % 250 == 0) {
					tempSymbols = tempStocks.toString().substring(1, tempStocks.toString().length()-1).replaceAll(" ", "");
					List<TimeframePojo> timeframes = alpacaAccount.getMultiTimeframe(tempSymbols, start.toString(), end.toString(), "1Day", "");
					if(timeframes == null || timeframes.size() == 0) {
						System.out.println("Timeframe Response is empty at " + LocalDateTime.now().toString());
						continue;
					}

					for(TimeframePojo timeframe : timeframes) {
						String symbol = timeframe.getSymbol();

						if(timeframe.getBars() == null || timeframe.getBars().length == 0) {
							//returning exception
							System.out.println(symbol + "\t- Unable to get the Bars for timeframe: " + start + " " + end);
							continue;
						}
						BarsPojo[] bars = timeframe.getBars();
						Double openPrice = Double.valueOf(bars[0].getOpenPrice());
						if(openPrice.compareTo(21.0) < 0 || openPrice.compareTo(51.0) > 0 ) continue;
						if(symbols == null) symbols = symbol;
						else symbols = symbols + "," + symbol;
					}
					tempStocks.clear();
				}
			}
			System.out.println("Highly Volatile Stocks for Period of: " + start + " - " + end);

			List<TimeframePojo> timeframes = alpacaAccount.getMultiTimeframe(symbols, start.toString(), end.toString(), "1Min", "");

			for(TimeframePojo timeframe : timeframes) {
				String symbol = timeframe.getSymbol();

				if(timeframe.getBars() == null || timeframe.getBars().length == 0) {
					//returning exception
					System.out.println(symbol + "\t- Unable to get the Bars for timeframe: " + start + " " + end);
					continue;
				}
				BarsPojo[] bars = timeframe.getBars();
				Double openPrice = Double.valueOf(bars[0].getOpenPrice());
				for(BarsPojo bar : bars) {
					LocalDateTime minuteTime = LocalDateTime.parse(bar.getTime(), FORMATTER);
					if(TickerUtils.isDaylightSaving(minuteTime) && (minuteTime.toLocalTime().isAfter(TickerConstants.dayLightSavingsDayEnd) || minuteTime.toLocalTime().isBefore(TickerConstants.dayLightSavingsDayStart))) {
						continue;
					}
					if(!TickerUtils.isDaylightSaving(minuteTime) && (minuteTime.toLocalTime().isAfter(TickerConstants.nonDaylightSavingsDayEnd) || minuteTime.toLocalTime().isBefore(TickerConstants.nonDaylightSavingsDayStart))) {
						continue;
					}
					Double highPrice = Double.valueOf(bar.getHighPrice());
					Double lowPrice = Double.valueOf(bar.getLowPrice());
					Double hourlySpread = (highPrice-lowPrice)*100/lowPrice;
					if(hourlySpread.compareTo(volatilitySpreadMin) > 0) {
						int volCount = volatilityMinCount.getOrDefault(symbol, 0);
						volCount++;
						volatilityMinCount.put(symbol, volCount);
					}
				}
			}
			tempStocks.clear();
			for(Map.Entry<String, Integer> entry : volatilityMinCount.entrySet()) {
				if(entry.getValue() >= volatilityCountMin) {
					System.out.println(entry.getKey() + "," + entry.getValue());
					highlyVolatileStocks.add(entry.getKey());
				}
				else {
					System.out.println(entry.getKey() + "," + entry.getValue());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return highlyVolatileStocks;
	}

	private static void executeBlock(String symbol, LocalDate start, LocalDate end, AlpacaApiCaller alpacaAccount) {
		try {			
			Double maxBuyPrice = 100.0;

			int maxDailyBuyCount50 = 100;
			Double profitPercent03 = 1.01;
			Double profitPercent07 = 1.02;
			DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
			HashMap<String, ArrayList<Double>> sellMap50And3 = new HashMap<String, ArrayList<Double>>();
			HashMap<String, ArrayList<Double>> sellMap50And7 = new HashMap<String, ArrayList<Double>>();
			List<String> highlyVolatileStocks = new ArrayList<String>();
			highlyVolatileStocks.add(symbol);

			while(start.isBefore(end)) {
				String startPrice = "", endPrice = "";
				int totalBuy50And3 = 0;
				int totalSell50And3 = 0;
				int totalBuy50And7 = 0;
				int totalSell50And7 = 0;
				int maxHolding3 = 0;
				int maxHolding7 = 0;
				int totalBuyCount50And3 = 0;
				int totalBuyCount50And7 = 0;

				HashMap<LocalDateTime, ArrayList<BarsPojo>> dailyTimeMap = new HashMap<LocalDateTime, ArrayList<BarsPojo>>();
				List<TimeframePojo> multiDailyTime = alpacaAccount.getMultiTimeframe(highlyVolatileStocks.toString().substring(1, highlyVolatileStocks.toString().length()-1).replaceAll(" ", ""), start.toString(), start.toString(), "1Min", "");
				for(TimeframePojo dailyTime : multiDailyTime) {
					BarsPojo[] dailyBars = dailyTime.getBars();
					for(BarsPojo bar : dailyBars) {
						bar.setSymbol(dailyTime.getSymbol());
						LocalDateTime minuteTime = LocalDateTime.parse(bar.getTime(), FORMATTER);
						if(TickerUtils.isDaylightSaving(minuteTime) && (minuteTime.toLocalTime().isAfter(TickerConstants.dayLightSavingsDayEnd) || minuteTime.toLocalTime().isBefore(TickerConstants.dayLightSavingsDayStart))) {
							continue;
						}
						if(!TickerUtils.isDaylightSaving(minuteTime) && (minuteTime.toLocalTime().isAfter(TickerConstants.nonDaylightSavingsDayEnd) || minuteTime.toLocalTime().isBefore(TickerConstants.nonDaylightSavingsDayStart))) {
							continue;
						}
						ArrayList<BarsPojo> minuteListSoFar = dailyTimeMap.getOrDefault(minuteTime, new ArrayList<BarsPojo>());
						minuteListSoFar.add(bar);
						dailyTimeMap.put(minuteTime, minuteListSoFar);
					}
				}

				ArrayList<LocalDateTime> sortedKeys= new ArrayList<LocalDateTime>(dailyTimeMap.keySet());
				if(sortedKeys.size() == 0) {
					if(start.getDayOfWeek() == DayOfWeek.FRIDAY)start = start.plusDays(2);
					start = start.plusDays(1);continue;
				}
				Collections.sort(sortedKeys);
				for(LocalDateTime sortedKey : sortedKeys) {
					ArrayList<BarsPojo> allSymbolBars = dailyTimeMap.get(sortedKey);
					for(BarsPojo bar: allSymbolBars) {
						if(startPrice == null || "".equals(startPrice)) startPrice = bar.getOpenPrice();
						endPrice = bar.getClosePrice();
						Double highPrice = Double.valueOf(bar.getHighPrice());
						Double lowPrice = Double.valueOf(bar.getLowPrice());
						Double vwap = Double.valueOf(bar.getVwap());
						ArrayList<Double> sells50And3 = sellMap50And3.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sells50And7 = sellMap50And7.getOrDefault(symbol, new ArrayList<Double>());
						ArrayList<Double> sellExecuted50And3 = new ArrayList<Double>();
						ArrayList<Double> sellExecuted50And7 = new ArrayList<Double>();
						Double lowestSell50And3 = 10000.0;
						Double lowestSell50And7 = 10000.0;
						for(Double sell : sells50And3) {
							if(sell.compareTo(lowestSell50And3) < 0 ) lowestSell50And3 = sell;
							if(highPrice.compareTo(sell*profitPercent03) > 0) {
								sellExecuted50And3.add(sell);
								System.out.println("50and3Sold " + symbol + " at: " + sell*profitPercent03 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey + " Net Count: " + totalBuyCount50And3);
								totalSell50And3++;
								totalBuyCount50And3--;
							}
						}
						sells50And3.removeAll(sellExecuted50And3);
						if(vwap.compareTo(lowPrice) < 0) {
							System.out.println("Something wrong with timeframe! " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " - " + bar.getTime());

						}
						if(maxHolding3 < maxDailyBuyCount50 && isBlockCheck(symbol, vwap, sells50And3) && vwap.compareTo(lowPrice) > 0) { 
							sells50And3.add(vwap);
							totalBuy50And3++;
							totalBuyCount50And3++;
							if(maxHolding3 < totalBuyCount50And3) maxHolding3 = totalBuyCount50And3;
							System.out.println("50And3Bought " + symbol + " at: " + vwap + " Low Price: " + lowPrice + " - " + bar.getTime() + " " + sortedKey);
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
								totalSell50And7++;
								totalBuyCount50And7--;
							}
						}
						sells50And7.removeAll(sellExecuted50And7);

						if(maxHolding7 < maxDailyBuyCount50 && isBlockCheck(symbol, vwap, sells50And7)  && vwap.compareTo(lowPrice) > 0 /*&& TickerUtils.calculateUpTrendMinute(uptrendFrame) == 1*/) { 
							sells50And7.add(vwap);

							totalBuy50And7++;
							totalBuyCount50And7++;
							if(maxHolding7 < totalBuyCount50And7) maxHolding7 = totalBuyCount50And7;
							System.out.println("50And7Bought " + symbol + " at: " + vwap + " Low Price: " + 
									lowPrice + " - " + bar.getTime() + " " + sortedKey);
						}
						if(!sells50And7.isEmpty())
							sellMap50And7.put(symbol, sells50And7);
						if(sells50And7.isEmpty())
							sellMap50And7.remove(symbol);
					}
				}
				Double holdingValue50And3 = 0.0;
				Double holdingValue50And7 = 0.0;

				Double holdingPrice = Double.valueOf(dailyTimeMap.get(sortedKeys.get(sortedKeys.size()-2)).get(0).getClosePrice());
				for (Map.Entry<String, ArrayList<Double>> entry : sellMap50And3.entrySet()) {

					Double totalShares = 0.0;

					for(Double holding : entry.getValue()) {
						totalShares = totalShares + maxBuyPrice/holding;
					}
					holdingValue50And3 = holdingValue50And3 + holdingPrice*totalShares;
				}

				for (Map.Entry<String, ArrayList<Double>> entry : sellMap50And7.entrySet()) {
					System.out.println(entry.getKey() + "," + holdingPrice);
					Double totalShares = 0.0;
					for(Double holding : entry.getValue()) {
						//					Double buyPrice = holding*0.82;
						totalShares = totalShares + maxBuyPrice/holding;
					}
					holdingValue50And7 = holdingValue50And7 + holdingPrice*totalShares;
				}
				Double netProfit50And3 = (totalSell50And3*profitPercent03*maxBuyPrice) + holdingValue50And3 - (totalBuy50And3*maxBuyPrice);
				Double netProfit50And7 = (totalSell50And7*profitPercent07*maxBuyPrice) + holdingValue50And7 - (totalBuy50And7*maxBuyPrice);
//				System.out.println("50And3Total Buy: " + totalBuy50And3*maxBuyPrice + ", Total Sell: " + totalSell50And3*profitPercent03*maxBuyPrice + ", Holding Value: " + holdingValue50And3 + " , NetProfit: " + netProfit50And3);
//				System.out.println("50And7Total Buy: " + totalBuy50And7*maxBuyPrice + ", Total Sell: " + totalSell50And7*profitPercent07*maxBuyPrice + ", Holding Value: " + holdingValue50And7 + " , NetProfit: " + netProfit50And7);
				pw.write(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + totalBuy50And3 + "," + totalSell50And3 + "," + holdingValue50And3 + "," + maxHolding3 + "," + netProfit50And3 + "," + profitPercent03 + "," + "\n");
				pw.write(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + totalBuy50And7 + "," + totalSell50And7 + "," + holdingValue50And7 + "," + maxHolding7 + "," + netProfit50And7 + "," + profitPercent07 + "\n");
				pwMega.write(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + totalBuy50And3 + "," + totalSell50And3 + "," + holdingValue50And3 + "," + maxHolding3 + "," + netProfit50And3 + "," + profitPercent03 + "," + "\n");
				pwMega.write(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + totalBuy50And7 + "," + totalSell50And7 + "," + holdingValue50And7 + "," + maxHolding7 + "," + netProfit50And7 + "," + profitPercent07 + "\n");
				System.out.println(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + totalBuy50And7 + "," + totalSell50And7 + "," + holdingValue50And7 + "," + maxHolding7 + "," + netProfit50And7 + "," + sellMap50And7.entrySet() + "\n");
				System.out.println(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + totalBuy50And3 + "," + totalSell50And3 + "," + holdingValue50And3 + "," + maxHolding3 + "," + netProfit50And3 + "," + sellMap50And3.entrySet());
				sellMap50And3.clear();
				sellMap50And7.clear();
				if(start.getDayOfWeek() == DayOfWeek.FRIDAY)start = start.plusDays(2);
				start = start.plusDays(1);
			}
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
		Double higherBlockLowPrice  = maxSellPrice*(1 - currentBlockNumber/10);
		if((lastPrice.compareTo(higherBlockLowPrice) < 0 || currentBlockNumber == 0) && isOnePercentAway){
			return true;
		}
		else {
			return false;
		}
	}
}
