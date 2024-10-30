package Customer;

import java.io.*;
import java.util.*;
import java.nio.file.*; // Import added for Files and Paths
import javax.swing.JOptionPane;

public class Customer_Info extends Customer{
    public Customer_Info(String customerID, String firstName, String lastName, String email, String password, String address, String phoneNumber){
        super(customerID, firstName,  lastName,  email,  password,  address,  phoneNumber);
    }
    public boolean updateProfile(String updatedFirstName, String updatedLastName, String updatedAddress, String updatedEmail, String updatedPhoneNumber) {
        // Validation logic
        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty() || updatedAddress.isEmpty() 
                || updatedEmail.isEmpty() || updatedPhoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!updatedFirstName.matches("[a-zA-Z]+") || !updatedLastName.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(null, "First and Last names should contain only letters.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!updatedPhoneNumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Phone number should contain only digits.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!emailChecking(updatedEmail)) {
            JOptionPane.showMessageDialog(null, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Update the fields
        setFirstName(updatedFirstName);
        setLastName(updatedLastName);
        setAddress(updatedAddress);
        setEmail(updatedEmail);
        setPhoneNumber(updatedPhoneNumber);

        // Update the customer information in the file
        if (updateCustomerInfoInFile()) {
            JOptionPane.showMessageDialog(null, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Failed to update profile. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean updateCustomerInfoInFile() {
        String filePath = "src\\database\\Customer.txt";
        try {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(filePath)));
            boolean found = false;
            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).startsWith(getCustomerID())) {
                    fileContent.set(i, getCustomerID() + ", " + getFirstName() + ", " + getLastName() + ", " + getEmail() + ", " +
                            "Customer" + ", " + getPassword() + ", " + getAddress() + ", " + getPhoneNumber()+", "+"Active");
                    found = true;
                    break;
                }
            }
            if (found) {
                Files.write(Paths.get(filePath), fileContent);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while updating the profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
    