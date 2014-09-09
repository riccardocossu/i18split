package net.riccardocossu.i18split.base.csv;

import static org.junit.Assert.*;
import net.riccardocossu.i18split.base.config.ConfigKeys;

import org.apache.commons.configuration.BaseConfiguration
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

public class CvsInputDriverTest {

	@Test
	public void csvInputDriverShouldNotcrash() {
		CsvInputDriver driver = new CsvInputDriver();
		Configuration conf = new BaseConfiguration()
		conf.addProperty(ConfigKeys.INPUT_BASE_PATH, "src/test/resources/engine/inCsvOutProperties")
		conf.addProperty(CsvInputDriver.FILE_NAME, "i18split.csv")
		String[] header = driver.init(conf)
		assertEquals(2,header.length)
		assertEquals("de",header[0])
		assertEquals("en",header[1])

	}

}
