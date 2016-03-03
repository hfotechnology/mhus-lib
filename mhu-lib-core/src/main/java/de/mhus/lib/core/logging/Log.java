package de.mhus.lib.core.logging;

import java.util.UUID;

import de.mhus.lib.core.MSingleton;
import de.mhus.lib.core.MSystem;

/**
 * Got the interface from apache-commons-logging. I need to switch because its not working
 * in eclipse plugins correctly.
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class Log {

	public enum LEVEL {TRACE,DEBUG,INFO,WARN,ERROR,FATAL};

	protected boolean localTrace = true;
	protected String name;
	protected LevelMapper levelMapper;
    protected ParameterMapper parameterMapper;
    protected LogEngine engine = null;
    protected UUID id = UUID.randomUUID();
	
	/**
	 * <p>Constructor for Log.</p>
	 *
	 * @param owner a {@link java.lang.Object} object.
	 */
	public Log(Object owner) {
		
		
		String name = null;
		if (owner == null) {
			name = "?";
		} else
		if (owner instanceof Class) {
			name = ((Class<?>)owner).getName();
		} else {
			name = String.valueOf(owner);
			if (name == null) 
				name = owner.getClass().getCanonicalName();
//			else {
//				int p = name.indexOf('@');
//				if (p > 0) name = name.substring(0,p);
//			}
		}
		this.name = name;
		localTrace = MSingleton.isTrace(name);
		
		update();
		
		register();
	}

    /**
     * <p>register.</p>
     *
     * @since 3.2.9
     */
    protected void register() {
		MSingleton.registerLogger(this);
	}
    
    /**
     * <p>unregister.</p>
     *
     * @since 3.2.9
     */
    protected void unregister() {
		MSingleton.unregisterLogger(this);
    }


    // -------------------------------------------------------- Logging Methods
    
    /**
     * Log a message in trace, it will automatically append the objects if trace is enabled. Can Also add a trace.
     * This is the local trace method. The trace will only written if the local trace is switched on.
     *
     * @param msg a {@link java.lang.Object} object.
     */
    public void t(Object ... msg) {
    	log(LEVEL.TRACE, msg);
    }

    /**
     * <p>log.</p>
     *
     * @param level a {@link de.mhus.lib.core.logging.Log.LEVEL} object.
     * @param msg a {@link java.lang.Object} object.
     */
    public void log(LEVEL level, Object ... msg) {
		if (engine == null) return;

    	if (levelMapper != null) level = levelMapper.map(this, level,msg);
    	
    	switch (level) {
		case DEBUG:
			if (!engine.isDebugEnabled()) return;
			break;
		case ERROR:
			if (!engine.isErrorEnabled()) return;
			break;
		case FATAL:
			if (!engine.isFatalEnabled()) return;
			break;
		case INFO:
			if (!engine.isInfoEnabled()) return;
			break;
		case TRACE:
			if (!engine.isTraceEnabled()) return;
			break;
		case WARN:
			if (!engine.isWarnEnabled()) return;
			break;
		default:
			return;
    	}

    	if (parameterMapper != null) msg = parameterMapper.map(this, msg);
    	
    	StringBuffer sb = new StringBuffer();
    	prepare(sb);
    	Throwable error = null;
//    	int cnt=0;
    	for (Object o : msg) {
			error = serialize(sb,o, error);
//   		cnt++;
    	}
    	
    	switch (level) {
		case DEBUG:
			engine.debug(sb.toString(),error);
			break;
		case ERROR:
			engine.error(sb.toString(),error);
			break;
		case FATAL:
			engine.fatal(sb.toString(),error);
			break;
		case INFO:
			engine.info(sb.toString(),error);
			break;
		case TRACE:
			engine.trace(sb.toString(),error);
			break;
		case WARN:
			engine.warn(sb.toString(),error);
			break;
		default:
			break;
    	}
	}

	private Throwable serialize(StringBuffer sb, Object o, Throwable error) {
    	try {
	    	if (o == null) {
				sb.append("[null]");
	    	} else
			if (o instanceof Throwable) {
				if (error == null) return (Throwable)o;
				// another error
				sb.append("[").append(o).append("]");
			} else
	    	if (o.getClass().isArray()) {
	    		sb.append("{");
	    		for (Object p : (Object[])o) {
	    			error = serialize(sb, p, error);
	    		}
	    		sb.append("}");
	    	} else
	    		sb.append("[").append(o).append("]");
    	} catch (Throwable t) {}
		return error;
	}


//	/**
//     * Log a message in trace, it will automatically append the objects if trace is enabled. Can Also add a trace.
//     */
//    public void tt(Object ... msg) {
//    	if (!isTraceEnabled()) return;
//    	StringBuffer sb = new StringBuffer();
//    	prepare(sb);
//    	Throwable error = null;
////    	int cnt=0;
//    	for (Object o : msg) {
//			error = serialize(sb,o, error);
////    		cnt++;
//    	}
//    	trace(sb.toString(),error);
//    }

    /**
     * Log a message in debug, it will automatically append the objects if debug is enabled. Can Also add a trace.
     *
     * @param msg a {@link java.lang.Object} object.
     */
    public void d(Object ... msg) {
    	log(LEVEL.DEBUG, msg);
    }

    /**
     * Log a message in info, it will automatically append the objects if debug is enabled. Can Also add a trace.
     *
     * @param msg a {@link java.lang.Object} object.
     */
    public void i(Object ... msg) {
    	log(LEVEL.INFO, msg);
    }
    
    /**
     * Log a message in warn, it will automatically append the objects if debug is enabled. Can Also add a trace.
     *
     * @param msg a {@link java.lang.Object} object.
     */
    public void w(Object ... msg) {
    	log(LEVEL.WARN, msg);
    }

    /**
     * Log a message in error, it will automatically append the objects if debug is enabled. Can Also add a trace.
     *
     * @param msg a {@link java.lang.Object} object.
     */
    public void e(Object ... msg) {
    	log(LEVEL.ERROR, msg);
    }

    /**
     * Log a message in info, it will automatically append the objects if debug is enabled. Can Also add a trace.
     *
     * @param msg a {@link java.lang.Object} object.
     */
    public void f(Object ... msg) {
    	log(LEVEL.FATAL, msg);
    }

    /**
     * <p>prepare.</p>
     *
     * @param sb a {@link java.lang.StringBuffer} object.
     */
    protected void prepare(StringBuffer sb) {
    	if (levelMapper != null) {
    		levelMapper.prepareMessage(this,sb);
    	} else {
    		sb.append('[').append(Thread.currentThread().getId()).append(']');
    	}
	}

	/**
	 * <p>Setter for the field <code>localTrace</code>.</p>
	 *
	 * @param localTrace a boolean.
	 * @since 3.2.9
	 */
	public void setLocalTrace(boolean localTrace) {
		this.localTrace = localTrace;
	}

	/**
	 * <p>isLocalTrace.</p>
	 *
	 * @return a boolean.
	 * @since 3.2.9
	 */
	public boolean isLocalTrace() {
		return localTrace;
	}

	/**
	 * Use the name of the caller
	 *
	 * @return a {@link java.lang.String} object.
	 */
//	public static Log getLog() {
//		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
//		// for (StackTraceElement e : stack) System.out.println(e.getClassName());
//		return getLog(stack[2].getClassName());
//	}
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return MSystem.toString(this, getName());
	}
	
	/**
	 * <p>getLog.</p>
	 *
	 * @param owner a {@link java.lang.Object} object.
	 * @return a {@link de.mhus.lib.core.logging.Log} object.
	 */
	public static Log getLog(Object owner) {
		// return new StaticBase(owner).log();
//		return MSingleton.get().createLog(owner);
		return new Log(owner);
	}

	/**
	 * <p>update.</p>
	 */
	public void update() {
		engine = MSingleton.get().getLogFactory().getInstance(getName());
		localTrace = MSingleton.isTrace(name);
		levelMapper = MSingleton.get().getLogFactory().getLevelMapper();
		parameterMapper = MSingleton.get().getLogFactory().getParameterMapper();
	}

	/**
	 * <p>Getter for the field <code>parameterMapper</code>.</p>
	 *
	 * @return a {@link de.mhus.lib.core.logging.ParameterMapper} object.
	 * @since 3.2.9
	 */
	public ParameterMapper getParameterMapper() {
		return parameterMapper;
	}
	
	/**
	 * Return if the given level is enabled. This function also uses the
	 * levelMapper to find the return value. Instead of the is...Enabled().
	 *
	 * @param level a {@link de.mhus.lib.core.logging.Log.LEVEL} object.
	 * @return a boolean.
	 */
	public boolean isLevelEnabled(LEVEL level) {
		if (engine == null) return false;

		if (localTrace)
			level = LEVEL.INFO;
		else
		if (levelMapper != null) 
			level = levelMapper.map(this, level);
    	
    	switch (level) {
		case DEBUG:
			return engine.isDebugEnabled();
		case ERROR:
			return engine.isErrorEnabled();
		case FATAL:
			return engine.isFatalEnabled();
		case INFO:
			return engine.isInfoEnabled();
		case TRACE:
			return engine.isTraceEnabled();
		case WARN:
			return engine.isWarnEnabled();
		default:
			return false;
    	}

	}
	
	/**
	 * <p>close.</p>
	 *
	 * @since 3.2.9
	 */
	public void close() {
		if (engine == null) return;
		unregister();
		engine.close();
		engine = null;
	}
	
	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.util.UUID} object.
	 * @since 3.2.9
	 */
	public UUID getId() {
		return id;
	}
	
}
