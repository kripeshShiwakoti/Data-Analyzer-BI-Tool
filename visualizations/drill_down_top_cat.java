package visualizations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class drill_down_top_cat {

    public HashMap<String, String> retrieve_json() {
        HashMap<String, String> mp = new HashMap<String, String>();
        String json_cat = "";
        String json_data = "";
        String[] color1 = {"#4572A7", "#AA4643", "#89A54E", "#80699B", "#3D96AE", "#DB843D", "#92A8CD", "#A47D7C", "#B5CA92"};


        try {

            int col = 0;

            json_cat = "[";
            json_data = "[";
            String connectionURL = "jdbc:mysql://localhost:3306/bhatbhateni";
            Connection connection = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(connectionURL, "root", "");
            Statement sc = connection.createStatement();
            String query = "select SQL_CACHE classkey, sum(qty) as sumqty from vizdata group by classkey order by sumqty desc limit 0,3";
            ResultSet rsc = sc.executeQuery(query);
            while (rsc.next()) {
                int sum = rsc.getInt(2);
                int classkey = rsc.getInt(1);
                Statement s2 = (Statement) connection.createStatement();
                String query1 = "SELECT SQL_CACHE `classdesc` FROM `class` WHERE `classkey`=" + classkey;
                ResultSet rs1 = s2.executeQuery(query1);
                rs1.next();
                String ClassDesc = rs1.getString(1);
                if (rsc.isLast()) {
                    json_cat += "\"" + ClassDesc + "\" ]";
                } else {
                    json_cat += "\"" + ClassDesc + "\"" + ",";
                }
                json_data += "{ \"y\": " + sum + ",";
                json_data += "\"color\": " + "\"" + color1[col] + "\"" + " ,";
                json_data += "\"drilldown\" : {";
                json_data += "\"name\" : ";
                json_data += "\"" + ClassDesc + " brands\" ,";
                Statement s3 = (Statement) connection.createStatement();
                String query3 = "SELECT SQL_CACHE productcode, sum(`qty`) as sum from vizdata where classkey= " + classkey + " group by productcode order by(sum) desc limit 0,10";
                ResultSet rs3 = s3.executeQuery(query3);
                String categories = "";
                String data_cat = "";
                while (rs3.next()) {

                    String pc = rs3.getString(1);
                    int i = rs3.getInt(2);
                    Statement s4 = (Statement) connection.createStatement();
                    String query4 = "SELECT SQL_CACHE productdesc FROM  product WHERE productcode = '" + pc + "'";
                    ResultSet rs4 = s4.executeQuery(query4);
                    rs4.next();
                    String pd = rs4.getString(1).replace("\n", "");
                    if (rs3.isLast()) {
                        categories += "\"" + pd + "\"";
                        data_cat += i;
                    } else {
                        categories += "\"" + pd + "\" ,";
                        data_cat += i + ",";
                    }
                }
                json_data += "\"categories\" : [";
                json_data += categories + "],";
                json_data += "\"data\" :";
                json_data += "[" + data_cat + "],";
                json_data += "\"color\": " + "\"" + color1[col] + "\"" + "}";
                col++;
                if (col >= 9) {
                    col = 0;
                }
                if (rsc.isLast()) {
                    json_data += "}]";
                } else {
                    json_data += "},";
                }

            }
            mp.put(json_data, json_cat);
            return mp;


        } catch (Exception e) {
            return null;
        }
    }
}
