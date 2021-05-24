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

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Symbol_Algo_sellLimitOrders_Refined {

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
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(14,59);
	private boolean isRealTimeTrading = false;
	static boolean buyFraction = true;
	static Double qty = 10.00;
	static Double maxQty = (qty*10) + 1;
	static Double pointZeroOneFactor = qty/1000;
	static Double priceToBuy = 0.00;
	static Double priceToSell = 0.0000;
	static Double pointOneFactor = .999;
	static Double onePointOneFactor = 1.001;
	static Double onePointTwoFactor = 1.002;
	static Double onePointFourFactor = 1.004;
	static Double pointEightFactor = .992;
	static boolean timeToSell = false;
	static String symbol = "GSX";
	static Double shortQty = 0.00;
	static AlpacaApiCaller alpacaAccount;
	static Double lastBuyOrderPrice = 0.00;
	static Double lastSellOrderPrice = 0.00;
	static OrdersPojo lastShortedOrder = null;
	static OrdersPojo placeBuyOrder;
	static OrdersPojo placedBuyOrder;
	static OrdersPojo placeSellOrder;
	static OrdersPojo placedSellOrder;
	static int secondsCounter = 0;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file;
	static PrintWriter pw; 
	static FileOutputStream fo;
	
	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
		pw = new PrintWriter(new FileOutputStream(file)); 
		fo = new FileOutputStream(file);
		//Every 10 sec place a limit order buy and limit sell order with a very little difference and loop it
		if(alpacaAccount.getClock().isIs_open()) {
			do {
				PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
				if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())*-1-shortQty) > 0) {	
					placeSellOrderWithLimitBuy(symbol, qty);
				}
				else 
				{
					Thread.sleep(100);
					PositionsPojo openSellPosition = alpacaAccount.getSymbolPosition(symbol);
					Double stopLossPrice = lastSellOrderPrice*onePointFourFactor;
					pw.write("Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
							"\t" + "Current Price: " + openSellPosition.getCurrent_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					if (stopLossPrice.compareTo(Double.valueOf(openSellPosition.getCurrent_price()))<0) {
						//Cancel all the open Orders for the symbol
						OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
						for(OrdersPojo openSymbolOrder:openSymbolOrders) {
							alpacaAccount.deleteOrder(openSymbolOrder.getId());
							pw.write("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
									"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
							OrdersPojo canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
							while(!"canceled".equals(canceledOrder.getStatus())) {
								canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
								pw.write("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
										"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
							}
						} 

						OrdersPojo buyAllOpenPositions = alpacaAccount.buySymbolQuantity(symbol, Double.valueOf(openSellPosition.getQty()));
						while (buyAllOpenPositions.getStatus() !=null && !"filled".equals(buyAllOpenPositions.getStatus()))
						{
							Thread.sleep(100);
							buyAllOpenPositions = alpacaAccount.getOrderDetails(buyAllOpenPositions.getId());
						}
						//buyFraction = false;
						//Return after closing all positions for up-trend
						return;
					}
					else {
						//If the StopLossPrice is not hit then short buy temporary
						if(lastShortedOrder == null || "filled".equals(lastShortedOrder.getStatus())) {
							lastShortedOrder = placeBuyOrderWithLimitSell(symbol, qty);						
						}
						Thread.sleep(100);
						lastShortedOrder = alpacaAccount.getOrderDetails(lastShortedOrder.getId());
						if("filled".equals(lastShortedOrder.getStatus())) shortQty = 0.00;
						else shortQty = qty;
					}
				}
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			pw.close();
		}
	}

	public static OrdersPojo placeSellOrderWithLimitBuy(String symbol, Double qty) throws IOException, InterruptedException {
		placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, qty);
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
		placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty,  lastSellOrderPrice*pointOneFactor);
		placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		return placedBuyOrder;
	}
	public static OrdersPojo placeBuyOrderWithLimitSell(String symbol, Double qty) throws IOException, InterruptedException {
		placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
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
		placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  lastBuyOrderPrice*onePointOneFactor);
		placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		return placedBuyOrder;
	}
}
