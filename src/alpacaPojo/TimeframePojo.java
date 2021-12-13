package alpacaPojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeframePojo {

	private BarsPojo bars[];
	private String symbol;
	private String nextPageToken;

	@Override
    public String toString() {
        return "{" +
                "bars='" + bars + '\'' +
                ", symbol='" + symbol + '\'' +
                ", nextPageToken='" + nextPageToken + '\'' +
                '}';
    }

	public BarsPojo[] getBars() {
		return bars;
	}

	public void setBars(BarsPojo[] bars) {
		this.bars = bars;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	@JsonProperty("next_page_token")
	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}
	
}
