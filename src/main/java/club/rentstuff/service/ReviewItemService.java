package club.rentstuff.service;

import java.util.List;

import club.rentstuff.model.ReviewItemDto;
import club.rentstuff.model.ReviewUserDto;

public interface ReviewItemService {

	Double getAverageRatingByItemId(Long itemId);

	ReviewItemDto createItemReview(Long itemId, Long userId, int rating, String comment);

	List<ReviewItemDto> findByItemId(Long id);


}
