package edu.uw.nan.broker;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * @author Neil Nevitt
 * A simple OrderQueue implementation backed by a TreeSet.
 *
 * @param T - the dispatch threshold type
 * @param E - the type of order contained in the queue
 */
public final class OrderQueueLaplace<T,E extends edu.uw.ext.framework.order.Order> implements edu.uw.ext.framework.broker.OrderQueue<T,E> {

	/**
	 * Constructor.
	 * @param threshold - the initial threshold
	 * @param filter - the dispatch filter used to control dispatching from this queue.
	 */
	public OrderQueueLaplace(T threshold, java.util.function.BiPredicate<T,E> filter) {
		
	}
	
	/**
	 * Constructor.
	 * @param threshold - the initial threshold
	 * @param filter - the dispatch filter used to control dispatching from this queue
	 * @param cmp - Comparator to be used for ordering
	 */
	public OrderQueueLaplace(T threshold, java.util.function.BiPredicate<T,E> filter, Comparator<E> cmp) {
		
	}
	/**
	 * Removes the highest dispatchable order in the queue. If there are orders in the queue but they do not meet the dispatch threshold order will not be removed and null will be returned.
	 * @return the first dispatchable order in the queue, or null if there are no dispatchable orders in the queue 
	 */
	@Override
	public E dequeue() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Executes the callback for each dispatchable order. Each dispatchable order is in turn removed from the queue and passed to the callback. If no callback is registered the order is simply removed from the queue.
	 */
	@Override
	public void dispatchOrders() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Adds the specified order to the queue. Subsequent to adding the order dispatches any dispatchable orders.
	 * @param order - the order to be added to the queue
	 */
	@Override
	public void enqueue(E order) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Obtains the current threshold value.
	 * @return the current threshold
	 */
	@Override
	public T getThreshold() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Registers the callback to be used during order processing.
	 * @param proc - the callback to be registered
	 */
	@Override
	public void setOrderProcessor(Consumer<E> proc) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Adjusts the threshold and dispatches orders.
	 * @param threshold - - the new threshold
	 */
	@Override
	public void setThreshold(T threshold) {
		// TODO Auto-generated method stub
		
	}

}
