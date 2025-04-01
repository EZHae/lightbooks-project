document.addEventListener('DOMContentLoaded', () => {
	console.log('fragments-chat.js');
	const userId = document.querySelector('span#userId').textContent;
	const loginId = document.querySelector('span#userLoginId').textContent;
	const chatBox = document.querySelector('div#chatBox');
	
	const chatLink = document.querySelector('a#chatLink');
	chatLink.addEventListener('click', () => {
		axios.get(`/user/chatroom/check?id=${userId}`).then(response => {
			const result = response.data;
			
			if (result == true) {
				if (chatBox.className.match('d-none')) {
					chatBox.classList.remove('d-none');
					const profileDropdown = new bootstrap.Dropdown(document.getElementById('profileDropdown'));
					profileDropdown.hide();
				}
			} else {
				let result = confirm('1대1 문의할 수 있는 채팅방이 만들어집니다.\n채팅방을 만드시겠습니까?');
				if (result) {
					axios.post(`/user/chatroom/create?userId=${userId}&loginId=${loginId}`).then(response => {
						alert('채팅방이 만들어졌습니다.');
						stompClient.send("/app/newChatRoom", {}, `${loginId}님이 새로운 채팅방을 개설했습니다!`);
					}).catch(error => {
						console.error(error);
					});					
				}
			}
		}).catch(error => {
			console.error(error);
		});
	});
	
	
	
	
	document.querySelector('button#closeChat').addEventListener('click', () => {
		if (!chatBox.className.match('d-none')) {
			chatBox.classList.add('d-none');
		}
	})
	
	////////
	
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
			if (receivedMessage.chatRoomId == userId && receivedMessage.senderLoginId != loginId) {
                showMessage(receivedMessage);
            }
	    });
	});
	
	// 메시지 전송 함수
	function sendMessage() {
		let senderId = document.querySelector('span#userId').textContent;
		let senderLoginId = document.querySelector('span#userLoginId').textContent;
		const receiverId = 16;
		const receiverLoginId = 'admin';
		let chatRoomId = document.querySelector('span#userId').textContent;
		let content = document.querySelector("input#message").value;
		
	    if (content.trim() !== "") {
			// 메세지 객체 만들기
			let message = { senderId, senderLoginId, receiverId, receiverLoginId, chatRoomId, content}
			
			// 메세지 전송
	        stompClient.send('/app/sendMessage', {}, JSON.stringify(message));
			
			// 보낸 메세지 채팅창에 추가
			showMessage(message);
			
			// 메세지 보내고 입력칸 비우기
	        document.querySelector('input#message').value = "";
	    }
	}
	
	// 채팅창에 메시지를 추가하는 함수
	function showMessage(message) {
	    let chatBox = document.querySelector('div#chat-body'); // 채팅 메시지를 표시할 div
		
		let sendTime = formatDateTime(Date.now());

		let messageClass = (message.senderLoginId === loginId) ? 'user' : 'admin';
		let html =
		`
			<div class="chatMessageItem ${messageClass}">
				<div>
					<span class="messageContent">${message.content}</span>
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
	
	///////////////////////////
	
	const btnReloadMessage = document.querySelector('button#btnReloadMessage');
	btnReloadMessage.addEventListener('click', () => {
		btnReloadMessage.classList.add('d-none');
		getChatMessage(userId);
	});
	
	function getChatMessage(chatRoomId) {
		const chatBody = document.querySelector('div#chat-body');
		chatBody.innerHTML =
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
		const chatBody = document.querySelector('div#chat-body');
		if (chatMessageList.length == 0) {
			chatBody.innerHTML =
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
				<div class="chatMessageItem ${messageClass}">
					<div>
						<span class="messageContent">${item.content}</span>
					</div>
					<div class="text-secondary text-end">
						<span>${sendTime}</span>
					</div>
				</div>
			`;
		});
		chatBody.innerHTML = html;
		
		// 메시지가 렌더링된 후 가장 아래로 스크롤
		chatBody.scrollTop = chatBody.scrollHeight;
	}
	
	
	
	
	// 버튼 클릭 시 메세지 전송
	document.querySelector('button#btnSendMessage').addEventListener('click', sendMessage);
	
	// 엔터 키로 메시지 전송
	document.querySelector('input#message').addEventListener('keypress', function(event) {
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