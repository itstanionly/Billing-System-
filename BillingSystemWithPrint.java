import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BillingSystemWithPrint extends JFrame {
    private JTextField itemNameField, itemPriceField, itemQtyField;
    private JTextField customerNameField, customerPhoneField;
    private JButton addButton, generateBillButton, printButton;
    private ArrayList<String> billItems = new ArrayList<>();
    private ArrayList<Double> itemTotals = new ArrayList<>();
    private double total = 0;
    private ReceiptPanel receiptPanel;

    private String customerName = "";
    private String customerPhone = "";

    public BillingSystemWithPrint() {
        setTitle("Billing System with Print Feature");
        setSize(700, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Customer Panel
        JPanel customerPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        customerNameField = new JTextField();
        customerPhoneField = new JTextField();
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        customerPanel.add(new JLabel("Customer Name:"));
        customerPanel.add(customerNameField);
        customerPanel.add(new JLabel("Phone Number:"));
        customerPanel.add(customerPhoneField);
        add(customerPanel, BorderLayout.NORTH);

        // Item Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        itemNameField = new JTextField();
        itemPriceField = new JTextField();
        itemQtyField = new JTextField();
        inputPanel.setBorder(BorderFactory.createTitledBorder("Item Details"));
        inputPanel.add(new JLabel("Item Name:"));
        inputPanel.add(itemNameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(itemPriceField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(itemQtyField);
        add(inputPanel, BorderLayout.WEST);

        // Buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Item");
        generateBillButton = new JButton("Generate Bill");
        printButton = new JButton("Print Bill");
        buttonPanel.add(addButton);
        buttonPanel.add(generateBillButton);
        buttonPanel.add(printButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Receipt Panel
        receiptPanel = new ReceiptPanel();
        add(receiptPanel, BorderLayout.CENTER);

        // Actions
        addButton.addActionListener(e -> addItem());
        generateBillButton.addActionListener(e -> {
            customerName = customerNameField.getText().trim();
            customerPhone = customerPhoneField.getText().trim();
            receiptPanel.repaint();
        });
        printButton.addActionListener(e -> printReceipt());

        setVisible(true);
    }

    private void addItem() {
        String name = itemNameField.getText().trim();
        String priceText = itemPriceField.getText().trim();
        String qtyText = itemQtyField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty() || qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int qty = Integer.parseInt(qtyText);
            double subtotal = price * qty;
            total += subtotal;
            billItems.add(String.format("%s (%dx₹%.2f)", name, qty, price));
            itemTotals.add(subtotal);
            itemNameField.setText("");
            itemPriceField.setText("");
            itemQtyField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format.");
        }
    }

    private void printReceipt() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName("Print Receipt");

        printerJob.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            receiptPanel.paint(g2d);
            return Printable.PAGE_EXISTS;
        });

        boolean doPrint = printerJob.printDialog();
        if (doPrint) {
            try {
                printerJob.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Print Failed: " + ex.getMessage());
            }
        }
    }

    class ReceiptPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
            int y = 30;

            // Decorative bow
            g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
            g2d.setColor(Color.PINK);
            g2d.drawString("🎀", 280, y); // A bow emoji (may not print)
            y += 40;

            g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2d.setColor(Color.BLACK);
            g2d.drawString("JAVA MINI SHOP", 200, y); y += 30;

            g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
            g2d.drawString("----------------------------------------", 50, y); y += 20;

            // Date and Time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            g2d.drawString("Date/Time : " + now.format(formatter), 50, y); y += 20;

            // Customer Info
            g2d.drawString("Customer  : " + customerName, 50, y); y += 20;
            g2d.drawString("Phone     : " + customerPhone, 50, y); y += 20;
            g2d.drawString("----------------------------------------", 50, y); y += 20;

            // Items
            for (int i = 0; i < billItems.size(); i++) {
                g2d.drawString(billItems.get(i) + " = ₹" + String.format("%.2f", itemTotals.get(i)), 50, y);
                y += 20;
            }

            g2d.drawString("----------------------------------------", 50, y); y += 20;

            // Total
            g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
            g2d.drawString("TOTAL: ₹" + String.format("%.2f", total), 50, y); y += 30;

            // Footer
            g2d.setFont(new Font("SansSerif", Font.ITALIC, 14));
            g2d.drawString("Thank you for shopping with us!", 150, y);
        }
    }

    public static void main(String[] args) {
        new BillingSystemWithPrint();
    }
}
