document.addEventListener('DOMContentLoaded', () => {
	console.log('waiting-payment.js');

	const btnChecks = document.querySelectorAll('button#btnCheck');
	btnChecks.forEach(btn => {
		btn.addEventListener('click', (event) => {
			console.log(event.target.dataset.id);

			const id = event.target.dataset.id;
			let result = confirm('해당 신청을 완료 처리 하겠습니까?');
			if (result) {
				disableClicks();
				axios.post(`/admin/waitingpayment/check?id=${id}`).then(response => {
					console.log(response);
					alert('처리되었습니다.');
					window.location.reload();
				}).catch(error => {
					console.error(error);
				})
			}
		});
	});

	// DB에 반영되는 사이 사용자가 아무동작 못하게 투명벽 설치
	function disableClicks() {
		let blocker = document.createElement("div");
		blocker.id = "blocker";
	
		// CSS 스타일 동적으로 추가
		blocker.style.position = "fixed";
		blocker.style.top = "0";
		blocker.style.left = "0";
		blocker.style.width = "100vw";
		blocker.style.height = "100vh";
		blocker.style.background = "rgba(0, 0, 0, 0)";
		blocker.style.zIndex = "9999";
	
		document.body.appendChild(blocker);
	}
});