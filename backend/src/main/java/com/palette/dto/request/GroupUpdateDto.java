package com.palette.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
public class GroupUpdateDto {
    @NotBlank(message = "그룹 이름을 입력해주세요.")
    private String groupName;

    @NotBlank(message = "그룹 정보를 입력해주세요.")
    private String groupIntroduction;

}
