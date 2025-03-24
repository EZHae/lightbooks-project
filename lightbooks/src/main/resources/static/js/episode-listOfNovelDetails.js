/**
 * 소설상세보기의 회차 리스트
 */

function updateButtonStates() { //지우면 안됨!!!!!!!!!!!!!!
        // 코인, 이용권의 잔액이 0일때 버튼 비활성화
        // DOM에서 값 가져오기
        const globalTicketCount = parseInt(document.getElementById("remainingGlobalTicket").innerText.trim()) || 0;
        const novelTicketCount = parseInt(document.getElementById("remainingIndividualTicket").innerText.trim()) || 0;
        const coinCount = parseInt(document.getElementById("remainingCoin").innerText.trim()) || 0;

        // 버튼 요소 가져오기
        const globalButton = document.getElementById("divBuyGlobal");
        const novelButton = document.getElementById("divBuyNovel");
        const coinButton = document.getElementById("divBuyCoin");

        // 조건에 따른 버튼 활성화/비활성화
        globalButton.classList.toggle("disabled", globalTicketCount === 0);
        globalButton.style.pointerEvents = globalTicketCount === 0 ? "none" : "auto";
        globalButton.style.opacity = globalTicketCount === 0 ? "0.5" : "1";

        novelButton.classList.toggle("disabled", novelTicketCount === 0);
        novelButton.style.pointerEvents = novelTicketCount === 0 ? "none" : "auto";
        novelButton.style.opacity = novelTicketCount === 0 ? "0.5" : "1";

        coinButton.classList.toggle("disabled", coinCount === 0);
        coinButton.style.pointerEvents = coinCount === 0 ? "none" : "auto";
        coinButton.style.opacity = coinCount === 0 ? "0.5" : "1";
    }

// 3. episode-item 클릭 처리 (에피소드 제목, 이전/다음 화 버튼)
function handleEpisodePurchase(element) { //지우면 안됨!!!!!!!!!!!!!
      const category = element.getAttribute("data-category");
      console.log("category:", category); 
      let isOwner;

      // 회차 제목인 경우
      if (element.classList.contains('episode-title')) {
         isOwner = element.closest("tr").dataset.isOwner === "true";
      }
      // 이전화/다음화 버튼일 경우
      else {
         isOwner = element.dataset.isOwner === "true";
      }
      
      console.log("isOwner:", isOwner);
	  
	  const episodeId = element.dataset.episodeId;
	  const novelId = element.dataset.novelId;

	  const updateAccessTime = () => {
	      axios.post(`/novel/${novelId}/episode/${episodeId}/access`)
	           .then(response => {
	                console.log("access_time 업데이트 성공:", response.data);
	            })
	            .catch(error => {
	                console.error("access_time 업데이트 실패:", error);
	            });
	      };
      
      if (isOwner || category === "0" || category === "1") {
              console.log("공지거나 무료회차 또는 작성자 또는 구매한 경우입니다. 상세 페이지로 이동합니다.");
			  updateAccessTime(); // access_time 업데이트
              window.location.href = element.getAttribute("href");
              return;
          }

        if (category === "2") {
         console.log("유료회차");
         const episodeId = element.dataset.episodeId;
         const episodeNum = element.getAttribute("data-episode-num");
         const inputNovelTitle = document.querySelector('input#inputNovelTitle').value;
         const novelId = element.dataset.novelId;
         console.log("element:", element); // 추가
         console.log("element.dataset.novelId:", element.dataset.novelId);
         console.log("novelId:",novelId);
         
         if (!inputNovelTitle) {
                 console.error("소설 제목 정보를 찾을 수 없습니다.");
                 return;
             }

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
						updateAccessTime(); // access_time 업데이트
                        window.location.href = element.getAttribute("href");
                    } else {
                        console.log("구매하지 않은 유료 회차입니다. 구매 팝업을 표시합니다.");
                        const modalElement = document.getElementById('buyEpisodeModal');
                        if (!modalElement) {
                            console.error("buyEpisodeModal 요소를 찾을 수 없습니다.");
                            return;
                        }

                  		console.log("bootstrap:", bootstrap); // 추가
						const buyEpisodeModal = new bootstrap.Modal(modalElement, {
							backdrop: 'true',
							keyboard: false
						});

						const closeModal = () => {
							console.log('Modal closed');
							buyEpisodeModal.hide();
						};

						
						const closeButton = modalElement.querySelector('.btn-close[data-bs-dismiss="modal"]');
						if (closeButton) {
							closeButton.addEventListener('click', closeModal);
						} else {
							console.error('buyEpisodeModal의 닫기 버튼을 찾을 수 없습니다.(header)');
						}

						const closeFooterButton = modalElement.querySelector('.btn-secondary[data-bs-dismiss="modal"]');
						if (closeFooterButton) {
							closeFooterButton.addEventListener('click', closeModal);
						} else {
							console.error(' buyEpisodeModal의 닫기버튼을 찾을 수 없습니다.(footer)');
						}

						document.querySelector('h5#modalTitle').textContent = `${inputNovelTitle}의 ${episodeNum}화`;

						const updateRemainingCoin = () => {
						    const nowCoinElement = document.querySelector('strong#nowCoin') || document.querySelector('span#remainingCoin');

						    console.log('nowCoinElement:', nowCoinElement);

						    if (nowCoinElement) {
						        const coinValue = nowCoinElement.textContent || "0";
						        console.log('strong#nowCoin textContent:', coinValue);

						        document.getElementById('remainingCoin').innerHTML = `<strong>${coinValue}</strong>`;
						        updateButtonStates();
						    } else {
						        console.error('코인 잔액 요소를 찾을 수 없습니다.');
						        document.getElementById('remainingCoin').innerHTML = '<strong>0</strong>';
							}
						};
						updateRemainingCoin(); // 코인 값 업데이트
						buyEpisodeModal.show(); // 모달 표시
						console.log("모달창 열렸음");

						updateButtonStates();

						document.querySelectorAll('a#selectProduct').forEach(btn => {
							btn.addEventListener('click', () => {
								const type = btn.dataset.type;
								console.log(type);
								const userId = document.querySelector('span#userId').textContent.trim();
								console.log(userId);
								const novelId = document.querySelector('input#novelId').value;
								console.log(novelId);
								const episodeId = element.dataset.episodeId;
								console.log(episodeId);
								const coin = Number(-100);
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

										const successModal = new bootstrap.Modal(document.getElementById('customSuccessModal'));
										successModal.show();//구매완료 창 띄우기

										const modalElement = document.getElementById('buyEpisodeModal');
										const buyEpisodeModal = bootstrap.Modal.getInstance(modalElement);
										buyEpisodeModal.hide(); //모달창 숨기기

										// 구매회차 페이지로 이동
										setTimeout(() => {
											window.location.href = element.getAttribute("href");
										}, 1000); // 1초 후 이동

										// 버튼 활성화 및 상태 복원
										btn.disabled = false;
										btn.textContent = '구매'; // 원래 버튼 텍스트로 복원

										// 페이지 새로고침 대신 필요한 부분 업데이트
										const episodeRow = document.querySelector(`a[data-episode-id="${episodeId}"]`);
										if (episodeRow) {
											episodeRow.setAttribute('data-category', '1');
										}
									})
									.catch(error => {
										console.error("구매 중 오류 발생:", error);
										btn.disabled = false;// 오류 발생 시 버튼 활성화 및 상태 복원
										btn.textContent = '구매';// 원래 버튼 텍스트로 복원
									});
							});
						});
					}
				});
	}
}

document.addEventListener("click", function(event) { //지우면 안됨!!!!!!!!!
	console.log("클릭된 요소:", event.target);
	const episodeItem = event.target.closest(".episode-item");
	if (episodeItem) {
		console.log('episodeItem:', episodeItem);
		event.preventDefault();
		handleEpisodePurchase(episodeItem);
	}
});

document.addEventListener('DOMContentLoaded', () => { //지우면 안됨!!!!!!!!!!!
	const pageHeader = document.querySelector('nav[th\\:fragment="pageHeader"]');
	    if (pageHeader) {
	        pageHeader.classList.add('d-none');
	    }
	
	console.log('[episode-list] 스크립트 로드됨');

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
			const targetAnchor = event.target.closest("a.ajax-link");
			if (targetAnchor) {
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
		});
	}
});