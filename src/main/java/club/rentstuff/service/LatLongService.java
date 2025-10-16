package club.rentstuff.service;

import club.rentstuff.model.LatLong;

public interface LatLongService {
	LatLong getLatLong(String zipCode);
}
