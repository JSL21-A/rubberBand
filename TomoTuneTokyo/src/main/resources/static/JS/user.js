//ID 중복 체크
document.addEventListener('DOMContentLoaded', () => {
  const input = document.getElementById('regUsername');
  const msg   = document.getElementById('usernameMsg');

  input.addEventListener('blur', () => {
    const v = input.value.trim();

    if (!v) {
      msg.textContent = '';
      return;
    }
    if (v.length < 4 || v.length > 20) {
      msg.textContent  = 'IDは4文字以上20文字以下で入力してください';
      msg.style.color = 'red';
	  isUsernameValid = false;
      return;
    }

    fetch(`/user/check-username?username=${encodeURIComponent(v)}`)
      .then(res => res.json())
      .then(available => {
        console.log('중복체크 결과:', available, 'for', v, '→ msg 엘리먼트:', msg);
        if (available) {
          msg.textContent  = '使用可能なIDです';
          msg.style.color = 'white';
		  isUsernameValid = true;
        } else {
          msg.textContent  = '既に使用中のIDです';
          msg.style.color = 'red';
		  isUsernameValid = false;
        }
      })
      .catch(err => {
        console.error(err);
        msg.textContent  = '通信エラーが発生しました';
        msg.style.color = 'red';
		isUsernameValid = false;
      });
  });

  input.addEventListener('input', () => {
    msg.textContent = '';
  });
});//ID 중복검사 끝

//닉네임 중복검사
document.addEventListener('DOMContentLoaded', () => {
  const input = document.getElementById('nickname');
  const msg   = document.getElementById('nickMsg');

  input.addEventListener('blur', () => {
    const v = input.value.trim();

    if (!v) {
      msg.textContent = '';
      return;
    }
    if (v.length < 2 || v.length > 8) {
      msg.textContent  = 'ニックネームは２文字以上８文字以下で入力してください';
      msg.style.color = 'red';
	  isNicknameVaild = false;
      return;
    }

    fetch(`/user/check-nickname?nickname=${encodeURIComponent(v)}`)
      .then(res => res.json())
      .then(available => {
        console.log('중복체크 결과:', available, 'for', v, '→ msg 엘리먼트:', msg);
        if (available) {
          msg.textContent  = '使用可能なニックネームです';
          msg.style.color = 'white';
		  isNicknameVaild = true;
        } else {
          msg.textContent  = '既に使用中のニックネームです';
          msg.style.color = 'red';
		  isNicknameVaild = false;
        }
      })
      .catch(err => {
        console.error(err);
        msg.textContent  = '通信エラーが発生しました';
        msg.style.color = 'red';
		isNicknameVaild = false;
      });
  });

  input.addEventListener('input', () => {
    msg.textContent = '';
  });
});//닉네임 중복검사 끝

// 비밀번호 input 요소 가져오기
document.addEventListener('DOMContentLoaded', () => {
const passwordInput = document.getElementById('password');
const passwordConfirmInput = document.getElementById('passwordConfirm');
const pwMatchMsg = document.getElementById('pwMatchMsg');

let isPasswordMatch = false; // 상태 변수 초기화

// 비밀번호 일치 여부 실시간 검사
const checkPasswordMatch = () => {
  if (passwordInput.value === passwordConfirmInput.value) {
    pwMatchMsg.textContent = 'パスワードが一致しました';
    pwMatchMsg.style.color = 'white';
    isPasswordMatch = true;
  } else {
    pwMatchMsg.textContent = 'パスワードが一致しません';
    pwMatchMsg.style.color = 'red';
    isPasswordMatch = false;
  }
};


// 이벤트 리스너 연결
passwordInput.addEventListener('input', checkPasswordMatch);
passwordConfirmInput.addEventListener('input', checkPasswordMatch)
})

// 이메일 인증번호 발송
$('#sendEmailCode').on('click', function(){
  const email = $('#email').val().trim();
  if (!email) return $('#emailMsg').text('メールアドレスを入力してください');
  $.post('/user/send-email-code', { email })
    .done(() => {
      $('#emailMsg').text('認証メールを送信しました').css('color', 'white');
      $('#emailCodeGroup').show();
	  $('#email').prop('disabled', true);
    })
    .fail(() => {
      $('#emailMsg').text('送信に失敗しました');
    });
});


// 이메일 인증번호 검증
$('#verifyEmailCode').on('click', function(){
  const email = $('#email').val().trim();
  const code  = $('#emailCode').val().trim();
  if (!code) return $('#codeMsg').text('認証番号を入力してください');
  $.post('/user/verify-email-code', { code })
    .done(res => {
		if(res === true) {
     	 $('#codeMsg').text('認証成功').css('color', 'white');
		 isEmailVerified = true;
		 $('#verifyEmailCode').prop('disabled', true);
		 $('#emailCode').prop('disabled', true);
		 } else {
			$('#codeMsg').text('認証番号が正しくありません').css('color', 'red');
			isEmailVerified = false;
		 }
    })
    .fail(() => {
      $('#codeMsg').text('エラーが発生しました').css('color', 'red');
	  isEmailVerified = false;
    });
});

//전체 유효성검사 + 통과시 회원가입 정보 제출 
form.addEventListener('submit', function(e){
		e.preventDefault();
		
		if(!isUsernameValid){
			alert('IDの重複確認をしてください');
			return;
		}
		if(!isPasswordMatch){
			alert('パスワードが一致していません。');
			return;
		}
		if(!isNicknameValid){
			alert('ニックネームの重複確認をしてください');
			return;
		}
		if(!isEmailVerified){
			alert('メール確認を完了してください');
			return;
		}
		form.submit();
}) 