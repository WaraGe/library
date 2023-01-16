package com.korit.library.service;

import com.korit.library.exception.CustomValidationException;
import com.korit.library.repository.BookRepository;
import com.korit.library.web.dto.*;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class BookService {

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private BookRepository bookRepository;

    public List<BookMstDto> searchBook(SearchReqDto searchReqDto) {
        searchReqDto.setIndex();
        return bookRepository.searchBook(searchReqDto);
    }

    public List<CategoryDto> getCategories() {
        return bookRepository.findAllCategory();
    }

    public void registerBook(BookReqDto bookReqDto) {
        duplicateBookCode(bookReqDto.getBookCode());
        bookRepository.saveBook(bookReqDto);
    }

    private void duplicateBookCode(String bookCode) {
        BookMstDto bookMstDto = bookRepository.findBookByBookCode(bookCode);
            if(bookMstDto != null) { //user가 있다면
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("bookCode", "이미 존재하는 도서코드입니다.");

                throw new CustomValidationException(errorMap);
        }
    }

    public void modifyBook(BookReqDto bookReqDto) {
        bookRepository.updateBookByBookCode(bookReqDto);
    }
    public void removeBook(String BookCode) {
        bookRepository.deleteBook(BookCode);
    }
    public void maintainModifyBook(BookReqDto bookReqDto) {
        bookRepository.maintainUpdateBookByBookCode(bookReqDto);
    }
    
    public void registerBookImage(String bookCode, List<MultipartFile> files) {
        if(files.size() < 1) { // 이미지가 없을때
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("files", "이미지를 선택하세요.");

            throw new CustomValidationException(errorMap);
        }
        List<BookImageDto> bookImageDtos = new ArrayList<>();

        files.forEach(file -> {
            String originFileName = file.getOriginalFilename(); // 파일 이름 가져오기
            String extension = originFileName.substring(originFileName.lastIndexOf(".")); // 확장자명 찾는것
            String tempFileName = UUID.randomUUID().toString().replaceAll("-","") + extension; // 랜덤으로 배정된 번호를 가져오고 확장자를 붙여주기

            Path uploadPath = Paths.get(filePath + "/book/" + tempFileName);
//          paths.get 경로 객체 생성

            File f = new File(filePath + "/book"); // /book까지의 경로를 가져온후, 
            if(!f.exists()) { // 경로가 있다면 true !not을 붙이면 경로가 없는것
                f.mkdirs(); //모든 경로를 생성하라 (경로가 없으면) == make dir
            }

            try {
                Files.write(uploadPath, file.getBytes()); //file에서 꺼낸 하나의 파일을 uploadPath에 쓰라는것
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BookImageDto bookImageDto = BookImageDto.builder()
                    .bookCode(bookCode)
                    .saveName(tempFileName)
                    .originName(originFileName)
                    .build();
            
            bookImageDtos.add(bookImageDto); // list파일을 하나식 꺼내서 파일에 저장후 dto에 들어가서 list를 add
        });

        bookRepository.registerBookImages(bookImageDtos);
    }

}
