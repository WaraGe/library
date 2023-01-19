package com.korit.library.service;

import com.korit.library.entity.RentalDtl;
import com.korit.library.entity.RentalMst;
import com.korit.library.exception.CustomRentalException;
import com.korit.library.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

//  누가 이책을 빌릴것인가 확인하기 위한것
    public void rentalOne(int userId, int bookId) {
        availability(userId);
        availabilityLoan(bookId);

        RentalMst rentalMst = RentalMst.builder()
                .userId(userId)
                .build();
        List<RentalDtl> rentalDtlList = new ArrayList<>();

        rentalRepository.saveRentalMst(rentalMst);
        rentalDtlList.add(RentalDtl.builder()
                .rentalId(rentalMst.getRentalId()) //mst에서 insert해서 나온 증가된 값을 넣어줘야 하기 때문에 생성된후 가져와야함
                .bookId(bookId)
                .build());

        rentalRepository.saveRentalDtl(rentalDtlList);
    }

    public void returnBook(int bookId) {
        NotAvailabilityLoan(bookId);
        rentalRepository.updateReturnDate(bookId);
    }


    private void availability(int userId){
        int rentalCount = rentalRepository.rentalAvailability(userId);
        if(rentalCount > 2) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("rentalCountError", "대여 횟수를 초과하였습니다");

            throw new CustomRentalException(errorMap);
        }
    }
    private void availabilityLoan(int bookId) {
        int loanCount = rentalRepository.loanRental(bookId);
        if(loanCount > 0) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("LoanError", "현재 대여중인 도서입니다");

            throw new CustomRentalException(errorMap);
        }
    }

    private void NotAvailabilityLoan(int bookId) {
        int loanCount = rentalRepository.loanRental(bookId);
        if(loanCount < 1) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("LoanError", "대여중인 도서가 아입니다");

            throw new CustomRentalException(errorMap);
        }
    }
}
