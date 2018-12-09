import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Node {
    private static int circleMax = (int)Math.pow(2, Constants.IDENTIFIER_SIZE);
    private int id;
    public FingerTable fingerTable;
    private Map<Integer, String> cache;
    private Node prevNode;

    public int getId() {
        return this.id;
    }

    public Node getPrevNode() {
        return this.prevNode;
    }

    public Map<Integer, String> getCaches() {
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

    public void setPrevNode(Node prevNode) {
        this.prevNode = prevNode;
    }

    public void migrateAll(Map<Integer, String> map) {
        // Rubrics 2, Correct keys are moved when a new node joins the DHT network. Print the keys that are migrated
        System.out.println("\tEntries migrated to Node(" + this.id + "): " + map);
        this.cache.putAll(map);
    }

    public Map<Integer, String> migrateFrom(int fromId) {
        int prevId = this.prevNode.getPrevNode().getId();
        Map<Integer, String> ret = this.cache.entrySet().stream()
                .filter( entry -> {
                    if (prevId < this.id) {
                        return entry.getKey() <= fromId;
                    }

                    if (fromId < this.id) {
                        return entry.getKey() <= fromId || entry.getKey() > prevId;
                    }

                    return entry.getKey() <= fromId && entry.getKey() > prevId;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.cache = this.cache.entrySet().stream()
                .filter( entry -> entry.getKey() > fromId)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return ret;
    }

    public Node(int id) {
        this.id = id;
        this.fingerTable = new FingerTable();
        this.cache = new TreeMap<>();
    }

    public void join(Node node) {
        // This is the first node, we gonna init DHT here.
        if (node == null) {
            IntStream.range(0, Constants.IDENTIFIER_SIZE)
                    .forEach( p -> fingerTable.add(this) );
            this.prevNode= this;
            return;
        }

        //otherwise, we join the node the current network.

        //First find the next node
        Node next = node.findNextNodeByVirtualId(id);

        //init the fingerTable of current joined Node.
        IntStream.range(0, Constants.IDENTIFIER_SIZE)
                .map(p -> (id + (int) Math.pow(2, p)) % circleMax)
                .forEach(virtualId -> {
                    Node successor = node.findNextNodeByVirtualId(virtualId);
                    fingerTable.add(successor);
                });

        //re-set the previous node
        prevNode = next.getPrevNode();
        next.setPrevNode(this);

        //update all the Node;
        this.prevNode.updateFingerTable(this);
        if (this.id == 228) System.out.println(this.fingerTable);

        //migrate the cache from next node.
        this.migrateAll(next.migrateFrom(this.id));
    }

    public void leave() {
        // Rubrics 4, Implement Node::leave() correctly.
        System.out.println("Node(" + this.id + ") leaved");
        if (this.prevNode.getId() == this.id) {
            // this is the only node remains in the network.
            return;
        }

        Node prev = this.prevNode;
        Node next = this.fingerTable.getFirstNode();

        next.setPrevNode(prev);

        // update all the fingerTable
        prev.updateFingerTable(next);

        // migrate the cache from removed node.
        next.migrateAll(this.cache);
    }

    public String insert(int key, String value) {
        // this is not the best way, since we control the remote node here.
        // the best way is just send the insert message to next node, and update once we got the correct node.
        return this.findNextNodeByVirtualId(key).putCache(key, value);
    }

    public String remove(int key) {
        return this.findNextNodeByVirtualId(key).removeCache(key);
    }

    public String find(int key) {
        System.out.print("find Key(" + key + ") From Node(" + this.id + "), Node Traverse Route: ");
        Node node = this.findNextNodeByVirtualIdAndPrintRoute(key, true);
        System.out.println(node.getId() + " ( Value in Node(" + node.getId() + "): " + node.getCache(key) + ")");
        return node.getCache(key);
    }

    public void updateFingerTable(Node source) {

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
        if (source.getId() == this.id) return;
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

        if (this.id > next.getId() && next.getId() >= virtualId) return next;

        return fingerTable.search(virtualId).findNextNodeByVirtualIdAndPrintRoute(virtualId, print);
    }
}
