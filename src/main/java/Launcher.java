import javafx.application.Application;

/** Launches JavaFX from a class that does not extend {@link Application}. */
public class Launcher {
    /** Starts the Otaku GUI. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
