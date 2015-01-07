package de.mhus.lib.core.logging;

import java.util.WeakHashMap;

import de.mhus.lib.annotations.activator.DefaultImplementation;
import de.mhus.lib.core.MSingleton;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.lang.IBase;
import de.mhus.lib.core.service.ConfigProvider;

@DefaultImplementation(ConsoleFactory.class)
public abstract class LogFactory implements IBase {

	WeakHashMap<String, Log> buffer = new WeakHashMap<String, Log>();
			
    /**
     * Convenience method to derive a name from the specified class and
     * call <code>getInstance(String)</code> with it.
     *
     * @param clazz Class for which a suitable Log name will be derived
     * @return 
     *     */
    public Log getInstance(Class<?> clazz) {
    	return getInstance(clazz.getCanonicalName());
    }

    public abstract void init(ResourceNode config) throws Exception;

	public void init() throws Exception {
		ResourceNode config = MSingleton.get().getBaseControl().base(this).base(ConfigProvider.class).getConfig(this, null);
		init(config);
	}
	

    /**
     * <p>Construct (if necessary) and return a <code>Log</code> instance,
     * using the factory's current set of configuration attributes.</p>
     *
     * <p><strong>NOTE</strong> - Depending upon the implementation of
     * the <code>LogFactory</code> you are using, the <code>Log</code>
     * instance you are returned may or may not be local to the current
     * application, and may or may not be returned again on a subsequent
     * call with the same name argument.</p>
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  logging implementation that is being wrapped)
     * @return 
     */
    public synchronized Log getInstance(String name) {
		Log inst = buffer.get(name);
		if (inst == null) {
			inst = createInstance(name);
			buffer.put(name,inst);
		}
		return inst;
    }

    /**
     * <p>Construct and return a <code>Log</code> instance,
     * using the factory's current set of configuration attributes.</p>
     *
     * <p><strong>NOTE</strong> - Depending upon the implementation of
     * the <code>LogFactory</code> you are using, the <code>Log</code>
     * instance you are returned may or may not be local to the current
     * application, and may or may not be returned again on a subsequent
     * call with the same name argument.</p>
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  logging implementation that is being wrapped)
     * @return 
     */
    public abstract Log createInstance(String name);

	public Log getLog(Class<?> class1) {
		return getInstance(class1);
	}

//    public void update(Observable o, Object arg) {
//        setTrace(MSingleton.instance().getConfig().getBoolean(name + ".TRACE" , MSingleton.instance().getConfig().getBoolean("TRACE",false) ));
//    }

}