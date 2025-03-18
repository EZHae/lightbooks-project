/**
 * 
 */


document.addEventListener('DOMContentLoaded', () => {
	const btnLike = document.querySelector('button#btnLike');
	const novelId = btnLike.dataset.novelId;
	const userId = btnLike.dataset.userId;
	
	let isProcessing = false; //중복 요청 방지 변수
	console.log("소설 아이디: ",novelId);
	console.log("로그인 유저 아이디: ",userId);

	// 좋아요, 별점 새로 불러오기
	loadLikeCount(novelId, userId);
	fetchRating();
	checkUserRating(novelId, userId);
	
	const averageRatingSpan = document.getElementById('averageRating');
	console.log(averageRatingSpan);
	

	// 별점 조회 기능
	function fetchRating() {
		fetch(`/api/${novelId}/rating`)		
			.then(response => response.json())
			.then(data => {
				
				console.log("API 응답 데이터:", data);
				averageRatingSpan.textContent = data.avgRating.toFixed(1)
		})
		.catch(error => console.error("별점 데이터를 가져오지 못했습니다."));
	}
	
	// 별점 관련 기능
	const btnRatingSubmit = document.getElementById('btnRatingSubmit');
	const stars = document.querySelectorAll('span.star');
	const selectedRating = document.getElementById('selectedRating');
	
	console.log("선택된 별 개수:", stars.length);
	let ratingValue = 0; // 현재 선택한 별점
	
	// 마우스를 올릴 때 해당 별 색칠
	stars.forEach(star => {
		star.addEventListener('mouseover', function(){
			updateStarColors(this.getAttribute("data-value"));
		});
		
		// 별에서 마우스가 벗어났을 때 기존 선택한 별점 상태 유지
		star.addEventListener('mouseout', function(){
			updateStarColors(ratingValue);
		});
		
		//별을 클릭하면 별점 값 저장
		star.addEventListener('click', function(){
			ratingValue = parseInt(this.getAttribute("data-value"));
			selectedRating.value = ratingValue;
			console.log("⭐ 선택된 별점 값: ", selectedRating.value);
			updateStarColors(ratingValue);
		});
	});
	
	// 별 색상을 업데이트하는 함수
	function updateStarColors(value) {
		console.log(`별 색상 업데이트! 선택된 값: ${value}`);
		
		document.querySelectorAll('.star').forEach(star => {
			const starValue = parseInt(star.getAttribute("data-value"));
			
			if (starValue <= value) {
	            star.classList.remove("bi-star");
	            star.classList.add("bi-star-fill"); // 채워진 별로 변경
	            star.style.color = "gold"; //  노란색
	        } else {
	            star.classList.remove("bi-star-fill");
	            star.classList.add("bi-star"); //  빈 별로 변경
	            star.style.color = "gray"; //  회색
	        }
		});
	}
	
	// 서버로 데이터 전송
	btnRatingSubmit.addEventListener('click', async function() {
		if(!userId || userId === "0") {
			alert("로그인이 필요합니다.")
			window.location.href = "/user/signin"; // 로그인 창으로 이동
			return;
		}
		if (ratingValue <= 0) {
            alert("별점을 선택해주세요!");
            return;
        }
		
		// 버튼을 다시 누를 경우 isProcessing 초기화
		if (window.isProcessing) return;
		window.isProcessing = true;
		
		// 연속 입력 방지
		btnRatingSubmit.disabled = true;
	    btnRatingSubmit.textContent = "처리 중...";
		
		const reqBody = { novelid: Number(novelId), userId: Number(userId), rating: ratingValue};	
		
		console.log(" 서버로 보낼 데이터:", reqBody);
		try{
			const response = await axios.post(`/api/${novelId}/rating`, reqBody , {withCredentials: true});
			console.log("별점 저장 완료: " , response.data);
			
			
			// 별점 준 뒤 버튼 색상 업데이트
			checkUserRating(novelId, userId);
			
			
			// 모달 닫기
			const ratingModal = bootstrap.Modal.getInstance(document.getElementById('ratingModal'));
			if (ratingModal) {
	            ratingModal.hide();
	        }
			
			// 별점 새로 불러오기
			fetchRating(novelId);
			
		} catch(error) {
			console.error("별점 저장 실패:" , error);
		} finally {
			
			window.isProcessing = false; // 요청 완료 후 다시 요청 가능
			
			// 🚀 버튼 활성화 (요청 완료 후)
	        btnRatingSubmit.disabled = false;
	        btnRatingSubmit.textContent = "확인";
			
		}
	});
	
	// 사용자 체크 (별점을 남겼는지)
	async function checkUserRating(novelId, userId){
		
		try{
			const response = await axios.get(`/api/${novelId}/user/${userId}/rating`);
			const hasRated = response.data;
			
			console.log("사용자가 별점을 남겼는가?", hasRated);
			
			const ratingButton = document.getElementById('openRatingBtn');
			
			if(hasRated) { // 사용자가 별점을 남겼으면 버튼 색상을 바꾸는 곳
				ratingButton.classList.remove('btn-outline-warning');
				ratingButton.classList.add('btn-warning');
				ratingButton.style.color = "white"; // 글자 색상을 흰색으로 변경
			} else {
				ratingButton.classList.remove('btn-warning');
				ratingButton.classList.add('btn-outline-warning');			
			}
			
		} catch (error){
			console.error("사용자 별점 조회 실패!!", error);
		}
	}
		
	
	
	

	// 좋아요 요청
	btnLike.addEventListener('click', async function() {
		// 비로그인 사용자는 로그인 페이지로 이동
		if(!userId || userId === "0") {
			alert("로그인이 필요합니다.")
			window.location.href = "/user/signin"; // 로그인 창으로 이동
			return;
		}
		
		//중복 요청 방지 (요청이 처리 중이면 클릭 불가능)
		if (isProcessing) return;
		isProcessing = true;
		
		const reqBody = {novelId, userId};
		
		
		// UI를 먼저 업데이트하여 즉각 반응하도록 수정
		const heartIcon = btnLike.querySelector("i");
		const likeCountSpan = btnLike.querySelector("span");
		
		const isLiked = btnLike.classList.contains("btn-danger"); // 현재 좋아요 여부
		const newLikeCount = isLiked ? parseInt(likeCountSpan.textContent) - 1 : parseInt(likeCountSpan.textContent) + 1;
		
		
		// 반응 속도
		btnLike.classList.toggle("btn-danger", !isLiked);
		btnLike.classList.toggle("btn-outline-danger", isLiked);
		heartIcon.className = `bi bi-heart${!isLiked ? '-fill' : ''}`;
		likeCountSpan.textContent = newLikeCount;
		
		try {
			const response = await axios.post(`/api/like`, reqBody, {withCredentials: true});
			console.log(response);
			
			if (response.status !== 200) {
				throw new Error("좋아요 실패!")
			}
			
			const { liked, likeCount } = response.data;
			
			// 좋아요 상태 업데이트
			btnLike.classList.toggle("btn-danger", liked);
			btnLike.classList.toggle("btn-outline-danger", !liked);
			heartIcon.className = `bi bi-heart${liked ? '-fill' : '' }`;
			likeCountSpan.textContent = likeCount;
			
		} catch (error) {
			console.error("좋아요 처리 중 오류", error);
			
			//오류 발생 시 원래 상태로 되돌리기 (UI 복구)
			btnLike.classList.toggle("btn-danger", isLiked);
            btnLike.classList.toggle("btn-outline-danger", !isLiked);
            heartIcon.className = `bi bi-heart${isLiked ? '-fill' : ''}`;
            likeCountSpan.textContent = isLiked ? newLikeCount + 1 : newLikeCount - 1;
		} finally {
            setTimeout(() => { isProcessing = false; }, 500); // 0.5초 동안 추가 클릭 방지
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
	
});


