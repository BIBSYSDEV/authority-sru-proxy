package no.unit.authority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static final String MISSING_ENVIRONMENT_VARIABLES = "Missing environment variables SRU_ENDPOINT";
    public static final String CORS_ALLOW_ORIGIN_HEADER_ENVIRONMENT_NAME = "ALLOWED_ORIGIN";
    public static final String SRU_ENDPOINT_KEY = "SRU_ENDPOINT";

    private String corsHeader;
    private String authoritySruHost;

    private Config() {
    }

    private static final class LazyHolder {

        private static final Config INSTANCE = new Config();

        public static final String INITIALIZING_CONFIG =
            "Initializing Config... SRU_ENDPOINT: {} and ALLOWED_ORIGIN: {}";

        static {
            logger.info(INITIALIZING_CONFIG,
                        System.getenv(SRU_ENDPOINT_KEY),
                        System.getenv(CORS_ALLOW_ORIGIN_HEADER_ENVIRONMENT_NAME));
            INSTANCE.setAuthoritySruHost(System.getenv(SRU_ENDPOINT_KEY));
            INSTANCE.setCorsHeader(System.getenv(CORS_ALLOW_ORIGIN_HEADER_ENVIRONMENT_NAME));
        }
    }

    public static Config getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Checking if authoritySruHost is present.
     *
     * @return <code>TRUE</code> if property is present.
     */
    public boolean checkProperties() {
        if (StringUtils.isEmpty(authoritySruHost)) {
            throw new RuntimeException(MISSING_ENVIRONMENT_VARIABLES);
        }
        return true;
    }

    public void setAuthoritySruHost(String authoritySruHost) {
        this.authoritySruHost = authoritySruHost;
    }

    public String getAuthoritySruHost() {
        return authoritySruHost;
    }

    public String getCorsHeader() {
        return corsHeader;
    }

    public void setCorsHeader(String corsHeader) {
        this.corsHeader = corsHeader;
    }

}
