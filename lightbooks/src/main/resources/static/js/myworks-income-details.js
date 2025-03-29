document.addEventListener('DOMContentLoaded', () => {
	console.log('myworks-income-details.js');
	const userId = document.querySelector('span#userId').textContent;
	const novelId = document.querySelector('span#novelId').textContent;
	let currentPage = 0; // 현재 페이지
	const pageSize = 10; // 페이지 크기 설정
	const blockSize = 5;
	
	const menuItems = document.querySelectorAll('a.nav-menu-item');
	menuItems.forEach(item => {
		item.addEventListener('click', (event) => {
			menuItems.forEach(tab => tab.classList.remove('active')); // 모든 탭에서 'active' 클래스 제거
			menuItems.forEach(tab => tab.classList.remove('bg-light'));
			item.classList.add('active'); // 선택한 탭만 'active' 추가
			item.classList.add('bg-light');			

			let menuName = event.target.dataset.menuname;
			
			if (menuName == 'sale') {
				console.log('saleList');
				document.querySelector('th#changeGrade').textContent = '구매자';
				document.querySelector('span#changeNotice').textContent = '판매 수익 코인은 회차 구매 코인의 60% 입니다. (소수점 버림)';
				document.querySelector('th#changeTime').textContent = '구매 일시';
				currentPage = 0;
				getIncome(currentPage, pageSize, 4);
			} else if (menuName == 'donation') {
				console.log('donationList');
				document.querySelector('th#changeGrade').textContent = '후원자';
				document.querySelector('span#changeNotice').textContent = '후원 수익 코인은 후원 코인의 90% 입니다. (소수점 버림)';
				document.querySelector('th#changeTime').textContent = '후원 일시';
				currentPage = 0;
				getIncome(currentPage, pageSize, 5);
			}
		});
	});
	
	getIncome();
	
	function getIncome(page = 0, size = 10, type=4) {
		axios.get(`/myworks/income/details/read?novelId=${novelId}&page=${page}&size=${size}&type=${type}`).then(response => {
			console.log(response);
			
			readIncome(response.data.content);
			renderPagination(response.data, type);
		}).catch(error => {
			console.error(error);
		});
	}
	
	function readIncome(contentData) {
		console.log(contentData);
		const contentSection = document.querySelector('tbody#contentSection');
		
		if (contentData.length === 0) {
			contentSection.innerHTML =
			`
				<tr class="row">
					<td class="col">수익발생없음</td>
				</tr>
			`;
			return;
		}
		
		let html = '';
		contentData.forEach(item => {
			let nickname = (item.nickname == null) ? '(알 수 없음)' : item.nickname;
			let episodeNum = (item.episodeNum == null) ? '-' : item.episodeNum;
			let createdTime = formatDateTime(item.createdTime);
			html += 
			`
				<tr class="row">
					<td class="col">${nickname}</td>
					<td class="col-2">${episodeNum}</td>
					<td class="col-2 text-end">${item.coin} 코인</td>
					<td class="col-4 text-end">${createdTime}</td>
				</tr>
			`;
		});
		contentSection.innerHTML = html;
	}
	
	/**
	 * 페이징 섹션을 출력하는 함수
	 * @param {*} pageData 받아온 Page 타입의 정보
	 */
	function renderPagination(pageData, type) {
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
	        getIncome(currentPage, pageSize, type);
	    });

	    li.appendChild(button);
	    return li;
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