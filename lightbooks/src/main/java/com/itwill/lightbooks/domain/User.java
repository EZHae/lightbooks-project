package com.itwill.lightbooks.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id")
    private String loginId;
    private String password;
    private String nickname;
    private String username;
    private Integer gender;
    private Integer age;
    private String phonenumber;
    private String email;

    @Column(name = "access_time")
    private LocalDateTime accessTime;
    
    @Column(name = "img_src")
    private String imgSrc;

    @ToString.Exclude
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = true)
    private UserWallet userWallet;
}