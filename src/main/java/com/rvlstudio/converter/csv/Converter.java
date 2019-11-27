package com.rvlstudio.converter.csv;

import java.util.Collection;

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

	<T> Converter registerTypeAdapter(TypeAdapter<T> adapter);
}