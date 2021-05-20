package REAL;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import alpacaPojo.AccountsPojo;
import alpacaPojo.AssetsPojo;
import alpacaPojo.ClockPojo;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;
import alpacaPojo.TimeframePojo;

public class AlpacaApiCaller extends AlpacaRestParams {
	protected String paperUrl;
	protected String realtimeUrl;
	protected String activeVersion;
	protected String inactiveVersion;
	protected String ordersEndpoint;
	protected String accountEndpoint;
	protected String lastQuoteEndpoint;
	protected String lastTradeEndpoint;
	protected String positionsEndpoint;
	protected String assetsEndpoint;
	protected String clockEndpoint;
	public AlpacaApiCaller(String alpacaKey, String secretId, String alpacaKeyValue, String secretIdValue,
			String headerContentType, String headerContentTypeValue, String alpacaKeyRealTime, String secretIdRealTime, String paperUrl, String realtimeUrl,
			String activeVersion, String inactiveVersion, String ordersEndpoint, String accountEndpoint,
			String lastQuoteEndpoint, String lastTradeEndpoint, String positionsEndpoint, String assetsEndpoint, String clockEndpoint) {
		super(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType, headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime);
		this.paperUrl = paperUrl;
		this.realtimeUrl = realtimeUrl;
		this.activeVersion = activeVersion;
		this.inactiveVersion = inactiveVersion;
		this.ordersEndpoint = ordersEndpoint;
		this.accountEndpoint = accountEndpoint;
		this.lastQuoteEndpoint = lastQuoteEndpoint;
		this.lastTradeEndpoint = lastTradeEndpoint;
		this.positionsEndpoint = positionsEndpoint;
		this.assetsEndpoint = assetsEndpoint;
		this.clockEndpoint = clockEndpoint;
	}
	protected AccountsPojo getAccountDetails() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + accountEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString()); 
		return getJsonObject(response.body(), AccountsPojo.class);
	}
	protected AccountsPojo getRealAccountDetails() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + accountEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString()); 
		return getJsonObject(response.body(), AccountsPojo.class);
	}
	protected ClockPojo getClock() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + clockEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString()); 
		return getJsonObject(response.body(), ClockPojo.class);
	}
	protected OrdersPojo buySymbolQuantity(String symbol, Double qty) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() != 200) {
			System.out.println("Buy Error:" + response.body()); 
		}
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected OrdersPojo buyRealSymbolQuantity(String symbol, Double qty) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() != 200) {
			System.out.println("Buy Error:" + response.body()); 
			return null;
		}
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected OrdersPojo buySymbolLimitOrder(String symbol, Double qty, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"gtc\",\r\n"
				+ "    \"limit_price\": \"" + price + "\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected OrdersPojo buyRealSymbolLimitOrder(String symbol, Double qty, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"gtc\",\r\n"
				+ "    \"limit_price\": \"" + price + "\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected OrdersPojo buySymbolBracketOrder(String symbol, Double qty, Double limitPriceToBuy, Double takeProfit, Double stopLoss) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"day\",\r\n" 
				+ "    \"limit_price\": \"" + limitPriceToBuy + "\",\r\n"
				+ "    \"order_class\": \"bracket\",\r\n"
				+ "    \"take_profit\": {\r\n"
				+ "        \"limit_price\": \"" + takeProfit + "\"\r\n"
				+ "    },\r\n"
				+ "    \"stop_loss\": {\r\n"
				+ "        \"stop_price\": \"" + stopLoss + "\"\r\n"
				+ "    }\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected OrdersPojo buySymbolPrice(String symbol, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"notional\": \""+ price + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected OrdersPojo buyRealSymbolPrice(String symbol, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"notional\": \""+ price + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected String getLiveDataStream(String symbol) throws URISyntaxException, InterruptedException {
		WebSocketClient dataStream =  new WebSocketClient( new URI( "wss://stream.data.alpaca.markets/v2/sip" ) )
		{
			@Override
			public void onMessage( String message ) {
				System.out.print(message);
			}

			@Override
			public void onOpen( ServerHandshake handshake ) {
				System.out.println( "opened connection" );
			}

			@Override
			public void onClose( int code, String reason, boolean remote ) {
				System.out.println( "closed connection" );
			}

			@Override
			public void onError( Exception ex ) {
				ex.printStackTrace();
			}
		};
		dataStream.connect();
		String message = "{\r\n"
				+ "    \"action\": \"" + "auth" + "\",\r\n"
				+ "    \"key\": \""+ alpacaKeyRealTime + "\",\r\n"
				+ "    \"secret\": \"" + secretIdRealTime + "\",\r\n"
				+ "}";
		//String simpleText = "{\"action\": \"auth\, " + "\"key\: " + alpacaKeyRealTime + " , \"secret\" : " + secretIdRealTime +"}";
		dataStream.send(message);
		String subscribe = "{\r\n"
				+ "    \"action\": \"" + "subscribe" + "\",\r\n"
				+ "    \"trades\": [\""+ symbol + "\"],\r\n"
				+ "    \"quotes\": [\"" + symbol + "\"]\r\n"
				+ "}";

		dataStream.send(subscribe);
		Thread.sleep(60000);
		dataStream.close();
		return "Empty";
	}
	protected OrdersPojo sellSymbolQuantity(String symbol, Double qty) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"sell\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected OrdersPojo sellRealSymbolQuantity(String symbol, Double qty) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"sell\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
//		System.out.println(response.body());
//		System.out.println(response.statusCode());
//		System.out.println(response.request());
		if(response.statusCode() != 200) {
			System.out.println("Sell Error:" + response.body()); 
			return null;
		}
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected OrdersPojo sellSymbolLimitOrder(String symbol, Double qty, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"sell\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"gtc\",\r\n"
				+ "    \"limit_price\": \"" + price + "\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected OrdersPojo sellRealSymbolLimitOrder(String symbol, Double qty, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"sell\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"gtc\",\r\n"
				+ "    \"limit_price\": \"" + price + "\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	protected LastTrade getLastTrade(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + inactiveVersion + "/last/stocks/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), LastTrade.class);
	}
	@SuppressWarnings("static-access")
	protected LastQuote getQuote(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + inactiveVersion + "/last_quote/stocks/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), LastQuote.class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo getOrderDetails(String orderId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo getRealOrderDetails(String orderId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() != 200) {
			System.out.println("Buy Error:" + response.body()); 
			return null;
		}
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	protected void deleteOrder(String orderId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.DELETE()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		//TODO: return valid responses based on error codes
		//return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	protected void deleteRealOrder(String orderId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.DELETE()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		//TODO: return valid responses based on error codes
		//return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo replaceStopLossOrder(String orderId, String stopPrice) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"stop_price\": \"" + stopPrice + "\"\r\n"
				+ "}";
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo[] getOrders() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?status=all&limit=500"))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo[] getRealOrders() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint + "?status=all&limit=500"))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo[] getOpenOrders(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?status=open&symbol="+symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo[] getRealOpenOrders(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint + "?status=open&symbol="+symbol))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo[] getOrdersUntil(String untilTimestamp) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?status=all&until="+untilTimestamp))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo[] getSymbolOrdersWithTime(String symbol, String afterTimestamp, String untilTimestamp) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?symbol="+symbol+"&status=all&until="+untilTimestamp+"&after="+afterTimestamp))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	protected OrdersPojo[] getSymbolOpenOrders(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?status=open&symbols="+symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	protected PositionsPojo[] getOpenPositions() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + positionsEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), PositionsPojo[].class);
	}
	@SuppressWarnings("static-access")
	protected PositionsPojo getSymbolPosition(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + positionsEndpoint + "/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), PositionsPojo.class);
	}
	@SuppressWarnings("static-access")
	protected PositionsPojo getRealSymbolPosition(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + positionsEndpoint + "/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), PositionsPojo.class);
	}
	@SuppressWarnings("static-access")
	protected AssetsPojo getAssetSymbol(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + assetsEndpoint + "/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), AssetsPojo.class);
	}
	@SuppressWarnings("static-access")
	protected TimeframePojo getTimeframe(String symbol, String startTime, String endTime, String timeframe) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + "/stocks/" + symbol + "/bars?start=" + startTime + "&end=" + endTime + "&timeframe=" + timeframe))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), TimeframePojo.class);
	}
	@SuppressWarnings("static-access")
	protected AssetsPojo[] getAssets() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + assetsEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), AssetsPojo[].class);
	}

	protected <T> T getJsonObject(String jsonString, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		T  responseObject = (T) mapper.readValue(jsonString,  objectClass);
		return responseObject;
	}
	protected <T> T[] getJsonObjectArray(String jsonString, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		T[]  responseObject = (T[]) mapper.readValue(jsonString,  objectClass);
		return responseObject;
	}
}
