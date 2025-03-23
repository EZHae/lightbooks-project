/**
 * 
 */
let isLastPage = false; // 마지막 페이지 여부
let isLoading = false; // 중복 방지

document.addEventListener('DOMContentLoaded', () => {
	
	const renderedCommentIds = new Set(); 
	const commentText = document.querySelector('textarea#commentText');
	const counter = document.getElementById('charCount');
	const maxLength = 200;
	
	getAllComments(0,true);
	renderedCommentIds.clear();
	console.log(counter);
	
	// 댓글 입력창
	commentText.addEventListener('input', function() {
		
		this.style.height = 'auto';
		this.style.height = this.scrollHeight + 'px';
		
		if(this.value.length > maxLength) {
			this.value = this.value.substring(0, maxLength);
		}
		counter.innerText = this.value.length;
	});
	
	// 스크롤
	window.addEventListener('scroll', () => {
		const scrollTop = window.scrollY;
	    const windowHeight = window.innerHeight;
	    const bodyHeight = document.body.scrollHeight;
		
		// 끝에 왔을 때	
		if(scrollTop + windowHeight >= bodyHeight - 100) {
			getAllComments(currentPageNo + 1);
		}		
	})
	
	// 현재 댓글 페이지 번호
	let currentPageNo = 0;	
	
	// 소설 목록 불러오기
	async function getAllComments(pageNo = 0, reset = false){ // 디폴트 값 = 0
		if (isLoading || isLastPage) return; // 이미 로딩 중이거나 마지막이면 종료
			isLoading = true; // 호출 시작
			
		// 댓글이 등록될 소설 아이디
		const novelId = document.querySelector('input#novelId').value;
		// 댓글이 등록될 회차 아이디
		const episodeId = document.querySelector('input#episodeId').value;
		
		// Ajax 요청을 보낼 URL: 
		const uri = `/novel/${novelId}/episode/${episodeId}/comment/list?p=${pageNo}`;
		
		try {
			// 비동기 함수 호출
			const { data } = await axios.get(uri);
			// 성공 콜백에서 할 일들을 작성.
			console.log(data);
			
			currentPageNo = data.page.number; // `page` 없이 `number`로 온다면 여기에 맞춤 
			
			// 마지막 페이지인지 체크
			if (data.page && data.page.totalPages <= currentPageNo + 1) {
				isLastPage = true;
			}
			
			makeCommentElements(data, reset); // 댓글 목록을 html 요소로 작성해서 보여줌.
		} catch (error){
			// 실패 콜백에서 할 일들을 작성.
			console.log(error);
		} finally {
			isLoading = false; // 끝난 후 다시 false
		}
	}
	
	function makeCommentElements(data, reset = false){
		// 로그인 사용자 아이디 -> 댓글 삭제/수정 버튼을 만들 지 여부를 결정하기 위해서.
//		const authUser = document.querySelector('span#authenticatedUser').innerText;
		
		const commentList = document.getElementById('commentList');
		const comments = data.content;
		
		if (reset) {
		      commentList.innerHTML = ''; // 댓글 전체 초기화
			  renderedCommentIds.clear();
	  	}
		
		// 첫 페이지일 경우에만 기존 내용 초기화
		if(!comments || comments.length === 0){
			if(reset) {
				commentList.innerHTML = '<p>아직 댓글이 없습니다. 첫 댓글을 남겨보세요!</p>';
			}
			return;
		}
		
		comments.forEach(comment => {
			if (renderedCommentIds.has(comment.id)) return; // 이미 렌더링된 댓글이면 건너뜀
					renderedCommentIds.add(comment.id);
					
			const li = document.createElement('li');
			li.innerHTML = `
				<div class="commentbox">
					<div class="comment-header">
						<span class="comment-nickname">${comment.nickname}</span>
						<span class="comment-data">${new Date(comment.modifiedTime).toLocaleString('ko-KR')}</span>
						${comment.spoiler === 1 ? '<span class="spoiler-label">⚠️ 스포일러</span>' : ''}
					</div>
		                <div class="comment-body">${comment.text}</div>
			                <div class="comment-footer">
		                  		<span class="like-count">👍 <span>${comment.likeCount}</span></span>
			                </div>
			            </div>
			`;
			
			commentList.appendChild(li);
		});
	}
	
	// 댓글 [등록] 버턴에 클릭 이벤트 리스너를 설정
	const btnRegisterComment = document.querySelector('button#btnRegisterComment');
	btnRegisterComment.addEventListener('click', registerComment);
		
	
	// 댓글 등록 함수
	async function registerComment() {
		
		// 댓글에 등록될 유저 아이디
		const userId = document.querySelector('input#userId').value;
		// 댓글이 등록될 소설 아이디
		const novelId = document.querySelector('input#novelId').value;
		// 댓글이 등록될 회차 아이디
		const episodeId = document.querySelector('input#episodeId').value;
		// 댓글 내용
		const text = document.querySelector('textarea#commentText').value;
		// 댓글 작성자
		const nickname = document.querySelector('input#nickname').value;
		// 댓글 스포일러 여부
		const spoiler = document.querySelector('#spoiler').checked ? 1 : 0;

		console.log("user: " ,userId);
		console.log("novelId: " ,novelId);
		console.log("text: " ,text);
		console.log("nickname: " ,nickname);
		console.log("spoiler: ",spoiler)
		
	    if (text.trim() === '') {
	        alert('댓글 내용은 반드시 입력해야 합니다.');
	        return;
	    }
	    
	    // Ajax 요청에서 Request Body에 포함시켜서 전송할 데이터
	    const reqBody = {novelId, episodeId, userId, text, nickname, spoiler};
	    
	    // Ajax 요청을 보내고, 응답/에러 처리
	    try {
	        const { data } = await axios.post(`/novel/${novelId}/episode/${episodeId}/comment`, reqBody);
	        console.log(data);
	        
	        // 댓글 입력 textarea의 내용을 지움.
	        document.querySelector('textarea#commentText').value = '';
			// 스크롤 최상단 이동
			window.scrollTo({ top: 0, behavior: 'smooth' });
	        // 댓글 목록을 다시 그림.
	        getAllComments(0, true);
	        
	    } catch (error) {
	        console.log(error);
	    }
	}
})