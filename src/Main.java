import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        Node[] arr = new Node[]{
                new Node(10),
                new Node(36),
                new Node(120),
                new Node(190),
                new Node(70),
                new Node(52),
                new Node(5)
        };

        System.out.println(" ------------------ Join Node -------------");
        Node a = arr[0];
        a.join(null);
        Stream.of(arr).
                filter(n -> n != a).
                forEach( n -> n.join(a));

        a.insert(15, "15");
        a.insert(100, "100");
        a.insert(101, "101");
        a.insert(103, "103");
        a.insert(102, "102");
        a.insert(110, "110");
        a.insert(111, "111");
        System.out.println("\n\n ------------------ Insert Keys(After Insert) -------------");
        for (Node n: arr) {
            System.out.println(n.getId() + ": " + n.getCache());
        }

        System.out.println("\n\n ------------------ Test Migrate -------------");
        Node c = new Node(110);
        c.join(a);



        System.out.println("\n\n ------------------ Test LookUp Key-------------");
        a.get(100);
        a.get(111);
        a.get(0);

        System.out.println("\n\n ------------------ Test Leave -------------");
        c.leave();
        for (Node n: arr) {
            n.leave();
        }
    }
}
