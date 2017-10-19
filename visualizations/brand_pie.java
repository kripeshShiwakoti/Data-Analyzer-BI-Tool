package visualizations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class brand_pie {

    private static String product_id;
    private static String date;

    public static String getDate() {
        return date;
    }

    public static void setDate(String date) {
        brand_pie.date = date;
    }

    public static String getProduct_id() {
        return product_id;
    }

    public static void setProduct_id(String product_id) {
        brand_pie.product_id = product_id;
    }

    public static String retrieve(String product_id, String date) {


       
        try {
            String json = "[{\"type\": \"pie\", \"name\": \"Brand Popularity\", \"data\": [";
            String connectionURL = "jdbc:mysql://localhost:3306/bhatbhateni";
            Connection connection = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(connectionURL, "root", "");
            Statement sc = (Statement) connection.createStatement();
            String query1 = "SELECT SQL_CACHE productcode, sum(`qty`) as sum from vizdata where classkey= " + product_id + " and YEAR(DATE) = "+date+ " group by productcode order by(sum) desc limit 0,20";
            ResultSet rs1 = sc.executeQuery(query1);
            while (rs1.next()) {
                String pc = rs1.getString(1);
                int i = rs1.getInt(2);
                Statement sc2 = (Statement) connection.createStatement();
                String query2 = "SELECT SQL_CACHE productdesc FROM  product WHERE productcode = '" + pc + "'";
                ResultSet rs2 = sc2.executeQuery(query2);
                rs2.next();
                String pd = rs2.getString(1).replace("\n", "<br/>");

                if (rs1.isLast()) {
                    json += "[\"" + pd + "\", " + i + " ]";
                } else {
                    json += "[\"" + pd + "\", " + i + " ],";

                }
            }
            json += "]}]";
        connection.close();
        return json;
    }
    
    catch (Exception e) {
        return null;
    }
}
}
