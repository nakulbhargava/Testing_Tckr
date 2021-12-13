import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import alpacaPojo.AccountsPojo;
import alpacaPojo.AssetsPojo;
import alpacaPojo.LastQuotePojo;
import alpacaPojo.LastTradePojo;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Symbol_Algo_LimitOrders {

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
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(14,25);
	private boolean isRealTimeTrading = false;
	static boolean buyFraction = true;
	static Double qty = 10.00;
	static Double maxBuyQty = (qty*10) + 1;
	static Double pointZeroOneFactor = qty/1000;
	static Double priceToBuy = 0.00;
	static Double priceToSell = 0.0000;
	static Double pointTwoFactor = .998;
	static Double onePointTwoFactor = 1.002;
	static Double onePointOneFactor = 1.001;
	static Double pointEightFactor = .992;
	static boolean timeToSell = false;
	static String symbol = "ARKK";
	static AlpacaApiCaller alpacaAccount;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		OrdersPojo placeBuyOrder;
		OrdersPojo placedBuyOrder;
		OrdersPojo placeSellOrder;
		OrdersPojo placedSellOrder;
		int secondsCounter = 0;
		OrdersPojo lossLimitOrder = null;

		Double lastBuyOrderPrice = 0.00;

		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
		File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file)); 
		FileOutputStream fo = new FileOutputStream(file);

		//Every 10 sec place a limit order buy and limit sell order with a very little difference and loop it
		if(alpacaAccount.getClock().isIs_open()) {
			do {
				PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
				if (maxBuyQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
					//					if (lossLimitOrder != null && "filled".equals(lossLimitOrder.getStatus())) {
					//						//Cancel all the open Orders for the symbol
					//						OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
					//						for(OrdersPojo openSymbolOrder:openSymbolOrders) {
					//							alpacaAccount.deleteOrder(openSymbolOrder.getId());
					//							pw.write("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
					//									"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					//						}
					//					}
					//This would mean that some other sell order was executed so need to cancel lossLimitOrder
					//					if(lossLimitOrder!=null && !"filled".equals(lossLimitOrder.getStatus())) {
					//						alpacaAccount.deleteOrder(lossLimitOrder.getId());
					//						pw.write("Buy Order Executed with following Details: " + lossLimitOrder.getSymbol() + "\t" + lossLimitOrder.getQty()+ "\t" + lossLimitOrder.getId() + 
					//								"\t" + lossLimitOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					//					}
					//					lossLimitOrder = null;
					priceToBuy = returnCurrentPrice();
					placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty, priceToBuy);
					placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
					pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
							"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					secondsCounter = 0; 
					while (!"filled".equals(placedBuyOrder.getStatus()) && secondsCounter < 10)
					{
						Thread.sleep(1000);
						placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
						secondsCounter++;
					}
					//If Buy order is not placed till time = 10sec then its an uptrend, lets try to buy at higher current buy price and repeat. No Need to wait and No need to place a sell order as we will cancel the corresponding buy order.
					if (!"filled".equals(placedBuyOrder.getStatus()) && secondsCounter >= 10) {
						//Cancel the buy Order
						alpacaAccount.deleteOrder(placeBuyOrder.getId());
						OrdersPojo deletedOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
						pw.write("Delete Order Executed with following Details: " + deletedOrder.getSymbol() + "\t" + deletedOrder.getQty()+ "\t" + deletedOrder.getId() + 
								"\t" + deletedOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						while (!"canceled".equals(deletedOrder.getStatus()))
						{
							Thread.sleep(100);
							deletedOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
						}
						pw.write("Order Canceled with following Details: " + deletedOrder.getSymbol() + "\t" + deletedOrder.getQty()+ "\t" + deletedOrder.getId() + 
								"\t" + deletedOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						continue;
					}
					lastBuyOrderPrice = priceToBuy;
					placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  priceToBuy*onePointOneFactor);
					placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
					pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
							"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					//				while (!"filled".equals(placedSellOrder.getStatus()))
					//				{
					//					placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
					//				}
					//Time counter for 30 sec, break the loop if limit orders were not executed, can be handled later
					//				LocalDateTime timeBeforeFilled = LocalDateTime.now();
					//				boolean ordersFilled = true;
					//				while (!"filled".equals(placedBuyOrder.getStatus()) && !"filled".equals(placedSellOrder.getStatus()))
					//				{
					//					Thread.sleep(100);
					//					placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
					//					placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
					//					pw.write("Either Buy Order " + placeBuyOrder.getId() + " or Sell Order " + placedSellOrder + " Not filled at: " + "\t" + LocalDateTime.now().toString() + "\n");
					//					if(timeBeforeFilled.plusSeconds(30).isBefore(LocalDateTime.now()) ) {						
					//						ordersFilled = false;
					//						break;					
					//					}
					//				}
					//				if(ordersFilled) {

					//					pw.write("Buy Order Filled with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getFilled_qty() + 
					//							"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
					//							"\t" + placedBuyOrder.getFilled_at() + "\t" + LocalDateTime.now().toString() + "\t" + placedBuyOrder.getId() + "\n");
					//					pw.write("Sell Order Filled with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getFilled_qty() + 
					//							"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
					//							"\t" + placedSellOrder.getFilled_at() + "\t" + placedSellOrder.getId() + "\n");
					//					Thread.sleep(10000);
					//				}

				}
				else 
				{
					//					if(lossLimitOrder == null)
					//					{
					//						lossLimitOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty*11,  priceToBuy*pointFourFactor);
					//						pw.write("Loss Limit Order Executed with following Details: " + lossLimitOrder.getSymbol() + "\t" + lossLimitOrder.getQty()+ "\t" + lossLimitOrder.getId() + 
					//								"\t" + lossLimitOrder.getSide() + "\t" + lossLimitOrder.getFilled_avg_price() + 
					//								"\t" + LocalDateTime.now().toString() + "\n");
					//					}
					Thread.sleep(1000);
					PositionsPojo openBuyPosition = alpacaAccount.getSymbolPosition(symbol);
					Double stopLossPrice = lastBuyOrderPrice*pointEightFactor;
					pw.write("Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
							"\t" + "Current Price: " + openBuyPosition.getCurrent_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					if (stopLossPrice.compareTo(Double.valueOf(openBuyPosition.getCurrent_price()))>0) {
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

						OrdersPojo sellAllOpenPositions = alpacaAccount.sellSymbolQuantity(symbol, Double.valueOf(openBuyPosition.getQty()));
						while (sellAllOpenPositions.getStatus() !=null && !"filled".equals(sellAllOpenPositions.getStatus()))
						{
							Thread.sleep(100);
							sellAllOpenPositions = alpacaAccount.getOrderDetails(sellAllOpenPositions.getId());
						}
						buyFraction = true;
					}
				}
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			pw.close();
		}
	}

	public static Double returnCurrentPrice() throws IOException, InterruptedException {
		Double currentPrice = 0.00;
		if(buyFraction)
		{
			OrdersPojo placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, pointZeroOneFactor);
			OrdersPojo placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
			while (!"filled".equals(placedBuyOrder.getStatus()))
			{
				placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
			}
			currentPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
			buyFraction = false;
			return currentPrice;
		}
		else
		{
			OrdersPojo placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, pointZeroOneFactor);
			OrdersPojo placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
			while (!"filled".equals(placedSellOrder.getStatus()))
			{
				placedSellOrder = alpacaAccount.getOrderDetails(placedSellOrder.getId());
			}
			currentPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());
			buyFraction = true;
			return currentPrice;
		}
	}
}
