package net.riccardocossu.i18split.base.properties

import net.riccardocossu.i18split.base.config.ConfigKeys;

import org.apache.commons.configuration.BaseConfiguration
import org.apache.commons.configuration.Configuration;
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Created by riccardo on 04/09/14.
 */
class PropertiesInputDriverTest {
    @Test
    def void should_empty_files_create_properties() {
        PropertiesInputDriver driver = new PropertiesInputDriver()
		Configuration conf  = new BaseConfiguration()
		conf.addProperty(PropertiesInputDriver.CONFIG_KEY_FILES_NAME, "emptyFile")
		conf.addProperty(ConfigKeys.INPUT_BASE_PATH, "src/test/resources/getFileList/testEmptyFiles")
		driver.init(conf)
        def propFiles = driver.getFileList()
        assertEquals(2,propFiles.size())
        assertTrue(propFiles.containsKey("it"))
        assertTrue(propFiles.containsKey("en_GB"))
    }
}
