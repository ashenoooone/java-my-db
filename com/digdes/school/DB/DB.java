package com.digdes.school.DB;

import org.w3c.dom.ls.LSOutput;

import java.util.*;

public class DB {
    private final List<Map<String, Object>> table = new ArrayList<>();
    private final List<Column> columns = new ArrayList<>();

    public DB(List<Column> columns) {
        this.columns.addAll(columns);
    }

    public DB(Column... columns) {
        this.columns.addAll(List.of(columns));
    }

    public void addRow(Map<String, Object> row) {
        this.table.add(row);
    }

    public List<Map<String, Object>> execute(String query) {
        String[] querySplit = query.split(" ");
        switch (querySplit[0]) {
            case "INSERT":
                return insertQuery(query);
            case "DELETE":
                return deleteQuery(query);
            case "UPDATE":
                return updateQuery(query);
            case "SELECT":
                return selectQuery(query);
            default:
                throw new IllegalArgumentException("Неверный запрос");
        }
    }

    private List<Map<String, Object>> insertQuery(String query) {
        String[] values = query.replace("INSERT VALUES", "").split(",");
        Map<String, Object> row = new HashMap<>();
        for (String value : values) {
            String[] parts = value.trim().split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Ошибка синтаксиса");
            }
            String key = parts[0].trim().replace("'", "");
            String stringValue = parts[1].trim();
            ColumnType columnType;
            try {
                columnType = this.findColumnByName(key).getType();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Неверное имя столбца: " + key);
            }
            Class<?> valueType = columnType.getTypeClass();
            Object parsedValue;
            if (valueType == String.class) {
                parsedValue = stringValue;
            } else if (valueType == Boolean.class) {
                parsedValue = Boolean.parseBoolean(stringValue);
            } else if (valueType == Long.class) {
                parsedValue = Long.valueOf(stringValue);
            } else if (valueType == Double.class) {
                parsedValue = Double.valueOf(stringValue);
            } else {
                throw new IncompatibleColumnTypeException("Неподдерживаемый тип столбца: " + valueType.getSimpleName());
            }
            row.put(key, parsedValue);
        }
        for (Column column : this.columns) {
            if (!row.containsKey(column.getName())) row.put(column.getName(), null);
        }
        table.add(row);
        return this.getTable();
    }

    private List<Map<String, Object>> deleteQuery(String query) {
        System.out.println("DELETING VALUE");
        return this.getTable();
    }

    private List<Map<String, Object>> updateQuery(String query) {
//        UPDATE VALUES ‘active’=false, ‘cost’=10.1 where ‘id’=3
        String[] values = query.replace("UPDATE VALUES", "").split(",");
        Map<String, Object> row = new HashMap<>();
        List<String> valuesToUpdate = new ArrayList<>();
        String condition = "";
        for (String value : values) {
            String[] splittedValue = value.toLowerCase().trim().split("where");
            valuesToUpdate.add(splittedValue[0]);
            if (splittedValue.length == 2) {
                condition = splittedValue[1].trim();
            }
        }
        System.out.println(condition);
        return this.getTable();
    }

    private List<Map<String, Object>> selectQuery(String query) {
        System.out.println("SELECTING VALUE");
        return this.getTable();
    }

    public List<Map<String, Object>> getTable() {
        List<Map<String, Object>> copyList = new ArrayList<>();
        for (Map<String, Object> originalMap : this.table) {
            Map<String, Object> copyMap = new HashMap<>(originalMap);
            copyList.add(copyMap);
        }
        return copyList;
    }


    private Column findColumnByName(String columnName) {
        for (Column column : this.columns) {
            if (Objects.equals(columnName, column.getName())) return column;
        }
        throw new IllegalArgumentException("Такого столбца нет");
    }

    @Override
    public String toString() {
        return "com.digdes.school.DB{" +
                "table=" + table +
                '}';
    }
}
