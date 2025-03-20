document.addEventListener('DOMContentLoaded', () => {
	console.log('mileageshop.js');
	
	const btnSelectGlobalTicket = document.querySelector('a#selectGlobalTicket');
	btnSelectGlobalTicket.addEventListener('click', (event) => {
		let nowMileage = document.querySelector('strong#nowMileage').textContent;
		console.log(nowMileage);
		
		if (nowMileage < 100) {
			alert('마일리지가 부족합니다.');
			return;
		}
		
		let result = confirm('100 마일리지를 자유 이용권으로 교환하시겠습니까?');
		if (result) {
			const userId = document.querySelector('span#userId');

			const data = {}
			// axios.post('/user/mileagepayment/app', data)
		}
	})
});