import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

/** Starts Otaku and prints its greeting. */
public class Otaku {
    private static final Path DATA_FILE = Path.of("data", "otaku.txt");

    /**
     * Greets the user, stores entered tasks, lists them on request, and ends the session on {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Storage storage = new Storage(DATA_FILE);
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (OtakuException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new ArrayList<>();
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = getCommandType(command);
            if (commandType == CommandType.BYE) {
                ui.showGoodbye();
                ui.showDivider();
                break;
            }

            try {
                boolean tasksChanged = processCommand(command, commandType, tasks, ui);
                if (tasksChanged) {
                    storage.save(tasks);
                }
            } catch (OtakuException e) {
                ui.showError(e.getMessage());
            }
            ui.showDivider();
        }
    }

    /** Processes one non-exit command. */
    private static boolean processCommand(String command, CommandType commandType,
            ArrayList<Task> tasks, Ui ui) throws OtakuException {
        if (commandType == CommandType.LIST) {
            ui.showTaskList(tasks);
            return false;
        }
        if (commandType == CommandType.TODO) {
            String description = command.substring(4).trim();
            requireNonEmpty(description, "I need a description after `todo`.");
            addTask(tasks, new Todo(description), ui);
            return true;
        }
        if (commandType == CommandType.DEADLINE) {
            String[] parts = command.substring(8).trim().split("\\s+/by\\s*", 2);
            if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                throw new OtakuException("A deadline needs a description and a time after `/by`.");
            }
            addTask(tasks, new Deadline(parts[0].trim(), parseDate(parts[1].trim())), ui);
            return true;
        }
        if (commandType == CommandType.EVENT) {
            String[] descriptionAndTimes = command.substring(5).trim().split("\\s+/from\\s+", 2);
            if (descriptionAndTimes.length != 2) {
                throw new OtakuException("An event needs a description, a start time after `/from`, and an end time after `/to`.");
            }
            String[] times = descriptionAndTimes[1].split("\\s+/to\\s*", 2);
            if (descriptionAndTimes[0].trim().isEmpty() || times.length != 2
                    || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
                throw new OtakuException("An event needs a description, a start time after `/from`, and an end time after `/to`.");
            }
            LocalDate from = parseDate(times[0].trim());
            LocalDate to = parseDate(times[1].trim());
            if (to.isBefore(from)) {
                throw new OtakuException("An event's end date cannot be before its start date.");
            }
            addTask(tasks, new Event(descriptionAndTimes[0].trim(), from, to), ui);
            return true;
        }
        if (commandType == CommandType.MARK) {
            int taskNumber = parseTaskNumber(command.substring(4).trim(), "mark", tasks.size());
            tasks.get(taskNumber - 1).markAsDone();
            ui.showTaskMarked(tasks.get(taskNumber - 1));
            return true;
        }
        if (commandType == CommandType.UNMARK) {
            int taskNumber = parseTaskNumber(command.substring(6).trim(), "unmark", tasks.size());
            tasks.get(taskNumber - 1).unmarkAsDone();
            ui.showTaskUnmarked(tasks.get(taskNumber - 1));
            return true;
        }
        if (commandType == CommandType.DELETE) {
            int taskNumber = parseTaskNumber(command.substring(6).trim(), "delete", tasks.size());
            Task removedTask = tasks.remove(taskNumber - 1);
            ui.showTaskDeleted(removedTask, tasks.size());
            return true;
        }
        throw new OtakuException(
                "I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.");
    }

    /** Returns the enum value matching the command word, or {@link CommandType#UNKNOWN}. */
    private static CommandType getCommandType(String command) {
        for (CommandType commandType : CommandType.values()) {
            String commandWord = commandType.name().toLowerCase(Locale.ROOT);
            if (command.equals(commandWord) || command.startsWith(commandWord + " ")) {
                return commandType;
            }
        }
        return CommandType.UNKNOWN;
    }

    /** Ensures that a required command argument has content. */
    private static void requireNonEmpty(String value, String message) throws OtakuException {
        if (value.isEmpty()) {
            throw new OtakuException(message);
        }
    }

    /** Parses an ISO date such as {@code 2019-10-15}. */
    private static LocalDate parseDate(String input) throws OtakuException {
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new OtakuException("Please enter dates as yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    /** Parses a valid task number and reports malformed or out-of-range values. */
    private static int parseTaskNumber(String input, String command, int taskCount) throws OtakuException {
        try {
            int taskNumber = Integer.parseInt(input);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new OtakuException("Task " + taskNumber + " does not exist. Choose a number from 1 to "
                        + taskCount + ".");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new OtakuException("Please give a whole task number after `" + command + "`.");
        }
    }

    /** Adds a task and prints the confirmation required by the command format. */
    private static void addTask(ArrayList<Task> tasks, Task task, Ui ui) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }
}
