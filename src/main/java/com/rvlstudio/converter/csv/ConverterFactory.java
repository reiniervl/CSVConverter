package com.rvlstudio.converter.csv;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ConverterFactory
 */
class ConverterFactory {
	private List<Cell> findProperties(Object o) {
		return findProperties(o, null);
	}

	private List<Cell> findProperties(Object o, String prefix) {
		List<Cell> cells = new ArrayList<>();
		if (o == null)
			return cells;

		try {
			BeanInfo info = Introspector.getBeanInfo(o.getClass());
			List<PropertyDescriptor> descriptors = Arrays.stream(info.getPropertyDescriptors())
					.filter(pd -> pd.getReadMethod() != null && pd.getPropertyType() != java.lang.Class.class)
					.collect(Collectors.toList());

			for (PropertyDescriptor pd : descriptors) {
				if (Collection.class.isAssignableFrom(pd.getPropertyType()))
					cells.addAll(findListProperties(((Collection<?>) getValue(pd, o)), pd.getName()));
				else
					cells.add(toCell(pd, o, prefix));
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return cells;
	}

	private List<Cell> findListProperties(Collection<?> collection, String container) {
		List<Cell> cells = new ArrayList<>();

		int i = 0;
		for (Object o : collection) {
			if (o.getClass().isPrimitive() || o.getClass() == String.class) {
				cells.add(toCell(container + "/" + i++, o));
			} else {
				cells.addAll(findProperties(o, container+ "/" + i++));
			}
		}

		return cells;
	}

	private Cell toCell(PropertyDescriptor pd, Object o, String prefix) {
		return toCell(prefix == null ? pd.getName() : prefix + "/" + pd.getName(), getValue(pd, o));
	}

	private Cell toCell(String name, Object o) {
		return new Cell() {
			private Object data = o;
			private String columnName = name;

			@Override
			public boolean hasData() {
				return data != null;
			}

			@Override
			public String getData() {
				return data.toString();
			}

			@Override
			public String getColumnName() {
				return columnName;
			}

			@Override
			public String toString() {
				return String.format("%s: %s", columnName, data);
			}
		};
	}

	private Object getValue(PropertyDescriptor pd, Object o) {
		try {
			return pd.getReadMethod().invoke(o);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Converter creatConverter(boolean headers) {
		return new Converter() {
			@Override
			public CSV convert(Object o) {

				return new CSV(){
					private List<Row> rows = Arrays.asList(new Row(){
					
						private List<Cell> cells = new ConverterFactory().findProperties(o, null);
						@Override
						public List<Cell> getCells() {
							return cells;
						}
					});
				
					@Override
					public List<Row> getRows() {
						return rows;
					}
				
					@Override
					public Set<String> getHeaders() {
						if(!rows.isEmpty()) return rows.get(0).getCells().stream().map(Cell::getColumnName).collect(Collectors.toSet());
						return null;
					}
				};
			}

			@Override
			public <T extends Collection<?>> CSV convert(T collection) {
				ConverterFactory factory = new ConverterFactory();
				List<Row> csvRows = collection.stream().map(factory::findProperties).map(l -> new Row(){
					private List<Cell> cells = l;
				
					@Override
					public List<Cell> getCells() {
						return cells;
					}
				}).collect(Collectors.toList());

				return new CSV(){
					private List<Row> rows = csvRows;
					@Override
					public List<Row> getRows() {
						return rows;
					}
				
					@Override
					public Set<String> getHeaders() {
						if(!rows.isEmpty()) return rows.get(0).getCells().stream().map(Cell::getColumnName).collect(Collectors.toSet());
						return null;
					}
				};
			}
		};
	}
}
