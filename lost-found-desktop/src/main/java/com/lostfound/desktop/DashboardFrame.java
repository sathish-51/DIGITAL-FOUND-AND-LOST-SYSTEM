
package com.lostfound.desktop;

import com.lostfound.desktop.api.AuthResult;
import com.lostfound.desktop.api.DashboardApi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;

public class DashboardFrame extends JFrame {

    private final AuthResult user;

    private final JPanel content = new JPanel(new BorderLayout(16, 16));
    private final JLabel heading = new JLabel("Dashboard");
    private final JLabel status = new JLabel("Ready");

    private final Color navy = new Color(30, 35, 75);
    private final Color navyLight = new Color(43, 51, 99);
    private final Color purple = new Color(104, 75, 220);
    private final Color purpleLight = new Color(239, 235, 255);
    private final Color background = new Color(245, 247, 253);
    private final Color textDark = new Color(40, 45, 65);
    private final Color textMuted = new Color(105, 112, 132);
    private final Color borderColor = new Color(224, 228, 240);
    private final Color green = new Color(30, 150, 105);
    private final Color red = new Color(220, 75, 85);
    private final Color orange = new Color(230, 145, 35);
    private final Color blue = new Color(55, 125, 220);

    private final Font normalFont =
            new Font("Segoe UI", Font.PLAIN, 15);

    private final Font buttonFont =
            new Font("Segoe UI", Font.BOLD, 14);

    private final Font titleFont =
            new Font("Segoe UI", Font.BOLD, 27);

    public DashboardFrame(AuthResult user) {
        this.user = Objects.requireNonNull(user, "User cannot be null");

        setTitle("FindIt - Digital Lost & Found System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(760, 500));
        setResizable(true);
        setLocationRelativeTo(null);

        getContentPane().setBackground(background);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);

        showDashboard();
    }

    // --------------------------------------------------
    // SIDEBAR
    // --------------------------------------------------

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(navy);
        sidebar.setPreferredSize(new Dimension(225, 0));
        sidebar.setBorder(new EmptyBorder(22, 14, 18, 14));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("  FINDIT");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 29));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("  Lost & Found Portal");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(205, 211, 240));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(30));

        addNav(sidebar, "Dashboard", purple, this::showDashboard);
        addNav(sidebar, "Lost Items", red, () -> showItems("LOST"));
        addNav(sidebar, "Found Items", green, () -> showItems("FOUND"));
        addNav(sidebar, "My Claims", blue, this::showClaims);
        addNav(sidebar, "Notifications", orange, this::showNotifications);
        addNav(sidebar, "My Profile", new Color(135, 105, 220),
                this::showProfile);

        sidebar.add(Box.createVerticalGlue());

        JPanel userCard = new JPanel();
        userCard.setOpaque(true);
        userCard.setBackground(navyLight);
        userCard.setBorder(new EmptyBorder(12, 10, 12, 10));
        userCard.setLayout(new BorderLayout(8, 4));
        userCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        userCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userName = new JLabel(
                escapeHtml(safe(user.fullName())));
        userName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userName.setForeground(Color.WHITE);

        JLabel userRole = new JLabel("Logged in");
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userRole.setForeground(new Color(210, 216, 240));

        JPanel userText = new JPanel();
        userText.setOpaque(false);
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        userText.add(userName);
        userText.add(Box.createVerticalStrut(5));
        userText.add(userRole);

        userCard.add(userText, BorderLayout.CENTER);
        sidebar.add(userCard);
        sidebar.add(Box.createVerticalStrut(12));

        addNav(sidebar, "Logout", red, () -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        return sidebar;
    }

    private void addNav(
            JPanel sidebar,
            String text,
            Color accent,
            Runnable action) {

        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setForeground(Color.WHITE);
        button.setBackground(navy);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 14, 12, 8));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(navyLight);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 4, 0, 0, accent),
                        new EmptyBorder(12, 10, 12, 8)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(navy);
                button.setBorder(new EmptyBorder(12, 14, 12, 8));
            }
        });

        button.addActionListener(e -> action.run());

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    // --------------------------------------------------
    // MAIN PANEL
    // --------------------------------------------------

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(16, 18));
        main.setBackground(background);
        main.setBorder(new EmptyBorder(22, 24, 16, 24));

        JPanel top = new JPanel(new BorderLayout(12, 8));
        top.setOpaque(false);

        heading.setFont(titleFont);
        heading.setForeground(navy);

        JLabel greeting = new JLabel(
                "Welcome, " + safe(user.fullName()));
        greeting.setFont(normalFont);
        greeting.setForeground(purple);
        greeting.setHorizontalAlignment(SwingConstants.RIGHT);

        top.add(heading, BorderLayout.WEST);
        top.add(greeting, BorderLayout.EAST);

        content.setOpaque(false);

        status.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        status.setForeground(textMuted);
        status.setBorder(new EmptyBorder(5, 2, 0, 2));

        main.add(top, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        main.add(status, BorderLayout.SOUTH);

        return main;
    }

    private void prepare(String title) {
        heading.setText(title);
        content.removeAll();
        status.setText("Connected to Digital Lost & Found API");
    }

    private void refreshContent() {
        content.revalidate();
        content.repaint();
    }

    // --------------------------------------------------
    // REUSABLE UI COMPONENTS
    // --------------------------------------------------

    
private JButton makeButton(String text, Color color) {
    JButton button = new JButton(text);

    button.setFont(new Font("Segoe UI", Font.BOLD, 15));
    button.setForeground(Color.WHITE);
    button.setBackground(color);

    button.setOpaque(true);
    button.setContentAreaFilled(true);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setBorder(new EmptyBorder(12, 18, 12, 18));
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    button.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            button.setBackground(color.darker());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            button.setBackground(color);
        }
    });

    return button;
}

    private JPanel createCard(
            String title,
            String value,
            Color accent,
            String description) {

        JPanel panel = new JPanel(new BorderLayout(8, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(16, 17, 16, 17)));

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setOpaque(false);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(textDark);

        JLabel marker = new JLabel("●");
        marker.setFont(new Font("Segoe UI", Font.BOLD, 18));
        marker.setForeground(accent);

        top.add(label, BorderLayout.CENTER);
        top.add(marker, BorderLayout.EAST);

        JLabel number = new JLabel(value);
        number.setFont(new Font("Segoe UI", Font.BOLD, 32));
        number.setForeground(accent);

        JLabel detail = new JLabel(description);
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detail.setForeground(textMuted);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(number);
        center.add(Box.createVerticalStrut(4));
        center.add(detail);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private JLabel profileLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textDark);
        label.setFont(normalFont);
        return label;
    }

    private JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textDark);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

private void styleTable(JTable table) {
    table.setFont(normalFont);
    table.setForeground(textDark);
    table.setBackground(Color.WHITE);
    table.setSelectionBackground(new Color(220, 215, 255));
    table.setSelectionForeground(navy);
    table.setGridColor(borderColor);
    table.setRowHeight(38);
    table.setShowGrid(true);
    table.setFillsViewportHeight(true);
    table.setAutoCreateRowSorter(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JTableHeader header = table.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    header.setPreferredSize(new Dimension(0, 44));
    header.setReorderingAllowed(false);
    header.setOpaque(true);

    header.setDefaultRenderer((tbl, value, isSelected,
            hasFocus, row, column) -> {

        JLabel label = new JLabel(
                value == null ? "" : value.toString());

        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setBackground(navy);
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 2, 1, purple),
                new EmptyBorder(8, 6, 8, 6)));

        return label;
    });

    header.repaint();
}


    private JScrollPane createScrollPane(Component component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(BorderFactory.createLineBorder(borderColor));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);
        return panel;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message == null ? "An error occurred." : message,
                "Something went wrong",
                JOptionPane.ERROR_MESSAGE);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String escapeHtml(String value) {
        return safe(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    // --------------------------------------------------
    // DASHBOARD HOME
    // --------------------------------------------------

    private void showDashboard() {
        prepare("Dashboard");

        JPanel page = createPagePanel();

        JPanel stats = new JPanel(new GridLayout(0, 2, 14, 14));
        stats.setOpaque(false);

        try {
            int lost = DashboardApi.items("LOST").size();
            int found = DashboardApi.items("FOUND").size();
            int claims = DashboardApi.claims(user.userId()).size();
            int notifications =
                    DashboardApi.notifications(user.userId()).size();

            stats.add(createCard(
                    "Lost Reports",
                    String.valueOf(lost),
                    red,
                    "Items reported missing"));

            stats.add(createCard(
                    "Found Items",
                    String.valueOf(found),
                    green,
                    "Items waiting to be reunited"));

            stats.add(createCard(
                    "My Claims",
                    String.valueOf(claims),
                    purple,
                    "Claims submitted by you"));

            stats.add(createCard(
                    "Notifications",
                    String.valueOf(notifications),
                    orange,
                    "Updates for your account"));

        } catch (Exception ex) {
            stats.add(createCard("Lost Reports", "—", red,
                    "Unable to load statistics"));
            stats.add(createCard("Found Items", "—", green,
                    "Unable to load statistics"));
            stats.add(createCard("My Claims", "—", purple,
                    "Unable to load statistics"));
            stats.add(createCard("Notifications", "—", orange,
                    "Unable to load statistics"));

            status.setText("Could not load dashboard: " + ex.getMessage());
        }

        JPanel welcome = new JPanel();
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        welcome.setBackground(Color.WHITE);
        welcome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(20, 22, 20, 22)));

        JLabel welcomeTitle = new JLabel("Welcome to FindIt!");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 23));
        welcomeTitle.setForeground(navy);

        JLabel description = new JLabel(
                "<html><div style='width:430px'>"
                + "Report missing belongings, browse found items, "
                + "and help reunite people with their belongings."
                + "</div></html>");
        description.setFont(normalFont);
        description.setForeground(textMuted);

        welcome.add(welcomeTitle);
        welcome.add(Box.createVerticalStrut(10));
        welcome.add(description);
        welcome.add(Box.createVerticalStrut(18));

        JPanel quickActions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        quickActions.setOpaque(false);

        JButton lostButton = makeButton("+ Report Lost Item", red);
        lostButton.addActionListener(e -> showItems("LOST"));

        JButton foundButton = makeButton("+ Report Found Item", green);
        foundButton.addActionListener(e -> showItems("FOUND"));

        JButton browseButton = makeButton("Browse Found Items", purple);
        browseButton.addActionListener(e -> showItems("FOUND"));

        quickActions.add(lostButton);
        quickActions.add(foundButton);
        quickActions.add(browseButton);

        welcome.add(quickActions);

        page.add(stats, BorderLayout.NORTH);
        page.add(welcome, BorderLayout.CENTER);

        content.add(page, BorderLayout.CENTER);
        refreshContent();
    }

    // --------------------------------------------------
    // LOST AND FOUND ITEMS
    // --------------------------------------------------

    private void showItems(String type) {
        
        boolean foundPage = "FOUND".equals(type);

        prepare(foundPage ? "Found Items" : "Lost Items");

        JPanel page = createPagePanel();

        JLabel info = new JLabel(
                foundPage
                        ? "Browse found belongings. Select an item to submit a claim."
                        : "Browse missing belongings or report an item you lost.");

        info.setFont(normalFont);
        info.setForeground(textMuted);

        JButton reportButton = makeButton(
                foundPage ? "+ Report Found Item" : "+ Report Lost Item",
                foundPage ? green : red);

        reportButton.addActionListener(e -> reportItem(type));

        JPanel toolbar = new JPanel(new BorderLayout(10, 10));
        toolbar.setOpaque(false);
        toolbar.add(info, BorderLayout.CENTER);
        toolbar.add(reportButton, BorderLayout.EAST);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                    "ID", "Item Name", "Category",
                    "Description", "Location", "Date", "Status"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(115);
        table.getColumnModel().getColumn(3).setPreferredWidth(230);
        table.getColumnModel().getColumn(4).setPreferredWidth(170);
        table.getColumnModel().getColumn(5).setPreferredWidth(115);
        table.getColumnModel().getColumn(6).setPreferredWidth(95);

        try {
            List<Map<String, Object>> items = DashboardApi.items(type);

            for (Map<String, Object> item : items) {
                model.addRow(new Object[]{
                    item.get("itemId"),
                    item.get("itemName"),
                    item.get("category"),
                    item.get("description"),
                    item.get("location"),
                    item.get("itemDate"),
                    item.get("status")
                });
            }

            status.setText(items.size() + " " +
                    (foundPage ? "found items" : "lost items") +
                    " available");

        } catch (Exception ex) {
            status.setText("Could not load items: " + ex.getMessage());
        }

        JPanel bottom = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 4));
        bottom.setOpaque(false);

        if (foundPage) {
            JButton claimButton =
                    makeButton("Claim Selected Item", purple);

            claimButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();

                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please select a found item first.",
                            "Select an Item",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int row = table.convertRowIndexToModel(selectedRow);

                Object statusValue = model.getValueAt(row, 6);
                if (statusValue == null
                        || !"FOUND".equalsIgnoreCase(
                                statusValue.toString())) {
                    showError("Only FOUND items can be claimed.");
                    return;
                }

                long itemId = Long.parseLong(
                        model.getValueAt(row, 0).toString());

                String itemName = String.valueOf(
                        model.getValueAt(row, 1));

                JTextArea messageArea = new JTextArea(5, 30);
                messageArea.setFont(normalFont);
                messageArea.setLineWrap(true);
                messageArea.setWrapStyleWord(true);
                messageArea.setBorder(new EmptyBorder(8, 8, 8, 8));

                JPanel claimForm = new JPanel(new BorderLayout(8, 8));
                claimForm.setPreferredSize(new Dimension(390, 190));
                claimForm.add(
                        formLabel("Item: " + itemName),
                        BorderLayout.NORTH);
                claimForm.add(
                        createScrollPane(messageArea),
                        BorderLayout.CENTER);
                claimForm.add(
                        formLabel("Explain why this item belongs to you."),
                        BorderLayout.SOUTH);

                int choice = JOptionPane.showConfirmDialog(
                        this,
                        claimForm,
                        "Submit Claim",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (choice != JOptionPane.OK_OPTION) {
                    return;
                }

                String message = messageArea.getText().trim();

                if (message.isBlank()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please explain why you are claiming this item.",
                            "Claim Message Required",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                claimButton.setEnabled(false);
                claimButton.setText("Submitting...");

                try {
                    DashboardApi.createClaim(
                            user.userId(), itemId, message);

                    JOptionPane.showMessageDialog(
                            this,
                            "Your claim has been submitted successfully!",
                            "Claim Submitted",
                            JOptionPane.INFORMATION_MESSAGE);

                    showItems("FOUND");

                } catch (Exception ex) {
                    showError("Could not submit claim:\n"
                            + ex.getMessage());

                    claimButton.setEnabled(true);
                    claimButton.setText("Claim Selected Item");
                }
            });

            bottom.add(claimButton);
        }

        JLabel count = new JLabel(
                model.getRowCount() + " item(s)");
        count.setFont(new Font("Segoe UI", Font.BOLD, 14));
        count.setForeground(textMuted);
        bottom.add(count);

        page.add(toolbar, BorderLayout.NORTH);
        page.add(createScrollPane(table), BorderLayout.CENTER);
        page.add(bottom, BorderLayout.SOUTH);

        content.add(page, BorderLayout.CENTER);
        refreshContent();
    }

    // --------------------------------------------------
    // REPORT LOST / FOUND ITEM FORM
    // --------------------------------------------------

    private void reportItem(String type) {
        boolean lost = "LOST".equals(type);

        JTextField nameField = new JTextField(25);
        JTextField categoryField = new JTextField(25);
        JTextField locationField = new JTextField(25);
        JTextField dateField =
                new JTextField(LocalDate.now().toString(), 25);

        JTextArea descriptionArea = new JTextArea(5, 25);
        descriptionArea.setFont(normalFont);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        styleInput(nameField);
        styleInput(categoryField);
        styleInput(locationField);
        styleInput(dateField);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(12, 14, 12, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        addFormRow(form, gbc, row++, "Item Name *", nameField);
        addFormRow(form, gbc, row++, "Category *", categoryField);
        addFormRow(form, gbc, row++, "Location *", locationField);
        addFormRow(form, gbc, row++, "Date (YYYY-MM-DD)",
                dateField);
        addFormRow(form, gbc, row++, "Description",
                createScrollPane(descriptionArea));

        JLabel required = new JLabel("* Required fields");
        required.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        required.setForeground(textMuted);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        form.add(required, gbc);

        JScrollPane formScroll = createScrollPane(form);
        formScroll.setPreferredSize(new Dimension(490, 430));

        int result = JOptionPane.showConfirmDialog(
                this,
                formScroll,
                lost ? "Report a Lost Item" : "Report a Found Item",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String location = locationField.getText().trim();
        String dateText = dateField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isBlank() || category.isBlank()
                || location.isBlank()) {
            showError("Item name, category and location are required.");
            return;
        }

        LocalDate itemDate = null;

        if (!dateText.isBlank()) {
            try {
                itemDate = LocalDate.parse(dateText);
            } catch (DateTimeParseException ex) {
                showError("Enter a valid date in YYYY-MM-DD format.");
                return;
            }
        }

        Map<String, Object> item = new HashMap<>();
        item.put("itemName", name);
        item.put("category", category);
        item.put("location", location);
        item.put("description", description);
        item.put("itemDate", itemDate == null
                ? null : itemDate.toString());

        try {
            DashboardApi.createItem(user.userId(), type, item);

            JOptionPane.showMessageDialog(
                    this,
                    lost
                            ? "Your lost-item report was saved!"
                            : "Your found-item report was saved!",
                    "Report Saved",
                    JOptionPane.INFORMATION_MESSAGE);

            showItems(type);

        } catch (Exception ex) {
            showError("Could not save the report:\n"
                    + ex.getMessage());
        }
    }

    private void addFormRow(
            JPanel form,
            GridBagConstraints gbc,
            int row,
            String labelText,
            Component input) {

        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 0;

        form.add(formLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;

        form.add(input, gbc);
    }

    private void styleInput(JTextField field) {
        field.setFont(normalFont);
        field.setForeground(textDark);
        field.setBackground(Color.WHITE);
        field.setCaretColor(purple);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
    }

    // --------------------------------------------------
    // MY CLAIMS
    // --------------------------------------------------

    private void showClaims() {
        showDataPage(
                "My Claims",
                () -> DashboardApi.claims(user.userId()),
                new String[]{
                    "claim_id", "claimId",
                    "item_id", "itemId",
                    "item_name", "itemName",
                    "claim_message", "claimMessage",
                    "status", "created_at", "createdAt"
                });
    }

    // --------------------------------------------------
    // NOTIFICATIONS
    // --------------------------------------------------

    private void showNotifications() {
        showDataPage(
                "Notifications",
                () -> DashboardApi.notifications(user.userId()),
                new String[]{
                    "notification_id", "notificationId",
                    "message", "is_read", "isRead", "created_at", "createdAt"
                });
    }

    private void showDataPage(
            String title,
            Callable<List<Map<String, Object>>> loader,
            String[] preferredColumns) {

        prepare(title);

        JPanel page = createPagePanel();

        JLabel info = new JLabel(
                "Your " + title.toLowerCase()
                        + " and their latest details");
        info.setFont(normalFont);
        info.setForeground(textMuted);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        try {
            List<Map<String, Object>> rows = loader.call();

            LinkedHashSet<String> keys = new LinkedHashSet<>();

            if (!rows.isEmpty()) {
                Map<String, Object> first = rows.get(0);

                for (String preferred : preferredColumns) {
                    if (first.containsKey(preferred)) {
                        keys.add(preferred);
                    }
                }

                keys.addAll(first.keySet());
            }

            if (keys.isEmpty()) {
                keys.add("Information");
                model.addColumn("Information");
                model.addRow(new Object[]{
                    "No " + title.toLowerCase() + " yet."
                });
            } else {
                for (String key : keys) {
                    model.addColumn(prettyColumnName(key));
                }

                for (Map<String, Object> item : rows) {
                    Object[] values = new Object[keys.size()];
                    int index = 0;

                    for (String key : keys) {
                        values[index++] = item.get(key);
                    }

                    model.addRow(values);
                }
            }

            status.setText(rows.size() + " record(s)");

        } catch (Exception ex) {
            model.addColumn("Information");
            model.addRow(new Object[]{
                "Could not load data: " + ex.getMessage()
            });

            status.setText("Could not load data: " + ex.getMessage());
        }

        page.add(info, BorderLayout.NORTH);
        page.add(createScrollPane(table), BorderLayout.CENTER);

        content.add(page, BorderLayout.CENTER);
        refreshContent();
    }

    private String prettyColumnName(String key) {
        String spaced = key.replaceAll("([a-z])([A-Z])", "$1 $2")
                .replace('_', ' ');

        String[] words = spaced.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (result.length() > 0) {
                result.append(' ');
            }

            result.append(Character.toUpperCase(word.charAt(0)));

            if (word.length() > 1) {
                result.append(word.substring(1));
            }
        }

        return result.toString();
    }

    // --------------------------------------------------
    // MY PROFILE
    // --------------------------------------------------

    private void showProfile() {
        prepare("My Profile");

        try {
            Map<String, Object> profile =
                    DashboardApi.profile(user.userId());

            JPanel card = new JPanel(new BorderLayout(12, 18));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    new EmptyBorder(24, 26, 24, 26)));

            JPanel header = new JPanel();
            header.setOpaque(false);
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

            JLabel avatar = new JLabel("  USER PROFILE  ");
            avatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
            avatar.setForeground(Color.WHITE);
            avatar.setOpaque(true);
            avatar.setBackground(purple);
            avatar.setBorder(new EmptyBorder(9, 12, 9, 12));
            avatar.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel title = new JLabel("Account Information");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(navy);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);

            header.add(avatar);
            header.add(Box.createVerticalStrut(15));
            header.add(title);

            JPanel details = new JPanel(new GridLayout(0, 1, 8, 12));
            details.setOpaque(false);
            details.setBorder(new EmptyBorder(10, 0, 10, 0));

            details.add(profileLabel(
                    "Full Name: " + safe(stringValue(
                            profile.get("full_name")))));

            details.add(profileLabel(
                    "Email: " + safe(stringValue(
                            profile.get("email")))));

            details.add(profileLabel(
                    "Role: " + safe(stringValue(
                            profile.get("role")))));

            details.add(profileLabel(
                    "Account Status: " + safe(stringValue(
                            profile.get("account_status")))));

            card.add(header, BorderLayout.NORTH);
            card.add(details, BorderLayout.CENTER);

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.add(card, BorderLayout.NORTH);

            content.add(createScrollPane(wrapper), BorderLayout.CENTER);

        } catch (Exception ex) {
            showError("Could not load profile:\n" + ex.getMessage());
        }

        refreshContent();
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString();
    }
}
