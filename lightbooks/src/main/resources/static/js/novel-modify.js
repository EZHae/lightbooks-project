/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
	const novelId = document.querySelector('input#id').value;
	console.log(novelId);
	
	// 체크박스 '비정기' 선택 시 나머지 체크박스 disabled
	const checkBox = document.getElementById("inlineCheckbox0");
	const otherCheckboxes = document.querySelectorAll(".form-check-input:not(#inlineCheckbox0)");
	
	
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
				window.location.href = `/novel/my-works?id=${userId}`;	
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
	
});