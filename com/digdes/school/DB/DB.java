package com.digdes.school.DB;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DB {
    private final List<Map<String, Object>> table = new ArrayList<>();
    private final List<Column> columns = new ArrayList<>();

    public DB(Column... columns) {
        this.columns.addAll(List.of(columns));
    }

    /**
     * Метод execute выполняет запрос в зависимости от его типа: insert, delete, update или select.
     *
     * @param query строка запроса, которую необходимо выполнить.
     * @return список отображений, представляющих результат выполнения запроса.
     * @throws IllegalArgumentException если запрос имеет неверный формат или тип.
     */
    public List<Map<String, Object>> execute(String query) {
        String[] querySplit = query.split(" ");
        switch (querySplit[0].toLowerCase()) {
            case "insert":
                return insertQuery(query);
            case "delete":
                return deleteQuery(query);
            case "update":
                return updateQuery(query);
            case "select":
                return selectQuery(query);
            default:
                throw new IllegalArgumentException("Неверный запрос");
        }
    }

    /**
     * Метод для выполнения операции INSERT в таблице.
     * Запрос может содержать значения для всех столбцов таблицы,
     * если значения не указаны, то для соответствующих столбцов будет использовано значение null.
     *
     * @param query строка запроса INSERT для выполнения в таблице
     * @return список, содержащий только один элемент - вставленную строку в виде Map<String, Object>
     * @throws IllegalArgumentException если в запросе есть синтаксические ошибки или
     *                                  указаны некорректные типы данных для столбцов таблицы
     */
    private List<Map<String, Object>> insertQuery(String query) {
        String[] values = query.replace("INSERT VALUES", "").split(",");
        Map<String, Object> row = new HashMap<>();
        for (String value : values) {
            String[] parts = value.trim().split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Ошибка синтаксиса");
            }
            String key = parts[0].trim().replace("'", "");
            Column column = findColumnByName(key);
            Object parsedValue = convertToCorrectClass(parts[1].trim(), column);
            row.put(key, parsedValue);
        }
        for (Column column : this.columns) {
            if (!row.containsKey(column.getName())) row.put(column.getName(), null);
        }
        table.add(row);
        return Collections.singletonList(row);
    }

    /**
     * Выполняет запрос на удаление данных из таблицы и возвращает результат в виде списка карт.
     *
     * @param query строка запроса, который должен быть выполнен в таблице.
     *              Если запрос не содержит "WHERE" выражения, то метод удаляет все строки таблицы.
     *              В запросе должно присутствовать выражение "WHERE" для задания условия удаления строк.
     * @return список карт, содержащих удаленные строки таблицы.
     * @throws IllegalArgumentException если переданная строка запроса некорректна.
     */
    private List<Map<String, Object>> deleteQuery(String query) {
        if (!query.toLowerCase().contains("where")) {
            List<Map<String, Object>> boof = new ArrayList<>(this.getTable());
            this.table.clear();
            return boof;
        }
        Pattern conditionPattern = Pattern.compile("WHERE (.+)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = conditionPattern.matcher(query);
        if (matcher.find()) {
            String condition = matcher.group(1);
            System.out.println(condition);
            List<Map<String, Object>> rowsToDelete = filterData(condition);
            this.getTable().removeAll(rowsToDelete);
            return rowsToDelete;
        } else {
            throw new IllegalArgumentException("Некорректно задан запрос");
        }
    }

    /**
     * Выполняет запрос на обновление данных в таблице и возвращает результат в виде списка строк.
     *
     * @param query строка запроса, который должен быть выполнен в таблице.
     *              Если запрос не содержит "WHERE" выражения, то метод обновляет все строки таблицы.
     * @return список строк, содержащих обновленные строки таблицы.
     * @throws IllegalArgumentException если переданная строка запроса некорректна.
     */
    private List<Map<String, Object>> updateQuery(String query) {
        if (!query.toLowerCase().contains("where")) {
            Pattern valuesPattern = Pattern.compile("VALUES (.+?)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher valuesMatcher = valuesPattern.matcher(query);
            if (valuesMatcher.find()) {
                String toUpdate = valuesMatcher.group(1).replace("'", "");
                String[] valuesStringArray = toUpdate.split(",");
                for (String values : valuesStringArray) {
                    String[] splittedValues = values.trim().split("=");
                    Object value = convertToCorrectClass(splittedValues[1], findColumnByName(splittedValues[0]));
                    if (value != null && value.getClass() == String.class) value = "'" + value + "'";
                    for (Map<String, Object> row : this.getTable()) {
                        for (Map.Entry<String, Object> entry : row.entrySet()) {
                            if (Objects.equals(entry.getKey(), splittedValues[0])) {
                                entry.setValue(value);
                            }
                        }
                    }
                }
                return this.getTable();
            } else {
                throw new IllegalArgumentException("Некорректно задан запрос");
            }
        }
        Pattern valuesPattern = Pattern.compile("VALUES (.+?) WHERE (.+)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher valuesMatcher = valuesPattern.matcher(query);
        if (valuesMatcher.find()) {
            String toUpdate = valuesMatcher.group(1).replace("'", "");
            String condition = valuesMatcher.group(2);
            List<Map<String, Object>> rowsToUpdate = filterData(condition);
            String[] valuesStringArray = toUpdate.split(",");
            for (String values : valuesStringArray) {
                String[] splittedValues = values.trim().split("=");
                Object value = convertToCorrectClass(splittedValues[1], findColumnByName(splittedValues[0]));
                for (Map<String, Object> row : rowsToUpdate) {
                    for (Map.Entry<String, Object> entry : row.entrySet()) {
                        if (Objects.equals(entry.getKey(), splittedValues[0])) {
                            entry.setValue(value);
                        }
                    }
                }
            }
            return rowsToUpdate;
        } else {
            throw new IllegalArgumentException("Некорректно задан запрос");
        }
    }

    /**
     * Выполняет запрос к таблице и возвращает результат в виде списка строк.
     *
     * @param query строка запроса, который должен быть выполнен в таблице.
     *              Если запрос не содержит "WHERE" выражения, то метод вернет всю таблицу.
     * @return список строк, подходящих под условие.
     * @throws IllegalArgumentException если переданная строка запроса некорректна.
     */
    private List<Map<String, Object>> selectQuery(String query) {
        if (!query.toLowerCase().contains("where")) {
            return this.getTable();
        }
        Pattern valuesPattern = Pattern.compile("WHERE (.+)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher valuesMatcher = valuesPattern.matcher(query);
        if (valuesMatcher.find()) {
            return filterData(valuesMatcher.group(1));
        } else {
            throw new IllegalArgumentException("Некорректно задан запрос");
        }
    }

    /**
     * Фильтрует данные в таблице по заданному фильтру.
     *
     * @param filterString строка фильтра, которая содержит одно или несколько условий в формате:
     * @return отфильтрованные строки таблицы в виде списка отображений,
     * каждое отображение представляет одну строку таблицы
     * и содержит пары ключ-значение для каждого столбца таблицы.
     * @throws IllegalArgumentException если строка фильтра задана
     *                                  некорректно или используется неподдерживаемый оператор.
     */
    public List<Map<String, Object>> filterData(String filterString) {
        List<Map<String, Object>> filteredRows = new ArrayList<>();
        String[] filters = filterString.split("(?i)\\s+(and|or)\\s+");
        Pattern columnOperatorValuePattern = Pattern.compile("'(\\w+)' +([\\w><!=]+) +'?([\\d.А-Яа-я\\w%]+)'?");
//        todo добавить поддержку and or операторов
        for (Map<String, Object> row : this.getTable()) {
            boolean matches = false;
            for (String filter : filters) {
                Matcher matcher = columnOperatorValuePattern.matcher(filter);
                if (!matcher.find()) throw new IllegalArgumentException("Некорректно задан фильтр");

                Column column = findColumnByName(matcher.group(1));
                String operator = matcher.group(2);
                if (!column.getAvailableOperators().contains(operator)) {
                    throw new IllegalArgumentException("Неподдерживаемый оператор");
                }
                Object value = row.get(column.getName());
                Object value2 = convertToCorrectClass(matcher.group(3), column);
                switch (operator) {
                    case "<":
                        if (lessThan(value, value2)) matches = true;
                        break;
                    case "<=":
                        if (lessThanOrEqual(value, value2)) matches = true;
                        break;
                    case ">":
                        if (greaterThan(value, value2)) matches = true;
                        break;
                    case ">=":
                        if (greaterThanOrEqual(value, value2)) matches = true;
                        break;
                    case "=":
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

    /**
     * Конвертирует переданное значение в правильный тип данных в соответствии с типом колонки.
     * Если передано значение "null", то возвращается null.
     *
     * @param value  строковое значение, которое нужно преобразовать.
     * @param column колонка, тип данных которой нужно использовать для преобразования.
     * @return объект, который является экземпляром класса соответствующего типа данных колонки.
     * @throws IllegalArgumentException если тип данных колонки не поддерживается.
     */
    private Object convertToCorrectClass(String value, Column column) {
        if (Objects.equals(value, "null")) return null;
        if (column.getType().getTypeClass() == Long.class) {
            return Long.valueOf(value);
        } else if (column.getType().getTypeClass() == Double.class) {
            return Double.valueOf(value);
        } else if (column.getType().getTypeClass() == Boolean.class) {
            return Boolean.valueOf(value);
        } else if (column.getType().getTypeClass() == String.class) {
            return value;
        } else throw new IllegalArgumentException("Неподдерживаемый тип");
    }

    private boolean like(Object value1, Object value2) {
        String pattern = ((String) value2).replace("%", ".*");
        return ((String) value1).matches(pattern);
    }

    private boolean iLike(Object value1, Object value2) {
        String pattern = ((String) value2).replace("%", ".*").toLowerCase();
        return ((String) value1).toLowerCase().matches(pattern);
    }

    private boolean lessThan(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2) < 0;
    }

    private boolean lessThanOrEqual(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2) <= 0;
    }

    private boolean greaterThan(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2) > 0;
    }

    private boolean greaterThanOrEqual(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2) >= 0;
    }

    private boolean equal(Object value1, Object value2) {
        if (value1 == null || value2 == null) {
            return value1 == null && value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    private boolean notEqual(Object value1, Object value2) {
        return !equal(value1, value2);
    }

    public List<Map<String, Object>> getTable() {
        return this.table;
    }


    /**
     * Поиск колонки по имени в таблице.
     *
     * @param columnName имя колонки для поиска
     * @return найденную колонку с заданным именем
     * @throws IllegalArgumentException если колонка с таким именем не найдена
     */
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
