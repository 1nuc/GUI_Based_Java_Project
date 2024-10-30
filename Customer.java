package Customer;

import javax.swing.JOptionPane;
import java.io.*;
import java.util.regex.Pattern;

public class Customer {
    private String firstName;
    private String lastName;
    private String email;
    private String customerID;
    private String password;
    private String address;
    private String phoneNumber;

    public Customer(String customerID, String firstName, String lastName, String email, String password, String address, String phoneNumber){
        this.customerID=customerID;
        this.firstName= firstName;
        this.lastName=lastName;
        this.email=email;
        this.password=password;
        this.address=address;
        this.phoneNumber=phoneNumber;
    }
    
    
    public String registerCustomer(String firstName, String lastName, String email, String password, String address, String phoneNumber) {
        if (!validateFirstName(firstName) ||
            !validateLastName(lastName) ||
            !validateAddress(address) ||
            !validatePhoneNumber(phoneNumber) ||
            !emailChecking(email) ||
            !validatePassword(password) ||
            !customerIDChecking()) {
            return null;
        }
        
        saveCustomerInfo();
        JOptionPane.showMessageDialog(null, "Successfully registered with Customer ID: " + getCustomerID());
        return getCustomerID();
        
    }

    private boolean validateFirstName(String firstName) {
        if (!firstName.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(null, "Invalid First Name. Please enter letters only.");
            return false;
        }
        setFirstName(firstName);
        return true;
    }

    private boolean validateLastName(String lastName) {
        if (!lastName.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(null, "Invalid Last Name. Please enter letters only.");
            return false;
        }
        setLastName(lastName);
        return true;
    }

    private boolean validateAddress(String address) {
        if (address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Address cannot be empty.");
            return false;
        }
        setAddress(address);
        return true;
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Invalid Phone Number. Please enter digits only.");
            return false;
        }
        setPhoneNumber(phoneNumber);
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password cannot be empty.");
            return false;
        }
        setPassword(password);
        return true;
    }

    protected boolean customerIDChecking() {
        String id;
        do {
            id = "CU" + String.format("%06d", (int) (Math.random() * 1000000));
        } while (idExists(id));
        
        setCustomerID(id);
        return true;
    }

    private boolean idExists(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\database\\Customer.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length > 0 && parts[0].equals(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error checking for existing IDs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    protected boolean emailChecking(String email) {
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            JOptionPane.showMessageDialog(null, "Invalid email format. Please enter a valid email.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        setEmail(email);
        return true;
    }

    protected void saveCustomerInfo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\database\\Customer.txt", true))) {
            writer.write(getCustomerID() + ", " + getFirstName() + ", " + getLastName() + ", " + getEmail() + ", Customer, " + getPassword() + ", " + getAddress() + ", " + getPhoneNumber()+", "+"Active");
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred writing to the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}