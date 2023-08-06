package com.depscanner.vulnservice.depsdev;

import com.depscanner.vulnservice.config.WebClientConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ApiHelper {
    private static final String API_URL = "https://api.deps.dev/";
    private static final String VERSION = "v3alpha/";

    public static final String GET_PACKAGE_URL = buildUrl("systems/{system}/packages/{name}");
    public static final String GET_VERSION_URL = buildUrl("systems/{system}/packages/{name}/versions/{version}");
    public static final String GET_DEPENDENCY_URL = buildUrl("systems/{system}/packages/{name}/versions/{version}:dependencies");
    public static final String GET_ADVISORY_URL = buildUrl("advisories/{key}");

    private static String buildUrl(String path) {
        return API_URL + VERSION + path;
    }

    public static String buildApiUrl(String endpoint, String... urlParams) {
        return UriComponentsBuilder.fromUriString(endpoint)
                .buildAndExpand(urlParams)
                .toUriString();
    }

    @CircuitBreaker(name="${spring.application.name}", fallbackMethod="fallbackMethod")
    public static <T> T makeApiRequest(String url, Class<T> responseType) {
        WebClient.Builder builder = WebClientConfig.webClient();
        try {
            return builder
                    .codecs(config -> config.defaultCodecs()
                            .maxInMemorySize(16 * 1024 * 1024))
                    .build()
                    .get()
                    .uri(URI.create(url))
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException.NotFound | WebClientResponseException.BadRequest e) {
            e.getMessage();
            return null;
        }
    }

    public <T> T fallbackMethod(String url) {
        log.info("Fallback method invoked for URL: " + url);
        return null;
    }

    public static String percentEncodeParam(String urlString) {
        return UriUtils.encodePath(urlString, StandardCharsets.UTF_8)
                .replace("%3A", ":")
                .replace("@", "%40")
                .replace("/", "%2F")
                .replace("%2540", "%40")
                .replace("%252F", "%2F");
        //some issues with double encoding @ and / due to repeat API calls
    }
}
