package Scheduler;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;



public class availibility_schedule extends Hall_Maintenance{

    
    public availibility_schedule(String Hall_ID, String Hall_Type, String Hall_Location, 
    double cost_per_hour, int Hall_Capacity,String Start_Date, 
    String End_Date, List<String> available_days, String M_Start_Date, String M_End_Date, String M_Remarks){
        super(Hall_ID, Hall_Type, Hall_Location, cost_per_hour, Hall_Capacity, Start_Date, End_Date, available_days,  M_Start_Date, M_End_Date, M_Remarks);
    }
    
    

   public void save_date_time_to_file(){
        

        List<String>ha_id=new ArrayList<>();
        List<String>ha_Type=new ArrayList<>();

        ha_id=ID_Of_Hall();
        ha_Type=Type_Of_Hall();
        try (PrintWriter write = new PrintWriter("src\\database\\Hall_Availability.txt")) {

            for (int i=0; i <ha_id.size();i++){
                check_available_days(ha_Type.get(i));
                write.println(ha_id.get(i)+", "+ha_Type.get(i)+", "+get_Start_Date()+", "+get_End_Date()+
                ", "+get_available_days().get(0)+", "+get_available_days().get(get_available_days().size() -1)+", ");

            }
            JOptionPane.showMessageDialog(null,"Hall Availability set successfully");


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public void specify_date_time(LocalDateTime [] date){

        //convert the format entered by the user to a localdate and time using localdatatime package in java               
        date[0]=date[0].with(LocalTime.of(8, 0));
        date[1]=date[1].with(LocalTime.of(18, 0));

        if (!isStartDateBeforeEndDate(date[0], date[1])) {
                JOptionPane.showMessageDialog(null, "Start Date must be before the End Date","Validation error",JOptionPane.ERROR_MESSAGE);
                date[0] = null;
                date[1] = null;
                specify_date_time(date);  // Re-run the method to re-enter the dates
        }else{
                File file=new File("src\\database\\Hall_Maintenance.txt");
                file.delete();
                JOptionPane.showMessageDialog(null,"Maintenance record has been deleted please set the maintenance record again in the maintenance schedule section with the new date and time");
                set_date_time(new LocalDateTime[]{date[0],date[1]});
        }  
    }


    public void set_date_time(LocalDateTime[] dates){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd hh a");

        LocalDateTime start_date=dates[0];
        LocalDateTime end_date=dates[1];

        set_Start_Date(start_date.format(formatter));
        set_End_Date(end_date.format(formatter));
        save_date_time_to_file();
    
    }



    public void Specific_Hall_availability(DefaultTableModel model, String id){

             //call the method of setting the start and end date
            Extract_date_Time("src\\database\\Hall_Availability.txt",id);

            DateTimeFormatter General_date_format=DateTimeFormatter.ofPattern("yyyy-MM-dd hh a");
            LocalDateTime start_date=LocalDateTime.parse(get_Start_Date(),General_date_format);
            LocalDateTime end_date=LocalDateTime.parse(get_End_Date(),General_date_format);

            DateTimeFormatter date_format=DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter time_format=DateTimeFormatter.ofPattern("hh:mm a");
            
            List<String>HallIDs=ID_Of_Hall();
            List<String>HallTypes=Type_Of_Hall();
            List<String> HallPrices=Price_Of_Hall();
            List<String> HallCapacity=Capacity_Of_Hall();

            List<LocalDateTime[]> bookings = readBookingDateTime(id);
            List<LocalDateTime[]> Maintenance_Range=read_M_Date_Time(id);
            
            LocalDateTime M_S_Date=null;
            LocalDateTime M_E_Date=null;
            //check if the value exists by detrimining the number of index in the list
            int index=HallIDs.indexOf(id);

            // create a control structure if the index exits ....if the index ==-1 means the index not in the list
            if(index != -1){
                    // get the hall type of the same index of the hall id 
                    String h_type=HallTypes.get(index);
                    String h_price=HallPrices.get(index);
                    String h_Capacity=HallCapacity.get(index);

                    // set the available days depending on the hall type
                    check_available_days(h_type);

                    // set a list to track the available days in the list
                    List <String> Ava_days=get_available_days();
                    
                    model.addRow(new Object[]{"Hall ID: " + id + ", " + "Hall Type: " + h_type +", "+"Hall Price: "+ h_price+", "+"Hall Capacity: "+h_Capacity});
                    while(!start_date.isAfter(end_date)){
                            String Day_Name = start_date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                            Boolean Is_Under_Maintenance=false;
                            boolean isBooked = false;

                            for (LocalDateTime[] period : bookings) {
                                if (!start_date.toLocalDate().isBefore(period[0].toLocalDate())
                                    && !start_date.toLocalDate().isAfter(period[1].toLocalDate())) {
                                    isBooked = true;
                                    break;
                                }
                            }
                    
                            for(LocalDateTime[] range:Maintenance_Range){
                                if( !start_date.toLocalDate().isBefore(range[0].toLocalDate()) 
                                && !start_date.toLocalDate().isAfter(range[1].toLocalDate()) ){
                                    M_S_Date=range[0];
                                    M_E_Date=range[1];
                                    Is_Under_Maintenance=true;
                                    break;
                                }
                            }                            

                            if(Is_Under_Maintenance){
                                String availability = String.format("Date: %s, Day: %s, Under Maintenance from %s To %s",
                                start_date.format(date_format),
                                Day_Name,
                                M_S_Date.format(time_format),
                                M_E_Date.format(time_format));
                                model.addRow(new String[]{availability});

                            // For the booked case
                            }
                            else if (isBooked) {
                                String availability = String.format("Date: %s, Day: %s, Already Booked",
                                    start_date.format(date_format),
                                    Day_Name);
                                model.addRow(new String[]{availability});
                            }
                            // For the available case
                            else if (Ava_days.contains(Day_Name)) {
                                String availability = String.format("Date: %s, Day: %s, Available from %s To %s",
                                    start_date.format(date_format),
                                    Day_Name,
                                    start_date.format(time_format),
                                    end_date.format(time_format));
                                model.addRow(new String[]{availability});
                            }

                            // For the not available case
                            else {
                                String availability = String.format("Date: %s, Day: %s, Not available",
                                    start_date.format(date_format),
                                    Day_Name);
                                model.addRow(new String[]{availability});
                            }
                            start_date=start_date.plusDays(1);
                    }
            }else{
                JOptionPane.showMessageDialog(null,"Not found","Validation Error",JOptionPane.ERROR_MESSAGE);
            }
    }

    
}
