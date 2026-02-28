import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class AuctionSystem {
    private static int participantCount;
    private static ArrayList<Participant> participants = new ArrayList<>();
    private static ArrayList<Participant> verifiedParticipants = new ArrayList<>();
    private static String biddingItem;
    private static double basePrice;
    private static double highestBid;
    private static Participant highestBidder;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AuctionSystem::showParticipantCountFrame);
    }

    // Frame to enter participant count
    private static void showParticipantCountFrame() {
        JFrame frame = new JFrame("Auction System - Participant Count");
        frame.setLayout(new FlowLayout());
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Enter number of participants:");
        JTextField textField = new JTextField(10);
        JButton okButton = new JButton("OK");

        frame.add(label);
        frame.add(textField);
        frame.add(okButton);

        okButton.addActionListener(e -> {
            try {
                participantCount = Integer.parseInt(textField.getText());
                if (participantCount <= 0) throw new NumberFormatException();
                frame.dispose();
                collectParticipantDetails();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid positive number.");
            }
        });

        frame.setVisible(true);
    }

    // Collect participant details one by one
    private static void collectParticipantDetails() {
        for (int i = 0; i < participantCount; i++) {
            String name = JOptionPane.showInputDialog("Enter name for Participant " + (i + 1) + ":");
            if (name == null || name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Name cannot be empty. Please enter again.");
                i--;
                continue;
            }

            String password = JOptionPane.showInputDialog("Enter password for Participant " + (i + 1) + ":");
            if (password == null || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password cannot be empty. Please enter again.");
                i--;
                continue;
            }

            participants.add(new Participant(name, password));
        }
        verifyParticipantsFrame();
    }

    // Frame for verification of participants
    private static void verifyParticipantsFrame() {
        JFrame frame = new JFrame("Auction System - Verify Participants");
        frame.setLayout(new GridLayout(participants.size() + 1, 3));
        frame.setSize(500, 200 + participants.size() * 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel statusLabel = new JLabel("Verification Status:");
        statusLabel.setForeground(Color.BLUE);
        frame.add(statusLabel);

        for (Participant participant : participants) {
            JPanel panel = new JPanel(new FlowLayout());
            JLabel nameLabel = new JLabel("Participant: " + participant.getName());
            JTextField nameField = new JTextField(10);
            JPasswordField passwordField = new JPasswordField(10);
            JButton verifyButton = new JButton("Verify");

            panel.add(nameLabel);
            panel.add(new JLabel("Enter Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Enter Password:"));
            panel.add(passwordField);
            panel.add(verifyButton);

            verifyButton.addActionListener(e -> {
                String name = nameField.getText();
                String password = new String(passwordField.getPassword());
                if (participant.verify(name, password)) {
                    JOptionPane.showMessageDialog(frame, "Verification successful for " + participant.getName());
                    verifiedParticipants.add(participant);
                    verifyButton.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(frame, "Verification failed. Try again.");
                }
            });

            frame.add(panel);
        }

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            if (verifiedParticipants.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No participants verified. Exiting.");
                System.exit(0);
            }
            frame.dispose();
            showBiddingItemFrame();
        });
        frame.add(nextButton);

        frame.setVisible(true);
    }

    // Frame to set bidding item and base price
    private static void showBiddingItemFrame() {
        JFrame frame = new JFrame("Auction System - Bidding Item");
        frame.setLayout(new FlowLayout());
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel itemLabel = new JLabel("Enter Bidding Item:");
        JTextField itemField = new JTextField(10);
        JLabel priceLabel = new JLabel("Enter Base Price:");
        JTextField priceField = new JTextField(10);
        JButton okButton = new JButton("OK");

        frame.add(itemLabel);
        frame.add(itemField);
        frame.add(priceLabel);
        frame.add(priceField);
        frame.add(okButton);

        okButton.addActionListener(e -> {
            try {
                biddingItem = itemField.getText();
                basePrice = Double.parseDouble(priceField.getText());
                if (biddingItem.isEmpty() || basePrice <= 0) throw new NumberFormatException();
                highestBid = basePrice;
                frame.dispose();
                startBiddingFrame();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid data.");
            }
        });

        frame.setVisible(true);
    }

    // Frame for bidding process
    private static void startBiddingFrame() {
        JFrame frame = new JFrame("Auction System - Bidding");
        frame.setLayout(new BorderLayout());
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel highestBidLabel = new JLabel("Highest Bidder: None | Highest Bid: " + basePrice);
        highestBidLabel.setHorizontalAlignment(SwingConstants.CENTER);
        highestBidLabel.setForeground(Color.BLUE);

        JPanel biddingPanel = new JPanel(new GridLayout(verifiedParticipants.size(), 1));

        for (Participant participant : verifiedParticipants) {
            JPanel panel = new JPanel(new FlowLayout());
            JLabel nameLabel = new JLabel(participant.getName());
            JButton yesButton = new JButton("Bid");
            JButton noButton = new JButton("Pass");

            yesButton.addActionListener(e -> {
                String input = JOptionPane.showInputDialog(frame, "Enter your bid (Current highest: " + highestBid + "):");
                try {
                    double bid = Double.parseDouble(input);
                    if (bid > highestBid) {
                        highestBid = bid;
                        highestBidder = participant;
                        highestBidLabel.setText("Highest Bidder: " + highestBidder.getName() + " | Highest Bid: " + highestBid);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Bid must be higher than current highest.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Enter a valid bid amount.");
                }
            });

            noButton.addActionListener(e -> panel.setVisible(false));

            panel.add(nameLabel);
            panel.add(yesButton);
            panel.add(noButton);
            biddingPanel.add(panel);
        }

        frame.add(highestBidLabel, BorderLayout.NORTH);
        frame.add(biddingPanel, BorderLayout.CENTER);

        JButton finishButton = new JButton("Finish");
        finishButton.addActionListener(e -> {
            announceWinner();
            saveToFile();
            frame.dispose();
        });
        frame.add(finishButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Announce the winner
    private static void announceWinner() {
        if (highestBidder == null) {
            JOptionPane.showMessageDialog(null, "Item: " + biddingItem + " - UNSOLD");
        } else {
            JOptionPane.showMessageDialog(null, "Item: " + biddingItem + " - Sold to " + highestBidder.getName() + " for " + highestBid);
        }
    }

    // Save results to a file
    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("auction_results.txt", true))) {
            String result = "Item: " + biddingItem + " - ";
            if (highestBidder == null) {
                result += "UNSOLD";
            } else {
                result += "Sold to " + highestBidder.getName() + " for " + highestBid;
            }
            writer.write(result);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving results to file.");
        }
    }

    // Participant class
    static class Participant {
        private final String name;
        private final String password;

        public Participant(String name, String password) {
            this.name = name;
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public boolean verify(String name, String password) {
            return this.name.equals(name) && this.password.equals(password);
        }
    }
}
