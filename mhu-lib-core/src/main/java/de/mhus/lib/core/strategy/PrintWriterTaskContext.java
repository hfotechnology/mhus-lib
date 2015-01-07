package de.mhus.lib.core.strategy;

import java.io.PrintWriter;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.PrintWriterLog;

public class PrintWriterTaskContext implements TaskContext {

	private MProperties attributes = new MProperties();
	private IConfig config;
	private boolean testOnly = false;
	private PrintWriter writer;
	private long estimated;
	private long step;
	private Log log = null;
	
	public PrintWriterTaskContext(String name, PrintWriter writer, IConfig config, boolean testOnly) {
		log = new PrintWriterLog(name, writer);
		this.writer = writer;
		this.config = config;
		this.testOnly = testOnly;
	}
	
	public PrintWriterTaskContext(PrintWriterLog log, IConfig config, boolean testOnly) {
		this.log = log;
		this.writer = log.getWriter();
		this.config = config;
		this.testOnly = testOnly;
	}
	
	@Override
	public void println() {
		writer.println();
	}

	@Override
	public void println(Object... out) {
		for (Object o : out)
			writer.print(o);
		writer.println();
	}

	@Override
	public void print(Object... out) {
		for (Object o : out)
			writer.print(o);
	}

	@Override
	public Log log() {
		return log;
	}

	@Override
	public void setEstimatedSteps(long steps) {
		estimated = steps;
	}

	@Override
	public void setCurrentStep(long step) {
		this.step = step;
	}

	@Override
	public void incrementStep() {
		step++;
	}

	@Override
	public IConfig getConfig() {
		return config;
	}

	@Override
	public boolean isTestOnly() {
		return testOnly;
	}

	@Override
	public MProperties getAttributes() {
		return attributes;
	}

	public long getEstimated() {
		return estimated;
	}

	public long getStep() {
		return step;
	}

}