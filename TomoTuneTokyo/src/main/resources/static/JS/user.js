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
      return;
    }

    fetch(`/user/check-username?username=${encodeURIComponent(v)}`)
      .then(res => res.json())
      .then(available => {
        console.log('중복체크 결과:', available, 'for', v, '→ msg 엘리먼트:', msg);
        if (available) {
          msg.textContent  = '使用可能なIDです';
          msg.style.color = 'green';
        } else {
          msg.textContent  = '既に使用中のIDです';
          msg.style.color = 'red';
        }
      })
      .catch(err => {
        console.error(err);
        msg.textContent  = '通信エラーが発生しました';
        msg.style.color = 'red';
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
      return;
    }

    fetch(`/user/check-nickname?nickname=${encodeURIComponent(v)}`)
      .then(res => res.json())
      .then(available => {
        console.log('중복체크 결과:', available, 'for', v, '→ msg 엘리먼트:', msg);
        if (available) {
          msg.textContent  = '使用可能なニックネームです';
          msg.style.color = 'green';
        } else {
          msg.textContent  = '既に使用中のニックネームです';
          msg.style.color = 'red';
        }
      })
      .catch(err => {
        console.error(err);
        msg.textContent  = '通信エラーが発生しました';
        msg.style.color = 'red';
      });
  });

  input.addEventListener('input', () => {
    msg.textContent = '';
  });
});//닉네임 중복검사 끝

// 비밀번호 일치 여부 실시간 검사
$('#password, #passwordConfirm').on('input', function(){
  if ($('#password').val() !== $('#passwordConfirm').val()) {
    $('#pwMatchMsg').text('パスワードが一致しません');
  } else {
    $('#pwMatchMsg').text('');
  }
});


// 이메일 인증번호 발송
$('#sendEmailCode').on('click', function(){
  const email = $('#email').val().trim();
  if (!email) return $('#emailMsg').text('メールアドレスを入力してください');
  $.post('/user/send-email-code', { email })
    .done(() => {
      $('#emailMsg').text('認証メールを送信しました');
      $('#emailCodeGroup').show();
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
  $.post('/user/verify-email-code', { email, code })
    .done(res => {
      $('#codeMsg').text(res.valid ? '認証成功' : '認証番号が正しくありません');
    })
    .fail(() => {
      $('#codeMsg').text('エラーが発生しました');
    });
});