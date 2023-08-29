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

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.AssetsPojo;
import alpacaPojo.BarsPojo;
import alpacaPojo.TimeframePojo;

public class DailySpreadBuyDTS {

	static PrintWriter pw;
	public static void main(String[] args) throws IOException {
		/*
		 * Get Volatile Stocks, could be daily, hourly
		 * Get the average mean price of volatile stocks, if list was daily then last 3 months average price, if list was hourly then mean price of last 1 month
		 * If for any day the price is below average price then buy
		 * Sell at a constant/random profit
		 * Max buy is configurable for the day
		 */
		for(int numberOfMonths = 1; numberOfMonths < 3; numberOfMonths++) {
			LocalDate start = LocalDate.now().minusMonths(numberOfMonths);
			LocalDate end = LocalDate.now().minusMonths(numberOfMonths-1);
			AlpacaApiCaller alpacaAccount = TickerUtils.getAlpacaAccountObject();		
			File file = new File("logs/outputReport.txt" + start + "_" + end + "_" + LocalDateTime.now().getNano());
			pw = new PrintWriter(new FileOutputStream(file), true);
			pw.write("Symbol,Start Date,End Date,Start Price,Close Price,Holding Value,Net Profit,PP\n");
			while(start.isBefore(end)) {
				List<String> movingStocks = extractVolatileStocks(start, alpacaAccount);
				if(start.getDayOfWeek() == DayOfWeek.FRIDAY) start = start.plusDays(3);
				else start = start.plusDays(1);
				for(String symbol : movingStocks) {
					executeBlock(symbol, start, alpacaAccount);
				}
			}
			pw.close();
		}
		//		File file = new File("logs/outputReport.txt" + start + "_" + end + "_" + LocalDateTime.now().getNano());
		//		pw = new PrintWriter(new FileOutputStream(file), true);
		//		pw.write("Symbol,Start Date,End Date,Start Price,Close Price,Net Buy,Net Sell,Holding Value,Max Count,Net Profit,PP,isGrowing,ActualPP,PositiveProfit\n");
		//		executeBlock("GOTU", start, end, alpacaAccount);
		//		pw.close();

	}

	public static List<String> extractVolatileStocks(LocalDate start, AlpacaApiCaller alpacaAccount) {
		List<String> highlyVolatileStocks = new ArrayList<String>();
		try {
			AssetsPojo[] assets = alpacaAccount.getAssets();
			HashMap<String, AssetsPojo> assetMap = new HashMap<String, AssetsPojo>();

			String symbols = null;
			List<String> tempStocks = new ArrayList<String>();
			for(int i = 0 ; i < assets.length; i++) {
				if(!"active".equals(assets[i].getStatus())) continue;
				assetMap.put(assets[i].getSymbol(), assets[i]);
				tempStocks.add(assets[i].getSymbol());
				//Send 100 stocks at a time for getting bars
				if(i % 1000 == 0) {
					symbols = tempStocks.toString().substring(1, tempStocks.toString().length()-1).replaceAll(" ", "");
					List<TimeframePojo> timeframes = alpacaAccount.getMultiTimeframe(symbols, start.toString(), start.toString(), "4hour", "");
					if(timeframes == null || timeframes.size() == 0) {
						System.out.println("Timeframe Response is empty at " + LocalDateTime.now().toString());
						continue;
					}

					for(TimeframePojo timeframe : timeframes) {
						String symbol = timeframe.getSymbol();

						if(timeframe.getBars() == null || timeframe.getBars().length == 0) {
							//returning exception
							System.out.println(symbol + "\t- Unable to get the Bars for timeframe: " + start + " " + start);
							continue;
						}
						BarsPojo[] bars = timeframe.getBars();
						Double openPrice = Double.valueOf(bars[0].getOpenPrice());
						Double closePrice = Double.valueOf(bars[bars.length - 1].getClosePrice());
						Double highestPrice = 0.0;
						Double lowestPrice = 10000000000.0;
						Double dailySpread = 0.0;
						Double volume = 0.0;
						for(BarsPojo bar : bars) {
							Double highPrice = Double.valueOf(bar.getHighPrice());
							if(highestPrice.compareTo(highPrice) < 0) {
								highestPrice = highPrice;
							}
							Double lowPrice = Double.valueOf(bar.getLowPrice());
							if(lowestPrice.compareTo(lowPrice) > 0) {
								lowestPrice = lowPrice;
							}
							volume = volume + Double.valueOf(bar.getVolume());
						}
						dailySpread = (highestPrice-lowestPrice)*100/lowestPrice;
						boolean isGreen = closePrice.compareTo(openPrice) > 0 ? true : false;
						if(!isGreen && volume.compareTo(2000000.00) > 0 && assetMap.get(symbol).isEasy_to_borrow() && dailySpread.compareTo(10.0) >0) {
							highlyVolatileStocks.add(symbol);
						}
					}
					tempStocks.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return highlyVolatileStocks;
	}
	private static void executeBlock(String symbol, LocalDate start, AlpacaApiCaller alpacaAccount) {
		try {			
			Double maxBuyPrice = 1000.0;

			Double profitPercent03 = 1.02;
			Double profitPercent07 = 1.04;
			DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

			List<String> highlyVolatileStocks = new ArrayList<String>();
			highlyVolatileStocks.add(symbol);

			String startPrice = "", endPrice = "";

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

				ArrayList<LocalDateTime> sortedKeys= new ArrayList<LocalDateTime>(dailyTimeMap.keySet());
				Collections.sort(sortedKeys);
				boolean bought3 = false;
				boolean bought7 = false;
				boolean sellExecuted50And3 = false;
				boolean sellExecuted50And7 = false;
				Double sells50And3 = 1000000.0, sells50And7 = 1000000.0;
				for(LocalDateTime sortedKey : sortedKeys) {
					ArrayList<BarsPojo> allSymbolBars = dailyTimeMap.get(sortedKey);
					for(BarsPojo bar: allSymbolBars) {
						if(startPrice == null || "".equals(startPrice)) startPrice = bar.getOpenPrice();
						endPrice = bar.getClosePrice();
						Double highPrice = Double.valueOf(bar.getHighPrice());
						Double lowPrice = Double.valueOf(bar.getLowPrice());

						if(sellExecuted50And3 && sellExecuted50And7) break;
						if(highPrice.compareTo(sells50And3*profitPercent03) > 0 && !sellExecuted50And3) {
							sellExecuted50And3 = true;
							System.out.println("50and3Sold " + symbol + " at: " + sells50And3*profitPercent03 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey);
						}
						if(!bought3) { 
							sells50And3 = (highPrice + lowPrice)/2;
							bought3 = true;
							System.out.println("50And3Bought " + symbol + " at: " + sells50And3 + " Low Price: " + lowPrice + " - " + bar.getTime() + " " + sortedKey);
						}

						if(highPrice.compareTo(sells50And7*profitPercent07) > 0 && !sellExecuted50And7) {
							sellExecuted50And7 = true;
							System.out.println("50And7Sold " + symbol + " at: " + sells50And7*profitPercent07 + " High Price: " + highPrice + " - " + bar.getTime() + " " + sortedKey);		
						}

						if(!bought7 /*&& TickerUtils.calculateUpTrendMinute(uptrendFrame) == 1*/) { 
							sells50And7 = (highPrice + lowPrice)/2;
							bought7 = true;
							System.out.println("50And7Bought " + symbol + " at: " + sells50And7 + " Low Price: " + 
									lowPrice + " - " + bar.getTime() + " " + sortedKey);
						}
					}
				}
				Double holdingValue50And3 = 0.0;
				Double holdingValue50And7 = 0.0;

				Double holdingPrice = Double.valueOf(dailyTimeMap.get(sortedKeys.get(sortedKeys.size()-2)).get(0).getClosePrice());
				Double totalShares = 0.0;
				totalShares = maxBuyPrice/sells50And3;
				if(!sellExecuted50And3)holdingValue50And3 = holdingPrice*totalShares;

				totalShares = maxBuyPrice/sells50And7;
				if(!sellExecuted50And7)holdingValue50And7 = holdingValue50And7 + holdingPrice*totalShares;

				Double netProfit50And3 = 0.0, netProfit50And7 = 0.0;
				if(sellExecuted50And3) netProfit50And3 = profitPercent03*maxBuyPrice - maxBuyPrice;
				else netProfit50And3 = holdingValue50And3 - maxBuyPrice;
				if(sellExecuted50And7) netProfit50And7 = profitPercent07*maxBuyPrice - maxBuyPrice;
				else netProfit50And7 = holdingValue50And7 - maxBuyPrice;

				pw.write(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + holdingValue50And3 + "," + netProfit50And3 + "," + profitPercent03 + "," + "\n");
				pw.write(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + holdingValue50And7 + "," + netProfit50And7 + "," + profitPercent07 + "\n");
				System.out.println(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + holdingValue50And7 + "," + netProfit50And7);
				System.out.println(symbol + "," + start + "," + start + "," + startPrice + "," + endPrice + "," + holdingValue50And3 + "," + netProfit50And3);
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Exception Caught: " + e);
		}
	}
}
