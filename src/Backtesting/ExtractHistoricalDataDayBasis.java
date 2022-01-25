package Backtesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.AssetsPojo;
import alpacaPojo.BarsPojo;
import alpacaPojo.TimeframePojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class ExtractHistoricalDataDayBasis {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";
	private static final String alpacaKeyRealTime = "AK7LQFK01P6RWA5UVNZT";
	private static final String secretIdRealTime = "CZJaF69oPrGsy2i4gvZsiSNNju6Pdtx9Gmh1tdyD";
	private static final String headerContentType = "Content-Type";
	private static final String headerContentTypeValue = "application/json";
	private static final String paperUrl = "https://paper-api.alpaca.markets";
	//	private static final String realtimeUrl = "https://data.alpaca.markets";
	private static final String realtimeUrl = "https://data.alpaca.markets";
	private static final String dataUrl = "https://data.alpaca.markets";
	//private static final String activeVersion = "/v1beta1";
	private static final String activeVersion = "/v2";
	private static final String inactiveVersion = "/v1";
	private static final String ordersEndpoint = "/orders";
	private static final String accountEndpoint = "/account";
	private static final String lastQuoteEndpoint = "/last_quote/stocks";
	private static final String lastTradeEndpoint = "/last/stocks";
	private static final String positionsEndpoint = "/positions";
	private static final String assetsEndpoint = "/assets";
	private static final String clockEndpoint = "/clock";
	private static final String outputFilePath = "C:\\Users\\nakul\\Documents\\HistoricalDataDay_Vol\\";
	private static int currentYear = LocalDate.now().getYear();
	private static int startingYear = LocalDate.now().getYear() - 3;
	private static String startDate = startingYear + "-" + LocalDate.now().getMonth() + "-" +  LocalDate.now().getDayOfMonth();
	private static String endDate = currentYear + "-" + LocalDate.now().getMonth() + "-" +  LocalDate.now().getDayOfMonth();
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

		//		String symbols[] = new String[]{"BODY", "BLDE", "BGFV", "BITF",
		//				"CANO", "CAN" , "BTBT", "BTCM" , "BTX", "CLOV", "COOK", "CRDF", "CXM", "DATS" , "CURV", "DIDI" , 
		//				"DOCS", "DOMA", "DOYU", "DVAX", "DTST", "EFTR" , "ELOX", "ENDP" , "EOLS", "EEIQ", "ENVB", "FBRX",
		//				"FCEL", "FIGS", "HLBZ", "HIMS", "HUT", "HYZN" , "IPDN", "IMPL" , "INO", "IRNT", "JKS", "JOBY","KPLT", "KAVL", "LOVE",
		//				"LAC", "MARA", "MOHO", "MILE", "FINV", "GEVO", "GOED", "GNLN", "GRNQ", "HIMX", "HIPO", "GSM"};

		AssetsPojo[] assets = alpacaAccount.getAssets();
		HashMap<String, List<BarsPojo>> masterHighBar = new HashMap<String, List<BarsPojo>>();
		HashMap<String, List<BarsPojo>> masterVolatileBar = new HashMap<String, List<BarsPojo>>();
		HashMap<String, List<BarsPojo>> masterOkVolatileBar = new HashMap<String, List<BarsPojo>>();

		String symbol;
		for(int i = 0 ; i < assets.length; i++) {
			if(!"active".equals(assets[i].getStatus())) continue;
			List<BarsPojo> highSymbolBars = new ArrayList<BarsPojo>();
			List<BarsPojo> volatileSymbolBars = new ArrayList<BarsPojo>();
			List<BarsPojo> okVolatileSymbolBars = new ArrayList<BarsPojo>();
			symbol = assets[i].getSymbol();
			//symbol = assets[i].getSymbol();
			
			LocalDate start = LocalDate.parse(startDate);
			LocalDate end = LocalDate.parse(endDate);

			File fileTrue = new File(outputFilePath + "DayData - " + symbol + "_" + startDate + "_" + endDate + ".txt");
			PrintWriter pw1 = new PrintWriter(new FileOutputStream(fileTrue));

			pw1.write("Symbol,Date,Open,Close,High,Low,Volume,Trade,VWAP"+ "\n");
			System.out.println("Symbol,Date,Open,Close,High,Low,Volume,Trade,VWAP");
			BarsPojo bars[] = null;
			try {
				TimeframePojo timeframe = alpacaAccount.getTimeframe(symbol, start.toString(), end.toString(), "1Day");
				Thread.sleep(300);
				bars = timeframe.getBars();
			}
			catch(Exception e){
				System.out.println("Data Not found for: " + symbol + " on " + start.minusDays(1));
				continue;
			}
			
			start = start.plusDays(1);
			if(bars == null) {
				System.out.println("Data Not found for: " + symbol + " on " + start.minusDays(1));
				continue;
			}

			for(BarsPojo bar: bars) {
				pw1.write(symbol + "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + "," + bar.getHighPrice() + "," + bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap() + "\n" );
				System.out.println(symbol + "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + "," + bar.getHighPrice() + "," + bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap());
				//Double openPrice = Double.valueOf(bar.getOpenPrice());
				Double highPrice = Double.valueOf(bar.getHighPrice());
				Double lowPrice = Double.valueOf(bar.getLowPrice());
				Double dailySpread = (highPrice-lowPrice)*100/lowPrice;
				if(dailySpread.compareTo(25.0) > 0) highSymbolBars.add(bar);
				if(dailySpread.compareTo(15.0) > 0 && dailySpread.compareTo(25.0) < 0) volatileSymbolBars.add(bar);
				if(dailySpread.compareTo(8.0) > 0 && dailySpread.compareTo(15.0) < 0) okVolatileSymbolBars.add(bar);
			}
			masterHighBar.put(symbol, highSymbolBars);
			masterVolatileBar.put(symbol, volatileSymbolBars);
			masterOkVolatileBar.put(symbol, okVolatileSymbolBars);
			pw1.close();
		}
		File fileTrue = new File(outputFilePath + "MasterHighAllAssets.txt");
		PrintWriter pw1 = new PrintWriter(new FileOutputStream(fileTrue));
		for(Map.Entry<String, List<BarsPojo>> entry : masterHighBar.entrySet()) {
			pw1.write("Symbol,Date,Open,Close,High,Low,Volume,Trade,VWAP"+ "\n");
			System.out.println("Symbol,Date,Open,Close,High,Low,Volume,Trade,VWAP");
			for(BarsPojo bar: entry.getValue()) {
				pw1.write(entry.getKey() + "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + "," + bar.getHighPrice() + "," + bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap() + "\n" );
				System.out.println(entry.getKey() + "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + "," + bar.getHighPrice() + "," + bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap());
			}
		}
		pw1.close();
		
		File fileTrue1 = new File(outputFilePath + "MasterVolatileAssets.txt");
		PrintWriter pw2 = new PrintWriter(new FileOutputStream(fileTrue1));
		for(Map.Entry<String, List<BarsPojo>> entry : masterVolatileBar.entrySet()) {
			pw2.write("Symbol,Date,Open,Close,High,Low,Volume,Trade,VWAP"+ "\n");
			System.out.println("Symbol,Date,Open,Close,High,Low,Volume,Trade,VWAP");
			for(BarsPojo bar: entry.getValue()) {
				pw2.write(entry.getKey() + "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + "," + bar.getHighPrice() + "," + bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap() + "\n" );
				System.out.println(entry.getKey() + "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + "," + bar.getHighPrice() + "," + bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap());
			}
		}
		pw2.close();
		
		File fileTrue2 = new File(outputFilePath + "MasterOkVolatileAssets.txt");
		PrintWriter pw3 = new PrintWriter(new FileOutputStream(fileTrue2));
		for(Map.Entry<String, List<BarsPojo>> entry : masterOkVolatileBar.entrySet()) {
			pw3.write("Symbol,Date,Open,Close,High,Low,Volume,Trade,VWAP"+ "\n");
			System.out.println("Symbol,Date,Open,Close,High,Low,Volume,Trade,VWAP");
			for(BarsPojo bar: entry.getValue()) {
				pw3.write(entry.getKey() + "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + "," + bar.getHighPrice() + "," + bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap() + "\n" );
				System.out.println(entry.getKey() + "," + bar.getTime() + "," + bar.getOpenPrice() + "," + bar.getClosePrice() + "," + bar.getHighPrice() + "," + bar.getLowPrice() + "," + bar.getVolume() + "," + bar.getTradeCount() + "," + bar.getVwap());
			}
		}
		pw3.close();
	}
}
