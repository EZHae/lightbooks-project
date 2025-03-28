/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
	console.log(document.querySelector('form#novelForm'));
	
	// 체크박스 '비정기' 선택 시 나머지 체크박스 disabled
	const checkBox = document.getElementById("inlineCheckbox0");
	const otherCheckboxes = document.querySelectorAll(".form-check-input:not(#inlineCheckbox0)");
	
	checkBox.addEventListener('change', function () {
		if(this.checked) {
			// 비정기가 체크되면 다른 체크들 비활성화하고 해제
			otherCheckboxes.forEach(cb => {
				cb.checked = false;
				cb.disabled = true;
			});
		} else {
			// 비정기가 체크가 해제되면 활성화
			otherCheckboxes.forEach(cb => {
				cb.disabled = false;
			})
		}
	});
	
	document.querySelector('form#novelForm').addEventListener('submit', function(event) {
		const checked = document.querySelectorAll('input[name="days"]:checked').length;
		
		if(checked === 0){
			alert('적어도 하나의 요일을 선택해야 합니다.')
			event.preventDefault();
		}
	});
	
	
	
	//  파일 선택시 미리 보기만 
	let selectedFile = null;
	
	document.getElementById('coverInput').addEventListener('change', function(e) {
		const file = e.target.files[0];
		if(!file) return;
		
		selectedFile = file;
		
		 
		const reader = new FileReader();
		reader.onload = function (e) {
			
			const img = new Image();
			img.onload = function () {
				
				console.log("선택한 파일:", file);
				const minWidth = 395;
				const maxWidth = 405;
				const minHeight = 595;
				const maxHeight = 605;
				// 픽셀 허용 범위 추가
				// 사이즈 검사 (400 x 600)
					if(img.width < minWidth || img.width > maxWidth || img.height < minHeight || img.height > maxHeight) {
						alert('이미지는 400x600 사이즈여야 합니다!');
						document.getElementById('coverInput').value = '';
						document.getElementById('coverPreview').src = "/images/defaultCover.jpg";
						document.getElementById('coverSrc').value = '';
						
						selectedFile = null;
						fileNameSpan.textContent = "선택된 파일 없음";
						
						return;
					}
				console.log("이미지 로드 성공!", img.width, img.height);
				document.getElementById('coverPreview').src = e.target.result;
			};
			img.src = e.target.result;
		};
		reader.readAsDataURL(file);
	});
		
	// 작성 완료시
	document.getElementById('novelForm').addEventListener('submit', function (e) {
		e.preventDefault(); // 기본 제출 막기

		document.getElementById('uploadBtn').disabled = true;
		document.getElementById('coverInput').disabled = true;
		document.getElementById('resetImageBtn').disabled = true;
		
		// 비동기 이미지 업로드
		if(selectedFile){
			const formData = new FormData();
			formData.append("file", selectedFile);
			
			axios.post("/upload/image", formData)
			.then(response => {
				const path = response.data; // 서버에서 이미지 경로 문자열로 반환
				document.getElementById('coverSrc').value = path;
				
				// 커버 경로 세팅 끝났으니 폼 제출 강제 실행!
				document.getElementById('novelForm').submit();
			})
			.catch(error => {
				console.error(error);
				alert('이미지 업로드 중 오류가 발생했습니다.')
			});
		} else {
			// 이미지 없이 등록 가능할 때
			document.getElementById('coverSrc').value = "/images/defaultCover.jpg";
			document.getElementById("novelForm").submit();
		}
	});
	
	document.getElementById('resetImageBtn').addEventListener('click', function() {
		const fileInput = document.getElementById('coverInput');
		const preview = document.getElementById('coverPreview');
		const hiddenInput = document.getElementById('coverSrc');
		
		// 파일 초기화
		fileInput.value = '';
		
		// 미리보기 이미지 디폴트로 변경
		preview.src = "/images/defaultCover.jpg";
		
		// hidden input 비우기
		hiddenInput.value = '';
		
		selectedFile = null;
		
		console.log("이미지 리셋 완료");
	});
	
	let selectedFileName = document.getElementById('fileName').textContent;

	// 파일 이름 ux 변경
	const fileInput = document.getElementById('coverInput');
	const customeFileBtn = document.getElementById('customFileBtn');
	const fileNameSpan = document.getElementById('fileName');
	const preview = document.getElementById('coverPreview');
	const hiddenInput = document.getElementById('coverSrc');

	customeFileBtn.addEventListener('click', () => {
		fileInput.click();
	});

	// 파일 이름 변경 - 파일 선택 후 다시 파일 선택 취소 시 선택된 파일 이름
	fileInput.addEventListener('change', (e) => {
		const file = e.target.files[0];
		if(file) {
			const reader = new FileReader();
			reader.onload = function(event) {
				preview.src = event.target.result;
			}
			reader.readAsDataURL(file);
			
			selectedFileName = file.name;
			fileNameSpan.textContent = file.name;
		} else {
			fileNameSpan.textContent = selectedFileName;
		}
	});
	
	// 이미지 삭제 버튼
	document.getElementById('resetImageBtn').addEventListener('click', function() {
		
		// 파일 초기화
		fileInput.value = '';
		
		// 미리보기 이미지 디폴트로 변경
		preview.src = "/images/defaultCover.jpg";
		
		// hidden input 비우기
		hiddenInput.value = '';
		
		selectedFile = null;
		fileNameSpan.textContent = '선택된 파일 없음';
		
		console.log("이미지 리셋 완료");
	});
	
	// 글자수
	const introInput = document.getElementById("intro");
	const introLength = document.getElementById("introLength");

	introInput.addEventListener("input", () => {
	  introLength.textContent = `${introInput.value.length} / 200`;
	});
});