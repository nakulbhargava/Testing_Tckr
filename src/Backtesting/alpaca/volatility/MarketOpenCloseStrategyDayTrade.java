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

public class MarketOpenCloseStrategyDayTrade {

	static PrintWriter pw;
	public static void main(String[] args) throws IOException {
		/*
		 * Get Volatile Stocks, could be daily, hourly
		 * Get the average mean price of volatile stocks, if list was daily then last 3 months average price, if list was hourly then mean price of last 1 month
		 * If for any day the price is below average price then buy
		 * Sell at a constant/random profit
		 * Max buy is configurable for the day
		 */

		AlpacaApiCaller alpacaAccount = TickerUtils.getAlpacaAccountObject();		
		Properties movingStocks = TickerUtils.loadProperties("resources/volatileBlockStocks.properties");
		//		Properties movingStocks = TickerUtils.loadProperties("resources/fixedContinuousStocks.csv");
		LocalDate start = LocalDate.now().minusMonths(60).minusDays(0);
		LocalDate end = LocalDate.now().minusMonths(0).plusDays(0);
		File file = new File("logs/outputDayReport.txt" + start + "_" + end + "_" + LocalDateTime.now().getNano());
		pw = new PrintWriter(new FileOutputStream(file), true);
		pw.write("Symbol,Date,Start Price,Close Price, High Price, Low Price\n");
		for(Entry<Object, Object> symbol : movingStocks.entrySet()) {

			executeBlock(symbol.getKey().toString(), start, end, alpacaAccount);

		}
		pw.close();

	}

	private static void executeBlock(String symbol, LocalDate start, LocalDate end, AlpacaApiCaller alpacaAccount) {
		try {
			DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
			HashMap<String, ArrayList<Double>> sellMap50And3 = new HashMap<String, ArrayList<Double>>();

			List<String> highlyVolatileStocks = new ArrayList<String>();
			highlyVolatileStocks.add(symbol);

			HashMap<LocalDateTime, ArrayList<BarsPojo>> dailyTimeMap = new HashMap<LocalDateTime, ArrayList<BarsPojo>>();
			List<TimeframePojo> multiDailyTime = alpacaAccount.getMultiTimeframe(highlyVolatileStocks.toString().substring(1, highlyVolatileStocks.toString().length()-1).replaceAll(" ", ""), start.toString(), end.toString(), "1Day", "");
			for(TimeframePojo dailyTime : multiDailyTime) {
				BarsPojo[] dailyBars = dailyTime.getBars();
				for(BarsPojo bar : dailyBars) {
					LocalDateTime dayTime = LocalDateTime.parse(bar.getTime(), FORMATTER);
					pw.write(dailyTime.getSymbol() + "," + dayTime + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + "," + bar.getHighPrice() + "," + bar.getLowPrice()+ "\n");
					System.out.println(dailyTime.getSymbol() + "," + dayTime + "," + bar.getOpenPrice() + "," + bar.getClosePrice());
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Exception Caught: " + e);
		}
	}
}
