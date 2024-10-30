package Customer;
    import java.time.*;
    import java.io.*;
    import java.util.*;
    import java.time.format.TextStyle;
    import Scheduler.availibility_schedule;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.time.format.DateTimeParseException;
    import java.util.Locale;
    import javax.swing.JOptionPane;
    import javax.swing.table.DefaultTableModel;
    import javax.swing.*;
    public class Booking extends availibility_schedule{
        private String bookingID;
        private String hallID;
        private String hallName;
        private LocalDateTime bookingDate;
        private int hours;
        private double totalCost;
        private String status;

        public Booking(String Hall_ID, String Hall_Type, String Hall_Location, double cost_per_hour, int Hall_Capacity, 
        String Start_Date, String End_Date, List<String> available_days,String customerID, String M_Start_Date, String M_End_Date, String M_Remarks){
            super(Hall_ID, Hall_Type, Hall_Location, cost_per_hour, Hall_Capacity, Start_Date, End_Date, available_days,  M_Start_Date, M_End_Date, M_Remarks);
        }

        // Getters
        public String getBookingID() { return bookingID; }
        public String getHallName() { return hallName; }
        public LocalDateTime getBookingDate() { return bookingDate; }
        public int getHours() { return hours; }
        public double getTotalCost() { return totalCost; }
        public String getStatus() { return status; }
        public String getHallID() { return hallID; }



        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return String.format("%s,%s,%s,%s,%d,%.2f,%s",
                bookingID, hallID, hallName, bookingDate.format(formatter), hours, totalCost, status);
        }

        // view booking with cancelled bookings and receipts
        
        public void DisplayAllCustomerBookings(String FileName, DefaultTableModel model, String CuID, String filter) {
            try (BufferedReader reader = new BufferedReader(new FileReader(FileName))) {
                String line;
                model.setRowCount(0); // Clear existing rows in the table
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(", ");
                    if (parts.length < 7) {
                        continue;
                    }
                    String customerID = parts[0];
                    if (customerID.equals(CuID)) {
                        String[] rowData = new String[6];
                        rowData[0] = parts[1]; // Booking ID
                        rowData[1] = parts[2]; // HallID
                        rowData[2] = parts[3]; // Hall Type
                        rowData[3] = parts[4]; // Start Date
                        rowData[4] = parts[5]; // End Date
                        rowData[5] = parts[6]; // Remarks

                        if (shouldDisplayBooking(rowData[4], rowData[5], filter)) {
                            model.addRow(rowData);
                        }
                    }
                }
                if (model.getRowCount() == 0) {
                    String message = "No " + (filter.equals("cancelled") ? "cancelled" : filter) + " bookings found for this customer.";
                    JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error reading the file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean shouldDisplayBooking(String startDateStr, String remarks, String filter) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h a", Locale.ENGLISH);
            try {
                LocalDateTime startDate = LocalDateTime.parse(startDateStr, formatter);
                boolean isCancelled=false;
                if(filter.equals("cancelled")){
                    isCancelled =true;
                }
                switch (filter) {
                    case "upcoming":
                        return startDate.isAfter(now) && !isCancelled;
                    case "past":
                        return startDate.isBefore(now) && !isCancelled;
                    case "cancelled":
                        return isCancelled;
                    case "all":
                    default:
                        return true;
                }
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing date: " + startDateStr);
                return false;
            }
        }

        public void DisplayCancelledBookings(String FileName, DefaultTableModel model, String CuID) {
            DisplayAllCustomerBookings(FileName, model, CuID, "cancelled");
        }
        
        public void DisplayReceipts(String FileName, DefaultTableModel model, String CuID) {
            try (BufferedReader reader = new BufferedReader(new FileReader(FileName))) {
                String line;
                model.setRowCount(0); // Clear existing rows in the table
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(", ");
                    if (parts.length < 8) {
                        continue; // Skip lines that don't have all expected fields
                    }

                    // Extract CustomerID
                    String customerID = parts[1].split(": ")[1];

                    if (customerID.equals(CuID)) {
                        String[] rowData = new String[6];
                        rowData[0] = parts[0].split(": ")[1]; // Receipt ID
                        rowData[1] = parts[7].split(": ")[1]; // Booking ID
                        rowData[2] = parts[6].split(": ")[1]; // Amount

                        // Combine Start date and End date for Payment Date
                        String startDate = parts[4].split(": ")[1];
                        String endDate = parts[5].split(": ")[1];
                        rowData[3] = startDate;
                        rowData[4] = endDate; // Payment Method (not provided in the given format)
                        rowData[5] = parts[2].split(": ")[1]; // Hall ID (additional information)

                        model.addRow(rowData);
                    }
                }
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "No receipts found for this customer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error reading the file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public void FilterSpecificBooking(DefaultTableModel model, String bookingId) {
                DefaultTableModel filteredModel = new DefaultTableModel();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    filteredModel.addColumn(model.getColumnName(i));
                }

                boolean found = false;

                for (int i = 0; i < model.getRowCount(); i++) {
                    String currentBookingId = (String) model.getValueAt(i, 0); // Assuming Booking ID is in the first column
                    if (currentBookingId.equals(bookingId)) {
                        Vector<Object> rowData = new Vector<>();
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            rowData.add(model.getValueAt(i, j));
                        }
                        filteredModel.addRow(rowData);
                        found = true;
                        break;
                    }
                }

                if (found) {
                    model.setRowCount(0);
                    for (int i = 0; i < filteredModel.getRowCount(); i++) {
                        model.addRow((Vector) filteredModel.getDataVector().elementAt(i));
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Booking ID. Please enter a valid Booking ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        // cancel booking
        
    public void cancelBooking(String bookingIDToCancel, String CuID) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h a", Locale.ENGLISH);
            List<String> bookings = new ArrayList<>();
            boolean found = false;
            try (BufferedReader reader = new BufferedReader(new FileReader("src\\database\\Booking.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(", ");
                    String bookingCustomerID = parts[0];
                    if (bookingCustomerID.equals(CuID)){
                        bookings.add(line);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error reading booking file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(null, "You have no current bookings.", "No Bookings", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (String booking : bookings) {
                String[] parts = booking.split(", ");
                String bookingID = parts[1];
                String bookingStartDate = parts[4];
                if (bookingID.equals(bookingIDToCancel)) {
                    try {
                        LocalDateTime bookingDate = LocalDateTime.parse(bookingStartDate, formatter);
                        if (canBeCancelled(bookingDate)) {
                            found = true;
                            saveCancelledBooking(booking);
                            removeBookingFromFile(booking);
                            updateReceiptForCancellation(bookingID);
                            JOptionPane.showMessageDialog(null, "Booking with ID " + bookingIDToCancel + " has been cancelled.", "Booking Cancelled", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Booking with ID " + bookingIDToCancel + " cannot be cancelled (must be at least 3 days before the booking date).", "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (DateTimeParseException e) {
                        JOptionPane.showMessageDialog(null, "Error parsing date for booking " + bookingID + ": " + e.getMessage(), "Date Parse Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(null, "Booking not found or cannot be cancelled.", "Booking Not Found", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean canBeCancelled(LocalDateTime bookingDate) {
            LocalDateTime now = LocalDateTime.now();
            return bookingDate.minusDays(3).isAfter(now);
        }

        private void saveCancelledBooking(String booking) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\database\\Cancelled_Booking.txt", true))) {
                writer.write(booking + "\n");
            } catch (IOException e) {
                System.out.println("Error writing to cbookings.txt file: " + e.getMessage());
            }
        }

        private void removeBookingFromFile(String bookingToRemove) {
            List<String> updatedBookings = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("src\\database\\Booking.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(bookingToRemove)) {
                        updatedBookings.add(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading booking file: " + e.getMessage());
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\database\\Booking.txt"))) {
                for (String booking : updatedBookings) {
                    writer.write(booking);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error updating booking file: " + e.getMessage());
            }
        }

        private void updateReceiptForCancellation(String bookingID) {
            String receiptFilePath = "src\\database\\Receipt.txt";
            List<String> updatedReceipts = new ArrayList<>();
            boolean receiptFound = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(receiptFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(", ");

                    // Assuming the booking ID is the last element in the parts array
                    String receiptBookingID = parts[parts.length - 1].split(": ")[1];

                    if (receiptBookingID.equals(bookingID)) {
                        // Receipt matches the booking ID, so we delete it by skipping this line
                        receiptFound = true;
                    } else {
                        updatedReceipts.add(line); // Keep the receipt as it is
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading receipts file: " + e.getMessage());
                return;
            }

            if (!receiptFound) {
                System.out.println("Receipt not found for the cancelled booking.");
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFilePath))) {
                for (String receipt : updatedReceipts) {
                    writer.write(receipt);
                    writer.newLine();
                }
                System.out.println("Receipt updated for the cancelled booking.");
            } catch (IOException e) {
                System.out.println("Error updating receipts file: " + e.getMessage());
            }
        }

    
        // book hall 
    
        public void bookHall(String HallID, LocalDateTime start_Date, LocalDateTime end_Date, String Remarks, String CuID,
                     JLabel INVStartDateLabel, JLabel INVEndDateLabel, JLabel INVHallTypeLabel, JLabel BookingIDLabel,
                     JLabel ReceiptDescLabel, JLabel ReceiptAmountLabel, JLabel ReceiptTotalLabel) {
                Extract_date_Time("src\\database\\Hall_Availability.txt", HallID);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh a");
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");

                List<String> Hall_IDs = ID_Of_Hall();
                List<String> HallTypes = Type_Of_Hall();

                int ix = Hall_IDs.indexOf(HallID);
                String hallType = HallTypes.get(ix);

                // Enter Booking Start Date and Time
                LocalDateTime startDate = LocalDateTime.parse(get_Start_Date(), formatter);
                LocalDateTime endDate = LocalDateTime.parse(get_End_Date(), formatter);
                LocalTime startTime = startDate.toLocalTime();
                LocalTime endTime = endDate.toLocalTime();

                if (start_Date.isBefore(startDate) || start_Date.toLocalTime().isBefore(startTime) || 
                    end_Date.isAfter(endDate) || end_Date.toLocalTime().isAfter(endTime)) {
                    JOptionPane.showMessageDialog(null, "Booking date and time must be within the availability range.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit the method here
                }

                if (!start_Date.isBefore(end_Date)) {
                    JOptionPane.showMessageDialog(null, "Booking start time must be before the end time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit the method here
                }

                List<LocalDateTime[]> bookings = readBookingDateTime(HallID);
                List<LocalDateTime[]> maintenance = read_M_Date_Time(HallID);
                check_available_days(hallType);
                List<String> availableDays = get_available_days();

                String dayName = start_Date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

                boolean isAvailable = true;
                for (LocalDateTime[] period : bookings) {
                    if (start_Date.isBefore(period[1]) && end_Date.isAfter(period[0])) {
                        isAvailable = false;
                        break;
                    }
                }
                for (LocalDateTime[] period : maintenance) {
                    if (start_Date.isBefore(period[1]) && end_Date.isAfter(period[0])) {
                        isAvailable = false;
                        break;
                    }
                }
                if (!availableDays.contains(dayName)) {
                    isAvailable = false;
                }

                if (isAvailable) {
                    double amount = calculatePayment(hallType, start_Date, end_Date);
                    String bookingID = generateBookingID();
                    processPayment(CuID, bookingID, HallID, hallType, start_Date, end_Date, Remarks, amount);

                    INVStartDateLabel.setText(start_Date.format(formatter));
                    INVEndDateLabel.setText(end_Date.format(formatter));
                    INVHallTypeLabel.setText(hallType);
                    BookingIDLabel.setText(bookingID);

                    // Update the ReceiptDescLabel and ReceiptAmountLabel
                    String[] receiptInfo = formatReceiptDescription(hallType, start_Date, end_Date);
                    ReceiptDescLabel.setText(receiptInfo[0]);
                    ReceiptAmountLabel.setText(receiptInfo[1]);
                    ReceiptTotalLabel.setText(receiptInfo[1]);
                } else {
                    JOptionPane.showMessageDialog(null, "Hall not available. Please select a different date or time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
        }

        public void saveBooking(String CuID, String BookingID,String hallID, String HallType, LocalDateTime startDate, LocalDateTime endDate, String remarks) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h a");
            String bookingDetails = String.format("%s, %s, %s, %s, %s, %s, %s",
                    CuID, BookingID, hallID,HallType, startDate.format(formatter), endDate.format(formatter), remarks);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\database\\Booking.txt", true))) {
                writer.write(bookingDetails);
                writer.newLine();
                
            } catch (IOException e) {
                System.out.println("Error saving booking: " + e.getMessage());
            }
        }

        public void processPayment(String CuID,String BookingID,String HallID, String HallType, LocalDateTime startDate, LocalDateTime endDate, String remarks, double amount) {
            // Save the booking after successful payment
            saveBooking(CuID,BookingID, HallID, HallType, startDate, endDate, remarks);
        }

        public double calculatePayment(String hallType, LocalDateTime startDate, LocalDateTime endDate) {
            double costPerHour;

            // Determine the cost per hour based on the hall type
            switch (hallType) {
                case "Auditorium":
                    costPerHour = 300.0;
                    break;
                case "Banquet_Hall":
                    costPerHour = 100.0;
                    break;
                case "Meeting_Room":
                    costPerHour = 50.0;
                    break;
                default:
                    costPerHour = 0.0; // Handle unrecognized hall type
                    JOptionPane.showMessageDialog(null,"Unrecognized hall type: " + hallType);
                    return 0.0;
            }

            // Calculate the duration in hours
            long hours = java.time.Duration.between(startDate, endDate).toHours();

            if (hours <= 0) {
                hours = 1; // Minimum charge for 1 hour
            }

            // Return the total cost
            return hours * costPerHour;
        }

        private String[] formatReceiptDescription(String hallType, LocalDateTime startDate, LocalDateTime endDate) {
            long hours = java.time.Duration.between(startDate, endDate).toHours();
            if (hours <= 0) {
                hours = 1; // Minimum charge for 1 hour
            }

            double costPerHour;
            switch (hallType) {
                case "Auditorium":
                    costPerHour = 300.0;
                    break;
                case "Banquet_Hall":
                    costPerHour = 100.0;
                    break;
                case "Meeting_Room":
                    costPerHour = 50.0;
                    break;
                default:
                    costPerHour = 0.0;
            }

            double totalAmount = hours * costPerHour;

            String description = String.format("%.2f x Hours (RM %.2f)", (double)hours, costPerHour);
            String totalAmountStr = String.format("RM %.2f", totalAmount);

            return new String[]{description, totalAmountStr};
        }
        // save bookings

        private String generateBookingID() {
            String prefix = "BK";
            String uniqueID;
            do {
                uniqueID = prefix + String.format("%06d", new Random().nextInt(1000000));
            } while (isBookingIDExists(uniqueID));
            return uniqueID;
        }

        private boolean isBookingIDExists(String bookingID) {
            try (BufferedReader reader = new BufferedReader(new FileReader("src\\database\\Booking.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(bookingID)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error checking booking ID: " + e.getMessage());
            }
            return false;
        }

        // save receipt
        private String generateReceiptID() {
            String prefix = "INV";
            String uniqueID;
            do {
                uniqueID = prefix + String.format("%06d", new Random().nextInt(1000000));
            } while (isReceiptIDExists(uniqueID));
            return uniqueID;
        }

        private boolean isReceiptIDExists(String receiptID) {
            try (BufferedReader reader = new BufferedReader(new FileReader("src\\database\\Receipt.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(receiptID)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error checking receipt ID: " + e.getMessage());
            }
            return false;
        }

        public String saveReceipt(String ID, String bookingID, String hallID, String hallType, LocalDateTime startDate, LocalDateTime endDate, double amount) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String receiptID = generateReceiptID();
            String receiptDetails = String.format("ReceiptID: %s, CustomerID: %s, HallID: %s, Hall Type: %s, Start date: %s, End date: %s, Amount: %.2f, BookingID: %s",
                    receiptID, ID, hallID, hallType, startDate.format(formatter), endDate.format(formatter), amount, bookingID);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\database\\Receipt.txt", true))) {
                writer.write(receiptDetails);
                writer.newLine();
                System.out.println("Receipt saved successfully. Receipt ID: " + receiptID);
            } catch (IOException e) {
                System.out.println("Error saving receipt: " + e.getMessage());
            }
            return receiptID;
        }


    }