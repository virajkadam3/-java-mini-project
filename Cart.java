import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Cart {
    private Map<Integer, Integer> fruitQuantities; // fruitId -> quantity
    private List<Fruit> fruitsInventory;

    public Cart(List<Fruit> fruitsInventory) {
        fruitQuantities = new HashMap<>();
        this.fruitsInventory = fruitsInventory;
    }

    public Map<Integer, Integer> getFruitQuantities() {
        return fruitQuantities;
    }

    public List<Fruit> getFruitsInventory() {
        return fruitsInventory;
    }

    public void addFruit(Fruit fruit, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }
        int currentQuantity = fruitQuantities.getOrDefault(fruit.getId(), 0);
        fruitQuantities.put(fruit.getId(), currentQuantity + quantity);
        System.out.println(quantity + " " + fruit.getName() + "(s) added to cart.");
    }

    public void removeFruit(int fruitId) {
        if (fruitQuantities.containsKey(fruitId)) {
            fruitQuantities.remove(fruitId);
            System.out.println("Fruit with ID " + fruitId + " removed from cart.");
        } else {
            System.out.println("Fruit with ID " + fruitId + " not found in cart.");
        }
    }

    public void reduceFruitQuantity(int fruitId, int quantity) {
        if (!fruitQuantities.containsKey(fruitId)) {
            System.out.println("Fruit with ID " + fruitId + " not found in cart.");
            return;
        }
        int currentQuantity = fruitQuantities.get(fruitId);
        if (quantity <= 0) {
            System.out.println("Quantity to reduce must be positive.");
            return;
        }
        if (quantity >= currentQuantity) {
            fruitQuantities.remove(fruitId);
            System.out.println("Fruit with ID " + fruitId + " removed from cart.");
        } else {
            fruitQuantities.put(fruitId, currentQuantity - quantity);
            System.out.println("Reduced quantity of fruit with ID " + fruitId + " by " + quantity + ".");
        }
    }

    public void viewCart() {
        if (fruitQuantities.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("----- Bill -----");
        System.out.printf("%-20s %10s %15s%n", "Fruit Name", "Quantity", "Price");
        System.out.println("---------------------------------------------");
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : fruitQuantities.entrySet()) {
            int fruitId = entry.getKey();
            int quantity = entry.getValue();
            Fruit fruit = findFruitById(fruitId);
            if (fruit != null) {
                double price = fruit.getPrice() * quantity;
                total += price;
                System.out.printf("%-20s %10d %15.2f%n", fruit.getName(), quantity, price);
            }
        }
        System.out.println("---------------------------------------------");
        System.out.printf("%-20s %10s %15.2f%n", "Total", "", total);
        System.out.println("------------------------------");
    }

    public void clearCart() {
        fruitQuantities.clear();
        System.out.println("Cart cleared.");
    }

    public double getTotalPrice() {
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : fruitQuantities.entrySet()) {
            int fruitId = entry.getKey();
            int quantity = entry.getValue();
            Fruit fruit = findFruitById(fruitId);
            if (fruit != null) {
                total += fruit.getPrice() * quantity;
            }
        }
        return total;
    }

    public List<Fruit> getFruits() {
        List<Fruit> fruits = new ArrayList<>();
        for (int fruitId : fruitQuantities.keySet()) {
            Fruit fruit = findFruitById(fruitId);
            if (fruit != null) {
                fruits.add(fruit);
            }
        }
        return fruits;
    }

    // Helper method to find Fruit by id from the cart's fruits
    private Fruit findFruitById(int fruitId) {
        for (Fruit fruit : fruitsInventory) {
            if (fruit.getId() == fruitId) {
                return fruit;
            }
        }
        return null;
    }
}
