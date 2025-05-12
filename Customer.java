import java.util.Scanner;
import java.util.List;

public class Customer implements User {
    private String username;
    private String password;
    private Cart cart;

    public Customer(List<Fruit> fruitsInventory) {
        cart = new Cart(fruitsInventory);
    }

    public Customer() {
        this(null);
    }

    public Customer(String username, String password) {
        this(username, password, null);
    }

    public Customer(String username, String password, List<Fruit> fruitsInventory) {
        this.username = username;
        this.password = password;
        cart = new Cart(fruitsInventory);
    }

    @Override
    public void register() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Customer Registration");
        System.out.print("Enter username: ");
        username = scanner.nextLine();
        System.out.print("Enter password: ");
        password = scanner.nextLine();
        System.out.println("Customer registered successfully!");
    }

    public void addFruit(Fruit fruit, int quantity) {
        cart.addFruit(fruit, quantity);
    }

    public void viewCart() {
        cart.viewCart();
    }

    public void deleteFruit(int fruitId) {
        cart.removeFruit(fruitId);
    }

    public void reduceFruitQuantity(int fruitId, int quantity) {
        cart.reduceFruitQuantity(fruitId, quantity);
    }

    public void buyFruit() {
        if (cart.getFruits().isEmpty()) {
            System.out.println("Cart is empty. Add fruits before buying.");
            return;
        }
        System.out.println("Buying fruits...");
        showBill();
        cart.clearCart();
    }

    public void deleteCart() {
        cart.clearCart();
    }

    public void showBill() {
        System.out.println("Bill:");
        cart.viewCart();
        System.out.println("Total Price: $" + cart.getTotalPrice());
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Cart getCart() {
        return cart;
    }

    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}
