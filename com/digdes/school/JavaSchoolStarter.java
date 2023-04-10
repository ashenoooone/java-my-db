package com.digdes.school;

import com.digdes.school.DB.Column;
import com.digdes.school.DB.ColumnType;
import com.digdes.school.DB.DB;

import java.util.List;
import java.util.Map;


public class JavaSchoolStarter {
    DB db;

    public JavaSchoolStarter() {
        Column lastname = new Column("lastname", ColumnType.STRING);
        Column id = new Column("id", ColumnType.LONG);
        Column cost = new Column("cost", ColumnType.DOUBLE);
        Column age = new Column("age", ColumnType.LONG);
        Column active = new Column("active", ColumnType.BOOLEAN);
        this.db = new DB(id, lastname, age, cost, active);
    }

    public List<Map<String, Object>> execute(String request) throws Exception {
        System.out.println(request);
        return this.db.execute(request);
    }

}
