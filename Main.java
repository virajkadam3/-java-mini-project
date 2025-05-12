import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class Main {
    private static List<Fruit> fruits = new ArrayList<>();
    private static List<User> users = new ArrayList<>();

    public static void main(String[] args) {
        initializeFruits();

        SwingUtilities.invokeLater(() -> {
            FruitManagementGUI gui = new FruitManagementGUI(fruits, users);
            gui.setVisible(true);
        });
    }

    private static void initializeFruits() {
        fruits.add(new Fruit(1, "Apple", 50.0, 100));
        fruits.add(new Fruit(2, "Banana", 30.5, 150));
        fruits.add(new Fruit(3, "Orange", 60.8, 120));
    }
}
