/**
 * 
 */


document.addEventListener('DOMContentLoaded', () => {
	const btnLike = document.querySelector('button#btnLike');
	const novelId = btnLike.dataset.novelId;
	const userId = btnLike.dataset.userId;
	
	console.log("소설 아이디: ",novelId);
	console.log("로그인 유저 아이디: ",userId);
	
	// 좋아요 개수 및 상태 불러오기
	loadLikeCount(novelId, userId);
	
	btnLike.addEventListener('click', async function() {
		// 비로그인 사용자는 로그인 페이지로 이동
		if(userId === "0") {
			alert("로그인이 필요합니다.")
			window.location.href = "/user/signin"; // 로그인 창으로 이동
			return;
		}
		const reqBody = {novelId, userId};
		
		try {
			btnLike.disabled = true;
			
			const response = await axios.post(`/api/like`, reqBody, {withCredentials: true});
			console.log(response);
			
			if (response.status !== 200) {
				throw new Error("좋아요 실패!")
			}
			
			const { liked, likeCount } = response.data;
			
			// 좋아요 상태 업데이트
				btnLike.classList.toggle("btn-danger", liked);
				btnLike.classList.toggle("btn-outline-danger", !liked);
				
				const heartIcon = btnLike.querySelector("i");
				if(heartIcon) {
					heartIcon.className = `bi bi-heart${liked ? '-fill' : '' }`;
				}
				const likeCountSpan = btnLike.querySelector("span");
				if(likeCountSpan) {
					likeCountSpan.textContent = likeCount;
				}
		} catch (error) {
			console.error("좋아요 처리 중 오류", error);
		} finally {
  	      btnLike.disabled = false; // 요청 완료 후 버튼 다시 활성화
	    }
	});
	
	
	async function loadLikeCount(novelId, userId) {
		try {
			const response = await axios.get(`/api/like/count/${novelId}?userId=${userId}`);
			const { liked, likeCount } = response.data;
			
			const btnLike = document.querySelector("button#btnLike")
			if(!btnLike) return;
			
			btnLike.classList.toggle("btn-danger", liked);
			btnLike.classList.toggle("btn-outline-danger", !liked);
			
			const heartIcon = btnLike.querySelector("i");
			if(heartIcon) {
				heartIcon.className = `bi bi-heart${liked ? '-fill' : ''}`
			}
			
			const likeCountSpan = btnLike.querySelector("span");
			if(likeCountSpan) {
				likeCountSpan.textContent = likeCount;
			}
		} catch (error) {
			console.error("좋아요 개수를 불러오지 못했습니다.");
		}
	}
	
	
	
	const modal = document.getElementById('ratingModal');
	const openBtn = document.getElementById('openRatingBtn');
	const closeBtn = document.querySelector('.close');
	const starts = document.querySelectorAll('span.star');
	const submitBtn = document.getElementById('btnRating');
	
	let selcetedRating = 0;
	
	// 모달
	openBtn.addEventListener('click', function() {
		modal.style.display = "block";
	});
	
	closeBtn.addEventListener('click', function(){
		modal.style.display = "none";
	})
	
	starts.forEach(star => {
		
	})
	
});


