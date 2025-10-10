package club.rentstuff.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.ReviewItemEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.ReviewItemDto;
import club.rentstuff.repo.RentalItemRepo;
import club.rentstuff.repo.ReviewItemRepo;
import club.rentstuff.repo.UserRepo;
import club.rentstuff.service.ConversionService;
import club.rentstuff.service.ReviewItemService;

@Service
public class ReviewItemServiceImpl implements ReviewItemService {
	@Autowired
	private ReviewItemRepo reviewItemRepository;

	@Autowired
	private RentalItemRepo itemRepository;

	@Autowired
	private UserRepo userRepository;

	@Autowired
	private ConversionService conversionService;

	@Override
	public ReviewItemDto createItemReview(Long itemId, Long userId, int rating, String comment) {
		RentalItemEntity item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("Item not found"));
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		ReviewItemEntity review = ReviewItemEntity.builder().item(item).reviewer(user).rating(rating).comment(comment)
				.createDate(LocalDateTime.now()).build();
		ReviewItemEntity entity = reviewItemRepository.save(review);

		return conversionService.convertReviewItemEntity(entity);
	}

	@Override
	public Double getAverageRatingByItemId(Long itemId) {

		return reviewItemRepository.findAverageRatingByItemId(itemId);
	}

	@Override
	public List<ReviewItemDto> findByItemId(Long id) {
		List<ReviewItemEntity> entities = reviewItemRepository.findByItemId(id);
		
		return entities.stream().map(en -> conversionService.convertReviewItemEntity(en)).collect(Collectors.toList());
	}
}
