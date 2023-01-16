package com.korit.library.web.api.admin;

import com.korit.library.aop.annotation.ParamsAspect;
import com.korit.library.aop.annotation.ValidAspect;
import com.korit.library.service.BookService;
import com.korit.library.web.dto.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"관리자 도서관리 API"})
@RequestMapping("/api/admin")
@RestController
public class BookApi {

    @Autowired
    private BookService bookService;

    @ParamsAspect
    @ValidAspect
    @GetMapping("/books")
    public ResponseEntity<CMRespDto<List<BookMstDto>>> searchBook(@Valid SearchReqDto searchReqDto, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Successfully", bookService.searchBook(searchReqDto)));

    }

    @GetMapping("/categories")
    public ResponseEntity<CMRespDto<List<CategoryDto>>> getCategories() {
        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Successfully", bookService.getCategories()));
    }
    @ParamsAspect
    @ValidAspect
    @PostMapping("/book") // 등록
    public ResponseEntity<CMRespDto<?>> registerBook(@Valid @RequestBody BookReqDto bookReqDto, BindingResult bindingResult) {
        bookService.registerBook(bookReqDto);
        return ResponseEntity
                .created(null)
                .body(new CMRespDto<>(HttpStatus.CREATED.value(), "Successfully", true));
    }

    @ParamsAspect
    @ValidAspect
    @PutMapping("/book/{bookCode}") // 수정
//    @PatchMapping // 수정
    public ResponseEntity<CMRespDto<?>> modifyBook(@PathVariable String bookCode, @Valid @RequestBody BookReqDto bookReqDto, BindingResult bindingResult) {
        bookService.modifyBook(bookReqDto);
        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Successfully", true));
    }

    @ParamsAspect
    @DeleteMapping("/book/{bookCode}")
    public ResponseEntity<CMRespDto<?>> deletebook(@PathVariable String bookCode) {
        bookService.removeBook(bookCode);
        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Delete Successfully", true));
    }

    @ParamsAspect
    @ValidAspect
    @PatchMapping("/book/{bookCode}")
    public ResponseEntity<CMRespDto<?>> maintainModifyBook(@PathVariable String bookCode, @Valid @RequestBody BookReqDto bookReqDtm, BindingResult bindingResult) {
        bookService.maintainModifyBook(bookReqDtm);
        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Update Successfully", true));
    }

    //파일 입출력
    @ParamsAspect
    @PostMapping("/book/{bookCode}/images")
    public ResponseEntity<CMRespDto<?>> registerBookImg(@PathVariable String bookCode, @RequestPart List<MultipartFile> files) {
        bookService.registerBookImage(bookCode, files);
        return ResponseEntity.ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Img Upload Successfully", true));
    }

}
