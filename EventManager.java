import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
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
        // Ensure that the event list will never be null.
        // Effective Java 2nd Ed, Item 43: "Return empty arrays
        // or collections, not nulls"
        this.events = new ArrayList<Event>();
    }

    // The singleton instance, created as necessary.
    private static EventManager instance = null;

    /**
     * Static method to get the singleton instance.
     * Creates the instance if it does not already exist.
     * 
     * @return the instance
     */ 
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
     * @return true if successful, false if there was an error
     */
    public boolean loadEvents(Path eventsPath) {
        List<Event> newEvents = new ArrayList<Event>();

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
    
                    newEvents.add(event);
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

        // We got here, so loading and parsing succeeded.
        // It should now be safe to update the event list.
        // We'll just construct a new list and let the old
        // one be garbage collected.
        this.events = new ArrayList<Event>(newEvents);
        
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

    /**
     * Saves the events to the file at `eventsPath`.
     * 
     * @param eventsPath the path to the events file
     * @return true if successful, false if there was an error
     */
    public boolean saveEvents(Path eventsPath) {
        // If there are no events, there is nothing to save,
        // but it can still be considered a success.
        if (this.events.size() == 0) {
            return true;
        }

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

            // Write the events to the CSV file
            for (Event event: this.events) {
                String[] entries = new String[] {
                    event.getDate().toString(),
                    event.getCategory(),
                    event.getDescription()
                };                
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

    /**
     * Gets a sorted list of all the categories across all events.
     * 
     * @return the category list
     */
    public List<String> getCategories() {
        // Each category string may be added to the set
        // multiple times, but there will be only one of each. 
        Set<String> categories = new HashSet<String>();
        for (Event event: this.events) {
            categories.add(event.getCategory());
        }

        // Create a new list based on the set of categories,
        // then sort it.
        List<String> result = new ArrayList<String>(categories);
        Collections.sort(result);
        return result;
    }

    private List<Event> events;
}
