# UI test plan

Compile all files in `src/main/java` into an isolated temporary directory with Java 25. Run each test case from its own empty temporary working directory so its `data/otaku.txt` file cannot affect another case.

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

## Save and load tasks across sessions

- **Aim:** Verify that all task types, completion status, delimiter characters in descriptions, and deletion are persisted across separate Otaku processes.
- **First-session console input:**

  ```text
  todo buy milk | tea
  deadline submit report /by 2026-09-30
  event workshop /from 2026-10-01 /to 2026-10-02
  mark 2
  bye
  ```

- **Expected first-session output:**

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
   [T][ ] buy milk | tea
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Sep 30 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] workshop (from: Oct 01 2026 to: Oct 02 2026)
 Now you have 3 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] submit report (by: Sep 30 2026)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

- **Second-session console input (in the same working directory):**

  ```text
  list
  delete 1
  bye
  ```

- **Expected second-session output:**

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
 Here are the tasks in your list:
1.[T][ ] buy milk | tea
2.[D][X] submit report (by: Sep 30 2026)
3.[E][ ] workshop (from: Oct 01 2026 to: Oct 02 2026)
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] buy milk | tea
 Now you have 2 tasks in the list.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

- **Third-session console input (in the same working directory):**

  ```text
  list
  bye
  ```

- **Expected third-session output:**

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
 Here are the tasks in your list:
1.[D][X] submit report (by: Sep 30 2026)
2.[E][ ] workshop (from: Oct 01 2026 to: Oct 02 2026)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Task types, errors, status changes, and deletion

- **Aim:** Verify all inherited task types render their type icon, incorrect input produces helpful errors without changing the list, marking and unmarking affect only the selected task, and deletion removes only the selected task and renumbers those after it.
- **Console input:**

  ```text
  todo read book
  todo
  deadline return book /by
  deadline return book /by Friday
  deadline return book /by 2019-12-02
  event study /from Monday
  event study /from 2019-02-30 /to 2019-03-01
  event study /from 2019-12-03 /to 2019-12-02
  event study /from 2019-12-02 /to 2019-12-03
  mark one
  mark 1
  mark 4
  mark 2
  unmark 2
  delete two
  delete 4
  delete 2
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
 Please enter dates as yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
 An event needs a description, a start time after `/from`, and an end time after `/to`.
____________________________________________________________
 Please enter dates as yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
 An event's end date cannot be before its start date.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] study (from: Dec 02 2019 to: Dec 03 2019)
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
   [D][X] return book (by: Dec 02 2019)
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: Dec 02 2019)
____________________________________________________________
 Please give a whole task number after `delete`.
____________________________________________________________
 Task 4 does not exist. Choose a number from 1 to 3.
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
 I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
____________________________________________________________
 Here are the tasks in your list:
1.[T][X] read book
2.[E][ ] study (from: Dec 02 2019 to: Dec 03 2019)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```
