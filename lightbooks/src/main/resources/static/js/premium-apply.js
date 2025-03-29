/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
	
	const btnPremiumSubmit = document.getElementById('btnPremiumSubmit')
	const userId = document.querySelector('span#userId').textContent;
	console.log("현재 아이디: ",userId);
	
	const novelId = btnPremiumSubmit.dataset.novelId;
	const type  = btnPremiumSubmit.dataset.novelGrade + 1; // 유료 신청이면 +1 무료신청이면 -1
	const status = btnPremiumSubmit.dataset.status;
	console.log("data : ", novelId, type, status, userId);
	
	
	
	
	// 조건 확인 하기
	function checkapplyCondition(){
		axios.get(`/premium/can-apply?novelId=${novelId}`)
		.then(response => {
			console.log(response)
			
			if(response.data) {
				btnPremiumSubmit.disabled = false; // 조건에 부족하면 비활성화
				btnPremiumSubmit.innerText = "신청 가능"
			} else {
				btnPremiumSubmit.disabled = true; // 조건에 부족하면 비활성화
				btnPremiumSubmit.innerText = "신청 불가능"
			}
		})
		.catch(error);
	}
	
	// 유료 신청 하기
	btnPremiumSubmit.addEventListener('click', (event) => {
		let result = confirm('신청 하십니까?');
		
		const data = {novelId, type, status, userId};
		
		if(result) {
			axios.post(`/novel/premium/apply`, data)
			.then(response => {
				console.log(response);
				alert('신청 완료');
				window.location.href = `/myworks/mynovel?id=${userId}`;
			}).catch(error => {
				console.log(error);
			});
		};		
	});
});