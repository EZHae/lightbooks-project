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

            // 3. episode-title 클릭 처리 (유료/무료 회차 및 작성자 확인)
            const episodeLink = event.target.closest(".episode-title");
            if (episodeLink) {
                event.preventDefault(); // 기본 동작 방지

                const category = episodeLink.getAttribute("data-category"); // 공지/무료/유료 구분
                const isOwner = episodeLink.closest("tr").getAttribute("data-is-owner") === "true"; // 작성자 여부

                if (isOwner) {
                    // 작성자인 경우 바로 상세 페이지로 이동
                    console.log("작성자입니다. 상세 페이지로 이동합니다.");
                    window.location.href = episodeLink.getAttribute("href");
                    return;
                }

                if (category === "0") {
                    // 공지사항인 경우 바로 이동
                    console.log("공지사항입니다.");
                    window.location.href = episodeLink.getAttribute("href");
                    return;
                }

                if (category === "1") {
                    // 무료 회차인 경우 바로 이동
                    console.log("무료 회차입니다.");
                    window.location.href = episodeLink.getAttribute("href");
                    return;
                }

                if (category === "2") {
                    // 유료 회차인 경우 구매 여부 확인
                    const episodeId = episodeLink.closest("tr").getAttribute("data-episode-id");
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

                            if (result === "PURCHASED" || result === "FREE") {
                                window.location.href = episodeLink.getAttribute("href");
                            } else {//구매하지 않았는데 유료회차를 클릭한 경우
								const ex = document.querySelector('strong#nowCoin').textContent;
								console.log(ex);
                                alert(`유료 회차입니다. 구매 후 이용해주세요.${ex}`);
								
                            }
                        })
                        .catch(error => console.error("오류 발생:", error));
                }
            }
        });
    }
});