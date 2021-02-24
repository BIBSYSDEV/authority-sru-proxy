package no.unit.authority;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import no.unit.marc.ParsingException;
import no.unit.marc.Reference;
import no.unit.marc.SearchRetrieveResponseParser;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static no.unit.authority.StringUtils.isEmpty;

public class GetAuthoritySruRecordHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {
    public static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    public static final String MANDATORY_PARAMETERS_MISSING = "Mandatory parameters 'auth_id' is missing.";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "An error occurred, error has been logged";
    public static final String AUTHORITY_ID_KEY = "auth_id";

    protected final transient AuthoritySruConnection connection;

    public GetAuthoritySruRecordHandler(AuthoritySruConnection connection) {
        this.connection = connection;
    }

    public GetAuthoritySruRecordHandler() {
        this.connection = new AuthoritySruConnection();
    }

    @Override
    @SuppressWarnings("unchecked")
    public GatewayResponse handleRequest(final Map<String, Object> input, Context context) {
        GatewayResponse gatewayResponse = new GatewayResponse();
        Config.getInstance().checkProperties();
        if (Objects.isNull(input) || !input.containsKey(QUERY_STRING_PARAMETERS_KEY)) {
            gatewayResponse.setErrorBody(MANDATORY_PARAMETERS_MISSING);
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return gatewayResponse;
        }

        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        String authorityId = queryStringParameters.get(AUTHORITY_ID_KEY);

        if (isEmpty(authorityId)) {
            gatewayResponse.setErrorBody(MANDATORY_PARAMETERS_MISSING);
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return gatewayResponse;
        }

        try {
            URL queryUrl = connection.generateQueryUrl(authorityId);
            List<Reference> referenceObjects;
            try (InputStreamReader streamReader = connection.connect(queryUrl)) {
                String xml = new BufferedReader(streamReader)
                        .lines()
                        .collect(Collectors.joining(System.lineSeparator()));
                referenceObjects = SearchRetrieveResponseParser.getReferenceObjectsFromSearchRetrieveResponse(xml);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type listOfMyClassObject = new TypeToken<List<Reference>>() {
                }.getType();
                gatewayResponse.setBody(gson.toJson(referenceObjects, listOfMyClassObject));
                gatewayResponse.setStatusCode(Response.Status.OK.getStatusCode());
            }
        } catch (URISyntaxException | IOException | ParsingException e) {
            DebugUtils.dumpException(e);
            gatewayResponse.setErrorBody(INTERNAL_SERVER_ERROR_MESSAGE + " : " + e.getMessage());
            gatewayResponse.setStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
        return gatewayResponse;
    }
}
