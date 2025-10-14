package club.rentstuff.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.model.PriceCalculationDto;
import club.rentstuff.model.RentalItemDto;
import club.rentstuff.service.RentalItemService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/rentalitems")
public class RentalItemController {

	@Autowired
	protected RentalItemService rentalItemService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<RentalItemDto>> getRentalItems() {
		return ResponseEntity.ok(rentalItemService.getAll());
	}

	@GetMapping("/{id}/calculate-price")
	public ResponseEntity<PriceCalculationDto> calculatePrice(@PathVariable Long id,
			@RequestParam LocalDateTime startDateTime, @RequestParam LocalDateTime endDateTime) {
		PriceCalculationDto result = rentalItemService.calculatePrice(id, startDateTime, endDateTime);
		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RentalItemDto> getRentalItem(@PathVariable Long id) {
		return ResponseEntity.ok(rentalItemService.getById(id));
	}

	@GetMapping("/owned")
	public ResponseEntity<List<RentalItemDto>> getMyRentalItems() {
		List<RentalItemDto> items = rentalItemService.findOwnedByUser();
		return ResponseEntity.ok(items);
	}

	@PatchMapping("/{id}/pause")
	public ResponseEntity<RentalItemDto> togglePause(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
		RentalItemDto updatedItem = rentalItemService.togglePause(id, request.get("paused"));
		return ResponseEntity.ok(updatedItem);
	}

	@GetMapping("/by-owner/{userId}")
	public ResponseEntity<List<RentalItemDto>> getRentalItemsOwned(@PathVariable Long userId) {
		List<RentalItemDto> items = rentalItemService.findByOwnerUserId(userId);
		return ResponseEntity.ok(items);
	}

	@GetMapping("/by-taxonomy/{taxonomyId}")
	public ResponseEntity<List<RentalItemDto>> getItemsByTaxonomy(@PathVariable Long taxonomyId) {
		List<RentalItemDto> items = rentalItemService.findByTaxonomyId(taxonomyId);
		return ResponseEntity.ok(items);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<RentalItemDto> createRentalItem(@RequestBody RentalItemDto dto) {
		return ResponseEntity.ok(rentalItemService.create(dto));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<RentalItemDto> updateRentalItem(@PathVariable Long id, @RequestBody RentalItemDto dto) {
		return ResponseEntity.ok(rentalItemService.update(id, dto));
	}
}