package Backtesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Symbol_Algo_OpenPosition {

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
	private static final String activeVersion = "/v2";
	private static final String inactiveVersion = "/v1";
	private static final String ordersEndpoint = "/orders";
	private static final String accountEndpoint = "/account";
	private static final String lastQuoteEndpoint = "/last_quote/stocks";
	private static final String lastTradeEndpoint = "/last/stocks";
	private static final String positionsEndpoint = "/positions";
	private static final String assetsEndpoint = "/assets";
	private static final String clockEndpoint = "/clock";
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(11,30);
	private boolean isRealTimeTrading = false;
	static Double qty = 100.00;
	static Double initialQty = 100.00;
	static Double pointZeroOneFactor = qty/10000;
	static Double priceToBuy = 0.00;
	static Double priceToSell = 0.0000;
	static Double pointTwoFactor = .998;
	static Double pointOneFactor = 0.999;
	static boolean timeToSell = false;
	static String symbol = "BBY";
	static AlpacaApiCaller alpacaAccount;

	public static void main(String[] args) throws IOException, InterruptedException {

		AlpacaApiCaller alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		OrdersPojo placeOrder;
		OrdersPojo placedOrder;

		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
		File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
//		FileOutputStream fo = new FileOutputStream(file);
		placeOrder = alpacaAccount.buySymbolQuantity(symbol, pointZeroOneFactor);
		placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
		while (!"filled".equals(placedOrder.getStatus()))
		{
			Thread.sleep(500);
			placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
		}
		PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
		pw.write(LocalDateTime.now().toString() + "\t" + openPosition.getSymbol()+ "\t" + openPosition.getCurrent_price()+ "\t" + openPosition.getAvg_entry_price()+ 
				"\t" + openPosition.getLastday_price() + "\t" + openPosition.getMarket_value() + 
				"\t" + openPosition.getSide()+ "\n");

		priceToBuy = Double.valueOf(openPosition.getAvg_entry_price());

		//Start with investing 1000 stocks on any random stock
		if(alpacaAccount.getClock().isIs_open()) {
			do {
				placeOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
				placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
				pw.write("Buy Order Executed with following Details: " + placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
						"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				while (!"filled".equals(placedOrder.getStatus()))
				{
					Thread.sleep(500);
					placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
					pw.write("Order Not filled at: " + "\t" + LocalDateTime.now().toString() + "\n");
				}
				pw.write("Buy Order Filled with following Details: " + placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
						"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.parse(placedOrder.getFilled_at()) + "\t" + LocalDateTime.now().toString() + "\n");
				priceToSell = Double.valueOf(placedOrder.getFilled_avg_price());
				
				do {
					Thread.sleep(1000);
					PositionsPojo latestSellPosition = alpacaAccount.getSymbolPosition(symbol);
					if(Double.valueOf(latestSellPosition.getCurrent_price()).equals(priceToSell) || (Double.valueOf(latestSellPosition.getCurrent_price()).compareTo(priceToSell*pointOneFactor) > 0 && Double.valueOf(latestSellPosition.getCurrent_price()).compareTo(priceToSell) < 0)) {
						pw.write("Price To Sell: " + latestSellPosition.getCurrent_price() + " is same  or greater than caliberated value of last current price: "  
								+ priceToSell*pointOneFactor + "\t" + LocalDateTime.now().toString() + "\t" + priceToSell + "\n");
						continue;
					}
					if(Double.valueOf(latestSellPosition.getCurrent_price()).compareTo(priceToSell) > 0) {
						priceToSell =  Double.valueOf(latestSellPosition.getCurrent_price());
						pw.write("New Price To Sell is : "  + "\t" + LocalDateTime.now().toString() + "\t" + priceToSell + "\t@: " + LocalDateTime.now().toString()  + "\n");
						continue;
					}
					else {
						pw.write("Attempting to Sell with following details: " + "\t" + latestSellPosition.getSymbol()+ "\t" 
								+ latestSellPosition.getCurrent_price() + "\n");
						break;
					}
				}while(LocalDateTime.now().isBefore(marketCloseTime));

				placeOrder = alpacaAccount.sellSymbolQuantity(symbol, qty);
				placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
				pw.write("Sell Order Executed with following Details: " + placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
						"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.parse(placedOrder.getFilled_at()) + 
						"\t" + LocalDateTime.now().toString() + "\n");

				while (!"filled".equals(placedOrder.getStatus()))
				{
					Thread.sleep(500);
					placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
					pw.write("Order Not filled at: " + "\t" + LocalDateTime.now().toString() + "\n");
				}
				pw.write("Sell Order Filled with following Details: " + placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
						"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.parse(placedOrder.getFilled_at()) + "\n");
				priceToBuy = Double.valueOf(placedOrder.getFilled_avg_price()); 
				do {
					Thread.sleep(1000);
					PositionsPojo latestBuyPosition = alpacaAccount.getSymbolPosition(symbol);
					Double caliberatedBuyPrice = Double.valueOf(latestBuyPosition.getCurrent_price())*pointTwoFactor;
					if(Double.valueOf(latestBuyPosition.getCurrent_price()).equals(priceToBuy) || (caliberatedBuyPrice.compareTo(priceToBuy) < 0 && Double.valueOf(latestBuyPosition.getCurrent_price()).compareTo(priceToBuy) > 0)) {
						pw.write("Price To Buy: " + latestBuyPosition.getCurrent_price() + " is same  or lesser than caliberated value of last current price: "  
								+ caliberatedBuyPrice + "\t" + LocalDateTime.now().toString() + "\t" + priceToBuy + "\n");
						continue;
					}
					if(Double.valueOf(latestBuyPosition.getCurrent_price()).compareTo(priceToBuy) < 0) {
						priceToBuy =  Double.valueOf(latestBuyPosition.getCurrent_price());
						pw.write("New Price To Buy : " + "\t" + priceToBuy + "\t@: " + LocalDateTime.now().toString()  + "\n");
						continue;
					}
					else {
						pw.write("Attempting to Buy with following details: " + "\t" + latestBuyPosition.getSymbol()+ "\t" 
								+ latestBuyPosition.getCurrent_price() + "\n");
						break;
					}

				}while(LocalDateTime.now().isBefore(marketCloseTime));
				 
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			pw.close();
		}
	}
}
