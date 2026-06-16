package LMS;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class AppDashboard {
    private JFrame frame;
    private JTabbedPane tabs;
    private JTable bookTable;
    private DefaultTableModel bookModel;
    private JLabel statusLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppDashboard().show());
    }

    private void show() {
        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(880, 560);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel header = new JLabel("Library Management System", JLabel.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        frame.add(header, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.addTab("Main Menu", mainMenuPanel());
        tabs.addTab("Librarian Dashboard", librarianPanel());
        tabs.addTab("Book Management", bookManagementPanel());
        frame.add(tabs, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.setVisible(true);
        refreshBooks();
    }

    private JPanel mainMenuPanel() {
        JPanel panel = menuPanel();

        JButton login = new JButton("Login");
        login.addActionListener(event -> showLoginDialog());
        panel.add(login);

        JButton admin = new JButton("Administrative Functions");
        admin.addActionListener(event -> showAdminDialog());
        panel.add(admin);

        JButton viewBooks = new JButton("View Books");
        viewBooks.addActionListener(event -> {
            tabs.setSelectedIndex(2);
            refreshBooks();
        });
        panel.add(viewBooks);

        JButton exit = new JButton("Exit");
        exit.addActionListener(event -> frame.dispose());
        panel.add(exit);

        return panel;
    }

    private JPanel librarianPanel() {
        JPanel panel = menuPanel();

        JButton search = new JButton("Search Book");
        search.addActionListener(event -> searchBooks());
        panel.add(search);

        JButton issue = new JButton("Issue Book");
        issue.addActionListener(event -> showConsoleWorkflowNotice("Issue Book"));
        panel.add(issue);

        JButton returnBook = new JButton("Return Book");
        returnBook.addActionListener(event -> showConsoleWorkflowNotice("Return Book"));
        panel.add(returnBook);

        JButton holds = new JButton("Manage Hold Requests");
        holds.addActionListener(event -> showConsoleWorkflowNotice("Manage Hold Requests"));
        panel.add(holds);

        JButton history = new JButton("View History");
        history.addActionListener(event -> showConsoleWorkflowNotice("View History"));
        panel.add(history);

        return panel;
    }

    private JPanel bookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        bookModel = new DefaultTableModel(new Object[] {"ID", "Title", "Author", "Subject", "Issued"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(bookModel);
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton add = new JButton("Add Book");
        add.addActionListener(event -> addBook());
        buttons.add(add);

        JButton update = new JButton("Update Book");
        update.addActionListener(event -> updateBook());
        buttons.add(update);

        JButton remove = new JButton("Remove Book");
        remove.addActionListener(event -> removeBook());
        buttons.add(remove);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(event -> refreshBooks());
        buttons.add(refresh);

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel menuPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        return panel;
    }

    private void showLoginDialog() {
        JTextField id = new JTextField();
        JPasswordField password = new JPasswordField();
        JPanel form = formPanel(new String[] {"User ID", "Password"}, new java.awt.Component[] {id, password});

        int result = JOptionPane.showConfirmDialog(frame, form, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT PNAME FROM PERSON WHERE ID = ? AND PASSWORD = ?")) {
            statement.setInt(1, Integer.parseInt(id.getText().trim()));
            statement.setString(2, new String(password.getPassword()));

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    status("Logged in as " + rs.getString("PNAME"));
                    tabs.setSelectedIndex(1);
                } else {
                    showError("Invalid user ID or password.");
                }
            }
        } catch (NumberFormatException ex) {
            showError("User ID must be a number.");
        } catch (SQLException ex) {
            showError("Login failed: " + ex.getMessage());
        }
    }

    private void showAdminDialog() {
        JPasswordField password = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(frame, password, "Admin Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && "lib".equals(new String(password.getPassword()))) {
            status("Administrative access granted.");
            tabs.setSelectedIndex(1);
        } else if (result == JOptionPane.OK_OPTION) {
            showError("Wrong admin password.");
        }
    }

    private void searchBooks() {
        JTextField query = new JTextField();
        int result = JOptionPane.showConfirmDialog(frame, query, "Search by title, author, or subject", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String text = "%" + query.getText().trim() + "%";
        loadBooks("SELECT ID, TITLE, AUTHOR, SUBJECT, IS_ISSUED FROM BOOK WHERE TITLE LIKE ? OR AUTHOR LIKE ? OR SUBJECT LIKE ?",
                statement -> {
                    statement.setString(1, text);
                    statement.setString(2, text);
                    statement.setString(3, text);
                });
        tabs.setSelectedIndex(2);
    }

    private void refreshBooks() {
        if (bookModel == null) {
            return;
        }

        loadBooks("SELECT ID, TITLE, AUTHOR, SUBJECT, IS_ISSUED FROM BOOK ORDER BY ID", statement -> {
        });
    }

    private void addBook() {
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField subject = new JTextField();
        JPanel form = formPanel(new String[] {"Title", "Author", "Subject"}, new java.awt.Component[] {title, author, subject});

        int result = JOptionPane.showConfirmDialog(frame, form, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO BOOK (ID, TITLE, AUTHOR, SUBJECT, IS_ISSUED) VALUES (?, ?, ?, ?, FALSE)")) {
            statement.setInt(1, nextBookId(connection));
            statement.setString(2, title.getText().trim());
            statement.setString(3, author.getText().trim());
            statement.setString(4, subject.getText().trim());
            statement.executeUpdate();
            status("Book added.");
            refreshBooks();
        } catch (SQLException ex) {
            showError("Could not add book: " + ex.getMessage());
        }
    }

    private void updateBook() {
        int row = selectedBookRow();
        if (row < 0) {
            return;
        }

        int id = (Integer) bookModel.getValueAt(row, 0);
        JTextField title = new JTextField((String) bookModel.getValueAt(row, 1));
        JTextField author = new JTextField((String) bookModel.getValueAt(row, 2));
        JTextField subject = new JTextField((String) bookModel.getValueAt(row, 3));
        JPanel form = formPanel(new String[] {"Title", "Author", "Subject"}, new java.awt.Component[] {title, author, subject});

        int result = JOptionPane.showConfirmDialog(frame, form, "Update Book", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, SUBJECT = ? WHERE ID = ?")) {
            statement.setString(1, title.getText().trim());
            statement.setString(2, author.getText().trim());
            statement.setString(3, subject.getText().trim());
            statement.setInt(4, id);
            statement.executeUpdate();
            status("Book updated.");
            refreshBooks();
        } catch (SQLException ex) {
            showError("Could not update book: " + ex.getMessage());
        }
    }

    private void removeBook() {
        int row = selectedBookRow();
        if (row < 0) {
            return;
        }

        int id = (Integer) bookModel.getValueAt(row, 0);
        int result = JOptionPane.showConfirmDialog(frame, "Remove selected book?", "Remove Book", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM BOOK WHERE ID = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
            status("Book removed.");
            refreshBooks();
        } catch (SQLException ex) {
            showError("Could not remove book: " + ex.getMessage());
        }
    }

    private void loadBooks(String sql, SqlBinder binder) {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            try (ResultSet rs = statement.executeQuery()) {
                bookModel.setRowCount(0);
                while (rs.next()) {
                    bookModel.addRow(new Object[] {
                        rs.getInt("ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getString("SUBJECT"),
                        rs.getBoolean("IS_ISSUED")
                    });
                }
                status("Loaded " + bookModel.getRowCount() + " book(s).");
            }
        } catch (SQLException ex) {
            showError("Could not load books: " + ex.getMessage());
        }
    }

    private int nextBookId(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COALESCE(MAX(ID), 0) + 1 AS NEXT_ID FROM BOOK")) {
            rs.next();
            return rs.getInt("NEXT_ID");
        }
    }

    private int selectedBookRow() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            showError("Select a book first.");
            return -1;
        }
        return bookTable.convertRowIndexToModel(row);
    }

    private JPanel formPanel(String[] labels, java.awt.Component[] fields) {
        JPanel panel = new JPanel(new GridLayout(labels.length, 2, 8, 8));
        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i]));
            panel.add(fields[i]);
        }
        return panel;
    }

    private Connection openConnection() throws SQLException {
        DatabaseConfig config = DatabaseConfig.load();
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }

    private void showConsoleWorkflowNotice(String action) {
        JOptionPane.showMessageDialog(
                frame,
                action + " still uses the original console workflow.\nUse `ant run` for this operation.",
                "Console Workflow",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void status(String message) {
        statusLabel.setText(message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Library Management System", JOptionPane.ERROR_MESSAGE);
        status(message);
    }

    private interface SqlBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
