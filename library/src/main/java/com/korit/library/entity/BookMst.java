package com.korit.library.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookMst {

    @ApiModelProperty(hidden = true)
    private int bookId;

    @ApiModelProperty(value = "도서코드", example = "소록-N")
    private String bookCode;
    @ApiModelProperty(value = "도서명", example = "그들이 말하지 않는 23가지")
    private String bookName;
    @ApiModelProperty(value = "저자", example = "장하준")
    private String author;
    @ApiModelProperty(value = "출판사", example = "부키")
    private String publisher;
    @ApiModelProperty(value = "출판일", example = "2000-00-00")
    private LocalDate publicationDate;
    @ApiModelProperty(value = "분야", example = "소설")
    private String category;
    @ApiModelProperty(value = "상태", example = "Y")
    private String rentalStatus;

    private BookLike bookLike; //bookmst에 booklike를 join

}
