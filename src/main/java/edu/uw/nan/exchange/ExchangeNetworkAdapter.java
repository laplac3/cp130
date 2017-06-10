package edu.uw.nan.exchange;

import static edu.uw.nan.exchange.ProtocolConstants.CLOSED_EVENT;
import static edu.uw.nan.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.nan.exchange.ProtocolConstants.ENCODING;
import static edu.uw.nan.exchange.ProtocolConstants.OPEN_EVENT;
import static edu.uw.nan.exchange.ProtocolConstants.PRICE_CHANGE_EVENT;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 * @author Neil Nevitt
 * Provides a network interface to an exchange.
 */
public class ExchangeNetworkAdapter implements ExchangeAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ExchangeNetworkAdapter.class);
	private final StockExchange realExchng;
    private MulticastSocket eventSocket;
    private DatagramPacket datagramPacket;
	private CommandListener listener;
	/**
	 * @param exchng - the exchange used to service the network requests
	 * @param multicastIP - the ip address used to propagate price changes
	 * @param multicastPort - the ip port used to propagate price changes
	 * @param commandPort - the ports for listening for commands
	 * @throws UnknownHostException - if unable to resolve multicast IP address
	 * @throws SocketException - if an error occurs on a socket operation
	 */
	public ExchangeNetworkAdapter(final StockExchange exchng, final String multicastIP,
            final int multicastPort,final int commandPort ) throws UnknownHostException,
            SocketException {
		this.realExchng = exchng;

		final InetAddress multicastGroup = InetAddress.getByName(multicastIP);
		final byte[] buffer = {};
		datagramPacket = new DatagramPacket( buffer, 0, multicastGroup, multicastPort);
		try {  
			eventSocket = new MulticastSocket();
			eventSocket.setTimeToLive(2);
			if ( logger.isInfoEnabled() ) {
				logger.info("Multicats events:" + multicastIP + ":" + multicastGroup );
			}

		} catch ( IOException e ) {
			logger.error( "Cannot open socket", e);
		}
		listener = new CommandListener(commandPort, exchng);
		Executors.newSingleThreadExecutor().execute(listener);
		this.realExchng.addExchangeListener(this);
	}
	/**
	 * The exchange has opened and prices are adjusting - add listener to receive price change events from the exchange and multicast them to brokers.
	 * @param event - the event
	 */
	@Override
	public void exchangeOpened(ExchangeEvent event) {
		logger.info("Exchange is open.");
		try {
			multiEvent(OPEN_EVENT);
		} catch ( final IOException e ) {
			logger.error("Error when joining price group",e);
		}
		
	}
	/**
	 * The exchange has closed - notify clients and remove price change listener.
	 * @param event - the event
	 */
	@Override
	public void exchangeClosed( ExchangeEvent event ) {
		logger.info("Exchange is open.");
		try {
			multiEvent(CLOSED_EVENT);
		} catch ( final IOException e ) {
			logger.error("Error when closing multicast.",e);
		}
	}

	/**
	 * Processes price change events.
	 * @param event - the event
	 */
	@Override
	public void priceChanged(ExchangeEvent event) {
		final String symbol = event.getTicker();
		final int price = event.getPrice();
		final String msg = String.join(ELEMENT_DELIMITER, PRICE_CHANGE_EVENT, symbol, Integer.toString(price));
		logger.info(msg);
		try {
			multiEvent(msg);
		} catch (final IOException e ) {
			logger.error("Erro in multicast price change.",e);
		}
	}
	/**
	 * Close the adapter.
	 */
	@Override
	public void close() {
		realExchng.removeExchangeListener(this);
		listener.terminate();
		eventSocket.close();
	}
	
	private synchronized void multiEvent( final String msg) throws IOException {
		final byte[] buffer = msg.getBytes(ENCODING);
		datagramPacket.setData(buffer);
		datagramPacket.setLength(buffer.length);
		eventSocket.send(datagramPacket);
	}

}
