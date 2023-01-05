package com.korit.library.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleDto {
    private int roleId;
    private String roleName;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}