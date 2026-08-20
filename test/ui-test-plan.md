# UI test plan

Compile all files in `src/main/java` into an isolated temporary directory with Java 25, then run `Otaku` from that directory for each case.

## Greeting and exit

- **Aim:** Verify that Otaku starts and exits cleanly.
- **Console input:**

  ```text
  bye
  ```

- **Expected output:**

  ```text
____________________________________________________________
  ___ _____  _    _  ___   _
 / _ \_   _|/ \  | |/ / | | |
| | | || | / _ \ | ' /| | | |
| |_| || |/ ___ \| . \| |_| |
 \___/ |_/_/   \_\_|\_\\___/
Hello! I'm Otaku.
What can I do for you?
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Task types, errors, and status changes

- **Aim:** Verify all inherited task types render their type icon, incorrect input produces helpful errors without changing the list, and marking and unmarking affect only the selected task.
- **Console input:**

  ```text
  todo read book
  todo
  deadline return book /by
  deadline return book /by Friday
  event study /from Monday
  event study /from Monday /to Tuesday
  mark one
  mark 1
  mark 4
  mark 2
  unmark 2
  blah
  list
  bye
  ```

- **Expected output:**

  ```text
____________________________________________________________
  ___ _____  _    _  ___   _
 / _ \_   _|/ \  | |/ / | | |
| | | || | / _ \ | ' /| | | |
| |_| || |/ ___ \| . \| |_| |
 \___/ |_/_/   \_\_|\_\\___/
Hello! I'm Otaku.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
 I need a description after `todo`.
____________________________________________________________
 A deadline needs a description and a time after `/by`.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Friday)
 Now you have 2 tasks in the list.
____________________________________________________________
 An event needs a description, a start time after `/from`, and an end time after `/to`.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] study (from: Monday to: Tuesday)
 Now you have 3 tasks in the list.
____________________________________________________________
 Please give a whole task number after `mark`.
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
 Task 4 does not exist. Choose a number from 1 to 3.
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Friday)
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: Friday)
____________________________________________________________
 I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.
____________________________________________________________
 Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Friday)
3.[E][ ] study (from: Monday to: Tuesday)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```
