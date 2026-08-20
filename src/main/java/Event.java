/** Represents a task that occurs between a start and end time. */
public class Event extends Task {
    private final String from;
    private final String to;

    /** Creates an incomplete event task. */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
