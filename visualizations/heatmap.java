package visualizations;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class heatmap {

    public static String retrieve() {


        String json = "";

        json += "{\"children\": [";


        try {

            String connectionURL = "jdbc:mysql://localhost:3306/bhatbhateni";
            Connection connection = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(connectionURL, "root", "");

            Statement s = (Statement) connection.createStatement();

            String query2 = "select SQL_CACHE classkey, sum(qty) as sumqty from vizdata group by classkey order by sumqty desc limit 0,10";
            ResultSet rs = s.executeQuery(query2);
            int r = 255;
            int g = 0;
            int b = 0;
            while (rs.next()) {
                int r1 = 255;
                int g1 = 0;
                int b1 = 0;

                Color col = new Color(r, g, b);
                String colParent = (Integer.toHexString((col.getRGB() & 0xffffff) | 0x1000000).substring(1));
                json += "{ \"children\": [";
                int sum = rs.getInt(2);
                int classkey = rs.getInt(1);

                Statement s2 = (Statement) connection.createStatement();
                String query = "SELECT SQL_CACHE `classdesc` FROM `class` WHERE `classkey`=" + classkey;
                ResultSet rs1 = s2.executeQuery(query);

                rs1.next();
                String ClassDesc = rs1.getString(1);
                Statement s3 = (Statement) connection.createStatement();
                String query3 = "SELECT SQL_CACHE productcode, sum(`qty`) as sum from vizdata where classkey= " + classkey + " group by productcode order by(sum) desc limit 0,10";
                ResultSet rs3 = s3.executeQuery(query3);
                while (rs3.next()) {
                    Color col1 = new Color(r1, g1, b1);
                    String colChild = (Integer.toHexString((col1.getRGB() & 0xffffff) | 0x1000000).substring(1));
                    String pc = rs3.getString(1);
                    int i = rs3.getInt(2);
                    Statement s4 = (Statement) connection.createStatement();
                    String query4 = "SELECT SQL_CACHE productdesc FROM  product WHERE productcode = '" + pc + "'";
                    ResultSet rs4 = s4.executeQuery(query4);
                    rs4.next();
                    String pd = rs4.getString(1).replace("\n", "");
                    r1 -= 20;
                    g1 += 10;
                    b1 += 20;
                    if (r1 < 2) {
                        r1 = 2;
                    }
                    if (g1 > 255) {
                        g1 = 255;
                    }
                    if (b1 > 255) {
                        b1 = 255;
                    }

                    json += "{";
                    json += "\"children\":[],";
                    json += "\"data\":";
                    json += "{";
                    json += "\"sales\" :";
                    json += "\"" + i + "\",";
                    json += "\"$color\"";
                    json += ":";
                    json += "\"" + colChild + "\",";
                    json += "\"$area\" :";
                    json += i;
                    json += "},";
                    json += "\"id\":\"" + pc + "\",";
                    json += "\"name\":\"" + pd + "\"";
                    if (rs3.isLast()) {
                        json += "}],";
                    } else {
                        json += "},";
                    }
                }
                json += "\"data\": {";
                json += "\"count\":" + sum;
                json += ", \"$area\":" + sum;
                json += ",\"$color\":";
                json += "\"" + colParent + "\"";
                json += "},";
                json += "\"id\":" + "\"" + ClassDesc + "\"";
                json += ", \"name\":" + "\"" + ClassDesc + "\"";
                if (rs.isLast()) {
                    json += "}],";
                } else {
                    json += "},";
                }
                r -= 20;
                g += 10;
                b += 20;
                if (r < 2) {
                    r = 2;
                }
                if (g > 255) {
                    g = 255;
                }
                if (b > 255) {
                    b = 255;
                }

            }
            connection.close();
            json += "\"data\":{\"color:\"#ff0000\"}, \"id\": \"root\", \"name\": \"Topsales\"}";
            return json;
            // out.print(json);
        } catch (Exception e) {
            return null;
        }



    }
}
