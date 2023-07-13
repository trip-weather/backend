package com.trading212.weathertrip.domain.dto.flight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FareDetailsBySegment {
    private String segmentId;
    private String cabin;
    private IncludedCheckedBags includedCheckedBags;
}
