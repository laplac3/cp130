package edu.uw.nan.exchange;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.StockExchange;

/**
 * @author Neil Nevitt
 * Accepts command requests and dispatches them to a CommandHandler.
 */
public class CommandListener implements Runnable {
	/**
	 * Logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);
	/**
	 * The command port.
	 */
	private final int commandPort;
	/**
	 * The Stock exchange.
	 */
	private final StockExchange realExchange;
	/**
	 * The socket for the server.
	 */
	private ServerSocket server;
	/**
	 * A boolean for listening. 
	 */
	private volatile boolean listen = true;
	/**
	 * The request executor. 
	 */
	private ExecutorService reqExecutor = Executors.newCachedThreadPool();
	/**
	 * Constructor
	 * @param commandPort - the port to listen for connections on
	 * @param realExchange - the "real" exchange to be used to execute the commands
	 */
	public CommandListener(int commandPort, StockExchange realExchange) {
		this.commandPort = commandPort;
		this.realExchange = realExchange;
	}
	/**
	 * Accept connections, and create a CommandExecutor for dispatching the command.
	 */
	@Override
	public void run() {
		try {
			if ( logger.isInfoEnabled() ) {
				logger.info("Server open for accepting commands.");
			}
			server = new ServerSocket(commandPort);
			while ( listen ) {
				Socket socket = null;
				try {
					socket = server.accept();
					if ( logger.isInfoEnabled() ) {
						logger.info(String.format("Accepted connection %s:%d", socket.getLocalAddress(), socket.getLocalPort()));
					}
				} catch ( final SocketException e ) {
					if ( server != null && !server.isClosed() ) {
						logger.warn("Error accepting connections.", e);
					}
				}	
				if ( socket == null ) {
					continue;
				}
				reqExecutor.execute(new CommandHandler(socket, realExchange));
			}
		} catch ( IOException e ) {
			logger.info("Server error.",e);
		} finally {
			terminate();
		}
	}
	/**
	 * Terminates this thread gracefully.
	 */
	public void terminate() {
		listen = false;
		try {
			if ( server != null && !server.isClosed() ) {
				logger.info("Closing server.");
				server.close();
			}
			server = null;
			if ( !reqExecutor.isShutdown() ) {
				reqExecutor.shutdown();
				reqExecutor.awaitTermination(1L, TimeUnit.SECONDS);
			}	
		} catch ( final InterruptedException e ) {
			logger.info("Interrupted waiting for executor to terminate.",e);
		} catch ( IOException e ) {
			logger.info("Error while closing listening socket", e);
		}
		
	}

}
