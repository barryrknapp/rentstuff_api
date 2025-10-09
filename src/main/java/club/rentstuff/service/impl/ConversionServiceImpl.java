package club.rentstuff.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.ReviewItemEntity;
import club.rentstuff.entity.TaxonomyEntity;
import club.rentstuff.model.RentalItemDto;
import club.rentstuff.service.ConversionService;

@Service
public class ConversionServiceImpl implements ConversionService {

    @Override
    public RentalItemDto convert(RentalItemEntity e) {
        if (e == null) {
            return null;
        }

        // Compute average rating from reviews
        double averageRating = e.getReviews().stream()
            .mapToInt(ReviewItemEntity::getRating)
            .average()
            .orElse(0.0);

        // Extract taxonomy IDs
        List<Long> taxonomyIds = e.getTaxonomies().stream()
            .map(TaxonomyEntity::getId)
            .collect(Collectors.toList());

        return RentalItemDto.builder()
            .id(e.getId())
            .name(e.getName())
            .imageUrl(e.getImageUrl())
            .description(e.getDescription())
            .price(e.getPrice())
            .createDate(e.getCreateDate())
            .modifyDate(e.getModifyDate())
            .ownerId(e.getOwner() != null ? e.getOwner().getId() : null)
            .taxonomyIds(taxonomyIds)
            .averageRating(averageRating)
            .build();
    }
}