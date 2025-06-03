import javax.swing.*;
 import java.awt.*;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.sql.*;
 import java.util.ArrayList;
 
 public class VoterPage extends JFrame {
     private static final String DB_URL = "jdbc:postgresql://localhost:5432/Practise";
     private static final String DB_USER = "postgres";
     private static final String DB_PASSWORD = "pinky@flower17"; // Change to your PostgreSQL password
 
     private ArrayList<JCheckBox> checkboxes;
     private JTextField usnTextField;
 
     public VoterPage() {
         setTitle("Voter Page");
         setSize(600, 500); // Increased size for better visibility
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setLocationRelativeTo(null);
 
         // Define RGB values for sky blue
         int skyBlueRed = 135;
         int skyBlueGreen = 206;
         int skyBlueBlue = 235;
 
         Color skyBlue = new Color(skyBlueRed, skyBlueGreen, skyBlueBlue);
 
         getContentPane().setBackground(skyBlue); // Set background color
 
         JPanel mainPanel = new JPanel(new BorderLayout());
         mainPanel.setBackground(skyBlue); // Set background color for the main panel
 
         JPanel contentPanel = new JPanel();
         contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
         contentPanel.setBackground(skyBlue); // Set background color for the content panel
         checkboxes = new ArrayList<>();
 
         JLabel usnLabel = new JLabel("Enter your USN:");
         usnLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Increased font size
         usnTextField = new JTextField(20);
         usnTextField.setFont(new Font("Arial", Font.PLAIN, 16)); // Increased font size
         contentPanel.add(usnLabel);
         contentPanel.add(usnTextField);
 
         loadCandidates(contentPanel);
 
         JButton submitButton = new JButton("Submit Vote");
         submitButton.setFont(new Font("Arial", Font.BOLD, 16)); // Increased font size
         submitButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 submitVote();
             }
         });
 
         contentPanel.add(submitButton);
 
         JButton backButton = new JButton("Back to Login Page");
         backButton.setFont(new Font("Arial", Font.BOLD, 16)); // Increased font size
         backButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 dispose();
                 new loginpage();
             }
         });
 
         JPanel topPanel = new JPanel(new BorderLayout());
         topPanel.setBackground(skyBlue); // Set background color for the top panel
         topPanel.add(backButton, BorderLayout.EAST);
 
         mainPanel.add(topPanel, BorderLayout.NORTH);
         mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
 
         add(mainPanel);
         setVisible(true);
     }
 
     private void loadCandidates(JPanel panel) {
         try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
              Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT usn, name FROM AsecCandidates LIMIT 4")) {
 
             while (rs.next()) {
                 String usn = rs.getString("usn");
                 String name = rs.getString("name");
                 JCheckBox checkBox = new JCheckBox(name);
                 checkBox.putClientProperty("usn", usn);
                 checkBox.setFont(new Font("Arial", Font.PLAIN, 16)); // Increased font size
                 checkboxes.add(checkBox);
                 panel.add(checkBox);
             }
 
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
 
     private void submitVote() {
         String voterUsn = usnTextField.getText().trim();
 
         if (voterUsn.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Please enter your USN.");
             return;
         }
 
         try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
              PreparedStatement checkVotedStmt = conn.prepareStatement("SELECT voted FROM ise WHERE usn = ?")) {
 
             checkVotedStmt.setString(1, voterUsn);
             ResultSet rs = checkVotedStmt.executeQuery();
 
             if (rs.next()) {
                 if ("yes".equalsIgnoreCase(rs.getString("voted"))) {
                     JOptionPane.showMessageDialog(this, "You have already voted. You cannot vote again.");
                     return;
                 }
             } else {
                 JOptionPane.showMessageDialog(this, "Invalid USN.");
                 return;
             }
 
         } catch (SQLException e) {
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "There was an error verifying your USN.");
             return;
         }
 
         ArrayList<String> selectedVoters = new ArrayList<>();
 
         for (JCheckBox checkBox : checkboxes) {
             if (checkBox.isSelected()) {
                 selectedVoters.add((String) checkBox.getClientProperty("usn"));
             }
         }
 
         if (selectedVoters.size() != 2) {
             JOptionPane.showMessageDialog(this, "You must vote for exactly 2 people.");
             return;
         }
 
         try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
             conn.setAutoCommit(false);
             String sqlUpdateVotingCount = "UPDATE AsecCandidates SET votingcount = votingcount + 1 WHERE usn = ?";
             String sqlUpdateVotedColumn = "UPDATE ise SET voted = 'yes' WHERE usn = ?";
             try (PreparedStatement pstmtUpdateVotingCount = conn.prepareStatement(sqlUpdateVotingCount);
                  PreparedStatement pstmtUpdateVotedColumn = conn.prepareStatement(sqlUpdateVotedColumn)) {
 
                 for (String usn : selectedVoters) {
                     pstmtUpdateVotingCount.setString(1, usn);
                     pstmtUpdateVotingCount.executeUpdate();
                 }
 
                 pstmtUpdateVotedColumn.setString(1, voterUsn);
                 pstmtUpdateVotedColumn.executeUpdate();
             }
             conn.commit();
             JOptionPane.showMessageDialog(this, "Vote successfully recorded.");
             dispose();
             new loginpage();
         } catch (SQLException ex) {
             ex.printStackTrace();
             JOptionPane.showMessageDialog(this, "There was an error recording your vote.");
         }
     }
 
     public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
             new VoterPage();
         });
     }
 }