package edu.uw.nan.broker;


import edu.uw.nan.broker.OrderQueueLaplace;

import java.util.function.BiPredicate;

import edu.uw.ext.framework.order.Order;


public class MarketOrderDispatchFilter implements BiPredicate<Boolean, Order> {

	@Override
	public boolean test(Boolean t, Order u) {
		// TODO Auto-generated method stub
		return false;
	}


}


