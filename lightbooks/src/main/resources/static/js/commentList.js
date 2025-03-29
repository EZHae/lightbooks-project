/**
 * 
 */


let currentPageNo = 0;
let isLastPage = false;
let isLoading = false;
const renderedCommentIds = new Set();

document.addEventListener('DOMContentLoaded', () => {
	
	getAllComments(0, true);
	
	// 스크롤!!
	window.addEventListener('scroll', () => {
	  if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 200) {
	    getAllComments(currentPageNo + 1);
	  }
	});
	
	async function getAllComments(pageNo = 0, reset = false) {
	  if (isLoading || isLastPage) return;
	  isLoading = true;

	  const novelId = document.querySelector('input#novelId')?.value;
	  const uri = `/novel/${novelId}/comments?p=${pageNo}`;

	  try {
	    const { data } = await axios.get(uri);
	    console.log(data);

	    currentPageNo = data.page.number;
	    if (data.page && data.page.totalPages <= currentPageNo + 1) {
	      isLastPage = true;
	    }

	    makeCommentElements(data, reset);
	  } catch (error) {
	    console.error("댓글 불러오기 실패", error);
	  } finally {
	    isLoading = false;
	  }
	}

	function makeCommentElements(data, reset = false) {
	  const commentList = document.getElementById('commentList');
	  const comments = data.content;
	  console.log("받은 댓글 목록:", data.content);
	  if (reset) {
	    commentList.innerHTML = '';
	    renderedCommentIds.clear();
	  }

	  if (!comments || comments.length === 0) {
	    if (reset) {
	      commentList.innerHTML = '<p>아직 댓글이 없습니다.</p>';
	    }
	    return;
	  }

	  let htmlStr = '';
	  comments.forEach(comment => {
	    if (renderedCommentIds.has(comment.commentId)) return;
	    renderedCommentIds.add(comment.commentId);

	    htmlStr += `
	      <div class="commentBox">
	        <div class="commentHeader">
	          <span class="comment-nickname">${comment.nickname}</span>
	          <span class="comment-date">${new Date(comment.modifiedTime).toLocaleString('ko-KR')}</span>
	        </div>
	        <div class="commentBody" data-spoiler="${comment.spoiler}">
	          ${
	            comment.spoiler === 1
	              ? `<span class="spoiler-toggle">주의! 스포일러입니다. (클릭해서 보기)</span>
	                 <span class="spoiler-text" style="display:none;">${comment.text}</span>`
	              : `<span class="comment-text">${comment.text}</span>`
	          }
	        </div>
			<div>
			<div class="comment-title">[${comment.episodeNum}화] - ${comment.episodeTitle}</div>
			</div>
			
	        <div class="commentFooter">
	          <div>
	            <button class="like-button btnLike ${comment.likedByUser ? 'liked' : ''}" data-id="${comment.commentId}">
	              ❤️ <span>${comment.likeCount}</span>
	            </button>
	          </div>
	        </div>
	      </div>
	    `;
	  });

		commentList.insertAdjacentHTML('beforeend', htmlStr);

		// 좋아요
		const btnLikes = document.querySelectorAll('button.btnLike');
		btnLikes.forEach(btn => btn.addEventListener('click', handleLikeClick));
	}

	// 좋아요 처리
	async function handleLikeClick(event) {
		const button = event.currentTarget;
		const commentId = button.getAttribute('data-id');
		const novelId = document.querySelector('#novelId').value;
		const userId = document.querySelector('input#userId')?.value;

		const url = `/novel/comment/${commentId}/like`;
		const data = { userId };

		// 중복 요청 방지
		if (button.dataset.processing === 'true') return;
		button.dataset.processing = 'true';
		try {
			const response = await axios.post(url, data);
			const { likeCount, likedByUser } = response.data;

			const countSpan = button.querySelector('span');
			countSpan.textContent = likeCount;

			if (likedByUser) {
				button.classList.add('liked');
			} else {
				button.classList.remove('liked');
			}
		} catch (error) {
			console.error(error);
		} finally {
			button.dataset.processing = 'false';
		}
	}

	// 스포일러 보기
	document.addEventListener('click', function(event) {
		if (event.target.classList.contains('spoiler-toggle')) {
			const textElem = event.target.nextElementSibling;
			if (textElem) {
				event.target.style.display = 'none';
				textElem.style.display = 'inline';
			}
		}
	});
});