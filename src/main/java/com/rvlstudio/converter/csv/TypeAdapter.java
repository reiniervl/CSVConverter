package com.rvlstudio.converter.csv;

/**
 * TypeAdapter
 */
public interface TypeAdapter<T> {
	Cell createCell(Object object, String prefix);
	Class<?> getType();
}