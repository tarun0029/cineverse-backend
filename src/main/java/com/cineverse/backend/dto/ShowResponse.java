package com.cineverse.backend.dto;

import com.cineverse.backend.entity.Show;
import com.cineverse.backend.entity.enums.SeatType;
import com.cineverse.backend.entity.enums.ShowFormat;
import com.cineverse.backend.entity.enums.ShowStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ShowResponse(
        UUID id,
        UUID movieId,
        String movieTitle,
        Long screenId,
        String screenName,
        UUID theatreId,
        String theatreName,
        Long languageId,
        String languageName,
        ShowFormat format,
        LocalDate showDate,
        LocalTime startTime,
        LocalTime endTime,
        ShowStatus status,
        List<SeatPricingResponse> pricing
) {

    public record SeatPricingResponse(SeatType seatType, BigDecimal price) {
    }

    public static ShowResponse from(Show show) {
        List<SeatPricingResponse> pricing = show.getSeatPricing().stream()
                .map(sp -> new SeatPricingResponse(sp.getSeatType(), sp.getPrice()))
                .toList();

        return new ShowResponse(
                show.getId(),
                show.getMovie().getId(),
                show.getMovie().getTitle(),
                show.getScreen().getId(),
                show.getScreen().getName(),
                show.getScreen().getTheatre().getId(),
                show.getScreen().getTheatre().getName(),
                show.getLanguage().getId(),
                show.getLanguage().getName(),
                show.getFormat(),
                show.getShowDate(),
                show.getStartTime(),
                show.getEndTime(),
                show.getStatus(),
                pricing
        );
    }
}
