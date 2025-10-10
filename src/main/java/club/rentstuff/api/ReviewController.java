package club.rentstuff.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.model.ReviewItemDto;
import club.rentstuff.model.ReviewUserDto;
import club.rentstuff.service.ReviewItemService;
import club.rentstuff.service.ReviewUserService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewItemService reviewItemService;

    @Autowired
    private ReviewUserService reviewUserService;    
    
    
    @GetMapping("/rentalitems/{id}")
    public ResponseEntity<List<ReviewItemDto>> getItemReviews(@PathVariable Long id) {
        return ResponseEntity.ok(reviewItemService.findByItemId(id));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ReviewUserDto>> getUserReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewUserService.findByUserId(userId));
    }
}