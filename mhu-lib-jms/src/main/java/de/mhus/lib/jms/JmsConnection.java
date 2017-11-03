package de.mhus.lib.jms;

import java.util.WeakHashMap;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import de.mhus.lib.core.cfg.CfgString;

public class JmsConnection extends JmsObject implements ExceptionListener {

    private static final Boolean NON_TRANSACTED = false;
    @SuppressWarnings("unused") // used by config manager
	private static final CfgString allowedSerializablePackages = new CfgString(JmsConnection.class, "org.apache.activemq.SERIALIZABLE_PACKAGES", "de,java,org,com") {
    	@Override
		public void onPreUpdate(String newValue) {
    		System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES", newValue);
    	}
    };
	private Connection connection;
	private ActiveMQConnectionFactory connectionFactory;
	private Session session;

	private String url;

	private String user;

	public JmsConnection(String url, String user, String password) throws JMSException {
		connectionFactory = new ActiveMQConnectionFactory(user, password, url);
		this.url = url;
		this.user = user;
	}

	@Override
	public synchronized void open() throws JMSException {
		if (isClosed()) throw new JMSException("connection closed");
		if (connection == null) {
			log().i("connect",url);
	        Connection con = connectionFactory.createConnection();
	        con.start();
	        connection = con;
            connection.setExceptionListener(this);
            session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
		}
	}
	
	public JmsDestination createTopic(String name) throws JMSException {
        return new JmsDestination(name, true).setConnection(this);
	}

	public JmsDestination createQueue(String name) throws JMSException {
        return new JmsDestination(name, false).setConnection(this);
	}
	
	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void reset() {
		log().i("reset",connection);
		try {
			session.close();
		} catch (Throwable t) {log().t(t);}
		try {
			connection.close();
		} catch (Throwable t) {log().t(t);}
		connection = null;
		session = null;
	}
		
	@Override
	public void onException(JMSException exception) {
		log().w("kill connection",connection,exception);
		reset();
	}
		
	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	@Override
	public boolean isConnected() {
		return connection != null;
	}

	@Override
	public JmsDestination getJmsDestination() {
		return null;
	}
	
}
