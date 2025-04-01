document.addEventListener('DOMContentLoaded', () => {
	console.log('admin-chat.js');
	
	getChatRoom();
	
	function getChatRoom() {
		axios.get('/admin/chat/read/chatroomlist').then(response => {
			console.log(response.data);
			let chatRoomList = response.data;
			renderChatRoom(chatRoomList);
		}).catch(error => {
			console.log(error);
		});
	}
	
	function renderChatRoom(chatRoomList) {
		const adminChatBox = document.querySelector('div#adminChatBox');
		if (chatRoomList.length == 0) {
			adminChatBox.innerHTML = 
			`
				<div>
					<span>열린 채팅방이 없습니다.</span>
				</div>
			`;
			return;
		}
		
		adminChatBox.innerHTML = ``;
		let html = ``;
		chatRoomList.forEach(item => {
			let isReadCount = (item.isReadCount > 0) 
				? `<span id="isReadCountFor${item.id}" class="badge bg-danger ms-2">${item.isReadCount}</span>` 
				: '';  // isReadCount가 0이면 빈 문자열
			let sendTime = (item.sendTime == null) ? '보낸 메세지 없음' : formatDateTime(item.sendTime);
			html += 
			`
				<div class="chatRoomItem border-bottom" data-id=${item.id} data-userid=${item.userId} data-loginid=${item.userLoginId}>
					<div>
						<span class="fs-5 fw-bold">${item.userLoginId}님의 채팅 문의</span>
						${isReadCount}
					</div>
					<span>id = ${item.id}</span>
					<div>
						<span class="text-secondary">${sendTime}</span>
					</div>
				</div>
			`;
		});
		adminChatBox.innerHTML = html;
		const chatRoomItems = document.querySelectorAll('div.chatRoomItem');
		chatRoomItems.forEach(item => {
			item.addEventListener('click', (event) => {
				const inputAdminMessage = document.querySelector('input#admin-message');
				const btnAdminSendMessage = document.querySelector('button#admin-btnSendMessage');
				
				if (inputAdminMessage.hasAttribute('readonly')) {
					inputAdminMessage.removeAttribute('readonly');
				}
				if (btnAdminSendMessage.className.match('disabled')) {
					btnAdminSendMessage.classList.remove('disabled');
				}
				
				
				let chatRoomId = event.currentTarget.dataset.id;
				let sendUserId = event.currentTarget.dataset.userid;
				let sendLoginId = event.currentTarget.dataset.loginid;
				document.querySelector('div#chatData').dataset.chatroomid = chatRoomId;
				document.querySelector('div#chatData').dataset.sendid = sendUserId;
				document.querySelector('div#chatData').dataset.sendloginid = sendLoginId;
				document.querySelector('span#chatRoomName').textContent = `${sendLoginId}님의 채팅 문의`
				getChatMessage(chatRoomId);
				if (document.querySelector(`span#isReadCountFor${chatRoomId}`)) {
					document.querySelector(`span#isReadCountFor${chatRoomId}`).remove();	
				}
			});
		});
	}
	
	
	
	
	function getChatMessage(chatRoomId) {
		const adminChatBody = document.querySelector('div#admin-chat-body');
		adminChatBody.innerHTML =
		`
			<div class="d-flex justify-content-center">
				<div class="spinner-border text-center" role="status">
					<span class="visually-hidden"></span>
				</div>
				<span class="ms-2">채팅목록 불러오는중...</span>
			</div>
		`;
		axios.get(`/admin/chat/read/chatmessage?chatRoomId=${chatRoomId}`).then(response => {
			let chatMessageList = response.data;
			console.log(chatMessageList);
			renderChatMessage(chatMessageList)
		}).catch(error => {
			console.log(error);
		});
	}
	
	function renderChatMessage(chatMessageList) {
		const adminChatBody = document.querySelector('div#admin-chat-body');
		if (chatMessageList.length == 0) {
			adminChatBody.innerHTML =
			`
				<div class="d-flex justify-content-center">
					<span>채팅 내역이 없습니다.<span>
				</div>
			`;
			return;
		}
		
		let html = ``;
		chatMessageList.forEach(item => {
			let sendTime = formatDateTime(item.sendTime);
			let messageClass = (item.senderLoginId === 'admin') ? 'admin' : 'user';
			html +=
			`
				<div class="adminChatMessageItem ${messageClass}">
					<div>
						<span class="fs-5">${item.content}</span>
					</div>
					<div class="text-secondary text-end">
						<span>${sendTime}</span>
					</div>
				</div>
			`;
		});
		adminChatBody.innerHTML = html;
		
		// 메시지가 렌더링된 후 가장 아래로 스크롤
		adminChatBody.scrollTop = adminChatBody.scrollHeight;
	}
	
	////////////////////////////////
	
	let socket = new SockJS('/chat');
	let stompClient = Stomp.over(socket);

	console.log(socket);
	console.log(stompClient);

	// 연결 설정
	stompClient.connect({}, function (frame) {
	    console.log("WebSocket 연결 성공:", frame);

	    // 구독 예제 (예: "/topic/messages" 경로)
	    stompClient.subscribe('/topic/messages', function (message) {
	        console.log("수신된 메시지:", JSON.parse(message.body));
			let receivedMessage = JSON.parse(message.body);
			let chatDatas = document.querySelector('div#chatData');
			if (receivedMessage.senderLoginId == chatDatas.dataset.sendloginid) {
	            showMessage(receivedMessage);
	        }
			reloadChatRoom();
	    });
		
		stompClient.subscribe('/topic/admin', function (message) {
			reloadChatRoom();
		});
	});
	
	// 메시지 전송 함수
	function sendMessage() {
		let chatData = document.querySelector('div#chatData');
		console.log(chatData);
		let senderId = 16;
		let senderLoginId = 'admin';
		let receiverId = chatData.dataset.sendid;
		let receiverLoginId = chatData.dataset.sendloginid;
		let chatRoomId = chatData.dataset.chatroomid;
		let content = document.querySelector("input#admin-message").value;
		let isRead = 0;
		
	    if (content.trim() !== "") {
			// 메세지 객체 만들기
			let message = { senderId, senderLoginId, receiverId, receiverLoginId, chatRoomId, content, isRead}
			
			// 메세지 전송
	        stompClient.send('/app/sendMessage', {}, JSON.stringify(message));
			
			// 보낸 메세지 채팅창에 추가
			showMessage(message);
			
			// 메세지 보내고 입력칸 비우기
	        document.querySelector('input#admin-message').value = "";
	    }
	}
	
	// 채팅창에 메시지를 추가하는 함수
	function showMessage(message) {
	    let chatBox = document.querySelector('div#admin-chat-body'); // 채팅 메시지를 표시할 div
		
		let sendTime = formatDateTime(Date.now());
		
		let messageClass = (message.senderLoginId === 'admin') ? 'admin' : 'user';
		let html =
		`
			<div class="adminChatMessageItem ${messageClass}">
				<div>
					<span class="fs-5">${message.content}</span>
				</div>
				<div class="text-secondary text-end">
					<span>${sendTime}</span>
				</div>
			</div>
		`;

	    chatBox.innerHTML += html;

	    // 가장 최근 메시지로 스크롤
	    chatBox.scrollTop = chatBox.scrollHeight;
	}
	
	const btnReloadRoom = document.querySelector('button#btnReloadRoom');
	// 새 메세지 및 채팅방 알림버튼
	function reloadChatRoom() {
		if (btnReloadRoom.className.match('d-none')) {
			btnReloadRoom.classList.remove('d-none');
		}
	}
	btnReloadRoom.addEventListener('click', () => {
		document.querySelector('div#adminChatBox').innerHTML =
		`
			<div class="spinner-border text-center" role="status">
				<span class="visually-hidden"></span>
			</div>
			<span>채팅방 불러오는중...</span>
		`;
		getChatRoom();
		btnReloadRoom.classList.add('d-none');
	});
	
	
	
	
	
	// 버튼 클릭 시 메세지 전송
	document.querySelector('button#admin-btnSendMessage').addEventListener('click', sendMessage);

	// 엔터 키로 메시지 전송
	document.querySelector('input#admin-message').addEventListener('keypress', function(event) {
	    if (event.key === 'Enter') {
	        event.preventDefault();  // 폼 제출 방지
	        sendMessage();
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