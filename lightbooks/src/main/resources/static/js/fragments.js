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
			WalletRefresh(userId);
			/*
			let uri = `/user/getUserWallet?userId=${userId}&type=c`;
			axios.get(uri).then(response => {
				let coin = response.data;
				console.log(coin);
				let strongNowCoin = document.querySelector('strong#nowCoin');
				strongNowCoin.textContent = coin;
			}).catch(error => {
				console.error(error);
			});
			*/
		});
		
		const btnMileageRefresh = document.querySelector('button#mileageRefresh');
		btnMileageRefresh.addEventListener('click', () => {
			WalletRefresh(userId);
			/*
			let uri = `/user/getUserWallet?userId=${userId}&type=m`;
			axios.get(uri).then(response => {
				let mileage = response.data;
				console.log(mileage);
				let strongNowMileage = document.querySelector('strong#nowMileage');
				strongNowMileage.textContent = mileage;
			}).catch(error => {
				console.error(error);
			});
			*/
		});
	}
	
	function WalletRefresh(userId) {
		console.log('WalletRefresh()');
		/*
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
		*/
		
		const url = `/user/getUserWallets?userId=${userId}`;
		axios.get(url).then(response => {
			console.log(response.data);
			let coin = response.data.coin;
			let mileage = response.data.mileage;
			
			let strongNowCoin = document.querySelector('strong#nowCoin');
			strongNowCoin.textContent = coin;
			
			let strongNowMileage = document.querySelector('strong#nowMileage');
			strongNowMileage.textContent = mileage;
		}).catch(error => {
			console.error(error);
		})
	}
	
	
	// 검색창에 공백 입력할 때 
	const btnSearch = document.querySelector('button#btnSearch')
		
	btnSearch.addEventListener('click', validateSearch)
	// 검색 버튼 클릭 시 공백만 입력된 경우 검색 방지
	function validateSearch(event) {
	    let keyword = document.getElementById("searchKeyword").value.trim();
	    if (keyword.length === 0) {
	        alert("검색어를 입력해주세요!"); // 사용자에게 알림
			event.preventDefault(); // 🔹 폼 전송 막기 (필수)
	        return false; // 검색 요청 차단
	    }
	    return true;
	}
	
	const spanCountNoReadNotification = document.querySelector('span#countNoReadNotification');
	
	
	
	// ============ 모달 ============
	// 모달창 nav 뒤로 밀어버리는 코드
	const navbar = document.querySelector('.navbar');
	const modalEl = document.getElementById('signModal');

	// 모달이 열릴 때만 적용
	modalEl.addEventListener('show.bs.modal', () => {
	  navbar.classList.add('modal-open-navbar');
	});

	modalEl.addEventListener('hidden.bs.modal', () => {
	  navbar.classList.remove('modal-open-navbar');
	});
	// 스크롤 이벤트 리스너 추가
	window.addEventListener('scroll', () => {
	  // 스크롤이 10px 이상 내렸다면
	  if (window.scrollY > 10) {
	    navbar.style.boxShadow = '0 4px 8px rgba(0, 0, 0, 0.05)'; // 그림자 효과 추가
	  } else {
	    navbar.style.boxShadow = 'none'; // 스크롤이 맨 위로 올라오면 그림자 없앰
	  }
	});
});