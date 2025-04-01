document.addEventListener('DOMContentLoaded', () => {
	console.log('coinshop.js');
	
	let selectProductId = document.querySelector('div#selectProductId').textContent;
	let divTransferSection = document.querySelector('div#transferSection');
	let coin;
	let cash;
	
	const btnSelectProducts = document.querySelectorAll('a#selectProduct');
	btnSelectProducts.forEach(btn => {
		btn.addEventListener('click', () => {
			
			const coinBuyModal = new bootstrap.Modal(document.querySelector("div#coinBuyModal")); // 모달 객체 생성
			
			if (!document.querySelector('span#userId')) {
				alert('로그인이 필요합니다.');
				return;
			}
			
			coinBuyModal.show(); // 로그인한 경우에만 모달 띄우기
			
			let selectedCoinValue = btn.dataset.coin;
			let selectedPriceValue = btn.dataset.price;
			
			const selectedCoin = document.querySelector('span#selectedCoin');
			const selectedPrice = document.querySelector('strong#selectedPrice');
			
			selectedCoin.textContent = selectedCoinValue;
			selectedPrice.textContent = selectedPriceValue
			
			let productId = btn.dataset.product;
			selectProductId = Number(productId);
			console.log(selectProductId);
		});
	});
	
	const btnBuyFromKakao = document.querySelector('a#btnBuyFromKakao');
	btnBuyFromKakao.addEventListener('click', () => {
		divTransferSection.innerHTML = '';
		const divBuyFromTransfer = document.querySelector('div#divBuyFromTransfer');
		if (divBuyFromTransfer.classList.contains('border-dark')) {
			divBuyFromTransfer.classList.remove('border-dark');
		}
		const divBuyFromKakao = document.querySelector('div#divBuyFromKakao');
		if (!divBuyFromKakao.classList.contains('border-dark')) {
			divBuyFromKakao.classList.add('border-dark');
		}
		let userId = document.querySelector('span#userId').textContent;
		
		switch (selectProductId) {
			case 50: coin = 50; cash = 5000; break;
			case 100: coin = 100; cash = 10000; break;
			case 200: coin = 200; cash = 20000; break;
			case 300: coin = 300; cash = 30000; break;
			case 500: coin = 500; cash = 50000; break;
			case 1000: coin = 1000; cash = 100000; break;
			default: break;
		}
		
		console.log(coin, cash);
		const type = 0;
		const data = {userId, coin, cash, type}
		console.log(data);
		
		axios.post('/order/kakaopay/ready', data).then(response => {
		    console.log(response);
		    if (response.data.next_redirect_pc_url) {
		        
				// 창 크기 조절
		        const width = 800;
		        const height = 600;

		        const paymentWindow = window.open(response.data.next_redirect_pc_url, '_blank', `width=${width},height=${height}`);
				
				const interval = setInterval(() => {
				    if (paymentWindow.closed) {
				        clearInterval(interval);
				        // location.reload(); // 창이 닫히면 부모 창 새로고침
						window.location.href = "/"; // 부모 창을 홈으로 이동
				    }
				}, 500);
		    } else {
		        alert('실패');
		    }
		}).catch(error => {
		    console.error(error);
		});
	});
	
	const btnBuyFromTransfer = document.querySelector('a#btnBuyFromTransfer');
	btnBuyFromTransfer.addEventListener('click', () => {
		divTransferSection.innerHTML = '';
		const divBuyFromTransfer = document.querySelector('div#divBuyFromTransfer');
		if (!divBuyFromTransfer.classList.contains('border-dark')) {
			divBuyFromTransfer.classList.add('border-dark');
		}
		const divBuyFromKakao = document.querySelector('div#divBuyFromKakao');
		if (divBuyFromKakao.classList.contains('border-dark')) {
			divBuyFromKakao.classList.remove('border-dark');
		}
		
		let userId = document.querySelector('span#userId').textContent;

		switch (selectProductId) {
			case 50: coin = 50; cash = 5000; break;
			case 100: coin = 100; cash = 10000; break;
			case 200: coin = 200; cash = 20000; break;
			case 300: coin = 300; cash = 30000; break;
			case 500: coin = 500; cash = 50000; break;
			case 1000: coin = 1000; cash = 100000; break;
			default: break;
		}

		let html =
		`<div class="mt-2">
			<table>
				<tr>
					<th>입금 은행</th>
					<td class="ps-3">농협</td>
				</tr>
				<tr>
					<th>입금 계좌</th>
					<td class="ps-3">000-000-0000-00000</td>
				</tr>
			</table>
			<div class="d-flex justify-content-center">
				<button id="btnTransferApp" class="btn btn-sm btn-primary">
					결제 신청
				</button>
			</div>
		</div>`;
		divTransferSection.innerHTML = html;

		const btnTransferApp = document.querySelector('button#btnTransferApp');
		btnTransferApp.addEventListener('click', () => {
			const type = 0;
			const url = '/order/transfer/ready'
			const data = {userId, coin, cash, type};

			const result = confirm('결제 신청을 하시겠습니까?');
			if (result) {
				disableClicks();
				axios.post(url, data).then(response => {
					console.log(response);
					alert('결제 신청이 완료되었습니다.');
					window.location.reload();
				}).catch(error => {
					console.error(error);
				});
			}
		});
	});


	// DB에 반영되는 사이 사용자가 아무동작 못하게 투명벽 설치
	function disableClicks() {
		let blocker = document.createElement("div");
		blocker.id = "blocker";
	
		// CSS 스타일 동적으로 추가
		blocker.style.position = "fixed";
		blocker.style.top = "0";
		blocker.style.left = "0";
		blocker.style.width = "100vw";
		blocker.style.height = "100vh";
		blocker.style.background = "rgba(0, 0, 0, 0)";
		blocker.style.zIndex = "9999";
	
		document.body.appendChild(blocker);
	}
	
	// ============ 모달 ============
	// 모달창 nav 뒤로 밀어버리는 코드
	const navbar = document.querySelector('.navbar');
	const modalEl = document.getElementById("coinBuyModal");

	// 모달 열릴 때 navbar 위치 변경
	modalEl.addEventListener('show.bs.modal', () => {
	  navbar.style.position = 'static';     // navbar를 static으로 만들어서 모달 앞에 겹치지 않도록
	  navbar.style.zIndex = '1';            // z-index 낮추기
	});

	// 모달 닫힐 때 navbar 복구
	modalEl.addEventListener('hidden.bs.modal', () => {
	  navbar.style.position = 'sticky';    // 모달 닫으면 다시 sticky로 복원
	  navbar.style.zIndex = '1005';         // z-index 복원
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