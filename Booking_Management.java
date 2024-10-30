package Adminstrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import Customer.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Booking_Management extends Booking{
        Booking_Management(String Hall_ID, String Hall_Type, String Hall_Location, double cost_per_hour, int Hall_Capacity,
            String Start_Date, String End_Date, List<String> available_days, String customerID, String M_Start_Date,
            String M_End_Date, String M_Remarks) {
        super(Hall_ID, Hall_Type, Hall_Location, cost_per_hour, Hall_Capacity, Start_Date, End_Date, available_days, customerID,
                M_Start_Date, M_End_Date, M_Remarks);
        //TODO Auto-generated constructor stub
    }

        public void viewBookings(DefaultTableModel model, String state) {
        try {
            File bookingsFile = new File("src\\database\\Booking.txt");
            File cancelledBookingsFile = new File("src\\database\\Cancelled_Booking.txt");
            File receiptsFile = new File("src\\database\\Receipt.txt");
    
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h a");
    
            switch(state){
                case "Upcoming":
                    printBookings(model, bookingsFile, true, now, formatter);
                    break;
                case "Past":
                    printBookings(model, bookingsFile, false, now, formatter);
                    break;
                case "Cancelled":
                    printCancelledBookings(model,cancelledBookingsFile, now, formatter);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Unable to request");
                
            }    
        } catch (FileNotFoundException e) {
            System.out.println("Error: One or more required files not found.");
        }
    }
    
    private void printBookings(DefaultTableModel model,File file, boolean upcoming, LocalDateTime now, DateTimeFormatter formatter) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        boolean hasBookings = false;
    
        while (scanner.hasNextLine()) {
            String bookingInfo = scanner.nextLine();
            String[] bookingData = bookingInfo.split(", ");
            if (bookingData.length >= 7) {
                LocalDateTime bookingDate = LocalDateTime.parse(bookingData[4], formatter);
                if ((upcoming && bookingDate.isAfter(now)) || (!upcoming && bookingDate.isBefore(now))) {
                    model.addRow(bookingData);
                    hasBookings = true;
                }
            }
        }
    
        if (!hasBookings) {
            JOptionPane.showMessageDialog(null,"No " + (upcoming ? "upcoming" : "past") + " bookings found.");
        }
    
        scanner.close();
    }
    
    private void printCancelledBookings(DefaultTableModel model, File file, LocalDateTime now, DateTimeFormatter formatter) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        boolean hasBookings = false;
    
        while (scanner.hasNextLine()) {
            String bookingInfo = scanner.nextLine();
            String[] bookingData = bookingInfo.split(", ");
            if (bookingData.length >= 6) {
                model.addRow(bookingData);
                hasBookings = true;
            }
        }
    
        if (!hasBookings) {
            JOptionPane.showMessageDialog(null,"No cancelled bookings found.","Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    
        scanner.close();
    }
    
    public static void main(String[] args){
        Booking_Management b= new Booking_Management(null, null, null, 0, 0, null, null, null, null, null, null, null);
//        b.viewBookings();
    }

    
    
}
