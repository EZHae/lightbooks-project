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
});