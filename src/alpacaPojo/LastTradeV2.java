package alpacaPojo;
import alpacaPojo.LastTradePojo;
import alpacaPojo.LastTradePojoV2;

public class LastTradeV2 {
	private String symbol;
	private LastTradePojoV2 trade;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public LastTradePojoV2 getTrade() {
		return trade;
	}
	public void setTrade(LastTradePojoV2 trade) {
		this.trade = trade;
	}

}
