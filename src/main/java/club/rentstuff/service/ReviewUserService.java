package club.rentstuff.service;

import java.util.List;

import club.rentstuff.model.ReviewUserDto;

public interface ReviewUserService {

	Double getAverageRatingByUserId(Long reviewedUserId);

	List<ReviewUserDto> findByUserId(Long reviewedUserId);

	ReviewUserDto createUserReview(Long reviewedUserId, Long userId, int rating, String comment);

}
