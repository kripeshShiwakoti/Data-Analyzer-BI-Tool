/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import java.util.ArrayList;

public class Rule {

    private double conf;
    private ArrayList left = new ArrayList();
    private ArrayList right = new ArrayList();

    Rule() {
    }

    Rule(ArrayList item1) {
        left.addAll(item1);
    }

    Rule(ArrayList item1, ArrayList item2) {
        left.addAll(item1);
        right.addAll(item2);
    }

    public double getConf() {
        return conf;
    }

    public void setConf(double conf) {
        this.conf = conf;
    }

    public ArrayList getLeft() {
        return left;
    }

    public void setLeft(ArrayList left) {
        this.left = left;
    }

    public ArrayList getRight() {
        return right;
    }

    public void setRight(ArrayList right) {
        this.right = right;
    }

    public Rule rightGen(int j) {
        Rule newRule = new Rule();
        int val;
        try {
            val = Integer.parseInt(this.left.get(j).toString());
            newRule.right.addAll(this.right);
            newRule.right.add(val);
            newRule.left.addAll(this.left);
            newRule.left.remove(j);
        } catch (Exception e) {
        }
        return newRule;
    }
}
