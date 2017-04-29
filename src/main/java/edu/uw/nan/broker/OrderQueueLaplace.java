package edu.uw.nan.broker;

import java.util.function.Consumer;

public final class OrderQueueLaplace<T,E extends edu.uw.ext.framework.order.Order> implements edu.uw.ext.framework.broker.OrderQueue<T,E> {

	@Override
	public E dequeue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispatchOrders() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueue(E arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T getThreshold() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOrderProcessor(Consumer<E> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setThreshold(T arg0) {
		// TODO Auto-generated method stub
		
	}

}
