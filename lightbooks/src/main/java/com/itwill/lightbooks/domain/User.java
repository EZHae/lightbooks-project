package com.itwill.lightbooks.domain;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
//@ToString
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
    
    @Column(name = "today_check", nullable = false, insertable = false)
    private Integer todayCheck;

    @JsonIgnore
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
	
	public User updateTodayCheck(int todayCheck) {
		this.todayCheck = todayCheck;
		
		return this;
	}
	
	//추가(다른 테이블에서 id를 가져가기 위한 메서드 ex)조회수 관련 본인글인지 확인 여부시 필요)
	public Long getUserId() {
	    return id;
	}
	
	@Override
	public String toString() {
	    return "User(id=" + id + 
	           ", loginId=" + loginId + 
	           ", password=********" + // 보안상 비밀번호 숨김 처리
	           ", nickname=" + nickname + 
	           ", username=" + username + 
	           ", gender=" + gender + 
	           ", year=" + year + 
	           ", phonenumber=" + phonenumber + 
	           ", email=" + email + 
	           ", accessTime=" + accessTime + 
	           ", imgSrc=" + imgSrc + ")";
	}
}
