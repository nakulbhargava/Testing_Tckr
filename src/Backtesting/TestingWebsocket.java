package Backtesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.AlpacaApiCaller;

public class TestingWebsocket {
	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	//PP
	private static final String alpacaKeyValue = "PKSSSGEQK3GXOMY3J6O0";
	private static final String secretIdValue = "OzGxMmmEYJVvv65jYbG08cSaPJ3MqE41UkI2AI0U";

	//Nakul
	//	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	//	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";

	private static final String alpacaKeyRealTime = "PKXTC9CIEKM9SXKWR7CN";
	private static final String secretIdRealTime = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";
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
	static File file = new File("C:\\Users\\nakul\\Documents\\outputPreMarketTicks - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;

	public static void main(String[] args) throws IOException, InterruptedException, IllegalArgumentException, IllegalAccessException, URISyntaxException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file, true));
		fo = new FileOutputStream(file, true);
		alpacaAccount.getLiveDataStream("AAPL");
	}
}

