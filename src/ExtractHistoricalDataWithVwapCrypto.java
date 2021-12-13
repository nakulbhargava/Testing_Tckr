import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.AssetsPojo;
import alpacaPojo.BarsPojo;
import alpacaPojo.TimeframePojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class ExtractHistoricalDataWithVwapCrypto {

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
	private static final String dataUrl = "https://data.alpaca.markets";
	//	private static final String activeVersion = "/v2";
	private static final String activeVersion = "/v1beta1";
	private static final String inactiveVersion = "/v1";
	private static final String ordersEndpoint = "/orders";
	private static final String accountEndpoint = "/account";
	private static final String lastQuoteEndpoint = "/last_quote/stocks";
	private static final String lastTradeEndpoint = "/last/stocks";
	private static final String positionsEndpoint = "/positions";
	private static final String assetsEndpoint = "/assets";
	private static final String clockEndpoint = "/clock";
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(15,55);
	private boolean isRealTimeTrading = false;
	public static void main(String[] args) throws IOException, InterruptedException {

		AlpacaApiCaller alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, dataUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		//String symbol = "DARE";
		//		String[] symbols = new String[]{"", "" , "", "" , "", "", "", "", "", "" , "", "" , "", "", "", "", "", "" , "", "" , "", "", "", ""};
		//		String symbols[] = new String[]{ "XPEV", "UBER", "SNAP", "PLUG", "NIO", "MRO", "LYFT", "GPS", "GOTU", "DKNG", "CCL", "BNGO", "AMWL", "ACAD", "LCTX",
		//				"CLSN", "CMCM", "CPRX", "CRBP", "SOLO", "CYRN", "SNDL", "ISR", "EVFM", "HDSN", "REI", "NXE", "UUUU", 
		//				"NGD", "GSAT", "NMTR", "STAF", "SRGA", "WTER", "CWBR", "DARE", "GERN", "IDRA", "ARDX", "ANY" , "ADMP", "NXTD" , "NEPT", "XSPA", "VISL", "SONM", "ECOR", "XELA" , "VERB", "SHIP" , "SFET", 
		//				"SNCR", "PSTV", "PROG", "POAI", "PIXY" , "PHUN", "PBTS" , "NAOV", "NAKD", "LPTH", "JUPW", "JAGX", "ITRM", "IO", "INFI", "HVBT", 
		//				"GSAT", "GSMG", "GSS", "GLBS", "GEG", "FAMI", "EBON", "DYNT", "DWSN", "DTSS", "CTRM", "AMD", "BILI", "BNTX", "CVX", "EBAY", "ETSY", 
		//				"EW", "FSLR", "FTV", "FUTU", "JCI", "PDD", "PENN", "PTON", "SAVA", "QSR", "SWKS", "SQ", "TEAM", "TSLA", "ZEN", "TNA", "BYND", "CHKP",
		//				"COIN", "EXAS", "FIVN", "HAS", "HCA", "HLT", "MAR", "NVAX", "PII", "PKI", "PXD", "ROKU", "SE", "SHW", "TXRH", "UPST", "MRNA", "ABNB",
		//				"WLTW", "WYNN", "ZEN", "ZLAB", "ZM", "Z", "BABA", "AON", "CHWY", "BIDU", "HAS", "NTES", "TWTR", "RCL",
		//				"BDR", "ACST" , "ADMA", "ADVM" , "AEI", "AKBA", "ALNA", "AMPE", "AREC", "ARPO" , "ASXC", "BIOL" , "BOXL", 
		//				"BRQS", "BXRX", "CASI", "CCO", "CEI" , "CEMI", "CFMS" , "BSQR", "CIDM", "CIG", "COCP", "DNN", "DPW", "EDU", "ESGC", "EVOK", 
		//				"GNUS", "GOED", "GTE", "FTFT", "IAG", "IDEX", "IGC", "IBIO", "ITP", "INUV", "IZEA", "JOB", "KOS", "LGHL", "LITB", "LLNW", "LMNL", 
		//				"LPCN", "LYG", "METX", "MOTS", "MREO", "NAT", "NBRV", "NBY", "NCMI", "NEW", "NGL", "OGEN", "ONE", "PEI", "PLX", "PTN", "PXS", "QD",
		//				"REED", "REPH", "RGLS", "SEEL", "SESN", "SOS", "SPPI", "TBLT", "TELL", "TLGT", "TLMD", "TMBR", "TNXP", "TRVN", "TTOO", "TXMD", "UEC", "URG",
		//				"VEON", "VERO", "VTGN", "VYNE", "WATT", "WORX", "WPG", "WTRH", "XERS", "YCBD", "ZIOP", "ZOM", "ZSAN", "DFFN"};

		//		String symbols[] = new String[]{"TIGR", "TASK", "SUNW", "SQL",
		//		"SPRT", "SPI" , "SOL", "SOAC" , "SNMP", "SNGX", "SMFR", "SLQT", "SLI", "SKIN" , "SIOX", "SIGA" , 
		//		"SHCR", "SFTW", "RSKD", "ROOT", "RKLB", "RIDE" , "RIBT", "REKR" , "REE", "REAL", "RDW", "PXLW", "DQ", "MGNX", "HGEN", "ACTG",
		//		"ABUS", "ACI" , "ADAP", "AERI" , "AFRM", "AIHS", "ADXS", "AGFY", "ALIT", "ARRY" , "APLS", "ARQQ" , 
		//		"ALZN", "APP", "ASTR", "AVGR", "AUPH", "BBIG" , "ATER", "BEST" , "ATOS", "BEKE", "BCTX", "BHAT"};

		//		String symbols[] = new String[]{"BLDE", "BGFV", "BITF",
		//				"CANO", "CAN" , "BTBT", "BTCM" , "BTX", "CLOV", "COOK", "CRDF", "CXM", "DATS" , "CURV", "DIDI" , 
		//				"DOCS", "DOMA", "DOYU", "DVAX", "DTST", "EFTR" , "ELOX", "ENDP" , "EOLS", "EEIQ", "ENVB", "FBRX",
		//				"FCEL", "FIGS", "HLBZ", "HIMS", "HUT", "HYZN" , "IPDN", "IMPL" , "INO", "IRNT", "JKS", "JOBY","KPLT", "KAVL", "LOVE",
		//				"LAC", "MARA", "MOHO", "MILE", "FINV", "GEVO", "GOED", "GNLN", "GRNQ", "HIMX", "HIPO", "GSM"};

		//		String symbols[] = new String[]{"ADXS"};

		String symbols[] = new String[]{"LTCUSD"};
		for(int i = 0 ; i < symbols.length; i++) {

			String startDate = "2020-08-01";
			String endDate = "2021-11-20";
			LocalDate start = LocalDate.parse(startDate);
			LocalDate end = LocalDate.parse(endDate);

			File fileTrue = new File("C:\\Users\\nakul\\Documents\\HistoricalData - VWAP\\MinData - " + symbols[i] + "_" + startDate + "_" + endDate + ".txt");
			PrintWriter pw1 = new PrintWriter(new FileOutputStream(fileTrue));

			pw1.write("Symbol,Date,Open,Close,High,Low,Volume,TradeCount,VWAP"+ "\n");
			System.out.println("Symbol,Date,Open,Close,High,Low,Volume,TradeCount,VWAP");


			//				TimeframePojo timeframe = alpacaAccount.getTimeframe(symbols[i], start.toString(), start.toString(), "1Min");
			TimeframePojo timeframe = alpacaAccount.getCryptoTimeframe(symbols[i], start.toString(), end.toString(), "1Day");
			Thread.sleep(350);
			BarsPojo bars[] = timeframe.getBars();
			start = start.plusDays(1);
			if(bars == null) {
				System.out.println("Data Not found for: " + symbols[i] + " on " + start.minusDays(1));
				continue;
			}

			for(BarsPojo bar: bars) {
				pw1.write(symbols[i]+ "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + ","+ bar.getHighPrice() 
				+ ","+ bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap() + "\n" );
				System.out.println(symbols[i]+ "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + ","+ bar.getHighPrice() 
				+ ","+ bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap());
			}
			pw1.close();
		}

	}
}
