package alpacaPojo;

public class ClockPojo {

	private String timestamp;
	private boolean is_open;
	private String next_open;
	private String next_close;
	@Override
    public String toString() {
        return "{" +
                "timestamp='" + timestamp + '\'' +
                ", is_open='" + is_open + '\'' +
                ", next_open='" + next_open + '\'' +
                ", next_close='" + next_close + '\'' +
                '}';
    }
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public boolean isIs_open() {
		return is_open;
	}
	public void setIs_open(boolean is_open) {
		this.is_open = is_open;
	}
	public String getNext_open() {
		return next_open;
	}
	public void setNext_open(String next_open) {
		this.next_open = next_open;
	}
	public String getNext_close() {
		return next_close;
	}
	public void setNext_close(String next_close) {
		this.next_close = next_close;
	}
}
