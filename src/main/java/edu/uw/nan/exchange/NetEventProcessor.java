package edu.uw.nan.exchange;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;

/**
 * @author Neil Nevitt
 * Listens for (by joining the multicast group) and processes events received from the exchange. Processing the events consists of propagating them to registered listeners.
 */
public class NetEventProcessor implements Runnable  {

	private static final Logger logger = LoggerFactory.getLogger(NetEventProcessor.class);
	private MulticastSocket sock;
	private EventListenerList listeners;
	/**
	 * Constructor.
	 * @param eventIpAddress - the multicast IP address to connect to
	 * @param eventPort - the multicast port to connect to
	 */
	public NetEventProcessor(String eventIpAddress, int eventPort) {
		try { 
			this.sock = new MulticastSocket(eventPort);
			sock.joinGroup(InetAddress.getByName(eventIpAddress));
		} catch ( IOException e ) {
			logger.warn(String.format("Could create processor for %s", eventIpAddress),e);
		}
	}
	/**
	 * Continuously accepts and processes market and price change events.
	 */
	@Override
	public void run() {
		try {
			while (true ) {
				final byte[] recBuffer = new byte[128];
				final DatagramPacket recPacket = new DatagramPacket( recBuffer, recBuffer.length);
				sock.receive(recPacket);
				final String str = new String(recPacket.getData(),recPacket.getOffset(), recPacket.getLength());
				logger.info(String.format("Recieved packet %s", str));
				
				final Scanner scan = new Scanner(str).useDelimiter(ProtocolConstants.ELEMENT_DELIMITER.toString());
				final String typeStr = scan.next();
				final ExchangeEvent.EventType type = typeStr.equals(
						ProtocolConstants.PRICE_CHANGE_EVENT.toString()) ? ExchangeEvent.EventType.PRICE_CHANGED
								: typeStr.equals(ProtocolConstants.CLOSED_EVENT.toString()) ? ExchangeEvent.EventType.CLOSED : ExchangeEvent.EventType.OPENED;
				
				for ( final ExchangeListener listener : listeners.getListeners(ExchangeListener.class)) {
					if ( type.equals(ProtocolConstants.PRICE_CHANGE_EVENT ) ) {
						final ExchangeEvent priceChange = ExchangeEvent.newPriceChangedEvent(this, scan.next(), scan.nextInt());
						listener.priceChanged(priceChange);
					} else if (type.equals(ProtocolConstants.CLOSED_EVENT) ) {
						final ExchangeEvent closed = ExchangeEvent.newClosedEvent(this);
						listener.exchangeClosed(closed);
					} else if (type.equals(ProtocolConstants.OPEN_EVENT) ) {
						final ExchangeEvent opened = ExchangeEvent.newOpenedEvent(this);
						listener.exchangeOpened(opened);
					} else {
						logger.warn(String.format("Cannot determine event type for %s", typeStr));
					}
				}
			}
		} catch ( final IOException e ) {
			logger.error("Failed to read socket",e);
		} 
		
	}

	/**
	 * Adds a market listener.
	 * @param l - the listener to add
	 */
	public void addExchangeListener( ExchangeListener l) {
		
	}
	/**
	 * Removes a market listener.
	 * @param l - the listener to remove
	 */
	public void removeExchangeListener( ExchangeListener l) {

	}

}
