package alpacaPojo;
import alpacaPojo.LastQuotePojo;
import alpacaPojo.LastQuotePojoV2;

public class LastQuoteV2 {
	private String symbol;
	private LastQuotePojoV2[] quotes;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public LastQuotePojoV2[] getQuotes() {
		return quotes;
	}
	public void setQuotes(LastQuotePojoV2[] quotes) {
		this.quotes = quotes;
	}
	
}
