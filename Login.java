package Staff_Acc_Manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;

abstract class Main_Log{
    abstract public String log(String email, String ID, String Password);
    abstract protected String read_file(String email, String ID, String Password);
    abstract protected String authenticate(String email, String ID, String Password);
    abstract protected boolean isBlocked(String ID);
}

public class Login extends Main_Log {
    private static final String FILE_PATH = "src\\database\\Database.txt";

    @Override
    public String log(String email, String ID, String Password) {
        String role = authenticate(email, ID, Password);
        if (role != null) {
            if (isBlocked(ID)) {
                JOptionPane.showMessageDialog(null, "This account is blocked.", "Blocked Account", JOptionPane.WARNING_MESSAGE);
                return "Blocked";
            }
            JOptionPane.showMessageDialog(null, "Login Successfully");
            return role;
        } else {
            JOptionPane.showMessageDialog(null, "Login Failed", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    @Override
    protected String authenticate(String email, String ID, String Password) {
        if (ID.equals("ADMIN") && email.equals("super@gmail.com") && Password.equals("Admin@123")) {
            return "Super Admin";
        }
        return read_file(email, ID, Password);
    }
    @Override
    protected boolean isBlocked(String ID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 6 && parts[0].equals(ID)) {
                    return parts[5].equals("Blocked");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error checking block status: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    @Override
    protected String read_file(String email, String ID, String Password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 5 && 
                    parts[0].equals(ID) && 
                    parts[2].equals(email) && 
                    parts[4].equals(Password)) {
                    return parts[3]; // Return the role
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading the file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}