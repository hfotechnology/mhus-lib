package de.mhus.lib.core.service;

import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MSingleton;
import de.mhus.lib.core.config.HashConfig;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.lang.MObject;

public class ConfigProvider extends MLog {

	private IConfig config;
	
	public ConfigProvider() {
		config = new HashConfig();
	}
	
	public ConfigProvider(IConfig config) {
		this.config = config;
	}
	
	public ResourceNode getConfig(Object owner, ResourceNode def) {
		if (config == null) {
			if (owner instanceof MObject) {
				config = MSingleton.get().getBaseControl().getBaseOf(((MObject)owner)).base(IConfig.class);
			}
		}
		if (config != null) {
			String name = owner.getClass().getCanonicalName();
			ResourceNode cClass = config.getNode(name);
			if (cClass != null) {
				log().t("found",name);
				return cClass;
			} else {
				log().t("not found",name);
			}
		}
		return def;
	}

}