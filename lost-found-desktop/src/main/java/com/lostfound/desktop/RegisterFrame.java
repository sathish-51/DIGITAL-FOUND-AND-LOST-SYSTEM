package com.lostfound.desktop;
import com.lostfound.desktop.api.ApiClient;
import com.lostfound.desktop.api.AuthResult;
import javax.swing.SwingWorker;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class RegisterFrame extends JFrame {

    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField =
            new JPasswordField();
    private final JPasswordField confirmField =
            new JPasswordField();

    private final JButton createButton =
            new JButton("CREATE ACCOUNT");

    public RegisterFrame() {
        setTitle("Digital Lost & Found | Register");
        setSize(480, 590);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(75, 60, 160));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(28, 35, 28, 35));
        card.setPreferredSize(new Dimension(390, 520));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(new Color(50, 45, 95));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Join our lost and found community");
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        addField(card, "Full name", nameField);
        addField(card, "Email address", emailField);
        addField(card, "Password (minimum 8 characters)",
                passwordField);
        addField(card, "Confirm password", confirmField);

        createButton.setBackground(new Color(103, 80, 220));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        createButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42));

        JButton backButton = new JButton("Back to Login");
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(new Color(103, 80, 220));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));

        card.add(createButton);
        card.add(Box.createVerticalStrut(8));
        card.add(backButton);

        root.add(card);
        setContentPane(root);

        createButton.addActionListener(e -> register());

        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void addField(
            JPanel card, String labelText, JComponent field) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 34));
        field.setPreferredSize(new Dimension(300, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(label);
        card.add(Box.createVerticalStrut(4));
        card.add(field);
        card.add(Box.createVerticalStrut(12));
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password =
                new String(passwordField.getPassword());
        String confirm =
                new String(confirmField.getPassword());

        if (name.isBlank() || email.isBlank()
                || password.isBlank() || confirm.isBlank()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showError("Enter a valid email address.");
            return;
        }

        if (password.length() < 8
                || password.getBytes(
                    java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            showError(
                    "Password must be at least 8 characters "
                    + "and no more than 72 UTF-8 bytes.");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        createButton.setEnabled(false);
        createButton.setText("Creating account...");

        SwingWorker<AuthResult, Void> worker =
                new SwingWorker<>() {

            @Override
            protected AuthResult doInBackground()
                    throws Exception {
                return ApiClient.register(name, email, password);
            }

            @Override
            protected void done() {
                createButton.setEnabled(true);
                createButton.setText("CREATE ACCOUNT");

                try {
                    AuthResult result = get();

                    JOptionPane.showMessageDialog(
                            RegisterFrame.this,
                            result.message(),
                            "Registration Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    new LoginFrame().setVisible(true);
                    dispose();

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showError("Registration was interrupted.");

                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    showError(cause.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message == null ? "Registration failed." : message,
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
