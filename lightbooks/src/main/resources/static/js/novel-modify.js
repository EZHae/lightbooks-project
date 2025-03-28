/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
	const novelId = document.querySelector('input#id').value;
	console.log(novelId);
	
	// 체크박스 '비정기' 선택 시 나머지 체크박스 disabled
	const checkBox = document.getElementById("inlineCheckbox0");
	const otherCheckboxes = document.querySelectorAll(".days:not(#inlineCheckbox0)");
	
	
	const btnDelete = document.querySelector('button#btnDelete');
	
	// 작품 삭제 이벤트 리스너
	btnDelete.addEventListener('click', function(event){
		event.preventDefault(); // 폼 기본 제출 방지
		
		const firstCheck = confirm("정말 삭제 하시겠습니까?");
		if(!firstCheck) return;
		
		const secondCheck  = confirm("이 작업은 되돌릴 수 없습니다. 정말 삭제하시겠습니까?")
		
		if(secondCheck) {
			const id = document.querySelector('input#id').value;
			const userId = document.querySelector('input#userId').value;
			console.log("id value: ",id)	
			
			const url = `/novel/delete?id=${id}&userId=${userId}`;
			
			axios.post(url)
			.then(() => {
				alert("삭제가 완료되었습니다.")
				window.location.href = `/myworks/mynovel?id=${userId}`;	
			})
			.catch(error => {
				alert("삭제 중 오류가 발생했습니다.");
				console.error(error);
			});
		}
	});
	
	if(checkBox.checked) {
		otherCheckboxes.forEach(cb => {
			cb.cbecked = false;
			cb.disabled = true;
		})
	}
	
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
	
	document.querySelector('form#novelUpdateForm').addEventListener('submit', function(event) {
		const checked = document.querySelectorAll('input[name="days"]:checked').length;
		
		if(checked === 0){
			alert('적어도 하나의 요일을 선택해야 합니다.')
			event.preventDefault();
		}
	});
	
	// 파일 업데이트 
	
	let selectedFile = null;
	const form = document.getElementById('novelUpdateForm');
	const formData = new FormData(form);

	// 새 이미지 새로 
	document.getElementById('btnUpdate').addEventListener('click', function(e) {
		e.preventDefault();
		
		document.getElementById('btnUpdate').disabled = true;
	    document.getElementById('btnDelete').disabled = true;
	    document.getElementById('coverInput').disabled = true;
	    document.getElementById('resetImageBtn').disabled = true;
		
		if(selectedFile) {
			// 이미지가 새로 선택됬을 때
			const formData = new FormData();
			formData.append("file", selectedFile);
			
			axios.post('/upload/image', formData)
				.then(response => {
					const path = response.data;
					document.getElementById('coverSrc').value = path; // 이미지 경로 저장
					document.getElementById('novelUpdateForm').submit(); // 폼 제출
			})
			.catch(error => {
				console.error(error);
				
			});
		} else {
			// 새 이미지 선택이 없는 경우 -> 기존 coverSrc 유지
			document.getElementById('novelUpdateForm').submit();
		}
		
	});
	
	//  파일 선택시 미리 보기만 
	
	
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

	// 페이지 로드시 초기 글자 수 반영
	introLength.textContent = `${introInput.value.length} / 200 `;
	// 글자수 반영
	introInput.addEventListener("input", () => {
	  introLength.textContent = `${introInput.value.length} / 200`;
	});
});