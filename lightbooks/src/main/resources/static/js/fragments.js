document.addEventListener('DOMContentLoaded', () => {
    console.log('fragments.js');
	
	// 페이지가 로드된 후 실행되는 코드
	// 오류메세지 alert 한 후 세션 지우는 요청보냄.
	window.onload = function() {
		const errorMessage = document.querySelector('span#errorMessage').textContent;
		if (errorMessage === '아이디 또는 비밀번호를 확인하세요.') {
			alert(`${errorMessage}`);
			axios.post('../user/removeSession').then().catch(error);
		}
	};

	
	if (document.querySelector('button#profileDropdown')) {
		let userId = document.querySelector('span#userId').textContent;
		WalletRefresh(userId);
		
		const btnCoinRefresh = document.querySelector('button#coinRefresh');
		btnCoinRefresh.addEventListener('click', () => {
			WalletRefresh(userId);
			/*
			let uri = `/user/getUserWallet?userId=${userId}&type=c`;
			axios.get(uri).then(response => {
				let coin = response.data;
				console.log(coin);
				let strongNowCoin = document.querySelector('strong#nowCoin');
				strongNowCoin.textContent = coin;
			}).catch(error => {
				console.error(error);
			});
			*/
		});
		
		const btnMileageRefresh = document.querySelector('button#mileageRefresh');
		btnMileageRefresh.addEventListener('click', () => {
			WalletRefresh(userId);
			/*
			let uri = `/user/getUserWallet?userId=${userId}&type=m`;
			axios.get(uri).then(response => {
				let mileage = response.data;
				console.log(mileage);
				let strongNowMileage = document.querySelector('strong#nowMileage');
				strongNowMileage.textContent = mileage;
			}).catch(error => {
				console.error(error);
			});
			*/
		});
	}
	
	function WalletRefresh(userId) {
		console.log('WalletRefresh()');
		/*
		let uriC = `/user/getUserWallet?userId=${userId}&type=c`;
		axios.get(uriC).then(response => {
			let coin = response.data;
			console.log(coin);
			let strongNowCoin = document.querySelector('strong#nowCoin');
			strongNowCoin.textContent = coin;
		}).catch(error => {
			console.error(error);
		});
		
		let uriM = `/user/getUserWallet?userId=${userId}&type=m`;
		axios.get(uriM).then(response => {
			let mileage = response.data;
			console.log(mileage);
			let strongNowMileage = document.querySelector('strong#nowMileage');
			strongNowMileage.textContent = mileage;
		}).catch(error => {
			console.error(error);
		});
		*/
		
		const url = `/user/getUserWallets?userId=${userId}`;
		axios.get(url).then(response => {
			console.log(response.data);
			let coin = response.data.coin;
			let mileage = response.data.mileage;
			
			let strongNowCoin = document.querySelector('strong#nowCoin');
			strongNowCoin.textContent = coin;
			
			let strongNowMileage = document.querySelector('strong#nowMileage');
			strongNowMileage.textContent = mileage;
		}).catch(error => {
			console.error(error);
		})
	}
	
	
	// 검색창에 공백 입력할 때 
	const btnSearch = document.querySelector('button#btnSearch')
		
	btnSearch.addEventListener('click', validateSearch)
	// 검색 버튼 클릭 시 공백만 입력된 경우 검색 방지
	function validateSearch(event) {
	    let keyword = document.getElementById("searchKeyword").value.trim();
	    if (keyword.length === 0) {
	        alert("검색어를 입력해주세요!"); // 사용자에게 알림
			event.preventDefault(); // 🔹 폼 전송 막기 (필수)
	        return false; // 검색 요청 차단
	    }
	    return true;
	}
	
	// 로그인 상태면 안읽은 알림과 그 개수 가져오기
	if (document.querySelector('span#userId')) {
		readNotificationAtFragment();
		
		const userId = document.querySelector('span#userId').textContent;
		btnAllRead.addEventListener('click', () => {
			axios.put(`/user/notification/read/all?userId=${userId}`).then(response => {
				console.log(response);
				readNotificationAtFragment()
			}).catch(error => {
				console.error(error);
			});
		});
	}
	
	function readNotificationAtFragment() {
		const userId = document.querySelector('span#userId').textContent;
		const spanCountNoReadNotification = document.querySelector('span#countNoReadNotification');
		const btnAllRead = document.querySelector('button#btnAllRead');

		axios.get(`/user/notification/fragment?userId=${userId}`).then(response => {
			console.log(response);
			const notifications = response.data.notifications;
			
			if (response.data.count === 0) {
				if (!spanCountNoReadNotification.className.match('d-none')) {
					spanCountNoReadNotification.classList.add('d-none');	
				}
				const liNoReadNotification = document.querySelector('li#noReadNotification');
				liNoReadNotification.innerHTML = 
				`
					<div class="m-1 mt-2">
						<div class="card card-body">
							<span>새로운 알림이 없습니다.</span>
						</div>
					</div>
				`;
				btnAllRead.classList.add('disabled');
			}
			
			if (response.data.count > 0) {
				spanCountNoReadNotification.textContent = response.data.count;
				if (spanCountNoReadNotification.className.match('d-none')) {
					spanCountNoReadNotification.classList.remove('d-none');	
				}
				btnAllRead.classList.remove('disabled');			
				
				let html = '';
				const liNoReadNotification = document.querySelector('li#noReadNotification');
				let target;
				notifications.forEach(notification => {
					let createdTime = formatDateTime(notification.createdTime);
					target = notification.target;
					html += 
					`	
						<div class="m-1 mt-2">
							<a class="text-decoration-none text-black notification-item" href="#" data-id="${notification.id}">
								<div class="card card-body notification-div">
									<div>
										${notification.msg}
									</div><br>
									<div class="text-end">
										${createdTime}
									</div>
								</div>
							</a>
						</div>

					`
				});
				liNoReadNotification.innerHTML = html;
				
				const aNotificationItems = document.querySelectorAll('.notification-item');
				aNotificationItems.forEach(item => {
					item.addEventListener('click', (event) => {
						console.log(event.target.closest('.notification-item'));
						let notificationId = event.target.closest('.notification-item').dataset.id;
						
						axios.put(`/user/notification/read?id=${notificationId}`).then(response => {
							console.log('읽음으로 변환');
							window.location.href = target;
						}).catch(error => {
							console.error(error);
						});	
					});
				});
			}
			
		}).catch(error => {
			console.log(error);
		});
	}
	
	// ============ 모달 ============
	const navbar = document.querySelector('.navbar');
	const modalEl = document.getElementById('signModal');
	const navCollapse = document.getElementById('navbarSupportedContent');
	// collapse 토글 버튼 이벤트 감지
	const toggler = document.querySelector('.navbar-toggler');
	
	toggler.addEventListener('click', () => {
		navbar.style.position = 'sticky';
		navbar.style.zIndex = '1200';
	});
	
	modalEl.addEventListener('show.bs.modal', () => {
	  // collapse가 열려 있으면 닫고 숨김
	  if (navCollapse.classList.contains('show')) {
	    const collapseInstance = bootstrap.Collapse.getInstance(navCollapse);
	    if (collapseInstance) {
	      collapseInstance.hide(); // Bootstrap 방식으로 닫기
	    }
	  }
	  
	  navbar.style.position = 'static';
	  navbar.style.zIndex = '1';
	});
	
	// 모달 닫힐 때 다시 복구
	modalEl.addEventListener('hidden.bs.modal', () => {
		navbar.style.position = 'sticky';
		navbar.style.zIndex = '1200'
	});

	window.addEventListener('scroll', () => {
		const pageHeader = document.getElementById('pageHeader');
		const isMyworksPage = document.body.classList.contains('myworks-page');
		const myworksNavbar = document.querySelector('.myworksNavbar');
		
		// pageHeader가 존재할 때만 처리
		if (pageHeader) {
		  if (isMyworksPage) {
		    pageHeader.style.boxShadow = 'none';
		  } else {
		    pageHeader.style.boxShadow = window.scrollY > 10 
		      ? '0 4px 8px rgba(0, 0, 0, 0.05)' 
		      : 'none';
		  }
		}

		// myworks 페이지 전용 헤더에 그림자 적용
		if (isMyworksPage && myworksNavbar) {
		  myworksNavbar.style.boxShadow = window.scrollY > 10 
		    ? '0 4px 8px rgba(0, 0, 0, 0.08)' 
		    : 'none';
		}
	});
	
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