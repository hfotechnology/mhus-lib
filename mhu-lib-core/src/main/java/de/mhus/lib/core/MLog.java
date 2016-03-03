package de.mhus.lib.core;

import de.mhus.lib.annotations.pojo.Hidden;
import de.mhus.lib.core.logging.Log;

/**
 * This class is currently only a place holder for a smarter strategy. But
 * the interface should be fix.
 * TODO implement strategy
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class MLog implements ILog {
	
	@Hidden
	private Log log;
	
	/** {@inheritDoc} */
	@Override
	public synchronized Log log() {
		if (log == null) {
			log = Log.getLog(this);
		}
		return log;
	}
	
}
