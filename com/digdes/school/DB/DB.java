package com.digdes.school.DB;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DB {
    private final List<Map<String, Object>> table = new ArrayList<>();
    private final List<Column> columns = new ArrayList<>();

    private final List<String> operators = new ArrayList<>(List.of(new String[]{"=", "!=", "like", "ilike", ">=", "<=", "<", ">"}));

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
        return filterData(condition);
    }

    private List<Map<String, Object>> selectQuery(String query) {
        System.out.println("SELECTING VALUE");
        return this.getTable();
    }

    public List<Map<String, Object>> filterData(String condition) {
//        like ilike < <= > >= == != and or
        List<Map<String, Object>> filteredRows = new ArrayList<>();
        List<Map<String, Object>> rows = this.getTable();
        System.out.println(condition);
        Pattern pattern = Pattern.compile("'(.*)' *([^\\d ]*) * '*([^']+)'*$| ");
        Matcher matcher = pattern.matcher(condition.trim());
        if (matcher.find()) {
            Column column = findColumnByName(matcher.group(1));
            String operator = matcher.group(2);
            if (!column.getAvailableOperators().contains(operator))
                throw new IllegalArgumentException("Неподдерживаемый оператор");
            Class<?> valueType = column.getType().getTypeClass();
            Object parsedValue = matcher.group(3);
            if (valueType == String.class) {
                parsedValue = (String) parsedValue;
            } else if (valueType == Boolean.class) {
                parsedValue = (Boolean) parsedValue;
            } else if (valueType == Long.class) {
                parsedValue = (Long) parsedValue;
            } else if (valueType == Double.class) {
                parsedValue = (Double) parsedValue;
            } else {
                throw new IncompatibleColumnTypeException("Неподдерживаемый тип столбца: " + valueType.getSimpleName());
            }
            for (Map<String, Object> row : this.getTable()) {
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    if (Objects.equals(entry.getKey(), column.getName())) {
                        switch (operator) {
                            case ">":
                                
                                break;
                            case ">=":
                                break;
                            case "==":
                                break;
                            case "!=":
                                break;
                            case "like":
                                break;
                            case "ilike":
                                break;
                        }
                    }
                }
            }
        }


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
