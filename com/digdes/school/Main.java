package com.digdes.school;


public class Main {
    public static void main(String[] args) throws Exception {
//        INSERT VALUES 'lastname' = 'Федоров', 'id'=3, 'age'=40, 'active'=true
        JavaSchoolStarter starter = new JavaSchoolStarter();
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Федоров', 'id'=1, 'age'=40, 'active'=true"));
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Федоров', 'id'=3, 'age'=40, 'active'=true"));
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Гонтарь', 'id'=3, 'age'=30, 'active'=true"));
//        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Петров', 'id'=3, 'age'=10, 'active'=true"));
//        System.out.println(starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 WHERE 'age'>39"));
//        System.out.println(starter.execute("UPDATE VALUES 'active'=false, 'cost'=1000 where 'id'=3"));
        System.out.println(starter.execute("select where 'lastname' like '%Ф%'"));
        System.out.println(starter.execute("delete where 'id' = 3"));
    }
}
