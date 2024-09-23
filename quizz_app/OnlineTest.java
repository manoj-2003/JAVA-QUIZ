import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

class OnlineTest extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    
    // UI components
    JLabel label;
    JRadioButton radioButton[] = new JRadioButton[5];
    JButton btnNext, btnBookmark;
    ButtonGroup bg;

    // Quiz state variables
    int count = 0, current = 0, x = 1, y = 1, now = 0;
    int m[] = new int[10];

    // Database connection
    Connection con;
    int userId; // To store the logged-in user's ID

    // Constructor with user ID
    OnlineTest(String s, int userId) {
        super(s);
        this.userId = userId;
        
        // Connect to the database
        connectDB();
        
        // Quiz UI setup
        label = new JLabel();
        add(label);
        bg = new ButtonGroup();
        for (int i = 0; i < 5; i++) {
            radioButton[i] = new JRadioButton();
            add(radioButton[i]);
            bg.add(radioButton[i]);
        }
        btnNext = new JButton("Next");
        btnBookmark = new JButton("Bookmark");
        btnNext.addActionListener(this);
        btnBookmark.addActionListener(this);
        add(btnNext);
        add(btnBookmark);
        set();
        label.setBounds(30, 40, 450, 20);
        radioButton[0].setBounds(50, 80, 450, 20);
        radioButton[1].setBounds(50, 110, 200, 20);
        radioButton[2].setBounds(50, 140, 200, 20);
        radioButton[3].setBounds(50, 170, 200, 20);
        btnNext.setBounds(100, 240, 100, 30);
        btnBookmark.setBounds(270, 240, 100, 30);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocation(250, 100);
        setVisible(true);
        setSize(600, 350);
    }

    // Connect to the database
    void connectDB() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection to MySQL database
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_db", "root", "Nandyala@2003");
            System.out.println("Connected to database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handle button actions
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnNext) {
            if (check())
                count++;
            current++;
            set();
            if (current == 9) {
                btnNext.setEnabled(false);
                btnBookmark.setText("Result");
            }
        }
        if (e.getActionCommand().equals("Bookmark")) {
            JButton bk = new JButton("Bookmark" + x);
            bk.setBounds(480, 20 + 30 * x, 100, 30);
            add(bk);
            bk.addActionListener(this);
            m[x] = current;
            x++;
            current++;
            set();
            if (current == 9)
                btnBookmark.setText("Result");
            setVisible(false);
            setVisible(true);
        }
        for (int i = 0, y = 1; i < x; i++, y++) {
            if (e.getActionCommand().equals("Bookmark" + y)) {
                if (check())
                    count++;
                now = current;
                current = m[y];
                set();
                ((JButton) e.getSource()).setEnabled(false);
                current = now;
            }
        }

        if (e.getActionCommand().equals("Result")) {
            showResult();
        }
    }

    // Method to show results and store them in the database
    private void showResult() {
        if (check())
            count++;
        JOptionPane.showMessageDialog(this, "Correct answers = " + count);
        storeResults(); // Store the quiz result in the database
        System.exit(0);
    }

    // Method to store quiz results in the database
    void storeResults() {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO Results (user_id, marks) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, count);
            ps.executeUpdate();
            System.out.println("Results stored successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Set questions with options
    void set() {
        radioButton[4].setSelected(true);
        if (current == 0) {
            label.setText("Que1: Wwwwhich of the following is used for GUI programming in Java?");
            radioButton[0].setText("Swing");
            radioButton[1].setText("AWT");
            radioButton[2].setText("JavaFX");
            radioButton[3].setText("All of the above");
        }
        if (current == 1) {
            label.setText("Que2: Which feature of Java 7 allows not explicitly closing IO resources?");
            radioButton[0].setText("try-catch-finally");
            radioButton[1].setText("IOException");
            radioButton[2].setText("AutoCloseable");
            radioButton[3].setText("Streams");
        }
        // Add other questions here similarly...
    }

    // Declare right answers.
    boolean check() {
        if (current == 0)
            return radioButton[3].isSelected();
        if (current == 1)
            return radioButton[2].isSelected();
        // Add checks for other questions here...
        return false;
    }

    public static void main(String[] args) {
        // Pass the user ID after successful login (e.g., 1)
        new OnlineTest("Online Quiz App", 1);
    }
}
