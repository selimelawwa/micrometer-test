import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class test {

    public static void main(String[] args) {
        PriorityQueue<Node> maxHeap = new PriorityQueue<Node>(Comparator.comparing(Node::getVal));
        

    }
    
    static class Node {
        int i;
        int j;
        int val;
        
        Node (int i, int j, int val) {
            this.i = i;
            this.j=j;
            this.val=val;
        }
        
        int getVal() {
            return val;
        }
    }

}
