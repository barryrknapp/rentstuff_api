package club.rentstuff.api;

import club.rentstuff.model.RentalItemDto;
import club.rentstuff.service.RentalItemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentalItemDto> getRentalItem(@PathVariable Long id) {
        return ResponseEntity.ok(rentalItemService.getById(id));
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