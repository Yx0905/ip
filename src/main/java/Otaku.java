import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/** Provides Otaku's command-processing logic for both the CLI and JavaFX interfaces. */
public class Otaku {
    private static final String DIVIDER = "____________________________________________________________";
    private static final Path DATA_FILE = Path.of("data", "otaku.txt");
    private final Storage storage;
    private final ArrayList<Task> tasks;
    private final String loadWarning;

    /** Creates an Otaku instance backed by the default data file. */
    public Otaku() {
        this(DATA_FILE);
    }

    /** Creates an instance backed by the given file, which is useful for isolated tests. */
    Otaku(Path dataFile) {
        storage = new Storage(dataFile);
        ArrayList<Task> loadedTasks;
        String warning = "";
        try {
            loadedTasks = storage.load();
        } catch (OtakuException e) {
            loadedTasks = new ArrayList<>();
            warning = " " + e.getMessage() + "\n Starting with an empty task list instead.";
        }
        tasks = loadedTasks;
        loadWarning = warning;
    }

    /** Runs the original command-line interface. */
    public static void main(String[] args) {
        Otaku otaku = new Otaku();
        System.out.println(DIVIDER);
        System.out.println(otaku.getGreeting());
        System.out.println(DIVIDER);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(otaku.getResponse(command));
            System.out.println(DIVIDER);
            if (getCommandType(command) == CommandType.BYE) {
                break;
            }
        }
    }

    /** Returns the greeting shown when either interface starts. */
    public String getGreeting() {
        String banner = "  ___ _____  _    _  ___   _\n"
                + " / _ \\_   _|/ \\  | |/ / | | |\n"
                + "| | | || | / _ \\ | ' /| | | |\n"
                + "| |_| || |/ ___ \\| . \\| |_| |\n"
                + " \\___/ |_/_/   \\_\\_|\\_\\\\___/";
        String greeting = banner + "\nHello! I'm Otaku.\nWhat can I do for you?";
        return loadWarning.isEmpty() ? greeting : greeting + "\n" + loadWarning;
    }

    /** Processes one user command and returns the text to display. */
    public String getResponse(String command) {
        CommandType commandType = getCommandType(command);
        if (commandType == CommandType.BYE) {
            return "Bye. Hope to see you again soon!";
        }
        try {
            CommandResult result = processCommand(command, commandType, tasks);
            if (result.tasksChanged()) {
                storage.save(tasks);
            }
            return result.message();
        } catch (OtakuException e) {
            return " " + e.getMessage();
        }
    }

    /** Processes one non-exit command. */
    private static CommandResult processCommand(String command, CommandType type,
            ArrayList<Task> tasks) throws OtakuException {
        if (type == CommandType.LIST) {
            return new CommandResult(formatTasks(tasks, null), false);
        }
        if (type == CommandType.FIND) {
            String keyword = command.substring(4).trim();
            requireNonEmpty(keyword, "I need a keyword after `find`.");
            return new CommandResult(formatTasks(tasks, keyword), false);
        }
        if (type == CommandType.TODO) {
            String description = command.substring(4).trim();
            requireNonEmpty(description, "I need a description after `todo`.");
            return addTask(tasks, new Todo(description));
        }
        if (type == CommandType.DEADLINE) {
            String[] parts = command.substring(8).trim().split("\\s+/by\\s*", 2);
            if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                throw new OtakuException("A deadline needs a description and a time after `/by`.");
            }
            return addTask(tasks, new Deadline(parts[0].trim(), parseDate(parts[1].trim())));
        }
        if (type == CommandType.EVENT) {
            String[] descriptionAndTimes = command.substring(5).trim().split("\\s+/from\\s+", 2);
            if (descriptionAndTimes.length != 2) {
                throw eventFormatException();
            }
            String[] times = descriptionAndTimes[1].split("\\s+/to\\s*", 2);
            if (descriptionAndTimes[0].trim().isEmpty() || times.length != 2
                    || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
                throw eventFormatException();
            }
            LocalDate from = parseDate(times[0].trim());
            LocalDate to = parseDate(times[1].trim());
            if (to.isBefore(from)) {
                throw new OtakuException("An event's end date cannot be before its start date.");
            }
            return addTask(tasks, new Event(descriptionAndTimes[0].trim(), from, to));
        }
        if (type == CommandType.MARK || type == CommandType.UNMARK) {
            String word = type.name().toLowerCase(Locale.ROOT);
            int number = parseTaskNumber(command.substring(word.length()).trim(), word, tasks.size());
            Task task = tasks.get(number - 1);
            if (type == CommandType.MARK) {
                task.markAsDone();
                return new CommandResult(" Nice! I've marked this task as done:\n   " + task, true);
            }
            task.unmarkAsDone();
            return new CommandResult(" OK, I've marked this task as not done yet:\n   " + task, true);
        }
        if (type == CommandType.DELETE) {
            int number = parseTaskNumber(command.substring(6).trim(), "delete", tasks.size());
            Task removed = tasks.remove(number - 1);
            return new CommandResult(" Noted. I've removed this task:\n   " + removed
                    + "\n Now you have " + tasks.size() + " tasks in the list.", true);
        }
        throw new OtakuException(
                "I don't recognize that command. Try todo, deadline, event, list, find, mark, unmark, delete, or bye.");
    }

    private static OtakuException eventFormatException() {
        return new OtakuException(
                "An event needs a description, a start time after `/from`, and an end time after `/to`.");
    }

    /** Returns the command word's enum value, or {@link CommandType#UNKNOWN}. */
    private static CommandType getCommandType(String command) {
        for (CommandType type : CommandType.values()) {
            String word = type.name().toLowerCase(Locale.ROOT);
            if (command.equals(word) || command.startsWith(word + " ")) {
                return type;
            }
        }
        return CommandType.UNKNOWN;
    }

    /** Formats either all tasks or those matching a keyword. */
    private static String formatTasks(ArrayList<Task> tasks, String keyword) {
        String heading = keyword == null ? " Here are the tasks in your list:"
                : " Here are the matching tasks in your list:";
        StringBuilder response = new StringBuilder(heading);
        int number = 1;
        for (Task task : tasks) {
            if (keyword == null || task.containsKeyword(keyword)) {
                response.append('\n').append(number++).append('.').append(task);
            }
        }
        return response.toString();
    }

    private static void requireNonEmpty(String value, String message) throws OtakuException {
        if (value.isEmpty()) {
            throw new OtakuException(message);
        }
    }

    private static LocalDate parseDate(String input) throws OtakuException {
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new OtakuException("Please enter dates as yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    private static int parseTaskNumber(String input, String command, int taskCount) throws OtakuException {
        try {
            int number = Integer.parseInt(input);
            if (number < 1 || number > taskCount) {
                throw new OtakuException("Task " + number + " does not exist. Choose a number from 1 to "
                        + taskCount + ".");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new OtakuException("Please give a whole task number after `" + command + "`.");
        }
    }

    private static CommandResult addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        return new CommandResult(" Got it. I've added this task:\n   " + task
                + "\n Now you have " + tasks.size() + " tasks in the list.", true);
    }

    /** Couples a command's display text with whether the task file needs saving. */
    private record CommandResult(String message, boolean tasksChanged) {
    }
}
