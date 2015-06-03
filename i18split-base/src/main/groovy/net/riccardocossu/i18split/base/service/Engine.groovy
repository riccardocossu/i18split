package net.riccardocossu.i18split.base.service

import net.riccardocossu.i18split.base.config.ConfigKeys
import net.riccardocossu.i18split.base.driver.FilterDriver
import net.riccardocossu.i18split.base.driver.InputDriver
import net.riccardocossu.i18split.base.driver.OutputDriver
import net.riccardocossu.i18split.base.model.DataRow;

import org.apache.commons.configuration.Configuration
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Engine {
	private static final Logger log = LoggerFactory.getLogger(Engine.class)

	private static Map<String, FilterDriver> inputPluginsByShortName = [:]

	static {
		ServiceLoader<FilterDriver> loader = ServiceLoader
				.load(FilterDriver.class)
		Iterator<FilterDriver> it = loader.iterator()
		while (it.hasNext()) {
			FilterDriver driver = it.next()
			String shortName = driver.shortName
			// if driver doesn't want or need to be called by shortname, it
			// should set this method to return null
			if (shortName != null) {
				inputPluginsByShortName.put shortName, driver
			}
		}
	}

	private Configuration configuration


	public Engine(Configuration configuration) {
		super()
		this.configuration = configuration
	}

	public void process() {
		def inputDriver = configuration.getString(ConfigKeys.INPUT_DRIVER)
		InputDriver reader = null
		OutputDriver writer = null
        def outputDriver = configuration.getString(ConfigKeys.OUTPUT_DRIVER)
		boolean keepOrder = configuration.getBoolean(ConfigKeys.KEEP_ORDER,false)
        try {
			reader = (InputDriver)inputPluginsByShortName[inputDriver]
			writer = (OutputDriver)inputPluginsByShortName[outputDriver]
            def usedKeys = reader.init configuration
            configuration.addProperty(ConfigKeys.INPUT_KEYS, usedKeys)
			writer.init configuration
            def rows = []
            DataRow row = reader.readNext()
            while (row != null) {
				if(row.values) {
					rows.add row
				}
                row = reader.readNext()
            }

            if(keepOrder) {
				rows = rows.sort {it.key}
			}
            for (r in rows) {
				writer.writeRow r
            }
        } catch(Exception e) {
			log.error("Conversion error", e)
        }  finally {
			if(reader) {
				reader.close()
			}
			if(writer) {
				writer.close()
			}
        }
	}

}
