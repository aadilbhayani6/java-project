import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class Employee {
    private int id;
    private String name;
    private double basicSalary;
    private String department;

    // Default constructor
    public Employee() {
        this.id = 0;
        this.name = "";
        this.basicSalary = 0.0;
        this.department = "";
    }

    // Parameterized constructor
    public Employee(int id, String name, double basicSalary, String department) {
        this.id = id;
        this.name = name;
        this.basicSalary = basicSalary;
        this.department = department;
    }

    // Copy constructor
    public Employee(Employee other) {
        this.id = other.id;
        this.name = other.name;
        this.basicSalary = other.basicSalary;
        this.department = other.department;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getBasicSalary() { return basicSalary; }
    public String getDepartment() { return department; }

    public double getAllowance() {
        return basicSalary * 0.10; // 10% allowance
    }

    public double getDeduction() {
        return basicSalary * 0.05; // 5% deduction
    }

    public double getNetSalary() {
        return basicSalary + getAllowance() - getDeduction();
    }

    public String toFileString() {
        return id + "|" + name.replace("|", " ") + "|" +
               basicSalary + "|" + department.replace("|", " ");
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name +
               ", Basic Salary: Rs. " + String.format("%.2f", basicSalary) +
               ", Department: " + department +
               ", Net Salary: Rs. " + String.format("%.2f", getNetSalary());
    }
}

public class EmployeePayrollSystem extends JFrame {
    private JTextField txtId, txtName, txtSalary;
    private JComboBox<String> cmbDepartment;
    private JTextArea txtOutput;
    private ArrayList<Employee> employees = new ArrayList<>();

    private static final String FILE_NAME = "employees.txt";

    public EmployeePayrollSystem() {
        setTitle("Employee Payroll System");
        setSize(720, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblId = new JLabel("Employee ID:");
        JLabel lblName = new JLabel("Employee Name:");
        JLabel lblSalary = new JLabel("Basic Salary:");
        JLabel lblDepartment = new JLabel("Department:");

        txtId = new JTextField(15);
        txtName = new JTextField(15);
        txtSalary = new JTextField(15);
        cmbDepartment = new JComboBox<>(new String[]{
            "HR", "IT", "Finance", "Sales", "Marketing"
        });

        JButton btnAdd = new JButton("Add Employee");
        JButton btnSave = new JButton("Save");
        JButton btnLoad = new JButton("Load");
        JButton btnView = new JButton("View All");
        JButton btnClear = new JButton("Clear");

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        inputPanel.add(lblId);
        inputPanel.add(txtId);
        inputPanel.add(lblName);
        inputPanel.add(txtName);
        inputPanel.add(lblSalary);
        inputPanel.add(txtSalary);
        inputPanel.add(lblDepartment);
        inputPanel.add(cmbDepartment);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnView);
        buttonPanel.add(btnClear);

        txtOutput = new JTextArea(14, 55);
        txtOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtOutput);

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addEmployee());
        btnSave.addActionListener(e -> saveEmployees());
        btnLoad.addActionListener(e -> loadEmployees());
        btnView.addActionListener(e -> viewEmployees());
        btnClear.addActionListener(e -> clearFields());
    }

    private void addEmployee() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            double salary = Double.parseDouble(txtSalary.getText().trim());
            String department = (String) cmbDepartment.getSelectedItem();

            if (id <= 0) {
                JOptionPane.showMessageDialog(this, "ID must be greater than 0.");
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter employee name.");
                return;
            }
            if (salary < 0) {
                JOptionPane.showMessageDialog(this, "Salary cannot be negative.");
                return;
            }
            for (Employee employee : employees) {
                if (employee.getId() == id) {
                    JOptionPane.showMessageDialog(this, "This Employee ID already exists.");
                    return;
                }
            }

            Employee employee = new Employee(id, name, salary, department);
            employees.add(employee);

            JOptionPane.showMessageDialog(this,
                "Employee added successfully.\nNet Salary: Rs. " +
                String.format("%.2f", employee.getNetSalary()));
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Enter a valid numeric ID and salary.");
        }
    }

    private void saveEmployees() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Employee employee : employees) {
                writer.write(employee.toFileString());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Records saved to " + FILE_NAME);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
        }
    }

    private void loadEmployees() {
        ArrayList<Employee> loaded = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "employees.txt not found. Save records first.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    double salary = Double.parseDouble(parts[2]);
                    String department = parts[3];
                    loaded.add(new Employee(id, name, salary, department));
                }
            }
            employees = loaded;
            JOptionPane.showMessageDialog(this, "Records loaded successfully.");
            viewEmployees();
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
        }
    }

    private void viewEmployees() {
        txtOutput.setText("");
        if (employees.isEmpty()) {
            txtOutput.setText("No employee records available.");
            return;
        }
        for (Employee employee : employees) {
            txtOutput.append(employee.toString() + "\n");
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtSalary.setText("");
        cmbDepartment.setSelectedIndex(0);
        txtId.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeePayrollSystem app = new EmployeePayrollSystem();
            app.setVisible(true);
        });
    }
}
