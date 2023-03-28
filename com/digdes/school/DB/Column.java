package com.digdes.school.DB;

public class Column {
    private final String name;
    private final ColumnType type;

    public Column(String name, ColumnType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ColumnType getType() {
        return type;
    }

    public boolean isValidValue(Object value) {
        // Если переданное значение null, то всегда считается валидным
        if (value == null) {
            return true;
        }
        System.out.println(value);
        System.out.println(type.getTypeClass());
        System.out.println(value.getClass());
        // Проверяем, соответствует ли тип переданного значения типу данных столбца
        return type.getTypeClass().isInstance(value);
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
