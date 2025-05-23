document.addEventListener('DOMContentLoaded', () => {
	console.log('mileageshop.js');
	
	const ticketExchangeModal = new bootstrap.Modal(document.querySelector("div#ticketExchangeModal")); // 모달 객체 생성
	
	const btnSelectGlobalTicket = document.querySelector('a#selectGlobalTicket');
	btnSelectGlobalTicket.addEventListener('click', (event) => {

		if (!document.querySelector('span#userId')) {
			alert('로그인이 필요합니다.');
			return;
		}
		
		let nowMileage = document.querySelector('strong#nowMileage').textContent;
		console.log(nowMileage);
		
		if (nowMileage < 100) {
			alert('마일리지가 부족합니다.');
			return;
		}
		
		let result = confirm('100 마일리지를 자유 이용권으로 교환하시겠습니까?');
		if (result) {
			disableClicks();
			const userId = document.querySelector("span#userId").textContent;
			const mileage = Number(-100);
			const descrip = '이용권 교환';
			const grade = 0;

			const data = {userId, mileage, descrip, grade}
			axios.post('/user/exchange/ticket', data).then(response => {
				alert('이용권을 교환하였습니다.');
				window.location.reload();
			}).catch(error => {
				console.log(error);
			});
		}
	});
	
	const btnSelecNovelTicket = document.querySelector('a#selecNovelTicket');
	btnSelecNovelTicket.addEventListener('click', () => {
		if (!document.querySelector('span#userId')) {
			alert('로그인이 필요합니다.');
			return;
		}
		ticketExchangeModal.show();
	});
	
	let inputSearch = document.querySelector('input#inputSearch');
	const btnSearchTicket = document.querySelector('button#btnSearchTicket');
	btnSearchTicket.addEventListener('click', search);
	inputSearch.addEventListener('keydown', (event) => {
		if (event.key === 'Enter') {
			event.preventDefault();
			search();
		}
	})
	
	// 검색 실행 함수
	function search() {
	    let keyword = inputSearch.value.trim();
	    if (keyword === "" || keyword.trim() === "") {
	        alert('검색어를 입력해주세요.');
			return;
	    }
		
		axios.get(`/novel/paidnobel/get?keyword=${keyword}`).then(response => {
			let result = response.data;
			console.log(result);
			
			const divResult = document.querySelector('div#divResult');
			divResult.innerHTML = '';
			
			if (result.length === 0) {
				const html = 
				`	<div class="text-center">
						<span class="text-danger">검색 결과가 없습니다.</span>
					</div>
				`
				divResult.innerHTML = html;
				return;
			}
			let html = '';
			for (const novel of result) {
				html +=  
				`
					<div class="row mt-2 text-center">
						<input class="col-1 p-0 ps-2 selectedNovel" type="radio" name="selectedNovelId" value="${novel.id}" data-title="${novel.title}" data-writer="${novel.writer}" />
						<div class="col-7">
							${novel.title}
						</div>
						<div class="col-4">
							${novel.writer}
						</div>
					</div>
				`;
			}
			divResult.innerHTML = html;

			const btnExchange = document.querySelector('button#btnExchange');
			const selectedNovels = document.querySelectorAll('.selectedNovel');
			selectedNovels.forEach(selected => {
				selected.addEventListener('click', (event) => {
					console.log(event.target.dataset);
					const selectedNovelId = document.querySelector('span#selectedNovelId');
					const selectedNovelTitle = document.querySelector('span#selectedNovelTitle');
					const selectedNovelWriter = document.querySelector('span#selectedNovelWriter');
					
					selectedNovelId.textContent = event.target.value;
					selectedNovelTitle.textContent = event.target.dataset.title;
					selectedNovelWriter.textContent = event.target.dataset.writer;
					
					if (btnExchange.classList.contains('disabled')) {
						btnExchange.classList.remove('disabled');
					}
				});
			});
		}).catch(error => {
			console.error(error);
		});
	}
	
	const btnExchange = document.querySelector('button#btnExchange');
	btnExchange.addEventListener('click', () => {
		let nowMileage = document.querySelector('strong#nowMileage').textContent;
		if (nowMileage < 50) {
			alert('마일리지가 부족합니다.');
			return;
		}
		
		const userId = document.querySelector("span#userId").textContent;
		const novelId = document.querySelector('span#selectedNovelId').textContent;
		const mileage = Number(-50);
		const descrip = '이용권 교환';
		const grade = 1;
		
		let result = confirm('이용권을 교환하시겠습니까?');
		if (result) {
			disableClicks();
			const data = {userId, novelId, mileage, descrip, grade};
			axios.post('/user/exchange/ticket', data).then(response => {
				alert('이용권을 교환하였습니다.');
				window.location.reload();
			}).catch(error => {
				console.error(error)
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
	
	// ============ 모달 ============
	const navbar = document.querySelector('.navbar');
	const modalEl = document.getElementById('ticketExchangeModal');
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