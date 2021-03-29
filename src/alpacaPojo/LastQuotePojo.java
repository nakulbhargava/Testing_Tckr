package alpacaPojo;

public class LastQuotePojo {

	private String askprice;
	private String asksize;
	private String askexchange;
	private String bidprice;
	private String bidsize;
	private String bidexchange;
	private String timestamp; 
	@Override
    public String toString() {
        return "{" +
                "askprice='" + askprice + '\'' +
                ", asksize='" + asksize + '\'' +
                ", askexchange='" + askexchange + '\'' +
                ", bidprice='" + bidprice + '\'' +
                ", bidsize='" + bidsize + '\'' +
                ", bidexchange='" + bidexchange + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
	public String getAskprice() {
		return askprice;
	}
	public void setAskprice(String askprice) {
		this.askprice = askprice;
	}
	public String getAsksize() {
		return asksize;
	}
	public void setAsksize(String asksize) {
		this.asksize = asksize;
	}
	public String getAskexchange() {
		return askexchange;
	}
	public void setAskexchange(String askexchange) {
		this.askexchange = askexchange;
	}
	public String getBidprice() {
		return bidprice;
	}
	public void setBidprice(String bidprice) {
		this.bidprice = bidprice;
	}
	public String getBidsize() {
		return bidsize;
	}
	public void setBidsize(String bidsize) {
		this.bidsize = bidsize;
	}
	public String getBidexchange() {
		return bidexchange;
	}
	public void setBidexchange(String bidexchange) {
		this.bidexchange = bidexchange;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
