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
        String[] tasks = new String[MAX_TASKS];
        boolean[] isDone = new boolean[MAX_TASKS];
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
                    System.out.println((i + 1) + ".[" + (isDone[i] ? "X" : " ") + "] " + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5).trim());
                if (taskNumber >= 1 && taskNumber <= taskCount) {
                    isDone[taskNumber - 1] = true;
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   [X] " + tasks[taskNumber - 1]);
                }
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
            }
            System.out.println(DIVIDER);
        }
    }
}
