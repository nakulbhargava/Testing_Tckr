package alpacaPojo;
import alpacaPojo.LastTradePojo;

public class LastTrade {
	private String status;
	private String symbol;
	private LastTradePojo last;
	@Override
    public String toString() {
        return "{" +
                "status='" + status + '\'' +
                ", symbol='" + symbol + '\'' +
                ", last='" + last + '\'' +
                '}';
    }
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public LastTradePojo getLast() {
		return last;
	}
	public void setLast(LastTradePojo last) {
		this.last = last;
	}
}
