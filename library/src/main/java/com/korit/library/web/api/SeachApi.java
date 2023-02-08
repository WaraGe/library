package com.korit.library.web.api;

import com.korit.library.security.PrincipalDetails;
import com.korit.library.service.SearchService;
import com.korit.library.web.dto.CMRespDto;
import com.korit.library.web.dto.SearchBookReqDto;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "도서 검색 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SeachApi {


    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<CMRespDto<?>> search(SearchBookReqDto searchBookReqDto,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if(principalDetails != null) {
            searchBookReqDto.setUserId(principalDetails.getUser().getUserId());
        }

        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Successfully"
                        , searchService.getSearchBooks(searchBookReqDto)));
    }

    @GetMapping("/search/totalcount")
    public ResponseEntity<CMRespDto<Integer>> getSearchBookTotalCount(SearchBookReqDto searchBookReqDto) {
        return ResponseEntity.ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(),
                        "Successfully", searchService.getSearchTotalCount(searchBookReqDto)));
    }
}
