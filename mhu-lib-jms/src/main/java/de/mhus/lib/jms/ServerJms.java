package de.mhus.lib.jms;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;

import de.mhus.lib.core.MThread;
import de.mhus.lib.core.logging.Log;


public abstract class ServerJms extends JmsChannel implements MessageListener {

    public ServerJms(JmsDestination dest) {
		super(dest);
	}

	MessageConsumer consumer;

	private MessageProducer replyProducer;
	private JmsInterceptor interceptorIn;

	private JmsInterceptor interceptorOut;

	private boolean fork = true;
	
	@Override
	public synchronized void open() throws JMSException {
		if (isClosed()) throw new JMSException("server closed");
		if (consumer == null || getSession() == null) {
			dest.open();
			if (dest.getConnection() == null || dest.getConnection().getSession() == null) throw new JMSException("connection offline");
			dest.getConnection().registerChannel(this);
			log().i("consume",dest);
            consumer = dest
            		.getConnection()
            		.getSession()
            		.createConsumer(dest.getDestination());
            consumer.setMessageListener(this);
            onOpen();
		}
	}

	/**
	 * The method is called after the open was successful.
	 */
	protected void onOpen() {
	}

	/**
	 * The method is called after the reset operation.
	 */
	protected void onReset() {
	}
	
	public synchronized void openAnswer() throws JMSException {
		if (replyProducer == null || getSession() == null) {
			open();
	        replyProducer = dest.getSession().createProducer(null);
		}
	}
	
	@Override
	public void reset() {
		log().i("reset",dest);
		try {
			consumer.close();
		} catch (Throwable t) {log().t(t);}
		try {
			replyProducer.close();
		} catch (Throwable t) {log().t(t);}
		onReset();
	}

	public abstract void receivedOneWay(Message msg) throws JMSException;
	
	public abstract Message received(Message msg) throws JMSException;

	protected void sendAnswer(Message msg, Message answer) throws JMSException {
		openAnswer();
		if (answer == null) answer = createErrorAnswer(null); // other side is waiting for an answer - send a null text
		if (interceptorOut != null)
			interceptorOut.prepare(answer);
		answer.setJMSMessageID(createMessageId());
		answer.setJMSCorrelationID(msg.getJMSCorrelationID());
        replyProducer.send(msg.getJMSReplyTo(), answer, deliveryMode, getPriority(), getTimeToLive());
	}

	@Override
	public void onMessage(final Message message) {
		
		if (fork) {
			new MThread(
					new Runnable() {
						
						@Override
						public void run() {
							processMessage(message);
						}
					}
					).start();
		} else
			processMessage(message);
			
	}
	
	public void processMessage(final Message message) {

		log().t("received",message);
		
		try {
			if (interceptorIn != null) {
				interceptorIn.begin(message);
			}
		} catch (Throwable t) {
			log().w(t);
			try {
				if (message.getJMSReplyTo() != null) {
					TextMessage answer = createErrorAnswer(t);
					log().t("errorAnswer",answer);
					sendAnswer(message, answer);
				}
			} catch (Throwable tt) {
				log().w(tt);
			}
			return;
		}
		
		try {
			if (message.getJMSReplyTo() != null) {
				Message answer = null;
				try {
					answer = received(message);
				} catch (JMSException t) {
					throw t;
				} catch(Throwable t) {
					log().i(t);
					answer = createErrorAnswer(t);
				}
				log().t("receivedAnswer",answer);
				sendAnswer(message, answer);
			} else {
				log().t("receivedOneWay",message);
				receivedOneWay(message);
			}
		} catch (JMSException t) {
			reset();
			log().w(t);
		} catch (Throwable t) {
			log().w(t);
		} finally {
			if (interceptorIn != null) {
				interceptorIn.end(message);
			}
		}
	}

	protected TextMessage createErrorAnswer(Throwable t) throws JMSException {
		TextMessage ret = getSession().createTextMessage(null);
		if (t != null)
			ret.setStringProperty("_error", t.toString());
		return ret;
	}

	@Override
	public void doBeat() {
		if (isClosed()) return;
		log().t("beat");
		try {
			open(); // try to reopen and re-listen
		} catch (JMSException e) {
			log().t(e);
		}
	}

	@Override
	public String getName() {
		return "openwire:/server" + dest.getName();
	}

	@Override
	public boolean isConnected() {
		return !(consumer == null || getSession() == null);
	}

	@Override
	public void checkConnection() {
		try {
			open();
		} catch (JMSException e) {
			log().t(e);
		}
	}

	public JmsInterceptor getInterceptorIn() {
		return interceptorIn;
	}

	public void setInterceptorIn(JmsInterceptor interceptor) {
		this.interceptorIn = interceptor;
	}

	public JmsInterceptor getInterceptorOut() {
		return interceptorOut;
	}

	public void setInterceptorOut(JmsInterceptor interceptorOut) {
		this.interceptorOut = interceptorOut;
	}

	public boolean isFork() {
		return fork;
	}

	public void setFork(boolean fork) {
		this.fork = fork;
	}

}
