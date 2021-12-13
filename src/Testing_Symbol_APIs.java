import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.AssetsPojo;
import alpacaPojo.LastQuoteV2;
import alpacaPojo.LastTradePojoV2;
import alpacaPojo.LastTradeV2;
import alpacaPojo.SnapshotPojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Symbol_APIs {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";
	private static final String alpacaKeyRealTime = "AK7LQFK01P6RWA5UVNZT";
	private static final String secretIdRealTime = "CZJaF69oPrGsy2i4gvZsiSNNju6Pdtx9Gmh1tdyD";
	private static final String headerContentType = "Content-Type";
	private static final String headerContentTypeValue = "application/json";
	private static final String paperUrl = "https://paper-api.alpaca.markets";
	private static final String realtimeUrl = "https://data.alpaca.markets";
	private static final String dataUrl = "https://data.alpaca.markets";
	private static final String activeVersion = "/v2";
	private static final String inactiveVersion = "/v1";
	private static final String ordersEndpoint = "/orders";
	private static final String accountEndpoint = "/account";
	private static final String lastQuoteEndpoint = "/last_quote/stocks";
	private static final String lastTradeEndpoint = "/last/stocks";
	private static final String positionsEndpoint = "/positions";
	private static final String assetsEndpoint = "/assets";
	private static final String clockEndpoint = "/clock";
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(15,55);
	private boolean isRealTimeTrading = false;
	public static void main(String[] args) throws IOException, InterruptedException {

		AlpacaApiCaller alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, dataUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		String symbol = "ARKK";
		String symbols = "ARKK,AAPL";
		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
		
		AssetsPojo assets[] = alpacaAccount.getAssets();
		String startTime = "2021-05-21T13:35:08Z";
		String endTime = "2021-05-21T13:35:09Z";
		LastQuoteV2 lastQuote = alpacaAccount.getQuoteV2(symbol, startTime, endTime);
		LastTradePojoV2 lastTrade = alpacaAccount.getLastTradeV2(symbol);
		LastTradeV2 trades = alpacaAccount.getTradesV2(symbol, startTime, endTime);
		SnapshotPojo snapshot = alpacaAccount.getSnapshot(symbol);
		SnapshotPojo[] snapshots = alpacaAccount.getSnapshots(symbols);
		File file = new File("C:\\Users\\nakul\\Documents\\outputAssets - " + localTime + ".txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
//		FileOutputStream fo = new FileOutputStream(file);
		int datList = assets.length;
		pw.write("Exchange" + "\t" + "Symbol" + "\t" + "Status" + "\t" + "Fractionable" + "\t" + "Marginable" + "\t" + "Shortable" + "\t" + "ETB" + "\t" + "Tradable" + "\n");
		for (int i = 0; i < datList; i++){
			pw.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t" + assets[i].getStatus() + "\t"
					+ assets[i].isFractionable() + "\t\t" + assets[i].isMarginable() + "\t\t" + assets[i].isShortable() + "\t\t" 
					+ assets[i].isEasy_to_borrow() + "\t" + assets[i].isTradable()  + "\n");
		}
		pw.close();
		File fileTrue = new File("C:\\Users\\nakul\\Documents\\outputTrueAssets - " + localTime + ".txt");
		PrintWriter pw1 = new PrintWriter(new FileOutputStream(fileTrue));
//		FileOutputStream fo1 = new FileOutputStream(fileTrue);
		int assetTotalList = assets.length;
		pw1.write("Exchange" + "\t" + "Symbol" + "\t" + "Status" + "\t" + "Fractionable" + "\t" + "Marginable" + "\t" + "Shortable" + "\t" + "ETB" + "\t" + "Tradable" + "\n");
		for (int i = 0; i < assetTotalList; i++){
			if("active".equals(assets[i].getStatus()) && assets[i].isFractionable() && assets[i].isMarginable() && assets[i].isShortable() && assets[i].isEasy_to_borrow() && assets[i].isTradable()) {
				pw1.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t" + assets[i].getStatus() + "\t"
						+ assets[i].isFractionable() + "\t\t" + assets[i].isMarginable() + "\t\t" + assets[i].isShortable() + "\t\t" 
						+ assets[i].isEasy_to_borrow() + "\t" + assets[i].isTradable()  + "\n");
			}
		}
		pw1.close();
	}
}
