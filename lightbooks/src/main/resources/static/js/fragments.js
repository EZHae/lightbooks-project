document.addEventListener('DOMContentLoaded', () => {
    console.log('fragments.js');

	// 페이지가 로드된 후 실행되는 코드
	// 오류메세지 alert 한 후 세션 지우는 요청보냄.
	window.onload = function() {
		const errorMessage = document.querySelector('span#errorMessage').textContent;
		if (errorMessage === '아이디 또는 비밀번호를 확인하세요.') {
			alert(`${errorMessage}`);
			axios.post('../user/removeSession').then().catch(error);
		}
	};

	
	if (document.querySelector('button#profileDropdown')) {
		let userId = document.querySelector('span#userId').textContent;
		WalletRefresh(userId);
		
		const btnCoinRefresh = document.querySelector('button#coinRefresh');
		btnCoinRefresh.addEventListener('click', () => {
			let uri = `/user/getUserWallet?userId=${userId}&type=c`;
			axios.get(uri).then(response => {
				let coin = response.data;
				console.log(coin);
				let strongNowCoin = document.querySelector('strong#nowCoin');
				strongNowCoin.textContent = coin;
			}).catch(error => {
				console.error(error);
			});
		});
		
		const btnMileageRefresh = document.querySelector('button#mileageRefresh');
		btnMileageRefresh.addEventListener('click', () => {
			let uri = `/user/getUserWallet?userId=${userId}&type=m`;
			axios.get(uri).then(response => {
				let mileage = response.data;
				console.log(mileage);
				let strongNowMileage = document.querySelector('strong#nowMileage');
				strongNowMileage.textContent = mileage;
			}).catch(error => {
				console.error(error);
			});
		});
	}
	
	function WalletRefresh(userId) {
		console.log('WalletRefresh()');
		
		let uriC = `/user/getUserWallet?userId=${userId}&type=c`;
		axios.get(uriC).then(response => {
			let coin = response.data;
			console.log(coin);
			let strongNowCoin = document.querySelector('strong#nowCoin');
			strongNowCoin.textContent = coin;
		}).catch(error => {
			console.error(error);
		});
		
		let uriM = `/user/getUserWallet?userId=${userId}&type=m`;
		axios.get(uriM).then(response => {
			let mileage = response.data;
			console.log(mileage);
			let strongNowMileage = document.querySelector('strong#nowMileage');
			strongNowMileage.textContent = mileage;
		}).catch(error => {
			console.error(error);
		});
	}
	
});