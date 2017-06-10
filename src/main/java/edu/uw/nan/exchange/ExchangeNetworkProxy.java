package edu.uw.nan.exchange;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.Order;

/**
 * @author Neil Nevitt
 * Client for interacting with a network accessible exchange. This SocketExchange methods encode the method request as a string, per ProtocolConstants, and send the command to the ExchangeNetworkAdapter, receive the response decode it and return the result.
 *
 */
public class ExchangeNetworkProxy implements StockExchange {
	private static final Logger logger = LoggerFactory.getLogger(ExchangeNetworkProxy.class);
	private InetAddress address;
	private MulticastSocket eventSock = null;
	private NetEventProcessor cmdProcessor;
	private Socket server;
	private EventListenerList listeners = new EventListenerList();
	private final ExecutorService exec = Executors.newSingleThreadExecutor();
	/**
	 * Constructor.
	 * @param eventIpAddress - the multicast IP address to connect to
	 * @param eventPort - the multicast port to connect to
	 * @param cmdIpAddress - the address the exchange accepts request on
	 * @param cmdPort - the address the exchange accepts request on
	 */
	public ExchangeNetworkProxy(String eventIpAddress,
            int eventPort, String cmdIpAddress, int cmdPort) {
		try {
			address =  InetAddress.getByName(eventIpAddress);
			cmdProcessor = new NetEventProcessor(eventIpAddress,eventPort);
			
			exec.execute(cmdProcessor);
			server = new Socket(cmdIpAddress, cmdPort );
			
		} catch (IOException e ) {
			logger.error(" Cannot connect to socket",e);
		} finally {
			if ( eventSock != null ) {
				eventSock.close();
			}
		}
	}
	/**
	 * The state of the exchange.
	 * @return true if the exchange is open otherwise false
	 */
	@Override
	public boolean isOpen() {
		boolean isOpen = false;
		try {
			final Scanner scan = new Scanner(this.toString()).useDelimiter(ProtocolConstants.ELEMENT_DELIMITER.toString());
			isOpen = scan.next().equals(ProtocolConstants.OPEN_STATE.toString());
		} catch ( Exception e ) {
			logger.error("Can't call event processor.",e);
		}
		return true;
	}
	/**
	 * Gets the ticker symbols for all of the stocks in the traded on the exchange.
	 * @return the stock ticker symbols
	 */
	@Override
	public String[] getTickers() {
		final ArrayList<String> tickers = new ArrayList<>();
		try {
			final Scanner scan = new Scanner(this.toString()).useDelimiter(ProtocolConstants.ELEMENT_DELIMITER.toString());
			while ( scan.hasNext() ) {
				tickers.add(scan.next());
			}
		} catch (Exception e) {
			logger.error("Can't call event processor.",e);
		}
		return tickers.toArray(new String[0]);
	}
	/**
	 * Gets a stocks current price.
	 * @param ticker - the ticker symbol for the stock
	 * @return the quote, or null if the quote is unavailable.
	 */
	@Override
	public StockQuote getQuote(String ticker) {
		StockQuote quote = null;
		try {
			quote = new StockQuote(ticker, Integer.valueOf(this.toString()));
		} catch ( Exception e ) {
			logger.error("Can't call event processor.",e);
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
		int price = 0;
		try {
			price = Integer.valueOf(this.toString());
		} catch ( final Exception e) {
			logger.error(String.format("Failed to execute orderId = %d", order.getOrderId()),e);
		}
		return price;
	}
	/**
	 * Adds a market listener. Delegates to the NetEventProcessor.
	 * @param l - the listener to add
	 */
	@Override
	public void addExchangeListener(ExchangeListener l) {
		listeners.add(ExchangeListener.class, l);
	}
	/**
	 * Removes a market listener. Delegates to the NetEventProcessor.
	 * @param l - the listener to remove
	 */
	@Override
	public void removeExchangeListener(ExchangeListener l) {
		listeners.remove(ExchangeListener.class, l);
		
	}

}
