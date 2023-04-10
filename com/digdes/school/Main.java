package com.digdes.school;


public class Main {
    public static void main(String[] args) throws Exception {
//        INSERT VALUES 'lastname' = 'Федоров', 'id'=3, 'age'=40, 'active'=true
        JavaSchoolStarter starter = new JavaSchoolStarter();
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Федоров', 'id'=3, 'age'=40, 'active'=true"));
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Петров', 'id'=3, 'age'=40, 'active'=true"));
        System.out.println(starter.execute("UPDATE VALUES ‘active’=false, ‘cost’=10.1 WHERE 'age' > 39"));
    }
}
