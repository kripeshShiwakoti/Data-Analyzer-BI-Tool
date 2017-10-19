/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MbAnalysis {
    
    private static double minSupport = .002;//set it from outside
    private static double minConfidence = .6;
    static int noOfTransaction;
    private static ArrayList<Object> allFreqItemList = new ArrayList<Object>(); //frequent item list
    private static ArrayList<String> cList = new ArrayList<String>();
    private static ArrayList<Rule> associationRules = new ArrayList<Rule>(); //at final association rules
    private static Date fdate = new Date();
    private static Date tdate = new Date();
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static String fd = "asdasd";
    private static String td= "sadasd";
 

    public Date getFdate() {
        return fdate;
    }

    public void setFdate(Date fdate) {
        this.fdate = fdate;
    }

    public Date getTdate() {
        return tdate;
    }

    public void setTdate(Date tdate) {
        this.tdate = tdate;
    }

    public double getMinConfidence() {
        return minConfidence;
    }

    public void setMinConfidence(double minConfidence) {
        this.minConfidence = minConfidence;
    }

    public double getMinSupport() {
        return minSupport;
    }

    public void setMinSupport(double minSupport) {
        this.minSupport = minSupport;
    }

    public ArrayList<Rule> getAssociationRules() {
        return associationRules;
    }

    public void setAssociationRules(ArrayList<Rule> associationRules) {
        this.associationRules = associationRules;
    }

    public MbAnalysis() {

        getDatabaseInput();

    }

    public static void main(String[] args) {
        getDatabaseInput();
    }//end main

    public static void getDatabaseInput() {
        fd = df.format(fdate);
        td= df.format(tdate);
        allFreqItemList.clear();
        associationRules.clear();
        try {

            ArrayList transactionList = new ArrayList();
            try {

                ResultSet rs = new DatabaseConnect().stmt.executeQuery("SELECT * FROM `class` order by classkey asc");

                while (rs.next()) {

                    cList.add(rs.getString(2));
                    //System.out.println(cList.get(cList.size()));
                }
                rs = new DatabaseConnect().stmt.executeQuery("SELECT count(distinct(VCHRNO))from sales where date between '2011-07-17' and '2011-07-30'");//set date from outside
                rs.next();

                noOfTransaction = rs.getInt(1);//for support calculation

                rs = new DatabaseConnect().stmt.executeQuery("SELECT classkey, vchrno FROM `sales` where date between '2011-07-17' and '2011-07-30' order by classkey asc, vchrno asc");//y not done sorting here
                while (rs.next()) {
                    transactionList.add(new DataCapsule(rs.getInt("classkey"), rs.getInt("vchrno")));
                }  //end while

                //  Collections.sort(transactionList, new SortByItem());
                rs.close();
                pruneBySupport(noOfTransaction, transactionList);
            }//end try
            catch (Exception e) {
            }//end catch
        } //end try
        catch (Exception e) {
        }//end catch
    }

//gives first freq itemset as 1=>T1,T3,T5... prune by support
    private static void pruneBySupport(int n, ArrayList transactionList) {
        try {
            ArrayList freqItemList = new ArrayList();
            DataCapsule oldObject = (DataCapsule) transactionList.get(0);
            //System.out.println(oldObject);
            ItemTransactionList obj = new ItemTransactionList(oldObject.getItem());
            obj.addTid(oldObject.getTid());
            for (int i = 1; i <= transactionList.size() - 1; i++) {
                DataCapsule newObject = (DataCapsule) transactionList.get(i);
                if (newObject.getItem() != oldObject.getItem()) {
                    double s = (double) obj.getTidList().size() / (double) n;//prune cat if support less
                    if (s > minSupport) {
                        freqItemList.add(obj);//item = tid1, tid2
                    }
                    obj = new ItemTransactionList(newObject.getItem());
                    obj.addTid(newObject.getTid());
                }
                else {
                    obj.addTid(newObject.getTid());//for a cat add list of tid
                }
                oldObject = newObject;
                newObject = null;
                if (i == transactionList.size() - 1) {//for last cat 
                    double s = (double) obj.getTidList().size() / (double) n;
                    if (s > minSupport) {
                        freqItemList.add(obj);
                    }
                }
            }
            transactionList.clear();
            calcualteFreqItemList(freqItemList);
            //  System.out.println("----------------------");
            //  display(freqItemList);
            // System.out.println("-------------------");
        }
        catch (Exception e) {
        }
    }

    //calculate all other frequent itemset
    private static void calcualteFreqItemList(ArrayList freqItemList) {
        int k = 2;
        try {
            while (!freqItemList.isEmpty()) {

                allFreqItemList.add(freqItemList);
                ArrayList newFreqItemList = candidateItemsetGen(k, freqItemList);
                freqItemList = new ArrayList();
                if (!newFreqItemList.isEmpty()) {
                    freqItemList.addAll(newFreqItemList);
                    newFreqItemList = new ArrayList();

                }
                k++;
            }
            assoRulesGen();
        }
        catch (Exception e) {
        }
    }

    //generate candidate itemset, for k-itemset join only those (k-1)-itemset whose first (k-2) items match..preuse others
    private static ArrayList candidateItemsetGen(int k, ArrayList freqItemList) {
        ArrayList newFreqItemList = new ArrayList();
        for (int i = 0; i <= freqItemList.size() - 2; i++) {
            ItemTransactionList p = (ItemTransactionList) freqItemList.get(i);
            for (int j = i + 1; j <= freqItemList.size() - 1; j++) {
                ItemTransactionList n = (ItemTransactionList) freqItemList.get(j);
                boolean bit = true;
                //check to see if first k-2 items are same
                if (k != 2) {
                    for (int m = 0; m <= k - 3; m++) {
                        if (p.getItem().get(m) != n.getItem().get(m)) {
                            bit = false;
                            break;
                        }//end if
                    }//end for m
                }
                if (bit) {
                    if (p != null & n != null) {
                        ItemTransactionList intersection = computeIntersection(p, n);
                        double s = (double) intersection.getTidList().size() / (double) noOfTransaction;

                        if (s >= minSupport) {
                            newFreqItemList.add(intersection);
                        }
                    }
                }
            }
            //end for j
        }
        return newFreqItemList;//ab, ac, ad list= tid's
    }//end itemset_gen

    private static ItemTransactionList computeIntersection(ItemTransactionList p, ItemTransactionList n) {
        ItemTransactionList intersection = new ItemTransactionList();
        intersection.getItem().addAll(p.getItem());
        intersection.getItem().add(n.getItem().get(n.getItem().size() - 1));
        int psize = p.getTidList().size();
        int nsize = n.getTidList().size();
        //for minimum value
        Object lastobjp = p.getTidList().get(psize - 1);
        Object lastobjn = n.getTidList().get(nsize - 1);
        int lastp = Integer.parseInt(lastobjp.toString());
        int lastn = Integer.parseInt(lastobjn.toString());
        int minval = lastp;
        if (lastn < lastp) {
            ItemTransactionList dummy = new ItemTransactionList();
            dummy = p;
            p = n;
            n = dummy;
            dummy = null;
            minval = lastn;
        }
        Hashtable hash = new Hashtable();
        for (int i = 0; i < p.getTidList().size(); i++) {
            int hval = Integer.parseInt(p.getTidList().get(i).toString());
            hash.put(hval, 1);
        }
        int hval = 0;
        for (int i = 0; hval < minval; i++) {
            hval = Integer.parseInt(n.getTidList().get(i).toString());
            if (hash.containsKey(hval)) {
                intersection.getTidList().add(hval);
            }
        }
        return intersection;//finaly a,b item= tid1 , tid2
    }

    private static void assoRulesGen() {

        for (int i = 1; i <= allFreqItemList.size() - 1; i++) {
            ArrayList freqItemList = (ArrayList) allFreqItemList.get(i);
            for (int j = 0; j <= freqItemList.size() - 1; j++) {
                ItemTransactionList freqItem = (ItemTransactionList) freqItemList.get(j);
                int supportOfXny = freqItem.getTidList().size();
                Rule currule = new Rule((ArrayList) freqItem.getItem());
                bfsRuleGen(i + 1, currule, supportOfXny);
            }
        }

        for (int i = 0; i < associationRules.size(); i++) {
            for (int k = 0; k < ((Rule) associationRules.get(i)).getLeft().size(); k++) {
                Object h = (associationRules.get(i)).getLeft().get(k);
                String c = h.toString();
                int r = Integer.parseInt(c);
                (associationRules.get(i)).getLeft().set(k, cList.get(r - 1));
            }

            for (int k = 0; k < ((Rule) associationRules.get(i)).getRight().size(); k++) {
                Object h = (associationRules.get(i)).getRight().get(k);
                String c = h.toString();
                int r = Integer.parseInt(c);
                (associationRules.get(i)).getRight().set(k, cList.get(r - 1));
            }
        }
    }

    private static void bfsRuleGen(int k, Rule currule, int supportOfXny) {
        Queue queue = new LinkedList();
        queue.add((Rule) currule);
        int size;
        try {
            while (!queue.isEmpty()) {
                Rule currentRule = (Rule) queue.remove();
                size = currentRule.getLeft().size();
                if (currentRule.getLeft().size() != 1) {
                    for (int i = 0; i < size; i++) {
                        Rule newRule = currentRule.rightGen(i);
                        if (!newRule.getRight().isEmpty()) {
                            double confidence = calculateConfidance(newRule, supportOfXny);
                            if (confidence > minConfidence) {
                                newRule.setConf(confidence);
                                queue.add(newRule);
                                associationRules.add(newRule);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
        }
    }

    private static double calculateConfidance(Rule rule1, double supportOfXny) {

        Collections.sort(rule1.getLeft());
        int sizeofrule1 = rule1.getLeft().size();
        double supportOfX = 0;
        ArrayList temp = new ArrayList();
        temp.addAll((ArrayList) allFreqItemList.get(sizeofrule1 - 1));

        int sizeoflist = temp.size();

        for (int i = 0; i < sizeoflist; i++) {
            ItemTransactionList rule2 = (ItemTransactionList) temp.get(i);
            ArrayList rule3 = rule2.getItem();//arraylist of item


            if (rule1.getLeft().equals(rule3)) {
                supportOfX = rule2.getTidList().size();
                break;
            }
        }
        double confidence = supportOfXny / supportOfX;
        return confidence;
    }
}
