/**
 * 
 */

// 스와이퍼 시작
document.addEventListener('DOMContentLoaded', () => {
	// 공지사항 공통
	readNotice();
	
	//  홈 메인 페이지일 경우
	if (window.location.pathname === "/") {
		loadHomeNovels();  // 추천 홈 데이터
	}
	
	const screenId = document.body.dataset.screenId;

	if (screenId === "1") {
		loadHomeNovels();
	} else if (screenId === "2") {
		loadBestNovels();
	} else if (screenId === "3") {
		loadNewNovels();
	}
	console.log("screenId:", screenId);
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
	
	
	// 홈 데이터 Axios
	async function loadHomeNovels() {
		// 스켈레톤
		renderSkeletonCards("bestNovelsContainer");
		renderSkeletonCards("freeNovelsContainer");
		renderSkeletonCards("paidNovelsContainer");
		renderSkeletonCards("eventNovelsContainer");
		renderSkeletonCards("genreNovelsContainer");
		
		try {
			const response = await axios.get('/api/recommend/home');
			const data = response.data;
			
			console.log(data.genreNovelsMap);
			
			renderNovels(data.bestNovels, 'bestNovelsContainer');
			renderImgNovels(data.freeNovels, 'freeNovelsContainer');
			renderImgNovels(data.paidNovels, 'paidNovelsContainer');
			renderImgNovels(data.eventNovels, 'eventNovelsContainer');
			renderGenreNovels(data.genreNovelsMap, 'genreNovelsContainer');
			
		} catch(error) {
			console.log(error);
		}
	}
	
	// 소설 공통 html
	function renderNovels(novels, containerId) {
		const container = document.getElementById(containerId);
		container.innerHTML = '';
		
		novels.forEach(novel => {
			// 9999+ 포맷
			const likeCount = (novel.likeCount > 9999) ? '9999+' : (novel.likeCount || 0);
			const totalViews = (novel.totalViews > 9999) ? '9999+' : (novel.totalViews || 0);
			const card = document.createElement('div');
			card.classList.add('novel-card');
			
			card.innerHTML = `
				<a href="/novel/${novel.id}">
					<div class="novel-image-wrapper">
						<span class="novel-card-grade ${novel.grade === 0 ? 'grade-free shimmer' : 'grade-paid shimmer'}">
							${novel.grade === 0 ? '무료' : '유료'}
						</span>
						<img class="novelImgs" src="${novel.coverSrc}" loading="lazy" alt="${novel.title}">
					</div>
					<span class="novel-card-title">${novel.title}</span><br/>
					<span class="novel-card-writer">${novel.writer} · ${novel.genres}</span><br/>
					<i class="bi bi-heart"></i> ${likeCount}
					<i class="bi bi-star"></i> ${novel.rating || 0}
					<i class="bi bi-person-plus-fill"></i> ${totalViews}
					
					
				</a>
			`;
			
			container.appendChild(card);
		});
	}
	// 장르별 소설 공통 함수
		function renderGenreNovels(genreMap, containerId) {
			const container = document.getElementById(containerId);
			container.innerHTML = '';
			
			Object.entries(genreMap).forEach(([genre, novels]) => {
				const section = document.createElement('div');
				
				section.innerHTML = `
					<div class="subTitle">
						<span class="font">${genre} 소설</span><i class="bi bi-caret-right-fill"></i>	
					</div>
					<div class="novel-grid">
						${novels.map(novel => `
		                   <div class="novel-card">
		                       <a href="/novel/${novel.id}">
		                           <div class="novel-image-wrapper">
		                               <span class="novel-card-grade ${novel.grade === 0 ? 'grade-free shimmer' : 'grade-paid shimmer'}">
		                                   ${novel.grade === 0 ? '무료' : '유료'}
		                               </span>
		                               <img class="novelImgs" src="${novel.coverSrc}" loading="lazy" alt="${novel.title}">
		                           </div>
		                       </a>
		                   </div>`).join('')}
		           </div>    
				`;
				container.appendChild(section);
			});
		}
	
	// 라잇무, 라잇 유, 이벤트 전용... 
	function renderImgNovels(novels, containerId){
		const container = document.getElementById(containerId);
		container.innerHTML = '';
		
		if(!novels || !Array.isArray(novels)) {
	        console.warn('novels가 없습니다.', containerId, novels);
	        return; // 데이터 없으면 바로 종료
	    }
		
		novels.forEach(novel => {
			const card = document.createElement('div');
			card.classList.add('novel-card');
			
			card.innerHTML = `
				<a href="/novel/${novel.id}">
	                <div class="novel-image-wrapper">
	                    <span class="novel-card-grade ${novel.grade === 0 ? 'grade-free shimmer' : 'grade-paid shimmer'}">
	                        ${novel.grade === 0 ? '무료' : '유료'}
	                    </span>
	                    <img class="novelImgs" src="${novel.coverSrc}" loading="lazy" alt="${novel.title}">
	                </div>
	            </a>
			`;
			
			container.appendChild(card);
		});
	}
	
	
	// 추천 베스트
	function loadBestNovels() {
		const container = document.querySelector("#novel-grid");
		if (!container) {
			console.warn("컨테이너를 찾을 수 없습니다!");
			return;
		}
		renderSkeletonCards(container.id);
	  axios.get("/api/recommend/best")
	    .then(response => {
	      const novels = response.data;
	      // 기존 소설 목록 초기화
	      container.innerHTML = "";

	      // 새로운 소설 목록 추가
	      novels.forEach(novel => {
	        const card = document.createElement("div");
	        card.className = "novel-card mb-2";

	        card.innerHTML = `
			<a href="/novel/${novel.id}">
			  	<div class="novel-image-wrapper">
	                <span class="novel-card-grade ${novel.grade === 0 ? 'grade-free shimmer' : 'grade-paid shimmer'}">
	                    ${novel.grade === 0 ? '무료' : '유료'}
	                </span>
	                <img class="novelImgs" src="${novel.coverSrc}" loading="lazy" alt="${novel.title}">
	            </div>
	            <span class="novel-card-title">${novel.title}</span>
	          </a>
	        `;

	        container.appendChild(card);
	      });
	    })
	    .catch(error => {
	      console.error("추천 베스트 소설 불러오기 실패!", error);
	    });
	}
	
	// 오늘 신작
	function loadNewNovels() {
		const container = document.querySelector("#novel-grid");
		if (!container) {
			console.warn("컨테이너를 찾을 수 없습니다!");
			return;
		}
		renderSkeletonCards(container.id);
		
		axios.get("/api/recommend/new")
		.then(response => {
			const data = response.data; // Map<String, List<NovelListItemDto>>
			const container = document.querySelector(".bestNovels");
			container.innerHTML = '';

			Object.entries(data).forEach(([date, novels]) => {
				// 날짜 타이틀
				const dateTitle = document.createElement("h5");
				dateTitle.className = "date-title mt-3";
				dateTitle.textContent = date;
				container.appendChild(dateTitle);

				// 소설 카드 그리드
				const grid = document.createElement("div");
				grid.className = "novel-grid";

				novels.forEach(novel => {
					const card = document.createElement("div");
					card.className = "novel-card";

					const link = document.createElement("a");
					link.href = `/novel/${novel.id}`;

					const wrapper = document.createElement("div");
					wrapper.className = "novel-image-wrapper";

					// 뱃지
					const badge = document.createElement("span");
					badge.className = `novel-card-grade ${novel.grade === 0 ? 'grade-free shimmer' : 'grade-paid shimmer'}`;
					badge.textContent = novel.grade === 0 ? "무료" : "유료";

					// 이미지
					const img = document.createElement("img");
					img.className = "novelImgs";
					img.src = novel.coverSrc;
					img.loading = "lazy";
					img.alt = novel.title;

					wrapper.appendChild(badge);
					wrapper.appendChild(img);

					link.appendChild(wrapper);
					card.appendChild(link);
					grid.appendChild(card);
				});

				container.appendChild(grid);
			});
		})
		.catch(error => {
			console.error("오늘신작 로딩 실패:", error);
		});
	}
	
	function renderSkeletonCards(containerId, count = 12) {
		const container = document.getElementById(containerId);
		container.innerHTML = '';

		for (let i = 0; i < count; i++) {
			const card = document.createElement('div');
			card.className = 'novel-card skeleton-card';

			card.innerHTML = `
				<div class="skeleton skeleton-img"></div>
				<div class="skeleton skeleton-text"></div>
				<div class="skeleton skeleton-text" style="width: 60%;"></div>
			`;

			container.appendChild(card);
		}
	}
})