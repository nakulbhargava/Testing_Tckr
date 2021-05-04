package TwoAccountPaperVsReal;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

//This is enhanced lazy limit strategy
public class Buy_Single_Account_Strategy {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";

	//Nakul
	//	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	//	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";

	//Vaishali
	//	private static final String alpacaKeyValue = "PKN9AR7BFFZXH5X3MP6H";
	//	private static final String secretIdValue = "daalp83963QKdcmOqI7iaqBwkdZxFRdFAmcijLZH";

	//Distorted
	private static final String alpacaKeyValue = "PKW027OGK8HX515NRG66";
	private static final String secretIdValue = "yoxhLKh4FXtxNl1LLHaUpAtoDkaKHCOHBopo0baS";

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
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(14,59);

	static Double qty = 1.0;
	static Double maxQty = 1000.0;

	static Double pointZeroOneFactor = maxQty/10000;
	
	//Below to be extracted from Extract Calibrating factor 5
	static Double profitFactor = 1.043;
	static Double upTrendFactor = 1.0015;
	static Double downTrendFactor = .9995;

	static String symbol = "SNAP";

	static AlpacaApiCaller alpacaAccount;
	static int secondsCounter = 0;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;

	static Double currentPrice = 0.00;
	static OrdersPojo placeBuyOrder;
	static OrdersPojo placedBuyOrder;
	static OrdersPojo placeSellOrder;
	static OrdersPojo placedSellOrder;
	static Double lastBuyOrderPrice = 0.00;
	static Double lastSellOrderPrice = 0.00;
	//Below value to be driven from ExtractingHighLowTimeframe
	static Double stableHighPrice = 61.6;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file));
		fo = new FileOutputStream(file);

		if(alpacaAccount.getClock().isIs_open()) {
			do {
				boolean isUpTrend = isUpTrend();
				PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
				Double currentQty = Double.valueOf(openPosition.getQty() == null? "0.00":openPosition.getQty());
				if(isUpTrend && maxQty.compareTo(currentQty) > 0 && returnCurrentPrice().compareTo(stableHighPrice) < 0) {
					placeBuyOrderWithLimitSell(symbol, qty);
				}
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			pw.close();
		}
	}

	public static boolean isUpTrend() throws IOException, InterruptedException {
		Double testPrice = returnCurrentPrice();

		do {
			Thread.sleep(1500);
			currentPrice = returnCurrentPrice();
			if (testPrice*downTrendFactor > currentPrice) {
				pw.write("Returning Downtrend to do nothing - Initial Price: " + testPrice + " Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Returning Downtrend to do nothing - Initial Price: " + testPrice + " Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString());
				return false;
			}
			if ( testPrice*upTrendFactor < currentPrice) {
				pw.write("Returning Uptrend to Buy - Initial Price: " + testPrice + " Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Returning Uptrend to Buy - Initial Price: " + testPrice + " Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString());
				return true;
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
		return false;
	}	
	
	public static Double returnCurrentPrice() throws IOException, InterruptedException {
		PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
		if (openPosition.getQty()!=null)
		{
			return Double.valueOf(openPosition.getCurrent_price());
		}
		Double currentPrice = 0.00;
		OrdersPojo placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, pointZeroOneFactor);
		OrdersPojo placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		}
		currentPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
		OrdersPojo placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, pointZeroOneFactor);
		while (placeSellOrder.getId() == null) {
			placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, pointZeroOneFactor);
		}
		OrdersPojo placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		while (!"filled".equals(placedSellOrder.getStatus()))
		{
			placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		}
		currentPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());
		return currentPrice;
	}

	public static OrdersPojo placeBuyOrderWithLimitSell(String symbol, Double qty) throws IOException, InterruptedException {
		placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
		placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Trying to Buy Order: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Trying to Buy Order: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString());
		if (placedBuyOrder.getId() == null) return null;
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{		
			Thread.sleep(100);
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());

		}
		pw.write("Buy Order filled: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Buy Order filled: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString());
		
		lastBuyOrderPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
		placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  lastBuyOrderPrice*profitFactor);
		placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString());
		return placedBuyOrder;
	}
}