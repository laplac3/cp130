package edu.uw.nan;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.AccountManagerTest;
import test.AccountTest;
import test.BrokerTest;
import test.DaoTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ OrderQueueTest.class, OrderManagerTest.class,BrokerTest.class, AccountManagerTest.class, DaoTest.class})
public class TestSuite{
}
