import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {

        // save all number from 0 ~ 255 as the candidates of random key;
        List<Integer> candidates = IntStream.range(0, 256).boxed().collect(Collectors.toList());

        List<Node> nodes = new ArrayList<>();
        Node joined = null;
        Set<Integer> ids = new HashSet<>();
        System.out.println(" ------------------ Rubrics 1, Build the finger table correctly and print the finger table in the screen when a new node joins. -------------");
        System.out.println(" ------------------ And Rubrics 2, Correct keys are moved when a new node joins the DHT network. Print the keys that are migrated -------------");
        Random r = new Random();
        for (int i = 0; i < 8; i ++) { // randomly create 8 node
            int nodeId = r.nextInt(256); // produce 0 ~ 255 random id
            if (ids.contains(nodeId)) {
                i --;
                continue;
            }
            ids.add(nodeId);

            System.out.println("\nNew Node(" + nodeId + ") joined, all the finger tables: ");
            Node node = new Node(nodeId);
            node.join(joined);
            joined = node;
            nodes.add(node);

            // print all the finger Table
            Collections.sort(nodes, Comparator.comparingInt(Node::getId));
            System.out.println("1. finger tables after joining a new node");
            for (Node n: nodes) {
                System.out.println("\tVirtual:" + Arrays.toString(IntStream.range(0, Constants.IDENTIFIER_SIZE).map( j -> (n.getId() + (int) Math.pow(2, j)) % (int) Math.pow(2, Constants.IDENTIFIER_SIZE)).toArray()));
                System.out.println("\tNode(" + n.getId() + "): " + n.fingerTable);
            }


            //insert ten random key from random Node to the networks;
            System.out.println("2. insert 10 extinct random keys from random node");
            for (int j = 0; j < 10; j ++) {
                int randomKey = r.nextInt(candidates.size());
                int key = candidates.remove(randomKey);
                Node addKeyFromNode = nodes.get(r.nextInt(nodes.size()));
                System.out.println("\tInsert Key(" + key + ") from Node(" + addKeyFromNode.getId() + ")");
                addKeyFromNode.insert(key, "" + key);
            }


            //Local caches after inserting keys.

            System.out.println("3. all caches in all node after inserting keys");
            for (Node n: nodes) {
                System.out.println("\tNode(" + n.getId() + "): " + n.getCaches());
            }
        }

        System.out.println("\n\n ------------------ Rubrics 3, Correctly lookup keys. Print the sequences of nodes get involved in this -------------");

        System.out.println("Randomly look up 10 keys between 0 ~ 255 from random Node");
        for (int i = 0; i < 10; i ++) {
            Node fromNode = nodes.get(r.nextInt(nodes.size()));
            int lookupKey = r.nextInt(256);
            fromNode.find(lookupKey);
        }

        System.out.println("\n\n ----------------- Rubrics 4,  Implement Node::leave() correctly. -------------------------------------------");

        System.out.println("Randomly drop the nodes from the network");

        for (int i = 0; i < 8; i ++) {
            int idx = r.nextInt(nodes.size());
            nodes.remove(idx).leave();
        }
    }
}
