import java.util.Scanner;

/** Starts Otaku and prints its greeting. */
public class Otaku {
    private static final String DIVIDER = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

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
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            try {
                taskCount = processCommand(command, tasks, taskCount);
            } catch (OtakuException e) {
                System.out.println(" " + e.getMessage());
            }
            System.out.println(DIVIDER);
        }
    }

    /** Processes one non-exit command and returns the resulting number of tasks. */
    private static int processCommand(String command, Task[] tasks, int taskCount) throws OtakuException {
        if (command.equals("list")) {
            printList(tasks, taskCount);
            return taskCount;
        }
        if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.substring(4).trim();
            requireNonEmpty(description, "I need a description after `todo`.");
            return addTask(tasks, taskCount, new Todo(description));
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            String[] parts = command.substring(8).trim().split("\\s+/by\\s*", 2);
            if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                throw new OtakuException("A deadline needs a description and a time after `/by`.");
            }
            return addTask(tasks, taskCount, new Deadline(parts[0].trim(), parts[1].trim()));
        }
        if (command.equals("event") || command.startsWith("event ")) {
            String[] descriptionAndTimes = command.substring(5).trim().split("\\s+/from\\s+", 2);
            if (descriptionAndTimes.length != 2) {
                throw new OtakuException("An event needs a description, a start time after `/from`, and an end time after `/to`.");
            }
            String[] times = descriptionAndTimes[1].split("\\s+/to\\s*", 2);
            if (descriptionAndTimes[0].trim().isEmpty() || times.length != 2
                    || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
                throw new OtakuException("An event needs a description, a start time after `/from`, and an end time after `/to`.");
            }
            return addTask(tasks, taskCount,
                    new Event(descriptionAndTimes[0].trim(), times[0].trim(), times[1].trim()));
        }
        if (command.equals("mark") || command.startsWith("mark ")) {
            int taskNumber = parseTaskNumber(command.substring(4).trim(), "mark", taskCount);
            tasks[taskNumber - 1].markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + tasks[taskNumber - 1]);
            return taskCount;
        }
        if (command.equals("unmark") || command.startsWith("unmark ")) {
            int taskNumber = parseTaskNumber(command.substring(6).trim(), "unmark", taskCount);
            tasks[taskNumber - 1].unmarkAsDone();
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   " + tasks[taskNumber - 1]);
            return taskCount;
        }
        throw new OtakuException("I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.");
    }

    /** Prints every task currently stored in the task list. */
    private static void printList(Task[] tasks, int taskCount) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
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
    private static int addTask(Task[] tasks, int taskCount, Task task) throws OtakuException {
        if (taskCount >= MAX_TASKS) {
            throw new OtakuException("Your task list is full. Remove a task before adding another one.");
        }

        tasks[taskCount] = task;
        int newTaskCount = taskCount + 1;
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + newTaskCount + " tasks in the list.");
        return newTaskCount;
    }
}
