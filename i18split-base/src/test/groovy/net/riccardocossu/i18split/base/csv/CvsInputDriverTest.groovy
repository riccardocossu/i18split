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

	@Test
	public void csvInputDriverShouldNotReadCorrectlyAFileWithEncodingISO8859IfConfiguredToReadUTF8() {
		CsvInputDriver driver = new CsvInputDriver();
		Configuration conf = new BaseConfiguration()
		conf.addProperty(ConfigKeys.INPUT_BASE_PATH, "src/test/resources/engine/inCsvOutProperties")
		conf.addProperty(CsvInputDriver.FILE_NAME, "encoding_UTF-8.csv")
		conf.addProperty(ConfigKeys.INPUT_ENCODING, "ISO_8859-1")
		String[] header = driver.init(conf)
		assertEquals(1,header.length)
		assertEquals("it",header[0])
		def row = driver.readNext()
		assertFalse(row.values["it"].equals("età"))
	}

	@Test
	public void csvInputDriverShouldReadCorrectlyAFileWithEncodingISO8859IfConfiguredToReadIt() {
		CsvInputDriver driver = new CsvInputDriver();
		Configuration conf = new BaseConfiguration()
		conf.addProperty(ConfigKeys.INPUT_BASE_PATH, "src/test/resources/engine/inCsvOutProperties")
		conf.addProperty(CsvInputDriver.FILE_NAME, "encoding_ISO_8859-1.csv")
		conf.addProperty(ConfigKeys.INPUT_ENCODING, "ISO_8859-1")
		String[] header = driver.init(conf)
		assertEquals(1,header.length)
		assertEquals("it",header[0])
		def row = driver.readNext()
		assertEquals("età",row.values["it"])
	}

}
