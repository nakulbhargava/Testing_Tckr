package LazyTwoAccount;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

public class Lazy_Two_Account_Strategy {

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

	static Double qty = 1.00;
	static Double maxQty = 100.00;
	static Double pointZeroOneFactor = qty/1000;
	static Double pointOneFactor = 0.999;
	static Double pointTwoFactor = 0.998;
	static Double onePointTwoFactor = 1.002;
	//static Double pointTwoFactor = .998;
	static Double onePointFourFactor = 1.005;
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
	static boolean isNewCycle = false;

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
			while(LocalDateTime.now().isBefore(marketCloseTime)) {
				Thread.sleep(20000);
				followUpTrendWave();
				Thread.sleep(20000);
				followDownTrendWave();
				pw.close();
			}
		}
	}

	public static void followUpTrendWave() throws IOException, InterruptedException {
		PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
		if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
			//priceToBuy = returnCurrentPrice();
			placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
			pw.write("Account 1 - Buy Order Executed: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
					"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
			System.out.println("Account 1 - Buy Order Executed: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
					"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
			while (!"filled".equals(placedBuyOrder.getStatus()))
			{
				Thread.sleep(100);
				placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());

			}
			lastBuyOrderPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
			placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  lastBuyOrderPrice*onePointTwoFactor);
			placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
			pw.write("Account 1 - Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
					"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
			System.out.println("Account 1 - Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
					"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
		}
	}
	public static void followDownTrendWave() throws IOException, InterruptedException {
		PositionsPojo openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
		if (maxQty.compareTo(Double.valueOf(openPosition2.getQty() == null? "0":openPosition2.getQty())*-1) > 0) {	
			placeSellOrder2 = alpacaAccount2.sellSymbolQuantity(symbol, qty);
			placedSellOrder2 = alpacaAccount2.getOrderDetails(placeSellOrder2.getId());
			pw.write("Account 2 - Short Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
					"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
			System.out.println("Account 2 - Short Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
					"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
			while (!"filled".equals(placedSellOrder2.getStatus()))
			{
				Thread.sleep(100);
				placedSellOrder2 = alpacaAccount2.getOrderDetails(placeSellOrder2.getId());
			}
			lastSellOrderPrice = Double.valueOf(placedSellOrder2.getFilled_avg_price());;
			placeBuyOrder2 = alpacaAccount2.buySymbolLimitOrder(symbol, qty,  lastSellOrderPrice*pointTwoFactor);
			placedBuyOrder2 = alpacaAccount2.getOrderDetails(placeBuyOrder2.getId());
			pw.write("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
					"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
			System.out.println("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
					"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
		}
	}
}