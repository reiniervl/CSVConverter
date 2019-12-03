package com.rvlstudio.converter.csv;

/**
 * TypeAdapter
 */
public interface TypeAdapter {
	Cell createCell(Object object, String prefix);
}