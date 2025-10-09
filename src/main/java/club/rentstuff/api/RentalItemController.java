package club.rentstuff.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.model.RentalItemDto;
import club.rentstuff.service.RentalItemService;
import lombok.extern.log4j.Log4j2;

/**
 * @author barry
 *
 */
@Log4j2
@RestController
@RequestMapping("/rentalitems")
public class RentalItemController {

	@Autowired
	protected RentalItemService rentalItemService;

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RentalItemDto> getRentalItem(@PathVariable Integer id) {
		return ResponseEntity.ok(rentalItemService.getById(id));
	}

	@GetMapping("/by-taxonomy/{taxonomyId}")
	public ResponseEntity<List<RentalItemDto>> getItemsByTaxonomy(@PathVariable Long taxonomyId) {
		List<RentalItemDto> items = rentalItemService.findByTaxonomyId(taxonomyId);
		return ResponseEntity.ok(items);
	}
}
