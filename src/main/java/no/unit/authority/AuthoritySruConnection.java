package no.unit.authority;

import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class AuthoritySruConnection {

    public static final String HTTPS = "https";
    public static final String VERSION_KEY = "version";
    public static final String SRU_VERSION_1_2 = "1.2";
    public static final String OPERATION_KEY = "operation";
    public static final String OPERATION_SEARCH_RETRIEVE = "searchRetrieve";
    public static final String QUERY_KEY = "query";
    public static final String QUERY_FIELD = "rec.identifier";

    public InputStreamReader connect(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

    /**
     * Generates URL to query authority sru service for record based on authority ID.
     *
     * @param authorityId record id in authority registry
     * @return URL to query
     * @throws URISyntaxException When the resulting URI is not valid
     * @throws MalformedURLException When the resulting URL is not valid
     */
    public URL generateQueryUrl(String authorityId) throws URISyntaxException, MalformedURLException {
        return new URIBuilder()
                .setScheme(HTTPS)
                .setHost(Config.getInstance().getAuthoritySruHost())
                .setPath("/")
                .setParameter(VERSION_KEY, SRU_VERSION_1_2)
                .setParameter(OPERATION_KEY, OPERATION_SEARCH_RETRIEVE)
                .setParameter(QUERY_KEY, String.format("%s=%s", QUERY_FIELD, authorityId))
                .build().toURL();
    }
}
