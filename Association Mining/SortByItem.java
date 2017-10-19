/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import java.util.Comparator;

public class SortByItem  implements Comparator<DataCapsule> {
    
    public int compare(DataCapsule o1, DataCapsule o2) {
        if (o1.getItem() >o2.getItem()) {
            return 1;
        } else {
            return 0;
        }
    }
}
    

