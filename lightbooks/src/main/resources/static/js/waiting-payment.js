document.addEventListener('DOMContentLoaded', () => {
	console.log('waiting-payment.js');
	
	let currentPage = 0; // 현재 페이지
	const pageSize = 20; // 페이지 크기 설정
	const blockSize = 5;
	let con;
	
	getCoinWatingPayment()
	function getCoinWatingPayment(page = 0, size = 20, con = 2) {
		axios.get(`/admin/waitingpayment/read/con?page=${page}&size=${size}&con=${con}`).then(response => {
			console.log(response);
			renderCoinWatingPayment(response.data.content);
			renderPagination(response.data, con);
		}).catch(error => {
			console.log(error);
		});
	}
	
	
	function renderCoinWatingPayment(contentData) {
		console.log(contentData);
		const contentSection = document.querySelector('Tbody#contentResult');

		if (contentData.length === 0) {
			contentSection.innerHTML =
			`
				<tr>
					<td colspan="9" class="text-center">신청내역이 없습니다.</tr>
				</tr>
			`;
			return;
		}
		
		let html = '';
		contentData.forEach(item => {
			let type = item.type;
			let createdTime = formatDateTime(item.createdTime);
			let con = item.con;
			html +=
			`
				<tr>
					<td>${item.id}</td>
					<td>${item.userLoginId}</td>
					<td>${item.userUsername}</td>
					<td>${type == 0 ? '코인 충전' : type == 1 ? '코인 사용' : type == 2 ? '코인 환불/환전' : type == 3 ? '작품후원' : '알 수 없음'}</td>
					<td>${item.coin}</td>
					<td>${item.cash}</td>
					<td>${createdTime}</td>
					<td>${con == 0 ? '대기 중' : '완료'}</td>
					<td class="text-center" id="con_result_${item.id}">
						
					</td>
				</tr>
			`;
		});
		contentSection.innerHTML = html;
		contentData.forEach(item => {
			if (item.con == 0) {
				document.querySelector(`td#con_result_${item.id}`).innerHTML = `<button id="btnCheck" data-id=${item.id} class="btn btn-primary btn-sm">확인</button>`;
			} else if (item.con == 1) {
				document.querySelector(`td#con_result_${item.id}`).innerHTML = `<span class="text-success">완료</span>`;
			} else {
				document.querySelector(`td#con_result_${item.id}`).innerHTML = `<span>-</span>`;
			}			
		});
		const btnChecks = document.querySelectorAll('button#btnCheck');
		console.log(btnChecks);
		btnChecks.forEach(btn => {
			btn.addEventListener('click', (event) => {
				console.log(event.target.dataset.id);

				const id = event.target.dataset.id;
				let result = confirm('해당 신청을 완료 처리 하겠습니까?');
				if (result) {
					disableClicks();
					axios.post(`/admin/waitingpayment/check?id=${id}`).then(response => {
						console.log(response);
						alert('처리되었습니다.');
						getCoinWatingPayment(currentPage, 20);
					}).catch(error => {
						console.error(error);
					})
				}
			});
		});
	}
	

	const menuItems = document.querySelectorAll('a.nav-menu-item');
	menuItems.forEach(item => {
		item.addEventListener('click', (event) => {
			menuItems.forEach(tab => tab.classList.remove('active')); // 모든 탭에서 'active' 클래스 제거
			menuItems.forEach(tab => tab.classList.remove('bg-light'));
			item.classList.add('active'); // 선택한 탭만 'active' 추가
			item.classList.add('bg-light');			

			con = event.target.dataset.con;
			
			if (con == 2) {
				currentPage = 0;
				getCoinWatingPayment(currentPage, pageSize, 2);
			} else if (con == 1) {
				currentPage = 0;
				getCoinWatingPayment(currentPage, pageSize, 1);
			} else if (con == 0) {
				currentPage = 0;
				getCoinWatingPayment(currentPage, pageSize, 0);
			}
		});
	});
	
	
	
	
	
	/**
	 * 페이징 섹션을 출력하는 함수
	 * @param {*} pageData 받아온 Page 타입의 정보
	 */
	function renderPagination(pageData, con) {
	    const pagination = document.querySelector('ul#pagination');
	    console.log('pageData', pageData);
	    pagination.innerHTML = '';

	    // startPage: 1, 6, 11, ... 고정
	    let startPage = Math.floor(currentPage / blockSize) * blockSize;

	    // endPage: startPage나 마지막페이지 중 적은 것으로 설정
	    let endPage = Math.min(startPage + blockSize, pageData.totalPages);
		console.log(startPage)
		console.log(startPage + blockSize)
		console.log(pageData.totalPages)
		console.log(endPage);
		
		const totalPages = pageData.totalPages;

	    /**
	     * '이전' 버튼 생성 현재 페이지 -1 페이지로 이동 
	     * 현재 페이지가 0이면 비활성, 전체 페이지가 0이면 출력안됨.
	     */
	    const prevButton = createPaginationButton('이전', currentPage - 1, con);
	        if (currentPage === 0) {
	            prevButton.classList.add('disabled');
	        }
	        if (pageData.totalPages === 0) {
	            prevButton.classList.add('d-none');
	        }
	        pagination.appendChild(prevButton);
		

		// 페이지 번호를 마지막 페이지번호 까지 생성
	    for (let i = startPage; i < endPage; i++) {
	        const pageButton = createPaginationButton(i + 1, i, con);
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
	    const nextButton = createPaginationButton('다음', currentPage + 1, con);
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
	function createPaginationButton(text, page, con) {
	    const li = document.createElement('li');
	    li.classList.add('page-item');

	    const button = document.createElement('button');
	    button.classList.add('page-link');
	    button.innerText = text;
	    button.addEventListener('click', () => {
	        currentPage = page;
	        getCoinWatingPayment(currentPage, pageSize, con);
	    });

	    li.appendChild(button);
	    return li;
	}
	

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
});