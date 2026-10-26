package com.lostfound.desktop;
import com.lostfound.desktop.api.ApiClient;
import com.lostfound.desktop.api.AuthResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField =
            new JPasswordField();

    private final JButton loginButton =
            new JButton("SIGN IN");

    public LoginFrame() {
        setTitle("Digital Lost & Found | Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        GradientPanel background = new GradientPanel();
        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(35, 40, 35, 40));
        card.setPreferredSize(new Dimension(390, 430));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(40, 40, 75));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setForeground(Color.GRAY);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        styleField(emailField);
        styleField(passwordField);

        JLabel emailLabel = makeLabel("Email address");
        JLabel passwordLabel = makeLabel("Password");

        loginButton.setBackground(new Color(103, 80, 220));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44));

        JButton registerButton =
                new JButton("Create a new account");
        registerButton.setForeground(new Color(103, 80, 220));
        registerButton.setFont(
                new Font("Segoe UI", Font.BOLD, 13));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footer = new JLabel(
                "Your journey to reuniting lost belongings starts here.");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(Color.GRAY);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);
        card.add(Box.createVerticalStrut(18));
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(24));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(registerButton);
        card.add(Box.createVerticalStrut(15));
        card.add(footer);

        background.add(card);
        setContentPane(background);

        loginButton.addActionListener(e -> login());

        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        getRootPane().setDefaultButton(loginButton);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(60, 60, 80));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(300, 40));
        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both email and password.",
                    "Missing Details",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");

        SwingWorker<AuthResult, Void> worker =
                new SwingWorker<>() {

            @Override
            protected AuthResult doInBackground()
                    throws Exception {
                return ApiClient.login(email, password);
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("SIGN IN");

                try {
                    AuthResult result = get();

                    if (result.success()) {
                        JOptionPane.showMessageDialog(
                                LoginFrame.this,
                                "Welcome, " + result.fullName() + "!",
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE);

                        dispose();
                        new DashboardFrame(result).setVisible(true);
                    }

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showError("Login was interrupted.");

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
                message == null ? "Login failed." : message,
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
    }

    private static class GradientPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            GradientPaint paint = new GradientPaint(
                    0, 0, new Color(39, 45, 110),
                    getWidth(), getHeight(),
                    new Color(135, 70, 200));

            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
