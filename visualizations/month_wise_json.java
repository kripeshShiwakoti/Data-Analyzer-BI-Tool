package visualizations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class month_wise_json {

    private static String name;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        month_wise_json.name = name;
    }

    public static String getYr() {
        return yr;
    }

    public static void setYr(String yr) {
        month_wise_json.yr = yr;
    }
    private static String yr;

    public static String retrieve(String name, String yr) {


        String desc = "";
        String data = "";
        String json1 = "[{";
        String gen_json;
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
                int month = database.getMonth();
                sa[month] += count;
            }
            rs1.close();
            rs.close();
            connection.close();
            for (int j = 0; j <= 11; j++) {
                if (j == 11) {
                    data = data.concat(String.valueOf(sa[j]));
                } else {
                    data = data.concat(String.valueOf(sa[j])).concat(",");
                }

            }
            String json2 = "\"name\":\"" + desc + "\",";
            String json3 = "\"data\":[" + data + "]";
            String json4 = "}]";
            gen_json = json1 + json2 + json3 + json4;
            return gen_json;
        } catch (Exception e) {
            return null;
        }
    }
}