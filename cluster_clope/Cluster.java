/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cluster_clope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cluster {

    public HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
    ArrayList trans = new ArrayList();
    private int NoOfTransaction;
    private int Width;//no of distinct items
    private int Size;//occurance of distinct items(sum of support)

    public double GetSiz() {
        return Size;
    }

    public double GetWidth() {
        return Width;
    }

    public int GetN() {
        return NoOfTransaction;
    }

    void RemoveTransaction(Transaction t) {
        trans.remove(t);
        NoOfTransaction--;
        Size -= t.itemlist.size();
        try {
            for (int i = 0; i < t.itemlist.size(); i++) {
                int item = (int) t.itemlist.get(i);
                int newsupport = items.get(item) - 1;
                items.remove(item);
                if (newsupport != 0) {
                    items.put(item, newsupport);
                }
            }
            Width = 0;
            for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                Width++;
            }
        } catch (Exception e) {
        }
    }

    void AddTransaction(Transaction t) {


        NoOfTransaction++;
        Size += t.itemlist.size();
        trans.add(t);
        int newsupport;
        try {
            for (int i = 0; i < t.itemlist.size(); i++) {
                newsupport = 0;
                int item = (int) t.itemlist.get(i);
                if (items.containsKey(item)) {
                    newsupport = items.get(item);
                }
                items.remove(item);
                items.put(item, newsupport + 1);
            }
            Width = 0;
            for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                Width++;
            }
        } catch (Exception e) {
        }
    }

    Boolean ContainsItem(int item) {
        if (items.containsKey(item)) {
            return true;
        } else {
            return false;
        }

    }
}