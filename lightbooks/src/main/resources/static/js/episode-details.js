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
});