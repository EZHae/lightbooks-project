document.addEventListener('DOMContentLoaded', () => {
	console.log('coin-payment.js');
	const userId = document.querySelector('span#userId').textContent;
	
	let currentPage = 0; // 현재 페이지
	const pageSize = 5; // 페이지 크기 설정
	const blockSize = 5;
	let lastPage = 0;

	// 첫 번째 페이지(0페이지)부터 데이터를 가져옵니다.
	getCoinPayment(0, pageSize, 0); // 0은 코인 충전
	getCoinPayment(0, pageSize, 1); // 1은 회차 구매
	getCoinPayment(0, pageSize, 2); // 2는 코인 환불/환전
	// getCoinPaymentWating(0, pageSize);
	getCoinPayment(0, pageSize, 3); // 3은 작가 후원
	
	
	function getCoinPayment(page = 0, size = 5, type = 0) {
		if (type === 0 || type === 1 || type === 3) {
			axios.get(`/user/coinpayment/read?userId=${userId}&page=${page}&size=${size}&type=${type}`).then(response => {
				console.log(type, response);
				const data = response.data.content;
				const pageData = response.data;
				
				// 코인 충전인 데이터만 필터
				if (type === 0) {
					const chargeData = data.filter(item => item.type === 0);
					readCoinChargeList(chargeData);	
					renderPagination(response.data, 0);		
				}
				
				// 회차구매인 데이터만 필터
				if (type === 1) {
					const buyData = data.filter(item => item.type === 1);
					readBuyList(buyData);
					renderPagination(response.data, 1);		
				}
				
				// 후원인 데이터만 필터
				if (type === 3) {
					const donationData = data.filter(item => item.type === 3);
					readDonationList(donationData);
					renderPagination(response.data, 3);			
				}
				
			}).catch(error => {
				console.error(error);
			});
		} else {
			console.log('else');
			axios.get(`/user/coinpaymentwaiting/read?userId=${userId}&page=${page}&size=${size}&type=${type}`).then(response => {
				const data = response.data.content
				console.log('else', response.data);
				
				readCoinReList(data);
				renderPagination(response.data, 2);
			}).catch(error => {
				console.error(error);
			})
		}
	}
	
	// 충전 리스트
	function readCoinChargeList(coinChargeList) {
		console.log(coinChargeList);
		const tbodyCoinCharge = document.querySelector('tbody#coinChargeTBody');
		
		if (coinChargeList.length === 0) {
			tbodyCoinCharge.innerHTML =
			`
				<tr class="row">
					<td colspan="4">충전 내역이 없습니다.</td>
				</tr>
			`
			return;
		}
		
		let html = '';
		coinChargeList.forEach(item => {
			let formatCreatedTime = formatDateTime(item.createdTime);
			html += 
			`
				<tr class="row">
					<td class="col-1 ts-6" style="font-size: 12px;">코인 충전</td>
					<td class="col">${item.coin}</td>
					<td class="col text-danger">${item.cash}</td>
					<td class="col">${formatCreatedTime}</td>
				</tr>
			`;
		});
		tbodyCoinCharge.innerHTML = html;
	}
	
	
	// 회차구매 리스트
	function readBuyList(buyList) {
		console.log(buyList);
		const tbodyBuy = document.querySelector('tbody#buyTBody');
		
		if (buyList.length === 0) {
			tbodyBuy.innerHTML =
			`
				<tr class="row">
					<td colspan="5">사용 내역이 없습니다.</td>
				</tr>
			`;
			return;
		}
		
		let html = '';
		buyList.forEach(item => {
			let formatCreatedTime = formatDateTime(item.createdTime);
			html += 
			`
				<tr class="row">
					<td class="col-1 ts-6" style="font-size: 12px;">작품 구매</td>
					<td class="col-5">${item.novelTitle}</td>
					<td class="col-1">${item.episodeNum}</td>
					<td class="col-2 text-danger">${Math.abs(item.coin)}</td>
					<td class="col-3">${formatCreatedTime}</td>
				</tr>
			`;
		});
		tbodyBuy.innerHTML = html;
	}
	
	
	// 바꿔야함 신청/완료가 있는 테이블이 따로 있음
	// 환전/환불 리스트
	function readCoinReList(coinReList) {
		console.log(coinReList);
		const tbodyCoinRe = document.querySelector('tbody#coinReTBody');
		
		if (coinReList.length === 0) {
			tbodyCoinRe.innerHTML =
			`
				<tr class="row">
					<td colspan="5">환불/환전 내역이 없습니다.</td>
				</tr>
			`;
			return;
		}

		let html = '';
		coinReList.forEach(item => {
			const con = (item.con === 0) ? '대기' : '완료';
			console.log(con);
			let formatCreatedTime = formatDateTime(item.createdTime);
			html += 
			`
				<tr class="row">
					<td class="col-1 ts-6" style="font-size: 12px;">환불/환전</td>
					<td class="col-3 text-danger">${Math.abs(item.coin)}</td>
					<td class="col-4 text-success">${item.cash}</td>
					<td class="col-3">${formatCreatedTime}</td> 
					<td id="con" class="col-1">${con}</td>
				</tr>
			`;
		});
		tbodyCoinRe.innerHTML = html;
		const tdCons = document.querySelectorAll('td#con');
		tdCons.forEach(td => {
			(td.textContent === '대기') ? td.classList.add('text-danger') : td.classList.add('text-success');
		});
	}
	
	// 후원 리스트
	function readDonationList(donationList) {
		console.log(donationList);
		const tbodyDonation = document.querySelector('tbody#donationTBody');
		
		if (donationList.length === 0) {
			tbodyDonation.innerHTML =
			`
				<tr class="row">
					<td colspan="5">사용 내역이 없습니다.</td>
				</tr>
			`;
			return;
		}
		
		let html = '';
		donationList.forEach(item => {
			let formatCreatedTime = formatDateTime(item.createdTime);
			html += 
			`
				<tr class="row">
					<td class="col-1 ts-6" style="font-size: 12px;">작품 후원</td>
					<td class="col-6">${item.novelTitle}</td>
					<td class="col-2 text-danger">${Math.abs(item.coin)}</td>
					<td class="col-3">${formatCreatedTime}</td>
				</tr>
			`;
		});
		tbodyDonation.innerHTML = html;
	}
	
	/**
	 * 페이징 섹션을 출력하는 함수
	 * @param {*} pageData 받아온 Page 타입의 정보
	 */
	function renderPagination(pageData, type) {
		let typeName;
		if (type === 0) {
			typeName = 'coinCharge';
		} else if (type === 1) {
			typeName = 'buy';
		} else if (type === 2) {
			typeName = 'coinRe';
		} else if (type === 3) {
			typeName = 'donation';
		}
	    const pagination = document.querySelector(`ul#${typeName}Pagination`);
	    console.log('pageData', pageData);
	    pagination.innerHTML = '';

	    // startPage: 1, 6, 11, ... 고정
	    let startPage = Math.floor(currentPage / blockSize) * blockSize;

	    // endPage: startPage나 마지막페이지 중 적은 것으로 설정
	    let endPage = Math.min(startPage + blockSize, pageData.totalPages);
		
		const totalPages = pageData.totalPages;

	    /**
	     * '이전' 버튼 생성 현재 페이지 -1 페이지로 이동 
	     * 현재 페이지가 0이면 비활성, 전체 페이지가 0이면 출력안됨.
	     */
	    const prevButton = createPaginationButton('이전', currentPage - 1, type);
	        if (currentPage === 0) {
	            prevButton.classList.add('disabled');
	        }
	        if (pageData.totalPages === 0) {
	            prevButton.classList.add('d-none');
	        }
	        pagination.appendChild(prevButton);
		

		// 페이지 번호를 마지막 페이지번호 까지 생성
	    for (let i = startPage; i < endPage; i++) {
	        const pageButton = createPaginationButton(i + 1, i, type);
	        if (i === currentPage) {
				// 사용자가 해당 페이지를 보고 있으면 active 효과 추가
	            pageButton.classList.add('active');
	        }
	        pagination.appendChild(pageButton);
	    }

	    /**
	     * '다음' 버튼 생성 현재 페이지 +1 페이지로 이동 
	     * 현재 페이지가 마지막 페이지면 비활성, 전체 페이지가 0이면 출력안됨.
	     */
	    const nextButton = createPaginationButton('다음', currentPage + 1, type);
	        if (currentPage === pageData.totalPages -1) {
	            nextButton.classList.add('disabled');
	        }
	        if (pageData.totalPages === 0) {
	            nextButton.classList.add('d-none');
	        }
	        pagination.appendChild(nextButton);
			
		
		const inputMores = document.querySelectorAll('input#inputMore');
			inputMores.forEach(input => {
				input.addEventListener('change', (event) => {
					let targetValue = event.target.value;
					if (targetValue > totalPages) {
						input.value = totalPages;
					} else if (targetValue <= 0) {
						input.value = 1;
					}
				});
			});
	}
	
	/**
	 * 페이징 버튼(이전, 숫자, 다음)에 해당 페이지로 이동하는 기능을 넣는 함수
	 * 해당 버튼 클릭 시 해당 페이지에 있는 comments를 요청하게 됨
	 * 
	 * @param {*} text 버튼에 출력될 문자
	 * @param {*} page 버튼 클릭 시 @function getComments(@param page)를 호출하여 해당 페이지에 있는 comments 요청
	 * @returns 
	 */
	function createPaginationButton(text, page, type) {
	    const li = document.createElement('li');
	    li.classList.add('page-item');

	    const button = document.createElement('button');
	    button.classList.add('page-link');
	    button.innerText = text;
	    button.addEventListener('click', () => {
	        currentPage = page;
	        getCoinPayment(currentPage, 5, type);
	    });

	    li.appendChild(button);
	    return li;
	}
	
	/////////////////////
	const btnReApp = document.querySelector('button#btnReApp');
	const btnReAppSubmit = document.querySelector('button#btnReAppSubmit');
	btnReApp.addEventListener('click', () => {
		const nowCoin = document.querySelector('strong#nowCoin').textContent;
		const reAppPossibleCoin = document.querySelector('strong#reAppPossibleCoin');
		reAppPossibleCoin.textContent = nowCoin;
		
		const inputReAppCoin = document.querySelector('input#reAppCoin');
		inputReAppCoin.addEventListener('change', (event) => {
			let inputCoin = parseInt(event.target.value, 10);
			const maxCoin = parseInt(reAppPossibleCoin.textContent, 10);
			
			if (inputCoin < 100) {
				inputCoin = 100;
			} else if (inputCoin > maxCoin) {
				inputCoin = maxCoin;
			}
			
			const inputCash = document.querySelector('input#resultCash');
			inputReAppCoin.value = inputCoin;
			inputCash.value = inputCoin * 90;
			btnReAppSubmit.classList.remove('disabled');
		});
	});
	
	btnReAppSubmit.addEventListener('click', () => {
		const userId = document.querySelector('span#userId').textContent;
		let coin = (-document.querySelector('input#reAppCoin').value);
		let cash = document.querySelector('input#resultCash').value;
		
		const type = 2;
		
		let data = {userId, coin, cash, type};
		console.log(data);
		let result = confirm('환불/환전 신청하시겠습니까?');
		
		if (result) {
			disableClicks();
			axios.post('/user/coinpayment/reApp', data).then(response => {
				alert('환불/환전 신청되었습니다.');
				window.location.href = "/";
			}).catch(error => {
				console.error(error);
			});			
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
	
	function formatDateTime(dateTimeString) {
	    const date = new Date(dateTimeString);
	    return date.toLocaleString('ko-KR', { 
	        year: 'numeric', 
	        month: '2-digit', 
	        day: '2-digit', 
	        hour: '2-digit', 
	        minute: '2-digit', 
	    });
	}
	
	// ============ 모달 ============
	const navbar = document.querySelector('.navbar');
	const modalEl = document.getElementById('reAppModal');
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


});