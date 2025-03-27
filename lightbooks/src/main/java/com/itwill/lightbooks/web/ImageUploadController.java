package com.itwill.lightbooks.web;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class ImageUploadController {
	
	@PostMapping("/image")
	public ResponseEntity<String> imageUpload(@RequestParam("file") MultipartFile file) {
		try {
			
			// 각각 os에 저장 
			String os = System.getProperty("os.name").toLowerCase();
			String uploadDir = os.contains("win")
					? "C:/upload/images/novelcovers/"
					: "/home/ezhae3221/gcp/upload/images/novelcovers/";
			
			String uuid = UUID.randomUUID().toString();
			String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			String savedName = uuid + ext;
			
			Path savePath = Paths.get(uploadDir, savedName);
			Files.createDirectories(savePath.getParent());
			file.transferTo(savePath);
			
			return ResponseEntity.ok("/uploads/novel-covers/" + savedName);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패");
		}
	}
}