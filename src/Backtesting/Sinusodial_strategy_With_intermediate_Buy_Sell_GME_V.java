package Backtesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

public class Sinusodial_strategy_With_intermediate_Buy_Sell_GME_V {

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
	static boolean buyFraction = true;
	static Double qty = 10.00;
	static Double maxQty = (qty*10) + 1;
	static Double stopLossPrice = 0.00;
	static Double pointZeroOneFactor = qty/1000;
	static Double buyProfitFactor = 0.999;
	//static Double pointTwoFactor = .998;
	static Double shortStopLossFactor = 1.001;
	static Double sellProfitFactor = 1.006;
	static Double stopLossFactor = .994;
	static boolean timeToSell = false;
	static String symbol = "RUN";
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
	static boolean firstTimeTrading = true;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file));
		fo = new FileOutputStream(file);

		if(alpacaAccount.getClock().isIs_open()) {
			followUpTrendWave();
			pw.close();
		}
	}

	public static void followUpTrendWave() throws IOException, InterruptedException {
		do {
			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
				placeBuyOrderWithLimitSell(symbol, qty);
				secondsCounter = 0;
			}
			else 
			{
				if (secondsCounter < 60) {
					Thread.sleep(1000);
					secondsCounter++;
					continue;
				}
				PositionsPojo openBuyPosition = alpacaAccount.getSymbolPosition(symbol);
				Double stopLossPrice = lastBuyOrderPrice*stopLossFactor;
				pw.write("Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openBuyPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				if (openBuyPosition.getCurrent_price() != null && stopLossPrice.compareTo(Double.valueOf(openBuyPosition.getCurrent_price()))>0) {
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

					OrdersPojo sellAllOpenPositions = alpacaAccount.sellSymbolQuantity(symbol, Double.valueOf(openBuyPosition.getQty()));
					while (sellAllOpenPositions.getStatus() !=null && !"filled".equals(sellAllOpenPositions.getStatus()))
					{
						Thread.sleep(100);
						sellAllOpenPositions = alpacaAccount.getOrderDetails(sellAllOpenPositions.getId());
					}
					followDownTrendWave();
				}
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
	}
	public static void followDownTrendWave() throws IOException, InterruptedException {
		do {
			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())*-1) > 0) {	
				placeSellOrderWithLimitBuy(symbol, qty);
				secondsCounter = 0;
			}
			else 
			{
				if (secondsCounter < 60) {
					Thread.sleep(1000);
					secondsCounter++;
					continue;
				}
				PositionsPojo openSellPosition = alpacaAccount.getSymbolPosition(symbol);
				Double stopLossPrice = lastSellOrderPrice*shortStopLossFactor;
				pw.write("Last Sell Price: " + lastSellOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openSellPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				if (openSellPosition.getCurrent_price() != null && stopLossPrice.compareTo(Double.valueOf(openSellPosition.getCurrent_price()))<0) {
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
					openSellPosition = alpacaAccount.getSymbolPosition(symbol);
					OrdersPojo buyAllOpenPositions = alpacaAccount.buySymbolQuantity(symbol, Double.valueOf(openSellPosition.getQty())*-1);
					while (buyAllOpenPositions.getStatus() !=null && !"filled".equals(buyAllOpenPositions.getStatus()))
					{
						Thread.sleep(100);
						buyAllOpenPositions = alpacaAccount.getOrderDetails(buyAllOpenPositions.getId());
					}
					//buyFraction = false;
					//Return after closing all positions for up-trend
					return;
				}
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
	}
	
	public static OrdersPojo placeSellOrderWithLimitBuy(String symbol, Double qty) throws IOException, InterruptedException {
		placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, qty);
		if(null == placeSellOrder.getId()) return null;
		placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		pw.write("Short Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		while (!"filled".equals(placedSellOrder.getStatus()))
		{
			Thread.sleep(100);
			placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		}
		lastSellOrderPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());;
		placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty,  lastSellOrderPrice*buyProfitFactor);
		placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		return placedBuyOrder;
	}
	
	public static OrdersPojo placeBuyOrderWithLimitSell(String symbol, Double qty) throws IOException, InterruptedException {
		placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
		if(null == placeBuyOrder.getId()) return null;
		placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{
			Thread.sleep(100);
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());

		}
		lastBuyOrderPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
		placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  lastBuyOrderPrice*sellProfitFactor);
		placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		return placeSellOrder;
	}
}