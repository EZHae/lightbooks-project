document.addEventListener('DOMContentLoaded', () => {
    console.log('user-signup.js');

	// 로그인 아이디 중복체크
    const inputLoginId = document.querySelector('input#loginId');
    const divLoginIdResult = document.querySelector('div#checkLoginIdResult');
	let isCheckLoginId = true;
    inputLoginId.addEventListener('change', (event) => {
        let loginId = event.target.value;
		
		if (loginId.trim() === '') {
			divLoginIdResult.innerHTML = '아이디를 입력해주세요.'
			isCheckLoginId = true;
			changeButtonState()
			return;
		}
        
        const uri = `../user/checkLoginId?loginId=${loginId}`;
		axios.get(uri).then(response => {
			console.log(response.data);
			isCheckLoginId = response.data;
			
			if (isCheckLoginId === false) {
				divLoginIdResult.innerHTML = '사용가능한 아이디입니다.';
			} else {
				divLoginIdResult.innerHTML = '중복된 아이디입니다.';
			}
			changeButtonState();
		}).catch(error => {
			console.log(error);
		});
    });

	// 닉네임 중복체크
    const inputNickname = document.querySelector('input#nickname');
    const divNicknameResult = document.querySelector('div#checkNicknameResult');
	let isCheckNickname = true;
    inputNickname.addEventListener('change', (event) => {
        let nickname = event.target.value;
		
		if (nickname.trim() === '') {
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
				divNicknameResult.innerHTML = '사용가능한 닉네임입니다.';
			} else {
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
				divPhonenumberResult.innerHTML = '사용가능한 전화번호입니다.';
			} else {
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
				divEmailResult.innerHTML = '사용가능한 이메일입니다.';
			} else {
				divEmailResult.innerHTML = '중복된 이메일입니다.';
			}
			changeButtonState();
		}).catch(error => {
			console.log(error);
		});
	});
	
	
	// 버튼 활성화 상태 변경
	const inputSignUp = document.querySelector('input#btnSignUp');
	function changeButtonState() {
	     
	    if (!isCheckLoginId && !isCheckNickname && !isCheckPhonenumber && !isCheckEmail) { 
	        inputSignUp.classList.remove('disabled');
	    } else {
	        inputSignUp.classList.add('disabled');
	        inputSignUp.removeAttribute('disabled');
	    }
	}
});