document.addEventListener('DOMContentLoaded', () => {
    console.log('fragments.js');
	
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