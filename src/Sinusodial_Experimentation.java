import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

public class Sinusodial_Experimentation {

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
	//private boolean isRealTimeTrading = false;
	//	static boolean buyFraction = true;
	//	static Double safeShortQty = 12.00;
	//	static Double safeShortBuyingPrice = 0.00;
	//	static Double safeShortSellingPrice = 0.00;
	static Double qty = 10.00;
	static Double maxQty = qty*10;
	//static Double maxSellQty = (qty*10) - 0.1;
	//	static Double shortQty = 0.00;
	static Double pointZeroOneFactor = qty/1000;
	static Double pointOneFactor = 0.999;
	//static Double pointTwoFactor = .998;
	static Double onePointFourFactor = 1.004;
	static Double onePointOneFactor = 1.001;
	static Double pointFourFactor = .996;
	static boolean timeToSell = false;
	static String symbol = "GME";
	static AlpacaApiCaller alpacaAccount;
	static OrdersPojo placeBuyOrder;
	static OrdersPojo placedBuyOrder;
	static OrdersPojo placeSellOrder;
	static OrdersPojo placedSellOrder;
	static int secondsCounter = 0;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;
	static Double lastBuyOrderPrice = 0.00;
	static Double lastSellOrderPrice = 0.00;
	static Double upTrendFactor = 1.002;
	static Double downTrendFactor = .998;

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
				if(!isUpTrend && LocalDateTime.now().isBefore(marketCloseTime)) {
					followDownTrendWave();
				}
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			pw.close();
		}
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

	public static boolean isUpTrend() throws IOException, InterruptedException {
		Double testPrice = returnCurrentPrice();
		Double currentPrice;
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
	public static void followUpTrendWave() throws IOException, InterruptedException {
		do {
			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
				OrdersPojo order = null;
				do{
					order = placeBuyOrderWithLimitSell(symbol, qty);
				}
				while (order == null);
				Thread.sleep(1000);
				secondsCounter = 0;
			}

			else 
			{
				if(secondsCounter < 30){
					Thread.sleep(1000);
					secondsCounter ++;
					continue;
				}
				//Cancel all the open Orders for the symbol
				OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
				for(OrdersPojo openSymbolOrder:openSymbolOrders) {
					alpacaAccount.deleteOrder(openSymbolOrder.getId());
					pw.write("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
							"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					OrdersPojo canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
					while(!"canceled".equals(canceledOrder.getStatus())) {
						if("filled".equals(canceledOrder.getStatus())) break;
						canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
						pw.write("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
								"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					}
				}
				PositionsPojo openBuyPosition = alpacaAccount.getSymbolPosition(symbol);
				OrdersPojo sellAllOpenPositions = alpacaAccount.sellSymbolQuantity(symbol, Double.valueOf(openBuyPosition.getQty()));
				while (sellAllOpenPositions.getStatus() !=null && !"filled".equals(sellAllOpenPositions.getStatus()))
				{
					Thread.sleep(100);
					sellAllOpenPositions = alpacaAccount.getOrderDetails(sellAllOpenPositions.getId());
				}
				return;
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
	}
	public static void followDownTrendWave() throws IOException, InterruptedException {
		do {
			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())*-1) > 0) {	
				placeSellOrderWithLimitBuy(symbol, qty);
				Thread.sleep(1000);
				secondsCounter = 0;
			}
			else 
			{
				if (secondsCounter < 30) {
					Thread.sleep(1000);
					secondsCounter++;
					continue;
				}
				//Cancel all the open Orders for the symbol
				OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
				for(OrdersPojo openSymbolOrder:openSymbolOrders) {
					alpacaAccount.deleteOrder(openSymbolOrder.getId());
					pw.write("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
							"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					OrdersPojo canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
					while(!"canceled".equals(canceledOrder.getStatus())) {
						if("filled".equals(canceledOrder.getStatus())) break;
						canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
						pw.write("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
								"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					}
				}
				PositionsPojo openSellPosition = alpacaAccount.getSymbolPosition(symbol);
				OrdersPojo buyAllOpenPositions = alpacaAccount.buySymbolQuantity(symbol, Double.valueOf(openSellPosition.getQty())*-1);
				while (buyAllOpenPositions.getStatus() !=null && !"filled".equals(buyAllOpenPositions.getStatus()))
				{
					Thread.sleep(100);
					buyAllOpenPositions = alpacaAccount.getOrderDetails(buyAllOpenPositions.getId());
				}
				//Return after closing all positions for up-trend
				return;
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
	}

	public static OrdersPojo placeSellOrderWithLimitBuy(String symbol, Double qty) throws IOException, InterruptedException {
		placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, qty);
		placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		pw.write("Short Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		while (!"filled".equals(placedSellOrder.getStatus()))
		{
			if (placedSellOrder.getId() == null) return null;
			Thread.sleep(100);
			placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		}
		lastSellOrderPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());;
		placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty,  lastSellOrderPrice*pointOneFactor);
		placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		return placedSellOrder;
	}

	public static OrdersPojo placeBuyOrderWithLimitSell(String symbol, Double qty) throws IOException, InterruptedException {
		placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
		placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{
			if (placedBuyOrder.getId() == null) return null;
			Thread.sleep(100);
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());

		}
		lastBuyOrderPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
		placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  lastBuyOrderPrice*onePointOneFactor);
		placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		return placedBuyOrder;
	}
}