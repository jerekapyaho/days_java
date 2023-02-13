import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.time.Period;
import java.time.LocalDate;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;
import java.io.Writer;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.ICSVParser;

/**
 * Singleton class for handling events.
 */
public class EventManager {
    // Private constructor to prevent instantiation.
    private EventManager() {
        this.events = new ArrayList<Event>();
    }

    // The singleton instance, created as necessary.
    private static EventManager instance = null;

    // Static method to get the singleton instance. 
    // Creates the instance if it does not already exist.
    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    /**
     * Loads the events from the file given in eventsPath.
     * 
     * @param eventsPath the path to the events file
     * @return
     */
    public boolean loadEvents(Path eventsPath) {
        // Read all the lines from the events file using 
        // our helper based on opencsv.
        try {
            List<Map<String, String>> lineMaps = readLineByLine(eventsPath);
            //System.out.println(lineMaps);

            for (Map<String, String> map : lineMaps) {
                LocalDate date = null;
                String dateString = map.get("date");
                try {
                    date = LocalDate.parse(dateString);
    
                    Event event = new Event(
                        date,
                        map.get("category"),
                        map.get("description")
                    );
    
                    this.events.add(event);
                }
                catch (DateTimeParseException dtpe) {
                    System.err.println("bad date: " + dateString);
                    continue;
                }
            }
        }
        catch (CsvValidationException cve) {
            cve.printStackTrace();
            return false;
        }
        catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return false;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // Read the CSV file one line at a time using a header-aware
    // CSV reader from the opencsv library. Discards invalid lines.
    private List<Map<String, String>> readLineByLine(Path filePath)
            throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(reader)) {
                Map<String, String> map;
                while ((map = csvReader.readMap()) != null) {
                    list.add(map);
                }
            }
        }
        return list;
    }

    public boolean saveEvents(Path eventsPath) {
        try {
            Writer writer = Files.newBufferedWriter(
                eventsPath, 
                StandardCharsets.UTF_8
            );
     
            ICSVWriter csvWriter = new CSVWriterBuilder(writer)
                .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVParser.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build(); 

            // Write the header row
            csvWriter.writeNext(
                new String[] { 
                    "date", 
                    "category", 
                    "description"
                }
            );
            System.out.println("date,category,description");

            // Write the events
            for (Event event: this.events) {
                String[] entries = new String[] {
                    event.getDate().toString(),
                    event.getCategory(),
                    event.getDescription()
                };
                System.out.println(
                    entries[0] + "," +
                    entries[1] + "," + 
                    entries[2]
                );
                
                csvWriter.writeNext(entries);
            }

            csvWriter.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Gets the list of events.
     * 
     * @return the event list
     */
    public List<Event> getEvents() {
        return this.events;
    }

    private List<Event> events;
}
