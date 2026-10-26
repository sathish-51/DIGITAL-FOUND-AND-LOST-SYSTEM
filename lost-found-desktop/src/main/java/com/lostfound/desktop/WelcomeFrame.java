package com.lostfound.desktop;
import com.lostfound.desktop.api.AuthResult;
import com.lostfound.desktop.api.ApiClient;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WelcomeFrame extends JFrame {

    public WelcomeFrame(AuthResult user) {
        setTitle("Digital Lost & Found | Home");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(241, 239, 255));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(45, 50, 45, 50));

        JLabel title = new JLabel("DIGITAL LOST & FOUND");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(92, 65, 190));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcome = new JLabel(
                "Welcome, " + user.fullName() + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 23));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel email = new JLabel(user.email());
        email.setForeground(Color.GRAY);
        email.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel message = new JLabel(
                "Your account is ready. Let's reunite "
                + "people with their belongings!");
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logout = new JButton("LOG OUT");
        logout.setBackground(new Color(103, 80, 220));
        logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(30));
        card.add(welcome);
        card.add(Box.createVerticalStrut(10));
        card.add(email);
        card.add(Box.createVerticalStrut(25));
        card.add(message);
        card.add(Box.createVerticalStrut(35));
        card.add(logout);

        panel.add(card);
        setContentPane(panel);

        logout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }
}
