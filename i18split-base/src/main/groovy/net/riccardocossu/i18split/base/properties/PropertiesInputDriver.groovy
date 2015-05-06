package net.riccardocossu.i18split.base.properties

import net.riccardocossu.i18split.base.config.ConfigKeys
import net.riccardocossu.i18split.base.driver.InputDriver
import net.riccardocossu.i18split.base.model.DataRow

import org.apache.commons.configuration.Configuration
import org.apache.commons.lang.StringUtils

/**
 * Created by riccardo on 04/09/14.
 */
class PropertiesInputDriver implements InputDriver {

	public static final String CONFIG_KEY_FILES_NAME = "i18split.input.properties.file.name"
	public static final String CONFIG_KEY_IS_XML = "i18split.input.properties.isXml"
	public static final String CONFIG_KEY_MASTER_LOCALE = "i18split.input.properties.masterLocale"

	private static final String SHORT_NAME = "properties.input"
	private boolean isXml = false
	/** this sets the file to be used as a reference for properties list */
	private String masterLocale
	private String baseName
	private File baseDir
	private Iterator<String> masterIterator
	Map<String,Properties> inputs
	private String defaultColumn
	@Override
	DataRow readNext() {
		if(masterIterator.hasNext()) {
			String key = masterIterator.next()
			DataRow row = new DataRow()
			row.values = [:]
			row.key = key
			Iterator<String> it = inputs.keySet().iterator()
			while(it.hasNext()) {
				String ik = it.next()
				Properties prop = inputs[ik]
				String value = prop.getProperty(key)
				if(value) {
					row.values[ik] = value
				} else {
					row.values[ik] = ''
				}
			}
			return row
		} else {
			return null
		}
	}

	@Override
	String[] init(Configuration configuration) {
		baseName = configuration.getString(CONFIG_KEY_FILES_NAME)
		baseDir = new File(configuration.getString(ConfigKeys.INPUT_BASE_PATH))
		isXml = configuration.getBoolean(CONFIG_KEY_IS_XML,false)
		masterLocale = configuration.getString(CONFIG_KEY_MASTER_LOCALE)
		defaultColumn = configuration.getString(ConfigKeys.DEFAULT_COLUMN,"default")
		inputs = getFileList()
		return inputs.keySet().toArray()
	}

	public Map getFileList() {
		def patternProp = ~/${baseName}_([a-zA-Z_]+)\.properties/
		final def files = baseDir.listFiles(new FilenameFilter() {
					@Override
					boolean accept(File dir, String name) {
						return name.startsWith(baseName)
					}
				})

		def inputFiles = [:]
		Set<String> iterable = new TreeSet<String>()
		for(f in files) {
			def matcherProp = f.name =~ patternProp
			if (matcherProp.find()) {
				def fileLocale = matcherProp.group(1)
				Properties p = new Properties()
				if(isXml) {
					f.withInputStream { is ->
						p.loadFromXML(is)
					}
				} else {
					f.withInputStream { r ->
						p.load(r)
					}
				}
				if (StringUtils.isNotBlank(masterLocale)){
					if(fileLocale.equals(masterLocale)) {
						iterable.addAll(p.keySet())
					}
				}else{
					iterable.addAll(p.keySet())
				}
				inputFiles[fileLocale] = p
			}
		}
		masterIterator = iterable.iterator()
		File defaultFile = new File(baseDir,baseName+".properties")
		if(defaultFile.exists()) {
			Properties p = new Properties()
			if(isXml) {
				defaultFile.withInputStream { is ->
					p.loadFromXML(is)
				}
			} else {
				defaultFile.withInputStream { r ->
					p.load(r)
				}
			}
			inputFiles[defaultColumn] = p
		}
		return inputFiles
	}

	@Override
	String getShortName() {
		return SHORT_NAME
	}

	@Override
	void close() throws IOException {
		// nothing to do here, input files have already been closed
	}
}
