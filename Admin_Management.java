package Adminstrator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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

public class Admin_Management extends Adminstrator{

    public Admin_Management(String Name, String Email, String ID, String Role, String Password) {
        super(Name, Email, ID, Role, Password);
        //TODO Auto-generated constructor stub
    }
    @Override
    public void Set_info(String UserId, String UserEmail, String Username, String Pass){
            set_Name(Username);
            
            if (!ID_checking(UserId)) {
                 return; // Exit the method if Hall ID is invalid
            }
         
            if (!email_checking(UserEmail)) {
                 return; // Exit the method if Hall ID is invalid
            }
                           
            set_Password(Pass);
            SaveToFile();
        
    }

    @Override
    protected void Role_checking(){//A function that checks the ID to identify the role
        if(get_ID().startsWith("AD")){
            set_Role("Adminstrator");
        }
        else if(get_ID().startsWith("SC")){
            set_Role("Schedular");
        }

    }

    @Override
    protected boolean ID_checking(String UserID) {
        if (UserID.isEmpty()) {
            JOptionPane.showMessageDialog(null, "User ID cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!UserID.matches("^(AD|SC)\\d{6}$")) {
            JOptionPane.showMessageDialog(null, "Invalid User ID. Please enter a valid ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (idExists(UserID)) {
            JOptionPane.showMessageDialog(null, "Sorry, but the User ID already exists.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        set_ID(UserID);
        Role_checking();
        return true;
    }

    @Override
    protected Boolean email_checking(String Email){//A function that checks the syntax of the Email entered
      if (Email.isEmpty()) {
               JOptionPane.showMessageDialog(null, "Email cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
               return false;
           }
           // Add more conditions as per your validation logic
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Email)) {
               JOptionPane.showMessageDialog(null, "Invalid Email format. Please enter a valid Email.", "Validation Error", JOptionPane.ERROR_MESSAGE);
               return false;
           } else{
                set_Email(Email);
                return true;
            }    }

    @Override
    protected void SaveToFile(){//saving all the info entered to a speciliazed file

        try{

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\database\\Database.txt",true))) {
                writer.write(get_ID()+", "+get_Name()+", "+get_Email()+", "+get_Role()+", "+get_Password()+", "+ "Active");
                writer.newLine();
                if(get_Role().equals("Schedular")){
                    JOptionPane.showMessageDialog(null, "Schedular info Saved Successfully");
                }
                else if(get_Role().equals("Adminstrator")){
                    JOptionPane.showMessageDialog(null, "Admin info Saved Successfully");
                }
            }

        }catch(IOException e){
            JOptionPane.showMessageDialog(null,"An error occured writing to the file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean idExists(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\database\\Database.txt"))) {
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

    @Override
    public void update_info(String EnteredID, String Selected_Opt, String Name, String ID, String Password, String Email){

        Path path=Paths.get("src\\database\\Database.txt");
            try {
                List<String> fileContent = Files.readAllLines(path);

                Boolean idfound=false;

                for(int i = 0; i < fileContent.size(); i++){
                    String [] Line=fileContent.get(i).split(", ");
                    if(Line[0].equals(EnteredID) && !EnteredID.isEmpty()){
                        idfound=true;
                        switch (Selected_Opt) {
                            case "User_Name":
                                set_Name(Name);
                                Line[1] = get_Name();
                                break;
                            case "User_Email":
                                if(email_checking(Email)){
                                    set_Email(Email);
                                    Line[2] = get_Email();
                                }else{
                                    return;
                                }
                                break;
                            case "User_ID":
                                if(ID_checking(ID)){
                                    set_ID(ID);
                                    Line[0] = get_ID();
                                    Line[3]=get_Role();
                                    JOptionPane.showMessageDialog(null, "User Role Updated Successfully");
                                }else{
                                    return;
                                }                                
                                break;
                            case "User_Password":
                                set_Password(Password);
                                Line[4] = get_Password();
                                break;
                            default:
                                JOptionPane.showMessageDialog(null,"Please enter a valid number","Validation Error", JOptionPane.ERROR_MESSAGE);
                                break;
                        }
                        // Replace the old line with the updated line in the list
                        fileContent.set(i,String.join(", ", Line));
                        break;
                    }
                }

                if (!idfound) {
                    JOptionPane.showMessageDialog(null, "User ID either Invalid or empty. Please enter a valid ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                else{
                    Files.write(path, fileContent);
                    JOptionPane.showMessageDialog(null,"User infromation have been successfully updated");

                }

                // Write the updated list back to the file
                
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace(); 
                return;
            }    
    }
    @Override
    public void Select_Specific_User(String FileName, DefaultTableModel model,String find){
        try (BufferedReader reader = new BufferedReader(new FileReader(FileName))) {
            Boolean found=false;
            String Lines;
            while((Lines=reader.readLine())!=null){
                String [] FTC=Lines.split(", ");
                if(find.equals(FTC[0])){
                    found=true;
                    model.addRow(FTC);
                }
                        
            }if(!found){
                JOptionPane.showMessageDialog(null, "Invalid User ID. Please enter a valid ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Error reading the file","Validation Error", JOptionPane.ERROR_MESSAGE);                }       
    }
    
    
    @Override
    public void delete_info(String FileName, String ID_Delete){
        
        Path path=Paths.get(FileName);

            try {
                List<String> fileContent = Files.readAllLines(path);
               
                while(true){        
                    if(Pattern.matches("^(AD|SC)\\d{6}$", ID_Delete)){
                        break;
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Invalid ID format. Please try again","Validation Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                Boolean idfound=false;

                for(int i = 0; i < fileContent.size(); i++){
                    String [] Line=fileContent.get(i).split(", ");
                    if(Line[0].equals(ID_Delete)){
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
    public void Manage_Block_User(String ID_Blocked, boolean isBlockAction){

        Path path=Paths.get("src\\database\\Database.txt");
            try {
                List<String> fileContent = Files.readAllLines(path);
                
               
                while(true){        
                    if(Pattern.matches("^(AD|SC)\\d{6}$", ID_Blocked)){
                        break;
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Invalid ID format. Please try again","Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                Boolean idfound=false;

                for(int i = 0; i < fileContent.size(); i++){
                    String [] Line=fileContent.get(i).split(", ");
                    if(Line[0].equals(ID_Blocked)){
                        idfound=true;
                        
                        if(isBlockAction){
                            if (Line[5].equalsIgnoreCase("Active")) {
                                Line[5] = "Blocked";
                            } 
                            else {
                                JOptionPane.showMessageDialog(null, "User Already blooked");
                                return;
                            }
                            fileContent.set(i, String.join(", ", Line));
                        }
                        else{
                            // Unblock User Logic
                             if (Line[5].equalsIgnoreCase("Blocked")) {
                                 Line[5] = "Active";
                                 fileContent.set(i, String.join(", ", Line));  // Update the line in file content
                            } else {
                                    JOptionPane.showMessageDialog(null, "User is already active.", "Info", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                            }
                        }
                        
                    }           
                }

                if (!idfound) {
                    JOptionPane.showMessageDialog(null,"ID not Found","Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                else{
                    Files.write(path, fileContent);
                    JOptionPane.showMessageDialog(null,"Record Blocked successfully.");

                }                    
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace(); 
                return;
            }
    }



    @Override
    public void filter_info(DefaultTableModel model, String Search){
            Path FilePath=Paths.get("src\\database\\Database.txt");

            try{

                List<String> file=Files.readAllLines(FilePath);  
                String search;
                Boolean find=false;
                for(String line: file){
                    String [] Lines=line.split(", ");
                    if(line.contains(Search)){
                        find=true;
                        model.addRow(Lines);
                        }
                    }
                    if(!find){
                        JOptionPane.showMessageDialog(null, "infromation not found");
                        return;
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
                        if(Role.equals("Adminstrator") && Pattern.matches("^(AD)\\d{6}$", line[0])){         
                            model.addRow(line);
                        }else if(Role.equals("Schedular") && Pattern.matches("^(SC)\\d{6}$", line[0])){
                            model.addRow(line);
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("Error reading the file");
                }
            
            
    }

    public static void main(String[] args){
        Admin_Management a=new Admin_Management(null, null, null, null, null);
        // a.Set_info();
        // a.update_info();
        // a.filter_info();
        // a.Block_User();
//        a.dalete_info();
    }

}