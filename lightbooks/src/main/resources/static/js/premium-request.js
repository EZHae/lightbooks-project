document.addEventListener('DOMContentLoaded', () => {
	console.log('premuim-request.js');

	let currentPage = 0; // 현재 페이지
	const pageSize = 20; // 한 페이지당 개수
	const blockSize = 5; // 블록당 페이지 넘어갈 개수
	let status;
	
	getPremiumRequest();
	
	// 코인 전체 목록
	function getPremiumRequest(page = 0, size = 20, status = 3){
		axios.get(`/admin/premiumrequest/read/status?page=${page}&size=${size}&status=${status}`)
			.then(response => {
				
				console.log("여기 나오나",response.data);
				renderPremiumWating(response.data.content);
				renderPagination(response.data, status);
			}).catch(error => {
				console.log(error);
			});
	}
	
	
	function renderPremiumWating(contentData) {
		const content = document.getElementById('premiumContent');
		
		if(contentData.length === 0){
			content.innerHTML = `
				
				<tr>
					<td colspan="8" class="text-center">신청 내역이 없습니다.</td>
				</tr>
			`;
			return;
		}
		
		let html = '';
		
		contentData.forEach(item => {
			let status = item.status;
			let createdTime = formatDateTime(item.createdTime);
			let type = item.type;
			let title = item.novelTitle;
			
			html += `
			<tr>
				<td>${item.id}</td>
				<td>${item.userLoginId}</td>
				<td>${item.userUsername}</td>
				<td>${title ? title : '제목 없음'}</td>
				<td>${type == 0 ? '무료 신청' : '유료 신청'}</td>
				<td>${createdTime}</td>
				<td>${status == 0 ? '대기 중' : (status == 1 ? '완료' : '거절')}</td>
				<td class="text-center" id="status-result-${item.id}">	
								
		 		</td>
			</tr>
			`;		
		});
		content.innerHTML = html;
		contentData.forEach(item => {
			if(item.status == 0){
				document.getElementById(`status-result-${item.id}`).innerHTML = `<button data-id=${item.id} id="btnCheck" class="btn btn-primary btn-sm">확인</button> <button data-id=${item.id} id="btnCancleCheck" class="btn btn-danger btn-sm">거절</button>`
			} else if (item.status == 1) {
				document.getElementById(`status-result-${item.id}`).innerHTML = `<span class="text-success">완료</span>`
			} else if (item.status == 2) {
				document.getElementById(`status-result-${item.id}`).innerHTML = `<span class="text-danger">거절</span>`
			}
			
		});
		
		addButtonEvents();
	};
	
	// 승인 거절 버튼 클릭 이벤트
	function addButtonEvents() {
		const btnCancleChecks = document.querySelectorAll('button#btnCancleCheck')
		const btnChecks = document.querySelectorAll('button#btnCheck')
		console.log(btnCancleChecks);
		console.log(btnChecks);
		
		btnCancleChecks.forEach(btn => {
			btn.addEventListener('click', (event)=>{
				console.log(event.target.dataset.id);
				
				const id = event.target.dataset.id;
				let result = confirm("해당 신청을 취소 처리 하시겠습니까?");
				
				if(result) {
					disableClicks();
					axios.post(`/admin/premiumrequest/cancle?id=${id}`).then(response => {
						console.log(response);
						alert('취소 처리 되었습니다😊');
						enableClicks();
						getPremiumRequest(currentPage, 20);
					}).catch(error => {
						console.error(error);
						enableClicks();
					})
				}
			});
		})
		btnChecks.forEach(btn => {
			btn.addEventListener('click', (event)=>{
				console.log(event.target.dataset.id);
				
				const id = event.target.dataset.id;
				let result = confirm("해당 신청을 승인 처리 하시겠습니까?");
				
				if(result) {
					disableClicks();
					axios.post(`/admin/premiumrequest/check?id=${id}`).then(response => {
						console.log(response);
						alert('승인 처리 되었습니다😊');
						enableClicks();
						getPremiumRequest(currentPage, 20);
					}).catch(error => {
						console.error(error);
						enableClicks();
					})
				}
			});
		})
	}
	
	const menuItems = document.querySelectorAll('a.nav-menu-item');
	menuItems.forEach(item => {
		item.addEventListener('click', (event) => {
			console.log("클릭했나요");
			menuItems.forEach(tab => tab.classList.remove('active'));
			menuItems.forEach(tab => tab.classList.remove('bg-light'));
			item.classList.add('active');
			item.classList.add('bg-light');
			
			status = event.target.dataset.status;
			
			if(status == 3) {
				currentPage = 0;
				getPremiumRequest(currentPage, pageSize, 3); // 전체
			} else if (status == 2) {
				currentPage = 0;
				getPremiumRequest(currentPage, pageSize, 2); 
			} else if (status == 1) {
				currentPage = 0;
				getPremiumRequest(currentPage, pageSize, 1);
			} else if (status == 0) {
				currentPage = 0;
				getPremiumRequest(currentPage, pageSize, 0);
			}
		});
	})
	
	function renderPagination(pageData, status) {

		const pagination = document.querySelector('ul#pagination');
		console.log('pageData', pageData);
		pagination.innerHTML = '';
		
		let startPage = Math.floor(currentPage / blockSize) * blockSize;
		let endPage = Math.min(startPage + blockSize, pageData.totalPages);
		

		const totalPages = pageData.totalPages;
		

		const prevButton = createPaginationButton('이전', currentPage - 1, status);
			if (currentPage === 0) {
				prevButton.classList.add('disabled');
			}
			if (pageData.totalPages === 0) {
				prevButton.classList.add('d-none');
			}
			pagination.appendChild(prevButton);

			// 페이지 번호를 마지막 페이지 번호까지 생성
			for (let i = startPage; i < endPage; i++) {
				const pageButton = createPaginationButton(i + 1, i, status);
				if (i == currentPage) {
					// 사용자가 해당 페이지를 보고 있으면 active 효과 추가
					pageButton.classList.add('active');
				}
				pagination.appendChild(pageButton);
			}

			// 다음 버튼 생성 - 현재 페이지 + 1 페이지로 이동
			if (pageData.totalElements > 0) {
				const nextButton = createPaginationButton('다음', currentPage + 1, status);
				if (currentPage === pageData.totalPages - 1) {
					nextButton.classList.add('disabled');
				}
				pagination.appendChild(nextButton);
			}
	
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

	// 페이징 버튼(이전, 숫자, 다음)에 해당 페이질 이동하는 기능
	function createPaginationButton(text, page, status) {
		const li = document.createElement('li');
		li.classList.add('page-item');
		
		const button = document.createElement('button');
		button.classList.add('page-link');
		button.innerHTML = text;
		button.addEventListener('click', () => {
			currentPage = page;
			getPremiumRequest(currentPage, pageSize, status);
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
	
	function enableClicks() {
		const blocker = document.getElementById("blocker");
		if (blocker) {
			blocker.remove();
		}
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