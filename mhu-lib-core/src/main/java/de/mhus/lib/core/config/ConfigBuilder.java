package de.mhus.lib.core.config;

import de.mhus.lib.core.MLog;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.directory.WritableResourceNode;
import de.mhus.lib.errors.MException;

/**
 * Use this class to copy a hole config structure. The target should be an empty
 * config node. And it should support creation of properties and sub configs.
 *
 * The main purpose is to create a config of a specified type, e.g. the source is a XmlConfig or
 * unknown and the target a JsonConfig type.
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class ConfigBuilder extends MLog {
	
	/**
	 * <p>cloneConfig.</p>
	 *
	 * @param src a {@link de.mhus.lib.core.directory.ResourceNode} object.
	 * @param tar a {@link de.mhus.lib.core.directory.WritableResourceNode} object.
	 */
	public void cloneConfig(ResourceNode src, WritableResourceNode tar) {
		
		preProcess(src,tar);
		
		// copy values
		for (String key : src.getPropertyKeys()) {
			if (accept(src,key)) {
				try {
					tar.setProperty(key, mapProperty(src,key,src.getString(key,null) ));
				} catch (Throwable e) {
					log().i("property",key,e);
				}
			}
		}
		
		// copy sub configurations
		for (String key : src.getNodeKeys()) {
			for (ResourceNode srcSub : src.getNodes(key)) {
				if (accept(srcSub)) {
					try {
						WritableResourceNode tarSub = tar.createConfig(key);
						cloneConfig(srcSub,tarSub);
					} catch (MException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		postProcess(src,tar);
		
	}
	

	/**
	 * Return true if the config sould be copied to the target
	 * @param src
	 * @return
	 */
	boolean accept(ResourceNode src) {
		return true;
	}

	/**
	 * Process before cloning of this node.
	 * @param src
	 * @param tar
	 */
	void preProcess(ResourceNode src, WritableResourceNode tar) {
	}

	/**
	 * Process after cloning of the node finished.
	 * @param src
	 * @param tar
	 */
	void postProcess(ResourceNode src, WritableResourceNode tar) {
	}

	/**
	 * You can change the value of the property.
	 * @param src
	 * @param key
	 * @param property
	 * @return
	 */
	String mapProperty(ResourceNode src, String key, String value) {
		return value;
	}

	/**
	 * Return true if the property can be cloned to the target.
	 * @param src
	 * @param key
	 * @return
	 */
	boolean accept(ResourceNode src, String key) {
		return true;
	}
		
}
