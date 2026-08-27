/** Represents a task that must be completed by a given time. */
public class Deadline extends Task {
    private final String by;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /** Returns the deadline text supplied by the user. */
    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
