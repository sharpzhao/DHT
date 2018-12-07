public class Main {

    public static void main(String[] args) {
        Node a = new Node(10);
        a.join(null);
        Node[] arr = new Node[]{
                new Node(36),
                new Node(120),
                new Node(190),
                new Node(70),
                new Node(52),
                new Node(5)
        };

        for (Node n: arr) {
            n.join(a);
        }

        System.out.println(" print circle ");

        Node cur = a.getPrevNode();
        System.out.print("10,");
        while (!cur.equals(a)) {
            System.out.print(cur.getId() + ",");
            cur = cur.getPrevNode();
        }
        System.out.println();

        System.out.println(a.fingerTable);
        for (Node n: arr) {
            System.out.println(n.fingerTable);
        }


        System.out.println(" ------------------ Test insert -------------");
        a.insert(15, "15");
        a.insert(100, "100");
        a.insert(101, "101");
        a.insert(103, "103");
        a.insert(102, "102");
        a.insert(110, "110");
        a.insert(111, "111");
        for (Node n: arr) {
            System.out.println(n.getId() + ": " + n.getCache());
        }

        System.out.println(" ------------------ Test migrate -------------");
        Node c = new Node(110);
        c.join(a);

        for (Node n: arr) {
            System.out.println(n.getId() + ": " + n.getCache());
        }
        System.out.println(c.getId() + ": " + c.getCache());


        System.out.println(" ------------------ Test remove -------------");
        a.remove(111);
        a.remove(15);
        for (Node n: arr) {
            System.out.println(n.getId() + ": " + n.getCache());
        }
        System.out.println(c.getId() + ": " + c.getCache());
    }
}
