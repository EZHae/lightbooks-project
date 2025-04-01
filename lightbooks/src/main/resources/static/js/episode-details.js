/**
 * episode/details.html
 */

document.addEventListener('DOMContentLoaded', () => {
    const menuBar = document.querySelector('.global-menu-bar'); // 상단 메뉴바
    const settingsIcon = document.createElement('div'); // 뷰어 설정 버튼 생성
    const settingsPopup = document.createElement('div'); // 뷰어 설정 팝업 생성
    let menuBarVisible = true; // 초기 상태: 메뉴 표시
    let popupVisible = false; // 초기 상태: 팝업 숨김
    let darkModeEnabled = false; // 다크모드 초기 상태

   // 메뉴바 표시/숨기기 로직
   document.addEventListener('click', (event) => {
      // 모달 창 내부 클릭 예외 처리
      if (event.target.closest('#buyEpisodeModal')) { // 모달 창 ID에 맞게 수정
         return;
      }

      // 팝업 클릭 이벤트와 충돌 방지
      if (event.target.closest('.viewer-settings-icon') || event.target.closest('.viewer-settings-popup')) {
         return;
      }

      // 메뉴바 토글
      if (menuBarVisible) {
         menuBar.style.display = 'none'; // 메뉴 숨기기
         menuBarVisible = false;
      } else {
         menuBar.style.display = 'flex'; // 메뉴 보이기
         menuBarVisible = true;
      }
   });

    // 뷰어 설정 버튼 추가
    settingsIcon.classList.add('viewer-settings-icon'); // 버튼에 클래스 추가
    settingsIcon.innerHTML = '<i class="bi bi-gear-fill" aria-hidden="true"></i>'; // 기어 아이콘
    document.body.appendChild(settingsIcon); // 버튼을 body에 추가

    // 뷰어 설정 팝업 추가
    settingsPopup.classList.add('viewer-settings-popup'); // 팝업에 클래스 추가
    settingsPopup.setAttribute('aria-hidden', 'true'); // 접근성을 위한 ARIA 속성 추가
    settingsPopup.innerHTML = `
        <label>테마 설정</label>
        <input type="radio" id="theme-light" name="theme" value="light" checked> <label for="theme-light">라이트 모드</label><br>
        <input type="radio" id="theme-dark" name="theme" value="dark"> <label for="theme-dark">다크 모드</label><br><br>
        <button id="apply-settings" class="btn btn-primary btn-sm">적용</button>
    `;
    document.body.appendChild(settingsPopup); // 팝업을 body에 추가

    // 뷰어 설정 버튼 클릭 이벤트
    settingsIcon.addEventListener('click', (event) => {
        event.stopPropagation(); // 상위 클릭 이벤트 방지
        popupVisible = !popupVisible; // 팝업 상태 토글
        settingsPopup.style.display = popupVisible ? 'block' : 'none';
        settingsPopup.setAttribute('aria-hidden', !popupVisible);
    });

    // 다크모드 설정 적용 이벤트
    const applySettingsButton = settingsPopup.querySelector('#apply-settings');
    applySettingsButton.addEventListener('click', () => {
        const selectedTheme = document.querySelector('input[name="theme"]:checked').value;

        if (selectedTheme === 'dark') {
            document.body.classList.add('dark-mode');
            darkModeEnabled = true;
        } else {
            document.body.classList.remove('dark-mode');
            darkModeEnabled = false;
        }

        // 팝업 닫기
        settingsPopup.style.display = 'none';
        popupVisible = false;
        settingsPopup.setAttribute('aria-hidden', 'true');
    });
	
	
	// 가로보기 
	const btnHorizontalView = document.getElementById('btnHorizontalView');
	const horizontalContainer = document.getElementById('horizontalViewContainer');
	const verticalContainer = document.getElementById('verticalContainer');
	let isHorizontal = false;

	btnHorizontalView.addEventListener('click', async () => {
		const novelId = document.querySelector('#novelId').value;
		const episodeId = document.querySelector('#episodeId').value;
		console.log("에피소드, 노벨 아이디:", episodeId, novelId);

		btnHorizontalView.disabled = true;
		try {
			if (!isHorizontal) {
				//가로보기 
				const response = await axios.get(`/novel/${novelId}/episode/${episodeId}/horizontalView`,
					{
						headers: { 'Accept': 'text/html' } // Accept: text/html 헤더 추가해야 Thymeleaf fragment가 HTML로 렌더링됨
					});
				
				horizontalContainer.innerHTML = response.data;
				horizontalContainer.style.display = 'block';
				horizontalContainer.style.height = '100vh';
				verticalContainer.style.display = 'none';
				
				let lastClickedNav = null;
				document.querySelector('.swiper-button-next').addEventListener('click', () => {
					lastClickedNav = 'next';
				});
				document.querySelector('.swiper-button-prev').addEventListener('click', () => {
					lastClickedNav = 'perv';
				})
				//  Swiper 초기화는 렌더링 이후에
				setTimeout(() => {
					let swiper = new Swiper(".mySwiper", {
						loop: false,
						speed: 0,
						pagination: {
							el: ".swiper-pagination",
							type: "progressbar",
						},
						navigation: {
							nextEl: ".swiper-button-next",
							prevEl: ".swiper-button-prev",
						},
						keyboard: {
					    	enabled: true, // 키보드 방향키는 여전히 사용 가능
					 	},	
						on: { //트리거 설정 
							slideChange: function(){
								const isLast = swiper.isEnd; // 마지막 슬라이드인가?
								
								// 마지막 슬라이드에서 오른쪽 화살표 누른 경우만
								if(isLast && this.clickedNav === 'next') {
									const nextBtn = document.getElementById('btnNextEpisode');
									if(nextBtn) nextBtn.click(); // 다음화 버튼 클릭 시뮬레이션
								}
							}
						}
					});
					
					console.log("스와이퍼 초기화 완료!");

					// 문장 단위 <p> 감싸기

					btnHorizontalView.textContent = '세로보기';
					isHorizontal = true;
				}, 100); // 살짝 늦게 실행
			} else {
				horizontalContainer.innerHTML = '';
				horizontalContainer.style.display = 'none';
				verticalContainer.style.display = 'block';
				btnHorizontalView.textContent = '가로보기';
				isHorizontal = false;
			}
		} catch (error) {
			console.log("가로보기 불러오기 실패", error);
		} finally {
			btnHorizontalView.disabled = false;
		}
	});
});