package com.rvlstudio.converter.csv;

import java.util.List;

/**
 * CSVRow
 */
public interface Row {

	/**
	 * Retreive the cells from the Row
	 * 
	 * @return cells
	 */
	List<Cell> getCells();
}