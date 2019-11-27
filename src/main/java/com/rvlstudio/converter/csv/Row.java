package com.rvlstudio.converter.csv;

import java.util.List;

/**
 * CSV Row
 */
public interface Row {

	/**
	 * Retreive the cells from the Row
	 * 
	 * @return cells
	 */
	List<Cell> getCells();
}