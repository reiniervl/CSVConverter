package com.rvlstudio.converter.csv;

import java.util.Collection;

public interface Converter {
	CSV convert(Object o);
	<T extends Collection<?>> CSV convert(T collection);
}