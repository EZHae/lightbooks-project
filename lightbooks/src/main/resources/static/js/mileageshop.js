document.addEventListener('DOMContentLoaded', () => {
	console.log('mileageshop.js');
	
	const btnSelectGlobalTicket = document.querySelector('a#selectGlobalTicket');
	btnSelectGlobalTicket.addEventListener('click', (event) => {
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
});