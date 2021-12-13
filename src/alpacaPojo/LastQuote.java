package alpacaPojo;
import alpacaPojo.LastQuotePojo;

public class LastQuote {
	private String status;
	private String symbol;
	private LastQuotePojo last;
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
	public LastQuotePojo getLast() {
		return last;
	}
	public void setLast(LastQuotePojo last) {
		this.last = last;
	}
}
