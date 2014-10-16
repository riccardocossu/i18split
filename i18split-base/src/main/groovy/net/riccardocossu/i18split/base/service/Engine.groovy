package net.riccardocossu.i18split.base.service

import net.riccardocossu.i18split.base.config.ConfigKeys
import net.riccardocossu.i18split.base.driver.FilterDriver
import net.riccardocossu.i18split.base.driver.InputDriver
import net.riccardocossu.i18split.base.driver.OutputDriver
import org.apache.commons.configuration.Configuration

public class Engine {

	private static Map<String, FilterDriver> inputPluginsByShortName = [:]

	static {
		ServiceLoader<FilterDriver> loader = ServiceLoader
				.load(FilterDriver.class);
		Iterator<FilterDriver> it = loader.iterator();
		while (it.hasNext()) {
			FilterDriver driver = it.next();
			String shortName = driver.getShortName();
			// if driver doesn't want or need to be called by shortname, it
			// should set this method to return null
			if (shortName != null) {
				inputPluginsByShortName.put(shortName, driver);
			}
		}
	}

	private Configuration configuration


	public Engine(Configuration configuration) {
		super();
		this.configuration = configuration
	}

	public void process() {
		def inputDriver = configuration.getString(ConfigKeys.INPUT_DRIVER)
        // FIXME deal with classcast and NPE
        InputDriver reader = inputPluginsByShortName.get(inputDriver)
        def outputDriver = configuration.getString(ConfigKeys.OUTPUT_DRIVER)
        OutputDriver writer = inputPluginsByShortName.get(outputDriver)
		boolean keepOrder = configuration.getBoolean(ConfigKeys.KEEP_ORDER,false)
        try {
            def usedKeys = reader.init(configuration)
            configuration.addProperty(ConfigKeys.INPUT_KEYS, usedKeys)
            writer.init(configuration)
            def rows = []
            def row = reader.readNext()
            while (row != null) {
				if(row.values) {
					rows.add(row)
				}
                row = reader.readNext()
            }

            if(keepOrder) {
				rows = rows.sort {it.key}
			}
            for (r in rows) {
				writer.writeRow(r)
            }
        } finally {
            reader.close()
            writer.close()
        }
	}

}
