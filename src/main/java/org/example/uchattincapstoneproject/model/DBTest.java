package org.example.uchattincapstoneproject.model;

public class DBTest {
    public static void main(String[] args) {
        System.out.println("starting sql test");
        DB db = DB.getInstance();
        db.testSQLQuery("anniep8");
        System.out.println("finished");
    }


}
