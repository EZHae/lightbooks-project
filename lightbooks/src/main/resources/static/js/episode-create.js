/**
 * src/main/resources/static/js/episode-create.js
 */
document.addEventListener('DOMContentLoaded', () => {

	// novel의 grade(무료/유료) 상태에 따라서 episode의 category가 활성화/비활성화
    function toggleEpisodeNum() {
        const categorySelect = document.getElementById('category');
        const episodeNumInput = document.getElementById('episodeNum');

        if (categorySelect.value === '0') {
            episodeNumInput.disabled = true;
            episodeNumInput.required = false;
        } else {
            episodeNumInput.disabled = false;
            episodeNumInput.required = true;
        }
    }

    toggleEpisodeNum(); // 초기 상태 설정

    const categorySelect = document.getElementById('category');
    if (categorySelect) {
        categorySelect.addEventListener('change', toggleEpisodeNum);
    }
});