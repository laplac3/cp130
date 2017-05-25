package edu.uw.nan.broker;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.Order;


/**
 * @author Neil Nevitt
 * A simple OrderQueue implementation backed by a TreeSet.
 *
 * @param T - the dispatch threshold type
 * @param E - the type of order contained in the queue
 */
public class OrderQueueLaplace<T,E extends Order> implements OrderQueue<T,E>, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(OrderQueueLaplace.class);
	/**
	 * The queue.
	 */
	private TreeSet<E> queue;
	/**
	 * The filter.
	 */
	private BiPredicate<T,E> filter;
	/**
	 * The order processor. 
	 */
	private Consumer<E> orderProcessor;
	/**
	 * The threshold.
	 */
	private T threshold;
	/**
	 * Thread that does the dispatching
	 */
	private Thread thread;
	/**
	 * Used to control access to the queue.
	 */
	private final ReentrantLock qLock = new ReentrantLock();
	/**
	 * Condition used to  process order. 
	 */
	private final Condition dCondition = qLock.newCondition();
	/**
	 * The lock used to control access to the process callback object.
	 */
	private final ReentrantLock pLock = new ReentrantLock();
	/**
	 * Constructor.
	 * @param threshold - the initial threshold
	 * @param filter - the dispatch filter used to control dispatching from this queue.
	 */
	public OrderQueueLaplace(String name, T threshold, BiPredicate<T,E> filter) {
		queue = new TreeSet<>();
		this.threshold = threshold;
		this.filter = filter;
		thread = new Thread(this, name + "OrderDispatchThread");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	/**
	 * Constructor.
	 * @param threshold - the initial threshold
	 * @param filter - the dispatch filter used to control dispatching from this queue
	 * @param cmp - Comparator to be used for ordering
	 */
	public OrderQueueLaplace(String name, T threshold, BiPredicate<T,E> filter, Comparator<E> cmp) {
		queue = new TreeSet<>(cmp);
		this.threshold = threshold;
		this.filter = filter;
		thread = new Thread(this, name + "OrderDispatchThread");
		thread.setDaemon(true);
		thread.start();
	}
	/**
	 * Removes the highest dispatchable order in the queue. If there are orders in the queue but they do not meet the dispatch threshold order will not be removed and null will be returned.
	 * @return the first dispatchable order in the queue, or null if there are no dispatchable orders in the queue 
	 */
	@Override
	public E dequeue() {
		E order =null;
		qLock.lock();
		try {
			
			if( !queue.isEmpty()) {
				order = queue.first();
				if ( filter != null && filter.test(threshold, order) ) {
					queue.remove(order);
				} else {
					order = null;
				}
			}
			
		} finally {
			qLock.unlock();
		}
		return order;
	}
	/**
	 * Executes the callback for each dispatchable order. Each dispatchable order is in turn removed from the queue and passed to the callback. If no callback is registered the order is simply removed from the queue.
	 */
	@Override
	public void dispatchOrders() {
		qLock.lock();
		try {
			dCondition.signal();
		} finally {
			qLock.unlock();
		}
		
	}
	
	/**
	 * Adds the specified order to the queue. Subsequent to adding the order dispatches any dispatchable orders.
	 * @param order - the order to be added to the queue
	 */
	@Override
	public void enqueue(E order) {
		qLock.lock();
		try {
			queue.add(order);
		} finally {
			qLock.unlock();
		}
		dispatchOrders();
	}
	/**
	 * Obtains the current threshold value.
	 * @return the current threshold
	 */
	@Override
	public T getThreshold() {
		return threshold;
	}
	/**
	 * Registers the callback to be used during order processing.
	 * @param proc - the callback to be registered
	 */
	@Override
	public void setOrderProcessor(Consumer<E> proc) {
		pLock.lock();
		try {
			orderProcessor = proc;
		} finally {
			pLock.unlock();
		}
	}
	/**
	 * Adjusts the threshold and dispatches orders.
	 * @param threshold - the new threshold
	 */
	@Override
	public final void setThreshold(T threshold) {
		this.threshold = threshold;
		dispatchOrders();
	}


	@Override
	public void run() {
		while (true ) {
			E order = null;
			qLock.lock();
			try {
				while ( (order = dequeue()) == null ) {
						dCondition.await();
					}
				}catch ( final InterruptedException e ) {
					logger.info("Order queue interupted.");	
			} finally {
				qLock.unlock();
			}
			pLock.lock();
			try {
				if ( orderProcessor != null) {
					logger.info(String.format("Order processor %d", order.getOrderId()));
					orderProcessor.accept(order);
				}
			} finally {
				pLock.unlock();
			}
		}
	}

}
