package REAL_Enhanced;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import alpacaPojo.AlpacaApiCaller;

//This is enhanced lazy limit strategy
public class Buy_Single_Account_Strategy_UBER {

	private static final String alpacaKey = ConfigConstants.alpacaKey;
	private static final String secretId = ConfigConstants.secretId;

	//Nakul
	//		private static final String alpacaKeyValue = ConfigConstants.alpacaKeyValueNakulPaper;
	//		private static final String secretIdValue = ConfigConstants.secretIdValueNakulPaper;

	//Vaishali
	//		private static final String alpacaKeyValue = ConfigConstants.alpacaKeyValueVaishaliPaper;
	//		private static final String secretIdValue = ConfigConstants.secretIdValueVaishaliPaper;

	//Distorted
	private static final String alpacaKeyValue = ConfigConstants.alpacaKeyValueDistPaper;
	private static final String secretIdValue = ConfigConstants.secretIdValueDistPaper;

	private static final String alpacaKeyRealTime = ConfigConstants.alpacaKeyRealTimeNakul;
	private static final String secretIdRealTime = ConfigConstants.secretIdRealTimeNakul;
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

	static Double qty = 0.05;
	static Double maxQty = 57.0;

	//Below to be extracted from Extract Calibrating factor 5
	static Double profitFactor = 1.044;
	static Double upTrendFactor = 1.0005;
	static Double downTrendFactor = .9955;

	static String symbol = "UBER";

	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\outputReal - " + symbol + " - " + localTime + ".txt");

	//Below value to be driven from ExtractingHighLowTimeframe
	static Double stableHighPrice = ConfigConstants.UBER_stablePrice;
	public static void main(String[] args) throws IOException, InterruptedException {

		AlpacaCommonClass alpacaBaap = new AlpacaCommonClass();
		
		alpacaBaap.alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		alpacaBaap.pw = new PrintWriter(new FileOutputStream(file), true);
		alpacaBaap.qty = qty;
		alpacaBaap.maxQty = maxQty;
		alpacaBaap.profitFactor = profitFactor;
		alpacaBaap.upTrendFactor = upTrendFactor;
		alpacaBaap.downTrendFactor = downTrendFactor;
		alpacaBaap.symbol = symbol;
		alpacaBaap.stableHighPrice = stableHighPrice;
		alpacaBaap.performContinousBuyOperations();		
	}
}