package net.riccardocossu.i18split.base.driver;

import org.apache.commons.configuration.Configuration;

public interface FilterDriver extends Closeable {

	/**
	 * Creates or initializes resources
	 * @param configuration configuration information, both general and specific for this plugin
	 * @returns used keys
	 */
	String[] init(Configuration configuration)

	/**
	 * @return a unique name which identifies this particular plugin
	 */
	String getShortName()

}
