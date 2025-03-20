/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
	
	const btnPremiumSubmit = document.getElementById('btnPremiumSubmit')
	const userId = document.querySelector('span#userId').textContent;
	console.log("현재 아이디: ",userId);
	
	const novelId = btnPremiumSubmit.dataset.novelId;
	const type = btnPremiumSubmit.dataset.novelGrade;
	const status = btnPremiumSubmit.dataset.status;
	console.log("data : ", novelId, type, status, userId);
	
	btnPremiumSubmit.addEventListener('click', (event) => {
		let result = confirm('신청 하십니까?');
		
		const data = {novelId, type, status, userId};
		
		if(result) {
			axios.post(`/novel/premium/apply`, data)
			.then(response => {
				console.log(response);
				alert('신청 완료');
				window.location.href = `/novel/my-works?id=${userId}`;
			}).catch(error => {
				console.log(error);
			});
		};		
	});
});