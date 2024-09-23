import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

class LoginPage extends JFrame implements ActionListener {
    
    JTextField userField;
    JPasswordField passField;
    JButton btnLogin;
    JLabel labelUser, labelPass;
    
    // Database connection
    Connection con;
    
    LoginPage() {
        setTitle("Login");
        
        // Create UI elements
        labelUser = new JLabel("Username:");
        labelPass = new JLabel("Password:");
        userField = new JTextField();
        passField = new JPasswordField();
        btnLogin = new JButton("Login");
        
        // Set bounds
        labelUser.setBounds(30, 30, 100, 30);
        labelPass.setBounds(30, 80, 100, 30);
        userField.setBounds(120, 30, 150, 30);
        passField.setBounds(120, 80, 150, 30);
        btnLogin.setBounds(120, 130, 150, 30);
        
        // Add ActionListener
        btnLogin.addActionListener(this);
        
        // Add elements to JFrame
        add(labelUser);
        add(labelPass);
        add(userField);
        add(passField);
        add(btnLogin);
        
        // Frame settings
        setLayout(null);
        setSize(400, 250);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Establish DB connection
        connectDB();
    }
    
    // Method to connect to the database
    void connectDB() {
        try {
            // Load the SQLite driver (You can replace it with MySQL if needed)
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_db", // URL of the MySQL database
            "root",                      // Your MySQL username
            "Nandyala@2003" );
            System.out.println("Connected to database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Action performed on login button click
    public void actionPerformed(ActionEvent e) {
        String username = userField.getText();
        String password = new String(passField.getPassword());
        
        if (authenticate(username, password)) {
            // If login is successful, start the quiz
            JOptionPane.showMessageDialog(this, "Login Successful!");
            new OnlineTest("Quiz App", getUserId(username)); // Pass user ID to OnlineTest
            dispose(); // Close login page
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Credentials.");
        }
    }
    
    // Method to authenticate user
    boolean authenticate(String username, String password) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // If there's a result, user exists
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Method to get user ID
    int getUserId(String username) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM Users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public static void main(String[] args) {
        new LoginPage();
    }
}

