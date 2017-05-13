package edu.uw.nan.broker;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

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
	protected edu.uw.ext.framework.broker.OrderQueue<Boolean,Order> marketOrders;
	/**
	 * Name of the broker
	 */
	private String brokerName;
	/**
	 * The stock exchange
	 */
	private edu.uw.ext.framework.exchange.StockExchange exchg;
	/**
	 * The account manager.
	 */
	private edu.uw.ext.framework.account.AccountManager acctMgr;
	/**
	 * The order managers.
	 */
	private HashMap<String, OrderManager> orderManagers; 
	
	/**
	 * Constructor for sub classes
	 * @param brokerName - name of the broker
	 * @param exchg - the stock exchange to be used by the broker
	 * @param acctMgr - the account manager to be used by the broker
	 */
	protected BrokerLaplace(final String brokerName,final StockExchange exchg, final AccountManager acctMgr) {
		super();
		this.brokerName = brokerName;
		this.exchg = exchg;
		this.acctMgr = acctMgr;
		
		
	}

	/**
	 * Constructor
	 * @param brokerName - name of the broker
	 * @param exchg - the stock exchange to be used by the broker
	 * @param acctMgr - the account manager to be used by the broker
	 */
	public BrokerLaplace( final String brokerName, final AccountManager acctMgr, final StockExchange exchg) {
		this(brokerName,exchg,acctMgr);
		marketOrders = new OrderQueueLaplace<>(exchg.isOpen(), (Boolean t, Order o)->t);
		Consumer<Order> stockTracker = (order) -> {
			logger.info(String.format("Executing - %s", order));
			final int sharePrice = exchg.executeTrade(order);
			try {
				final Account account = acctMgr.getAccount(order.getAccountId());
				account.reflectOrder(order, sharePrice);
				logger.info(String.format("New balance - %d", account.getBalance()));
			} catch ( final AccountException e ) {
				logger.error(String.format("Unable to update account %s",order.getAccountId()));
			}
		};
		marketOrders.setOrderProcessor(stockTracker);
		initializeOrderManagers();
		exchg.addExchangeListener(this);
	}
	
	/**
	 * Fetch the stock list from the exchange and initialize an order manager for each stock. Only to be used during construction.
	 */
	protected final void initializeOrderManagers() {
		orderManagers = new HashMap<>();
		final Consumer<StopBuyOrder> mb2mp = ( StopBuyOrder order) -> marketOrders.enqueue(order);
		final Consumer<StopSellOrder> ms2mp = ( StopSellOrder order ) -> marketOrders.enqueue(order);
		for ( String ticker : exchg.getTickers() ) {
			final int curPrice = exchg.getQuote(ticker).getPrice();
			final OrderManager orderMgr = createOrderManager(ticker,curPrice);
			orderMgr.setBuyOrderProcessor(mb2mp);
			orderMgr.setSellOrderProcessor(ms2mp);
			orderManagers.put(ticker, orderMgr);
			if ( logger.isInfoEnabled()) {
				logger.info(String.format("Initialized order manager for %s at $d", ticker, curPrice ));
			}
		}
	}

	private OrderManager createOrderManager(String ticker, int curPrice) {
		// TODO Auto-generated method stub
		return null;
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
	public synchronized void priceChanged(final ExchangeEvent event) {
		checkFields();
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Processing price changes %s:%d", event.getTicker(), event.getPrice()));
		}
		OrderManager orderMgr = orderManagers.get(event.getTicker());
		if ( orderMgr != null ){
			orderMgr.adjustPrice(event.getPrice());
		}
		
	}
	/**
	 * Release broker resources.
	 * @throws edu.uw.ext.framework.broker.BrokerException - if the operation fails
	 */
	@Override
	public void close() throws BrokerException {
		
		try {
			exchg.removeExchangeListener(this);
			acctMgr.close();
			orderManagers = null;
		} catch ( AccountException e ) {
			logger.warn(String.format("Cannot close account manager"), e);
			throw new BrokerException(e);
		}
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
	public synchronized final Account createAccount(String username, String password, int balance) throws BrokerException {
		checkFields();
		try {
			return acctMgr.createAccount(username, password, balance);
		} catch ( AccountException e) {
			logger.error(String.format("unable to create account for %s", username),e);
			throw new BrokerException(e);
		}
		
	}
	/**
	 * Delete an account with the broker.
	 * @param username - the user or account name for the account
	 * @throws edu.uw.ext.framework.broker.BrokerException - if unable to delete account
	 */
	@Override
	public final void deleteAccount(String username) throws BrokerException {
		checkFields();
		try {
			acctMgr.deleteAccount(username);
		} catch ( AccountException e ) {
			logger.error(String.format("Unable to delete account for %s", username),e);
			throw new BrokerException(e);
		}
	}
	/**
	 * Locate an account with the broker. The username and password are first verified and the account is returned.
	 * @param username - the user or account name for the account
	 * @param password - the password for the new account
	 * @return the account
	 * @throws edu.uw.ext.framework.broker.BrokerException - username and password are invalid
	 */
	@Override
	public synchronized final Account getAccount(String username, String password) throws BrokerException {
		checkFields();
		Account account = null;
		try {
			if ( acctMgr.validateLogin(username,password) )
				return account = acctMgr.getAccount(username);
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
	public synchronized final void placeOrder(final MarketBuyOrder order) throws BrokerException {
		checkFields();
		marketOrders.enqueue(order);
		
	}
	/**
	 * Place an order with the broker.
	 * @param order - the order being placed with the broker 
	 */
	@Override
	public synchronized final void placeOrder(MarketSellOrder order) throws BrokerException {
		checkFields();
		marketOrders.enqueue(order);
	}
	/**
	 * Place an order with the broker.
	 * @param order - the order being placed with the broker
	 * @throws edu.uw.ext.framework.broker.BrokerException - if unable to place order
	 */
	@Override
	public synchronized final void placeOrder(StopBuyOrder order) throws BrokerException {
		checkFields();
		orderManagerLookup(order.getStockTicker()).queueOrder(order);
	}
	/**
	 * Place an order with the broker.
	 * @param order - the order being placed with the broker
	 * @throws edu.uw.ext.framework.broker.BrokerException - if unable to place order
	 */
	@Override
	public synchronized final void placeOrder(StopSellOrder order) throws BrokerException {
		checkFields();
		orderManagerLookup(order.getStockTicker()).queueOrder(order);
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
			throw new IllegalStateException("Broker not properly initialized.");
		}
	}
	
	private synchronized OrderManager orderManagerLookup(final String ticker ) throws BrokerException {
	final OrderManager orderMgr = orderManagers.get(ticker);
	if ( orderMgr == null ) {
		throw new BrokerException(String.format("Requested stock, $s does not exist", orderMgr.toString()), new AccountException());
		}
	return orderMgr;
	}
}
