package Strategies;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

public class Sinusodial_Strategy_Final{

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	private static final String alpacaKeyValue = "PKN9AR7BFFZXH5X3MP6H";
	private static final String secretIdValue = "daalp83963QKdcmOqI7iaqBwkdZxFRdFAmcijLZH";
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

	static Double qty = 10.00;
	//static Double stopLossPrice = 0.00;
	static Double pointZeroOneFactor = qty/1000;
	static Double buyProfitFactor = 0.999;
	static Double shortStopLossFactor = 1.009;
	static Double sellProfitFactor = 1.001;
	static Double stopLossFactor = .991;
	static Double upTrendFactor = 1.0025;
	static Double downTrendFactor = .9975;

	static String symbol = "GME";
	
	static AlpacaApiCaller alpacaAccount;
	static int secondsCounter = 0;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;
	static Double localMaxima = 0.00;
	static Double localMinima = 0.00;
	static Double currentPrice = 0.00;
	
	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file));
		fo = new FileOutputStream(file);

		if(alpacaAccount.getClock().isIs_open()) {
			do {
				boolean isUpTrend = isUpTrend();
				if(isUpTrend) {
					followUpTrendWave();
				}
				if(!isUpTrend) {
					followDownTrendWave();
				}
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			pw.close();
		}
	}

	public static boolean isUpTrend() throws IOException, InterruptedException {
		Double testPrice = returnCurrentPrice();

		do {
			Thread.sleep(10000);
			currentPrice = returnCurrentPrice();
			if (testPrice*downTrendFactor > currentPrice)
				return false;
			if ( testPrice*upTrendFactor < currentPrice)
				return true;
		}while(LocalDateTime.now().isBefore(marketCloseTime));
		return false;
	}
	
	public static Double returnCurrentPrice() throws IOException, InterruptedException {
		Double currentPrice = 0.00;
		OrdersPojo placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, pointZeroOneFactor);
		OrdersPojo placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		}
		currentPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
		OrdersPojo placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, pointZeroOneFactor);
		OrdersPojo placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		while (!"filled".equals(placedSellOrder.getStatus()))
		{
			placedSellOrder = alpacaAccount.getOrderDetails(placedSellOrder.getId());
		}
		currentPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());
		return currentPrice;
	}
	public static void followUpTrendWave() throws IOException, InterruptedException {
		PositionsPojo openPosition;
		Double filledBuyPrice = 0.00;
		while(LocalDateTime.now().isBefore(marketCloseTime)) {
			openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (openPosition.getQty() == null) {	
				OrdersPojo buyOrder = placeBuyOrder(symbol, qty);
				if(buyOrder == null)continue;
				
				localMaxima = Double.valueOf(buyOrder.getFilled_avg_price());
				filledBuyPrice = Double.valueOf(buyOrder.getFilled_avg_price());
			}
			else 
			{
				Thread.sleep(1000);
				currentPrice = returnCurrentPrice();
				if(localMaxima.compareTo(currentPrice) < 0)
					localMaxima = currentPrice;
				Double stopLossPrice = localMaxima*stopLossFactor;
				Double takeProfitPrice = filledBuyPrice*sellProfitFactor;
				pw.write("Last Buy Price: " + filledBuyPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				if (stopLossPrice.compareTo(currentPrice)>0 || (takeProfitPrice.compareTo(currentPrice) > 0 && currentPrice.compareTo(filledBuyPrice) > 0)) {
					OrdersPojo sellAllOpenPositions = alpacaAccount.sellSymbolQuantity(symbol, Double.valueOf(openPosition.getQty()));
					while (sellAllOpenPositions.getStatus() !=null && !"filled".equals(sellAllOpenPositions.getStatus()))
					{
						Thread.sleep(100);
						sellAllOpenPositions = alpacaAccount.getOrderDetails(sellAllOpenPositions.getId());
					}
					return;
				}
				Thread.sleep(100);
			}
		}
	}
	public static void followDownTrendWave() throws IOException, InterruptedException {
		PositionsPojo openPosition;
		Double filledSellPrice = 0.00;
		
		while(LocalDateTime.now().isBefore(marketCloseTime)) {
			openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (openPosition.getQty() == null) {	
				OrdersPojo sellOrder = placeSellOrder(symbol, qty);
				if(sellOrder == null) continue;
				
				filledSellPrice = Double.valueOf(sellOrder.getFilled_avg_price());
				localMinima = Double.valueOf(sellOrder.getFilled_avg_price());
			}
			else 
			{
				Thread.sleep(1000);
				openPosition = alpacaAccount.getSymbolPosition(symbol);
				if(openPosition.getQty() == null) continue;
				currentPrice = Double.valueOf(openPosition.getCurrent_price());
				if(localMinima.compareTo(currentPrice) > 0)
					localMinima = currentPrice;
				Double stopLossPrice = localMinima*shortStopLossFactor;
				Double takeProfitPrice = filledSellPrice*buyProfitFactor;
				pw.write("Last Sell Price: " + filledSellPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				if (stopLossPrice.compareTo(currentPrice)<0 || (takeProfitPrice.compareTo(currentPrice) < 0 && currentPrice.compareTo(filledSellPrice) < 0)) {
					OrdersPojo buyAllOpenPositions = alpacaAccount.buySymbolQuantity(symbol, Double.valueOf(openPosition.getQty())*-1);
					while (buyAllOpenPositions.getStatus() !=null && !"filled".equals(buyAllOpenPositions.getStatus()))
					{
						Thread.sleep(100);
						buyAllOpenPositions = alpacaAccount.getOrderDetails(buyAllOpenPositions.getId());
					}
					return;
				}
				Thread.sleep(100);
			}
		}
	}

	public static OrdersPojo placeSellOrder(String symbol, Double qty) throws IOException, InterruptedException {
		OrdersPojo placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, qty);
		if(null == placeSellOrder.getId()) return null;
		OrdersPojo placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		pw.write("Short Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		while (!"filled".equals(placedSellOrder.getStatus()))
		{
			Thread.sleep(100);
			placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		}

		return placedSellOrder;
	}
	
	public static OrdersPojo placeBuyOrder(String symbol, Double qty) throws IOException, InterruptedException {
		OrdersPojo placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
		if(null == placeBuyOrder.getId()) return null;
		OrdersPojo placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{
			Thread.sleep(100);
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());

		}
		return placedBuyOrder;
	}
}