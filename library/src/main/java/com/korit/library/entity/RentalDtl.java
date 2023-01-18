package com.korit.library.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalDtl {
    private int rentalDtlId;
    private int rentalId;
    private int bookId;
    private LocalDate returnDate;

}
