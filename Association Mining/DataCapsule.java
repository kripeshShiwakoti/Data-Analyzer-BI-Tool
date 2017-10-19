/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

public class DataCapsule {

    private int item;
    private int tid;

    DataCapsule() {
    }

    DataCapsule(int item, int tid) {
        this.item = item;
        this.tid = tid;
    }

    int getItem() {
        return item;
    }

    int getTid() {
        return tid;
    }
}
