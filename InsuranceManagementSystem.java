import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

// -------------------------------------------------------------
// Case Study 59: Insurance Policy & Claims Management System
// -------------------------------------------------------------

class InvalidPolicyException extends Exception {
    public InvalidPolicyException(String message) { super(message); }
}

class InvalidClaimException extends Exception {
    public InvalidClaimException(String message) { super(message); }
}

// 1. Customer Model Class
class Customer {
    private String customerId, name, email, phone, address;

    public Customer(String id, String name, String email, String phone, String address) {
        this.customerId = id; this.name = name; this.email = email; this.phone = phone; this.address = address;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    @Override
    public String toString() { return customerId + " - " + name; }
}

// 2. Abstract Class: Policy (Demonstrating Abstraction)
abstract class Policy {
    protected String policyNumber, customerId, customerName, policyType, issueDate;
    protected double coverageAmount;
    protected int durationYears;

    public Policy(String no, String cid, String cname, String type, double cov, int dur, String date) {
        this.policyNumber = no; this.customerId = cid; this.customerName = cname;
        this.policyType = type; this.coverageAmount = cov; this.durationYears = dur; this.issueDate = date;
    }

    // Method Overriding: calculatePremium() overridden in subclasses
    public abstract double calculatePremium();

    public String getPolicyNumber() { return policyNumber; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getPolicyType() { return policyType; }
    public double getCoverageAmount() { return coverageAmount; }
    public int getDurationYears() { return durationYears; }
    public String getIssueDate() { return issueDate; }

    public void setCoverageAmount(double coverageAmount) { this.coverageAmount = coverageAmount; }
    public void setDurationYears(int durationYears) { this.durationYears = durationYears; }

    @Override
    public String toString() { return policyNumber + " (" + policyType + ") - " + customerName; }
}

// Subclass 1: Life Insurance
class LifePolicy extends Policy {
    private int age;
    private boolean isSmoker;

    public LifePolicy(String no, String cid, String cname, double cov, int dur, String date, int age, boolean isSmoker) {
        super(no, cid, cname, "Life", cov, dur, date);
        this.age = age;
        this.isSmoker = isSmoker;
    }

    @Override
    public double calculatePremium() {
        double base = coverageAmount * 0.025;
        double ageFactor = (age > 40) ? (age - 40) * 150.0 : 0.0;
        double smokerCharge = isSmoker ? (base * 0.20) : 0.0;
        return Math.round((base + ageFactor + smokerCharge) * 100.0) / 100.0;
    }
}

// Subclass 2: Health Insurance
class HealthPolicy extends Policy {
    private int age;
    private boolean hasPreExisting;

    public HealthPolicy(String no, String cid, String cname, double cov, int dur, String date, int age, boolean hasPreExisting) {
        super(no, cid, cname, "Health", cov, dur, date);
        this.age = age;
        this.hasPreExisting = hasPreExisting;
    }

    @Override
    public double calculatePremium() {
        double base = coverageAmount * 0.035;
        double diseaseFactor = hasPreExisting ? 2500.0 : 0.0;
        double ageFactor = (age > 50) ? (age - 50) * 180.0 : 0.0;
        return Math.round((base + diseaseFactor + ageFactor) * 100.0) / 100.0;
    }
}

// Subclass 3: Vehicle Insurance
class VehiclePolicy extends Policy {
    private String vehicleType, vehicleNumber;
    private int vehicleAge;

    public VehiclePolicy(String no, String cid, String cname, double cov, int dur, String date, String vtype, String vnum, int vage) {
        super(no, cid, cname, "Vehicle", cov, dur, date);
        this.vehicleType = vtype;
        this.vehicleNumber = vnum;
        this.vehicleAge = vage;
    }

    @Override
    public double calculatePremium() {
        double rate = "Two-Wheeler".equalsIgnoreCase(vehicleType) ? 0.025 : 
                     ("Commercial".equalsIgnoreCase(vehicleType) ? 0.055 : 0.04);
        double base = coverageAmount * rate;
        double ageDepreciation = vehicleAge * 300.0;
        return Math.round((base + ageDepreciation) * 100.0) / 100.0;
    }
}

// 3. Claim Model Class
class Claim {
    private String claimId, policyNumber, customerId, reason, claimDate, status, documentDetails;
    private double claimAmount;

    public Claim(String id, String pno, String cid, double amt, String rsn, String date, String status, String docs) {
        this.claimId = id; this.policyNumber = pno; this.customerId = cid; this.claimAmount = amt;
        this.reason = rsn; this.claimDate = date; this.status = status; this.documentDetails = docs;
    }

    public String getClaimId() { return claimId; }
    public String getPolicyNumber() { return policyNumber; }
    public String getCustomerId() { return customerId; }
    public double getClaimAmount() { return claimAmount; }
    public String getReason() { return reason; }
    public String getClaimDate() { return claimDate; }
    public String getStatus() { return status; }
    public String getDocumentDetails() { return documentDetails; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return claimId + " (" + status + ") - Rs. " + claimAmount; }
}

// 4. Premium Payment Model Class
class Premium {
    private String paymentId, policyNumber, paymentDate, paymentMethod;
    private double amountPaid;

    public Premium(String pid, String pno, double amt, String date, String method) {
        this.paymentId = pid; this.policyNumber = pno; this.amountPaid = amt;
        this.paymentDate = date; this.paymentMethod = method;
    }

    public String getPaymentId() { return paymentId; }
    public String getPolicyNumber() { return policyNumber; }
    public double getAmountPaid() { return amountPaid; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
}

// =============================================================
// MAIN SWING APPLICATION (GUI)
// =============================================================
public class InsuranceManagementSystem extends JFrame {

    // Collections required by Case Study
    private ArrayList<Policy> policyList = new ArrayList<>();
    private LinkedList<Claim> claimHistory = new LinkedList<>();
    private HashMap<String, Policy> policyMap = new HashMap<>();
    private TreeMap<Double, List<Policy>> policiesByPremium = new TreeMap<>();
    private ArrayList<Customer> customerList = new ArrayList<>();
    private ArrayList<Premium> paymentList = new ArrayList<>();

    // Formatting utilities
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // UI Controls
    private DefaultTableModel custModel, policyModel, claimModel, payModel, searchModel;
    private JComboBox<String> cbPolicyCust, cbPolicyType, cbClaimPolicy, cbPayPolicy;
    private JLabel lblTotalPol, lblTotalCov, lblTotalClaims, lblTotalApproved, lblTotalPaid;
    private JTextArea txtTreeMapArea;

    public InsuranceManagementSystem() {
        super("Insurance Policy & Claims Management System - Case Study 59");
        seedSampleData();
        buildGUI();
    }

    private void seedSampleData() {
        Customer c1 = new Customer("C101", "Aarav Sharma", "aarav@gmail.com", "9876543210", "MG Road, Bengaluru");
        Customer c2 = new Customer("C102", "Priya Verma", "priya@gmail.com", "9123456780", "Park Street, Kolkata");
        Customer c3 = new Customer("C103", "Rohan Mehta", "rohan@gmail.com", "9988776655", "Ring Road, Mumbai");
        customerList.add(c1); customerList.add(c2); customerList.add(c3);

        LifePolicy p1 = new LifePolicy("POL-101", c1.getCustomerId(), c1.getName(), 1000000.0, 20, "2024-01-15", 35, false);
        HealthPolicy p2 = new HealthPolicy("POL-102", c2.getCustomerId(), c2.getName(), 500000.0, 5, "2024-03-10", 45, true);
        VehiclePolicy p3 = new VehiclePolicy("POL-103", c3.getCustomerId(), c3.getName(), 600000.0, 3, "2024-05-20", "Car", "MH-01-AB-1234", 2);
        storePolicy(p1); storePolicy(p2); storePolicy(p3);

        claimHistory.add(new Claim("CLM-501", "POL-102", c2.getCustomerId(), 45000.0, "Hospitalization", "2024-07-15", "APPROVED", "Hospital Bill"));
        claimHistory.add(new Claim("CLM-502", "POL-103", c3.getCustomerId(), 15000.0, "Bumper repair", "2024-08-10", "PENDING", "Workshop Bill"));

        paymentList.add(new Premium("PAY-201", "POL-101", p1.calculatePremium(), "2024-01-16", "Net Banking"));
        paymentList.add(new Premium("PAY-202", "POL-102", p2.calculatePremium(), "2024-03-11", "Credit Card"));
    }

    private void storePolicy(Policy p) {
        policyList.add(p);
        policyMap.put(p.getPolicyNumber(), p);
        rebuildTreeMap();
    }

    private void rebuildTreeMap() {
        policiesByPremium.clear();
        for (Policy p : policyList) {
            double prem = p.calculatePremium();
            policiesByPremium.putIfAbsent(prem, new ArrayList<>());
            policiesByPremium.get(prem).add(p);
        }
    }

    private void buildGUI() {
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Customer Registration", createCustomerPanel());
        tabs.addTab("Policy Issuance (CRUD)", createPolicyPanel());
        tabs.addTab("Claim Processing", createClaimPanel());
        tabs.addTab("Premium Payments", createPaymentPanel());
        tabs.addTab("Search & Sort", createSearchSortPanel());
        tabs.addTab("Reports & TreeMap", createReportsPanel());
        tabs.addChangeListener(e -> refreshAll());

        add(tabs);
    }

    private JTextField addRow(JPanel p, String labelText, String initial) {
        p.add(new JLabel(labelText));
        JTextField tf = new JTextField(initial);
        p.add(tf);
        return tf;
    }

    // MODULE 1: Customer Registration
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel form = new JPanel(new GridLayout(6, 2, 4, 4));
        form.setBorder(new TitledBorder("Customer Details"));
        JTextField txtId = addRow(form, "Customer ID:", "C" + (101 + customerList.size()));
        JTextField txtName = addRow(form, "Customer Name:", "");
        JTextField txtEmail = addRow(form, "Email Address:", "");
        JTextField txtPhone = addRow(form, "Phone Number:", "");
        JTextField txtAddr = addRow(form, "Address:", "");

        JButton btnAdd = new JButton("Register Customer"), btnDel = new JButton("Delete Customer");
        form.add(btnAdd); form.add(btnDel);

        custModel = new DefaultTableModel(new String[]{"Customer ID", "Name", "Email", "Phone", "Address"}, 0);
        JTable table = new JTable(custModel);
        refreshCustomerTable();

        panel.add(form, BorderLayout.WEST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            try {
                String id = txtId.getText().trim(), name = txtName.getText().trim();
                String email = txtEmail.getText().trim(), phone = txtPhone.getText().trim(), addr = txtAddr.getText().trim();
                if (id.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) throw new IllegalArgumentException("All fields mandatory.");
                if (!phone.matches("\\d{10}")) throw new IllegalArgumentException("Phone must be 10 digits.");
                if (!email.contains("@") || !email.contains(".")) throw new IllegalArgumentException("Invalid email format.");
                for (Customer c : customerList) if (c.getCustomerId().equalsIgnoreCase(id)) throw new IllegalArgumentException("Customer ID exists.");

                customerList.add(new Customer(id, name, email, phone, addr));
                refreshCustomerTable();
                refreshAll();
                JOptionPane.showMessageDialog(this, "Customer Registered Successfully!");
                txtId.setText("C" + (101 + customerList.size()));
                txtName.setText(""); txtEmail.setText(""); txtPhone.setText(""); txtAddr.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE); }
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                customerList.removeIf(c -> c.getCustomerId().equalsIgnoreCase(custModel.getValueAt(row, 0).toString()));
                refreshCustomerTable();
                refreshAll();
                JOptionPane.showMessageDialog(this, "Customer Deleted.");
            } else { JOptionPane.showMessageDialog(this, "Select a customer to delete."); }
        });
        return panel;
    }

    private void refreshCustomerTable() {
        custModel.setRowCount(0);
        for (Customer c : customerList) custModel.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress()});
    }

    // MODULE 2 & 3: Policy Issuance, Premium Calculation & CRUD
    private JPanel createPolicyPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel form = new JPanel(new GridLayout(9, 2, 3, 3));
        form.setBorder(new TitledBorder("Policy Creation & CRUD"));
        JTextField txtPolNum = addRow(form, "Policy Number:", "POL-" + (101 + policyList.size()));
        form.add(new JLabel("Customer:"));
        cbPolicyCust = new JComboBox<>();
        form.add(cbPolicyCust);
        form.add(new JLabel("Policy Type:"));
        cbPolicyType = new JComboBox<>(new String[]{"Life", "Health", "Vehicle"});
        form.add(cbPolicyType);
        JTextField txtCov = addRow(form, "Coverage (Rs):", "500000");
        JTextField txtDur = addRow(form, "Duration (Yrs):", "5");
        JTextField txtP1 = addRow(form, "Age / Veh Type:", "30");
        JTextField txtP2 = addRow(form, "Smoker/Disease/VehNo:", "false");

        JLabel lblPrem = new JLabel("Calculated Premium: Rs. 0.00");
        form.add(new JLabel("Premium Output:"));
        form.add(lblPrem);

        JPanel btnRow = new JPanel(new GridLayout(1, 4, 3, 3));
        JButton btnCalc = new JButton("Calculate"), btnIssue = new JButton("Issue (Create)");
        JButton btnUpdate = new JButton("Update"), btnDelete = new JButton("Delete");
        btnRow.add(btnCalc); btnRow.add(btnIssue); btnRow.add(btnUpdate); btnRow.add(btnDelete);

        JPanel leftPanel = new JPanel(new BorderLayout(3, 3));
        leftPanel.add(form, BorderLayout.CENTER);
        leftPanel.add(btnRow, BorderLayout.SOUTH);

        policyModel = new DefaultTableModel(new String[]{"Policy No", "Type", "Cust ID", "Cust Name", "Coverage", "Duration", "Premium"}, 0);
        JTable table = new JTable(policyModel);
        refreshPolicyTable();

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnCalc.addActionListener(e -> {
            try {
                Policy p = parsePolicyFields(txtPolNum.getText(), txtCov.getText(), txtDur.getText(), (String) cbPolicyType.getSelectedItem(), txtP1.getText(), txtP2.getText(), false);
                lblPrem.setText("Calculated Premium: Rs. " + df.format(p.calculatePremium()));
            } catch (Exception ex) { lblPrem.setText("Error: " + ex.getMessage()); }
        });

        btnIssue.addActionListener(e -> {
            try {
                String num = txtPolNum.getText().trim();
                if (policyMap.containsKey(num)) throw new InvalidPolicyException("Policy number already exists.");
                Policy p = parsePolicyFields(num, txtCov.getText(), txtDur.getText(), (String) cbPolicyType.getSelectedItem(), txtP1.getText(), txtP2.getText(), true);
                storePolicy(p);
                refreshPolicyTable();
                refreshAll();
                JOptionPane.showMessageDialog(this, "Policy Issued! Annual Premium: Rs. " + df.format(p.calculatePremium()));
                txtPolNum.setText("POL-" + (101 + policyList.size()));
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Policy Error", JOptionPane.ERROR_MESSAGE); }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                Policy p = policyMap.get(policyModel.getValueAt(row, 0).toString());
                if (p != null) {
                    try {
                        p.setCoverageAmount(Double.parseDouble(txtCov.getText().trim()));
                        p.setDurationYears(Integer.parseInt(txtDur.getText().trim()));
                        rebuildTreeMap();
                        refreshPolicyTable();
                        JOptionPane.showMessageDialog(this, "Policy updated successfully!");
                    } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid coverage or duration."); }
                }
            } else { JOptionPane.showMessageDialog(this, "Select a policy from the table to update."); }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String num = policyModel.getValueAt(row, 0).toString();
                policyList.removeIf(p -> p.getPolicyNumber().equalsIgnoreCase(num));
                policyMap.remove(num);
                rebuildTreeMap();
                refreshPolicyTable();
                refreshAll();
                JOptionPane.showMessageDialog(this, "Policy deleted.");
            } else { JOptionPane.showMessageDialog(this, "Select a policy to delete."); }
        });
        return panel;
    }

    private Policy parsePolicyFields(String num, String covStr, String durStr, String type, 
                                     String p1, String p2, boolean checkCust) throws InvalidPolicyException {
        if (num.isEmpty()) throw new InvalidPolicyException("Policy number cannot be empty.");
        if (checkCust && cbPolicyCust.getSelectedIndex() < 0) throw new InvalidPolicyException("Please select a registered customer.");
        String custStr = (cbPolicyCust.getSelectedItem() != null) ? cbPolicyCust.getSelectedItem().toString() : "C101 - Customer";
        String cid = custStr.split(" - ")[0], cname = custStr.contains(" - ") ? custStr.split(" - ")[1] : "Customer";

        double cov; int dur;
        try {
            cov = Double.parseDouble(covStr.trim());
            dur = Integer.parseInt(durStr.trim());
            if (cov <= 0 || dur <= 0) throw new InvalidPolicyException("Coverage and duration must be positive.");
        } catch (NumberFormatException ex) { throw new InvalidPolicyException("Coverage and duration must be numeric."); }

        String today = sdf.format(new Date());
        if ("Life".equalsIgnoreCase(type)) {
            return new LifePolicy(num, cid, cname, cov, dur, today, Integer.parseInt(p1.trim()), Boolean.parseBoolean(p2.trim()));
        } else if ("Health".equalsIgnoreCase(type)) {
            return new HealthPolicy(num, cid, cname, cov, dur, today, Integer.parseInt(p1.trim()), Boolean.parseBoolean(p2.trim()));
        } else {
            return new VehiclePolicy(num, cid, cname, cov, dur, today, p1.trim(), p2.trim(), 2);
        }
    }

    private void refreshPolicyTable() {
        policyModel.setRowCount(0);
        for (Policy p : policyList) {
            policyModel.addRow(new Object[]{p.getPolicyNumber(), p.getPolicyType(), p.getCustomerId(),
                p.getCustomerName(), df.format(p.getCoverageAmount()), p.getDurationYears() + " Yrs", df.format(p.calculatePremium())});
        }
    }

    // MODULE 4: Claim Processing (LinkedList)
    private JPanel createClaimPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel form = new JPanel(new GridLayout(7, 2, 3, 3));
        form.setBorder(new TitledBorder("File & Process Claims (LinkedList)"));
        JTextField txtCId = addRow(form, "Claim ID:", "CLM-" + (501 + claimHistory.size()));
        form.add(new JLabel("Select Policy:"));
        cbClaimPolicy = new JComboBox<>();
        form.add(cbClaimPolicy);
        JTextField txtAmt = addRow(form, "Claim Amount (Rs):", "");
        JTextField txtReason = addRow(form, "Reason:", "");
        JTextField txtDocs = addRow(form, "Documents:", "");
        form.add(new JLabel("Claim Status:"));
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"PENDING", "APPROVED", "REJECTED"});
        form.add(cbStatus);

        JPanel btnRow = new JPanel(new GridLayout(1, 4, 3, 3));
        JButton btnSubmit = new JButton("Submit"), btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject"), btnDelClaim = new JButton("Delete");
        btnRow.add(btnSubmit); btnRow.add(btnApprove); btnRow.add(btnReject); btnRow.add(btnDelClaim);

        JPanel left = new JPanel(new BorderLayout(3, 3));
        left.add(form, BorderLayout.CENTER);
        left.add(btnRow, BorderLayout.SOUTH);

        claimModel = new DefaultTableModel(new String[]{"Claim ID", "Policy No", "Cust ID", "Amount", "Reason", "Docs", "Date", "Status"}, 0);
        JTable table = new JTable(claimModel);
        refreshClaimTable();

        panel.add(left, BorderLayout.WEST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnSubmit.addActionListener(e -> {
            try {
                String cid = txtCId.getText().trim();
                if (cbClaimPolicy.getSelectedItem() == null) throw new InvalidClaimException("Select a valid policy.");
                String polNum = cbClaimPolicy.getSelectedItem().toString().split(" ")[0];
                Policy p = policyMap.get(polNum);
                if (p == null) throw new InvalidClaimException("Policy not found.");

                double amt = Double.parseDouble(txtAmt.getText().trim());
                if (amt <= 0) throw new InvalidClaimException("Claim amount must be positive.");
                if (amt > p.getCoverageAmount()) throw new InvalidClaimException("Claim exceeds coverage limit.");
                String reason = txtReason.getText().trim(), docs = txtDocs.getText().trim();
                if (reason.isEmpty() || docs.isEmpty()) throw new InvalidClaimException("Reason and document details required.");

                claimHistory.add(new Claim(cid, polNum, p.getCustomerId(), amt, reason, sdf.format(new Date()), (String) cbStatus.getSelectedItem(), docs));
                refreshClaimTable();
                JOptionPane.showMessageDialog(this, "Claim Submitted into LinkedList history!");
                txtCId.setText("CLM-" + (501 + claimHistory.size()));
                txtAmt.setText(""); txtReason.setText(""); txtDocs.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Claim Error", JOptionPane.ERROR_MESSAGE); }
        });

        btnApprove.addActionListener(e -> updateClaimStatus(table, "APPROVED"));
        btnReject.addActionListener(e -> updateClaimStatus(table, "REJECTED"));

        btnDelClaim.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                claimHistory.removeIf(c -> c.getClaimId().equalsIgnoreCase(claimModel.getValueAt(row, 0).toString()));
                refreshClaimTable();
                JOptionPane.showMessageDialog(this, "Claim deleted.");
            } else { JOptionPane.showMessageDialog(this, "Select a claim to delete."); }
        });
        return panel;
    }

    private void updateClaimStatus(JTable table, String status) {
        int row = table.getSelectedRow();
        if (row != -1) {
            String id = claimModel.getValueAt(row, 0).toString();
            for (Claim c : claimHistory) if (c.getClaimId().equalsIgnoreCase(id)) { c.setStatus(status); break; }
            refreshClaimTable();
            JOptionPane.showMessageDialog(this, "Claim status updated to " + status);
        } else { JOptionPane.showMessageDialog(this, "Select a claim from the table."); }
    }

    private void refreshClaimTable() {
        claimModel.setRowCount(0);
        for (Claim c : claimHistory) {
            claimModel.addRow(new Object[]{c.getClaimId(), c.getPolicyNumber(), c.getCustomerId(),
                df.format(c.getClaimAmount()), c.getReason(), c.getDocumentDetails(), c.getClaimDate(), c.getStatus()});
        }
    }

    // MODULE 5: Premium Payment Tracking
    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel form = new JPanel(new GridLayout(6, 2, 4, 4));
        form.setBorder(new TitledBorder("Record Premium Payment"));
        JTextField txtPayId = addRow(form, "Payment ID:", "PAY-" + (201 + paymentList.size()));
        form.add(new JLabel("Select Policy:"));
        cbPayPolicy = new JComboBox<>();
        form.add(cbPayPolicy);
        JTextField txtPayAmt = addRow(form, "Amount (Rs):", "");
        JTextField txtDate = addRow(form, "Payment Date:", sdf.format(new Date()));
        form.add(new JLabel("Method:"));
        JComboBox<String> cbMethod = new JComboBox<>(new String[]{"Net Banking", "UPI", "Credit Card", "Cheque", "Cash"});
        form.add(cbMethod);
        JButton btnPay = new JButton("Record Payment");
        form.add(new JLabel("Action:")); form.add(btnPay);

        cbPayPolicy.addActionListener(e -> {
            if (cbPayPolicy.getSelectedItem() != null) {
                Policy p = policyMap.get(cbPayPolicy.getSelectedItem().toString().split(" ")[0]);
                if (p != null) txtPayAmt.setText(String.valueOf(p.calculatePremium()));
            }
        });

        payModel = new DefaultTableModel(new String[]{"Payment ID", "Policy No", "Amount Paid", "Date", "Method"}, 0);
        JTable table = new JTable(payModel);
        refreshPaymentTable();

        panel.add(form, BorderLayout.WEST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnPay.addActionListener(e -> {
            try {
                String pid = txtPayId.getText().trim(), pol = cbPayPolicy.getSelectedItem().toString().split(" ")[0];
                double amt = Double.parseDouble(txtPayAmt.getText().trim());
                if (amt <= 0) throw new IllegalArgumentException("Amount must be positive.");

                paymentList.add(new Premium(pid, pol, amt, txtDate.getText().trim(), (String) cbMethod.getSelectedItem()));
                refreshPaymentTable();
                JOptionPane.showMessageDialog(this, "Payment of Rs. " + df.format(amt) + " recorded successfully!");
                txtPayId.setText("PAY-" + (201 + paymentList.size()));
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid payment input: " + ex.getMessage()); }
        });
        return panel;
    }

    private void refreshPaymentTable() {
        payModel.setRowCount(0);
        for (Premium p : paymentList) payModel.addRow(new Object[]{p.getPaymentId(), p.getPolicyNumber(), df.format(p.getAmountPaid()), p.getPaymentDate(), p.getPaymentMethod()});
    }

    // MODULE 6: Searching & Sorting
    private JPanel createSearchSortPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        top.setBorder(new TitledBorder("Search Policies & Sort"));
        JTextField txtSearch = new JTextField(12);
        JComboBox<String> cbFilter = new JComboBox<>(new String[]{"Policy Number", "Customer Name", "Policy Type"});
        JButton btnSearch = new JButton("Search"), btnReset = new JButton("Reset");
        JButton btnSortPremAsc = new JButton("Sort Premium (Low->High)");
        JButton btnSortPremDesc = new JButton("Sort Premium (High->Low)");
        JButton btnSortDuration = new JButton("Sort by Duration");

        top.add(new JLabel("Keyword:")); top.add(txtSearch);
        top.add(new JLabel("Field:")); top.add(cbFilter);
        top.add(btnSearch); top.add(btnReset);
        top.add(btnSortPremAsc); top.add(btnSortPremDesc); top.add(btnSortDuration);

        searchModel = new DefaultTableModel(new String[]{"Policy No", "Type", "Cust ID", "Cust Name", "Coverage", "Duration", "Premium"}, 0);
        JTable table = new JTable(searchModel);
        displaySearchResults(policyList);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim().toLowerCase();
            String field = (String) cbFilter.getSelectedItem();
            List<Policy> filtered = new ArrayList<>();
            for (Policy p : policyList) {
                boolean match = ("Policy Number".equals(field) && p.getPolicyNumber().toLowerCase().contains(kw)) ||
                                ("Customer Name".equals(field) && p.getCustomerName().toLowerCase().contains(kw)) ||
                                ("Policy Type".equals(field) && p.getPolicyType().toLowerCase().contains(kw));
                if (match) filtered.add(p);
            }
            displaySearchResults(filtered);
        });

        btnReset.addActionListener(e -> { txtSearch.setText(""); displaySearchResults(policyList); });
        btnSortPremAsc.addActionListener(e -> { policyList.sort(Comparator.comparingDouble(Policy::calculatePremium)); displaySearchResults(policyList); });
        btnSortPremDesc.addActionListener(e -> { policyList.sort((p1, p2) -> Double.compare(p2.calculatePremium(), p1.calculatePremium())); displaySearchResults(policyList); });
        btnSortDuration.addActionListener(e -> { policyList.sort(Comparator.comparingInt(Policy::getDurationYears)); displaySearchResults(policyList); });
        return panel;
    }

    private void displaySearchResults(List<Policy> list) {
        searchModel.setRowCount(0);
        for (Policy p : list) {
            searchModel.addRow(new Object[]{p.getPolicyNumber(), p.getPolicyType(), p.getCustomerId(),
                p.getCustomerName(), df.format(p.getCoverageAmount()), p.getDurationYears() + " Yrs", df.format(p.calculatePremium())});
        }
    }

    // MODULE 7: Reports & TreeMap Demonstration
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel stats = new JPanel(new GridLayout(1, 5, 5, 5));
        stats.setBorder(new TitledBorder("System Statistics"));
        lblTotalPol = addStat(stats, "Policies: 0");
        lblTotalCov = addStat(stats, "Coverage: Rs 0");
        lblTotalClaims = addStat(stats, "Claims: 0");
        lblTotalApproved = addStat(stats, "Approved: Rs 0");
        lblTotalPaid = addStat(stats, "Collected: Rs 0");

        JPanel center = new JPanel(new GridLayout(1, 2, 6, 6));

        JPanel treeMapBox = new JPanel(new BorderLayout(3, 3));
        treeMapBox.setBorder(new TitledBorder("TreeMap: Policies Sorted by Premium"));
        txtTreeMapArea = new JTextArea();
        txtTreeMapArea.setEditable(false);
        txtTreeMapArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        treeMapBox.add(new JScrollPane(txtTreeMapArea), BorderLayout.CENTER);

        JPanel claimSortBox = new JPanel(new BorderLayout(3, 3));
        claimSortBox.setBorder(new TitledBorder("Claims Amount Ranking"));
        DefaultTableModel crModel = new DefaultTableModel(new String[]{"Claim ID", "Policy No", "Amount", "Status"}, 0);
        JTable crTable = new JTable(crModel);
        claimSortBox.add(new JScrollPane(crTable), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout());
        JButton btnSortClaimDesc = new JButton("Sort Claims (High->Low)"), btnSortClaimAsc = new JButton("Sort Claims (Low->High)");
        btnBar.add(btnSortClaimDesc); btnBar.add(btnSortClaimAsc);
        claimSortBox.add(btnBar, BorderLayout.SOUTH);

        btnSortClaimDesc.addActionListener(e -> {
            claimHistory.sort((c1, c2) -> Double.compare(c2.getClaimAmount(), c1.getClaimAmount()));
            populateClaimsReport(crModel, claimHistory);
        });

        btnSortClaimAsc.addActionListener(e -> {
            claimHistory.sort(Comparator.comparingDouble(Claim::getClaimAmount));
            populateClaimsReport(crModel, claimHistory);
        });

        center.add(treeMapBox);
        center.add(claimSortBox);
        panel.add(stats, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        populateClaimsReport(crModel, claimHistory);
        refreshReports();
        return panel;
    }

    private JLabel addStat(JPanel parent, String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        parent.add(l);
        return l;
    }

    private void populateClaimsReport(DefaultTableModel model, List<Claim> list) {
        model.setRowCount(0);
        for (Claim c : list) model.addRow(new Object[]{c.getClaimId(), c.getPolicyNumber(), df.format(c.getClaimAmount()), c.getStatus()});
    }

    private void refreshReports() {
        if (lblTotalPol == null) return;
        double cov = 0.0, approved = 0.0, paid = 0.0;
        for (Policy p : policyList) cov += p.getCoverageAmount();
        for (Claim c : claimHistory) if ("APPROVED".equalsIgnoreCase(c.getStatus())) approved += c.getClaimAmount();
        for (Premium p : paymentList) paid += p.getAmountPaid();

        lblTotalPol.setText("Policies: " + policyList.size());
        lblTotalCov.setText("Coverage: Rs. " + df.format(cov));
        lblTotalClaims.setText("Claims: " + claimHistory.size());
        lblTotalApproved.setText("Approved: Rs. " + df.format(approved));
        lblTotalPaid.setText("Collected: Rs. " + df.format(paid));

        StringBuilder sb = new StringBuilder("TreeMap Premium Ordering:\n--------------------------------------------------\n");
        for (Map.Entry<Double, List<Policy>> entry : policiesByPremium.entrySet()) {
            sb.append("Premium: Rs. ").append(df.format(entry.getKey())).append("\n");
            for (Policy p : entry.getValue()) {
                sb.append("  |- ").append(p.getPolicyNumber()).append(" | ")
                  .append(p.getCustomerName()).append(" (").append(p.getPolicyType()).append(")\n");
            }
        }
        txtTreeMapArea.setText(sb.toString());
        txtTreeMapArea.setCaretPosition(0);
    }

    private void refreshAll() {
        if (cbPolicyCust != null) {
            cbPolicyCust.removeAllItems();
            for (Customer c : customerList) cbPolicyCust.addItem(c.toString());
            cbClaimPolicy.removeAllItems();
            cbPayPolicy.removeAllItems();
            for (Policy p : policyList) {
                cbClaimPolicy.addItem(p.getPolicyNumber() + " (" + p.getCustomerName() + ")");
                cbPayPolicy.addItem(p.getPolicyNumber() + " (" + p.getCustomerName() + ")");
            }
        }
        refreshReports();
    }

    // Main Entry Point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InsuranceManagementSystem frame = new InsuranceManagementSystem();
            frame.setVisible(true);
        });
    }
}
