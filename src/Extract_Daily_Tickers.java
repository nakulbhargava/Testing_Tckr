import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.PositionsPojo;

public class Extract_Daily_Tickers{

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	//PP
	private static final String alpacaKeyValue = "PKSSSGEQK3GXOMY3J6O0";
	private static final String secretIdValue = "OzGxMmmEYJVvv65jYbG08cSaPJ3MqE41UkI2AI0U";
	
	//Nakul
//	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
//	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";
	
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
	
	static AlpacaApiCaller alpacaAccount;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\outputDailyTicks - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;
	static Double localMaxima = 0.00;
	static Double localMinima = 0.00;
	static Double currentPrice = 0.00;
	
	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file, true));
		fo = new FileOutputStream(file, true);
		PositionsPojo[] openPositions = alpacaAccount.getOpenPositions();
		HashMap<String, List<Double>> tickerPrices = new HashMap<String, List<Double>>();
		for (int i = 0 ; i< openPositions.length; i++) {
			String symbol = openPositions[i].getSymbol();
			List<Double> ticks = new ArrayList<Double>();
			Double currentPrice = Double.valueOf(openPositions[i].getCurrent_price());
			ticks.add(currentPrice);
			tickerPrices.put(symbol, ticks);
		}
		if(alpacaAccount.getClock().isIs_open()) {
			do {
				try {
					openPositions = alpacaAccount.getOpenPositions();
				}
				catch(Exception e) {
					continue;
				}
				for (int i = 0 ; i< openPositions.length; i++) {
					tickerPrices.get(openPositions[i].getSymbol()).add(Double.valueOf(openPositions[i].getCurrent_price()));
					System.out.println(openPositions[i].getSymbol() + "," + LocalDateTime.now() + ","  + LocalDateTime.now().getSecond() + "," +openPositions[i].getCurrent_price());
					pw.write(openPositions[i].getSymbol() + "," + LocalDateTime.now() + ","  + LocalDateTime.now().getSecond() + "," +openPositions[i].getCurrent_price() + "\n");
				}
				Thread.sleep(500);
			}while(LocalDateTime.now().isBefore(marketCloseTime));
		}
		for (int i = 0 ; i< openPositions.length; i++) {
			List<Double> ticks = tickerPrices.get(openPositions[i].getSymbol());
			Double totalSum = 0.0;
			for(Double tick: ticks) {
				totalSum = totalSum + tick;
			}
			System.out.println(openPositions[i].getSymbol() + " Average Value:\t" + totalSum/ticks.size());
			pw.write(openPositions[i].getSymbol() + " Average Value:\t" + totalSum/ticks.size());
		}
		pw.close();
	}
}