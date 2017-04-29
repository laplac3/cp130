package edu.uw.nan.broker;

import java.util.function.Consumer;

import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;

/**
 * @author Neil Nevitt
 * Maintains queues to different types of orders and requests the execution of orders when price conditions allow their execution.
 *
 */
public class OrderManagerLaplace implements edu.uw.ext.framework.broker.OrderManager {

	/**
	 * Queue for stop buy orders
	 */
	protected edu.uw.ext.framework.broker.OrderQueue<Integer,edu.uw.ext.framework.order.StopBuyOrder> stopBuyOrderQueue;
	/**
	 * Queue for stop sell orders
	 */
	protected edu.uw.ext.framework.broker.OrderQueue<Integer,edu.uw.ext.framework.order.StopSellOrder> stopSellOrderQueue;	
	/**
	 * Constructor. Constructor to be used by sub classes to finish initialization.
	 * @param stockTickerSymbol - the ticker symbol of the stock this instance is manage orders for
	 */
	protected OrderManagerLaplace(String stockTickerSymbol) {
		
	}
	
	/**
	 * Constructor.
	 * @param stockTickerSymbol - the ticker symbol of the stock this instance is manage orders for.
	 * @param price - the current price of stock to be managed
	 */
	public OrderManagerLaplace(String stockTickerSymbol, int price) {
		
	}
	/**
	 * Respond to a stock price adjustment by setting threshold on dispatch filters.
	 * @param price - the new price
	 */
	@Override
	public final void adjustPrice(int price) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Gets the stock ticker symbol for the stock managed by this stock manager.
	 * @return the stock ticker symbol
	 */
	@Override
	public final String getSymbol() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Queue a stop buy order.
	 * @param order - the order to be queued
	 */
	@Override
	public final void queueOrder(StopBuyOrder order) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Queue a stop sell order.
	 * @param order - the order to be queued
	 */
	@Override
	public final void queueOrder(StopSellOrder order) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Registers the processor to be used during buy order processing. This will be passed on to the order queues as the dispatch callback.
	 * @param processor - the callback to be registered
	 */
	@Override
	public final void setBuyOrderProcessor(Consumer<StopBuyOrder> processor) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Registers the processor to be used during sell order processing. This will be passed on to the order queues as the dispatch callback.
	 * @param processor - the callback to be registered.
	 */
	@Override
	public void setSellOrderProcessor(Consumer<StopSellOrder> processor) {
		// TODO Auto-generated method stub
		
	}

}
