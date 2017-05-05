package edu.uw.nan.broker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerFactory;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 * @author Neil Nevitt
 * BrokerFactory implementation that returns a SimpleBroker.
 */
public class BrokerFactoryLaplace implements BrokerFactory {

	/**
	 * Constructor
	 */
	public BrokerFactoryLaplace() {
		
	}
	/**
	 * Instantiates a new SimpleBroker.
	 * @param name - the broker's name
	 * @param acctMngr - the account manager to be used by the broker
	 * @param exch - the exchange to be used by the broker
	 * @return a newly created SimpleBroker instance.
	 */
	@Override
	public final Broker newBroker(String name, AccountManager acctManager, StockExchange exch ) {
		
		return new BrokerLaplace(name, exch,acctManager);
	}

}
