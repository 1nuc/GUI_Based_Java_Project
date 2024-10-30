package Customer;
    
    import java.io.BufferedReader;
    import java.io.FileReader;
    import java.io.IOException;
    import javax.swing.JOptionPane;
    import Staff_Acc_Manager.*;


    public class Customer_Login extends Login{
       private static final String FILE_PATH = "src\\database\\Customer.txt";

    public Boolean cus_log(String email, String ID, String Password) {
        Boolean role = read_Customer_data(email, ID, Password);
        if (role) {
            if (isBlocked(ID)) {
                JOptionPane.showMessageDialog(null, "This account is blocked.", "Blocked Account", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            JOptionPane.showMessageDialog(null, "Login Successfully");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Login Failed", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
   
    @Override
    protected boolean isBlocked(String ID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lines = line.split(", ");
                if (lines.length >= 9 && lines[0].equals(ID)) {
                    return lines[8].equals("Blocked");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error checking block status: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    protected Boolean read_Customer_data(String email, String ID, String Password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 8 && 
                    parts[0].equals(ID) && 
                    parts[3].equals(email) && 
                    parts[5].equals(Password)) {
                    return true; // Return the role
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading the file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    }