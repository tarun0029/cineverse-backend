package com.cineverse.backend.service;

import com.cineverse.backend.dto.ScreenRequest;
import com.cineverse.backend.dto.ScreenResponse;
import com.cineverse.backend.entity.Screen;
import com.cineverse.backend.entity.Theatre;
import com.cineverse.backend.exception.ResourceNotFoundException;
import com.cineverse.backend.repository.ScreenRepository;
import com.cineverse.backend.repository.TheatreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;

    public ScreenService(ScreenRepository screenRepository, TheatreRepository theatreRepository) {
        this.screenRepository = screenRepository;
        this.theatreRepository = theatreRepository;
    }

    public List<ScreenResponse> listByTheatre(UUID theatreId) {
        requireTheatre(theatreId);
        return screenRepository.findByTheatreId(theatreId).stream()
                .map(ScreenResponse::from)
                .toList();
    }

    @Transactional
    public ScreenResponse create(UUID theatreId, ScreenRequest request) {
        Theatre theatre = requireTheatre(theatreId);

        Screen screen = Screen.builder()
                .theatre(theatre)
                .name(request.name())
                .screenType(request.screenType())
                .totalSeats(0)
                .build();

        screenRepository.save(screen);
        return ScreenResponse.from(screen);
    }

    @Transactional
    public ScreenResponse update(UUID theatreId, Long screenId, ScreenRequest request) {
        Screen screen = requireScreen(theatreId, screenId);
        screen.setName(request.name());
        screen.setScreenType(request.screenType());
        return ScreenResponse.from(screen);
    }

    @Transactional
    public void delete(UUID theatreId, Long screenId) {
        Screen screen = requireScreen(theatreId, screenId);
        screen.setActive(false);
    }

    private Theatre requireTheatre(UUID theatreId) {
        return theatreRepository.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + theatreId));
    }

    /**
     * Package-visible: also used by SeatService, since seat operations need the same
     * theatreId/screenId path-integrity check (a screenId in the URL must actually belong to
     * the theatreId also in the URL, not just exist somewhere).
     */
    Screen requireScreen(UUID theatreId, Long screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id " + screenId));
        if (!screen.getTheatre().getId().equals(theatreId)) {
            throw new ResourceNotFoundException("Screen not found with id " + screenId + " for theatre " + theatreId);
        }
        return screen;
    }
}
