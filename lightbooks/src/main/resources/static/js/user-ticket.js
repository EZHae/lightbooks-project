document.addEventListener('DOMContentLoaded', () => {
    console.log('user-ticket.js');
	
	const userId = document.querySelector('span#userId').textContent;

	let currentPage = 0; // 현재 페이지
	const pageSize = 5; // 페이지 크기 설정
	const blockSize = 5;
	
	getTicket(0, pageSize); // 0은 마일리지 적립
	getTicketPayment(0, pageSize); // 1은 마일리지 사용
	
	function getTicket(page = 0, size = 5) {
		axios.get(`/user/ticket/read?userId=${userId}&page=${page}&size=${size}`).then(response => {
			const data = response.data;
			
			readTicketList(data.content);
			renderPagination(data, 0);
		}).catch(error => {
			console.error(error);
		});
	}
	
	function getTicketPayment(page = 0, size = 5) {
		axios.get(`/user/ticketpayment/read?userId=${userId}&page=${page}&size=${size}`).then(response => {
			const data = response.data;
			
			readTicketPaymentList(data.content);
			renderPagination(data, 1);
		}).catch(error => {
			console.error(error);
		});
	}
	
	// 보유 리스트
	function readTicketList(TicketHoldList) {
		console.log(TicketHoldList);
		const tbodyTicketHoldList = document.querySelector('tbody#ticketHoldTBody');
		
		if (TicketHoldList.length === 0) {
			tbodyTicketHoldList.innerHTML =
			`
				<tr class="row">
					<td colspan="3">보유중인 이용권이 없습니다.</td>
				</tr>
			`
			return;
		}
		
		let html = '';
		TicketHoldList.forEach(item => {
			html += 
			`
				<tr class="row">
					<td class="col-2 ${item.grade === '전체 이용권' ? 'fw-bold text-primary' : ''}">${item.grade}</td>
					<td class="col-7">${item.novelTitle}</td>
					<td class="col-3">${item.createdTime}</td>
				</tr>
			`;
		});
		tbodyTicketHoldList.innerHTML = html;
	}

	// 사용 리스트
	function readTicketPaymentList(ticketUseList) {
		console.log(ticketUseList);
		const tbodyTicketUse = document.querySelector('tbody#ticketUseTBody');
		
		if (ticketUseList.length === 0) {
			tbodyTicketUse.innerHTML =
			`
				<tr class="row">
					<td colspan="3">사용 내역이 없습니다.</td>
				</tr>
			`
			return;
		}
		
		let html = '';
		ticketUseList.forEach(item => {
			html += 
			`
				<tr class="row">
					<td class="col-7">${item.novelTitle}</td>
					<td class="col-2">${item.episodeNum}</td>
					<td class="col-3">${item.createdTime}</td>
				</tr>
			`;
		});
		tbodyTicketUse.innerHTML = html;
	}
	
	/**
	 * 페이징 섹션을 출력하는 함수
	 * @param {*} pageData 받아온 Page 타입의 정보
	 */
	function renderPagination(pageData, type) {
		let typeName;
		if (type === 0) {
			typeName = 'ticketHold';
		} else {
			typeName = 'ticketUse';
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
			if (type === 0) {
				getTicket(currentPage, 5);	
			} else {
				getTicketPayment(currentPage, 5);
			}
	        
	    });

	    li.appendChild(button);
	    return li;
	}
});