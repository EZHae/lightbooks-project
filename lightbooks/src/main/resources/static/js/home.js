/**
 * 
 */

// 스와이퍼 시작
document.addEventListener('DOMContentLoaded', () => {
	// 스와이퍼 시작
	const swiper = new Swiper('.swiper', {
	  // Optional parameters
	  slidesPerView: 1.8,     //  너비
	  centeredSlides: true,
	  spaceBetween: 10, 
	  loop: true,
	  
	  autoplay: {
	    delay: 3000,
	  },
	
	  // If we need pagination
	  pagination: {
	    el: '.swiper-pagination',
		clickable: true,
	  },
	
	  // Navigation arrows
	  navigation: {
	    nextEl: '.swiper-button-next',
	    prevEl: '.swiper-button-prev',
		
	  },
	
	  // And if we need scrollbar
	  scrollbar: {
	    el: '.swiper-scrollbar',
	  },
	});
	
	readNotice();
	
	// 공지사항 읽기
	function readNotice() {
		axios.get('/post/notice/read/highlight').then(response => {
			console.log(response);
			makeNotice(response.data);
		}).catch(error => {
			console.error(error);
		});
	}
	
	function makeNotice(noticeData) {
		console.log(noticeData);
		const noticeItems = document.querySelector('ul#notice-items');
		const noticeButton = document.querySelector('button#notice-first');
		let html = '';

		if (noticeData.length === 0) {
			html =
				`
					<li><a class="dropdown-item" href="#">등록된 공지사항이 없습니다.</a></li>
					<li class="text-end" style="background:#f0f0f0"><a class="dropdown-item text-primary" href="/post/notice">공지사항 보러가기</a></li>
				`;
			noticeItems.innerHTML = html;
			noticeButton.textContent = '등록된 공지사항이 없습니다.';
		}
		
		// 버튼 텍스트를 가장 오래된 데이터로 설정
		const firstNotice = noticeData[0]; // 가장 오래된 데이터
		noticeButton.textContent = firstNotice.title;
		
		// 공지사항 목록을 ul에 추가
		noticeData.forEach(post => {
		    html += 
		    `
		    	<li><a class="dropdown-item" href="/post/notice/details?id=${post.id}">${post.title}</a></li>
		    `;
		});
		noticeItems.innerHTML = html;
		noticeItems.innerHTML += `<li class="text-end" style="background:#f0f0f0"><a class="dropdown-item text-primary" href="/post/notice">공지사항 보러가기</a></li>`;
	}
	
})