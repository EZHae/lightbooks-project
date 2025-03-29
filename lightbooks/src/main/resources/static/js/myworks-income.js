document.addEventListener('DOMContentLoaded', () => {
	console.log('myworks-income.js');
	const userId = document.querySelector('span#userId').textContent;
	let currentPage = 0; // 현재 페이지
	const pageSize = 4; // 페이지 크기 설정
	const blockSize = 5;
	
	getNovelWithIncome();
	
	function getNovelWithIncome(page = 0, size = 4) {
		axios.get(`/myworks/income/read?userId=${userId}&page=${page}&size=${size}`).then(response => {
			console.log(response);
			
			readNovelWithIncome(response.data.content);
			renderPagination(response.data);
		}).catch(error => {
			console.error(error);
		})
	}
	
	
	function readNovelWithIncome(contentData) {
		console.log(contentData);
		const contentSection = document.querySelector('div#contentSection');
		
		if (contentData.length === 0) {
			contentSection.outerHTML =
			`
				<div class="d-flex justify-content-center">
					<span class="fs-5 fw-bold">수익이 발생한 작품이 없습니다.</span>
				</div>
			`;
			return;
		}
		
		let html = '';
		contentData.forEach(item => {
			const novelId = item[0];
			const novelTitle = item[1];
			const novelGrade = (item[2] == 0) ? '무료' : '유료';
			const novelCoverSrc = item[3];
			const type4Income = item[4];
			const type5Income = item[5];
			html += 
			`
				<div class="col">
					<div class="card card-body shadow-sm card-item mx-2 mt-2 mb-4">
						<div class="row">
							<div class="col-4">
								<img class="img-fluid w-100 h-100" style="object-fit: cover;" src="${novelCoverSrc}">
							</div>
							<div class="col-8">
								<div>
									<span>${novelGrade}</span>
								</div>
								<div class="mb-2 border-bottom border-black">
									<span class="fs-4">${novelTitle}</span>
								</div>
								<div class="border-bottom border-dark-subtle">
									<span>누적 판매금액</span><br>
									<span>${type4Income} 코인</span><a href="/myworks/income/details?id=${userId}&novelId=${novelId}" class="ms-2 btn btn-sm btn-link p-1">자세히보기</a>								
								</div>
								<div>
									<span>누적 후원금액</span><br>
									<span>${type5Income} 코인</span><a href="/myworks/income/details?id=${userId}&novelId=${novelId}" class="ms-2 btn btn-sm btn-link p-1">자세히보기</a>
								</div>
							</div>
						</div>
					</div>
				</div>
			`;
		});
		contentSection.innerHTML = html;
	}
	
	
	
	
	
	
	
	/**
	 * 페이징 섹션을 출력하는 함수
	 * @param {*} pageData 받아온 Page 타입의 정보
	 */
	function renderPagination(pageData) {
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
	    const prevButton = createPaginationButton('이전', currentPage - 1);
	        if (currentPage === 0) {
	            prevButton.classList.add('disabled');
	        }
	        if (pageData.totalPages === 0) {
	            prevButton.classList.add('d-none');
	        }
	        pagination.appendChild(prevButton);
		

		// 페이지 번호를 마지막 페이지번호 까지 생성
	    for (let i = startPage; i < endPage; i++) {
	        const pageButton = createPaginationButton(i + 1, i);
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
	    const nextButton = createPaginationButton('다음', currentPage + 1);
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
	function createPaginationButton(text, page) {
	    const li = document.createElement('li');
	    li.classList.add('page-item');

	    const button = document.createElement('button');
	    button.classList.add('page-link');
	    button.innerText = text;
	    button.addEventListener('click', () => {
	        currentPage = page;
	        getNovelWithIncome(currentPage, pageSize);
	    });

	    li.appendChild(button);
	    return li;
	}
});