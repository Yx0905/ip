package otaku.ui;

import java.util.List;
import java.util.Scanner;

import otaku.task.Task;

/** Handles all console input and output for Otaku. */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = "  ___ _____  _    _  ___   _\n"
            + " / _ \\_   _|/ \\  | |/ / | | |\n"
            + "| | | || | / _ \\ | ' /| | | |\n"
            + "| |_| || |/ ___ \\| . \\| |_| |\n"
            + " \\___/ |_/_/   \\_\\_|\\_\\\\___/";

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Returns whether another command is available to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Shows the greeting displayed when Otaku starts. */
    public void showWelcome() {
        showDivider();
        System.out.println(BANNER);
        System.out.println("Hello! I'm Otaku.");
        System.out.println("What can I do for you?");
        showDivider();
    }

    /** Shows the farewell displayed when Otaku exits. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /** Shows a horizontal divider between command responses. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Shows an error message to the user. */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /** Explains that loading failed and Otaku will start with no tasks. */
    public void showLoadingError(String message) {
        showError(message);
        System.out.println(" Starting with an empty task list instead.");
        showDivider();
    }

    /** Shows every task currently stored in the task list. */
    public void showTaskList(List<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Confirms that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        showTaskCount(taskCount);
    }

    /** Confirms that a task was marked as done. */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /** Confirms that a task was marked as not done. */
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /** Confirms that a task was deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        showTaskCount(taskCount);
    }

    /** Shows the current number of tasks. */
    private void showTaskCount(int taskCount) {
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }
}
