package Adminstrator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Customer_Management extends Admin_Management{

    public Customer_Management(String Name, String Email, String ID, String Role, String Password) {
        super(Name, Email, ID, Role, Password);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void delete_info(String FileName,String Cus_ID){
        
        Path path=Paths.get(FileName);
            try {
                List<String> fileContent = Files.readAllLines(path);
//                view_User_Info("src\\database\\Database.txt");
               
                while(true){        
                    if(Pattern.matches("^(CU)\\d{6}$", Cus_ID)){
                        break;
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Invalid ID format. Please try again","Validation Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                Boolean idfound=false;

                for(int i = 0; i < fileContent.size(); i++){
                    String [] Line=fileContent.get(i).split(", ");
                    if(Line[0].equals(Cus_ID)){
                        idfound=true;
                        fileContent.remove(i);
                    }
                        
                }

                if (!idfound) {
                    JOptionPane.showMessageDialog(null,"ID not found");
                    return;
                }
                else{
                    Files.write(path, fileContent);
                   JOptionPane.showMessageDialog(null,"Record deleted successfully.");

                }                    
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace(); 
                return;
            }
       
    }   
    @Override
    public void Manage_Block_User(String CU_Blocked, boolean IsBlocked) {

        Path path = Paths.get("src\\database\\Customer.txt");
        try {
            List<String> fileContent = Files.readAllLines(path);

            // Validate ID format (CU followed by 6 digits)
            while (true) {
                if (Pattern.matches("^(CU)\\d{6}$", CU_Blocked)) {
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid ID format. Please try again", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            boolean idFound = false;
            // Iterate through the file lines
            for (int i = 0; i < fileContent.size(); i++) {
                String[] Line = fileContent.get(i).split(", ");
                if (Line[0].equals(CU_Blocked)) {
                    idFound = true;

                    if (IsBlocked) {
                        // Block the user
                        if (Line[8].equalsIgnoreCase("Active")) {
                            Line[8] = "Blocked";  // Set status to "Blocked"
                        } else {
                            JOptionPane.showMessageDialog(null, "Customer is already blocked.");
                            return;
                        }
                    } else {
                        // Unblock the user
                        if (Line[8].equalsIgnoreCase("Blocked")) {
                            Line[8] = "Active";  // Set status to "Active"
                        } else {
                            JOptionPane.showMessageDialog(null, "Customer is already active.");
                            return;
                        }
                    }

                    // Update the file content with the modified status
                    fileContent.set(i, String.join(", ", Line));
                }
            }

            // If the ID was not found in the file
            if (!idFound) {
                JOptionPane.showMessageDialog(null, "ID not found.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                // Write the updated content back to the file
                Files.write(path, fileContent);
                String successMessage = IsBlocked ? "Customer blocked successfully." : "Customer unblocked successfully.";
                JOptionPane.showMessageDialog(null, successMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();  // Handle any file reading/writing errors
        }
    }

    


    @Override
    public void filter_info(DefaultTableModel model, String Search){

       Path FilePath=Paths.get("src\\database\\Customer.txt");

            try{

                List<String> file=Files.readAllLines(FilePath);  
                String search;
                Boolean find=false;
                for(String line: file){
                    String [] content=line.split(", ");
                    if(line.contains(Search)){
                        find=true;
                        model.addRow(content);
                        }
                    }
                    if(!find){
                        JOptionPane.showMessageDialog(null, "infromation not found");
                    }

            }catch(IOException e){
                JOptionPane.showMessageDialog(null,"Error reading the file","Validation Error", JOptionPane.ERROR_MESSAGE);
            } 
    }
    @Override 
    public void view_User_Info(String FileName, DefaultTableModel model, String Role){
                try (BufferedReader reader = new BufferedReader(new FileReader(FileName))) {
                    String Lines;
                    while((Lines=reader.readLine())!=null){
                        String [] line=Lines.split(", ");
                        if(Role.equals("Customer") && Pattern.matches("^(CU)\\d{6}$", line[0])){
                            model.addRow(line);
                        }
                       
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("Error reading the file");
                }
            
            
    }

    public static void main(String [] args){
        Customer_Management u=new Customer_Management(null, null, null, null, null);
        // u.filter_info();
    }
}