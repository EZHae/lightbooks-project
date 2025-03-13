package com.itwill.lightbooks.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
//-> 스프링 컨테이너에서 생성하고 관리하는 설정 컴포넌트
//-> 스프링 컨테이너에서 필요한 곳에 의존성을 주입해 줄 수 있음.

@EnableMethodSecurity
//->  각 컨트롤러의 메서드의 애너테이션으로 접근 관리 가능
// 예시) PostController::create 확인하기
public class SecurityConfig {

	// Spring Security 5 버전부터 비밀번호는 반드시 암호화(Encoding)를 해야만 함.
	// 만약 비밀번호를 암호화하지 않으면 HTTP 403(access denied, 접근 거부) 또는
	// HTTP 500(internal server serror, 내부 서버 오류) 에러가 발생함.
	// 비밀번호를 암호화하는 객체를 스프링 컨테이너가 빈으로 관리해야 함.
	@Bean // -> 스프링 컨테이너에서 관리하는 객체(빈) -> 필요할때 해당 메서드를 실행하면 의존성을 주입함.
	PasswordEncoder passwordEncoder() {
		log.info("BCryptPasswordEncoder 생성");
		return new BCryptPasswordEncoder();
	}

	/*
	 * SecurityFilterChain: 스프링 시큐리티 필터 체인 객체(Bean) - 로그인/로그아웃, 인증필터에서 필요한 설정들을 구성.
	 * - 로그인 페이지(뷰), 로그아웃 페이지(뷰) 설정 - 페이지 접근 권한(ADMIN, USER, ...) 설정. - 인증 설정(로그인 없이
	 * 접근 가능한 페이지 vs 로그인해야만 접근할 수 있는 페이지)
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		log.info("securityFilterChain 생성");

		// CSRF(Cross Site Request Forgery) 기능 비활성화
		// CSRF 기능을 활성화한 경우에는 서버로 요청을 보낼 때 csrf 토큰을 항상 같이 보내야함
		// Ajax Post/Put/Delete 요청에서도 csrf 토큰을 서버로 전송해야 하는데,
		// Ajax 요청에서 csrf 토큰을 전달하지 않으면 HTTP 403 에러가 발생함.
		http.csrf(t -> t.disable());

		// 로그인 페이지(아이디/비밀번호 입력 폼) 설정을 스프링 시큐리티에서 제공하는 기본 HTML 페이지를 사용하도록 설정
		// http.formLogin(Customizer.withDefaults());

		// 커스텀 로그인 페이지 HTML 사용
		// 위처럼 기본을 사용하면 요청주소가 "/login"인 것을 아래처럼 바꿈. ("/login"은 시큐리티를 사용하면 기본제공됨)
        http.formLogin(t -> t
                .loginProcessingUrl("/user/signin") // 로그인 요청을 처리할 URL
                .successHandler(this::handleSuccess)
                .failureHandler(this::handleFailure)
            );
        
        // 로그아웃 설정 (현재 페이지 유지)
        http.logout(t -> t
            .logoutUrl("/user/signout") // 로그아웃 요청 URL
            .logoutSuccessHandler(this::handleLogout) // 로그아웃 후 현재 페이지로 이동
            .invalidateHttpSession(true) // 세션 무효화
            .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
        );

		/*
		 * 페이지 접근 권한, 인증 구성: - 컨트롤러 메서드에서 애너테이션으로 설정. (1) SecurityConfig
		 * 클래스(빈)는 @EnableMethodSecurity 애너테이션을 설정. (2) 각각의 컨트롤러 메서드에서 @PreAuthorize
		 * 또는 @PostAuthorize 애너테이션을 설정. - 장점: 새로운 요청 경로가 생겨도(컨트롤러가 추가되어도) Config 클래스는
		 * 변경이 불필요함 - 단점: 모든 설정을 한 곳에서 관리할 수 없음
		 */

		return http.build();
	}
	
	// 로그인 성공 핸들러: 로그인 요청을 보낸 페이지로 이동
	private void handleSuccess(HttpServletRequest request, HttpServletResponse response,
	                           Authentication authentication) throws IOException {
	    log.info("로그인 성공: {}", authentication.getName());

	    // 이전 페이지(로그인 요청을 보낸 페이지) URL 가져오기
	    String referer = request.getHeader("Referer");
	    if (referer == null || referer.isEmpty()) {
	        referer = "/"; // Referer가 없으면 홈으로 이동
	    }

	    // 로그인 성공 후 이전 페이지로 리다이렉트
	    response.sendRedirect(referer);
	}

	// 로그인 실패 핸들러: 홈("/")으로 이동하며 오류 메시지 전달
	private void handleFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
	    log.info("로그인 실패: {}", exception.getMessage());

	    // 이전 페이지(로그인 요청을 보낸 페이지) URL 가져오기
	    String referer = request.getHeader("Referer");
	    if (referer == null || referer.isEmpty()) {
	        referer = "/"; // Referer가 없으면 홈으로 이동
	    }

	    // 세션에 에러 메시지 저장
	    request.getSession().setAttribute("errorMessage", "아이디 또는 비밀번호를 확인하세요.");

	    // 로그인 실패 후 이전 페이지로 리다이렉트
	    response.sendRedirect(referer);
	}
    
    // 로그아웃 핸들러: 로그아웃 후 원래 페이지로 이동
    private void handleLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String referer = request.getHeader("Referer"); // 이전 페이지 URL 가져오기
        if (referer == null || referer.isEmpty()) {
            referer = "/"; // 이전 페이지 정보가 없으면 홈으로 이동
        }
        log.info("로그아웃 후 이동할 페이지: {}", referer);
        response.sendRedirect(referer);
    }
}
