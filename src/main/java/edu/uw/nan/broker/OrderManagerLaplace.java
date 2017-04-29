package edu.uw.nan.broker;

import java.util.function.Consumer;

import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;

public class OrderManagerLaplace implements edu.uw.ext.framework.broker.OrderManager {

	@Override
	public void adjustPrice(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSymbol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queueOrder(StopBuyOrder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queueOrder(StopSellOrder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBuyOrderProcessor(Consumer<StopBuyOrder> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSellOrderProcessor(Consumer<StopSellOrder> arg0) {
		// TODO Auto-generated method stub
		
	}

}
