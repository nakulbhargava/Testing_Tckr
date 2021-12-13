package alpacaPojo;

public class AlpacaRestParams {
	protected String alpacaKey;
	protected String secretId;
	protected String alpacaKeyValue;
	protected String secretIdValue;
	protected String headerContentType;
	protected String headerContentTypeValue;
	protected String alpacaKeyRealTime;
	protected String secretIdRealTime;

	public AlpacaRestParams(String alpacaKey, String secretId, String alpacaKeyValue, String secretIdValue,
			String headerContentType, String headerContentTypeValue,
			String alpacaKeyRealTime, String secretIdRealTime) {
		super();
		this.alpacaKey = alpacaKey;
		this.secretId = secretId;
		this.alpacaKeyValue = alpacaKeyValue;
		this.secretIdValue = secretIdValue;
		this.headerContentType = headerContentType;
		this.headerContentTypeValue = headerContentTypeValue;
		this.alpacaKeyRealTime = alpacaKeyRealTime;
		this.secretIdRealTime = secretIdRealTime;
	}
	//No Need to have getters and setters once these params are set, they are constant
/*	public String getAlpacaKey() {
		return alpacaKey;
	}
	public void setAlpacaKey(String alpacaKey) {
		this.alpacaKey = alpacaKey;
	}
	public String getSecretId() {
		return secretId;
	}
	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}
	public String getAlpacaKeyValue() {
		return alpacaKeyValue;
	}
	public void setAlpacaKeyValue(String alpacaKeyValue) {
		this.alpacaKeyValue = alpacaKeyValue;
	}
	public String getSecretIdValue() {
		return secretIdValue;
	}
	public void setSecretIdValue(String secretIdValue) {
		this.secretIdValue = secretIdValue;
	}
	public String getHeaderContentType() {
		return headerContentType;
	}
	public void setHeaderContentType(String headerContentType) {
		this.headerContentType = headerContentType;
	}
	public String getHeaderContentTypeValue() {
		return headerContentTypeValue;
	}
	public void setHeaderContentTypeValue(String headerContentTypeValue) {
		this.headerContentTypeValue = headerContentTypeValue;
	}*/
}
