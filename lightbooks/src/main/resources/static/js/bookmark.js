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
        }, 200);
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
        } catch (error) {
            console.error("아이템 불러오기 실패", error);
            itemList.insertAdjacentHTML('beforeend', '<p>데이터를 불러오는 데 실패했습니다. 잠시 후 다시 시도해주세요.</p>');
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

});



