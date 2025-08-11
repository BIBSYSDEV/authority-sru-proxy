package no.unit.authority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthoritySruConnectionTest {

    public static final String SRU_RESPONSE = "/SRU_response.xml";
    public static final String MOCK_AUTH_ID = "90361304";
    public static final String SRU_ENDPOINT = "authority.bibsys.no/authority/rest/sru";
    public static final String AUTHORITY_FULL_URL = "https://authority.bibsys.no/authority/rest/sru?version=1.2"
                                                    + "&operation=searchRetrieve&query=rec.identifier%3D90361304";
    public static final String SRU_ENDPOINT_WITHOUT_BASE_PATH = "authority.bibsys.no";
    public static final String AUTHORITY_FULL_URL_WITHOUT_BASE_PATH = "https://authority.bibsys.no?version=1.2"
                                                    + "&operation=searchRetrieve&query=rec.identifier%3D90361304";

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
        Config.getInstance().setAuthoritySruHost("example.com");
        URL url = authoritySruConnection.generateQueryUrl(MOCK_AUTH_ID);
        assertTrue(url.getQuery().endsWith("query=rec.identifier%3D" + MOCK_AUTH_ID));
    }

    @Test
    public void shouldUseCorrectUrlSyntaxWhenGeneratingQueryUrl() throws MalformedURLException, URISyntaxException {
        Config.getInstance().setAuthoritySruHost(SRU_ENDPOINT);
        var urlWithBasePath = authoritySruConnection.generateQueryUrl(MOCK_AUTH_ID);
        assertThat(urlWithBasePath.toString(), equalTo(AUTHORITY_FULL_URL));

        Config.getInstance().setAuthoritySruHost(SRU_ENDPOINT_WITHOUT_BASE_PATH);
        var urlWithoutBasePath = authoritySruConnection.generateQueryUrl(MOCK_AUTH_ID);
        assertThat(urlWithoutBasePath.toString(), equalTo(AUTHORITY_FULL_URL_WITHOUT_BASE_PATH));
    }

}