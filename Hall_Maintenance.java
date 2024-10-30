package Scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Hall_Maintenance extends Hall_Availability {
    private String M_Start_Date;
    private String M_End_Date;
    private String M_Remarks;
    
    Hall_Maintenance(String Hall_ID, String Hall_Type,
     String Hall_Location, double cost_per_hour, int Hall_Capacity,String Start_Date,
     String End_Date, List<String> available_days, 
     String M_Start_Date, String M_End_Date, String M_Remarks){
        super(Hall_ID, Hall_Type, Hall_Location, cost_per_hour, Hall_Capacity, Start_Date, End_Date, available_days);

        this.M_Start_Date=Start_Date;
        this.M_End_Date=End_Date;
        this.M_Remarks=M_Remarks;
    }

    void set_M_Start_Date(String M_Start_Date){
        this.M_Start_Date=M_Start_Date;
    } 
    public String get_M_Start_Date(){
        return M_Start_Date;
    }


    void set_M_End_Date(String End_Date){
        this.M_End_Date=End_Date;
    } 
    public String get_M_End_Date(){
        return M_End_Date;
    }

    void set_M_Remarks(String M_Remarks){
        this.M_Remarks=M_Remarks;
    } 

    public String get_M_Remarks(){
        return M_Remarks;
    }

    public void Schedule_Maintenance(String ID, String Remarks, LocalDateTime Start_Date, LocalDateTime End_Date){

        Extract_date_Time("src\\database\\Hall_Availability.txt",ID);
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd hh a");

        List<String>Hall_IDs=ID_Of_Hall();
        List<String>Hall_type=Type_Of_Hall();
            
            int index=Hall_IDs.indexOf(ID);
            if(index!=-1){

                 String HA_Type=Hall_type.get(index);
                 check_available_days(HA_Type);

                set_M_Remarks(Remarks);
                LocalDateTime startDate = LocalDateTime.parse(get_Start_Date(), formatter);
                LocalDateTime endDate = LocalDateTime.parse(get_End_Date(), formatter);
                //Precaution checking for the date and time
                if (Start_Date.isBefore(startDate) || End_Date.isAfter(endDate)) {
                    JOptionPane.showMessageDialog(null,"Maintenance Date time must be within the range of Hall Availability","Valdiation error", JOptionPane.ERROR_MESSAGE);
                } else if (!Start_Date.isBefore(End_Date)) {
                    JOptionPane.showMessageDialog(null,"Maintenance Start Date Time must be before the End Date Time","Valdiation error", JOptionPane.ERROR_MESSAGE);
                  }else{

                JOptionPane.showMessageDialog(null,"Hall ID: " + ID + ", Hall Type: " + HA_Type+" From: " + Start_Date.format(formatter)+ " " +
                       "To: " + End_Date.format(formatter) +" For "+Remarks);
                
                //store the start and end date and time into the set modifier
                set_M_Start_Date(Start_Date.format(formatter));
                set_M_End_Date(End_Date.format(formatter));
                

                Mark_maintenance(ID);
                }      
            }else{
                JOptionPane.showMessageDialog(null,"Hall ID not found","Valdiation error", JOptionPane.ERROR_MESSAGE);
            }
 
    }

    public void Mark_maintenance(String ID){

        try (PrintWriter write = new PrintWriter(new FileWriter("src\\database\\Hall_Maintenance.txt",true))) {
            write.println(ID+", "+get_M_Start_Date()+
            ", "+get_M_End_Date()+", "+get_M_Remarks()+", ");
            JOptionPane.showMessageDialog(null,"Maintenance Schedule marked successfully for the Hall with ID: "+ID);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    protected List<LocalDateTime[]> read_M_Date_Time(String HallID){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd hh a");
        List<LocalDateTime []> M_Periods=new ArrayList<>();

        try (BufferedReader read = new BufferedReader(new FileReader("src\\database\\Hall_Maintenance.txt"))) {
            String Lines;
            while ((Lines=read.readLine())!=null) {
                String[] line=Lines.split(": |, ");
                if(line[0].equals(HallID)){
                    try{
                        LocalDateTime M_St_Date=LocalDateTime.parse(line[1], formatter);
                        LocalDateTime M_En_Date=LocalDateTime.parse(line[2], formatter);
                        M_Periods.add(new LocalDateTime[]{M_St_Date, M_En_Date});
                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null,"Error parsing the maintenance date in line \n"+Arrays.toString(line),"Validation Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
           JOptionPane.showMessageDialog(null,"Error Reading the file","Validation Error", JOptionPane.ERROR_MESSAGE);
        }
        return M_Periods;

    }
}
