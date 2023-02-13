import java.time.LocalDate;
import java.time.Period;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

public class Days {
    public static void main(String... args) {
        LocalDate today = LocalDate.now();
        String birthdateString = System.getenv("BIRTHDATE");
        if (birthdateString != null && !birthdateString.isBlank()) {
            LocalDate birthdate = LocalDate.parse(birthdateString);
            if (today.getMonth() == birthdate.getMonth() && 
                today.getDayOfMonth() == birthdate.getDayOfMonth()) {
                System.out.println("Happy birthday!");
            }
        }
        
        String userHomeDirectory = System.getProperty("user.home");
        if (userHomeDirectory.isBlank()) {
            System.out.println("Unable to determine user home directory");
            System.exit(-1);
        }

        Path daysPath = Paths.get(userHomeDirectory, ".days");
        if (Files.notExists(daysPath)) {
            System.out.println(daysPath + " directory does not exist, please create it");
            System.exit(-1);
        }
        Path eventsPath = daysPath.resolve("events.csv");
        if (Files.notExists(eventsPath)) {
            System.out.println(eventsPath + " file not found");
            System.exit(-1);
        }

        //System.out.println("Using EventManager to load events");
        EventManager eventManager = EventManager.getInstance();
        boolean success = eventManager.loadEvents(eventsPath);
        if (!success) {
            System.err.println("Error loading events");
            System.exit(-1);
        }
        //System.out.println("Events loaded successfully");
        List<Event> events = eventManager.getEvents();

        // If we are still here, we might have some events
        // in the list. Print them out:
        for (Event event: events) {
            System.out.print(event + " -- ");

            Period difference = Period.between(event.getDate(), today);
            //System.out.println("diff = " + diff);

            System.out.println(Event.getDifferenceString(difference));
        }

        // Test adding an event and saving events.
        // If you run the program repeatedly, you will get one extra event
        // per run (and will have to clean up later).
        System.out.println("Event count = " + events.size());
        Event newEvent = new Event(
            LocalDate.now(), 
            "test", 
            "I just added this!"
        );
        events.add(newEvent);
        success = eventManager.saveEvents(eventsPath);
        if (!success) {
            System.out.println("Error saving events");
            System.exit(-1);
        }
        System.out.println("Event count = " + events.size());
    }
}
