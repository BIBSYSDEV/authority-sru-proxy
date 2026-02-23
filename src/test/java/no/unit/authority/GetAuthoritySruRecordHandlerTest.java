package no.unit.authority;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import no.unit.marc.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.lineSeparator;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static no.unit.authority.GetAuthoritySruRecordHandler.AUTHORITY_ID_KEY;
import static no.unit.authority.GetAuthoritySruRecordHandler.QUERY_STRING_PARAMETERS_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAuthoritySruRecordHandlerTest {

    public static final String MOCK_AUTHORITY_ID = "6039710";
    public static final String MOCK_SRU_HOST = "authority-sru-host-dot-com";
    public static final String SRU_RESPONSE = "/SRU_response.xml";
    public static final String EXPECTED_ID = "90361304";
    public static final String EXPECTED_LINE_PRESENTATION = "*ldr 99999nz  a2299999n  4500" + lineSeparator()
            + "*001 90361304" + lineSeparator()
            + "*003 NO-TrBIB" + lineSeparator()
            + "*005 20181018093138.0" + lineSeparator()
            + "*008 910502n| adz|naabn|         |a|ana|     " + lineSeparator()
            + "*0247# $ax90361304 $2NO-TrBIB" + lineSeparator()
            + "*0247# $ahttp://hdl.handle.net/11250/1269159 $2hdl" + lineSeparator()
            + "*0247# $ahttp://viaf.org/viaf/126209034 $2viaf" + lineSeparator()
            + "*040## $aNO-TrBIB $bnob $cNO-TrBIB $fnoraf" + lineSeparator()
            + "*1112# $aNATO Advanced Research Workshop on Movable Bed Physical Models" + lineSeparator()
            + "*901## $akat2";

    private AuthoritySruConnection mockConnection;
    private GetAuthoritySruRecordHandler mockAlmaRecordHandler;

    /**
     * Sets up test objects with mocks.
     */
    @BeforeEach
    public void setup() {
        final Config instance = Config.getInstance();
        instance.setAuthoritySruHost(MOCK_SRU_HOST);

        mockConnection = mock(AuthoritySruConnection.class);
        mockAlmaRecordHandler = new GetAuthoritySruRecordHandler(mockConnection);
    }

    @Test
    void getsAuthoritySruRecord() throws IOException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(AUTHORITY_ID_KEY, MOCK_AUTHORITY_ID);
        Map<String, Object> event = new HashMap<>();
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParameters);

        var stream = GetAuthoritySruRecordHandlerTest.class.getResourceAsStream(SRU_RESPONSE);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        var gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        var gson = new GsonBuilder().setPrettyPrinting().create();
        Type listOfMyClassObject = new TypeToken<List<Reference>>() {}.getType();
        List<Reference> references = gson.fromJson(gatewayResponse.getBody(), listOfMyClassObject);

        assertEquals(1, references.size());
        assertEquals(EXPECTED_ID, references.getFirst().getId());
        assertEquals(EXPECTED_LINE_PRESENTATION, references.getFirst().getLinePresentation());
    }

    @Test
    void badRequestIfParametersAreMissing() {
        Map<String, Object> event = new HashMap<>();
        var gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        assertEquals(400, gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(GetAuthoritySruRecordHandler.MANDATORY_PARAMETERS_MISSING));

        Map<String, String> queryParameters = new HashMap<>();
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParameters);
        gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        assertEquals(400, gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(GetAuthoritySruRecordHandler.MANDATORY_PARAMETERS_MISSING));

        queryParameters.put(AUTHORITY_ID_KEY, "");
        gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        assertEquals(400, gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(GetAuthoritySruRecordHandler.MANDATORY_PARAMETERS_MISSING));
    }

    @Test
    void shouldReturnInternalErrorWhenUpstreamConnectionFails() throws IOException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(AUTHORITY_ID_KEY, MOCK_AUTHORITY_ID);
        Map<String, Object> event = new HashMap<>();
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParameters);

        doThrow(IOException.class).when(mockConnection).connect(any());

        var gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        assertThat(gatewayResponse.getStatusCode(), equalTo(HTTP_INTERNAL_ERROR));
        assertThat(gatewayResponse.getBody(), containsString("An error occurred, error has been logged"));
        assertThat(gatewayResponse.getBody(), containsString("IOException"));
    }
}
