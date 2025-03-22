/**
 * 소설상세보기의 회차 리스트
 */
document.addEventListener('DOMContentLoaded', () => {
   console.log('[episode-list] 스크립트 로드됨');
   
   function updateButtonStates() { //코인, 이용권의 잔액이 0일때 버튼 비활성화
       // DOM에서 값 가져오기
       const globalTicketCount = parseInt(document.getElementById("remainingGlobalTicket").innerText.trim()) || 0;
       const novelTicketCount = parseInt(document.getElementById("remainingIndividualTicket").innerText.trim()) || 0;
       const coinCount = parseInt(document.getElementById("remainingCoin").innerText.trim()) || 0;

       // 버튼 요소 가져오기
       const globalButton = document.getElementById("divBuyGlobal");
       const novelButton = document.getElementById("divBuyNovel");
       const coinButton = document.getElementById("divBuyCoin");

       // 조건에 따른 버튼 활성화/비활성화
       if (globalTicketCount === 0) {
           globalButton.classList.add("disabled");
           globalButton.style.pointerEvents = "none"; // 클릭 방지
           globalButton.style.opacity = "0.5"; // 흐리게 표시
       } else {
           globalButton.classList.remove("disabled");
           globalButton.style.pointerEvents = "auto";
           globalButton.style.opacity = "1";
       }

       if (novelTicketCount === 0) {
           novelButton.classList.add("disabled");
           novelButton.style.pointerEvents = "none";
           novelButton.style.opacity = "0.5";
       } else {
           novelButton.classList.remove("disabled");
           novelButton.style.pointerEvents = "auto";
           novelButton.style.opacity = "1";
       }

       if (coinCount === 0) {
           coinButton.classList.add("disabled");
           coinButton.style.pointerEvents = "none";
           coinButton.style.opacity = "0.5";
       } else {
           coinButton.classList.remove("disabled");
           coinButton.style.pointerEvents = "auto";
           coinButton.style.opacity = "1";
       }
   }

   const container = document.getElementById("episodeListContainer");
   if (container) {
      container.addEventListener("click", function(event) {
         // 1. 삭제 버튼 클릭 처리
         const deleteBtn = event.target.closest(".btn-delete");
         if (deleteBtn) {
            console.log("[삭제 버튼] 클릭됨:", deleteBtn);

            event.preventDefault();
            event.stopImmediatePropagation();

            const episodeId = deleteBtn.dataset.episodeId;
            const novelId = deleteBtn.dataset.novelId;

            if (confirm('정말로 삭제하시겠습니까?')) {
               fetch(`/novel/${novelId}/episode/${episodeId}/delete`, { method: 'POST' })
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

         // 2. AJAX 링크 처리
         const targetAnchor = event.target.closest("a");
         if (targetAnchor && targetAnchor.classList.contains("ajax-link")) {
            event.preventDefault();
            const url = targetAnchor.getAttribute("href");
            if (!url) return;

            console.log("[AJAX 링크] 클릭됨, URL:", url);

            fetch(url, { headers: { "X-Requested-With": "XMLHttpRequest" } })
               .then(response => {
                  if (!response.ok) {
                     throw new Error("네트워크 오류: " + response.statusText);
                  }
                  return response.text();
               })
               .then(html => {
                  container.innerHTML = html;
                  history.pushState(null, "", url);
               })
               .catch(error => console.error("회차 리스트 업데이트 중 오류 발생:", error));
         }

         // 3. episode-title 클릭 처리
         const episodeLink = event.target.closest(".episode-title");
         console.log('확인', episodeLink);
         if (episodeLink) {
            event.preventDefault();

            const category = episodeLink.getAttribute("data-category");
            const isOwner = episodeLink.closest("tr").getAttribute("data-is-owner") === "true";

            if (isOwner || category === "0" || category === "1") {
               console.log("공지거나 무료회차 또는 작성자 또는 구매한 경우입니다. 상세 페이지로 이동합니다.");
               window.location.href = episodeLink.getAttribute("href");
               return;
            }

            if (category === "2") {
               console.log("유료회차");
               const episodeId = episodeLink.closest("tr").getAttribute("data-episode-id");
               const episodeNum = episodeLink.getAttribute("data-episode-num");
               const inputNovelTitle = document.querySelector('input#inputNovelTitle').value;
               const novelId = episodeLink.closest("tr").getAttribute("data-novel-id");

               fetch(`/novel/${novelId}/episode/${episodeId}/check`)
                  .then(response => {
                     if (response.status === 401) {
                        alert("로그인이 필요합니다.");
                        return null;
                     }
                     return response.text();
                  })
                  .then(result => {
                     if (!result) return;
                     console.log('result', result);

                     if (result === "PURCHASED" || result === "FREE") {
                        console.log("구매된 회차입니다. 이동합니다.");
                        window.location.href = episodeLink.getAttribute("href");
                     } else {
                        console.log("구매하지 않은 유료 회차입니다. 구매 팝업을 표시합니다.");
                        const modalElement = document.getElementById('buyEpisodeModal');
                        if (!modalElement) {
                           console.error("buyEpisodeModal 요소를 찾을 수 없습니다.");
                           return;
                        }

                        const buyEpisodeModal = new bootstrap.Modal(modalElement, {
                           backdrop: 'static',
                           keyboard: false
                        });

                        document.querySelector('h5#modalTitle').textContent = `${inputNovelTitle}의 ${episodeNum}화`;
                        document.getElementById('remainingCoin').innerHTML = `<strong>${document.querySelector('strong#nowCoin').textContent}</strong>`;

                        buyEpisodeModal.show();
                        console.log("모달창 열렸음");
						
						// 코인 값을 설정하고 버튼 상태를 업데이트
						document.getElementById('remainingCoin').innerHTML = `<strong>${document.querySelector('strong#nowCoin').textContent}</strong>`;

						// 버튼 상태 업데이트 함수 호출
						updateButtonStates();

                        document.querySelectorAll('a#selectProduct').forEach(btn => {
                           btn.addEventListener('click', () => {
                              let type = btn.dataset.type;
                              console.log(type);
                              let userId = document.querySelector('span#userId').textContent.trim();
                              console.log(userId);
                              let novelId = document.querySelector('input#novelId').value;
                              console.log(novelId);
                              const episodeId = episodeLink.closest("tr").getAttribute("data-episode-id");
                              console.log(episodeId);
                              let coin = Number(-100);
                              console.log(coin);

                              if (!userId || !novelId || !episodeId) {
                                 console.error("필수 정보(userId, novelId, episodeId)가 누락되었습니다.");
                                 return;
                              }

                              const data = { userId, novelId, episodeId, type, coin };

                              // 버튼 광클릭시 중복 구매 방지
                              btn.disabled = true;
                              btn.textContent = '구매 중...';

                              axios.post(`/novel/${novelId}/episode/buy`, data)
                                 .then(response => {
                                    console.log("구매 성공:", response.data);
                                    window.alert("구매가 완료되었습니다.");
                                    const modalElement = document.getElementById('buyEpisodeModal');
                                    const buyEpisodeModal = bootstrap.Modal.getInstance(modalElement);
                                    buyEpisodeModal.hide();

                                    // 버튼 활성화 및 상태 복원
                                    btn.disabled = false;
                                    btn.textContent = '구매'; // 원래 버튼 텍스트로 복원

                                    // 페이지 새로고침 대신 필요한 부분 업데이트 (이전 답변 참고)
                                    const episodeRow = document.querySelector(`tr[data-episode-id="${episodeId}"]`);
                                    if (episodeRow) {
                                       episodeRow.querySelector('.episode-title').setAttribute('data-category', '1');
                                    }
                                 })
                                 .catch(error => {
                                    console.error("구매 중 오류 발생:", error);
                                    // 오류 발생 시 버튼 활성화 및 상태 복원
                                    btn.disabled = false;
                                    btn.textContent = '구매'; // 원래 버튼 텍스트로 복원
                                 });
                           });
                        });
                     }
                  });
            }
         }
      });
   }
});