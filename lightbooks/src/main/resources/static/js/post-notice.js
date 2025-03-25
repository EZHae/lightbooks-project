document.addEventListener('DOMContentLoaded', () => {
	console.log('post-notice.js');
	let userId = null;
	if (document.querySelector('span#userId')) {
		userId = document.querySelector('span#userId').textContent;
		console.log(userId);
	}
	
	readNotice();
	
	// 공지사항 읽기
	function readNotice() {
		axios.get('/post/notice/read').then(response => {
			console.log(response);
			makeNotice(response.data);
		}).catch(error => {
			console.error(error);
		});
	}
	
	// 공지사항 생성
	function makeNotice(noticeData) {
		console.log(noticeData);
		const noticeTBody = document.querySelector('tbody#noticeTBody');
		let html = '';
		
		if (noticeData.length === 0) {
			html =
				`
					<tr class="row">
						<td class="col">등록된 공지사항이 없습니다.</td>
					</tr>
				`;
			noticeTBody.innerHTML = html;
		}
		
		// highlight가 1인 항목과 0인 항목을 분리해서 각각 정렬
		let highlightedPosts = noticeData.filter(post => post.highlight === 1);
		let regularPosts = noticeData.filter(post => post.highlight === 0);
		
		// 1. highlight가 1인 항목은 modifiedTime 기준으로 내림차순 정렬
		highlightedPosts.sort((a, b) => new Date(a.modifiedTime) - new Date(b.modifiedTime));
		
		// 2. highlight가 0인 항목은 createdTime 기준으로 오름차순 정렬
		regularPosts.sort((a, b) => new Date(b.createdTime) - new Date(a.createdTime));
		
		// 두 배열을 합치기
		const sortedPosts = [...highlightedPosts, ...regularPosts];
		
		// 정렬된 데이터를 HTML로 표시
		if (sortedPosts.length > 0 && userId == 16) {
			sortedPosts.forEach(post => {
				let formatCreatedTime = formatDateTime(post.createdTime);
				html += 
				`
					<tr class="row">
						<td class="col-1" ${post.highlight === 1 ? 'style="background: #f0f0f0"' : ''}>
							<div class="form-check form-switch">
								<input class="form-check-input highlight-switch" type="checkbox" role="switch" data-id="${post.id}" ${post.highlight === 1 ? 'checked' : ''}>
							</div>
						</td>
						<td class="col-6" ${post.highlight === 1 ? 'style="background: #f0f0f0"' : ''}>
							<a class="text-black link-offset-2 link-offset-3-hover link-underline-dark link-underline-opacity-0 link-underline-opacity-75-hover" href="/post/notice/details?id=${post.id}">
								${post.highlight === 1 ? `<strong>${post.title}</strong>` : post.title}
							</a>
						</td>
						<td class="col-2" ${post.highlight === 1 ? 'style="background: #f0f0f0"' : ''}>${post.writer}</td>
						<td class="col-3" ${post.highlight === 1 ? 'style="background: #f0f0f0"' : ''}>${formatCreatedTime}</td>
					</tr>
				`;
			});
			noticeTBody.innerHTML = html;
			addSwitchEventListeners(noticeData); // 데이터 전달
		} else if (sortedPosts.length > 0 && userId != 16) {
			sortedPosts.forEach(post => {
				let formatCreatedTime = formatDateTime(post.createdTime);
				html += 
				`
					<tr class="row">
						<td class="col-7" ${post.highlight === 1 ? 'style="background: #f0f0f0"' : ''}>
							<a class="text-black link-offset-2 link-offset-3-hover link-underline-dark link-underline-opacity-0 link-underline-opacity-75-hover" href="/post/notice/details?id=${post.id}">
								${post.highlight === 1 ? `<strong>${post.title}</strong>` : post.title}
							</a>
						</td>
						<td class="col-2" ${post.highlight === 1 ? 'style="background: #f0f0f0"' : ''}>${post.writer}</td>
						<td class="col-3" ${post.highlight === 1 ? 'style="background: #f0f0f0"' : ''}>${formatCreatedTime}</td>
					</tr>
				`;
			});
			noticeTBody.innerHTML = html;
		}
	}

	// 스위치 이벤트 리스너 추가
	function addSwitchEventListeners(noticeData) {
		const switches = document.querySelectorAll('.highlight-switch');
		const currentHighlightCount = noticeData.filter(post => post.highlight === 1).length;

		switches.forEach(switchElem => {
			switchElem.addEventListener('change', (e) => {
				const postId = e.target.getAttribute('data-id');
				const newHighlight = e.target.checked ? 1 : 0;

				// highlight가 1로 설정될 수 있는 최대 개수 확인
				if (newHighlight === 1 && currentHighlightCount >= 5) {
					alert('최대 5개의 공지만 강조할 수 있습니다.');
					e.target.checked = false; // 스위치 다시 원래대로 설정
				} else {
					updateHighlight(postId, newHighlight);
				}
			});
		});
	}

	// highlight 값을 업데이트하는 함수
	function updateHighlight(postId, newHighlight) {
		disableClicks();
		axios.post('/admin/post/update/highlight', { id: postId, highlight: newHighlight }).then(response => {
			alert('해당 공지사항의 강조여부가 변경되었습니다.');
			readNotice(); // 업데이트 후 공지사항 다시 불러오기
			removeBlocker(); // 업데이트 후 투명 벽 제거
			//window.location.reload(); // 업데이트 후 새로고침
		}).catch(error => {
			console.error(error);
			removeBlocker();
		});
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
	
	// 투명벽 제거 함수
	function removeBlocker() {
	    let blocker = document.getElementById("blocker");
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