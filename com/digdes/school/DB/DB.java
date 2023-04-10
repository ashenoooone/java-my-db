package com.digdes.school.DB;
import java.util.*;

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
        System.out.println("------------------------------------------------");
        return filterData(condition);
    }

    private List<Map<String, Object>> selectQuery(String query) {
        System.out.println("SELECTING VALUE");
        return this.getTable();
    }

    public List<Map<String, Object>> filterData(String filterString) {
        List<Map<String, Object>> filteredRows = new ArrayList<>();
        String[] filters = filterString.split("(?i)\\s+(and|or)\\s+");
        for (Map<String, Object> row : this.getTable()) {
            boolean matches = false;
            for (String filter : filters) {
                String[] parts = filter.split("\\s+");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid filter string: " + filterString);
                }
                String columnName = parts[0].replaceAll("^'|'$", "");
                Column column = findColumnByName(columnName);
                String operator = parts[1];
                String valueString = parts[2].replaceAll("^'|'$", "");
                Object value = row.get(columnName);
                Object value2;
                if (!column.getAvailableOperators().contains(operator)) {
                    throw new IllegalArgumentException("Неподдерживаемый оператор");
                }
                if (value instanceof Long) {
                    value2 = Long.valueOf(valueString);
                } else if (value instanceof String) {
                    value2 = valueString;
                } else if (value instanceof Double) {
                    value2 = Double.valueOf(valueString);
                } else if (value instanceof Boolean) {
                    value2 = Boolean.valueOf(valueString);
                } else {
                    throw new IllegalArgumentException("Неподдерживаемый тип переменной");
                }
                switch (operator) {
                    case "<":
                        if (lessThan(value, value2)) matches = true;
                        break;
                    case "<=":
                        if (lessThanOrEqual(value, value2)) matches = true;
                        break;
                    case ">":
                        System.out.println(greaterThan(value, value2));
                        if (greaterThan(value, value2)) matches = true;
                        break;
                    case ">=":
                        if (greaterThanOrEqual(value, value2)) matches = true;
                        break;
                    case "==":
                        if (equal(value, value2)) matches = true;
                        break;
                    case "!=":
                        if (notEqual(value, value2)) matches = true;
                        break;
                    case "like":
                        if (like(value, value2)) matches = true;
                        break;
                    case "ilike":
                        if (iLike(value, value2)) matches = true;
                        break;
                }

                if (!matches) {
                    break;
                }
            }
            if (matches) {
                filteredRows.add(row);
            }
        }
        return filteredRows;
    }


    private static boolean like(Object value1, Object value2) {
        String pattern = ((String) value2).replace("%", ".*");
        return ((String) value1).matches(pattern);
    }

    private static boolean iLike(Object value1, Object value2) {
        String pattern = ((String) value2).replace("%", ".*").toLowerCase();
        return ((String) value1).toLowerCase().matches(pattern);
    }

    private static boolean lessThan(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2) < 0;
    }

    private static boolean lessThanOrEqual(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2) <= 0;
    }

    private static boolean greaterThan(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2) > 0;
    }

    private static boolean greaterThanOrEqual(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2) >= 0;
    }

    private static boolean equal(Object value1, Object value2) {
        if (value1 == null || value2 == null) {
            return value1 == null && value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    private static boolean notEqual(Object value1, Object value2) {
        return !equal(value1, value2);
    }

    public List<Map<String, Object>> getTable() {
        return this.table;
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
