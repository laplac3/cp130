package edu.uw.nan.exchange;

import static edu.uw.nan.exchange.ProtocolConstants.GET_STATE_CMD;
import static edu.uw.nan.exchange.ProtocolConstants.OPEN_STATE;
import static edu.uw.nan.exchange.ProtocolConstants.GET_TICKERS_CMD;
import static edu.uw.nan.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.nan.exchange.ProtocolConstants.GET_QUOTE_CMD;
import static edu.uw.nan.exchange.ProtocolConstants.INVALID_STOCK;
import static edu.uw.nan.exchange.ProtocolConstants.BUY_ORDERS;
import static edu.uw.nan.exchange.ProtocolConstants.SELL_ORDERS;
import static edu.uw.nan.exchange.ProtocolConstants.EXECUTE_TRADE_CMD;
import static edu.uw.nan.exchange.ProtocolConstants.ENCODING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.Order;

/**
 * @author Neil Nevitt
 * Client for interacting with a network accessible exchange. This SocketExchange methods 
 * encode the method request as a string, per ProtocolConstants, and send the command to the 
 * ExchangeNetworkAdapter, receive the response decode it and return the result.
 */
public class ExchangeNetworkProxy implements StockExchange {
	/**
	 * Logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExchangeNetworkProxy.class);
	/**
	 * The net event processor.
	 */
	private NetEventProcessor eventProcessor;
	/**
	 * The command IP address.
	 */
	private String cmdIpAddress;
	/**
	 * The command port.
	 */
	private int cmdPort;
	/**
	 * Constructor.
	 * @param eventIpAddress - the multicast IP address to connect to
	 * @param eventPort - the multicast port to connect to
	 * @param cmdIpAddress - the address the exchange accepts request on
	 * @param cmdPort - the address the exchange accepts request on
	 */
	public ExchangeNetworkProxy(final String eventIpAddress,
            final int eventPort, final String cmdIpAddress, final int cmdPort) {
		this.cmdIpAddress = cmdIpAddress;
		this.cmdPort = cmdPort;
		this.eventProcessor = new NetEventProcessor(eventIpAddress, eventPort);
		Executors.newSingleThreadExecutor().execute(eventProcessor);
	}
	/**
	 * The state of the exchange.
	 * @return true if the exchange is open otherwise false
	 */
	@Override
	public boolean isOpen() {
		String response = "";
		PrintWriter pwriter = null;
		BufferedReader buffer = null;
		try (Socket socket = new Socket(cmdIpAddress, cmdPort) ){
			if ( logger.isInfoEnabled()) {
				logger.info(String.format("Connected to %s:%d", socket.getLocalAddress(), socket.getLocalPort()));
			}
			final InputStream ins = socket.getInputStream();
			final Reader reader = new InputStreamReader(ins, ENCODING);
			buffer = new BufferedReader(reader);
			
			final OutputStream outs = socket.getOutputStream();
			final Writer writer = new OutputStreamWriter(outs, ENCODING);
			pwriter = new PrintWriter(writer, true);
			
			pwriter.println(GET_STATE_CMD);
			response = buffer.readLine();			
		} catch ( final IOException e ) {
			logger.warn("Error in sending command", e);
		}
		
		final boolean state = OPEN_STATE.equals(response);
		return state;
	}
	/**
	 * Gets the ticker symbols for all of the stocks in the traded on the exchange.
	 * @return the stock ticker symbols
	 */
	@Override
	public String[] getTickers() {
		String response = "";
		PrintWriter pwriter = null;
		BufferedReader buffer = null;
		try (Socket socket = new Socket(cmdIpAddress, cmdPort) ){
			if ( logger.isInfoEnabled()) {
				logger.info(String.format("Connected to %s:%d", socket.getLocalAddress(), socket.getLocalPort()));
			}
			final InputStream ins = socket.getInputStream();
			final Reader reader = new InputStreamReader(ins, ENCODING);
			buffer = new BufferedReader(reader);
			
			final OutputStream outs = socket.getOutputStream();
			final Writer writer = new OutputStreamWriter(outs, ENCODING);
			pwriter = new PrintWriter(writer, true);
			
			pwriter.println(GET_TICKERS_CMD);
			response = buffer.readLine();
			
		} catch ( final IOException e ) {
			logger.warn("Error in sending command", e);
		}
	
		final String[] tickers = response.split(ELEMENT_DELIMITER);
		return tickers;
	}
	/**
	 * Gets a stocks current price.
	 * @param ticker - the ticker symbol for the stock
	 * @return the quote, or null if the quote is unavailable.
	 */
	@Override
	public StockQuote getQuote(String ticker) {
		final String command = String.join(ELEMENT_DELIMITER, GET_QUOTE_CMD,ticker);
		
		String response = "";
		PrintWriter pwriter = null;
		BufferedReader buffer = null;
		try (Socket socket = new Socket(cmdIpAddress, cmdPort) ){
			if ( logger.isInfoEnabled()) {
				logger.info(String.format("Connected to %s:%d", socket.getLocalAddress(), socket.getLocalPort()));
			}
			final InputStream ins = socket.getInputStream();
			final Reader reader = new InputStreamReader(ins, ENCODING);
			buffer = new BufferedReader(reader);
			
			final OutputStream outs = socket.getOutputStream();
			final Writer writer = new OutputStreamWriter(outs, ENCODING);
			pwriter = new PrintWriter(writer, true);
			
			pwriter.println(command);
			response = buffer.readLine();
			
		} catch ( final IOException e ) {
			logger.warn("Error in sending command", e);
		}
		
		int price = INVALID_STOCK;
		try {
			price = Integer.parseInt(response);
		} catch ( NumberFormatException n ) {
			logger.warn(String.format("String to int failed %s.", response ),n);
		}
		
		StockQuote quote = null;
		if ( price >= 0 ) {
			quote = new StockQuote(ticker, price);
		}
		return quote;
	}
	/**
	 * Creates a command to execute a trade and sends it to the exchange.
	 * @param order - the order to execute
	 * @return the price the order was executed at
	 */
	@Override
	public int executeTrade(Order order) {
		final String type = ( order.isBuyOrder()) ? BUY_ORDERS : SELL_ORDERS;
		final String command = String.join(
				ELEMENT_DELIMITER,EXECUTE_TRADE_CMD, type, order.getAccountId() , 
				order.getStockTicker(), Integer.toString(order.getNumberOfShares()));
		
		String response = "";
		PrintWriter pwriter = null;
		BufferedReader buffer = null;
		try (Socket socket = new Socket(cmdIpAddress, cmdPort) ){
			if ( logger.isInfoEnabled()) {
				logger.info(String.format("Connected to %s:%d", socket.getLocalAddress(), socket.getLocalPort()));
			}
			final InputStream ins = socket.getInputStream();
			final Reader reader = new InputStreamReader(ins, ENCODING);
			buffer = new BufferedReader(reader);  //good for reading lines
			
			final OutputStream outs = socket.getOutputStream();
			final Writer writer = new OutputStreamWriter(outs, ENCODING);
			pwriter = new PrintWriter(writer, true); // the true auto flushes after adding context.
			
			pwriter.println(command);
			response = buffer.readLine();  // wait until receive a complete line
			
		} catch ( final IOException e ) {
			logger.warn("Error in sending command", e);
		}
		
		int executionPrice = 0;
		try {
			executionPrice = Integer.parseInt(response);
		} catch ( final NumberFormatException e) {
			logger.warn(String.format("String to int failed %s", response),e);
		}
		return executionPrice;
	}
	/**
	 * Adds a market listener. Delegates to the NetEventProcessor.
	 * @param l - the listener to add
	 */
	@Override
	public void addExchangeListener(ExchangeListener l) {
		eventProcessor.addExchangeListener(l);
	}
	/**
	 * Removes a market listener. Delegates to the NetEventProcessor.
	 * @param l - the listener to remove
	 */
	@Override
	public void removeExchangeListener(ExchangeListener l) {
		eventProcessor.removeExchangeListener(l);	
	}
}
