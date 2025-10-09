package club.rentstuff.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.ReviewItemEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.repo.RentalItemRepo;
import club.rentstuff.repo.ReviewItemRepo;
import club.rentstuff.repo.UserRepo;
import club.rentstuff.service.ReviewItemService;

@Service
public class ReviewItemServiceImpl implements ReviewItemService {
	@Autowired
	private ReviewItemRepo reviewItemRepository;


    @Autowired
    private RentalItemRepo itemRepository;

    @Autowired
    private UserRepo userRepository;

    @Override
    public ReviewItemEntity createItemReview(Long itemId, Long userId, int rating, String comment) {
        RentalItemEntity item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ReviewItemEntity review = ReviewItemEntity.builder()
            .item(item)
            .reviewer(user)
            .rating(rating)
            .comment(comment)
            .createDate(LocalDateTime.now())
            .build();
        return reviewItemRepository.save(review);
    }
    
	@Override
	public Double getAverageRatingByItemId(Long itemId) {

		return reviewItemRepository.findAverageRatingByItemId(itemId);
	}
}
