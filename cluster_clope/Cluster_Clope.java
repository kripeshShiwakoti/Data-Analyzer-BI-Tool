package cluster_clope;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Cluster_Clope {

    public static ArrayList TransactionList = new ArrayList();
    public static ArrayList clist = new ArrayList();
    static ArrayList<Cluster> ClusterList = new ArrayList<Cluster>();
    static double r = .9;

    public static void main(String[] args) {
        GetDatabaseInput();
        String json = retrieve();
    }

    public static String tututu() {
        GetDatabaseInput();

        String json = retrieve();

        return json;

    }
    public static ArrayList displaylist = new ArrayList();

    public static String retrieve() {

        int val = 1;
        String json = "{\"id\":" + "\"" + val + "\"," + "\"name\":" + "\"" + "cluster" + "\"," + "\"children\":[";;
        val++;
        for (int i = 0; i < ClusterList.size(); i++) {
            if (i != 0) {
                json += ",";
            }
            json = json + "{\"id\":" + "\"" + val + "\"," + "\"name\":" + "\"" + "newcluster" + "\"," + "\"children\":[";
            val++;
            Cluster curcluster = ClusterList.get(i);
            HashMap map = curcluster.items;
            Map<Integer, Integer> dummap = sortMapByValues(map);


            //  for (int key : dummap.keySet()) {
            //System.out.println("key/value: " + key + "/" + dummap.get(key));
            //  }



            int size = dummap.size();
            int reqno = (int) Math.round(.02 * size);
            ArrayList<Integer> intKeys = new ArrayList<Integer>(dummap.keySet());


            for (int j = 0; j < reqno; j++) {
                int key = intKeys.get(j);
                String k = clist.get(key).toString();
                if (j != 0) {
                    json += ",";
                }
                json = json + "{\"id\":" + "\"" + val + "\"," + "\"name\":" + "\"" + k + "\"," + "\"children\":[]}";
                val++;

                System.out.println("cluster " + i + " " + k + "  " + intKeys.get(j));
            }
            json += "]}";
        }
        json += "]}";

        System.out.println(json);
        return json;

    }

    private static void GetDatabaseInput() {
        String dbUrl = "jdbc:mysql://localhost:3306/bhatbhateni";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = (Connection) DriverManager.getConnection(dbUrl, "root", "");
            Statement stmt = (Statement) con.createStatement();
   //  rs = stmt.executeQuery("SELECT classkey, vchrno FROM `cluster` order by vchrno asc");
           ResultSet rs = stmt.executeQuery("SELECT classkey, vchrno FROM `sales` where date between '2011-9-11' and '2011-10-11'  order by vchrno asc");


            rs.next();
            int item = rs.getInt(1);//item_id
            int newtid = rs.getInt(2);

            Transaction t = new Transaction(newtid);
            t.SetList(item);
            int oldtid = newtid;
            while (rs.next()) {
                item = rs.getInt(1);// item_id
                newtid = rs.getInt(2);// transaction id
                if (newtid == oldtid) {
                    t.SetList(item);
                } else {
                    t.SortList();
                    TransactionList.add(t);
                    t = new Transaction(newtid);
                    t.SetList(item);
                }
                oldtid = newtid;
            }
            t.SortList();
            TransactionList.add(t);


            rs = stmt.executeQuery("SELECT classdesc FROM `class` order by classkey asc");

            while (rs.next()) {
                clist.add(rs.getString(1));

            }
            // System.out.println(clist.toString());

            allocation();
        } catch (Exception ex) {
            //  Logger.getLogger(Clustering.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void display(Cluster c1) {
        System.out.println("****Next Cluster****");
        for (int i = 0; i < c1.trans.size(); i++) {
            int tid = ((Transaction) c1.trans.get(i)).GetTid();
            System.out.print(tid + " ");
        }
        System.out.println();
    }

    private static double DeltaAdd(Cluster C, Transaction t, double r) {
        double S_new = C.GetSiz() + t.itemlist.size();
        double W_new = C.GetWidth();
        for (int i = 0; i < t.itemlist.size(); i++) {
            if (!C.ContainsItem((int) t.itemlist.get(i))) {
                W_new++;
            }
        }
        double diff1 = S_new * (C.GetN() + 1) / (Math.pow(W_new, r));
        double diff2 = (C.GetSiz() * C.GetN()) / (Math.pow(C.GetWidth(), r));
        return (diff1 - diff2);
    }

    private static double CreateNew(Transaction t, double r) {
        double a = t.itemlist.size();
        return (a / Math.pow(a, r));
    }

    private static void allocation() {

        try {
            for (int k = 0; k < TransactionList.size(); k++) {
                Transaction t = new Transaction();
                t = (Transaction) TransactionList.get(k);
                double ProfitNew = CreateNew(t, r);

                if (ClusterList.isEmpty()) {
                    Cluster newcluster = new Cluster();
                    newcluster.AddTransaction(t);
                    ClusterList.add(newcluster);
                    t.AssignCluster(newcluster);
                    continue;
                }
                //calculate cost of adding to old cluster
                double[] ProfitOld = new double[1000];
                int max = 0;

                for (int i = 0; i < ClusterList.size(); i++) {
                    Cluster c = new Cluster();
                    c = (Cluster) ClusterList.get(i);
                    ProfitOld[i] = DeltaAdd(c, t, r);
                    if (ProfitOld[i] > ProfitOld[max]) {
                        max = i;
                    }
                }
                if (ProfitOld[max] >= ProfitNew) {//profit of old cluster is high so add to old cluster
                    Cluster newcluster = new Cluster();
                    newcluster = (Cluster) ClusterList.remove(max);
                    newcluster.AddTransaction(t);
                    ClusterList.add(newcluster);
                    t.AssignCluster(newcluster);

                } else {
                    Cluster newcluster = new Cluster();
                    newcluster.AddTransaction(t);
                    ClusterList.add(newcluster);
                    t.AssignCluster(newcluster);
                }
            }
        } catch (Exception e) {
            // System.out.println("the exception" + e);
        }
        refinement();

    }

    private static void refinement() {
        try {
            int accuracy = 1;
            boolean change = true;
            while (change && accuracy < 500) {
                System.out.println("change " + accuracy);
                change = false;
                for (int k = 0; k < TransactionList.size(); k++) {
                    Transaction t = new Transaction();
                    Cluster c = new Cluster();

                    t = (Transaction) TransactionList.get(k);
                    c = t.cluster;
                    t.RemoveCluster();


                    ClusterList.remove(c);
                    c.RemoveTransaction(t);
                    if ((int) c.trans.size() > 0) {
                        ClusterList.add(c);
                    }
                    double[] ProfitOld = new double[1000];
                    int max = 0;

                    for (int i = 0; i < ClusterList.size(); i++) {

                        Cluster c1 = new Cluster();
                        c1 = (Cluster) ClusterList.get(i);
                        ProfitOld[i] = DeltaAdd(c1, t, r);
                        if (ProfitOld[i] > ProfitOld[max]) {
                            max = i;
                        }

                    }
                    Cluster newcluster = new Cluster();
                    newcluster = (Cluster) ClusterList.remove(max);
                    newcluster.AddTransaction(t);
                    ClusterList.add(newcluster);
                    t.AssignCluster(newcluster);
                    if (!newcluster.equals(c)) {
                        change = true;
                    }
                }
                accuracy++;
            }

            //you get final output from here
            //clusterlist has all the clusters
            //cluster class has ArrayList trans which is the required cluster of transaction

            //   createdisplay();

            //   System.out.println("REFINEMENT");
            //  for (int i = 0; i < ClusterList.size(); i++) {
            //    Cluster c = new Cluster();
            //  c = ClusterList.get(i);
            //  display(c);
            //  }
        } catch (Exception e) {
            // System.out.println("the exception" + e);
        }

    }

    public static <K, V extends Comparable<V>> Map<K, V> sortMapByValues(final HashMap<K, V> map) {
        Comparator<K> valueComparator = new Comparator<K>() {

            public int compare(K k1, K k2) {
                final V v1 = map.get(k1);
                final V v2 = map.get(k2);

                /*
                 * Not sure how to handle nulls ...
                 */
                if (v1 == null) {
                    return (v2 == null) ? 0 : 1;
                }

                int compare = v2.compareTo(v1);
                if (compare != 0) {
                    return compare;
                } else {
                    Integer h1 = k1.hashCode();
                    Integer h2 = k2.hashCode();
                    return h2.compareTo(h1);
                }
            }
        };
        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }
}