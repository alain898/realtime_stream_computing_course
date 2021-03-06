package com.alain898.course.realtimestreaming.course21.flinkcdc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class TestData {
    public static void main(String[] args) throws Exception {
        Statement stmt;
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "123456");
        stmt = conn.createStatement();

        for (int i = 100; i < 20000; i++) {
            int id = i;
            String name = String.format("name%d", i);
            int counts = i * 10;
            String description = String.format("description%d", i);
            String sql = String.format("insert into db001.table001 values(%d, '%s', %d, '%s')",
                    id, name, counts, description);
            stmt.executeUpdate(sql);
            Thread.sleep(1000);
            System.out.printf("insert id[%s]\n", id);
        }

        stmt.close();
    }
}
