import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Node {
    private int id;
    public FingerTable fingerTable;
    private Map<Integer, String> cache;
    private Node prevNode;

    private static int circleMax = (int)Math.pow(2, Constants.IDENTIFIER_SIZE);

    public int getId() {
        return this.id;
    }

    public Node getPrevNode() {
        return this.prevNode;
    }

    public Map<Integer, String> getCache() {
        return this.cache;
    }

    public String removeCache(int key) {
        return this.cache.remove(key);
    }

    public String putCache(int key, String value) {
        return this.cache.put(key, value);
    }

    public String getCache(int key) {
        return this.cache.get(key);
    }

    public Map<Integer, String> migrateFrom(int fromId) {
        Map<Integer, String> ret = this.cache.entrySet().stream()
                .filter( entry -> entry.getKey() <= fromId)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // Rubrics 2, Correct keys are moved when a new node joins the DHT network. Print the keys that are migrated
        System.out.println("Entries need to be migrated from Node(" + this.id + ") to Node(" + fromId + "): " + ret);
        this.cache = this.cache.entrySet().stream()
                .filter( entry -> entry.getKey() > fromId)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return ret;
    }

    public void setPrevNode(Node prevNode) {
        this.prevNode = prevNode;
    }

    public Node(int id) {
        this.id = id;
        this.fingerTable = new FingerTable();
        this.cache = new HashMap<>();
    }

    public void join(Node node) {
        // This is the first node, we gonna init DHT here.
        if (node == null) {
            IntStream.range(0, Constants.IDENTIFIER_SIZE)
                    .forEach( p -> fingerTable.add(this) );
            this.prevNode= this;
            // Rubrics 1, Build the finger table correctly and print the finger table in the screen when a new node joins.
            System.out.println("New Node(" + this.id + ") Joined, FingerTable: " + this.fingerTable);
            return;
        }

        //otherwise, we join the node the current network.

        //First get the next node
        Node next = node.findNextNodeByVirtualId(id);

        IntStream.range(0, Constants.IDENTIFIER_SIZE)
                .map(p -> (id + (int) Math.pow(2, p)) % circleMax)
                .forEach(virtualId -> {
                    Node successor = node.findNextNodeByVirtualId(virtualId);
                    fingerTable.add(successor.equals(next) && this.id > virtualId ? this : successor);
                });

        //re-set the previous node
        prevNode = next.getPrevNode();
        next.setPrevNode(this);

        //update all the Node;
        this.prevNode.updateFingerTable(this);

        System.out.println("New Node(" + this.id + ") Joined, FingerTable: " + this.fingerTable);
        //migrate the cache from next node.
        this.cache.putAll(next.migrateFrom(this.id));

        // Rubrics 1, Build the finger table correctly and print the finger table in the screen when a new node joins.
    }

    public String insert(int key, String value) {
        return this.findNextNodeByVirtualId(key).putCache(key, value);
    }

    public String remove(int key) {
        return this.findNextNodeByVirtualId(key).removeCache(key);
    }

    public String get(int key) {
        System.out.print("Get Key(" + key + "), Node Traverse Route: ");
        Node node = this.findNextNodeByVirtualIdAndPrintRoute(key, true);
        System.out.println(node.getId() + " ( Value: " + node.getCache(key) + ")");
        return node.getCache(key);
    }

    public void updateFingerTable(Node source) {

        if (source.getId() == this.id) return;

        IntStream.range(0, Constants.IDENTIFIER_SIZE)
                .forEach( p -> {
                    int virtualId = (this.id + (int) Math.pow(2, p)) % circleMax;
                    int prevId = source.getPrevNode().getId();
                    int curId = source.getId();

                    if (virtualId > prevId && virtualId <= curId) {
                        this.fingerTable.set(p, source);
                    }

                    if (prevId > curId && (virtualId <= curId || virtualId > prevId)) {
                        this.fingerTable.set(p, source);
                    }
                });
        this.prevNode.updateFingerTable(source);
    }

    public Node findNextNodeByVirtualId(int virtualId) {
        return findNextNodeByVirtualIdAndPrintRoute(virtualId, false);
    }

    public Node findNextNodeByVirtualIdAndPrintRoute(int virtualId, boolean print) {
        if (print) System.out.print(this.id + " --> ");
        Node next = this.fingerTable.getFirstNode();

        if (next.equals(this)) return this;

        if (this.id < virtualId && (next.getId() >= virtualId
                || (next.getId() <= virtualId && next.getId() < this.id))) {
            return next;
        }

        if (this.id > next.getId() && next.getId() > virtualId) return next;

        return fingerTable.search(virtualId).findNextNodeByVirtualIdAndPrintRoute(virtualId, print);
    }
}
