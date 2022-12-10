package com.sangmin.jwttutorial.dto;

import com.sangmin.jwttutorial.enums.AuthorityName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto {

    private AuthorityName authorityName;

}
