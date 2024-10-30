package Scheduler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Hall_Management extends Hall{

   public Hall_Management(String Hall_ID, String Hall_Type, String Hall_Location, double cost_per_hour, int Hall_Capacity){
        super(Hall_ID, Hall_Type, Hall_Location, cost_per_hour, Hall_Capacity);
    }
    

    public void set_Hall_info(String hallID, String HallType, String hallLocation){

        if (!Hall_ID_checking(hallID)) {
            return; // Exit the method if Hall ID is invalid
        }
    // Validate Hall Type
        setHall_Type(HallType);      
        setHall_location(hallLocation);
        set_Capacity_Price();
        
       

    }

   
    protected boolean Hall_ID_checking(String hallID) {
        if (hallID.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Hall ID cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!hallID.matches("^(HA)\\d{6}$")) {
            JOptionPane.showMessageDialog(null, "Invalid Hall ID. Please enter a valid ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (hallIdExists(hallID)) {
            JOptionPane.showMessageDialog(null, "Sorry, but the Hall ID already exists.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        setHall_ID(hallID);
        return true;
    }

    private boolean hallIdExists(String hallID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\database\\Hall_Info.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length > 0 && parts[0].equals(hallID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error checking for existing Hall IDs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    protected void set_Capacity_Price(){
        if (getHall_Type().equals("Auditorium")){
            setHall_Capacity(1000);
            setcost_per_hour(300.00);
        }

        if (getHall_Type().equals("Banquet_Hall")){
            setHall_Capacity(300);
            setcost_per_hour(100.00);
        }
        
        if (getHall_Type().equals("Meeting_Room")){
            setHall_Capacity(30);
            setcost_per_hour(50.00);
        }
        Save_Hall_Info();
    }

   
    protected void Save_Hall_Info(){//saving all the info entered to a speciliazed file
        try{
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\database\\Hall_Info.txt",true))) {
                writer.write(getHall_ID()+", "+getHall_Type()+", "+getHall_location()+", "+getHall_Capacity()+", "+getcost_per_hour()+", ");
                writer.newLine();
                JOptionPane.showMessageDialog(null,"Hall information has been added successfully");
            }
        }catch(IOException e){
            JOptionPane.showMessageDialog(null,"An error occured writing to the file");
        }
    }
    
    public void view_file(String FileName, DefaultTableModel model){
                try (BufferedReader reader = new BufferedReader(new FileReader(FileName))) {
                    String Lines;
                    while((Lines=reader.readLine())!=null){
                        String [] line=Lines.split(", ");
                        model.addRow(line);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("Error reading the file");
                }       
    }
    public void Select_Specific_Hall(String FileName, DefaultTableModel model,String check){
        try (BufferedReader reader = new BufferedReader(new FileReader(FileName))) {
            Boolean found=false;
            String Lines;
            while((Lines=reader.readLine())!=null){
                String [] line=Lines.split(", ");
                if(check.equals(line[0])){
                    found=true;
                    model.addRow(line);
                }
                        
            }if(!found){
                JOptionPane.showMessageDialog(null, "Invalid Hall ID. Please enter a valid ID with a pattern of(HA//6d).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Error reading the file","Validation Error", JOptionPane.ERROR_MESSAGE);
                }       
    }

   
    public void update_Hall_info(String entered_ID,String Selected_Type, String id, String type, String Location){
            try { //initializing the file for reading and creating a new temp file to update the date.
                BufferedReader reader=new BufferedReader(new FileReader("src\\database\\Hall_Info.txt"));
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\database\\Hall_Info_temp.txt",true))) {

                    //view the existing file information for the user to select which hall to update.
//                    view_file("src/Schedular/Schedular/Hall_Info.txt");

                    Hall_Menu_GUI GUI=new Hall_Menu_GUI();

                    //some variables initializations that will be used in the section below.
                    String lines;
                    Boolean Hall_found =false;
                    //creating a while loop to read the data of the file to a strin named lines
                    while((lines=reader.readLine())!=null){
                        //splitting the lines and store them inside a string array
                        String[] line=lines.split(", ");
                        //making a condition to see if the Hall ID which is stored in the array index [0] matches the user input
                        if(entered_ID.equals(line[0]) && !entered_ID.isEmpty()){
                            Hall_found=true;//Boolean value that set to track the exitense of the Hall ID
                            //updating the data
                            switch (Selected_Type) {
                                case "Hall_ID":
                                    if(Hall_ID_checking(id)){
                                        setHall_ID(id);
                                        line[0]=getHall_ID();
                                    }   break;
                                case "Hall_Type":                                   
                                    setHall_Type(type);
                                    line[1]=getHall_Type();
                                    set_Capacity_Price();
                                    line[3]=Integer.toString(getHall_Capacity());
                                    line[4]=Double.toString(getcost_per_hour());
                                    break;
                                case "Hall_Location":
                                    setHall_location(Location);
                                    line[2]=getHall_location();
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(null, "You must select an option", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                            }
                            writer.write(String.join(", ", line)+", ");
                            writer.newLine();
                        }
                        //copy the rest of the unmodified code to the file
                        else{
                            writer.write(lines);
                            writer.newLine();
                        }
                        
                    }//exiting the while loop
                    //if the Hall ID is not in the file then writing back all the line information back to the file
                    //The Boolean value that set to track Hall ID 
                    if(!Hall_found){
                        JOptionPane.showMessageDialog(null, "Hall ID either Invalid or empty. Please enter a valid ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Hall infromation have been successfully updated");
                    }

                    //closing the reader and writer for the both file 
                    reader.close();
                    writer.close();

                    //deleting the actual file 
                    File oldFile= new File("src\\database\\Hall_Info.txt");
                    oldFile.delete();

                    //renaming the temp file with the name of the actual file
                    File new_file= new File("src\\database\\Hall_Info_temp.txt");
                    new_file.renameTo(oldFile);
                }

            } catch (FileNotFoundException e) {
                // TO Auto-generated catch block
                JOptionPane.showMessageDialog(null,"An error occured writing to the file");
            } catch (IOException e) {
                // TO Auto-generated catch block
                JOptionPane.showMessageDialog(null,"An error occured writing to the file");
            }
    }


    public void delete_Hall_info(String ID_Delete){
        Path filePath=Paths.get("src\\database\\Hall_Info.txt");
        try{
            List<String> lines=Files.readAllLines(filePath);
            Boolean found = false;           
                for(int i=0; i< lines.size(); i++){
                    String line=lines.get(i);
                    String [] parts=line.split(", ");
                    if(parts[0].equals(ID_Delete.toUpperCase())){                       
                        found=true;
                        int response= JOptionPane.showConfirmDialog(null, "Are you sure want to delete this hall record","Confirm Deletion",JOptionPane.YES_NO_CANCEL_OPTION ,JOptionPane.QUESTION_MESSAGE);
                        if (response == JOptionPane.YES_OPTION) {
                            lines.remove(i);
                            JOptionPane.showMessageDialog(null, "Hall information deleted successfully.", "Deletion Success", JOptionPane.INFORMATION_MESSAGE);
                        } else if (response == JOptionPane.NO_OPTION) {
                             JOptionPane.showMessageDialog(null, "Hall information deletion canceled.", "Deletion Canceled", JOptionPane.INFORMATION_MESSAGE);
                             break;
                        } else if (response == JOptionPane.CANCEL_OPTION) {
        // Handle the cancel option if needed
                             JOptionPane.showMessageDialog(null, "Operation canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
                             break;
                            }
                    }
                }
                if(found){
                    Files.write(filePath,lines);
                }else{
                    JOptionPane.showMessageDialog(null, "Hall ID either Invalid or empty. Please enter a valid ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
        }catch(IOException e){
            JOptionPane.showMessageDialog(null,"An error occured writing to the file");
        }
    }
   //this method can be used for all files
    public void Hall_search_filter(String FileName, DefaultTableModel model, String search){

            Path FilePath=Paths.get(FileName);

            try{

                List<String> file=Files.readAllLines(FilePath);  
                Boolean find=false;
                    for(String line: file){                    
                        if(line.contains(search)){
                            find=true;
                            String[] word=line.split(", ");
                            model.addRow(word);
                        }
                    }
                    if(!find){
                        JOptionPane.showMessageDialog(null, "Hall Information not found type again", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    }

            }catch(IOException e){
                JOptionPane.showMessageDialog(null,"An error occured writing to the file");
            }

    }

    public List<String> read_Hall_IDs(){
        List<String> list=new ArrayList<>();
        return list;
    }   
       
}
    
