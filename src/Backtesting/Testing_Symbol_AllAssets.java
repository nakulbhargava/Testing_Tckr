package Backtesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import Backtesting.alpaca.volatility.TickerUtils;
import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.AssetsPojo;
import alpacaPojo.BarsPojo;
import alpacaPojo.OrdersPojo;
import alpacaPojo.TimeframePojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Symbol_AllAssets {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	private static final String alpacaKeyValue = "PKSOGC8HQ3NBEPT88Q34";
	private static final String secretIdValue = "6h0AQc3wdqRfdZjXi1NvlQJEuCuEsgDGXiwROhY8";
	private static final String alpacaKeyRealTime = "PKSOGC8HQ3NBEPT88Q34";
	private static final String secretIdRealTime = "6h0AQc3wdqRfdZjXi1NvlQJEuCuEsgDGXiwROhY8";
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

	public static void main(String[] args) throws IOException, InterruptedException {

		AlpacaApiCaller alpacaAccount = TickerUtils.getAlpacaAccountObject();

		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());

		AssetsPojo assets[] = alpacaAccount.getAssets();
		File file = new File("C:\\Users\\nakul\\Documents\\outputAssets - " + localTime + ".txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
		//		FileOutputStream fo = new FileOutputStream(file);
		int datList = assets.length;
		pw.write("Exchange" + "," + "Symbol" + "," + "Status" + "," + "Fractionable" + "," + "Marginable" + "," + "Shortable" + "," + "ETB" + "," + "Tradable" + "\n");
		for (int i = 0; i < datList; i++){
			pw.write(assets[i].getExchange() + "," + assets[i].getSymbol() + "," + assets[i].getStatus() + ","
					+ assets[i].isFractionable() + "," + assets[i].isMarginable() + "," + assets[i].isShortable() + "," 
					+ assets[i].isEasy_to_borrow() + "," + assets[i].isTradable()  + "\n");
		}
		File fileTrue = new File("C:\\Users\\nakul\\Documents\\outputTrueAssets - " + localTime + ".txt");
		PrintWriter pw1 = new PrintWriter(new FileOutputStream(fileTrue));
		//		FileOutputStream fo1 = new FileOutputStream(fileTrue);
		int assetTotalList = assets.length;
		pw1.write("Exchange" + "," + "Symbol" + "," + "Status" + "," + "Fractionable" + "," + "Marginable" + "," + "Shortable" + "," + "ETB" + "," + "Tradable" + "\n");
		for (int i = 0; i < assetTotalList; i++){
			if("active".equals(assets[i].getStatus()) && assets[i].isFractionable() && assets[i].isMarginable() && assets[i].isShortable() && assets[i].isEasy_to_borrow() && assets[i].isTradable()) {
				TimeframePojo frame = alpacaAccount.getTimeframe(assets[i].getSymbol(), LocalDate.now().minusDays(0).toString(), LocalDate.now().minusDays(0).toString(), "1Day");
				BarsPojo bar = frame.getBars() != null && frame.getBars().length != 0 ? frame.getBars()[0] : null; 
				if(bar != null) {
					pw1.write(assets[i].getExchange() + "," + assets[i].getSymbol() + "," + assets[i].getStatus() + ","
							+ assets[i].isFractionable() + "," + assets[i].isMarginable() + "," + assets[i].isShortable() + "," 
							+ assets[i].isEasy_to_borrow() + "," + assets[i].isTradable() + "," + bar.getTime() + "," + bar.getOpenPrice()  + "," + bar.getClosePrice()  + "," + bar.getHighPrice()  + "," + bar.getLowPrice() + "," + bar.getTradeCount() + "," + bar.getVolume() + "\n");
					System.out.println(assets[i].getExchange() + "," + assets[i].getSymbol() + "," + assets[i].getStatus() + ","
							+ assets[i].isFractionable() + "," + assets[i].isMarginable() + "," + assets[i].isShortable() + "," 
							+ assets[i].isEasy_to_borrow() + "," + assets[i].isTradable() + "," + bar.getTime() + "," + bar.getOpenPrice()  + "," + bar.getClosePrice()  + "," + bar.getHighPrice()  + "," + bar.getLowPrice() + "," + bar.getTradeCount() + "," + bar.getVolume());
				}
			}
		}
		pw.close();
		pw1.close();
	}
}
