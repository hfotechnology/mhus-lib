package de.mhus.lib.core;

import java.util.UUID;
import java.util.WeakHashMap;

import de.mhus.lib.core.config.HashConfig;
import de.mhus.lib.core.configupdater.ConfigUpdater;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.logging.LevelMapper;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.TrailLevelMapper;
import de.mhus.lib.core.system.DefaultSingleton;
import de.mhus.lib.core.system.DummyClass;
import de.mhus.lib.core.system.ISingleton;
import de.mhus.lib.core.system.ISingletonFactory;
import de.mhus.lib.core.system.SingletonInitialize;

/**
 * <p>MSingleton class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class MSingleton {

	private static ISingleton singleton;
	/** Constant <code>trace</code> */
	protected static Boolean trace;
	private static WeakHashMap<UUID, Log> loggers = new WeakHashMap<>();
	private static ResourceNode emptyConfig = null;
	private static ConfigUpdater configUpdater;
	
//	private static DummyClass dummy = new DummyClass(); // the class is inside this bundle and has the correct class loader
	
	private MSingleton() {}
	
	/**
	 * <p>get.</p>
	 *
	 * @return a {@link de.mhus.lib.core.system.ISingleton} object.
	 */
	public static synchronized ISingleton get() {
		if (singleton == null) {
			try {
				ISingleton obj = null;
				String path = "de.mhus.lib.mutable.SingletonFactory";
				if (System.getProperty("mhu.lib.singleton.factory") != null) path = System.getProperty(MConstants.PROP_SINGLETON_FACTORY_CLASS);
				if (isDirtyTrace()) System.out.println("--- MSingletonFactory:" + path);
				ISingletonFactory factory = (ISingletonFactory)Class.forName(path).newInstance();
				if (factory != null) {
					obj = factory.createSingleton();
				}
				singleton = obj;
			} catch (Throwable t) {
				if (isDirtyTrace()) t.printStackTrace();
			}
			if (singleton == null)
				singleton = new DefaultSingleton();
			if (isDirtyTrace()) System.out.println("--- MSingleton: " + singleton.getClass().getCanonicalName());
			if (singleton instanceof SingletonInitialize)
				((SingletonInitialize)singleton).doInitialize(DummyClass.class.getClassLoader());
		}
		return singleton;
	}
	
	
	/**
	 * <p>isDirtyTrace.</p>
	 *
	 * @return a boolean.
	 */
	public static boolean isDirtyTrace() {
		if (trace == null) trace = "true".equals(System.getProperty(MConstants.PROP_DIRTY_TRACE));
		return trace;
	}
	
	/**
	 * <p>setDirtyTrace.</p>
	 *
	 * @param dt a boolean.
	 */
	public static void setDirtyTrace(boolean dt) {
		trace = dt;
	}

	/**
	 * <p>isTrace.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isTrace(String name) {
		if (isDirtyTrace()) 
			System.out.println("--- Ask for trace: " + name);
//		String value = System.getProperty(name+".trace");
//		if (value != null) return "true".equals(value);
		return get().isTrace(name);
	}
		
	/**
	 * <p>doStartTrailLog.</p>
	 *
	 * @since 3.2.9
	 */
	public static void doStartTrailLog() {
		LevelMapper mapper = get().getLogFactory().getLevelMapper();
		if (mapper != null && mapper instanceof TrailLevelMapper)
			((TrailLevelMapper)mapper).doConfigureTrail("MAP");
	}
	
	/**
	 * <p>doStopTrailLog.</p>
	 *
	 * @since 3.2.9
	 */
	public static void doStopTrailLog() {
		LevelMapper mapper = get().getLogFactory().getLevelMapper();
		if (mapper != null && mapper instanceof TrailLevelMapper)
			((TrailLevelMapper)mapper).doResetTrail();
	}

	/**
	 * <p>registerLogger.</p>
	 *
	 * @param log a {@link de.mhus.lib.core.logging.Log} object.
	 * @since 3.2.9
	 */
	public static void registerLogger(Log log) {
		synchronized (loggers) {
			loggers.put(log.getId(), log);
		}
	}

	/**
	 * <p>unregisterLogger.</p>
	 *
	 * @param log a {@link de.mhus.lib.core.logging.Log} object.
	 * @since 3.2.9
	 */
	public static void unregisterLogger(Log log) {
		synchronized (loggers) {
			loggers.remove(log.getId());
		}
	}
	
	/**
	 * <p>updateLoggers.</p>
	 *
	 * @since 3.2.9
	 */
	public static void updateLoggers() {
		try {
			synchronized (loggers) {
				for (UUID logId : loggers.keySet().toArray(new UUID[loggers.size()]))
					loggers.get(logId).update();
			}
		} catch(Throwable t) {
			if (MSingleton.isDirtyTrace()) t.printStackTrace();
		}
	}

	/**
	 * <p>getConfig.</p>
	 *
	 * @param owner a {@link java.lang.Object} object.
	 * @return a {@link de.mhus.lib.core.directory.ResourceNode} object.
	 * @since 3.2.9
	 */
	public static ResourceNode getConfig(Object owner) {
		if (emptyConfig == null) emptyConfig = new HashConfig();
		return get().getConfigProvider().getConfig(owner, emptyConfig);
	}
	
	/**
	 * <p>Getter for the field <code>configUpdater</code>.</p>
	 *
	 * @return a {@link de.mhus.lib.core.configupdater.ConfigUpdater} object.
	 * @since 3.2.9
	 */
	public static synchronized ConfigUpdater getConfigUpdater() {
		if (configUpdater == null)
			configUpdater = new ConfigUpdater();
		return configUpdater;
	}
		
}
