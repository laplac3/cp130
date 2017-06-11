package edu.uw.nan.exchange;

import static edu.uw.nan.exchange.ProtocolConstants.BUY_ORDERS;
import static edu.uw.nan.exchange.ProtocolConstants.ENCODING;
import static edu.uw.nan.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.nan.exchange.ProtocolConstants.CMD_ELEMENT;
import static edu.uw.nan.exchange.ProtocolConstants.GET_STATE_CMD;
import static edu.uw.nan.exchange.ProtocolConstants.GET_TICKERS_CMD;
import static edu.uw.nan.exchange.ProtocolConstants.GET_QUOTE_CMD;
import static edu.uw.nan.exchange.ProtocolConstants.EXECUTE_TRADE_CMD;
import static edu.uw.nan.exchange.ProtocolConstants.OPEN_STATE;
import static edu.uw.nan.exchange.ProtocolConstants.CLOSED_STATE;
import static edu.uw.nan.exchange.ProtocolConstants.QUOTE_CMD_TICKER_ELEMENT;
import static edu.uw.nan.exchange.ProtocolConstants.INVALID_STOCK;
import static edu.uw.nan.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_TYPE_ELEMENT;
import static edu.uw.nan.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_TICKER_ELEMENT;
import static edu.uw.nan.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_SHARES_ELEMENT;
import static edu.uw.nan.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ext.framework.order.Order;

/**
 * @author Neil Nevitt
 * An instance of this class is dedicated to executing commands received from clients.
 */
public class CommandHandler implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(CommandHandler.class);
	private final StockExchange realExchange;
	private Socket sock;
	/**
	 * Constructor
	 * @param sock - the socket for communication with the client.
	 * @param realExchange - the "real" exchange to dispatch commands to.
	 */
	public CommandHandler( Socket sock, StockExchange realExchange ) {
		this.sock = sock;
		this.realExchange = realExchange;
	}
	/**
	 * Process the Command.
	 */
	@Override
	public void run() {
		try {
			final InputStream ins = sock.getInputStream();
			final Reader reader = new InputStreamReader(ins, ENCODING );
			final BufferedReader bReader = new BufferedReader(reader);
			
			final OutputStream outs = sock.getOutputStream();
			final Writer writer = new OutputStreamWriter( outs, ENCODING );
			final PrintWriter pwriter = new PrintWriter(writer, true ); 
			
			String message = bReader.readLine();
			if (message == null ) {
				message = "";
			}
			if ( logger.isInfoEnabled() ) {
				logger.info(String.format("Recieved message command %s", message ));
			}
			final String[] members = message.split(ELEMENT_DELIMITER);
			final String command = members[CMD_ELEMENT];
			
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Processing command %s.", command));
			}
			switch ( command ) {
				case GET_STATE_CMD:
					final String response = realExchange.isOpen() ? OPEN_STATE : CLOSED_STATE;
					pwriter.println(response);
					break;
					
				case GET_TICKERS_CMD:
					final String[] tickers = realExchange.getTickers();
					final String tickersString = String.join(ELEMENT_DELIMITER, tickers);
					pwriter.println(tickersString);
					break;
					
				case GET_QUOTE_CMD:
					String ticker = members[QUOTE_CMD_TICKER_ELEMENT];
					final StockQuote quote = realExchange.getQuote(ticker);
					int price = ( quote == null ) ? INVALID_STOCK : quote.getPrice();
					pwriter.println(price);
					break;
					
				case EXECUTE_TRADE_CMD:
					if ( realExchange.isOpen() ) {
						final String oType = members[EXECUTE_TRADE_CMD_TYPE_ELEMENT];
						final String iD = members[EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT];
						String tick = members[EXECUTE_TRADE_CMD_TICKER_ELEMENT];
						final String shares = members[EXECUTE_TRADE_CMD_SHARES_ELEMENT];
						int qty = -1;
						try {
							qty = Integer.parseInt(shares);
						} catch ( final NumberFormatException e ) {
							logger.warn(String.format("Cannot convert string to int %s", shares));
						}
						Order order;
						if (BUY_ORDERS.equals(oType) ) {
							order = new MarketBuyOrder( iD, qty, tick );
						} else {
							order = new MarketSellOrder( iD, qty, tick );
						}
						
						int prices = realExchange.executeTrade(order);
						pwriter.println(prices);
					} else {
						pwriter.println(0);
					}
					
					break;
					
				default:
					logger.error(String.format("Cannot recognize command", command));
					break;
			}
		} catch ( final IOException ex ) {
			logger.error("Error in sending response", ex);
		} finally {
			try {
				if ( sock != null ) {
					sock.close();
				}
			} catch ( final IOException exc ) {
				logger.info("Cannot close socket",exc);
			}
		}
	}
	

}
