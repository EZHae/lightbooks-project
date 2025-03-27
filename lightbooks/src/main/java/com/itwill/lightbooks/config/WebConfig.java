package com.itwill.lightbooks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * 정적 자원(업로드된 이미지 등)을 웹에서 접근할 수 있도록 경로를 매핑하는 설정 클래스
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	/*
	 * 업로드된 소설 커버 이미지를 웹 경로로 매핑
	 * 
	 * 예 :			// 자바에서 역슬래시는 유니코드로 인식 할 수 있어서 위험. 자바에서는 자동으로 인식함.
	 *  - 실제 저장 경로 (윈도우) : C:/upload/images/novelcovers/abcd.jpg 
	 *  - 실제 저장 경로 (리눅스) : /home/ezhae3221/gcp/upload/images/novelcovers/abcd.jpg
	 *  - 웹에서 접근할 경로: http://localhost:8080/uploads/novel-covers/abcd.jpg
	 */
	
	@Override
	public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/uploads/novel-covers/**") // 사용자가 접근할 URL 패턴
				.addResourceLocations(
						// 실제 파일이 저장된 로컬/서버 경로를 지정 (os에 따라 다르게 설정)
						"file:/home/ezhae3221/gcp/upload/images/novelcovers/", // 리눅스 서버용
						"file:/C:/upload/images/novelcovers/");				   // 윈도우 로컬 개발용 경로
	}
}
