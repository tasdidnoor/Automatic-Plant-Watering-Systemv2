import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class myDATA {

    // ArrayList for storing data (CLO 4 requirement)
    private ArrayList<String> dataRecords;

    // For CSV file
    private PrintWriter csvWriter;
    private DateTimeFormatter formatter;

    // For tracking
    private int recordCount;

    // Constructor
    public myDATA() {
        dataRecords = new ArrayList<>();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        recordCount = 0;
        setupCSV();
    }

    // Set up the CSV file
    private void setupCSV() {
        try {
            File file = new File("moisture_data.csv");
            boolean fileExists = file.exists();

            csvWriter = new PrintWriter(new FileOutputStream(file, true));

            if (!fileExists) {
                csvWriter.println("Timestamp,MoisturePercent,ThresholdVoltage,Action");
                csvWriter.flush();
                System.out.println("Created new data file: moisture_data.csv");
            } else {
                System.out.println("Appending to existing data file: moisture_data.csv");
            }

        } catch (Exception e) {
            System.out.println("CSV error: " + e.getMessage());
        }
    }

    // Add a new reading
    public void addReading(int moisturePercent, double thresholdVoltage, String action) {
        String timestamp = LocalDateTime.now().format(formatter);

        // Create the record string
        String record = timestamp + "," + moisturePercent + "," +
                String.format("%.2f", thresholdVoltage) + "," + action;

        // Add to ArrayList
        dataRecords.add(record);
        recordCount++;

        // Write to CSV file
        csvWriter.println(record);
        csvWriter.flush();

        // Confirm to console
        System.out.println("[DATA] Record " + recordCount + " saved: " + record);
    }

    // Get all records
    public ArrayList<String> getAllRecords() {
        return dataRecords;
    }

    // Get moisture values as int array
    public int[] getMoistureValues() {
        int[] values = new int[dataRecords.size()];
        for (int i = 0; i < dataRecords.size(); i++) {
            String[] parts = dataRecords.get(i).split(",");
            values[i] = Integer.parseInt(parts[1]);  // MoisturePercent is second column
        }
        return values;
    }

    // Get timestamps as String array
    public String[] getTimestamps() {
        String[] timestamps = new String[dataRecords.size()];
        for (int i = 0; i < dataRecords.size(); i++) {
            String[] parts = dataRecords.get(i).split(",");
            timestamps[i] = parts[0];  // Timestamp is first column
        }
        return timestamps;
    }

    // Get the number of records
    public int getRecordCount() {
        return recordCount;
    }

    // Print all records to console (for debugging)
    public void printAllRecords() {
        System.out.println("\nALL DATA RECORDS");
        for (String record : dataRecords) {
            System.out.println(record);
        }
        System.out.println("Total: " + recordCount + " records\n");
    }

    // Close the CSV file
    public void close() {
        if (csvWriter != null) {
            csvWriter.close();
            System.out.println("Data file closed. " + recordCount + " records saved.");
        }
    }
}