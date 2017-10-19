package visualizations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class dataslide {
    

    
    public String retrieve(String ck)
    {
    String json="";
    String gen_string="";
    try {

        String connectionURL = "jdbc:mysql://localhost:3306/bhatbhateni";
        Connection connection = null;
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = (Connection) DriverManager.getConnection(connectionURL, "root", "");
        Statement s1 = (Statement) connection.createStatement();
        String query1 = " SELECT  SQL_CACHE UNIX_TIMESTAMP(  `date` ) as unixtime, sum(qty)  FROM vizdata where classkey=" + ck + " group by unixtime";
        ResultSet rs1 = s1.executeQuery(query1);
        while (rs1.next()) {
            double ut = rs1.getDouble(1);
                ut *= 1000;
            String qty = rs1.getString(2);
            if (rs1.isLast()) {
                json += "[" + ut + "," + qty + "]";
            } else {
                json += "[" + ut + "," + qty + "],";
            }
        }
        connection.close();
       gen_string = "["+ json + "]";
       return gen_string;
         }  catch (Exception e) {
            return null;
        }
    
    }
}
