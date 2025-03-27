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
	
	
	document.getElementById('coverInput').addEventListener('change', function(e) {
		const file = e.target.files[0];
		if(!file) return;
		
		const img = new Image();
		img.src = URL.createObjectURL(file);
		
		// 픽셀 허용 범위 추가
		const minWidth = 395;
		const maxWidth = 405;
		const minHeight = 595;
		const maxHeight = 605;
		
		console.log("선택한 파일:", file);
		
		img.onload = () => {
			// 사이즈 검사 (400 x 600)
			console.log("이미지 로드 성공!", img.width, img.height);
			
			if(img.width < minWidth || img.width > maxWidth || img.hight < minHeight || img.hight > maxHeight) {
				alert('이미지는 400x600 사이즈여야 합니다!');
				e.target.value = '';
				document.getElementById('coverPreview').src = "/images/defaultCover.jpg";
				document.getElementById('coverSrc').value = '';
				return;
			}
			
			// 비동기 업로드
			const formData = new FormData();
			formData.append("file", file);
			
			axios.post("/upload/image", formData)
			.then(response => {
				const path = response.data; // 서버에서 이미지 경로 문자열로 반환
				
				document.getElementById('coverPreview').src = path;
				document.getElementById('coverSrc').value = path;
			})
			.catch(error => {
				console.error(error);
				alert('이미지 업로드 중 오류가 발생했습니다.')
			});
		}
	});
});