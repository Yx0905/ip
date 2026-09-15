package otaku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertEquals(" Your quest log:\n1.[T][ ] read book", otaku.getResponse("list"));
    }

    @Test
    public void getResponse_mutatingCommand_savesForNextInstance() {
        Path dataFile = temporaryDirectory.resolve("otaku.txt");
        Otaku firstSession = new Otaku(dataFile);
        firstSession.getResponse("deadline submit report /by 2026-09-30");
        firstSession.getResponse("mark 1");

        Otaku secondSession = new Otaku(dataFile);
        assertEquals(" Your quest log:\n"
                + "1.[D][X] submit report (by: Sep 30 2026)", secondSession.getResponse("list"));
    }

    @Test
    public void getCommandResponse_validAndInvalidCommands_reportsErrorStatus() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertFalse(otaku.getCommandResponse("list").isError());
        assertTrue(otaku.getCommandResponse("unknown").isError());
    }

    @Test
    public void getResponse_malformedCommands_rejectsErrorsWithoutChangingState() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertTrue(otaku.getResponse("  todo read book  ").contains("Quest accepted!"));
        assertEquals(" That quest is already in your log.", otaku.getResponse("todo read book"));
        assertEquals(" A deadline accepts exactly one `/by` parameter.",
                otaku.getResponse("deadline report /by 2026-09-20 /by 2026-09-21"));
        assertEquals(" An event's end date must be after its start date.",
                otaku.getResponse("event class /from 2026-09-20 /to 2026-09-20"));
        assertEquals(" Your quest log:\n1.[T][ ] read book", otaku.getResponse("  list  "));
    }

    @Test
    public void getCommandResponse_emptyOrNullCommand_reportsHelpfulError() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertTrue(otaku.getCommandResponse("   ").isError());
        assertTrue(otaku.getCommandResponse(null).isError());
        assertEquals(" I need a command before I can update the quest log.", otaku.getResponse(""));
    }
}
