package com.rvlstudio.converter.csv;

import java.util.Collection;

/**
 * The Converter converts an Object or collection of Objects in to CSV
 * Only the properties that have a public getter are converted. Number
 * and String are not treated as compound structures.
 */
public interface Converter {
	/**
	 * Convert a single object in to one record
	 * 
	 * @param o Object o to be converted
	 * @return A CSV with one row
	 */
	CSV convert(Object o);

	/**
	 * The collection will be treated as records or rows
	 * 
	 * @param <T> Must implement the interface Collection
	 * @param collection The records
	 * @return A CSV containing all objects in the collection as rows
	 */
	<T extends Collection<?>> CSV convert(T collection);

	Converter registerTypeAdapter(Class<?> c, TypeAdapter adapter);

	/**
	 * Configuration of the output. There are three default options:
	 * headers -> present
	 * quoated -> true
	 * delimiter -> ","
	 * 
	 * @param name Option key
	 * @param value Value for the key in String format
	 * @return Converter implementation
	 */
	Converter withOption(String name, String value);
}
