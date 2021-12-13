package REAL_Penny;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import REAL_Enhanced.ConfigConstants;
import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.OrdersPojo;

public class Cancel_All_Open_Orders {

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
//	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(15,00);

	static String symbol = "ISR";

	static AlpacaApiCaller alpacaAccount;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\outputReal - " + symbol + " - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file), true);
		fo = new FileOutputStream(file, true);

		OrdersPojo openOrders[] = alpacaAccount.getRealOpenOrders(symbol);
		if(openOrders != null && openOrders.length !=0) {
			for (OrdersPojo openOrder : openOrders) {
				pw.write(openOrder.toString() + "\n");
				alpacaAccount.deleteRealOrder(openOrder.getId());
			}
		}


		pw.close();
	}
}