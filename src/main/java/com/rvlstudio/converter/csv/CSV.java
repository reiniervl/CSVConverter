package com.rvlstudio.converter.csv;

import java.util.List;

/**
 * CSV
 */
public interface CSV {
	/**
	 * The headers are an optional attribute in CSV. The header
	 * or column names must be unique. The order is not guaranteed
	 * 
	 * @return Unique set of column names or null if not present
	 */
	List<String> getHeaders();

	/**
	 * The list of rows is not ordered, but the order is not
	 * guaranteed, meaning that it may differ from the list
	 * provided.
	 * 
	 * @return All rows of cells
	 */
	List<Row> getRows();

	public static Converter createConverter() {
		return new ConverterImpl();
	}
}