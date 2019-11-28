package com.rvlstudio.converter.csv;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ConverterFactory
 *
 * Creeert een implementatie van de Converter om Objecten om te zetten naar CSV
 */
public class ConverterFactory {
	private static String delimiter;
	private static boolean quoated, headers;
	private List<TypeAdapter<?>> adapters;

	private ConverterFactory(List<TypeAdapter<?>> adapters, Map<String, String> options) {
		this.adapters = adapters;
		delimiter = options.getOrDefault("delimiter", ";");
		quoated = Boolean.parseBoolean(options.getOrDefault("quoated", "true"));
		headers = Boolean.parseBoolean(options.getOrDefault("headers", "true"));
	}

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
					.filter(pd -> pd.getReadMethod() != null && pd.getPropertyType() != Class.class)
					.collect(Collectors.toList());

			Optional<TypeAdapter<?>> adapter =
					adapters.stream().filter(a -> a.getType().isAssignableFrom(o.getClass())).findAny();
			if (adapter.isPresent()) {
				cells.add(adapter.get().createCell(o, prefix + "/"));
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

	private static List<Row> getRows(Object o, ConverterFactory factory) {
		if (Collection.class.isAssignableFrom(o.getClass())) {
			return ((Collection<?>) o).stream().map(factory::findProperties).map(factory::createRow)
					.collect(Collectors.toList());
		} else {
			return Collections.singletonList(factory.createRow(factory.findProperties(o)));
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

	public static Converter creatConverter() {
		return new Converter() {
			private List<TypeAdapter<?>> adapters = new ArrayList<>();
			private Map<String, String> options = new HashMap<>();

			@Override
			public CSV convert(Object o) {

				return new CSV() {
					private List<Row> rows = ConverterFactory.getRows(o, new ConverterFactory(adapters, options));

					@Override
					public List<Row> getRows() {
						return rows;
					}

					@Override
					public List<String> getHeaders() {
						if (rows != null && !rows.isEmpty()) {
							return rows.stream().max(Comparator.comparingInt(r -> r.getCells().size()))
									.get().getCells().stream().map(Cell::getColumnName).collect(Collectors.toList());
						}
						return new ArrayList<>(0);
					}

					private String rowToString(Row row, List<String> columns) {
						List<String> rowData = new ArrayList<>();
						for (String column : columns) {
							Optional<Cell> result = row.getCells().stream()
									.filter(c -> c.getColumnName().equals(column)).findFirst();
							if(result.isPresent() && result.get().hasData()) {
								rowData.add( quoated ? "\"" + result.get().getData()+ "\"" : result.get().getData());
							} else {
								rowData.add("");
							}
						}
						return String.join(delimiter, rowData);
					}

					@Override
					public String toString() {
						List<String> columns = getHeaders();
						StringBuilder builder = new StringBuilder();
						if(headers) builder.append(String.join(delimiter, columns)).append("\r\n");
						for (Row row : rows) {
							builder.append(rowToString(row, columns)).append("\r\n");
						}
						return builder.toString();
					}
				};
			}

			@Override
			public <T extends Collection<?>> CSV convert(T collection) {
				return convert((Object) collection);
			}

			@Override
			public <T> Converter registerTypeAdapter(TypeAdapter<T> adapter) {
				this.adapters.add(adapter);
				return this;
			}

			public Converter withOption(String name, String option) {
				this.options.put(name, option);
				return this;
			}
		};
	}
}
