package Backtesting.alpaca.volatility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.AssetsPojo;
import alpacaPojo.BarsPojo;
import alpacaPojo.TimeframePojo;

/*
 * This program will take the input and split it into multiple files with daily, weekly and monthly splits
 */
public class ExtractVolatileStocks {

	AlpacaApiCaller alpacaAccount;
	//static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		
	public static void main(String[] args) throws IOException {
		ExtractVolatileStocks demo = new ExtractVolatileStocks();
//		List<String> highlyVolatileStocks = demo.extractVolatileStocks(LocalDate.now().minusMonths(4));
		demo.extractVolatilityCount(demo.extractVolatileStocksFromFile(""), LocalDate.now().minusMonths(12).toString(), LocalDate.now().toString());
	} 

	public void extractVolatilityCount(List<String> volatileStocks, String start, String end) throws IOException {
		File file = new File("logs/volatilityCount" + start + "_" + end + ".txt");
		PrintWriter pw0 = new PrintWriter(new FileOutputStream(file), true);
		pw0.write("Symbol,Spread5,Spread10,Spread15,Spread20,startPrice,endPrice\n");
		this.alpacaAccount = TickerUtils.getAlpacaAccountObject();
		for(String volatileStock : volatileStocks) {
			int spread5Count = 0;
			int spread10Count = 0;
			int spread15Count = 0;
			int spread20Count = 0;
			TimeframePojo dailyBarTime = alpacaAccount.getTimeframe(volatileStock, start, end, "1Day");
			if(dailyBarTime != null) {
				BarsPojo[] dailyBars = dailyBarTime.getBars();
				if(dailyBars == null) continue;
				for(BarsPojo dailyBar : dailyBars) {
					Double spread = (Double.valueOf(dailyBar.getHighPrice()) - Double.valueOf(dailyBar.getLowPrice()))/Double.valueOf(dailyBar.getVwap());
					Double spreadPercent = spread*100;
					if(spreadPercent.compareTo(7.0) > 0) spread5Count++;
					if(spreadPercent.compareTo(10.0) > 0) spread10Count++;
					if(spreadPercent.compareTo(15.0) > 0) spread15Count++;
					if(spreadPercent.compareTo(20.0) > 0) spread20Count++;
				}
				System.out.println(volatileStock + "," + spread5Count + "," + spread10Count + "," + spread15Count + 
						"," + spread20Count + "," + dailyBars[0].getVwap() + "," + dailyBars[dailyBars.length-1].getVwap());
				pw0.write(volatileStock + "," + spread5Count + "," + spread10Count + "," + spread15Count + 
						"," + spread20Count + "," + dailyBars[0].getVwap() + "," + dailyBars[dailyBars.length-1].getVwap() + "\n");
			}
		}
		pw0.close();
	}
	public List<String> extractVolatileStocks(LocalDate endDate) {

		//String end = LocalDate.now().toString();
		String end = endDate.toString();

		List<String> highlyVolatileStocks = new ArrayList<String>();
		try {

			this.alpacaAccount = TickerUtils.getAlpacaAccountObject();
			AssetsPojo[] assets = alpacaAccount.getAssets();
			Properties alpacaConfig = TickerUtils.loadProperties(TickerConstants.alpacaConfig);
//			int volatilityMonths = alpacaConfig.getProperty(TickerConstants.volatilityMonths) == null ? 3 : Integer.parseInt(alpacaConfig.getProperty(TickerConstants.volatilityMonths));
//			int volatilityCount = alpacaConfig.getProperty(TickerConstants.volatilityCount) == null ? 30 : Integer.parseInt(alpacaConfig.getProperty(TickerConstants.volatilityCount));
//			Double volatilitySpread = alpacaConfig.getProperty(TickerConstants.volatilitySpread) == null ? 7 : Double.valueOf(alpacaConfig.getProperty(TickerConstants.volatilitySpread));
			int volatilityMonths = 2;
			String start = endDate.minusMonths(volatilityMonths).toString();
			int volatilityCount =21;
			Double volatilitySpread = 7.0;
			
			HashMap<String, Integer> volatilityDayCount = new HashMap<String, Integer>();
			String symbols = null;
			List<String> tempStocks = new ArrayList<String>();
			for(int i = 0 ; i < assets.length; i++) {
				if(!"active".equals(assets[i].getStatus())) continue;
				tempStocks.add(assets[i].getSymbol());
				//Send 100 stocks at a time for getting bars
				if(i % 600 == 0 && i != 0) {
					symbols = tempStocks.toString().substring(1, tempStocks.toString().length()-1).replaceAll(" ", "");
					List<TimeframePojo> timeframes = alpacaAccount.getMultiTimeframe(symbols, start, end, "1Day", "");
					if(timeframes == null || timeframes.size() == 0) {
						System.out.println("Timeframe Response is empty at " + LocalDateTime.now().toString());
						tempStocks.clear();
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
						Double highestPrice = 0.0;
						for(BarsPojo bar : bars) {
							Double highPrice = Double.valueOf(bar.getHighPrice());
							if(highestPrice.compareTo(highPrice) < 0) {
								highestPrice = highPrice;
							}
							Double lowPrice = Double.valueOf(bar.getLowPrice());
							Double dailySpread = (highPrice-lowPrice)*100/lowPrice;
							if(dailySpread.compareTo(volatilitySpread) > 0) {
								int volCount = volatilityDayCount.getOrDefault(symbol, 0);
								volCount++;
								volatilityDayCount.put(symbol, volCount);
							}
						}
					}
					tempStocks.clear();
				}
			}
//			System.out.println("Highly Volatile Stocks for Period of: " + start + " - " + end);

			for(Map.Entry<String, Integer> entry : volatilityDayCount.entrySet()) {
				if(entry.getValue() >= volatilityCount) {
//					System.out.println(entry.getKey() + " " + entry.getValue());
					highlyVolatileStocks.add(entry.getKey());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return highlyVolatileStocks;
	}
	public List<String> extractVolatileStocksFromFile(String file) throws IOException {
		List<String> highlyVolatileStocks = new ArrayList<String>();
//		Properties volatileProp = TickerUtils.loadProperties(TickerConstants.dtSwitchVolatileConfig);
		Properties volatileProp = TickerUtils.loadProperties("resources/fixedContinuousStocks.csv");
		for(Object key : volatileProp.keySet()) {
			highlyVolatileStocks.add(key.toString());
		}
		return highlyVolatileStocks;
	}
	public List<String> extractHourlyVolatileStocks() {

		String end = LocalDate.now().toString();

		List<String> highlyVolatileStocks = new ArrayList<String>();
		try {

			this.alpacaAccount = TickerUtils.getAlpacaAccountObject();
			AssetsPojo[] assets = alpacaAccount.getAssets();
			Properties alpacaConfig = TickerUtils.loadProperties(TickerConstants.alpacaConfig);
			//			int volatilityMonths = alpacaConfig.getProperty(TickerConstants.volatilityMonths) == null ? 3 : Integer.parseInt(alpacaConfig.getProperty(TickerConstants.volatilityMonths));
			int volatilityCountHourly = alpacaConfig.getProperty(TickerConstants.volatilityCount) == null ? 80 : Integer.parseInt(alpacaConfig.getProperty(TickerConstants.volatilityCount));
			Double volatilitySpreadHourly = alpacaConfig.getProperty(TickerConstants.volatilitySpread) == null ? 3 : Double.valueOf(alpacaConfig.getProperty(TickerConstants.volatilitySpread));
			String start = LocalDate.now().minusMonths(1).toString();

			HashMap<String, Integer> volatilityHourCount = new HashMap<String, Integer>();
			//HashMap<String, Integer> volatileButNotGrowing = new HashMap<String, Integer>();
			String symbols = null;
			List<String> tempStocks = new ArrayList<String>();
			for(int i = 0 ; i < assets.length; i++) {
				if(!"active".equals(assets[i].getStatus())) continue;
				tempStocks.add(assets[i].getSymbol());
				//Send 100 stocks at a time for getting bars
				if(i % 100 == 0) {
					symbols = tempStocks.toString().substring(1, tempStocks.toString().length()-1).replaceAll(" ", "");
					List<TimeframePojo> timeframes = alpacaAccount.getMultiTimeframe(symbols, start, end, "1Hour", "");
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
						for(BarsPojo bar : bars) {
							Double highPrice = Double.valueOf(bar.getHighPrice());
							Double lowPrice = Double.valueOf(bar.getLowPrice());
							Double hourlySpread = (highPrice-lowPrice)*100/lowPrice;
							if(hourlySpread.compareTo(volatilitySpreadHourly) > 0) {
								int volCount = volatilityHourCount.getOrDefault(symbol, 0);
								volCount++;
								volatilityHourCount.put(symbol, volCount);
							}
						}
					}
					tempStocks.clear();
				}
			}
			System.out.println("Highly Volatile Stocks for Period of: " + start + " - " + end);

			for(Map.Entry<String, Integer> entry : volatilityHourCount.entrySet()) {
				if(entry.getValue() >= volatilityCountHourly) {
//					System.out.println(entry.getKey() + "," + entry.getValue() + ",Y");
					highlyVolatileStocks.add(entry.getKey());
				}
				else {
//					System.out.println(entry.getKey() + "," + entry.getValue() + ",N");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return highlyVolatileStocks;
	}

}

