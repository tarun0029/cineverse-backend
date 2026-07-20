package com.cineverse.backend.service;

import com.cineverse.backend.dto.SeatAvailabilityResponse;
import com.cineverse.backend.dto.ShowRequest;
import com.cineverse.backend.dto.ShowResponse;
import com.cineverse.backend.entity.Language;
import com.cineverse.backend.entity.Movie;
import com.cineverse.backend.entity.Screen;
import com.cineverse.backend.entity.Seat;
import com.cineverse.backend.entity.Show;
import com.cineverse.backend.entity.ShowSeatPricing;
import com.cineverse.backend.entity.enums.BookingStatus;
import com.cineverse.backend.entity.enums.SeatType;
import com.cineverse.backend.entity.enums.ShowStatus;
import com.cineverse.backend.exception.InvalidReferenceException;
import com.cineverse.backend.exception.InvalidShowRequestException;
import com.cineverse.backend.exception.ResourceNotFoundException;
import com.cineverse.backend.exception.ShowTimeConflictException;
import com.cineverse.backend.repository.BookingSeatRepository;
import com.cineverse.backend.repository.LanguageRepository;
import com.cineverse.backend.repository.MovieRepository;
import com.cineverse.backend.repository.ScreenRepository;
import com.cineverse.backend.repository.SeatRepository;
import com.cineverse.backend.repository.ShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShowService {

    private static final int CLEANING_BUFFER_MINUTES = 15;
    private static final List<BookingStatus> ACTIVE_BOOKING_STATUSES = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final LanguageRepository languageRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public ShowService(
            ShowRepository showRepository,
            MovieRepository movieRepository,
            ScreenRepository screenRepository,
            LanguageRepository languageRepository,
            SeatRepository seatRepository,
            BookingSeatRepository bookingSeatRepository) {
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.screenRepository = screenRepository;
        this.languageRepository = languageRepository;
        this.seatRepository = seatRepository;
        this.bookingSeatRepository = bookingSeatRepository;
    }

    public List<ShowResponse> search(UUID movieId, Long cityId, LocalDate showDate) {
        return showRepository.findByMovie_IdAndShowDateAndScreen_Theatre_City_Id(movieId, showDate, cityId).stream()
                .map(ShowResponse::from)
                .toList();
    }

    public ShowResponse getById(UUID id) {
        Show show = showRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + id));
        return ShowResponse.from(show);
    }

    public List<SeatAvailabilityResponse> getSeatAvailability(UUID showId) {
        Show show = showRepository.findWithDetailsById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + showId));

        Map<SeatType, BigDecimal> priceByType = show.getSeatPricing().stream()
                .collect(Collectors.toMap(ShowSeatPricing::getSeatType, ShowSeatPricing::getPrice));

        Set<Long> bookedSeatIds = new HashSet<>(
                bookingSeatRepository.findBookedSeatIdsByShowIdAndStatuses(showId, ACTIVE_BOOKING_STATUSES));

        return seatRepository.findByScreenIdOrderByRowLabelAscSeatNumberAsc(show.getScreen().getId()).stream()
                .map(seat -> new SeatAvailabilityResponse(
                        seat.getId(),
                        seat.getRowLabel(),
                        seat.getSeatNumber(),
                        seat.getSeatType(),
                        priceByType.get(seat.getSeatType()),
                        bookedSeatIds.contains(seat.getId())))
                .toList();
    }

    @Transactional
    public ShowResponse create(ShowRequest request) {
        if (request.showDate().isBefore(LocalDate.now())) {
            throw new InvalidShowRequestException("Show date cannot be in the past");
        }

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new InvalidReferenceException("Movie not found with id " + request.movieId()));
        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new InvalidReferenceException("Screen not found with id " + request.screenId()));
        Language language = languageRepository.findById(request.languageId())
                .orElseThrow(() -> new InvalidReferenceException("Language not found with id " + request.languageId()));

        List<Seat> screenSeats = seatRepository.findByScreenIdOrderByRowLabelAscSeatNumberAsc(screen.getId());
        if (screenSeats.isEmpty()) {
            throw new InvalidShowRequestException("Screen " + screen.getId() + " has no seat layout configured yet");
        }

        Set<SeatType> screenSeatTypes = screenSeats.stream().map(Seat::getSeatType).collect(Collectors.toSet());
        Set<SeatType> pricedSeatTypes = request.pricing().stream()
                .map(ShowRequest.SeatPricingRequest::seatType)
                .collect(Collectors.toSet());
        if (!pricedSeatTypes.containsAll(screenSeatTypes)) {
            throw new InvalidShowRequestException(
                    "Pricing must be provided for every seat type on this screen: " + screenSeatTypes);
        }

        LocalTime endTime = request.startTime().plusMinutes(movie.getDurationMinutes() + CLEANING_BUFFER_MINUTES);
        if (!endTime.isAfter(request.startTime())) {
            throw new InvalidShowRequestException("Show would extend past midnight; multi-day shows are not supported yet");
        }

        checkForOverlap(screen.getId(), request.showDate(), request.startTime(), endTime);

        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .language(language)
                .format(request.format())
                .showDate(request.showDate())
                .startTime(request.startTime())
                .endTime(endTime)
                .build();

        List<ShowSeatPricing> pricing = request.pricing().stream()
                .<ShowSeatPricing>map(p -> ShowSeatPricing.builder().show(show).seatType(p.seatType()).price(p.price()).build())
                .toList();
        show.setSeatPricing(pricing);

        showRepository.save(show);
        return ShowResponse.from(show);
    }

    @Transactional
    public void cancel(UUID id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + id));
        show.setStatus(ShowStatus.CANCELLED);
    }

    private void checkForOverlap(Long screenId, LocalDate showDate, LocalTime startTime, LocalTime endTime) {
        boolean hasOverlap = showRepository.findByScreenIdAndShowDate(screenId, showDate).stream()
                .filter(existing -> existing.getStatus() != ShowStatus.CANCELLED)
                .anyMatch(existing -> startTime.isBefore(existing.getEndTime()) && existing.getStartTime().isBefore(endTime));

        if (hasOverlap) {
            throw new ShowTimeConflictException(
                    "This screen already has a show scheduled that overlaps with " + startTime + "-" + endTime + " on " + showDate);
        }
    }
}
