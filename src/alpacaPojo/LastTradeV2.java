package alpacaPojo;
import alpacaPojo.LastTradePojo;
import alpacaPojo.LastTradePojoV2;

public class LastTradeV2 {
	private String symbol;
	private LastTradePojoV2[] trades;
	public LastTradePojoV2[] getTrades() {
		return trades;
	}
	public void setTrades(LastTradePojoV2[] trades) {
		this.trades = trades;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
