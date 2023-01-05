package com.korit.library.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
public class CMRespDto<T> {
    private String message;
    private T data;
}
