package com.cineverse.backend.service;

import com.cineverse.backend.dto.TheatreRequest;
import com.cineverse.backend.dto.TheatreResponse;
import com.cineverse.backend.entity.City;
import com.cineverse.backend.entity.Theatre;
import com.cineverse.backend.entity.User;
import com.cineverse.backend.exception.InvalidReferenceException;
import com.cineverse.backend.exception.ResourceNotFoundException;
import com.cineverse.backend.repository.CityRepository;
import com.cineverse.backend.repository.TheatreRepository;
import com.cineverse.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;

    public TheatreService(TheatreRepository theatreRepository, CityRepository cityRepository, UserRepository userRepository) {
        this.theatreRepository = theatreRepository;
        this.cityRepository = cityRepository;
        this.userRepository = userRepository;
    }

    public Page<TheatreResponse> search(Long cityId, Pageable pageable) {
        Page<Theatre> page = cityId != null
                ? theatreRepository.findByCityIdAndActiveTrue(cityId, pageable)
                : theatreRepository.findByActiveTrue(pageable);
        return page.map(TheatreResponse::from);
    }

    public TheatreResponse getById(UUID id) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + id));
        return TheatreResponse.from(theatre);
    }

    @Transactional
    public TheatreResponse create(TheatreRequest request) {
        City city = resolveCity(request.cityId());
        User owner = resolveOwner(request.ownerId());

        Theatre theatre = Theatre.builder()
                .name(request.name())
                .addressLine(request.addressLine())
                .city(city)
                .pincode(request.pincode())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .contactNumber(request.contactNumber())
                .owner(owner)
                .build();

        theatreRepository.save(theatre);
        return TheatreResponse.from(theatre);
    }

    @Transactional
    public TheatreResponse update(UUID id, TheatreRequest request) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + id));

        theatre.setName(request.name());
        theatre.setAddressLine(request.addressLine());
        theatre.setCity(resolveCity(request.cityId()));
        theatre.setPincode(request.pincode());
        theatre.setLatitude(request.latitude());
        theatre.setLongitude(request.longitude());
        theatre.setContactNumber(request.contactNumber());
        theatre.setOwner(resolveOwner(request.ownerId()));

        return TheatreResponse.from(theatre);
    }

    @Transactional
    public void delete(UUID id) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + id));
        theatre.setActive(false);
    }

    private City resolveCity(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new InvalidReferenceException("City not found with id " + cityId));
    }

    private User resolveOwner(Integer ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new InvalidReferenceException("User not found with id " + ownerId));
    }
}
