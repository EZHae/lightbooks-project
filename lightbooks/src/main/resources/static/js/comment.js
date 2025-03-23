/**
 * 
 */
let isLastPage = false; // 마지막 페이지 여부
let isLoading = false; // 중복 방지
const renderedCommentIds = new Set(); 

document.addEventListener('DOMContentLoaded', () => {
	const commentText = document.querySelector('textarea#commentText');
	const counter = document.getElementById('charCount');
	const novelTitle = document.querySelector('#novelTitle')?.value;
	const episodeTitle = document.querySelector('#episodeTitle')?.value;
	
	const maxLength = 200;
	
	renderedCommentIds.clear();
	getAllComments(0,true);
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
			
		if (reset) {
		   	renderedCommentIds.clear(); 
		 }
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
		
		if (text.replace(/\s/g, '') === '') {
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
			
			isLastPage = false;
			currentPageNo = 0;	
			renderedCommentIds.clear();
			
	        // 댓글 목록을 다시 그림.
	        getAllComments(0, true);
	        
	    } catch (error) {
	        console.log(error);
	    }
	}
	
	// 댓글 목록을 만들어주는 이벤튼	
	function makeCommentElements(data, reset = false){
		// 로그인 사용자 아이디 -> 댓글 삭제/수정 버튼을 만들 지 여부를 결정하기 위해서.
		const authUser = document.querySelector('#authNickname').innerText;
		console.log(authUser);
		
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
		let htmlStr = '';
		comments.forEach(comment => {
		    if (renderedCommentIds.has(comment.id)) return;
		    renderedCommentIds.add(comment.id);

		    htmlStr += `
		        <div class="commentBox">
		            <div class="commentHeader">
		                <span class="comment-nickname">${comment.nickname}</span>
		                <span class="comment-date">${new Date(comment.modifiedTime).toLocaleString('ko-KR')}</span>
		            </div>

		            <div class="commentBody" data-spoiler="${comment.spoiler}">
		                ${
							comment.spoiler === 1
							     ? `<span class="spoiler-toggle" style="color: gray; cursor: pointer;">주의! 스포일러입니다. (클릭해서 보기)</span>
							        <span class="spoiler-text" style="display: none;">${comment.text}</span>`
							     : `<span class="comment-text">${comment.text}</span>`
							}
		            </div>
				    <span class="comment-title">[${novelTitle}] - ${episodeTitle}화</span>

		            <div class="commentEpisode-info">
		                ${comment.episodeTitle ? `${comment.episodeTitle}` : ''}
		            </div>
					
					
		            <div class="commentFooter">
						<div>
							<button class="like-button btnLike ${comment.likedByUser ? 'liked' : ''}" data-id="${comment.id}">
						    	❤️ <span>${comment.likeCount}</span>
					  		</button>
						</div>
					`;
				    // 수정/삭제는 사용자 본인일 경우만
				    if (authUser === comment.nickname) {
						console.log(comment.nickname);
				        htmlStr += `
		                <div class="right-actions">
		                    <button class="btnUpdate" data-id="${comment.id}">수정</button>
		                    <button class="btnDelete" data-id="${comment.id}">삭제</button>
		                </div>
				        `;
				    }
				    htmlStr += `
				            </div> 
				        </div> 
				    `;
			});
		commentList.insertAdjacentHTML('beforeend', htmlStr);
		
		
		// 댓글 [삭제], [수정] 버튼을 찾고, 클릭 이벤트 리스너를 설정.
		const btnDeletes = document.querySelectorAll('button.btnDelete');	
		
		btnDeletes.forEach((btn) => btn.addEventListener('click', deleteComment));

		const btnUpdates = document.querySelectorAll('button.btnUpdate');
		btnUpdates.forEach((btn) => btn.addEventListener('click', handleEditClick));
		
		const btnLikes = document.querySelectorAll('button.btnLike');
		btnLikes.forEach((btn) => btn.addEventListener('click', handleLikeClick));
	}
	
	
	// 댓글 삭제
	async function deleteComment(event) {
		console.log(event.target);
		
		const check = confirm('정말 삭제할까요?');
		if(!check){
			return;
		}
		const novelId = document.querySelector('input#novelId').value;
		const episodeId = document.querySelector('input#episodeId').value;
		const commentId = event.target.getAttribute('data-id');
		const uri = `/novel/${novelId}/episode/${episodeId}/comment/${commentId}`;
		try{
			const response = await axios.delete(uri);
			console.log(`deleted comment id = ${response.data}`);
			
			alert('댓글이 삭제됐습니다.');
			getAllComments(0,true);
		} catch(error){
			console.log(error);
		}
		
	}
	
	// 댓글 업데이트 버튼 클릭 시 
	function handleEditClick(event) {
	    const commentBox = event.target.closest('.commentBox');
	    const commentBody = commentBox.querySelector('.commentBody');
		// 이미 작성 중이라면?
		if(commentBody.querySelector('textarea')){
			return;
		}
		
		let originalText = '';
		const spoilerTextEl = commentBody.querySelector('.spoiler-text');
		const plainTextEl = commentBody.querySelector('.comment-text');

		if (spoilerTextEl) {
		  originalText = spoilerTextEl.textContent.trim();
		} else if (plainTextEl) {
		  originalText = plainTextEl.textContent.trim();
		} else {
		  // 혹시 모르니 fallback 처리
		  originalText = commentBody.textContent.trim();
		}
		  
		// 기존 텍스트 영역
	    commentBody.innerHTML = `
	        <textarea class="edit-textarea form-control" rows="3">${originalText}</textarea>
			<div class="edit-bottom">
	            <label>
				  <input id="edit-spoiler" class="edit-spoiler" type="checkbox"/> 
				  <span class="spoiler-text">댓글에 스포일러 포함</span>
				</label>
				<div class="btnEdit">
					<button class="btnSaveEdit" data-id="${event.target.dataset.id}">저장</button>
					<button class="btnEditCancle" >취소</button>
				</div>
			</div>
	    `;
		
		// 스포일러 확인 여부를 체크
		const isSpoiler = commentBody.getAttribute('data-spoiler') === '1';
		// 체크 요소를 찾아서 체크상태로 만들어줌
		const checkbox = commentBody.querySelector('.edit-spoiler');
		if (checkbox && isSpoiler) {
		  checkbox.checked = true;
		}
		
		// 수정/삭제 버튼 숨기기
		const actions = commentBox.querySelector('.right-actions');
		if (actions) {
			actions.style.display = 'none';
		}
	
		const saveBtn = commentBody.querySelector('.btnSaveEdit');
		saveBtn.addEventListener('click', updateComment);

		const btnEditCancle = commentBody.querySelector('.btnEditCancle');
		btnEditCancle.addEventListener('click', () => {
			commentBody.innerHTML = `
	            <p class="comment-text">${originalText}</p>
	        `;
	        if (actions) {
	            actions.style.display = 'block';
	        }
		});
	};
	
	// 댓글 업데이트
	async function updateComment(event) {
		console.log(event.target);
		// 수정할 목록들..
		const commentId = event.currentTarget.getAttribute('data-id');
		console.log("댓글 ID:", commentId);
		
		const novelId = document.querySelector('input#novelId').value;
		const episodeId = document.querySelector('input#episodeId').value;
		
		const commentBox = event.currentTarget.closest('.commentBox');
		const nickname = document.querySelector('input#nickname').value;
		const spoiler = commentBox.querySelector('input#edit-spoiler').checked ? 1 : 0;
		console.log("스포일러 여부: ", spoiler);
		const textarea = commentBox.querySelector('textarea.edit-textarea');
	    const updatedText = textarea.value;

		if (updatedText.replace(/\s/g, '') === '') {
		    alert('수정할 내용이 비어있습니다.');
		    return;
		}
		
		const uri = `/novel/${novelId}/episode/${episodeId}/comment/${commentId}`;
		const data = { text: updatedText, spoiler, nickname };
		
		try {
			
			const response = await axios.put(uri, data);
			console.log(`update comment id = ${response.data}`);
			
			alert('댓글이 수정됐습니다.');
			
			renderedCommentIds.clear(); // 기존 댓글 ID 캐시 초기화
			getAllComments(0, true);
		} catch (error) {
			console.log(error);
		}
	}
	
	
	document.addEventListener('click', function(event) {
		if (event.target.classList.contains('spoiler-toggle')) {
			const textElem = event.target.nextElementSibling;
			if (textElem) {
				event.target.style.display = 'none';
				textElem.style.display = 'inline';
			}

		}
	});
	
	// 좋아요표시
	
	async function handleLikeClick(event){
		const button = event.currentTarget;
		const commentId = button.getAttribute('data-id');
		const novelId = document.querySelector('#novelId').value;
		const episodeId = document.querySelector('#episodeId').value;
		const userId = document.querySelector('input#userId').value;
		console.log("버튼 클래스 적용 전:", button.classList.value);
		
		
		const url = `/novel/${novelId}/episode/${episodeId}/comment/${commentId}/like`;
		const data = { userId }
		
		try {
			const response = await axios.post(url, data);
			const {likeCount , likedByUser } = response.data;
			console.log("likedByUser:", likedByUser);
			// 카운트 숫자 갱신
			const countSpan = button.querySelector('span');
			countSpan.textContent = likeCount;
			
			// 좋아요 눌렀는지 여부
			if(likedByUser) {
				button.classList.add('liked');
			} else {
				button.classList.remove('liked');
			}
			console.log("버튼 클래스 적용 후:", button.classList.value);
		} catch(error) {
			console.log(error);
		}
	};
})