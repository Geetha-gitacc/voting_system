import javax.swing.*;
 import java.awt.*;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.sql.*;
 import java.util.ArrayList;
 import java.util.List;
 
 public class president extends JFrame {
     private static final String DB_URL = "jdbc:postgresql://localhost:5432/Practise";
     private static final String DB_USER = "postgres";
     private static final String DB_PASSWORD = "pinky@flower17"; // Change to your PostgreSQL password
 
     private ButtonGroup candidateGroup;
     private List<JRadioButton> candidateButtons;
     private JButton voteButton;
 
     public president() {
         setTitle("President Voting");
         setSize(500, 400); // Increased the frame size
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setLocationRelativeTo(null);
 
         // Set background color of the frame to sky blue
         getContentPane().setBackground(new Color(135, 206, 235));
 
         JPanel panel = new JPanel();
         panel.setLayout(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.fill = GridBagConstraints.HORIZONTAL;
         panel.setBackground(new Color(135, 206, 235)); // Set panel background to match the frame
 
         candidateGroup = new ButtonGroup();
         candidateButtons = new ArrayList<>();
 
         List<String> candidates = fetchCandidates();
 
         if (candidates.isEmpty()) {
             JOptionPane.showMessageDialog(this, "No candidates found in the database.");
             return;
         }
 
         for (int i = 0; i < candidates.size(); i++) {
             JRadioButton candidateButton = new JRadioButton(candidates.get(i));
             candidateButton.setBackground(new Color(255, 255, 255)); // Set background of text to white
             candidateGroup.add(candidateButton);
             candidateButtons.add(candidateButton);
             gbc.gridx = 0;
             gbc.gridy = i;
             panel.add(candidateButton, gbc);
         }
 
         voteButton = new JButton("Vote");
         gbc.gridx = 0;
         gbc.gridy = candidates.size();
         panel.add(voteButton, gbc);
 
         voteButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 recordVote();
             }
         });
 
         add(panel);
 
         // Add "Back to Login" button in the upper right corner
         JButton backButton = new JButton("Back to Login");
         backButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 new loginpage().setVisible(true);
                 dispose(); // Close the current frame
             }
         });
 
         // Add button to a separate panel for proper layout management
         JPanel topPanel = new JPanel();
         topPanel.setLayout(new BorderLayout());
         topPanel.setBackground(new Color(135, 206, 235)); // Set top panel background to match the frame
         topPanel.add(backButton, BorderLayout.EAST);
         add(topPanel, BorderLayout.NORTH);
 
         setVisible(true);
     }
 
     private List<String> fetchCandidates() {
         List<String> candidates = new ArrayList<>();
         try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
             String query = "SELECT name FROM new_data"; // Fetch all candidates
             try (Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(query)) {
                 while (rs.next()) {
                     candidates.add(rs.getString("name"));
                 }
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
         return candidates;
     }
 
     private void recordVote() {
         for (JRadioButton button : candidateButtons) {
             if (button.isSelected()) {
                 String selectedCandidate = button.getText();
                 saveVoteToDatabase(selectedCandidate);
                 JOptionPane.showMessageDialog(this, "You voted for: " + selectedCandidate);
 
                 // Show president based on highest vote count after voting
                 showPresident();
 
                 return;
             }
         }
         JOptionPane.showMessageDialog(this, "Please select a candidate to vote for.");
     }
 
     private void saveVoteToDatabase(String candidateName) {
         try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
             String updateQuery = "UPDATE new_data SET voting_count = voting_count + 1 WHERE name = ?";
             try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                 pstmt.setString(1, candidateName);
                 int rowsAffected = pstmt.executeUpdate();
                 if (rowsAffected > 0) {
                     System.out.println("Vote recorded for: " + candidateName);
                 } else {
                     System.out.println("Failed to record vote for: " + candidateName);
                 }
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
 
     private void showPresident() {
         try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
             String query = "SELECT name, voting_count FROM new_data ORDER BY voting_count DESC";
             try (Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(query)) {
                 if (rs.next()) {
                     String presidentName = rs.getString("name");
                     int highestVotes = rs.getInt("voting_count");
                     // Check for tie
                     while (rs.next() && rs.getInt("voting_count") == highestVotes) {
                         JOptionPane.showMessageDialog(this, "It's a tie vote.");
                         return;
                     }
                     JOptionPane.showMessageDialog(this, "The elected president is: " + presidentName);
                 } else {
                     JOptionPane.showMessageDialog(this, "No candidates found.");
                 }
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
 
     public static void main(String[] args) {
         try {
             Class.forName("org.postgresql.Driver");
         } catch (ClassNotFoundException e) {
             e.printStackTrace();
             return;
         }
         new president();
     }
 }