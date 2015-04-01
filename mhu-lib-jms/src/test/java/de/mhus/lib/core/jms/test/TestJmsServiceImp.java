package de.mhus.lib.core.jms.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import de.mhus.lib.jms.JmsChannel;
import de.mhus.lib.jms.JmsServiceListener;

public class TestJmsServiceImp implements TestJmsService, JmsServiceListener {

	public String lastAction = null;
	private JmsChannel channel;
	
	@Override
	public void withoutReturnValue() {
		lastAction = "withoutReturnValue";
	}

	@Override
	public void oneWayWithoutReturn() {
		lastAction = "oneWayWithoutReturn";
	}

	@Override
	public void withParameters(String nr1, long nr2, int nr3) {
		lastAction = "withParameters " + nr1 + nr2 + nr3;
	}

	@Override
	public String withParametersAndReturn(String nr1, long nr2, int nr3) {
		lastAction = "withParametersAndReturn " + nr1 + nr2 + nr3;
		return "R " + nr1;
	}

	@Override
	public Map<String, String> mapSample(Map<String, String> in) {
		lastAction = "mapSample " + in;
		HashMap<String, String> ret = new HashMap<>();
		ret.put("x", "y");
		return ret;
	}

	@Override
	public List<String> listSample(List<String> in) {
		lastAction = "listSample " + in;
		LinkedList<String> ret = new LinkedList<>();
		ret.add("x");
		return ret;
	}

	@Override
	public void receiveMessage(Message raw) throws JMSException {
		String text = "";
		if (raw == null)
			text = null;
		else
		if (raw instanceof TextMessage)
			text = ((TextMessage)raw).getText();
		lastAction = "receiveMessage " + text;
	}

	@Override
	public Message sendMessage(String text) throws JMSException {
		lastAction = "sendMessage " + text;
		return channel.getSession().createTextMessage(text);
	}

	@Override
	public List<Message> messageBroadcast(String text) throws JMSException {
		lastAction = "messageBroadcast " + text;
		LinkedList<Message> out = new LinkedList<>();
		out.add(channel.getSession().createTextMessage(text));
		return out;
	}

	
	@Override
	public void jmsServiceOnOpen(JmsChannel channel) {
		this.channel = channel;
	}

	@Override
	public void jmsServiceOnReset(JmsChannel channel) {
		
	}

	@Override
	public void throwException(String text) throws IOException {
		lastAction = "throwException " + text;
		throw new IOException(text);
	}

}
