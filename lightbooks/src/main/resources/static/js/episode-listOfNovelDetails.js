/**
 * episode-listOfNovelDetails.html
 */
document.addEventListener('DOMContentLoaded', () => {
	
	//삭제 버튼
	document.querySelectorAll('.btn-delete').forEach(button => {
	        button.addEventListener('click', function () {
	            const episodeId = this.dataset.episodeId;
	            const novelId = this.dataset.novelId;
	            deleteEpisode(episodeId, novelId);
	        });
	 });
		

	function deleteEpisode(episodeId, novelId) {
		if (confirm('정말로 삭제하시겠습니까?')) {
			fetch(`/novel/${novelId}/episode/${episodeId}/delete`, {
				method: 'POST', // POST로 설정
			})
				.then(response => {
					if (response.ok) {
						alert('삭제되었습니다!');
						document.querySelector(`tr[data-episode-id="${episodeId}"]`).remove(); // 특정 행 삭제
					} else {
						alert('삭제 실패: 서버 오류가 발생했습니다.');
					}
				})
				.catch(error => console.error('삭제 중 오류 발생:', error));
		}
	}

});