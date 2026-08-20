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
        String banner = "  ___ _____  _    _  ___   _ \n"
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

            if (command.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5).trim());
                if (taskNumber >= 1 && taskNumber <= taskCount) {
                    tasks[taskNumber - 1].markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[taskNumber - 1]);
                }
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7).trim());
                if (taskNumber >= 1 && taskNumber <= taskCount) {
                    tasks[taskNumber - 1].unmarkAsDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[taskNumber - 1]);
                }
            } else if (command.startsWith("todo ")) {
                taskCount = addTask(tasks, taskCount, new Todo(command.substring(5).trim()));
            } else if (command.startsWith("deadline ")) {
                String[] parts = command.substring(9).split(" /by ", 2);
                if (parts.length == 2) {
                    taskCount = addTask(tasks, taskCount, new Deadline(parts[0].trim(), parts[1].trim()));
                }
            } else if (command.startsWith("event ")) {
                String[] descriptionAndTimes = command.substring(6).split(" /from ", 2);
                if (descriptionAndTimes.length == 2) {
                    String[] times = descriptionAndTimes[1].split(" /to ", 2);
                    if (times.length == 2) {
                        taskCount = addTask(tasks, taskCount,
                                new Event(descriptionAndTimes[0].trim(), times[0].trim(), times[1].trim()));
                    }
                }
            } else if (taskCount < MAX_TASKS) {
                taskCount = addTask(tasks, taskCount, new Todo(command));
            }
            System.out.println(DIVIDER);
        }
    }

    /** Adds a task and prints the confirmation required by the command format. */
    private static int addTask(Task[] tasks, int taskCount, Task task) {
        if (taskCount >= MAX_TASKS) {
            return taskCount;
        }

        tasks[taskCount] = task;
        int newTaskCount = taskCount + 1;
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + newTaskCount + " tasks in the list.");
        return newTaskCount;
    }
}
