package REAL_Penny;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import REAL_Enhanced.AlpacaCommonClass;
import REAL_Enhanced.ConfigConstants;
import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.Ticker;

public class EODSellOrders {
	private static final String alpacaKey = ConfigConstants.alpacaKey;
	private static final String secretId = ConfigConstants.secretId;
	//Nakul
	//		private static final String alpacaKeyValue = ConfigConstants.alpacaKeyValueNakulPaper;
	//		private static final String secretIdValue = ConfigConstants.secretIdValueNakulPaper;

	//Vaishali
	private static final String alpacaKeyValue = "";
	private static final String secretIdValue = "";

	//Distorted
	//	private static final String alpacaKeyValue = ConfigConstants.alpacaKeyValueDistPaper;
	//	private static final String secretIdValue = ConfigConstants.secretIdValueDistPaper;

	private static final String alpacaKeyRealTime = ConfigConstants.alpacaKeyRealTimeVaishali;
	private static final String secretIdRealTime = ConfigConstants.secretIdRealTimeVaishali;
	private static final String headerContentType = ConfigConstants.headerContentType;
	private static final String headerContentTypeValue = ConfigConstants.headerContentTypeValue;
	private static final String paperUrl = ConfigConstants.paperUrl;
	private static final String realtimeUrl = ConfigConstants.realtimeUrl;
	private static final String activeVersion = ConfigConstants.activeVersion;
	private static final String inactiveVersion = ConfigConstants.inactiveVersion;
	private static final String ordersEndpoint = ConfigConstants.ordersEndpoint;
	private static final String accountEndpoint = ConfigConstants.accountEndpoint;
	private static final String lastQuoteEndpoint = ConfigConstants.lastQuoteEndpoint;
	private static final String lastTradeEndpoint = ConfigConstants.lastTradeEndpoint;
	private static final String positionsEndpoint = ConfigConstants.positionsEndpoint;
	private static final String assetsEndpoint = ConfigConstants.assetsEndpoint;
	private static final String clockEndpoint = ConfigConstants.clockEndpoint;
	
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\outputRealEOD - " + localTime + ".txt");
	
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		AlpacaCommonClass alpacaBaap = new AlpacaCommonClass();

		alpacaBaap.alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		alpacaBaap.pw = new PrintWriter(new FileOutputStream(file), true);
		List<Ticker> tickers = null;
		EODSellOrders demo = new EODSellOrders();
		tickers = demo.readFileAsList();
		alpacaBaap.placeEODSellOrders(tickers);	
	}
	private List<Ticker> readFileAsList() {
		
		File fileName = new File("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\eod\\EODOrders_V.txt");
		String line;
		List<Ticker> ticks = new ArrayList<Ticker>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			while((line = reader.readLine()) != null) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				Ticker tick = new Ticker();
				tick.symbol = splitLine.get(0);
				tick.qty = splitLine.get(1);
				tick.price = splitLine.get(2);
				ticks.add(tick);
			}
			return ticks;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
