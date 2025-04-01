/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
    // 텍스트 입력 글자 수 업데이트 함수
    function updateCharacterCount() {
        const textarea = document.getElementById('content'); // 텍스트 입력 필드 가져오기
        const countDisplay = document.getElementById('characterCount'); // 글자 수 표시할 요소 가져오기
        const currentLength = textarea.value.length; // 현재 입력된 글자 수
        const maxLength = textarea.getAttribute('maxlength'); // 최대 입력 가능한 글자 수
        countDisplay.textContent = `${currentLength}/${maxLength}`; // "현재 글자수/최대 글자수" 형식으로 표시
    }

    // textarea 요소에 입력 이벤트 리스너 추가
    const textarea = document.getElementById('content');
    if (textarea) {
        textarea.addEventListener('input', updateCharacterCount); // 'input' 이벤트 발생 시 updateCharacterCount 호출
    }

    // 페이지 로드 시 초기 글자 수 표시
    updateCharacterCount();
});