package com.rvlstudio.converter.csv;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ConverterImpl implements Converter {
	private Map<Class<?>, TypeAdapter> adapters = new HashMap<Class<?>, TypeAdapter>();
	private Map<String, String> options = new HashMap<String, String>();

	private List<Cell> findProperties(Object o) {
		return findProperties(o, "");
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

			if (adapters.containsKey(o.getClass())) {
				cells.add(adapters.get(o.getClass()).createCell(o, prefix + "/"));
			} else {

				for (PropertyDescriptor pd : descriptors) {
					if (Collection.class.isAssignableFrom(pd.getPropertyType()))
						cells.addAll(findListProperties(((Collection<?>) getValue(pd, o)), pd.getName()));
					else
						cells.add(toCell(pd, o, prefix));
				}
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
			if (o.getClass().getGenericSuperclass().getTypeName().equals("java.lang.Number")
					|| o.getClass() == String.class) {
				cells.add(toCell(container + "/" + i++, o));
			} else {
				cells.addAll(findProperties(o, container + "/" + i++));
			}
		}

		return cells;
	}

	private Object getValue(PropertyDescriptor pd, Object o) {
		try {
			return pd.getReadMethod().invoke(o);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Cell toCell(PropertyDescriptor pd, Object o, String prefix) {
		return toCell(prefix.isEmpty() ? pd.getName() : prefix + "/" + pd.getName(), getValue(pd, o));
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

	private List<Row> getRows(Object o) {
		if (Collection.class.isAssignableFrom(o.getClass())) {
			return ((Collection<?>) o).stream().map(this::findProperties).map(this::createRow)
					.collect(Collectors.toList());
		} else {
			return Arrays.asList(createRow(findProperties(o)));
		}
	}

	private Row createRow(List<Cell> cell) {
		return new Row() {
			private List<Cell> cells = cell;

			@Override
			public List<Cell> getCells() {
				return cells;
			}
		};
	}

	@Override
	public CSV convert(Object o) {
		return new CSVImpl(getRows(o), options);
	}

	@Override
	public <T extends Collection<?>> CSV convert(T collection) {
		return convert((Object) collection);
	}

	@Override
	public Converter registerTypeAdapter(Class<?> c, TypeAdapter adapter) {
		this.adapters.put(c, adapter);
		return this;
	}

	@Override
	public Converter withOption(String name, String value) {
		options.put(name, value);
		return this;
	}

}
