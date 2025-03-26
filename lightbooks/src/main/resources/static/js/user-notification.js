document.addEventListener('DOMContentLoaded', () => {
    console.log('user-notification.js');
	const userId = document.querySelector('span#userId').textContent;
	const btnAllDelete = document.querySelector('button#btnAllDelete');
	const btnAllReadAtList = document.querySelector('button#btnAllReadAtList');
	
	readNotification();
	
	function readNotification() {
		const divNotificationSection = document.querySelector('div#notificationSection');
		axios.get(`/user/notification/get?userId=${userId}`).then(response => {
			console.log(response);
			const notifications = response.data;
			
			if (response.data.length === 0) {
				divNotificationSection.innerHTML = 
				`
					<div class="mt-2">
						<div class="card card-body">
							<span>알림이 없습니다.</span>
						</div>
					</div>
				`
				return;
			}
			
			btnAllDelete.classList.remove('disabled');
			btnAllReadAtList.classList.remove('disabled');
			let html = ``;
			let target;
			notifications.forEach(notification => {
				let isRead = (notification.isRead === 0) ? '안읽음' : '읽음';
				let createdTime = formatDateTime(notification.createdTime);
				target = notification.target;
				html += 
				`
					<div class="mt-2">
					    <a class="text-decoration-none text-black notification-item" href="#" data-id="${notification.id}" data-isRead="${notification.isRead}">
					        <div class="card card-body notification-div" style="${notification.isRead == 1 ? 'background-color: #f0f0f0;' : ''}">
					            <div class="d-flex justify-content-between">
					                <div>${notification.msg}</div>
					                <button class="btn btn-danger btn-sm btnDelete" data-id="${notification.id}">삭제</button>
					            </div>
					            <div class="text-end">
					                ${createdTime}
					            </div>
					            <div>${isRead}</div>
					        </div>
					    </a>
					</div>
				`
			});
			divNotificationSection.innerHTML = html;
			
			const aNotificationItems = document.querySelectorAll('.notification-item');
			aNotificationItems.forEach(item => {
				item.addEventListener('click', (event) => {
					console.log(event.target.closest('.notification-item'));
					let notificationId = event.target.closest('.notification-item').dataset.id;
					let notificationIsRead = event.target.closest('.notification-item').dataset.isread;
					console.log(notificationIsRead);
					
					if (notificationIsRead == 0) {
						axios.put(`/user/notification/read?id=${notificationId}`).then(response => {
							console.log('읽음으로 변환');
							window.location.href = target;
						}).catch(error => {
							console.error(error);
						});	
					} else {
						window.location.href = target;
					}
				});
			});
			
			const btnDeletes = document.querySelectorAll('.btnDelete');
			btnDeletes.forEach(btn => {
				btn.addEventListener('click', (event) => {
					event.preventDefault(); // <a>의 기본 동작 차단 (페이지 이동 방지)
					event.stopPropagation(); // 부모 <a> 클릭 방지
					console.log(event.target.dataset.id);
					let notificationId = event.target.dataset.id;
					
					let result = confirm('선택한 알림을 삭제하시겠습니까?');
					if (!result) {
						console.log('삭제안함');
						return;
					}
					disableClicks();
					axios.delete(`/user/notification/delete?id=${notificationId}`).then(response => {
						alert('삭제하였습니다.');
						window.location.reload();
					}).catch(error => {
						console.log(error);
					});
				});
			});
			
			btnAllDelete.addEventListener('click', () => {
				let result = confirm('모든 알림을 삭제하시겠습니까?');
				if (!result) {
					console.log('삭제안함');
					return;
				}
				disableClicks();
				axios.delete(`/user/notification/delete/all?userId=${userId}`).then(response => {
					alert('삭제하였습니다.');
					window.location.reload();
				}).catch(error => {
					console.log(error);
				});
			});
			
			btnAllReadAtList.addEventListener('click', () => {
				let result = confirm('모든 알림을 읽으시겠습니까?');
				if (!result) {
					console.log('안읽음');
					return;
				}
				disableClicks();
				axios.put(`/user/notification/read/all?userId=${userId}`).then(response => {
					alert('모두 읽음처리 하였습니다.');
					window.location.reload();
				}).catch(error => {
					console.log(error);
				});
			});
		}).catch(error => {
			console.log(error);
		});
	}
	
	
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
	
	function formatDateTime(dateTimeString) {
	    const date = new Date(dateTimeString);
	    return date.toLocaleString('ko-KR', { 
	        year: 'numeric', 
	        month: '2-digit', 
	        day: '2-digit', 
	        hour: '2-digit', 
	        minute: '2-digit', 
	    });
	}
});