package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;

import com.itwill.lightbooks.domain.NovelGradeRequest;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PremiumRequestDto {

	private Long id;
	private Long userId;
	private Long novelId;
	private String novelTitle;
	private Integer type;
	private Integer status;
	private LocalDateTime createdTime;
	
	@Setter
	@Transient
	private String userLoginId;
	
	@Setter
	@Transient
	private String userUsername;
	
	
	public PremiumRequestDto(NovelGradeRequest request) {
		this.id = request.getId();
		this.userId = request.getUser() != null ? request.getUser().getUserId() : null;
		this.novelId = request.getNovel() != null ? request.getNovel().getId() : null;
		this.novelTitle = request.getNovel() != null ? request.getNovel().getTitle() : null;
		this.type = request.getType();
		this.status = request.getStatus();
		this.createdTime = request.getCreatedTime();
		this.userLoginId = request.getUser() != null ? request.getUser().getLoginId() : null;
		this.userUsername = request.getUser() != null ? request.getUser().getUsername() : null;
	}
}
