/**
 * 
 */


document.addEventListener('DOMContentLoaded', () => {

	// 검색 키워드를 input hidden에서 현재 값을 가져옴	
	const keyword = document.getElementById('searchKeyword').value;
	// 검색 결과 초기 : 제목 0페이지, 작가 0페이지
	loadSearchResults(keyword, 0, 0);

});

// 검색 결과 데이터를 서버에서 비동기로 받아오는 함수
function loadSearchResults(keyword, tp, wp) {
	axios.get('/novel/search/result', { params: { keyword, tp, wp } }) // 제목 페이지 번호, 작가 페이지 번호를 전달.
		.then(response => {
			console.log(response);
			// 제목 기준 결과 그리기
			renderResult("제목", response.data.titlePage, keyword, tp, wp, true);
			// 작가 기준 결과 그리기
			renderResult("작가", response.data.writerPage, keyword, tp, wp, false);
		});
}

// 검색 결과(제목/작가)를 랜더링
function renderResult(label, pageData, keyword, tp, wp, isTitle) {
	// 결과를 출력할 영역 (제목 작가 각각)
	const container = document.getElementById(isTitle ? 'titleResultContainer' : 'writerResultContainer');
	container.innerHTML = `<h3> ${label} '${keyword}' 로 검색된 결과 </h3>`;

	if (!pageData || pageData.content.length === 0) {
		container.innerHTML += `<p class="text-muted">검색된 결과가 없습니다.</p>`;
		return;
	}

	// 소설들이 들어갈 영역
	const grid = document.createElement("div");
	grid.className = "searchGrid";

	// 각 소설 데이터를 반복하면서 생성
	pageData.content.forEach(novel => {
		const box = document.createElement("div");
		box.className = "searchBox";

		// 카드 html 구성
		box.innerHTML = `
				<div class="searchBox-link">
				    <a href="/novel/${novel.id}">
						<img src="${novel.coverSrc || '/images/default.jpg'}" class="search-img">
					</a>
				</div>
	          	<div class="searchContent">
					<a href="/novel/${novel.id}">
			            <h3>${novel.title}</h3>
						<p class="meta-line">
						  ${novel.genres ? novel.genres : '-'} / ${novel.writer ? novel.writer : '미상'}
						</p>
			            <p class="meta-line">
			              <i class="bi bi-heart"></i> ${novel.likeCount || 0} 
			              <i class="bi bi-star"></i> ${novel.rating || 0}
			              <i class="bi bi-person-plus-fill"></i> ${novel.totalViews || 0}
			            </p>
			            <p class="meta-line">
			              ${novel.modifiedTime ? novel.modifiedTime.split('T')[0] + ' 업데이트' : ''}
						  </br>
			              ${getStateLabel(novel.state)}
			            </p>
					</a>
      			</div>
		        `;

		// 그리드에 카드 추가
		grid.appendChild(box);
	});

	// 전체 그리드 결과 추가
	container.appendChild(grid);

	// 페이징 영역도 같이 렌더링
	renderPagination(container, pageData, keyword, tp, wp, isTitle);
}


// 페이지 번호 버튼들이 만들어 붙이는 함수
function renderPagination(container, pageData, keyword, tp, wp, isTitle) {
	const pagDiv = document.createElement("ul");
	pagDiv.className = "pagination justify-content-center mt-4";

	const currentPage = pageData.number;
	const totalPages = pageData.totalPages;
	pagDiv.appendChild(createPageButton("처음", 0, currentPage === 0, isTitle, keyword, tp, wp));
	pagDiv.appendChild(createPageButton("이전", currentPage - 1, !pageData.hasPrevious, isTitle, keyword, tp, wp));

	// 페이지 수만큼 반복해서 버튼 생성
	for (let i = 0; i < pageData.totalPages; i++) {
		const li = document.createElement("li");
		li.className = `page-item ${i === currentPage ? 'active' : ''}`;
		const a = document.createElement("a");

		a.className = "page-link";
		a.innerText = i + 1;
		a.href = "#";
		a.onclick = (e) => {
			e.preventDefault();
			isTitle ? loadSearchResults(keyword, i, wp) : loadSearchResults(keyword, tp, i);
		};
		li.appendChild(a);
		pagDiv.appendChild(li);
	}

	pagDiv.appendChild(createPageButton("다음", currentPage + 1, !pageData.hasNext, isTitle, keyword, tp, wp));
	pagDiv.appendChild(createPageButton("끝", totalPages - 1, currentPage === totalPages - 1, isTitle, keyword, tp, wp));

	container.appendChild(pagDiv);
}

function createPageButton(label, pageNo, disabled, isTitle, keyword, tp, wp) {
  const li = document.createElement("li");
  li.className = `page-item ${disabled ? 'disabled' : ''}`;
  const a = document.createElement("a");
  a.className = "page-link";
  a.innerText = label;
  a.href = "#";
  if (!disabled) {
    a.onclick = (e) => {
      e.preventDefault();
      isTitle ? loadSearchResults(keyword, pageNo, wp) : loadSearchResults(keyword, tp, pageNo);
    };
  }
  li.appendChild(a);
  return li;
}

function getStateLabel(state) {
  switch (state) {
    case 0: return "완결";
    case 1: return "연재중";
    case 2: return "휴재";
    case 3: return "준비중";
    default: return "-";
  }
}