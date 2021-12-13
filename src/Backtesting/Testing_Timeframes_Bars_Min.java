package Backtesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.BarsPojo;
import alpacaPojo.TimeframePojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Timeframes_Bars_Min {

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
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(12,35);
	private boolean isRealTimeTrading = false;
	
	
	public static void main(String[] args) throws IOException, InterruptedException, ParseException {

		AlpacaApiCaller alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		String symbol = "BNGO";
		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());

		File file = new File("C:\\Users\\nakul\\Documents\\outputTimeframe - " + localTime + ".txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
//		FileOutputStream fo = new FileOutputStream(file);

		//AssetsPojo assets = alpacaAccount.getAssetSymbol(symbol);
		TimeframePojo timeframe = alpacaAccount.getTimeframe(symbol, "2021-07-20", "2021-07-21", "1Min");
		BarsPojo bars[] = timeframe.getBars();
		//Double highLowPercent = (Double.valueOf(bars.getHighPrice()) - Double.valueOf(bars.getLowPrice()))*100/Double.valueOf(bars.getClosePrice());
		DecimalFormat df = new DecimalFormat("#.##");
		Dataframes dataframes = new Dataframes();
		dataframes = dataframes.populateBars(bars);
		pw.write("S.No" + "\t" + "Symbol" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "H-L" + "\t" + "O-C"+ "\n");
		Double highLow = 0.00; 
		Double openClose;
		int i=0;
		Double sumHighLow = 0.00;
		Double highestHighLow = 0.00;
		Double open, close, high, low;
		for (BarsPojo bar:bars) {
			i++;
			open = Double.valueOf(bar.getOpenPrice());
			close = Double.valueOf(bar.getClosePrice());
			high = Double.valueOf(bar.getHighPrice());
			low = Double.valueOf(bar.getLowPrice());
			highLow = high - low;
			openClose = open - close;
			
			pw.write(i + "\t" + symbol + "\t" + bar.getTime() + "\t" 
					+ df.format(open) + "\t" + df.format(close) + "\t" + df.format(high)
					+ "\t" + df.format(low) + "\t" + df.format(highLow) + "\t" + df.format(openClose) + "\n");
			if(highestHighLow < highLow) highestHighLow = highLow;
			sumHighLow = sumHighLow + highLow;
		}
		Double sub = (highestHighLow - sumHighLow/bars.length);
		pw.write("Weighted Average HighLow: " + sumHighLow/bars.length + "\t" + "Highest HighLow: " + highestHighLow + "\t" + "Sub: " + sub);
		pw.close();

	}
}