/**
 * src/main/resources/static/js/episode-create.js
 */
	function toggleEpisodeNum() {
	    const categorySelect = document.getElementById('category');
	    const episodeNumInput = document.getElementById('episodeNum');
	
	    if (categorySelect.value === '0') { // '공지' 선택 시
	        episodeNumInput.disabled = true;
	        episodeNumInput.required = false;
	    } else { // '무료' 또는 '유료' 선택 시
	        episodeNumInput.disabled = false;
	        episodeNumInput.required = true;
	    }
	}
	
	function checkEpisodeNum() {
	    const novelId = document.querySelector('input[name="novelId"]').value;
	    const categorySelect = document.getElementById("category"); // 카테고리 선택
	    const episodeNum = document.getElementById("episodeNum").value;

	    // 공지사항일 경우 로직 실행하지 않음
	    if (categorySelect.value === '0') { // category가 '공지'인 경우
	        document.getElementById("episodeForm").submit(); // 바로 폼 제출
	        return; // 함수 종료
	    }
		
		// 회차 번호가 비어 있거나 공백인 경우 처리
		if (!episodeNum || episodeNum.trim() === "") {
			alert("회차 번호를 입력하세요."); // 사용자에게 경고 메시지
			document.getElementById("episodeNum").focus();
			return; // 함수 종료
		}

	    // 공지사항이 아닌 경우에만 회차 번호 확인
	    fetch(`/novel/${novelId}/episode/api/check-episode-num?novelId=${novelId}&episodeNum=${episodeNum}`)
	        .then(response => response.json())
	        .then(exists => {
	            if (exists) {
	                alert("이미 존재하는 회차번호입니다. 다른 번호를 입력하세요.");
	                document.getElementById("episodeNum").focus();
	            } else {
	                document.getElementById("episodeForm").submit();
	            }
	        })
	        .catch(error => {
	            console.error("Error:", error);
	            alert("서버와 통신 중 문제가 발생했습니다. 다시 시도해주세요.");
	        });
	}
	
	// DOMContentLoaded 이벤트에서 초기 상태 설정
	document.addEventListener('DOMContentLoaded', () => {
	    const categorySelect = document.getElementById('category');
	    if (categorySelect) {
	        toggleEpisodeNum(); // 초기 상태 설정
	        categorySelect.addEventListener('change', toggleEpisodeNum);
	    }
	});
