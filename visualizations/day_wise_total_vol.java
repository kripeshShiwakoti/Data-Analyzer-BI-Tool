package visualizations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class day_wise_total_vol {

    public static String ret_String(String s) {
        return s;
    }

    public HashMap<String, String> retrieve_json(String fyr, String fmth, String fdy, String tyr, String tmth, String tdy) {
        HashMap<String, String> mp = new HashMap<String, String>();
        String desc = "Sales Volume by units (pc)";
        String data = "";
        String json = "[{";
        String json1 = "[";
        json += "\"name\":\"" + desc + "\",";
        json+= "\"data\":[" ;
        String fdate = fyr + "-" + fmth + "-" + fdy;
        String tdate = tyr + "-" + tmth + "-" + tdy;
        String date1 = "";
        try {

            String connectionURL = "jdbc:mysql://localhost:3306/bhatbhateni";
            Connection connection = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = (Connection) DriverManager.getConnection(connectionURL, "root", "");
            Statement s1 = (Statement) connection.createStatement();
            String query1 = " Select  SQL_CACHE sum(QTY),date from sales WHERE `date` Between '" + fdate + "' AND '" + tdate + "' group by date";
            ResultSet rs1 = s1.executeQuery(query1);
            while (rs1.next()) {
                date1 = rs1.getString(2);
                int count = rs1.getInt(1);
                if (rs1.isLast()) {
                    json1 += "\"" + date1 + "\"";
                    json += count;
                } else {
                    json1 += "\"" + date1 + "\"" + ",";
                    json += count + ",";
                }
            }
            connection.close();
            json += "]";
            json += "}]";
            json1 += "]";
            mp.put(json, json1);

            return mp;
        } catch (Exception e) {
            return null;
        }
    }
}