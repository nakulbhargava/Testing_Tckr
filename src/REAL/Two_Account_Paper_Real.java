package REAL;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

public class Two_Account_Paper_Real {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";
	private static final String alpacaKeyValueAccount2 = "PKN9AR7BFFZXH5X3MP6H";
	private static final String secretIdValueAccount2 = "daalp83963QKdcmOqI7iaqBwkdZxFRdFAmcijLZH";
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
	static Double maxQty = (qty*10);
	static Double pointZeroOneFactor = qty/1000;
	static Double pointOneFactor = 0.999;
	static Double pointTwoFactor = .998;
	static Double onePointTwoFactor = 1.002;
	static Double onePointOneFactor = 1.001;
	static Double pointFourFactor = .995;

	static String symbol = "SNAP";
	static AlpacaApiCaller alpacaAccount, alpacaAccount2;
	static OrdersPojo placeBuyOrder;
	static OrdersPojo placedBuyOrder;
	static OrdersPojo placeSellOrder;
	static OrdersPojo placedSellOrder;
	static OrdersPojo placeBuyOrder2;
	static OrdersPojo placedBuyOrder2;
	static OrdersPojo placeSellOrder2;
	static OrdersPojo placedSellOrder2;

	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;
	static Double lastBuyOrderPrice = 0.00;
	static Double lastSellOrderPrice = 0.00;
	static Double lastBuyOrderPrice2 = 0.00;
	static Double lastSellOrderPrice2 = 0.00;
	static boolean isNewCycle = true;
	static int totalLoss = 0, totalProfit = 0;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		alpacaAccount2 = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValueAccount2, secretIdValueAccount2, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file));
		fo = new FileOutputStream(file);

		if(alpacaAccount.getClock().isIs_open()) {
			followDownTrendWave();
			pw.close();
		}
	}

	public static void followDownTrendWave() throws IOException, InterruptedException {
		do {
			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
			PositionsPojo openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
			if (qty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())*-1) > 0
					&& maxQty.compareTo(Double.valueOf(openPosition2.getQty() == null? "0":openPosition2.getQty())) > 0) {	
				placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, qty/10);
				placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
				pw.write("Account 1 - Short Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Account 1 - Short Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				while (!"filled".equals(placedSellOrder.getStatus()))
				{
					Thread.sleep(100);
					placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
				}
				lastSellOrderPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());;
				placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty/10,  lastSellOrderPrice*pointOneFactor);
				placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
				pw.write("Account 1 - Buy Order Executed: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Account 1 - Buy Order Executed: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
			}
			else 
			{

				//If its moving up then its time to buy in account2
				//openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
				Double currentQty = Double.valueOf(openPosition2.getQty() == null? "0.00":openPosition2.getQty());
				Double currentTrendPrice = Double.valueOf(openPosition.getCurrent_price()==null?"0.00":openPosition.getCurrent_price())*pointOneFactor;
				if(currentTrendPrice.compareTo(lastSellOrderPrice) < 0 && currentQty.compareTo(0.00) == 0) {
					Thread.sleep(1000);
					continue;
				}
				if (maxQty.compareTo(currentQty) > 0 && isNewCycle) {	
					//priceToBuy = returnCurrentPrice();
					placeBuyOrder2 = alpacaAccount2.buySymbolQuantity(symbol, qty/5);
					if (placeBuyOrder2.getId() == null) continue;
					placedBuyOrder2 = alpacaAccount2.getOrderDetails(placeBuyOrder2.getId());
					pw.write("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
							"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					System.out.println("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
							"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					while (!"filled".equals(placedBuyOrder2.getStatus()))
					{
						Thread.sleep(100);
						placedBuyOrder2 = alpacaAccount2.getOrderDetails(placeBuyOrder2.getId());

					}
					lastBuyOrderPrice2 = Double.valueOf(placedBuyOrder2.getFilled_avg_price());
					placeSellOrder2 = alpacaAccount2.sellSymbolLimitOrder(symbol, maxQty,  lastBuyOrderPrice2*onePointTwoFactor);
					placedSellOrder2 = alpacaAccount2.getOrderDetails(placeSellOrder2.getId());
					pw.write("Account 2 - Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
							"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					System.out.println("Account 2 - Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
							"\tTotal Profit: " + totalProfit + "\tTotal Loss: " + totalLoss + 
							"\t" + LocalDateTime.now().toString() + "\n");
					isNewCycle = false;
				}
//				else 
//				{
//					//To Introduce - Release the position in second account if the trend is changing again at the average filled price
//					Double currentPrice = Double.valueOf(openPosition2.getCurrent_price());
//					if(currentPrice.compareTo(lastBuyOrderPrice2*pointTwoFactor) < 0) {
//						OrdersPojo[] openSymbolOrders = alpacaAccount2.getSymbolOpenOrders(symbol);
//						if(openPosition2.getQty() != null) {
//							for(OrdersPojo openSymbolOrder:openSymbolOrders) {
//								alpacaAccount2.deleteOrder(openSymbolOrder.getId());
//								pw.write("Account 2 - Delete Order Executed: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
//										"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
//								OrdersPojo canceledOrder = alpacaAccount2.getOrderDetails(openSymbolOrder.getId());
//								while(!"canceled".equals(canceledOrder.getStatus())) {
//									if("filled".equals(canceledOrder.getStatus())) break;
//									canceledOrder = alpacaAccount2.getOrderDetails(openSymbolOrder.getId());
//									pw.write("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
//											"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
//								}
//							}
//							openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
//							if(openPosition2.getQty() != null) {
//								placeSellOrder2 = alpacaAccount2.sellSymbolQuantity(symbol, Double.valueOf(openPosition2.getQty()));
//								placedSellOrder2 = alpacaAccount2.getOrderDetails(placeSellOrder2.getId());
//								pw.write("Account 2 - Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
//										"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
//										"\t" + LocalDateTime.now().toString() + "\n");
//								System.out.println("Account 2 - Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
//										"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
//										"\t" + LocalDateTime.now().toString() + "\n");
//							}
//							totalLoss = (int) (totalLoss + 1000 - 1000*pointTwoFactor);
//							pw.write("Account 2 - Total Loss: " + totalLoss + "\n");
//							System.out.println("Account 2 - Total Loss: " + totalLoss + "\n");
//						}
//					}
//				}

				if(openPosition.getQty() == null) {
					Thread.sleep(1000);
					continue;
				}
				//Delete everything in Account 1 to start a new cycle
				OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
				for(OrdersPojo openSymbolOrder:openSymbolOrders) {
					alpacaAccount.deleteOrder(openSymbolOrder.getId());
					pw.write("Account 1 - Delete Order Executed: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
							"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					System.out.println("Account 1 - Delete Order Executed: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
							"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					OrdersPojo canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
					while(!"canceled".equals(canceledOrder.getStatus())) {
						canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
						pw.write("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
								"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					}
				} 
				openPosition = alpacaAccount.getSymbolPosition(symbol);
				OrdersPojo buyAllOpenPositions = alpacaAccount.buySymbolQuantity(symbol, Double.valueOf(openPosition.getQty())*-1);
				while (buyAllOpenPositions.getStatus() !=null && !"filled".equals(buyAllOpenPositions.getStatus()))
				{
					Thread.sleep(100);
					buyAllOpenPositions = alpacaAccount.getOrderDetails(buyAllOpenPositions.getId());
				}
				totalProfit++;
				isNewCycle = true;
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
	}
}