package com.digdes.school;


public class Main {
    public static void main(String[] args) throws Exception {
//        INSERT VALUES 'lastname' = 'Федоров', 'id'=3, 'age'=40, 'active'=true
        JavaSchoolStarter starter = new JavaSchoolStarter();
        System.out.println(starter.execute("INSERT VALUES 'id'=1, 'age'=40, 'active'=true"));
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Федоров', 'id'=0, 'age'=50, 'active'=true"));
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Гонтарь', 'id'=2, 'age'=10, 'active'=true"));
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Васил', 'id'=3, 'age'=99, 'active'=true"));
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Бондарчук', 'id'=4, 'age'=100, 'active'=false"));
        System.out.println(starter.execute("INSERT VALUES 'lastname' = 'Маначук', 'id'=5, 'age'=33, 'active'=false"));
        System.out.println(starter.execute("select where 'cost' != null"));
    }
}
