package com.rvlstudio.converter.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class CSVImpl implements CSV {
	private List<Row> rows;
	private boolean headers;
	private boolean quoated;
	private String delimiter;

	CSVImpl(List<Row> rows, Map<String, String> options) {
		this.rows = rows;
		this.delimiter = options.getOrDefault("delimiter", ",");
		headers = options.getOrDefault("headers", "present").toLowerCase().equals("present");
		this.quoated = Boolean.parseBoolean(options.getOrDefault("quoated", "true").toLowerCase());
	}

	private String rowToString(Row row, List<String> columns) {
		StringBuilder builder = new StringBuilder();
		List<String> rowData = new ArrayList<>();
		for (String column : columns) {
			Optional<Cell> result =
					row.getCells().stream().filter(c -> c.getColumnName().equals(column)).findFirst();
			String data = result.isPresent() && result.get().hasData() ? result.get().getData() : "";
			if (quoated && !data.isEmpty()) data = "\"" + data + "\"";
			rowData.add(data);
		}
		builder.append(String.join(delimiter, rowData)).append("\r\n");
		return builder.toString();
	}

	@Override
	public List<Row> getRows() {
		return rows;
	}

	@Override
	public List<String> getHeaders() {
		if (rows != null && !rows.isEmpty()) {
			return rows.stream().max((r1, r2) -> r1.getCells().size() - r2.getCells().size()).get()
					.getCells().stream().map(Cell::getColumnName).collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public String toString() {
		List<String> columns = getHeaders();
		StringBuilder builder = new StringBuilder();
		if (headers && columns != null)
			builder.append(String.join(delimiter, columns)).append("\r\n");
		rows.forEach(r -> builder.append(rowToString(r, columns)));
		return builder.toString();
	}
}
