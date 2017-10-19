/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import java.sql.*;

public class DatabaseConnect {

    String dbUrl = "jdbc:mysql://localhost:3306/bhatbhateni";
    Connection con;
    public Statement stmt;

    public DatabaseConnect() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(dbUrl, "root", "");

            stmt = con.createStatement();

        }
        catch (Exception e) {
        }


    }

    public void close() {
        try {
            con.close();
        }
        catch (Exception e) {
        }
    }
}
