package com.korit.library.service;

import com.korit.library.entity.BookImage;
import com.korit.library.entity.BookMst;
import com.korit.library.entity.CategoryView;
import com.korit.library.exception.CustomValidationException;
import com.korit.library.repository.BookRepository;
import com.korit.library.web.dto.*;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class BookService {

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private BookRepository bookRepository;

    public List<BookMst> searchBook(SearchReqDto searchReqDto) {
        searchReqDto.setIndex();
        return bookRepository.searchBook(searchReqDto);
    }

    public List<CategoryView> getCategories() {
        return bookRepository.findAllCategory();
    }

    public void registerBook(BookReqDto bookReqDto) {
        duplicateBookCode(bookReqDto.getBookCode());
        bookRepository.saveBook(bookReqDto);
    }

    private void duplicateBookCode(String bookCode) {
        BookMst bookMst = bookRepository.findBookByBookCode(bookCode);
            if(bookMst != null) { //user가 있다면
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

            throw new CustomValidationException(errorMap); // error 생성
        }
        List<BookImage> bookImages = new ArrayList<>();

        files.forEach(file -> {
            String originFileName = file.getOriginalFilename(); // 파일 이름 가져오기
            String extension = originFileName.substring(originFileName.lastIndexOf(".")); // 확장자명 찾는것
            String tempFileName = UUID.randomUUID().toString().replaceAll("-","") + extension; // 랜덤으로 배정된 번호를 가져오고 확장자를 붙여주기

            Path uploadPath = Paths.get(filePath + "book/" + tempFileName);
            //패스객체 생성

            File f = new File(filePath + "book"); // /book까지의 경로를 가져온후,
            if(!f.exists()) { // 경로가 있다면 true !not을 붙이면 경로가 없는것
                f.mkdirs(); //모든 경로를 생성하라 (경로가 없으면) == make dir
            }

            try {
                Files.write(uploadPath, file.getBytes()); //file에서 꺼낸 하나의 파일을 uploadPath에 쓰라는것
            } catch (IOException e) { // 경로가 없다면 오류가 나니깐 IOException 필수
                throw new RuntimeException(e);
            }

            BookImage bookImage = BookImage.builder()
                    .bookCode(bookCode)
                    .saveName(tempFileName)
                    .originName(originFileName)
                    .build();
            
            bookImages.add(bookImage); // list파일을 하나식 꺼내서 파일에 저장후 dto에 들어가서 list를 add(이미지의 갯수만큼)
        });
        bookRepository.registerBookImages(bookImages);
    }
    public List<BookImage> getBooks(String bookCode) {
        return bookRepository.findBookImageAll(bookCode);
    }

    public void removeBookImage(int imageId) {
        BookImage bookImage = bookRepository.findBookImageByImageId(imageId);
        if(bookImage == null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "존재하지 않는 아이디 입니다");

            throw new CustomValidationException(errorMap);
        }
        if(bookRepository.deleteBookImage(imageId) > 0) { //0보다 크므로 지웠다는 의미(파일이 있었다!)
            File file = new File(filePath + "book/" + bookImage.getSaveName()); // getSaveName == 저장된 파일의 이름
            if(file.exists()) { // 존재 한다면
                file.delete();
            }
            log.info("Deleting book image!");
        }
    }
}
