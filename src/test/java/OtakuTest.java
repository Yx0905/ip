import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class OtakuTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_validAndInvalidCommands_preservesStateAfterError() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertTrue(otaku.getResponse("todo read book").contains("[T][ ] read book"));
        assertEquals(" Please give a whole task number after `mark`.", otaku.getResponse("mark one"));
        assertEquals(" Here are the tasks in your list:\n1.[T][ ] read book", otaku.getResponse("list"));
    }

    @Test
    public void getResponse_mutatingCommand_savesForNextInstance() {
        Path dataFile = temporaryDirectory.resolve("otaku.txt");
        Otaku firstSession = new Otaku(dataFile);
        firstSession.getResponse("deadline submit report /by 2026-09-30");
        firstSession.getResponse("mark 1");

        Otaku secondSession = new Otaku(dataFile);
        assertEquals(" Here are the tasks in your list:\n"
                + "1.[D][X] submit report (by: Sep 30 2026)", secondSession.getResponse("list"));
    }
}
