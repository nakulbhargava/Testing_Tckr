package Backtesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import REAL_Enhanced.ConfigConstants;
import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.PositionsPojo;

public class PreMarketList {
	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	//PP
	private static final String alpacaKeyValue = "PKSSSGEQK3GXOMY3J6O0";
	private static final String secretIdValue = "OzGxMmmEYJVvv65jYbG08cSaPJ3MqE41UkI2AI0U";

	//Nakul
	//	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	//	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";

	private static final String alpacaKeyRealTime = "";
	private static final String secretIdRealTime = "";
	private static final String headerContentType = "Content-Type";
	private static final String headerContentTypeValue = "application/json";
	private static final String paperUrl = "https://paper-api.alpaca.markets";
	private static final String realtimeUrl = "https://data.alpaca.markets";
	private static final String activeVersion = "/v2";
	private static final String inactiveVersion = "/v1";
	private static final String ordersEndpoint = "/orders";
	private static final String accountEndpoint = "/account";
	private static final String lastQuoteEndpoint = "/last_quote/stocks";
	private static final String lastTradeEndpoint = "/last/stocks";
	private static final String positionsEndpoint = "/positions";
	private static final String assetsEndpoint = "/assets";
	private static final String clockEndpoint = "/clock";
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(14,59);

	static AlpacaApiCaller alpacaAccount;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\outputPreMarketTicks - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;

	public static void main(String[] args) throws IOException, InterruptedException, IllegalArgumentException, IllegalAccessException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file, true));
		fo = new FileOutputStream(file, true);
		DecimalFormat df = new DecimalFormat("#.####");
		PositionsPojo[] openPositions = alpacaAccount.getOpenPositions();
		HashMap<String, TickerDiff> tickerPrices = new HashMap<String, TickerDiff>();
		Field[] fields = ConfigConstants.class.getDeclaredFields();
		for (Field field : fields) {
			for (PositionsPojo openPosition : openPositions) {
				if(field.getName().contains(openPosition.getSymbol())) {
					Double stablePrice = Double.valueOf(df.format(field.get(null)));
					TickerDiff tickerDiff = new TickerDiff();
					tickerDiff.currentPrice = Double.valueOf(openPosition.getCurrent_price());
					tickerDiff.yesterdayPrice = stablePrice;
					tickerDiff.calculateDifference();
					tickerPrices.put(openPosition.getSymbol(), tickerDiff);
				}
			}
		}

		System.out.println("Symbol	CurrentPrice	YesterdayPrice		Difference");
		pw.write("Symbol	CurrentPrice	YesterdayPrice		Difference\n");
		for (Map.Entry<String,TickerDiff> entry : sortByValue(tickerPrices).entrySet()) {
			System.out.println(entry.getKey() +
					"\t" + entry.getValue().toString());
			pw.write(entry.getKey() +
					"\t" + entry.getValue().toString() + "\n");
		}
		pw.close();
	}
	public static class TickerDiff {
		public Double currentPrice;
		public Double yesterdayPrice;
		public Double difference;	

		@Override
		public String toString() {
			return (currentPrice + "\t\t" + yesterdayPrice + "\t\t\t" + difference);
		}
		public void calculateDifference() {
			difference = 100*(currentPrice - yesterdayPrice)/yesterdayPrice;
		}
	}
	public static HashMap<String, TickerDiff> sortByValue(HashMap<String, TickerDiff> hm)
	{
		// Create a list from elements of HashMap
		List<Map.Entry<String, TickerDiff> > list =
				new LinkedList<Map.Entry<String, TickerDiff> >(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, TickerDiff> >() {
			public int compare(Map.Entry<String, TickerDiff> o1,
					Map.Entry<String, TickerDiff> o2)
			{
				return (o1.getValue().difference).compareTo(o2.getValue().difference);
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, TickerDiff> temp = new LinkedHashMap<String, TickerDiff>();
		for (Map.Entry<String, TickerDiff> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
}

