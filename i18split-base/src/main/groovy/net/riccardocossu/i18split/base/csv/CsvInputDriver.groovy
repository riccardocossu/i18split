package net.riccardocossu.i18split.base.csv

import net.riccardocossu.i18split.base.config.ConfigKeys
import net.riccardocossu.i18split.base.driver.InputDriver
import net.riccardocossu.i18split.base.model.DataRow

import org.apache.commons.configuration.Configuration

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVReadProc;

/**
 * Created by riccardo on 04/09/14.
 */
class CsvInputDriver implements InputDriver {

	private static final String FILE_NAME = "i18split.input.csv.fileName"
	private static final String SEPARATOR = "i18split.input.csv.separator"
	private static final String QUOTE = "i18split.input.csv.quote"

	private static final String SHORT_NAME = "csv.input"
	private String fileName
	private File baseDir
	private CSV csv
	private Reader input
	private Iterator<String[]> lineIt
	private String[] inputs

    @Override
    DataRow readNext() {
		if(lineIt.hasNext()) {
			String[] line = lineIt.next()
			DataRow row = new DataRow()
			if(line.length > 0 && line[0].trim().length() > 0) {
				row.values = [:]
				row.key = line[0]
				for (int col = 1; col <= inputs.length; col++) {
					row.values[inputs[col - 1]] = line[col]
				}
			}
			return row
		} else {
			return null
		}
    }

    @Override
    String[] init(Configuration configuration) {
        fileName = configuration.getString(FILE_NAME)
        baseDir = new File(configuration.getString(ConfigKeys.INPUT_BASE_PATH))
		String separatorString = configuration.getString(SEPARATOR,',')
		char separator = separatorString.charAt(0)
		String quoteString = configuration.getString(QUOTE, '"')
		char quote = quoteString.charAt(0)
		String encoding = configuration.getString(ConfigKeys.INPUT_ENCODING, 'UTF-8')
		csv = CSV.separator(separator).quote(quote).create()
		input = new InputStreamReader(new FileInputStream("${baseDir}/${fileName}".toString()),encoding)
		List<String[]> lines = new ArrayList<String[]>()
		csv.read(input, new CSVReadProc() {
			void procRow(int rowIndex, String... values) {
				if(rowIndex == 0) {
					// it's the header
					inputs = new String[values.length -1]
					for(int i = 0; i < inputs.length; i++) {
						inputs[i] = values[i+1]
					}
				} else {
					lines.add(values)
				}
			}
		})
		lineIt = lines.iterator()
		return inputs
    }


    @Override
    String getShortName() {
        return SHORT_NAME
    }

    @Override
    void close() throws IOException {
		if(input) {
			input.close()
		}
    }
}
