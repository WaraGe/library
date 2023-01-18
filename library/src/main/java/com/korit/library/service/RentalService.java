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

    private void availability(int userId){
        int rentalCount = rentalRepository.rentalAvailability(userId);
        if(rentalCount > 2) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("rentalCountError", "대여 횟수를 초과하였습니다");

            throw new CustomRentalException(errorMap);
        }
    }
}
