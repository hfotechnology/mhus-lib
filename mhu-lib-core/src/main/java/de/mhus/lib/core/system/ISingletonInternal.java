package de.mhus.lib.core.system;


import java.util.Set;

import de.mhus.lib.core.logging.LogFactory;

/**
 * <p>ISingletonInternal interface.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 * @since 3.2.9
 */
public interface ISingletonInternal {

	/**
	 * <p>setLogFactory.</p>
	 *
	 * @param logFactory a {@link de.mhus.lib.core.logging.LogFactory} object.
	 */
	void setLogFactory(LogFactory logFactory);

	/**
	 * <p>getLogTrace.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	Set<String> getLogTrace();

}
