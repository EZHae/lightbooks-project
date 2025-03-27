document.addEventListener('DOMContentLoaded', () => {
	console.log('novel-donation-modal.js');
	
	const openDonationBtn = document.querySelector('button#openDonationBtn');
	const donationModal = new bootstrap.Modal(document.querySelector("div#donationModal")); // 모달 객체 생성
	let novelId;
	let userId;
	let novelTitle;
	let novelWriter;
	let html = ``;
	
	// 로그인 했을 때만 모달창 띄우게하기
	openDonationBtn.addEventListener('click', () => {
		if (!document.querySelector('span#userId')) {
			alert('로그인이 필요합니다.');
			return;
		}
		const novelUserId = document.querySelector('span#novelUserId').textContent;
		if (novelUserId == document.querySelector('span#userId').textContent) {
			alert('본인 소설에는 후원을 할 수 없습니다.');
			return;
		}
		novelId = document.querySelector('span#novelId').textContent;
		userId = document.querySelector('span#userId').textContent;
		novelTitle = document.querySelector('span#inputNovelTitle').textContent;
		novelWriter = document.querySelector('span#novelwriter').textContent;
		donationModal.show(); // 로그인한 경우에만 모달 띄우기
	});
	
	
	let nowCoin;
	const divTabResultSection = document.querySelector('div#tabResultSection');
	const modalItems = document.querySelectorAll('a.nav-tab-item');
	modalItems.forEach(item => {
		item.addEventListener('click', (event) => {
			modalItems.forEach(tab => tab.classList.remove('active')); // 모든 탭에서 'active' 클래스 제거
			item.classList.add('active'); // 선택한 탭만 'active' 추가			
			
			divTabResultSection.innerHTML = '';
			itemName = event.target.dataset.itemname;
			
			if (itemName == 'donation') {
				nowCoin = document.querySelector('strong#nowCoin').textContent;
				divTabResultSection.innerHTML = 
				`
					<div>
						<span>후원 가능 코인</span>
						<strong id="donationPossibleCoin">${nowCoin}</strong>
					</div>
					<div class="mt-2">
						<label for="donationCoin">후원 코인</label>
						<input type="number" id="donationCoin" class="form-control" min=10 />
					</div>
					<div>
						<strong>후원은 10코인부터 가능합니다.</strong>
					</div>
					<div class="d-flex justify-content-end">
						<button id="btnDonation" class="disabled btn btn-outline-primary">후원하기</button>
					</div>
				`;
				
				const btnDonation = document.querySelector('button#btnDonation');
				const inputDonationCoin = document.querySelector('input#donationCoin');
				let inputCoin;
				inputDonationCoin.addEventListener('change', (event) => {
					inputCoin = Number(event.target.value);
					const maxCoin = Number(donationPossibleCoin.textContent);
					
					if (inputCoin < 10) {
						inputCoin = 10;
					} else if (inputCoin > maxCoin) {
						inputCoin = maxCoin;
					}
					inputDonationCoin.value = inputCoin;
					if (btnDonation.className.match('disabled')) {
						btnDonation.classList.remove('disabled');
					}
				});
				
				btnDonation.addEventListener('click', () => {
					let result = confirm(`"${novelWriter}"작가의 "${novelTitle}"작품에\n"${inputCoin}" 코인을 후원하시겠습니까?`);
					if (result) {
						disableClicks();
						divTabResultSection.innerHTML = 
						`
							<div class="d-flex justify-content-center">
								<i class="fs-1 bi bi-hand-thumbs-up"></i>
							</div>
							<div class="mt-1 d-flex justify-content-center">
								<strong>소중한 후원 감사합니다.</strong>
							</div>
							<div class="mt-1 d-flex justify-content-center">
								<span>잠시 후 페이지가 이동됩니다.</span>
							</div>
						`;
						const coin = -Number(inputCoin);
						const data = {userId, novelId, coin};
						console.log(data);
						axios.post(`/user/donation`, data).then(response => {
							window.location.reload();
						}).catch(error => {
							console.error(error);
						});
					}
				});
			} else if (itemName == 'donationRanking' ) {
				divTabResultSection.innerHTML =
					`
						<h5 class="text-center">${novelTitle}</h4>
						<div class="row border-bottom border-black">
							<div class="col-2 text-center">
								순위
							</div>
							<div class="col-7">
								닉네임
							</div>
							<div class="col-3">
								총 후원 코인
							</div>
						</div>
					`;
				axios.get(`/user/donation/ranking?novelId=${novelId}`).then(response => {
					// TODO
					console.log(response);
					const rankingData = response.data;
					let rank = 1;
					let html;
					
					if (rankingData.length == 0) {
						divTabResultSection.innerHTML +=
						`
						<div class="text-center mt-3">
							<span>첫번째 후원자가 되어주세요!</span>
						</div>
						`;
						return;
					}

					rankingData.forEach(data => {
						const nickname = data[0];
						const totalDonation = Math.abs(data[1]) + ' 코인';
						html = 
						`
							<div class="row border-bottom">
								<div class="col-2 text-center" id="rank${rank}">
									${rank}
								</div>
								<div class="col-7">
									${nickname}
								</div>
								<div class="col-3 text-end">
									${totalDonation}
								</div>
							</div>
						`;
						divTabResultSection.innerHTML += html;
						
						let divDonationRank = document.querySelector(`div#rank${rank}`);
						if (rank === 1) {
							divDonationRank.innerHTML =
							`
								<i class="bi bi-trophy-fill" style="color: gold"></i>
							`;
						} else if (rank === 2) {
							divDonationRank.innerHTML =
							`
								<i class="bi bi-trophy-fill" style="color: silver"></i>
							`;
						} else if (rank === 3) {
							divDonationRank.innerHTML =
							`
								<i class="bi bi-trophy-fill" style="color: #cd7f32;"></i>
							`;
						}
						rank += 1;
					});
					divTabResultSection.innerHTML += 
					`
						<div class="mt-4">
							<span>후원랭킹에는 상위 10명까지 조회됩니다.<span>
						</div>
					`
				}).catch(error => {
					console.error(error);
				});
			}
		});
	});
	
	
	
	
	
	
	// ============ 모달 ============
	const navbar = document.querySelector('.navbar');
	const modalEl = document.getElementById('donationModal');
	const navCollapse = document.getElementById('navbarSupportedContent');
	// collapse 토글 버튼 이벤트 감지
	const toggler = document.querySelector('.navbar-toggler');

	toggler.addEventListener('click', () => {
		navbar.style.position = 'sticky';
		navbar.style.zIndex = '1200';
	});

	modalEl.addEventListener('show.bs.modal', () => {
	  // collapse가 열려 있으면 닫고 숨김
	  if (navCollapse.classList.contains('show')) {
	    const collapseInstance = bootstrap.Collapse.getInstance(navCollapse);
	    if (collapseInstance) {
	      collapseInstance.hide(); // Bootstrap 방식으로 닫기
	    }
	  }
	  
	  navbar.style.position = 'static';
	  navbar.style.zIndex = '1';
	});

	// 모달 닫힐 때 다시 복구
	modalEl.addEventListener('hidden.bs.modal', () => {
		navbar.style.position = 'sticky';
		navbar.style.zIndex = '1200'
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
});