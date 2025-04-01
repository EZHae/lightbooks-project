/**
 * src/main/resources/static/js/episode-create.js
 */
function toggleEpisodeNum() {
    const categorySelect = document.getElementById('category');
    const episodeNumInput = document.getElementById('episodeNum');
    const calculatedEpisodeNum = document.getElementById('calculatedEpisodeNum');
	

    if (categorySelect.value === '0') { // '공지' 선택 시
        episodeNumInput.disabled = true;
        episodeNumInput.required = false;
        episodeNumInput.value = null;
    } else { // '무료' 또는 '유료' 선택 시
        episodeNumInput.disabled = false;
        episodeNumInput.required = true;
        if (episodeNumInput.value.trim() === '') { // 입력 필드가 비어 있으면
            if (calculatedEpisodeNum.value) {
                episodeNumInput.value = calculatedEpisodeNum.value; // 계산된 값 사용
            }
        }
    }
}

function validateEpisodeNum(input) {
    const value = input.value;
    const regex = /^[0-9]*$/; // 숫자만 허용하는 정규식

    if (value.trim() !== "" && !regex.test(value)) {
        alert('숫자만 입력해주세요.');
        input.value = value.replace(/[^0-9]/g, ''); // 숫자 이외의 문자 제거
    }
}
	
function checkEpisodeNum() {
    const novelId = document.querySelector('input[name="novelId"]').value;
    const categorySelect = document.getElementById("category"); // 카테고리 선택
    const episodeNum = document.getElementById("episodeNum").value;
    const calculatedEpisodeNum = document.getElementById('calculatedEpisodeNum').value;

    if (categorySelect.value === '0') { // 공지사항일 경우
        document.getElementById("episodeForm").submit(); // 바로 폼 제출
        return; // 함수 종료
    }
	btnCreated.disabled = true;
    // 회차 번호가 비어있으면 자동 할당된 값(calculatedEpisodeNum)으로 대체
    const episodeNumToCheck = episodeNum.trim() === '' ? calculatedEpisodeNum : episodeNum;
    if (episodeNum.trim() === '') {
        document.getElementById("episodeNum").value = calculatedEpisodeNum;
    }

    // 회차 번호가 비어있지 않은 경우에만 서버에 중복 확인 요청
    if (episodeNumToCheck) {
        fetch(`/novel/${novelId}/episode/api/check-episode-num?novelId=${novelId}&episodeNum=${episodeNumToCheck}`)
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
    } else {
        // 회차 번호가 비어있는 경우 바로 폼 제출
        document.getElementById("episodeForm").submit();
		btnCreated.disabled = false;
    } 
}

// DOMContentLoaded 이벤트에서 초기 상태 설정
document.addEventListener('DOMContentLoaded', function() {
    const categorySelect = document.getElementById('category');
    categorySelect.addEventListener('change', toggleEpisodeNum);
    toggleEpisodeNum();
	
	
	function handleSubmit(){
		document.getElementById('createBtn').disabled = ture;
		return true;
	}
});

// 폼 제출 시 episodeNum 입력 필드가 비어 있으면 calculatedEpisodeNum 값으로 설정
document.querySelector('form').addEventListener('submit', function() {
    const episodeNumInput = document.getElementById('episodeNum');
    const calculatedEpisodeNum = document.getElementById('calculatedEpisodeNum');

    if (episodeNumInput.value.trim() === '') {
        episodeNumInput.value = calculatedEpisodeNum.value;
    }
});