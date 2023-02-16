import java.time.LocalDate;
import java.time.Period;

/**
 * Represents an event.
 */
public class Event implements Comparable<Event> {
    /**
     * Constructs an event with the given date, category, and description.
     * 
     * @param date the date of the event
     * @param category the category of the event
     * @param description the description of the event
     */
    public Event(LocalDate date, String category, String description) {
        this.date = date;
        this.category = category;
        this.description = description;
    }

    /**
     * Gets the date of this event.
     * 
     * @return the date
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Gets the category of this event.
     * 
     * @return the category
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Gets the description of this event.
     * 
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns a string representation of this event.
     * 
     * @return the event string
     */
    @Override
    public String toString() {
        return this.date + ": " 
            + this.description
            + " (" + this.category + ")"; 
    }

    private LocalDate date;
    private String category;
    private String description;

    /**
     * Helper method to get the spelled-out period.
     * 
     * @param p the period
     * @return the period as a string
     */
    public static String getDifferenceString(Period p) {
        StringBuilder sb = new StringBuilder();

        int years = Math.abs(p.getYears());
        int months = Math.abs(p.getMonths());
        int days = Math.abs(p.getDays());

        // If all components are zero, must be today
        if (years == 0 && months == 0 && days == 0) {
            sb.append("today");
            return sb.toString();
        }

        if (years != 0)
        {
            sb.append(years + " years ");
        }
        if (months != 0)
        {
            sb.append(months + " months ");
        }
        if (days != 0)
        {
            sb.append(days + " days ");
        }

        if (p.isNegative()) {
            sb.insert(0, "in ");
        }
        else {
            sb.append("ago");
        }

        return sb.toString();
    }

    // Comparable<T> implementation:

    /**
     * Compares this event to another based on their dates.
     * 
     * @param other the other event
     * @return whatever compareTo returns
     */
    public int compareTo(Event other) {
        return this.date.compareTo(other.date);
    }    
}
