package de.mhus.lib.karaf.jms;

import java.util.List;

import javax.jms.JMSException;

import de.mhus.lib.jms.JmsConnection;
import de.mhus.lib.karaf.MOsgi.Service;

public interface JmsManagerService {
	
//	void addConnection(String name, JmsConnection con);
//	void addConnection(String name, String url, String user, String password) throws JMSException;
//	String[] listConnections();
	JmsConnection getConnection(String name);
//	void removeConnection(String name);
//	String[] listChannels();
	JmsDataChannel getChannel(String name);
	void addChannel(JmsDataChannel channel);
	void removeChannel(String name);
	void resetChannels();
	void doChannelBeat();
	List<JmsDataChannel> getChannels();
	List<JmsConnection> getConnections();
	List<Service<JmsDataSource>> getDataSources();
	String getServiceName(de.mhus.lib.karaf.MOsgi.Service<JmsDataSource> ref);
	void doBeat();
	
	/**
	 * Called if the connection is offline to inform all the channels to disconnect from the connection.
	 * 
	 * @param connectionName
	 */
	void resetConnection(String connectionName);

}
