import java.util.Scanner;

/** Starts Otaku and prints its greeting. */
public class Otaku {
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Greets the user, echoes each command, and ends the session when the user enters {@code bye}.
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
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            System.out.println("    " + command);
            System.out.println(DIVIDER);
        }
    }
}
