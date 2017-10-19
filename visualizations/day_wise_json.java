package visualizations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class day_wise_json {

    private static String name;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        day_wise_json.name = name;
    }

    public static String getYr() {
        return yr;
    }

    public static void setYr(String yr) {
        day_wise_json.yr = yr;
    }
    private static String yr;

    public static String retrieve(String name,String yr) {

        String desc = "";
        String data = "";
        String json = "[{";
 
        try {

            String connectionURL = "jdbc:mysql://localhost:3306/bhatbhateni";
            Connection connection = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(connectionURL, "root", "");

            Statement s = (Statement) connection.createStatement();

            String query2 = "select  SQL_CACHE classdesc from class where classkey = " + name;
            ResultSet rs = s.executeQuery(query2);
            while (rs.next()) {
                desc = rs.getString(1);
            }
            Statement s2 = (Statement) connection.createStatement();
            String query = "SELECT  SQL_CACHE * FROM sales WHERE classkey = " + name + " AND YEAR(DATE) = " + yr;
            ResultSet rs1 = s2.executeQuery(query);
            float sa[] = new float[12];
            while (rs1.next()) {
                Date database = rs1.getDate(3);
                float count = rs1.getFloat(4);
                int daydate = database.getDay();
                sa[daydate] += count;
            }
            connection.close();
            for (int j = 0; j <= 6; j++) {
                if (j == 6) {
                    data = data.concat(String.valueOf(sa[j]));
                } else {
                    data = data.concat(String.valueOf(sa[j])).concat(",");
                }

            }

            json += "\"name\":\"" + desc + "\",";

            json += "\"data\":[" + data + "]";
            json += "}]";
            return json;
        } catch (Exception e) {
            return null;
        }


    }
}