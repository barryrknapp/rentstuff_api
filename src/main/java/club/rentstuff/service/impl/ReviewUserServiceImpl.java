package club.rentstuff.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.ReviewUserEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.ReviewUserDto;
import club.rentstuff.repo.ReviewItemRepo;
import club.rentstuff.repo.ReviewUserRepo;
import club.rentstuff.repo.UserRepo;
import club.rentstuff.service.ConversionService;
import club.rentstuff.service.ReviewUserService;

@Service
public class ReviewUserServiceImpl implements ReviewUserService {
	@Autowired
	private ReviewItemRepo reviewItemRepository;


    @Autowired
    private ReviewUserRepo reviewRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ConversionService conversionService;
    
    
    @Override
    public ReviewUserDto createUserReview(Long reviewedUserId, Long userId, int rating, String comment) {
        UserEntity reviewer = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        
        UserEntity reviewedUser = userRepository.findById(reviewedUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ReviewUserEntity review = ReviewUserEntity.builder()
            .reviewedUser(reviewedUser)
            .reviewer(reviewer)
            .rating(rating)
            .comment(comment)
            .createDate(LocalDateTime.now())
            .build();
        ReviewUserEntity updated =  reviewRepository.save(review);
        
        return conversionService.convertReviewUserEntity(updated);
    }
    
	@Override
	public Double getAverageRatingByUserId(Long reviewedUserId) {

		return reviewRepository.findAverageRatingByReviewedUserId(reviewedUserId);
	}

	@Override
	public List<ReviewUserDto> findByUserId(Long id) {
		List<ReviewUserEntity> reviews = reviewRepository.findByReviewedUserId(id);
		
		return reviews.stream().map(r -> conversionService.convertReviewUserEntity(r)).collect(Collectors.toList());
	}
}
