package alpacaPojo;

public class SnapshotPojo {
	String symbol;
	LastTradePojoV2 latestTrade;
	LastQuotePojoV2 latestQuote;
	BarsPojo minuteBar;
	BarsPojo dailyBar;
	BarsPojo prevDailyBar;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public LastTradePojoV2 getLatestTrade() {
		return latestTrade;
	}
	public void setLatestTrade(LastTradePojoV2 latestTrade) {
		this.latestTrade = latestTrade;
	}
	public LastQuotePojoV2 getLatestQuote() {
		return latestQuote;
	}
	public void setLatestQuote(LastQuotePojoV2 latestQuote) {
		this.latestQuote = latestQuote;
	}
	public BarsPojo getMinuteBar() {
		return minuteBar;
	}
	public void setMinuteBar(BarsPojo minuteBar) {
		this.minuteBar = minuteBar;
	}
	public BarsPojo getDailyBar() {
		return dailyBar;
	}
	public void setDailyBar(BarsPojo dailyBar) {
		this.dailyBar = dailyBar;
	}
	public BarsPojo getPrevDailyBar() {
		return prevDailyBar;
	}
	public void setPrevDailyBar(BarsPojo prevDailyBar) {
		this.prevDailyBar = prevDailyBar;
	}
}
