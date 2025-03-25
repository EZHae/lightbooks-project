/**
 * bookmark.html
 */
console.log("bookmark.js 로드됨");


document.addEventListener('DOMContentLoaded', () => {
	console.log("DOMContentLoaded event fired"); // 추가된 코드
	
	let currentPageNo = 0;
	let isLoading = false;
	let isLastPage = false;
	let type = 'liked'; // 초기 타입 설정
	const userId = document.querySelector('input#userId').value;
	const itemList = document.getElementById('itemList');
	
	if (!userId) {
	        console.error("userId를 찾을 수 없습니다.");
	        return;
	    }

	getAllItems(currentPageNo, true);

	window.addEventListener('scroll', () => {
		if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 200) {
			getAllItems(currentPageNo + 1);
		}
	});

	async function getAllItems(pageNo = 0, reset = false) {
		alert("test");
		console.log("getAllItems called"); // 추가된 코드
		
		if (isLoading || isLastPage) return;
		isLoading = true;

		const sortBy = 'novel.likeCount';
		const direction = 'DESC';
		
		const uri = `/user/bookmark?id=${userId}&type=${type}&p=${pageNo}&s=${sortBy}&d=${direction}`;
		console.log("Request URI:", uri);
		console.log("type value:", type); // type 변수 값 확인

		try {
			const { data } = await axios.get(uri);
			currentPageNo = data.number;
			isLastPage = data.totalPages <= currentPageNo + 1;
			makeItemElements(data, reset);
		} catch (error) {
			console.error("아이템 불러오기 실패", error);
		} finally {
			isLoading = false;
		}
	}

	function makeItemElements(data, reset = false) {
		if (reset) itemList.innerHTML = '';
		if (!data.content || data.content.length === 0) {
			if (reset) {
				itemList.innerHTML = '<p>아이템이 없습니다.</p>';
			}
			return;
		}

		let htmlStr = '';
		data.content.forEach(item => {
			htmlStr += `<div class="item">
                    <img src="${item.coverSrc}" alt="${item.novelTitle}">
                    <h3>${item.novelTitle}</h3>
                    <p>${item.writer}</p>
                    <p>조회수: ${item.totalViews}</p>
                    </div>`;
		});
		itemList.insertAdjacentHTML('beforeend', htmlStr);
	}

	// 타입 변경 버튼 이벤트
	document.getElementById('likedBtn').addEventListener('click', () => {
		type = 'liked';
		currentPageNo = 0;
		isLastPage = false;
		console.log(userId, type, currentPageNo);
		getAllItems(currentPageNo, true);
	});
	document.getElementById('watchedBtn').addEventListener('click', () => {
		type = 'watched';
		currentPageNo = 0;
		isLastPage = false;
		console.log(userId, type, currentPageNo);
		getAllItems(currentPageNo, true);
	});
	document.getElementById('purchasedBtn').addEventListener('click', () => {
		type = 'purchased';
		currentPageNo = 0;
		isLastPage = false;
		console.log(userId, type, currentPageNo);
		getAllItems(currentPageNo, true);
	});
});



