package com.rvlstudio.converter.csv;

import java.util.List;

/**
 * Main interface for using this converter.
 * Use the static method {@link CSV#createConverter} to get an implementing
 * intance of a Converter interface to converter any Object to CSV.
 * All publicly accessible properties of the object will be converterd in to CSV format.
 * The {@link Object#toString} method is overriden to return the formatted CSV.
 * 
 * Usage:
 * SomeClass someClass = ...
 * CSV.createConverter().converter(someClass);
 * System.out.println(CSV);
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

	/**
	 * Create a Converter implementation to convert an object to CSV.
	 * 
	 * @return Converter implementation
	 */
	public static Converter createConverter() {
		return new ConverterImpl();
	}
}