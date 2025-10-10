package club.rentstuff.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.model.TaxonomyDto;
import club.rentstuff.service.TaxonomyService;

@RestController
@RequestMapping("/taxonomies")
public class TaxonomiesController {

	@Autowired
	private TaxonomyService taxonomyService;

	@GetMapping()
	public ResponseEntity<List<TaxonomyDto>> getTaxonomies() {
		return ResponseEntity.ok(taxonomyService.getAll());
	}

}