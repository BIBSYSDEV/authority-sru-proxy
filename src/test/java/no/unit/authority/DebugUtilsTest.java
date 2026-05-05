package no.unit.authority;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DebugUtilsTest {

    private static final String RUNTIME_EXCEPTION_MESSAGE = "Some error message";

    @Test
    public void testDumpException() {
        var runtimeException = new RuntimeException(RUNTIME_EXCEPTION_MESSAGE);
        var exceptionDump = DebugUtils.dumpException(runtimeException);

        assertNotNull(exceptionDump);
        assertThat(exceptionDump, containsString(RUNTIME_EXCEPTION_MESSAGE));
    }

}
