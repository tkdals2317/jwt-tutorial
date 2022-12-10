package com.sangmin.jwttutorial.entity;

import com.sangmin.jwttutorial.enums.AuthorityName;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "authority")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "authority_name", length = 50)
    private AuthorityName authorityName;
}