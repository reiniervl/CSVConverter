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

	/**
	 * An adapter can change the way a certain class is converted in to a
	 * cell. If a class holds the column name, this can be changed to be the
	 * actual column name, instead of the name of the class
	 *
	 * @param adapter Implents the TypeAdapter interface to return a Cell
	 * @param <T> Type of the class
	 * @return To complete the builder pattern, the registration needs to return the converter
	 */
	<T> Converter registerTypeAdapter(TypeAdapter<T> adapter);

	/**
	 * Options are stored in a map to configure the way the Converter works. In
	 * particular the String output.
	 * Default options are:
	 * delimiter -> ;
	 * headers -> true
	 * quoated -> true
	 *
	 * @param name String of the options name/key
	 * @param option the value, also a String
	 * @return To complete the builder pattern, the registration needs to return the converter
	 */
	Converter withOption(String name, String option);
}
