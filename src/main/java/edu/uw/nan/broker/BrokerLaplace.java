package edu.uw.nan.broker;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import edu.uw.nan.account.AccountLaplace;

/**
 * @author Neil Nevitt
 * An implementation of the Broker interface, provides a full implementation less the creation of the order manager and market queue.
 *
 */
public class BrokerLaplace implements 
	edu.uw.ext.framework.broker.Broker, 
	edu.uw.ext.framework.exchange.ExchangeListener {

	private static final Logger logger = LoggerFactory.getLogger(BrokerLaplace.class);
	/**
	 * marketOrders
	 * The market order queue.
	 */
	protected edu.uw.ext.framework.broker.OrderQueue<Boolean,edu.uw.ext.framework.order.Order> marketOrders;
	private String brokerName;
	private edu.uw.ext.framework.exchange.StockExchange exchg;
	private edu.uw.ext.framework.account.AccountManager acctMgr;
	private Map<String, OrderManager> orderManagers;
	
	/**
	 * Constructor for sub classes
	 * @param brokerName - name of the broker
	 * @param exchg - the stock exchange to be used by the broker
	 * @param acctMgr - the account manager to be used by the broker
	 */
	protected BrokerLaplace(String brokerName, StockExchange exchg, AccountManager acctMgr) {
		super();
		this.brokerName = brokerName;
		this.exchg = exchg;
		this.acctMgr = acctMgr;
	}

//	/**
//	 * Constructor
//	 * @param brokerName - name of the broker
//	 * @param exchg - the stock exchange to be used by the broker
//	 * @param acctMgr - the account manager to be used by the broker
//	 */
//	public BrokerLaplace(String brokerName, StockExchange exchg, AccountManager acctMgr) {
//		super();
//		this.brokerName = brokerName;
//		this.exchg = exchg;
//		this.acctMgr = acctMgr;
//	}
	
	/**
	 * Fetch the stock list from the exchange and initialize an order manager for each stock. Only to be used during construction.
	 */
	protected final void initializeOrderManagers() {
		
	}

	/**
	 * Upon the exchange opening sets the market dispatch filter threshold.
	 * @param event - the exchange (closed) event
	 */
	@Override
	public final void exchangeClosed(ExchangeEvent event) {
		checkFields();
		marketOrders.setThreshold(false);
		logger.info("Market is closed.");
	}
	/**
	 * Upon the exchange opening sets the market dispatch filter threshold and processes any available orders.
	 * @param event - the exchange (open) event
	 */
	@Override
	public final void exchangeOpened(ExchangeEvent event) {
		checkFields();
		marketOrders.setThreshold(true);;
		marketOrders.dispatchOrders();
		logger.info("Market is open.");
		
	}
	/**
	 * Upon the exchange opening sets the market dispatch filter threshold and processes any available orders.
	 * @param event - the price change event
	 */
	@Override
	public void priceChanged(ExchangeEvent event) {
		orderManagers.get(event.getTicker()).adjustPrice(event.getPrice());
		
	}
	/**
	 * Release broker resources.
	 * @throws edu.uw.ext.framework.broker.BrokerException - if the operation fails
	 */
	@Override
	public void close() throws BrokerException {
		exchg.removeExchangeListener(this);
		try {
			acctMgr.close();
		} catch ( AccountException e ) {
			logger.warn(String.format("Cannot close account manager"), e);
			throw new BrokeException(e);
		}
		orderManagers = null;
	}
	/**
	 * Create an account with the broker.
	 * @param username - the user or account name for the account
	 * @param password - the password for the new account
	 * @param balance - the initial account balance in cents
	 * @return the new account
	 * @throws edu.uw.ext.framework.broker.BrokerException - if unable to create account
	 */
	@Override
	public final Account createAccount(String username, String password, int balance) throws BrokerException {
		checkFields();
		return null;
	}
	/**
	 * Delete an account with the broker.
	 * @param username - the user or account name for the account
	 * @throws edu.uw.ext.framework.broker.BrokerException - if unable to delete account
	 */
	@Override
	public final void deleteAccount(String username) throws BrokerException {
		checkFields();
		
	}
	/**
	 * Locate an account with the broker. The username and password are first verified and the account is returned.
	 * @param username - the user or account name for the account
	 * @param password - the password for the new account
	 * @return the account
	 * @throws edu.uw.ext.framework.broker.BrokerException - username and password are invalid
	 */
	@Override
	public Account getAccount(String username, String password) throws BrokerException {
		checkFields();
		Account account = null;
		try {
			acctMgr.validateLogin(username,password);
			account = acctMgr.getAccount(username);
		} catch (AccountException e) {
			logger.warn(String.format("Password does not match.", username));
		}
		return account;
	 
	}

	/**
	 * Get the name of the broker.
	 * @return the name of the broker
	 */
	@Override
	public final String getName() {
		
		return this.brokerName;
	}
	/**
	 * Place an order with the broker.
	 * @param order - the order being placed with the broker.
	 */
	@Override
	public void placeOrder(MarketBuyOrder order) throws BrokerException {
		checkFields();
		
	}
	/**
	 * Place an order with the broker.
	 * @param order - the order being placed with the broker 
	 */
	@Override
	public void placeOrder(MarketSellOrder order) throws BrokerException {
		checkFields();
		
	}
	/**
	 * Place an order with the broker.
	 * @param order - the order being placed with the broker
	 * @throws edu.uw.ext.framework.broker.BrokerException - if unable to place order
	 */
	@Override
	public void placeOrder(StopBuyOrder order) throws BrokerException {
		checkFields();
		
	}
	/**
	 * Place an order with the broker.
	 * @param order - the order being placed with the broker
	 * @throws edu.uw.ext.framework.broker.BrokerException - if unable to place order
	 */
	@Override
	public void placeOrder(StopSellOrder order) throws BrokerException {
		checkFields();
		
		
	}
	/**
	 * Get a price quote for a stock.
	 * @param symbol - the stocks ticker symbol
	 * @return the quote
	 * @throws edu.uw.ext.framework.broker.BrokerException - if unable to obtain quote
	 */
	@Override
	public StockQuote requestQuote(String symbol) throws BrokerException {
		checkFields();
		return null;
	}
	
	private void checkFields() {
		if ( brokerName == null || exchg == null || marketOrders == null || acctMgr == null || orderManagers == null ) {
			throw new IllegalStateException();
		}
	}
}
