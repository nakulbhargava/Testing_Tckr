package Backtesting;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConfigConstants {
	public static final String alpacaKey = "APCA-API-KEY-ID";
	public static final String secretId = "APCA-API-SECRET-KEY";

	private static final String alpacaKeyValueNakulPaper = "PKXTC9CIEKM9SXKWR7CN";
	private static final String secretIdValueNakulPaper = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";


	private static final String alpacaKeyValueVaishaliPaper = "PKN9AR7BFFZXH5X3MP6H";
	private static final String secretIdValueVaishaliPaper = "daalp83963QKdcmOqI7iaqBwkdZxFRdFAmcijLZH";

	private static final String alpacaKeyValueDistPaper = "PKW027OGK8HX515NRG66";
	private static final String secretIdValueDistPaper = "yoxhLKh4FXtxNl1LLHaUpAtoDkaKHCOHBopo0baS";

	private static final String alpacaKeyRealTimeNakul = "AK7LQFK01P6RWA5UVNZT";
	private static final String secretIdRealTimeNakul = "CZJaF69oPrGsy2i4gvZsiSNNju6Pdtx9Gmh1tdyD";
	private static final String headerContentType = "Content-Type";
	private static final String headerContentTypeValue = "application/json";
	private static final String paperUrl = "https://paper-api.alpaca.markets";
	private static final String realtimeUrl = "https://api.alpaca.markets";
	private static final String activeVersion = "/v2";
	private static final String inactiveVersion = "/v1";
	private static final String ordersEndpoint = "/orders";
	private static final String accountEndpoint = "/account";
	private static final String lastQuoteEndpoint = "/last_quote/stocks";
	private static final String lastTradeEndpoint = "/last/stocks";
	private static final String positionsEndpoint = "/positions";
	private static final String assetsEndpoint = "/assets";
	private static final String clockEndpoint = "/clock";
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(15,00);
}
