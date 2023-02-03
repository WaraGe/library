package com.korit.library.web.api;

import com.korit.library.web.dto.CMRespDto;
import com.korit.library.web.dto.SearchBookReqDto;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "도서 검색 API")
@RestController
@RequestMapping("/api")
public class SeachApi {

    @GetMapping("/search")
    public ResponseEntity<CMRespDto<?>> search(SearchBookReqDto searchBookReqDto) {

        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Successfully", null));
    }
}
