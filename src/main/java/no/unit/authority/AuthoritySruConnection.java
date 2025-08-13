package no.unit.authority;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthoritySruConnection {

    private static final Logger logger = LoggerFactory.getLogger(AuthoritySruConnection.class);

    public static final String HTTPS = "https";
    public static final String VERSION_KEY = "version";
    public static final String SRU_VERSION_1_2 = "1.2";
    public static final String OPERATION_KEY = "operation";
    public static final String OPERATION_SEARCH_RETRIEVE = "searchRetrieve";
    public static final String QUERY_KEY = "query";
    public static final String QUERY_FIELD = "rec.identifier";
    public static final String CONNECTING_TO = "Connecting to {}";
    public static final String PATH_DELIMITER = "/";

    public InputStreamReader connect(URL url) throws IOException {
        logger.info(CONNECTING_TO, url.toString());
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
                .setHost(getAuthoritySruHost())
                .setPath(getAuthoritySruPath())
                .setParameter(VERSION_KEY, SRU_VERSION_1_2)
                .setParameter(OPERATION_KEY, OPERATION_SEARCH_RETRIEVE)
                .setParameter(QUERY_KEY, String.format("%s=%s", QUERY_FIELD, authorityId))
                .build().toURL();
    }

    private String getAuthoritySruHost() {
        return Stream.of(Config.getInstance().getAuthoritySruHost().split(PATH_DELIMITER))
                   .findFirst()
                   .orElse(Config.getInstance().getAuthoritySruHost());
    }

    private String getAuthoritySruPath() {
        return Stream.of(Config.getInstance().getAuthoritySruHost().split(PATH_DELIMITER))
                   .skip(1)
                   .collect(Collectors.joining(PATH_DELIMITER));
    }

}
