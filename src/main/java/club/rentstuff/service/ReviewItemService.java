package club.rentstuff.service;

import club.rentstuff.entity.ReviewItemEntity;

public interface ReviewItemService {

	Double getAverageRatingByItemId(Long itemId);

	ReviewItemEntity createItemReview(Long itemId, Long userId, int rating, String comment);

}
