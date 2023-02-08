package com.korit.library.service;

import com.korit.library.entity.BookLike;
import com.korit.library.exception.CustomRentalException;
import com.korit.library.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public int like(int bookId, int userId) {
        BookLike bookLike = BookLike.builder()
                .bookId(bookId)
                .userId(userId)
                .build();
        if(likeRepository.getLikeStatus(bookLike) > 0) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("LikeError", "이미 추천을 누른 도서명 입니다.");
            throw new CustomRentalException(errorMap);
        }

        likeRepository.addLike(bookLike);
        return likeRepository.getLikeCount(bookId);
    }

    public int dislike(int bookId, int userId) {
        BookLike bookLike = BookLike.builder()
                .bookId(bookId)
                .userId(userId)
                .build();
        if(likeRepository.getLikeStatus(bookLike) == 0) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("LikeError", "추천을 눌려주세요.");
            throw new CustomRentalException(errorMap);
        }

        likeRepository.deleteLike(bookLike);
        return likeRepository.getLikeCount(bookId);
    }
}
