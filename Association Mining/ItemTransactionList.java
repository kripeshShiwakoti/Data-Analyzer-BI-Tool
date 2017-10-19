/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import java.util.ArrayList;

//item transaction list eg [a] (t1,t2,t3) && [a,b](t1,t2,t4,t5)
public class ItemTransactionList {

    private ArrayList item = new ArrayList();
    private ArrayList tidList = new ArrayList();
    private int support = 0;

    ItemTransactionList() {
    }

    ItemTransactionList(int item) {
        this.item.add(item);
    }

    int getSupport() {
        return support;
    }

    ArrayList getItem() {
        return item;
    }

    ArrayList getTidList() {
        return tidList;
    }

    void addTid(int tid) {
        tidList.add(tid);
        support++;
    }
}
