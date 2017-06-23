package edu.uw.nan;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.AccountManagerTest;
import test.AccountTest;
import test.BrokerTest;
import test.ClientOrderCodecTest;
import test.DaoTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({ClientOrderCodecTest.class })
public class TestSuite{
}
