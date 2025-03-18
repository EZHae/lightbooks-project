/**
 * 소설상세보기의 회차 리스트
 */
document.addEventListener('DOMContentLoaded', () => {
  console.log('[episode-list] 스크립트 로드됨');

  const container = document.getElementById("episodeListContainer");
  if (container) {
    container.addEventListener("click", function (event) {
      // 1. 삭제 버튼 클릭 처리
      const deleteBtn = event.target.closest(".btn-delete");
      if (deleteBtn) {
        console.log("[삭제 버튼] 클릭됨:", deleteBtn);
        
        // 중복 이벤트 처리 방지
        event.preventDefault();
        event.stopImmediatePropagation();
  
        const episodeId = deleteBtn.dataset.episodeId;
        const novelId = deleteBtn.dataset.novelId;
        
        if (confirm('정말로 삭제하시겠습니까?')) {
          fetch(`/novel/${novelId}/episode/${episodeId}/delete`, {
            method: 'POST'
          })
          .then(response => {
            if (response.ok) {
              alert('삭제되었습니다!');
              const row = document.querySelector(`tr[data-episode-id="${episodeId}"]`);
              if (row) row.remove();
            } else {
              alert('삭제 실패: 서버 오류가 발생했습니다.');
            }
          })
          .catch(error => console.error('삭제 중 오류 발생:', error));
        }
        return;
      }
      
      // 2. AJAX 링크 처리: ajax-link 클래스가 있는 <a> 태그
      const targetAnchor = event.target.closest("a");
      if (targetAnchor && targetAnchor.classList.contains("ajax-link")) {
        event.preventDefault();
        const url = targetAnchor.getAttribute("href");
        if (!url) return;
  
        console.log("[AJAX 링크] 클릭됨, URL:", url);
        
        fetch(url, {
          headers: {
            "X-Requested-With": "XMLHttpRequest"
          }
        })
          .then(response => {
            if (!response.ok) {
              throw new Error("네트워크 오류: " + response.statusText);
            }
            return response.text();
          })
          .then(html => {
            container.innerHTML = html;
            history.pushState(null, "", url);//브라우저의 URL(주소창에 표시되는 URL)을 변경하지만 페이지 전체를 다시 로드 안하게
          })
          .catch(error => console.error("회차 리스트 업데이트 중 오류 발생:", error));
      }
    });
  }
});

