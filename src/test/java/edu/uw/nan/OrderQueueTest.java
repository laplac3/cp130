package edu.uw.nan;

import test.AbstractOrderQueueTest;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.broker.*;
import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;

/*****************************************************************************
 * Replace these imports with the import of your implementing classes.       *
 *****************************************************************************/
import edu.uw.nan.broker.OrderQueueLaplace;


/**
 * Concrete subclass of AbstractQueueTest, provides implementations of the 
 * createStopBuyOrderQueue, createStopSellOrderQueue and createAnyOrderQueue
 * methods which create instances of "my" OrderQueue implementation class, using
 * "my" Comparator implementations.
 */
public class OrderQueueTest extends AbstractOrderQueueTest {
    /**
     * Creates an instance of "my" OrderQueue implementation class, using
     * an instance of "my" implementation of Comparator that is intended to
     * order StopBuyOrders.
     *
     * @param filter the OrderDispatch filter to be used
     * 
     * @return a new OrderQueue instance
     */
	@Override
	protected final OrderQueue<Integer,StopBuyOrder> createStopBuyOrderQueue(
                        final DispatchFilter<Integer, StopBuyOrder> filter) {
        /*********************************************************************
         * This needs to be an instance of your OrderQueue and Comparator.   *
         *********************************************************************/
        return new OrderQueueLaplace<>(0, filter, new StopBuyOrderComparator());
    }

    /**
     * Creates an instance of "my" OrderQueue implementation class, using
     * an instance of "my" implementation of Comparator that is intended to
     * order StopSellOrders.
     *
     * @param filter the OrderDispatch filter to be used
     * 
     * @return a new OrderQueue instance
     */
	@Override
    protected final OrderQueue<Integer,StopSellOrder> createStopSellOrderQueue(
                          final DispatchFilter<Integer, StopSellOrder> filter) {
        /*********************************************************************
         * This needs to be an instance of your OrderQueue and Comparator.   *
         *********************************************************************/
        return new OrderQueueLaplace<>(0, filter, new StopSellOrderComparator());
    }
    
    /**
     * Creates an instance of "my" OrderQueue implementation class, the queue
     * will order the Orders according to their natural ordering.
     *
     * @param filter the OrderDispatch filter to be used
     * 
     * @return a new OrderQueue instance
     */
    @Override
    protected final OrderQueue<Boolean,Order> createAnyOrderQueue(
                            final DispatchFilter<Boolean, Order> filter) {
        /*********************************************************************
         * This needs to be an instance of your OrderQueue.                  *
         *********************************************************************/
        return new OrderQueueLaplace<Boolean, Order>(true, filter);
    }

}
