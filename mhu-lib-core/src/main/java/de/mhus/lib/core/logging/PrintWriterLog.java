package de.mhus.lib.core.logging;

import java.io.PrintWriter;
import java.util.Date;

import de.mhus.lib.core.MCast;

public class PrintWriterLog extends Log {

	private PrintWriter out;
	private String name;
	private boolean printTime = true;
	private Log.LEVEL level = Log.LEVEL.TRACE;
	private boolean traces = true;

	public PrintWriterLog(String name, PrintWriter writer) {
		super(name);
		out = writer;
	}
	
	@Override
	public void debug(Object message) {
		if (!isDebugEnabled()) return;
		out.println(printTime() + "DEBUG: " + name + " " + message);
		if (message != null && message instanceof Throwable)
			((Throwable)message).printStackTrace(out);
	}

	@Override
	public void debug(Object message, Throwable t) {
		if (!isDebugEnabled()) return;
		out.println(printTime() + "DEBUG: " + name + " " + message);
		if (t!=null && traces) t.printStackTrace(out);
	}

	@Override
	public void error(Object message) {
		if (!isErrorEnabled()) return;
		out.println(printTime() + "ERROR: " + name + " " + message);
		if (message != null && message instanceof Throwable && traces)
			((Throwable)message).printStackTrace(out);
	}

	@Override
	public void error(Object message, Throwable t) {
		if (!isErrorEnabled()) return;
		out.println(printTime() + "ERROR: " + name + " " + message);
		if (t!=null && traces) t.printStackTrace(out);
	}

	@Override
	public void fatal(Object message) {
		if (!isFatalEnabled()) return;
		out.println(printTime() + "FATAL: " + name + " " + message);
		if (message != null && message instanceof Throwable && traces)
			((Throwable)message).printStackTrace(out);
	}

	@Override
	public void fatal(Object message, Throwable t) {
		if (!isFatalEnabled()) return;
		out.println(printTime() + "FATAL: " + name + " " + message);
		if (t!=null && traces) t.printStackTrace(out);
	}
	
	@Override
	public void info(Object message) {
		if (!isInfoEnabled()) return;
		out.println(printTime() + "INFO : " + name + " " + message);
		if (message != null && message instanceof Throwable && traces)
			((Throwable)message).printStackTrace(out);
	}

	@Override
	public void info(Object message, Throwable t) {
		if (!isInfoEnabled()) return;
		out.println(printTime() + "INFO : " + name + " " + message);
		if (t!=null && traces) t.printStackTrace(out);
	}

	@Override
	public boolean isDebugEnabled() {
		return isTrace() || level.ordinal() <= LEVEL.DEBUG.ordinal();
	}

	@Override
	public boolean isErrorEnabled() {
		return isTrace() || level.ordinal() <= LEVEL.ERROR.ordinal();
	}

	@Override
	public boolean isFatalEnabled() {
		return isTrace() || level.ordinal() <= LEVEL.FATAL.ordinal();
	}

	@Override
	public boolean isInfoEnabled() {
		return isTrace() || level.ordinal() <= LEVEL.INFO.ordinal();
	}

	@Override
	public boolean isTraceEnabled() {
		return isTrace() || level.ordinal() <= LEVEL.TRACE.ordinal();
	}

	@Override
	public boolean isWarnEnabled() {
		return isTrace() || level.ordinal() <= LEVEL.WARN.ordinal();
	}

	@Override
	public void trace(Object message) {
		if (isTrace()) {
			out.println(printTime() + "TRACE: " + name + " " + message);
			if (message != null && message instanceof Throwable && traces)
				((Throwable)message).printStackTrace(out);
		}
	}

	@Override
	public void trace(Object message, Throwable t) {
		if (!isTrace()) return;
		out.println(printTime() + "TRACE: " + name + " " + message);
		if (t!=null && traces) t.printStackTrace(out);
	}

	@Override
	public void warn(Object message) {
		if (!isWarnEnabled()) return;
		out.println(printTime() + "WARN : " + name + " " + message);
		if (message != null && message instanceof Throwable && traces)
			((Throwable)message).printStackTrace(out);
	}

	@Override
	public void warn(Object message, Throwable t) {
		if (!isWarnEnabled()) return;
		out.println(printTime() + "WARN : " + name + " " + message);
		if (t!=null && traces) t.printStackTrace(out);
	}
	
	public String printTime() {
		if (printTime) {
			return MCast.toString(new Date()) + " "; // TODO maybe more efficient
		}
		return "";
	}

	public Log.LEVEL getLevel() {
		return level;
	}

	public void setLevel(Log.LEVEL level) {
		this.level = level;
	}

	public boolean isTraces() {
		return traces;
	}

	public void setTraces(boolean traces) {
		this.traces = traces;
	}

	public PrintWriter getWriter() {
		return out;
	}


}