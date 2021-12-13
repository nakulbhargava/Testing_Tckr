import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import alpacaPojo.AccountsPojo;
import alpacaPojo.AssetsPojo;
import alpacaPojo.LastQuotePojo;
import alpacaPojo.LastTradePojo;
import alpacaPojo.OrdersPojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Symbol_Algo2 {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";
	private static final String alpacaKeyRealTime = "AK7LQFK01P6RWA5UVNZT";
	private static final String secretIdRealTime = "CZJaF69oPrGsy2i4gvZsiSNNju6Pdtx9Gmh1tdyD";
	private static final String headerContentType = "Content-Type";
	private static final String headerContentTypeValue = "application/json";
	private static final String realtimeApiEndpoint = "https://api.alpaca.markets";
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
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(14,53);
	private boolean isRealTimeTrading = false;
	private static final int buyCounterConstant = 5;
	static Double qty = 1000.00;
	static Double initialQty = 1000.00;
	static Double pointOneFactor = qty/1000;
	static Double priceToSell = 0.0000;
	static String symbol = "CPRX";
	static AlpacaApiCaller alpacaAccount;
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		OrdersPojo placeOrder;
		OrdersPojo placedOrder;

		//alpacaAccount.getLiveDataStream(symbol);
		int buyCounter=0;
		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
		File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
		FileOutputStream fo = new FileOutputStream(file);

		if(alpacaAccount.getClock().isIs_open()) {
			do {
				if(timeToBuyBack(pw)) {

					placeOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
					placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());

					while (!"filled".equals(placedOrder.getStatus()))
					{
						placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
					}
					pw.write(qty + buyCounter + priceToSell + placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
							"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
							"\t" + placedOrder.getFilled_at()+ "\n");
					priceToSell = Double.valueOf(placedOrder.getFilled_avg_price()); 
					buyCounter = returnBuyCounter(pw);
					while(buyCounter == buyCounterConstant) {
						Thread.sleep(60000);
						buyCounter = returnBuyCounter(pw);
					}
					if (buyCounter == -buyCounterConstant)
					{
						placeOrder = alpacaAccount.sellSymbolQuantity(symbol, qty);
						while (!"filled".equals(placedOrder.getStatus()))
						{
							placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
						}
						pw.write(qty + buyCounter + priceToSell + placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
								"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
								"\t" + placedOrder.getFilled_at()+ "\n");
						buyCounter = 0;
					}
				}
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			pw.close();
		}
	}

	private static int returnBuyCounter(PrintWriter pw) throws IOException, InterruptedException {
		int buyCounter = 0;
		OrdersPojo placeOrder,placedOrder;
		do {
			placeOrder = alpacaAccount.buySymbolQuantity(symbol, pointOneFactor);
			placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());

			while (!"filled".equals(placedOrder.getStatus()))
			{
				placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
			}
			pw.write(qty + buyCounter + priceToSell + placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
					"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
					"\t" + placedOrder.getFilled_at()+ "\n");
			buyCounter++;
			qty = qty + pointOneFactor;
			while (priceToSell > Double.valueOf(placedOrder.getFilled_avg_price()) || buyCounter < -buyCounterConstant) {
				priceToSell = Double.valueOf(placedOrder.getFilled_avg_price());
				placeOrder = alpacaAccount.sellSymbolQuantity(symbol, pointOneFactor);
				placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());

				while (!"filled".equals(placedOrder.getStatus()))
				{
					placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
				}

				pw.write(qty + buyCounter + priceToSell + placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
						"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
						"\t" + placedOrder.getFilled_at()+ "\n");
				buyCounter--;
				qty = qty - pointOneFactor;
			}
			priceToSell = Double.valueOf(placedOrder.getFilled_avg_price()); 
		}while(!(buyCounter == buyCounterConstant || buyCounter == -buyCounterConstant));
		return buyCounter;
	}
	private static boolean timeToBuyBack(PrintWriter pw) throws IOException, InterruptedException {
		int buyCounter = returnBuyCounter(pw);
		if (buyCounter == buyCounterConstant) {
			if(alpacaAccount.getSymbolPosition(symbol)!=null )
				qty = initialQty - Double.valueOf(alpacaAccount.getSymbolPosition(symbol).getQty());
			else qty = initialQty; 
			return true;
		}
		else {
			Thread.sleep(60000);
			return timeToBuyBack(pw);
		}
	}

}