import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FingerTable {
    private List<Node> successors;

    public FingerTable() {
        this.successors = new ArrayList<>();
    }

    public Node getFirstNode() {
        return this.successors.get(0);
    }

    public void add(Node node) {
        this.successors.add(node);
    }

    public void set(int idx, Node node) {
        this.successors.set(idx, node);
    }

    public Node search(int virtualId) {
        for (int i = 0; i < successors.size() - 1; i ++) {
            Node cur = successors.get(i);
            Node next = successors.get(i + 1);

            if (cur.getId() < virtualId && (next.getId() >= virtualId || next.getId() < cur.getId())) return cur;

            if (cur.getId() > next.getId() && virtualId < next.getId()) return cur;
        }
        return successors.get(successors.size() - 1);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.successors.stream().map(Node::getId).toArray());
    }
}
