package com.cineverse.backend.service;

import com.cineverse.backend.dto.SeatLayoutRequest;
import com.cineverse.backend.dto.SeatResponse;
import com.cineverse.backend.entity.Screen;
import com.cineverse.backend.entity.Seat;
import com.cineverse.backend.exception.SeatLayoutAlreadyExistsException;
import com.cineverse.backend.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final ScreenService screenService;

    public SeatService(SeatRepository seatRepository, ScreenService screenService) {
        this.seatRepository = seatRepository;
        this.screenService = screenService;
    }

    public List<SeatResponse> list(UUID theatreId, Long screenId) {
        screenService.requireScreen(theatreId, screenId);
        return seatRepository.findByScreenIdOrderByRowLabelAscSeatNumberAsc(screenId).stream()
                .map(SeatResponse::from)
                .toList();
    }

    @Transactional
    public List<SeatResponse> generateLayout(UUID theatreId, Long screenId, SeatLayoutRequest request) {
        Screen screen = screenService.requireScreen(theatreId, screenId);

        if (seatRepository.existsByScreenId(screenId)) {
            throw new SeatLayoutAlreadyExistsException(
                    "Screen " + screenId + " already has a seat layout; delete the existing seats before regenerating");
        }

        List<Seat> seats = new ArrayList<>();
        for (SeatLayoutRequest.SeatRowRequest row : request.rows()) {
            for (int seatNumber = 1; seatNumber <= row.numberOfSeats(); seatNumber++) {
                seats.add(Seat.builder()
                        .screen(screen)
                        .rowLabel(row.rowLabel())
                        .seatNumber(seatNumber)
                        .seatType(row.seatType())
                        .build());
            }
        }

        seatRepository.saveAll(seats);
        screen.setTotalSeats(seats.size());

        return seats.stream().map(SeatResponse::from).toList();
    }
}
