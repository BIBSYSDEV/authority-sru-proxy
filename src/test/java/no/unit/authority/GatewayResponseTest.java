package no.unit.authority;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static no.unit.authority.GatewayResponse.CORS_ALLOW_ORIGIN_HEADER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;

class GatewayResponseTest {

    private static final String SOME_CORS_HEADER = "Some CORS Header";

    @Test
    void shouldGenerateCorrectHeaders() {
        Config.getInstance().setCorsHeader(SOME_CORS_HEADER);
        var gatewayResponse = new GatewayResponse();
        var headers = gatewayResponse.getHeaders();

        assertThat(headers.get(CONTENT_TYPE), equalTo(APPLICATION_JSON));
        assertThat(headers.get(CORS_ALLOW_ORIGIN_HEADER), equalTo(SOME_CORS_HEADER));
        assertThat(headers.get("Access-Control-Allow-Methods"), equalTo("OPTIONS,GET"));
        assertThat(headers.get("Access-Control-Allow-Credentials"), equalTo("true"));
        assertThat(headers.get("Access-Control-Allow-Headers"), equalTo(CONTENT_TYPE));
    }

    @Test
    void shouldBeginWithErrorStatusCodeOnNewInstance() {
        var gatewayResponse = new GatewayResponse();

        assertThat(gatewayResponse.getStatusCode(), equalTo(HTTP_INTERNAL_ERROR));
    }

}