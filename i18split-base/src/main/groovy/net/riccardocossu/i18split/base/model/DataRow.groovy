package net.riccardocossu.i18split.base.model

/**
 * this abstracts a single row
 * @author riccardo
 *
 */
class DataRow {

	/**
	 * language independent key
	 */
	String key
	/**
	 * the key is the locale code (it_IT), the value the localized string
	 */
	Map<String,String> values

}
