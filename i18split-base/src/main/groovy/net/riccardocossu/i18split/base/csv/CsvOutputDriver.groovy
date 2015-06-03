package net.riccardocossu.i18split.base.csv;

import au.com.bytecode.opencsv.CSV
import au.com.bytecode.opencsv.CSV.Builder;
import au.com.bytecode.opencsv.CSVWriteProc;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import net.riccardocossu.i18split.base.config.ConfigKeys;
import net.riccardocossu.i18split.base.driver.OutputDriver;
import net.riccardocossu.i18split.base.model.DataRow;

public class CsvOutputDriver implements OutputDriver {
	private static final Logger log = LoggerFactory.getLogger(CsvOutputDriver.class)
	private static final String FILE_NAME = "i18split.output.csv.fileName"
	private static final String SEPARATOR = "i18split.output.csv.separator"
	private static final String QUOTE = "i18split.output.csv.quote"
	private String[] keys
	private Writer out
	private static final String SHORT_NAME = "csv.output"
	private CSV csv

	@Override
	public String[] init(Configuration configuration) {
		keys = configuration.getStringArray(ConfigKeys.INPUT_KEYS)
		def fileName = configuration.getString(ConfigKeys.OUTPUT_FILE,configuration.getString(FILE_NAME))
		String fileOut = "${configuration.getString(ConfigKeys.OUTPUT_BASE_PATH)}/${fileName}".toString()
		String encoding = configuration.getString(ConfigKeys.OUTPUT_ENCODING,"UTF-8")
		out = new OutputStreamWriter(new FileOutputStream(fileOut),encoding)
		String separatorString = configuration.getString(SEPARATOR,',')
		char separator = separatorString.charAt(0)
		String quoteString = configuration.getString(QUOTE, '"')
		char quote = quoteString.charAt(0)
		csv = CSV.separator(separator).quote(quote).create()
		// write header line (should this be optional)
		csv.write(out,new CSVWriteProc() {
			public void process(CSVWriter output) {
				String[] values = new String[keys.length]+1
				values[0] = 'KEY'
				int i = 1
				keys.each { k ->
					values[i++] = k
				}
				output.writeNext(values)
			}
		})
		return keys

	}

	@Override
	public String getShortName() {
		return SHORT_NAME
	}

	@Override
	public void close() throws IOException {
		if(out) {
			out.close()
		}
	}

	@Override
	public void writeRow(DataRow data) {
		csv.write(out, new CSVWriteProc() {
			public void process(CSVWriter output) {
				String[] values = new String[keys.length]+1
				values[0] = data.key
				int i = 1
				keys.each { k ->
					values[i++] = data.values[k]
				}
				output.writeNext(values)
			}
		});

	}

}
