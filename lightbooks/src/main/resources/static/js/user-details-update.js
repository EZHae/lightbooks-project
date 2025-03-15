document.addEventListener('DOMContentLoaded', () => {
    console.log('user-details-update.js');

	// 닉네임 중복체크
    const inputNickname = document.querySelector('input#nickname');
    const divNicknameResult = document.querySelector('div#checkNicknameResult');
	let isCheckNickname = true;
    inputNickname.addEventListener('change', (event) => {
        let nickname = event.target.value;
		
		if (nickname.trim() === '') {
			divNicknameResult.className = "";
			divNicknameResult.classList.add('text-danger');
			divNicknameResult.innerHTML = '닉네임을 입력해주세요.'
			isCheckNickname = true;
			changeButtonState()
			return;
		}
        
        const uri = `../user/checkNickname?nickname=${nickname}`;
		axios.get(uri).then(response => {
			console.log(response.data);
			isCheckNickname = response.data;
			
			if (isCheckNickname === false) {
				divNicknameResult.className = "";
				divNicknameResult.classList.add('text-success');
				divNicknameResult.innerHTML = '사용가능한 닉네임입니다.';
			} else {
				divNicknameResult.className = "";
				divNicknameResult.classList.add('text-danger');
				divNicknameResult.innerHTML = '중복된 닉네임입니다.';
			}
			changeButtonState();
		}).catch(error => {
			console.log(error);
		});
    });


	// 전화번호 포맷
    const inputPhonenumber = document.querySelector('input#phonenumber');
    const divPhonenumberResult = document.querySelector('div#checkPhonenumberResult');
	let isCheckPhonenumber = true;
    // 숫자 이외의 문자가 입력되는 것을 방지
    inputPhonenumber.addEventListener('input', (event) => {
        let inputValue = event.target.value;
        event.target.value = inputValue.replace(/\D/g, ''); // 숫자 이외의 문자 제거
    });

    // 포커스를 잃을 때 포맷 검사
    inputPhonenumber.addEventListener('change', (event) => validatePhoneNumber(event.target));
    function validatePhoneNumber(input) {
        let numbers = input.value.replace(/\D/g, '');  // 숫자만 남기기
        let formatted = '';

        // 유효한 이동통신사 번호 패턴
        const validPrefixes = ['010', '011', '016', '017', '018', '019'];

        if (numbers.length < 10) {
            // 최소 10자리가 되어야 유효한 번호로 판단
			divPhonenumberResult.className = "";
			divPhonenumberResult.classList.add('text-danger');
            divPhonenumberResult.textContent = '올바른 전화번호 입력하세요';
			isCheckPhonenumber = true;
			changeButtonState()
            input.value = '';  // 잘못된 번호는 입력값을 비우기
            return;
        }

        if (validPrefixes.includes(numbers.slice(0, 3))) {
            // 번호 패턴이면 올바르게 포맷
            if (numbers.length <= 3) {
                formatted = numbers;
            } else if (numbers.length <= 6) {
                formatted = numbers.slice(0, 3) + '-' + numbers.slice(3);
            } else if (numbers.length <= 10) {
                formatted = numbers.slice(0, 3) + '-' + numbers.slice(3, 6) + '-' + numbers.slice(6);
            } else {
                formatted = numbers.slice(0, 3) + '-' + numbers.slice(3, 7) + '-' + numbers.slice(7, 11);
            }

            input.value = formatted;  // 포맷된 값 다시 입력 필드에 넣기
            divPhonenumberResult.textContent = '';  // 에러 메시지 지우기
        } else if (numbers.length > 0) {
            // 번호 패턴이 아니면 경고 메시지
			divPhonenumberResult.className = "";
			divPhonenumberResult.classList.add('text-danger');
            divPhonenumberResult.textContent = '올바른 전화번호 입력하세요';
			isCheckPhonenumber = true;
			changeButtonState()
            input.value = '';  // 잘못된 번호는 입력값을 비우기
        } else {
            divPhonenumberResult.textContent = '';  // 값이 비어있으면 에러 메시지 없애기
        }
		
		// 전화번호 중복체크
		let phonenumber = input.value;
		if (phonenumber.trim() === '') {
			divPhonenumberResult.className = "";
			divPhonenumberResult.classList.add('text-danger');
			divPhonenumberResult.innerHTML = '전화번호를 입력해주세요.'
			isCheckPhonenumber = true;
			changeButtonState()
			return;
		}
		
		const uri = `../user/checkPhonenumber?phonenumber=${phonenumber}`;
		axios.get(uri).then(response => {
			console.log(response.data);
			isCheckPhonenumber = response.data;
			
			if (isCheckPhonenumber === false) {
				divPhonenumberResult.className = "";
				divPhonenumberResult.classList.add('text-success');
				divPhonenumberResult.innerHTML = '사용가능한 전화번호입니다.';
			} else {
				divPhonenumberResult.className = "";
				divPhonenumberResult.classList.add('text-danger');
				divPhonenumberResult.innerHTML = '중복된 전화번호입니다.';
			}
			changeButtonState();
		}).catch(error => {
			console.log(error);
		});
		
    }
	
	// 이메일 중복체크
	const inputEmail = document.querySelector('input#email');
	const divEmailResult = document.querySelector('div#checkEmailResult');
	let isCheckEmail = true;
	inputEmail.addEventListener('change', (event) => {
	    let email = event.target.value;
		
		if (email.trim() === '') {
			divEmailResult.className = "";
			divEmailResult.classList.add('text-danger');
			divEmailResult.innerHTML = '이메일을 입력해주세요.'
			isCheckEmail = true;
			changeButtonState()
			return;
		}
	    
	    const uri = `../user/checkEmail?email=${email}`;
		axios.get(uri).then(response => {
			console.log(response.data);
			isCheckEmail = response.data;
			
			if (isCheckEmail === false) {
				divEmailResult.className = "";
				divEmailResult.classList.add('text-success');
				divEmailResult.innerHTML = '사용가능한 이메일입니다.';
			} else {
				divEmailResult.className = "";
				divEmailResult.classList.add('text-danger');
				divEmailResult.innerHTML = '중복된 이메일입니다.';
			}
			changeButtonState();
		}).catch(error => {
			console.log(error);
		});
	});

	// 비밀번호
	const inputOldPassword = document.querySelector('input#oldPassword');
	const divOldPasswordResult = document.querySelector('div#checkOldPasswordResult');
	let isCheckOldPassword = true;
	inputOldPassword.addEventListener('change', (event) => {
	    let oldPassword = event.target.value;
		const id = document.querySelector('input#id').value;
		
		if (oldPassword.trim() === '') {
			divOldPasswordResult.className = "";
			divOldPasswordResult.classList.add('text-danger');
			divOldPasswordResult.innerHTML = '현재 비빌번호를 입력해주세요.'
			isCheckOldPassword = true;
			changeButtonStatePW()
			return;
		}
	    
	    const uri = `../user/checkOldPassword`;
		let data = {id, oldPassword};
		console.log(data);
		axios.post(uri, data).then(response => {
			console.log(response.data);
			isCheckOldPassword = response.data;
			
			if (isCheckOldPassword === false) {
				divOldPasswordResult.className = "";
				divOldPasswordResult.classList.add('text-success');
				divOldPasswordResult.innerHTML = '현재 비밀번호와 일치합니다.';
			} else {
				divOldPasswordResult.className = "";
				divOldPasswordResult.classList.add('text-danger');
				divOldPasswordResult.innerHTML = '현재 비밀번호와 다릅니다.';
			}
			changeButtonStatePW()
		}).catch(error => {
			console.log(error);
		});
	});
	
	// 새 비밀번호 확인
	const inputNewPassword = document.querySelector('input#newPassword');
	const inputNewPasswordCheck = document.querySelector('input#newPasswordCheck');
	const inputNewPasswords = [inputNewPassword, inputNewPasswordCheck];
	const divCheckPasswordResult = document.querySelector('div#checkPasswordResult');
	let isCheckPassword = true;
	inputNewPasswords.forEach(input => {
		input.addEventListener('change', () => {
			let newPassword = document.querySelector('input#newPassword').value;
			let newPasswordCheck = document.querySelector('input#newPasswordCheck').value;
			console.log(newPassword);
			console.log(newPasswordCheck);

			if (newPassword === newPasswordCheck) {
				isCheckPassword = false;
				divCheckPasswordResult.className = "";
				divCheckPasswordResult.classList.add('text-success');
				divCheckPasswordResult.innerHTML = '변경할 비밀번호와 동일합니다.'
			} else {
				isCheckPassword = true;
				divCheckPasswordResult.className = "";
				divCheckPasswordResult.classList.add('text-danger');
				divCheckPasswordResult.innerHTML = '변경할 비밀번호와 동일하게 입력해주세요.'
			}
			changeButtonStatePW()
		});
	});

	// 탈퇴 버튼 상태 변경
	const deleteUserText = document.querySelector('strong#deleteUserText').textContent;
	let inputDeleteUserText = document.querySelector('input#inputDeleteUserText');
	const btnDeleteUser = document.querySelector('button#btnDeleteUser');
	inputDeleteUserText.addEventListener('change', () => {
		if (deleteUserText === inputDeleteUserText.value) {
			btnDeleteUser.classList.remove('disabled');
		} else {
			btnDeleteUser.classList.add('disabled');
			btnDeleteUser.removeAttribute('disabled');
		}
	});

	// 탈퇴 확인 버튼 기능
	btnDeleteUser.addEventListener('click', () => {
		const result = confirm('정말 탈퇴하시겠습니까?');

		if (result) {
			const id = document.querySelector('input#id').value;
			const uri = `../user/delete?id=${id}`;
			disableClicks()
			axios.post(uri).then(() => {
				alert('회원 탈퇴가 완료되었습니다.');
				window.location.href = "/";
			}).catch(error => {
				console.error(error);
			});
		}
	});

	// 회원정보 수정 버튼 활성화 상태 변경
	const inputUpdateProfile = document.querySelector('input#btnUpdateProfile');
	function changeButtonState() {
	    if (!isCheckNickname && !isCheckPhonenumber && !isCheckEmail) { 
	        inputUpdateProfile.classList.remove('disabled');
	    } else {
	        inputUpdateProfile.classList.add('disabled');
	        inputUpdateProfile.removeAttribute('disabled');
	    }
	}
	inputUpdateProfile.addEventListener('click', () => {
		alert('회원 정보가 변경되었습니다. 다시 로그인해주세요');
		disableClicks()
	});

	// 비밀번호 변경 버튼 활성화 상태 변경
	const inputUpdatePassword = document.querySelector('input#btnUpdatePassword');
	function changeButtonStatePW() {
		if (!isCheckOldPassword && !isCheckPassword) {
			inputUpdatePassword.classList.remove('disabled');
		} else {
			inputUpdatePassword.classList.add('disabled');
			inputUpdatePassword.removeAttribute('disabled');
		}
	}
	inputUpdatePassword.addEventListener('click', () => {
		alert('비밀번호가 변경되었습니다. 다시 로그인해주세요');
		disableClicks()
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