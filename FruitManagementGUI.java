import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FruitManagementGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private List<Fruit> fruits;
    private List<User> users;

    private static final String LOG_FILE = "actions_log.txt";
    private static final String ADMIN_LOG_FILE = "admin_log.txt";
    private static final String CUSTOMER_LOG_FILE = "customer_log.txt";
    private static final String FRUIT_LOG_FILE = "fruit_log.txt";
    private static final String BILL_LOG_FILE = "bill_log.txt";

    public FruitManagementGUI(List<Fruit> fruits, List<User> users) {
        this.fruits = fruits;
        this.users = users;

        // Set Nimbus Look and Feel for modern UI
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception e) {
            System.out.println("Failed to set Nimbus Look and Feel");
        }

        setTitle("Fruit Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245)); // light gray background

        mainPanel.add(createWelcomePanel(), "welcome");
        mainPanel.add(createRegisterPanel(), "register");
        mainPanel.add(createLoginPanel(), "login");
        // Admin and Customer panels will be added dynamically after login

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");
    }

    private void logAction(String action, String logFile) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = timestamp + " - " + action;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel welcomeLabel = new JLabel("Welcome to the Fruit Management System!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(34, 139, 34)); // forest green

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");

        styleButton(registerButton, new Color(34, 139, 34), Color.WHITE);
        styleButton(loginButton, new Color(34, 139, 34), Color.WHITE);

        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        loginButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        panel.add(registerButton, gbc);

        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(34, 139, 34));

        JLabel userTypeLabel = new JLabel("Select User Type:");
        userTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String[] userTypes = {"Admin", "Customer"};
        JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPasswordField passwordField = new JPasswordField(15);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        styleButton(registerButton, new Color(34, 139, 34), Color.WHITE);
        styleButton(backButton, new Color(128, 128, 128), Color.WHITE);

        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        messageLabel.setForeground(Color.RED);

        registerButton.addActionListener(e -> {
            String userType = (String) userTypeCombo.getSelectedItem();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username and password cannot be empty.");
                return;
            }

            boolean exists = users.stream().anyMatch(u -> u.getUsername().equals(username));
            if (exists) {
                messageLabel.setText("Username already exists.");
                return;
            }

            if ("Admin".equals(userType)) {
                Admin admin = new Admin(username, password);
                users.add(admin);
                messageLabel.setText("Admin registered successfully.");
                logAction("Admin registered: " + username, ADMIN_LOG_FILE);
            } else {
                Customer customer = new Customer(username, password, fruits);
                users.add(customer);
                messageLabel.setText("Customer registered successfully.");
                logAction("Customer registered: " + username, CUSTOMER_LOG_FILE);
            }

            usernameField.setText("");
            passwordField.setText("");
        });

        backButton.addActionListener(e -> {
            messageLabel.setText(" ");
            usernameField.setText("");
            passwordField.setText("");
            cardLayout.show(mainPanel, "welcome");
        });

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(userTypeLabel, gbc);
        gbc.gridx = 1;
        panel.add(userTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(registerButton, gbc);
        gbc.gridx = 1;
        panel.add(backButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(34, 139, 34));

        JLabel userTypeLabel = new JLabel("Select User Type:");
        userTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String[] userTypes = {"Admin", "Customer"};
        JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPasswordField passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        styleButton(loginButton, new Color(34, 139, 34), Color.WHITE);
        styleButton(backButton, new Color(128, 128, 128), Color.WHITE);

        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        messageLabel.setForeground(Color.RED);

        loginButton.addActionListener(e -> {
            String userType = (String) userTypeCombo.getSelectedItem();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            User user = null;
            for (User u : users) {
                if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                    if ("Admin".equals(userType) && u instanceof Admin) {
                        user = u;
                        break;
                    } else if ("Customer".equals(userType) && u instanceof Customer) {
                        Customer c = (Customer) u;
                        // Recreate customer with fruits list to ensure cart has fruits inventory
                        user = new Customer(c.getUsername(), c.getPassword(), fruits);
                        break;
                    }
                }
            }

            if (user == null) {
                messageLabel.setText("Invalid username or password.");
                logAction("Failed login attempt for username: " + username + " as " + userType, CUSTOMER_LOG_FILE);
                return;
            }

            usernameField.setText("");
            passwordField.setText("");
            messageLabel.setText(" ");

            if (user instanceof Admin) {
                logAction("Admin logged in: " + user.getUsername(), ADMIN_LOG_FILE);
                showAdminPanel((Admin) user);
            } else if (user instanceof Customer) {
                logAction("Customer logged in: " + user.getUsername(), CUSTOMER_LOG_FILE);
                showCustomerPanel((Customer) user);
            }
        });

        backButton.addActionListener(e -> {
            messageLabel.setText(" ");
            usernameField.setText("");
            passwordField.setText("");
            cardLayout.show(mainPanel, "welcome");
        });

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(userTypeLabel, gbc);
        gbc.gridx = 1;
        panel.add(userTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(loginButton, gbc);
        gbc.gridx = 1;
        panel.add(backButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        return panel;
    }

    private void showAdminPanel(Admin admin) {
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setBackground(new Color(255, 255, 255));
        adminPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome, Admin: " + admin.getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(34, 139, 34));
        adminPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBackground(new Color(255, 255, 255));
        JButton addFruitButton = new JButton("Add Fruit");
        JButton viewFruitsButton = new JButton("View Fruits");
        JButton deleteFruitButton = new JButton("Delete Fruit");
        JButton updateFruitButton = new JButton("Update Fruit");
        JButton logoutButton = new JButton("Logout");

        styleButton(addFruitButton, new Color(34, 139, 34), Color.WHITE);
        styleButton(viewFruitsButton, new Color(34, 139, 34), Color.WHITE);
        styleButton(deleteFruitButton, new Color(34, 139, 34), Color.WHITE);
        styleButton(updateFruitButton, new Color(34, 139, 34), Color.WHITE);
        styleButton(logoutButton, new Color(178, 34, 34), Color.WHITE);

        buttonPanel.add(addFruitButton);
        buttonPanel.add(viewFruitsButton);
        buttonPanel.add(deleteFruitButton);
        buttonPanel.add(updateFruitButton);
        buttonPanel.add(logoutButton);

        adminPanel.add(buttonPanel, BorderLayout.CENTER);

        addFruitButton.addActionListener(e -> {
            showAddFruitDialog();
            logAction("Admin clicked Add Fruit button", ADMIN_LOG_FILE);
        });
        viewFruitsButton.addActionListener(e -> {
            showFruitsDialog();
            logAction("Admin clicked View Fruits button", ADMIN_LOG_FILE);
        });
        deleteFruitButton.addActionListener(e -> {
            showDeleteFruitDialog();
            logAction("Admin clicked Delete Fruit button", ADMIN_LOG_FILE);
        });
        updateFruitButton.addActionListener(e -> {
            showUpdateFruitDialog();
            logAction("Admin clicked Update Fruit button", ADMIN_LOG_FILE);
        });
        logoutButton.addActionListener(e -> {
            logAction("Admin logged out", ADMIN_LOG_FILE);
            cardLayout.show(mainPanel, "welcome");
            mainPanel.remove(adminPanel);
        });

        mainPanel.add(adminPanel, "adminPanel");
        cardLayout.show(mainPanel, "adminPanel");
    }

    private void showCustomerPanel(Customer customer) {
        JPanel customerPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, Customer: " + customer.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        customerPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        JButton viewFruitsButton = new JButton("View Fruits");
        JButton addFruitToCartButton = new JButton("Add Fruit to Cart");
        JButton viewCartButton = new JButton("View Cart");
        JButton deleteFruitFromCartButton = new JButton("Delete Fruit from Cart");
        JButton buyFruitsButton = new JButton("Buy Fruits");
        JButton showBillButton = new JButton("Show Bill");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(viewFruitsButton);
        buttonPanel.add(addFruitToCartButton);
        buttonPanel.add(viewCartButton);
        buttonPanel.add(deleteFruitFromCartButton);
        buttonPanel.add(buyFruitsButton);
        buttonPanel.add(showBillButton);
        buttonPanel.add(logoutButton);

        customerPanel.add(buttonPanel, BorderLayout.CENTER);

        viewFruitsButton.addActionListener(e -> {
            showFruitsDialogForCustomer();
            logAction("Customer clicked View Fruits button", CUSTOMER_LOG_FILE);
        });
        addFruitToCartButton.addActionListener(e -> {
            showAddFruitToCartDialog(customer);
            logAction("Customer clicked Add Fruit to Cart button", CUSTOMER_LOG_FILE);
        });
        viewCartButton.addActionListener(e -> {
            showCartDialog(customer);
            logAction("Customer clicked View Cart button", CUSTOMER_LOG_FILE);
        });
        deleteFruitFromCartButton.addActionListener(e -> {
            showDeleteFruitFromCartDialog(customer);
            logAction("Customer clicked Delete Fruit from Cart button", CUSTOMER_LOG_FILE);
        });
        buyFruitsButton.addActionListener(e -> {
            customer.buyFruit();
            logAction("Customer completed purchase", CUSTOMER_LOG_FILE);
            JOptionPane.showMessageDialog(this, "Purchase completed.");
        });
        showBillButton.addActionListener(e -> {
            showBillDialog(customer);
            logAction("Customer clicked Show Bill button", CUSTOMER_LOG_FILE);
        });
        logoutButton.addActionListener(e -> {
            logAction("Customer logged out", CUSTOMER_LOG_FILE);
            cardLayout.show(mainPanel, "welcome");
            mainPanel.remove(customerPanel);
        });

        mainPanel.add(customerPanel, "customerPanel");
        cardLayout.show(mainPanel, "customerPanel");
    }

    private void showAddFruitDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();

        Object[] message = {
            "Fruit ID:", idField,
            "Fruit Name:", nameField,
            "Fruit Price:", priceField,
            "Fruit Stock:", stockField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Fruit", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());

                Fruit fruit = new Fruit(id, name, price, stock);
                fruits.add(fruit);
                logAction("Fruit added: " + name + " (ID: " + id + ", Price: " + price + ", Stock: " + stock + ")", FRUIT_LOG_FILE);
                JOptionPane.showMessageDialog(this, "Fruit added successfully.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid values.");
            }
        }
    }

    private void showFruitsDialog() {
        if (fruits.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No fruits available.");
            return;
        }
        String[] columnNames = {"Fruit Name", "Price", "Stock"};
        Object[][] data = new Object[fruits.size()][3];
        for (int i = 0; i < fruits.size(); i++) {
            Fruit fruit = fruits.get(i);
            data[i][0] = fruit.getName();
            data[i][1] = "₹" + fruit.getPrice();
            data[i][2] = fruit.getStock();
        }
        JTable table = new JTable(data, columnNames);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Available Fruits", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showFruitsDialogForCustomer() {
        if (fruits.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No fruits available.");
            return;
        }
        String[] columnNames = {"Fruit Name", "Price"};
        Object[][] data = new Object[fruits.size()][2];
        for (int i = 0; i < fruits.size(); i++) {
            Fruit fruit = fruits.get(i);
            data[i][0] = fruit.getName();
            data[i][1] = "₹" + fruit.getPrice();
        }
        JTable table = new JTable(data, columnNames);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Available Fruits", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDeleteFruitDialog() {
        String nameStr = JOptionPane.showInputDialog(this, "Enter fruit name to delete:");
        if (nameStr != null) {
            String nameInput = nameStr.trim();
            boolean removed = fruits.removeIf(fruit -> fruit.getName().equalsIgnoreCase(nameInput));
            if (removed) {
                logAction("Fruit deleted: " + nameInput, FRUIT_LOG_FILE);
                JOptionPane.showMessageDialog(this, "Fruit deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Fruit not found.");
            }
        }
    }

    private void showUpdateFruitDialog() {
        String nameStr = JOptionPane.showInputDialog(this, "Enter fruit name to update:");
        if (nameStr != null) {
            try {
                String nameInput = nameStr.trim();
                Fruit fruit = null;
                for (Fruit f : fruits) {
                    if (f.getName().equalsIgnoreCase(nameInput)) {
                        fruit = f;
                        break;
                    }
                }
                if (fruit == null) {
                    JOptionPane.showMessageDialog(this, "Fruit not found.");
                    return;
                }

                JTextField nameField = new JTextField(fruit.getName());
                JTextField priceField = new JTextField(String.valueOf(fruit.getPrice()));
                JTextField stockField = new JTextField(String.valueOf(fruit.getStock()));

                Object[] message = {
                    "Fruit Name:", nameField,
                    "Fruit Price:", priceField,
                    "Fruit Stock:", stockField
                };

                int option = JOptionPane.showConfirmDialog(this, message, "Update Fruit", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    int stock = Integer.parseInt(stockField.getText().trim());

                    fruit.setName(name);
                    fruit.setPrice(price);
                    fruit.setStock(stock);
                    logAction("Fruit updated: " + name + " (ID: " + fruit.getId() + ", Price: " + price + ", Stock: " + stock + ")", FRUIT_LOG_FILE);
                    JOptionPane.showMessageDialog(this, "Fruit updated successfully.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    private void showAddFruitToCartDialog(Customer customer) {
        String nameStr = JOptionPane.showInputDialog(this, "Enter fruit name to add to cart:");
        if (nameStr != null) {
            String nameInput = nameStr.trim();
            Fruit fruit = null;
            for (Fruit f : fruits) {
                if (f.getName().equalsIgnoreCase(nameInput)) {
                    fruit = f;
                    break;
                }
            }
            if (fruit == null) {
                JOptionPane.showMessageDialog(this, "Fruit not found.");
                return;
            }
            String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity to add:");
            if (quantityStr == null) {
                return;
            }
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr.trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                    return;
                }
                if (quantity > fruit.getStock()) {
                    JOptionPane.showMessageDialog(this, "Quantity exceeds stock available.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid quantity input.");
                return;
            }
            customer.addFruit(fruit, quantity);
            fruit.setStock(fruit.getStock() - quantity);
            JOptionPane.showMessageDialog(this, "Fruit added to cart.");
        }
    }

    private void showDeleteFruitFromCartDialog(Customer customer) {
        String nameStr = JOptionPane.showInputDialog(this, "Enter fruit name to delete from cart:");
        if (nameStr != null) {
            String nameInput = nameStr.trim();
            Fruit fruit = null;
            for (Fruit f : fruits) {
                if (f.getName().equalsIgnoreCase(nameInput)) {
                    fruit = f;
                    break;
                }
            }
            if (fruit == null) {
                JOptionPane.showMessageDialog(this, "Fruit not found.");
                return;
            }
            String[] options = {"Reduce Quantity", "Delete Whole Fruit"};
            int choice = JOptionPane.showOptionDialog(this, "Choose an option:", "Delete Fruit from Cart",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (choice == 0) { // Reduce Quantity
                String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity to reduce:");
                if (quantityStr == null) {
                    return;
                }
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr.trim());
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity input.");
                    return;
                }
                customer.reduceFruitQuantity(fruit.getId(), quantity);
                fruit.setStock(fruit.getStock() + quantity);
                JOptionPane.showMessageDialog(this, "Reduced quantity of fruit in cart.");
            } else if (choice == 1) { // Delete Whole Fruit
                customer.deleteFruit(fruit.getId());
                fruit.setStock(fruit.getStock() + 1);
                JOptionPane.showMessageDialog(this, "Fruit deleted from cart.");
            }
        }
    }

    private void showCartDialog(Customer customer) {
        if (customer == null || customer.getCart() == null || customer.getCart().getFruits().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s %10s%n", "Fruit Name", "Quantity"));
        sb.append("--------------------------------\n");
        Cart cart = customer.getCart();
        for (Map.Entry<Integer, Integer> entry : cart.getFruitQuantities().entrySet()) {
            int fruitId = entry.getKey();
            int quantity = entry.getValue();
            Fruit fruit = null;
            for (Fruit f : cart.getFruitsInventory()) {
                if (f.getId() == fruitId) {
                    fruit = f;
                    break;
                }
            }
            if (fruit != null) {
                sb.append(String.format("%-20s %10d%n", fruit.getName(), quantity));
            }
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Fruits in Cart", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBillDialog(Customer customer) {
        Cart cart = customer.getCart();
        if (cart == null || cart.getFruitQuantities().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s %10s %15s%n", "Fruit Name", "Quantity", "Price"));
        sb.append("------------------------------------------------\n");
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : cart.getFruitQuantities().entrySet()) {
            int fruitId = entry.getKey();
            int quantity = entry.getValue();
            Fruit fruit = null;
            for (Fruit f : cart.getFruitsInventory()) {
                if (f.getId() == fruitId) {
                    fruit = f;
                    break;
                }
            }
            if (fruit != null) {
                double price = fruit.getPrice() * quantity;
                total += price;
                sb.append(String.format("%-20s %10d %15.2f%n", fruit.getName(), quantity, price));
            }
        }
        sb.append("------------------------------------------------\n");
        sb.append(String.format("%-20s %10s %15.2f%n", "Total", "", total));
        logAction("Bill generated for customer: " + customer.getUsername() + ", Total: " + total, BILL_LOG_FILE);
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Bill", JOptionPane.INFORMATION_MESSAGE);
    }
}
