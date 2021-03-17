package no.unit.authority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthoritySruConnectionTest {

    public static final String SRU_RESPONSE = "/SRU_response.xml";
    public static final String MOCK_AUTH_ID = "90361304";

    private AuthoritySruConnection authoritySruConnection;

    @BeforeEach
    public void setup() {
        authoritySruConnection = new AuthoritySruConnection();
    }

    @Test
    public void testConnect() throws IOException {
        final URL localFileUrl = AuthoritySruConnectionTest.class.getResource(SRU_RESPONSE);
        final InputStreamReader streamReader = authoritySruConnection.connect(localFileUrl);
        assertNotNull(streamReader);
        streamReader.close();
    }

    @Test
    public void testGenerateQueryByAuthIdUrl() throws MalformedURLException, URISyntaxException {
        URL url = authoritySruConnection.generateQueryUrl(MOCK_AUTH_ID);
        assertTrue(url.getQuery().endsWith("query=rec.identifier%3D" + MOCK_AUTH_ID));
    }

}