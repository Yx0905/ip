import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/** Starts Otaku and prints its greeting. */
public class Otaku {
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Greets the user, stores entered tasks, lists them on request, and ends the session on {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = "  ___ _____  _    _  ___   _\n"
                + " / _ \\_   _|/ \\  | |/ / | | |\n"
                + "| | | || | / _ \\ | ' /| | | |\n"
                + "| |_| || |/ ___ \\| . \\| |_| |\n"
                + " \\___/ |_/_/   \\_\\_|\\_\\\\___/";

        System.out.println(DIVIDER);
        System.out.println(banner);
        System.out.println("Hello! I'm Otaku.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            CommandType commandType = getCommandType(command);
            if (commandType == CommandType.BYE) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            try {
                processCommand(command, commandType, tasks);
            } catch (OtakuException e) {
                System.out.println(" " + e.getMessage());
            }
            System.out.println(DIVIDER);
        }
    }

    /** Processes one non-exit command. */
    private static void processCommand(String command, CommandType commandType,
            ArrayList<Task> tasks) throws OtakuException {
        if (commandType == CommandType.LIST) {
            printList(tasks);
            return;
        }
        if (commandType == CommandType.TODO) {
            String description = command.substring(4).trim();
            requireNonEmpty(description, "I need a description after `todo`.");
            addTask(tasks, new Todo(description));
            return;
        }
        if (commandType == CommandType.DEADLINE) {
            String[] parts = command.substring(8).trim().split("\\s+/by\\s*", 2);
            if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                throw new OtakuException("A deadline needs a description and a time after `/by`.");
            }
            addTask(tasks, new Deadline(parts[0].trim(), parts[1].trim()));
            return;
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
            addTask(tasks, new Event(descriptionAndTimes[0].trim(), times[0].trim(), times[1].trim()));
            return;
        }
        if (commandType == CommandType.MARK) {
            int taskNumber = parseTaskNumber(command.substring(4).trim(), "mark", tasks.size());
            tasks.get(taskNumber - 1).markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + tasks.get(taskNumber - 1));
            return;
        }
        if (commandType == CommandType.UNMARK) {
            int taskNumber = parseTaskNumber(command.substring(6).trim(), "unmark", tasks.size());
            tasks.get(taskNumber - 1).unmarkAsDone();
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   " + tasks.get(taskNumber - 1));
            return;
        }
        if (commandType == CommandType.DELETE) {
            int taskNumber = parseTaskNumber(command.substring(6).trim(), "delete", tasks.size());
            Task removedTask = tasks.remove(taskNumber - 1);
            System.out.println(" Noted. I've removed this task:");
            System.out.println("   " + removedTask);
            System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
            return;
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

    /** Prints every task currently stored in the task list. */
    private static void printList(ArrayList<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Ensures that a required command argument has content. */
    private static void requireNonEmpty(String value, String message) throws OtakuException {
        if (value.isEmpty()) {
            throw new OtakuException(message);
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
    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }
}
