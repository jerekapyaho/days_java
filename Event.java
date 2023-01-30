import java.time.LocalDate;

public class Event {
    public Event(LocalDate date, String category, String description) {
        this.date = date;
        this.category = category;
        this.description = description;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public String getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.date + ": " 
            + this.description
            + " (" + this.category + ")"; 
    }

    private LocalDate date;
    private String category;
    private String description;
}