/**
 * bookmark.html
 */
console.log("bookmark.js 로드됨");


document.addEventListener('DOMContentLoaded', () => {
    console.log("DOMContentLoaded event fired");

    let currentPageNo = 0;
    let isLoading = false;
    let isLastPage = false;
    let type = 'liked'; // 초기 타입 설정
    let sortBy = 'novel.likeCount'; // 초기 정렬 기준
    let direction = 'DESC'; // 초기 정렬 방향

    const userId = document.querySelector('input#userId').value;
   console.log("User ID:", userId);
   
    const itemList = document.getElementById('itemList');

    if (!userId) {
        console.error("userId를 찾을 수 없습니다.");
        itemList.innerHTML = '<p>사용자 정보를 찾을 수 없습니다. 다시 로그인해주세요.</p>';
        return;
    }

    getAllItems(currentPageNo, true);

    let debounceTimer;
    window.addEventListener('scroll', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 200) {
                getAllItems(currentPageNo + 1);
            }
        }, 150);
    });

    async function getAllItems(pageNo = 0, reset = false) {
        console.log("getAllItems called");

        if (isLoading || isLastPage) return;
        isLoading = true;

        const uri = `/user/bookmark/data`;
        try {
            const requestBody = { id: userId, type, p: pageNo, s: sortBy, d: direction };
            const { data } = await axios.post(uri, requestBody);
         console.log("Request Body:", {
             id: userId,
             type: type,
             p: pageNo,
             s: sortBy,
             d: direction
         }); // 모든 요청 데이터 확인
         
            currentPageNo = data.number;
            isLastPage = data.totalPages <= currentPageNo + 1;
            makeItemElements(data, reset);
			
			if (data && data.totalPages <= currentPageNo + 1) {
			        isLastPage = true;
			        //itemList.insertAdjacentHTML('beforeend', '<p>마지막 페이지입니다.</p>');
			    }
			
        } catch (error) {
            console.error("아이템 불러오기 실패", error);
            itemList.insertAdjacentHTML('beforeend', '<p>데이터를 불러오는 데 실패했습니다. 잠시 후 다시 시도해주세요.</p>');
        } finally {
            isLoading = false;
        }
    }

	function makeItemElements(data, reset = false) {
	        console.log("data.content:", data.content);
	        console.log("itemList", itemList);
	        if (reset) itemList.innerHTML = '';
	        if (!data.content || data.content.length === 0) {
	            if (reset) {
	                itemList.innerHTML = '<p>저장된 작품이 없습니다.</p>' + '<br>'.repeat(8);
	            }
	            return;
	        }

	        let row = document.createElement('div'); // row 컨테이너 생성
	        row.classList.add('card-grid'); 

	        data.content.forEach(item => {
	            let itemHtml = `
	                <div class="item">
	                    <div class="image-container">
	                        <img id="bookmarkimage" src="${item.coverSrc}" alt="${item.novelTitle}">
	                    </div>
	                    <div class="text-container">
	                        <div class="gradeIcon">
	                            <span class="${item.novelGrade === 0 ? 'free' : 'paid'}">
	                                ${item.novelGrade === 0 ? '무료' : '유료'} 
	                            </span>
	                        </div>
	                        <div>
	                            <a href="/novel/${item.novelId}">${item.novelTitle}</a>
	                            ${type === 'watched' ? `<span class="episode-num">${item.episodeNum}화</span>` : ''}
	                            ${type === 'purchased' ? `<span class="episode-num">${item.episodeNum}화</span>` : ''}
	                        </div>
	                        <div><p>작가: ${item.novelWriter}</p></div>
	                        <div><p>장르: ${item.novelGenres.join(', ')}</p></div>
	                        <div><p>작품 소개: ${item.novelIntro}</p></div>
	                        <div class="view-container">
	                            <i class="bi bi-person-plus-fill"></i>
	                            <span>${item.totalViews}</span>
	                        </div>
	                        <div class="like-container">
	                            <i class="bi bi-heart-fill"></i>
	                            <span>${item.likeCount}</span>
	                        </div>
	                        ${type === 'purchased' ? `<div><p><i class="bi bi-clock-fill"></i>${formatDate(item.purchasedDate)}</p></div>` : ''}
	                        ${type === 'watched' ? `<div><p><i class="bi bi-clock-fill"></i>${formatDate(item.accessTime)}</p></div>` : ''}
	                    </div>
	                </div>
	            `;
	            row.insertAdjacentHTML('beforeend', itemHtml); // row 컨테이너에 itemHtml 추가
	        });

	        console.log("htmlStr:", row.innerHTML); // row 컨테이너의 HTML 출력
	        itemList.appendChild(row); // row 컨테이너를 itemList에 추가
	    }

   function activateButton(button) {
       document.querySelectorAll('.typeBtn').forEach(btn => btn.classList.remove('active'));
       button.classList.add('active');
   }
   
   const buttons = document.querySelectorAll('.typeBtn');
   buttons.forEach(button => {
       button.addEventListener('click', () => {
           activateButton(button);
           type = button.getAttribute('data-type');
           currentPageNo = 0;
           isLastPage = false;
           console.log(userId, type, currentPageNo);
           getAllItems(currentPageNo, true);
       });
   });
   
   function formatDate(dateTimeString) {
       const date = new Date(dateTimeString);
       const dateOptions = { year: 'numeric', month: 'long', day: 'numeric' };
       const timeOptions = { hour: 'numeric', minute: 'numeric', second: 'numeric' };

       const formattedDate = date.toLocaleDateString('ko-KR', dateOptions);
       const formattedTime = date.toLocaleTimeString('ko-KR', timeOptions);

       return `${formattedDate} ${formattedTime}`;
   }

});
