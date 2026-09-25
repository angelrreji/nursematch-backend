package com.nursematch.geo;

import com.nursematch.geo.dto.GeocodeResult;
import com.nursematch.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GeocodingClient {

    @Value("${geocoding.base-url}")
    private String baseUrl;

    @Value("${geocoding.user-agent}")
    private String userAgent;

    private final RestClient restClient = RestClient.create();

    public GeocodeResult geocode(String address, String city, String state) {

        String query = address + ", " + city + ", " + state;

        String url = baseUrl
                + "?q=" + query.replace(" ", "+")
                + "&format=json"
                + "&limit=1"
                + "&addressdetails=1";

        List<Map<String, Object>> response = restClient.get()
                .uri(url)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .retrieve()
                .body(List.class);

        if (response == null || response.isEmpty()) {
            throw new ResourceNotFoundException("Address could not be geocoded");
        }

        Map<String, Object> result = response.get(0);

        double lat = Double.parseDouble(result.get("lat").toString());
        double lng = Double.parseDouble(result.get("lon").toString());

        Map<String, Object> addressDetails = (Map<String, Object>) result.get("address");

        String resolvedCity = extractCity(addressDetails);
        String resolvedState = (String) addressDetails.getOrDefault("state", state);

        return new GeocodeResult(lat, lng, resolvedCity, resolvedState);
    }

    private String extractCity(Map<String, Object> addressDetails) {
        // Nominatim returns different keys depending on region
        if (addressDetails.containsKey("city")) return (String) addressDetails.get("city");
        if (addressDetails.containsKey("town")) return (String) addressDetails.get("town");
        if (addressDetails.containsKey("village")) return (String) addressDetails.get("village");
        return "Unknown";
    }
}