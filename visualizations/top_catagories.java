package visualizations;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
public class top_catagories {
    
    public static String retrieve(){
       String json="";            
             json+= "{\"children\": ["; 

        try {
                String connectionURL = "jdbc:mysql://localhost:3306/bhatbhateni";
                Connection connection = null;
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(connectionURL, "root", "");

                Statement s = (Statement) connection.createStatement();

                String query2 = "SELECT SUM(`qty`) AS sum,  `productcode` FROM vizdata GROUP BY  `productcode` ORDER BY sum DESC LIMIT 0 , 30";
                ResultSet rs = s.executeQuery(query2);
                 int r = 255;
                 int g = 0;
                 int b = 0;
                while (rs.next()) {
                    Color col = new Color(r, g, b);
                    String colParent = (Integer.toHexString((col.getRGB() & 0xffffff) | 0x1000000).substring(1));
                    int sum=rs.getInt(1);
                    String classkey=rs.getString(2);

                    Statement s2 = (Statement) connection.createStatement();
                    String query = "SELECT `productdesc` FROM `product` WHERE `productcode` = '" + classkey + "'";
                    ResultSet rs1 = s2.executeQuery(query);

                    rs1.next();
                    String ClassDesc=rs1.getString(1).replace("\n", "");
                    json+="{";
                    json+="\"children\":[],";
                    json+="\"data\":";
                    json+="{";
                    json+="\"sales\" :";
                    json+="\""+sum+"\",";
                    json += "\"$color\"";
                    json += ":";
                    json += "\"" + colParent + "\",";
                    json+="\"$area\" :";
                    json+= sum;
                    json+="},";
                    json+="\"id\":\""+ClassDesc+"\",";
                    json+="\"name\":\""+ClassDesc+"\"";
                    if (rs.isLast()) {
                    json+="}],";
                }
                    else {
                    json+="},";
                }
                     r -= 5;
                  //  g += 10;
                    b += 5;
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
                json+="\"data\":{}, \"id\": \"root\", \"name\": \"Topsales\"}";
                return json;
               // out.print(json);
            } catch (Exception e) {
                return null;
            }
    
        }
}
