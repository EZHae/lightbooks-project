/**
 * 
 */
document.addEventListener('DOMContentLoaded', () => {
	console.log('coinshop.js');
	
	const btnSelectGoods = document.querySelectorAll('a#selectGoods');
	console.log(btnSelectGoods);
	
	btnSelectGoods.forEach(btn => {
		btn.addEventListener('click', () => {
			let selectedCoinValue = btn.dataset.coin;
			let selectedPriceValue = btn.dataset.price;
			
			const selectedCoin = document.querySelector('span#selectedCoin');
			const selectedPrice = document.querySelector('strong#selectedPrice');
			
			selectedCoin.textContent = selectedCoinValue;
			selectedPrice.textContent = selectedPriceValue
		})
	});
});