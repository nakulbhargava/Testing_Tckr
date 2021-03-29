package alpacaPojo;

public class LastTradePojo {

	private String price;
	private String size;
	private String exchange;
	private String cond1;
	private String cond2;
	private String cond3;
	private String cond4;
	private String timestamp;
	private String easy_to_borrow;
	@Override
    public String toString() {
        return "{" +
                "price='" + price + '\'' +
                ", size='" + size + '\'' +
                ", exchange='" + exchange + '\'' +
                ", cond1='" + cond1 + '\'' +
                ", cond2='" + cond2 + '\'' +
                ", cond3='" + cond3 + '\'' +
                ", cond4='" + cond4 + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", easy_to_borrow='" + easy_to_borrow + '\'' +
                '}';
    }
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getCond1() {
		return cond1;
	}
	public void setCond1(String cond1) {
		this.cond1 = cond1;
	}
	public String getCond2() {
		return cond2;
	}
	public void setCond2(String cond2) {
		this.cond2 = cond2;
	}
	public String getCond3() {
		return cond3;
	}
	public void setCond3(String cond3) {
		this.cond3 = cond3;
	}
	public String getCond4() {
		return cond4;
	}
	public void setCond4(String cond4) {
		this.cond4 = cond4;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getEasy_to_borrow() {
		return easy_to_borrow;
	}
	public void setEasy_to_borrow(String easy_to_borrow) {
		this.easy_to_borrow = easy_to_borrow;
	}
}
