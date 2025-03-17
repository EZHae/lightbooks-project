package com.itwill.lightbooks.domain;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "USERS")
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id")
    @EqualsAndHashCode.Include
    private String loginId;
    private String password;
    private String nickname;
    private String username;
    private Integer gender;
    private Integer year;
    private String phonenumber;
    private String email;

    @Column(name = "access_time")
    private LocalDateTime accessTime;
    
    @Column(name = "img_src")
    private String imgSrc;

    @ToString.Exclude
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserWallet userWallet;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public User updateProfile(String username, String nickname, String phonenumber, String email) {
		this.username = username;
		this.nickname = nickname;
		this.phonenumber = phonenumber;
		this.email = email;
		
		return this;
	}
	
	public User updatePassword(String password) {
		this.password = password;
		
		return this;
	}
	
	@Override// User 엔티티의 username(로그인 ID) 대신 id(사용자 PK) 를 반환
	public String getUsername() { //회차 조회수 관련해서 소설의 작성자가 본인인지 확인하여 본인이면 조회수 증가를 막기 위해 필요..
		return id.toString(); // username 대신 id 반환, Long -> String
	}
}
