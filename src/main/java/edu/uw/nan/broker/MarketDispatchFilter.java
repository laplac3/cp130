package edu.uw.nan.broker;


import edu.uw.nan.broker.OrderQueueLaplace;
import edu.uw.ext.framework.order.Order;

public class MarketDispatchFilter implements DispatchFilter<Boolean,Order> {

	public MarketDispatchFilter( boolean marketOnOff ) {
		setThreshold(marketOnOff);
	}
	
	public boolean check( final Order order) {
		return getThreshold();
	}
}
