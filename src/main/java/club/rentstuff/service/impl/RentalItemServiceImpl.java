package club.rentstuff.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.model.RentalItemDto;
import club.rentstuff.repo.RentalItemRepo;
import club.rentstuff.service.ConversionService;
import club.rentstuff.service.RentalItemService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RentalItemServiceImpl implements RentalItemService {


	    @Autowired
	    private RentalItemRepo itemRepository;
	    
	    @Autowired
	    private ConversionService conversionService;

	    @Override
	    public RentalItemDto getItemForBooking(Long itemId) {
	    	RentalItemEntity e = itemRepository.findById(itemId).orElseThrow();
	        return conversionService.convert(e);
	    }
	    
	    @Override
	    public List<RentalItemDto> findByTaxonomyId(Long taxonomyId) {
	    	
	    	return itemRepository.findByTaxonomiesId(taxonomyId).stream().map(s -> conversionService.convert(s)).collect(Collectors.toList());
	    }

		@Override
		public RentalItemDto getById(Integer id) {
	        return conversionService.convert(itemRepository.getById(Long.valueOf(id)));
		}
	

	}
