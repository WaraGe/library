package com.korit.library.web.api;

import com.korit.library.security.PrincipalDetails;
import com.korit.library.service.RentalService;
import com.korit.library.web.dto.CMRespDto;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"도서 대여 API"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor // final을 생성한다면 Autowired가 자동으로 달림!
public class RentalApi {

    private final RentalService rentalService; // final을 달아준다면 필수 생성(초기화)를 시켜줘야지 사용이 가능하기 때문에
    /*
       client가 /rental/ 요청을 날릴때
       /rental/{bookId}
      대여요청 => 대여요청을 날린 사용자의 대여가능 여부를 확인 ->  rental_mst(대여코드), rental_dtl
      -> 대여가 가능함(대여 가능 횟수 3권 미만일 때) > rental_mst(대여코드), rental_dtl
      -> 대여가 불가능함(대여 가능 횟수 0이면) > 예외처리
    */

    @PostMapping("/rental/{bookId}")
    public ResponseEntity<CMRespDto<?>> rental(@PathVariable int bookId,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {

        rentalService.rentalOne(principalDetails.getUser().getUserId(), bookId);
        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Success", null));
    }

    @PutMapping("/rental/{bookId}") // 반납 update이기때문 putmapping
    public ResponseEntity<CMRespDto<?>> rentalReturn(@PathVariable int bookId) {
        rentalService.returnBook(bookId);

        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Success", null));
    }

}
