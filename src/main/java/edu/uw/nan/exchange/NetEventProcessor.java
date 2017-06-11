package edu.uw.nan.exchange;

import static edu.uw.nan.exchange.ProtocolConstants.ENCODING;
import static edu.uw.nan.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.nan.exchange.ProtocolConstants.EVENT_ELEMENT;
import static edu.uw.nan.exchange.ProtocolConstants.OPEN_EVENT;
import static edu.uw.nan.exchange.ProtocolConstants.CLOSED_EVENT;
import static edu.uw.nan.exchange.ProtocolConstants.PRICE_CHANGE_EVENT;
import static edu.uw.nan.exchange.ProtocolConstants.PRICE_CHANGE_EVNT_TICKER_ELEMENT;
import static edu.uw.nan.exchange.ProtocolConstants.PRICE_CHANGE_EVNT_PRICE_ELEMENT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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
	
	private static final int BUFFER_SIZE = 1024;
	private String eventIpAddress;
	private int eventPort;
	private EventListenerList listeners = new EventListenerList();
	/**
	 * Constructor.
	 * @param eventIpAddress - the multicast IP address to connect to
	 * @param eventPort - the multicast port to connect to
	 */
	public NetEventProcessor(String eventIpAddress, int eventPort) {
		this.eventIpAddress = eventIpAddress;
		this.eventPort = eventPort;
	}
	/**
	 * Continuously accepts and processes market and price change events.
	 */
	@Override
	public void run() {
		try (MulticastSocket eventSocket = new MulticastSocket(eventPort) ) {
			final InetAddress eventGroup = InetAddress.getByName(eventIpAddress);
			eventSocket.joinGroup(eventGroup);
			if ( logger.isInfoEnabled() ) {
				logger.info("Recieving events from" + eventIpAddress + ":" + eventGroup);
			}
			final byte[] buffer = new byte[BUFFER_SIZE];
			final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			while ( true ) {
				eventSocket.receive(packet);
				final String message = new String(packet.getData(), packet.getOffset(), packet.getLength(), ENCODING );
				final String[] members = message.split(ELEMENT_DELIMITER);
				final String type = members[EVENT_ELEMENT];
				
				switch ( type ) {
					case OPEN_EVENT:
						firelisteners(ExchangeEvent.newOpenedEvent(this));
						break;
					
					case CLOSED_EVENT:
						firelisteners(ExchangeEvent.newClosedEvent(this));
						break;
					
					case PRICE_CHANGE_EVENT:
						final String ticker = members[PRICE_CHANGE_EVNT_TICKER_ELEMENT];
						final String priceString = members[PRICE_CHANGE_EVNT_PRICE_ELEMENT];
						int price = -1;
						
						try {
							price= Integer.parseInt(priceString);
						} catch ( final NumberFormatException n ) {
							logger.warn(String.format("String to int conversion failed for %s", eventGroup), n);
						}
						firelisteners(ExchangeEvent.newPriceChangedEvent(this, ticker, price));
						break;
						
					default:
						break;
				}
			}
		} catch (IOException e ) {
			logger.warn("Socket error",e);
		}
				
		
	}

	/**
	 * Adds a market listener.
	 * @param l - the listener to add
	 */
	public void addExchangeListener( ExchangeListener l) {
		listeners.add(ExchangeListener.class, l);
	}
	/**
	 * Removes a market listener.
	 * @param l - the listener to remove
	 */
	public void removeExchangeListener( ExchangeListener l) {
		listeners.remove(ExchangeListener.class, l);
	}

	
	private void firelisteners( final ExchangeEvent event ) {
		ExchangeListener[] list;
		list = listeners.getListeners(ExchangeListener.class );
		
		for ( ExchangeListener l : list ) {
			switch ( event.getEventType() ) {
				case OPENED:
					l.exchangeOpened(event);
				case CLOSED:
					l.exchangeClosed(event);
				case PRICE_CHANGED:
					l.priceChanged(event);
			default:
				break;
					
			}
		}
	}

}
