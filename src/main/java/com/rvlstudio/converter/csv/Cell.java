package com.rvlstudio.converter.csv;

/**
 * Cell
 */
public interface Cell {
	/**
	 * Optional method to get the header or column name.
	 * Headers are not required in CSV
	 * 
	 * @return Name of the column
	 */
	String getColumnName();

	/**
	 * Retreives the data or value from the cell. If the cell
	 * is empty, null is returned
	 * 
	 * @return Value of the cell or null
	 */
	String getData();

	/**
	 * Convenience method to check if the cells holds any data
	 * 
	 * @return true if there cell does not hold null
	 */
	boolean hasData();
}