package club.rentstuff.service.impl;


import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import club.rentstuff.model.LatLong;
import club.rentstuff.service.LatLongService;

@Service
public class LatLongServiceImpl implements LatLongService {

	private final RestTemplate restTemplate;

	public LatLongServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	@Cacheable(value = "zipCodeLatLong", key = "#zipCode")
	public LatLong getLatLong(String zipCode) {
		if (zipCode == null || !zipCode.matches("\\d{5}")) {
			throw new IllegalArgumentException("Invalid ZIP code: " + zipCode);
		}

		try {
			String url = "https://api.zippopotam.us/us/" + zipCode;
			ZippopotamResponse response = restTemplate.getForObject(url, ZippopotamResponse.class);
			if (response == null || response.getPlaces() == null || response.getPlaces().isEmpty()) {
				throw new RestClientException("No data found for ZIP code: " + zipCode);
			}
			ZippopotamResponse.Place place = response.getPlaces().get(0);
			return new LatLong(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()));
		} catch (RestClientException e) {
			throw new RuntimeException("Failed to fetch lat/long for ZIP code " + zipCode + ": " + e.getMessage(), e);
		}
	}
}

// DTO for Zippopotam.us response
class ZippopotamResponse {
	private List<Place> places;

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	static class Place {
		private String latitude;
		private String longitude;

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
	}
}