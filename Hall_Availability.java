package Scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

public class Hall_Availability extends Hall_Management{

    private String Start_Date;
    private String End_Date;
    private List<String> available_days;

    public Hall_Availability(String Hall_ID, String Hall_Type, String Hall_Location, 
    double cost_per_hour, int Hall_Capacity, String Start_Date, String End_Date, List<String> available_days){
        super(Hall_ID, Hall_Type, Hall_Location, cost_per_hour, Hall_Capacity);

        this.Start_Date=Start_Date;
        this.End_Date=End_Date;
        this.available_days=new ArrayList<>();
    }

    void set_Start_Date(String Start_Date){
        this.Start_Date=Start_Date;
    } 
    public String get_Start_Date(){
        return Start_Date;
    }


    void set_End_Date(String End_Date){
        this.End_Date=End_Date;
    } 
    public String get_End_Date(){
        return End_Date;
    }


        // make encabsualtion for the available days of the halls depending on their type

    void set_available_days(List <String> available_days){
        this.available_days=available_days;
    }

     public List<String> get_available_days(){
        return available_days;
    }

     @Override
    public List<String> read_Hall_IDs(){
        
        List<String> list=new ArrayList<>();
        try (BufferedReader read = new BufferedReader(new FileReader("src\\database\\Hall_Info.txt"))) {
            String Lines;
            while((Lines=read.readLine())!=null){
                String [] line=Lines.split(", ");
                String id=line[0];
                String type=line[1];
                String Price=line[4];
                String Capacity=line[3];
                list.add(id+", "+type+", "+Capacity+", "+Price);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null,"Error reading the file","Valdiation error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
            return list;

    }   
    
    //creating lists methods to extract the Hall id with the type for setting availability and booking
    
    public List<String> Type_Of_Hall(){
        List<String>hall_information=new ArrayList<>();
        hall_information=read_Hall_IDs();
        List<String>Hall_Types=new ArrayList<>();

        for(String info: hall_information){
            String[] parts = info.split(", ");
            Hall_Types.add(parts[1]);

         }
        return Hall_Types;
    }

    public List<String> ID_Of_Hall(){
        List<String>hall_information=new ArrayList<>();
        hall_information=read_Hall_IDs();
        List<String>Hall_ID=new ArrayList<>();

        for(String info: hall_information){
            String[] parts = info.split(", ");
            Hall_ID.add(parts[0]);

         }
        return Hall_ID;
    }
    
    public List<String> Price_Of_Hall(){
        List<String>hall_information=new ArrayList<>();
        hall_information=read_Hall_IDs();
        List<String> Price=new ArrayList<>();

        for(String info: hall_information){
            String[] parts = info.split(", ");
            Price.add(parts[3]);

         }
        return Price;
    }
    
    public List<String> Capacity_Of_Hall(){
        List<String>hall_information=new ArrayList<>();
        hall_information=read_Hall_IDs();
        List<String> capacity=new ArrayList<>();

        for(String info: hall_information){
            String[] parts = info.split(", ");
            capacity.add(parts[2]);

         }
        return capacity;
    }

    protected Boolean Year_Valid(String date){
        int year=Integer.parseInt(date.substring(0,4));
        return year >=2024 ;
    }

    protected boolean isStartDateBeforeEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.isBefore(endDate);
    }
    //this is for the rest of roles
    //Make sure to use this function when reading any file either by inhereting it or applying polymoriphism
 public void Extract_date_Time(String FileName,String id) {
        // TODO Auto-generated method stub
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(FileName))) {
                String Lines;
                String [] line=null;
                while((Lines=reader.readLine())!=null){
                    line=Lines.split(", ");
                }
                set_Start_Date(line[2]);
                set_End_Date(line[3]);
            }
        } catch (IOException e) {
            // TO Auto-generated catch block
            JOptionPane.showMessageDialog(null,"Error Reading the file","Valdiation error", JOptionPane.ERROR_MESSAGE);
        }    
    }
//our assumption for the day's availbility for each Hall
    public void check_available_days(String Hall_Type){
        List<String> all_days =Arrays.asList("Monday","Tuesday","Wednesday","Thursday","Friday");
            if(Hall_Type.equals("Auditorium")){
                set_available_days(all_days);
            }

            else if(Hall_Type.equals("Meeting_Room")){
                set_available_days(all_days);
            }

            else if(Hall_Type.equals("Banquet_Hall")){
                set_available_days(all_days);
            }

    }
    protected List<LocalDateTime[]> readBookingDateTime(String hallID) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h a");
        List<LocalDateTime[]> bookingPeriods = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("src\\database\\Booking.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": |, ");
                if (parts[2].equals(hallID)) {
                    try {
                        LocalDateTime start = LocalDateTime.parse(parts[4], formatter);
                        LocalDateTime end = LocalDateTime.parse(parts[5], formatter);
                        bookingPeriods.add(new LocalDateTime[]{start, end});
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null,"Error Parsing the booking Date","Valdiation error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (IOException e) {
           JOptionPane.showMessageDialog(null,"Error Parsing the Booking file","Valdiation error", JOptionPane.ERROR_MESSAGE);
        }

        return bookingPeriods;
    }
    
}

