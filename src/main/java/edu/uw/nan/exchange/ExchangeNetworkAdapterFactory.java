package edu.uw.nan.exchange;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.NetworkExchangeAdapterFactory;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 * @author Neil Nevitt
 * A NetworkExchangeAdapterFactory implementation for creating ExchangeNetworkAdapter instances.
 */
public class ExchangeNetworkAdapterFactory implements NetworkExchangeAdapterFactory {
	private static final Logger logger = LoggerFactory.getLogger(ExchangeNetworkAdapterFactory.class);
	/**
	 * Constructor
	 */
	public ExchangeNetworkAdapterFactory() {
	}
	/**
	 * Instantiates an ExchangeNetworkAdapter.
	 * @param exchange - the underlying real exchange
	 * @param multicastIP - the multicast ip address used to distribute events
	 * @param multicastPort - the port used to distribute events
	 * @param commandPort - the listening port to be used to accept command requests
	 * @return a newly instantiated ExchangeNetworkAdapter, or null if instantiation fails
	 */
	@Override
	public ExchangeAdapter newAdapter(StockExchange exchange, String multicastIP, int multicastPort, int commandPort) {
		
		ExchangeAdapter adapter = null;
		try {
			adapter = new ExchangeNetworkAdapter(exchange, multicastIP, multicastPort, commandPort);
		} catch (UnknownHostException e) {
			logger.error(String.format("Cannot resolve address for %s", multicastIP), e);
		} catch (SocketException e ) {
			logger.error(String.format("Cannot open socket for %s:%d", multicastIP, multicastPort), e);
		}
		return adapter;
		
	}

}
