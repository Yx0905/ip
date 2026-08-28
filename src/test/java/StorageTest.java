import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence using an isolated temporary directory. */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void load_missingFile_returnsEmptyTaskList() throws OtakuException {
        Storage storage = new Storage(tempDirectory.resolve("data").resolve("tasks.txt"));

        assertEquals(List.of(), storage.load());
    }

    @Test
    public void saveAndLoad_allTaskTypesAndStatuses_restoresEquivalentTasks() throws OtakuException {
        Path dataFile = tempDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read | revise 日本語");
        Deadline deadline = new Deadline("submit work", LocalDate.of(2026, 9, 1));
        Event event = new Event("orientation", LocalDate.of(2026, 8, 20), LocalDate.of(2026, 8, 21));
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event));
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("[T][ ] read | revise 日本語", loadedTasks.get(0).toString());
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("[D][X] submit work (by: Sep 01 2026)", loadedTasks.get(1).toString());
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("[E][ ] orientation (from: Aug 20 2026 to: Aug 21 2026)", loadedTasks.get(2).toString());
    }

    @Test
    public void save_existingFile_replacesPreviousContents() throws OtakuException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("old task"), new Todo("another old task")));

        storage.save(List.of(new Todo("replacement task")));

        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertEquals("replacement task", loadedTasks.get(0).getDescription());
    }

    @Test
    public void load_blankLines_ignoresBlankLines() throws IOException, OtakuException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "\nT|0|cmVhZCBib29r\n   \n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
    }

    @Test
    public void load_malformedRecord_throwsExceptionWithLineNumber() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T|0|dmFsaWQ=", "invalid record"), StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        OtakuException exception = assertThrows(OtakuException.class, storage::load);

        assertEquals("Saved task data is invalid on line 2.", exception.getMessage());
    }
}
