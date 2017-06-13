package edu.uw.nan.exchange;

/**
 * @author Neil Nevitt
 * 
 */
public final class ProtocolConstants {
	/**
	 * 
	 */
	public static final String OPEN_EVENT = "OPEN_EVENT";
	/**
	 * 
	 */
	public static final String CLOSED_EVENT = "CLOSED_EVENT";
	/**
	 * 
	 */
	public static final String PRICE_CHANGE_EVENT = "PRICE_CHANGE_EVENT";
	
	/**
	 * 
	 */
	public static final String GET_TICKERS_CMD = "GET_TICKERS_CMD";
	/**
	 * 
	 */
	public static final String GET_QUOTE_CMD = "GET_QUOTE_CMD";
	/**
	 * 
	 */
	public static final String GET_STATE_CMD = "GET_STATE_CMD";
	/**
	 * 
	 */
	public static final String EXECUTE_TRADE_CMD = "EXECUTE_TRADE_CMD";
	
	/**
	 * 
	 */
	public static final String OPEN_STATE = "OPEN";
	/**
	 * 
	 */
	public static final String CLOSED_STATE = "CLOSED";
	/**
	 * 
	 */
	public static final String PRICE_CHANGED = "PRICE_CHANGED";
		
	/**
	 * 
	 */
	public static final String BUY_ORDERS = "BUY_ORDERS";
	/**
	 * 
	 */
	public static final String SELL_ORDERS = "SELL_ORDERS";
	
	/**
	 * 
	 */
	public static final String ELEMENT_DELIMITER = ":";
	
	/**
	 * 
	 */
	public static final int EVENT_ELEMENT = 0;
	/**
	 * 
	 */
	public static final int PRICE_CHANGE_EVNT_TICKER_ELEMENT = 1;
	/**
	 * 
	 */
	public static final int PRICE_CHANGE_EVNT_PRICE_ELEMENT = 2;
	
	/**
	 * 
	 */
	public static final int CMD_ELEMENT = 0;
	/**
	 * 
	 */
	public static final int QUOTE_CMD_TICKER_ELEMENT = 1;
	/**
	 * 
	 */
	public static final int EXECUTE_TRADE_CMD_TYPE_ELEMENT = 1;
	/**
	 * 
	 */
	public static final int EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT = 2;
	/**
	 * 
	 */
	public static final int EXECUTE_TRADE_CMD_TICKER_ELEMENT = 3;
	/**
	 * 
	 */
	public static final int EXECUTE_TRADE_CMD_SHARES_ELEMENT = 4;
	/**
	 * 
	 */
	public static final int INVALID_STOCK = -1;
	/**
	 * 
	 */
	public static final String ENCODING = "ISO-8859-1";
	
	/**
	 * Constructor. 
	 */
	public ProtocolConstants() {
		
	}
}
