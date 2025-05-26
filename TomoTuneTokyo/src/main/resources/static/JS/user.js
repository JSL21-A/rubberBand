// static/JS/user.js

// ───────────────────────────────────────────
// 0) CSRF 토큰 자동 설정 (jQuery AJAX)
// ───────────────────────────────────────────
$(function(){
  const token  = $('meta[name="_csrf"]').attr('content');
  const header = $('meta[name="_csrf_header"]').attr('content');
  $.ajaxSetup({ beforeSend: xhr => xhr.setRequestHeader(header, token) });
});

// ───────────────────────────────────────────
// 1) 회원가입: ID 중복 검사
// ───────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  const input = document.getElementById('regUsername');
  const msg   = document.getElementById('usernameMsg');

  input.addEventListener('blur', () => {
    const v = input.value.trim();
    if (!v) { msg.textContent = ''; return; }
    if (v.length < 4 || v.length > 20) {
      msg.textContent = 'IDは4文字以上20文字以下で入力してください';
      msg.style.color = 'red';
      return;
    }
    fetch(`/user/check-username?username=${encodeURIComponent(v)}`)
      .then(res => res.json())
      .then(available => {
        msg.textContent = available ? '使用可能なIDです' : '既に使用中のIDです';
        msg.style.color = available ? 'white' : 'red';
      })
      .catch(() => {
        msg.textContent = '通信エラーが発生しました';
        msg.style.color = 'red';
      });
  });

  input.addEventListener('input', () => { msg.textContent = ''; });
});

// ───────────────────────────────────────────
// 2) 회원가입: 닉네임 중복 검사
// ───────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  const input = document.getElementById('nickname');
  const msg   = document.getElementById('nickMsg');

  input.addEventListener('blur', () => {
    const v = input.value.trim();
    if (!v) { msg.textContent = ''; return; }
    if (v.length < 2 || v.length > 8) {
      msg.textContent = 'ニックネームは２文字以上８文字以下で入力してください';
      msg.style.color = 'red';
      return;
    }
    fetch(`/user/check-nickname?nickname=${encodeURIComponent(v)}`)
      .then(res => res.json())
      .then(available => {
        msg.textContent = available ? '使用可能なニックネームです' : '既に使用中のニックネームです';
        msg.style.color = available ? 'white' : 'red';
      })
      .catch(() => {
        msg.textContent = '通信エラーが発生しました';
        msg.style.color = 'red';
      });
  });

  input.addEventListener('input', () => { msg.textContent = ''; });
});

// ───────────────────────────────────────────
// 3) 회원가입: 비밀번호 일치 검사
// ───────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  const pw1 = document.getElementById('password');
  const pw2 = document.getElementById('passwordConfirm');
  const msg = document.getElementById('pwMatchMsg');

  function checkMatch() {
    if (pw1.value === pw2.value) {
      msg.textContent = 'パスワードが一致しました';
      msg.style.color = 'white';
    } else {
      msg.textContent = 'パスワードが一致しません';
      msg.style.color = 'red';
    }
  }

  pw1.addEventListener('input', checkMatch);
  pw2.addEventListener('input', checkMatch);
});

// ───────────────────────────────────────────
// 4) 회원가입: 이메일 인증
// ───────────────────────────────────────────
$('#sendEmailCode').on('click', () => {
  const email = $('#email').val().trim();
  if (!email) return $('#emailMsg').text('メールアドレスを入力してください').css('color', 'red');
  $.post('/user/send-email-code', { email })
    .done(() => {
      $('#emailMsg').text('認証メールを送信しました').css('color','white');
      $('#emailCodeGroup').show();
      $('#email').prop('readonly', true);
    })
    .fail(() => {
      $('#emailMsg').text('送信に失敗しました');
    });
});

$('#verifyEmailCode').on('click', () => {
  const code = $('#emailCode').val().trim();
  if (!code) return $('#codeMsg').text('認証番号を入力してください');
  $.post('/user/verify-email-code',{ code })
    .done(ok => {
      $('#codeMsg').text(ok ? '認証成功' : '認証番号が正しくありません')
                   .css('color', ok ? 'white' : 'red');
    })
    .fail(() => {
      $('#codeMsg').text('エラーが発生しました').css('color','red');
    });
});

// ───────────────────────────────────────────
// 5) 회원가입: 최종 제출
// ───────────────────────────────────────────
$('#registerBtn').on('click', async e => {
  e.preventDefault();
  const form   = document.getElementById('registerForm');
  const data   = new URLSearchParams(new FormData(form));
  const csrf   = $('meta[name="_csrf"]').attr('content');
  const header = $('meta[name="_csrf_header"]').attr('content');

  const res = await fetch('/user/register', {
    method: 'POST',
    headers: { 'Content-Type':'application/x-www-form-urlencoded', [header]: csrf },
    body: data.toString()
  });

  if (res.ok) {
    $('#registerMsg').text('登録が完了しました！').css('color','white');
    setTimeout(() => window.location.href='/', 2000);
  } else {
    $('#registerMsg').text('通信エラーが発生しました。').css('color','red');
  }
});

$('#login').on('click', async e => {
  e.preventDefault();
  const username = $('input[name="username"]').val().trim();
  const password = $('input[name="password"]').val().trim();

  $.post('/user/login', { username, password })
    .done(async data => {
		
		window.currentUser = data.userId;
      // 기존 UI 갱신
      $('#slideMenu').removeClass('open');
      $('#slideMenu .login-form').hide();
      $('#welcomeName').text(username);
      $('#slideMenu .welcome').show();

      // **추가**: 로그인 후 슬라이드 메뉴 헤더만 다시 불러와서
      // 서버 사이드에서 Thymeleaf로 렌더된 상태(인증 정보 반영)를 반영
      $('#slideMenu').load('/user/header-fragment #mainPane, #notificationPane', function() {
        // 닫기 버튼 재바인딩
        $('.close-btn').off('click').on('click', () => {
          $('#slideMenu').removeClass('open');
        });
      });
	   await initChat(currentUser);
	      })
	      .fail(xhr => {
	        $('#loginError').text(
	          xhr.status === 401
	            ? 'IDやパスワードが間違っています'
	            : 'エラー発生'
	        );
	      });
	  });

//비밀번호 초기화
let fpUsernameGlobal = null;
// 0) 폼 토글 함수
function showForgotForm() {
  $('.login-form, .register-form').hide();
  $('.forgot-form').show();
}
function backToLogin() {
  $('.forgot-form, .register-form').hide();
  $('.login-form').show();
}

// 1) “ID／パスワードをお忘れの方” 링크 클릭
$('#forgotLink').on('click', e => {
  e.preventDefault();
  showForgotForm();
});

// 2) “ログインに戻る” 클릭
$('.back-to-login').on('click', e => {
  e.preventDefault();
  backToLogin();
});

// 3) 아이디 존재 확인 (次へ 버튼)
$('#fpNextBtn').on('click', async () => {
  const username = $('#fpUsername').val().trim();
  if (!username) {
    $('#fpUsernameMsg').text('IDを入力してください').css('color','red');
    return;
  }
  try {
    const res = await fetch(`/user/check-username?username=${encodeURIComponent(username)}`);
    const exists = await res.json();
    if (exists) {
      $('#fpUsernameMsg').text('存在しないIDです').css('color','red');
      return;
    }
    // 1단계 통과 → 이메일 입력 폼으로 교체
	fpUsernameGlobal = username;
    renderEmailStep(username);
  } catch {
    $('#fpUsernameMsg').text('通信エラーが発生しました').css('color','red');
  }
});

// 4) 이메일 입력·인증 단계 렌더 함수
function renderEmailStep(username) {
  $('.forgot-form').html(`
    <h2>メールアドレス認証</h2>
    <div class="form-group">
      <input type="email" id="fpEmail" placeholder="登録済みメールアドレス" required />
      <button type="button" id="fpSendCode">認証メール送信</button>
      <div id="fpEmailMsg" class="validation-msg"></div>
    </div>
    <div class="form-group" style="display:none" id="fpCodeGroup">
      <input type="text" id="fpCode" placeholder="認証番号を入力" />
      <button type="button" id="fpVerifyCode">認証する</button>
      <div id="fpCodeMsg" class="validation-msg"></div>
    </div>
    <div class="login-actions">
      <a href="#" class="btn-link back-to-login">ログインに戻る</a>
    </div>
  `);
  // 뒤로가기 바인딩
  $('.back-to-login').on('click', e => { e.preventDefault(); backToLogin(); });
  // 이메일 전송 클릭
  $('#fpSendCode').on('click', async () => {
	const email = $('#fpEmail').val().trim();
	  if (!email) {
	    return $('#fpEmailMsg')
	      .text('メールアドレスを入力してください')
	      .css('color','red');
	  }

	  try {
	    // ① 아이디–이메일 매칭 체크
	    const matchRes = await fetch(
	      `/user/check-username-email?username=${encodeURIComponent(fpUsernameGlobal)}&email=${encodeURIComponent(email)}`
	    );
	    const isMatch = await matchRes.json();
	    if (!isMatch) {
	      return $('#fpEmailMsg')
	        .text('IDとメールアドレスが一致しません')
	        .css('color','red');
	    }
		
		await $.post('/user/send-email-code', { email });
		    $('#fpEmailMsg')
		      .text('認証メールを送信しました')
		      .css('color','white');
		    $('#fpCodeGroup').show();
		    $('#fpEmail').prop('disabled', true);

		  } catch (e) {
		    console.error(e);
		    $('#fpEmailMsg')
		      .text('通信エラーが発生しました')
		      .css('color','red');
		  }
		});
  // 코드 검증 클릭
  $('#fpVerifyCode').on('click', async () => {
    const code = $('#fpCode').val().trim();
    if (!code) return $('#fpCodeMsg').text('認証番号を入力してください').css('color','red');
    try {
      const ok = await $.post('/user/verify-email-code', { code });
      if (ok) {
        renderResetStep(username);
      } else {
        $('#fpCodeMsg').text('認証番号が正しくありません').css('color','red');
      }
    } catch {
      $('#fpCodeMsg').text('エラーが発生しました').css('color','red');
    }
  });
}

// 5) 비밀번호 재설정 단계 렌더
function renderResetStep(username) {
  $('.forgot-form').html(`
    <h2>新しいパスワード設定</h2>
    <div class="form-group">
      <input type="password" id="fpNewPw" placeholder="新しいパスワード" required />
    </div>
    <div class="form-group">
      <input type="password" id="fpNewPwConfirm" placeholder="新しいパスワード（確認）" required />
      <div id="fpPwMsg" class="validation-msg"></div>
    </div>
    <button type="button" id="fpResetBtn" class="btn-login">パスワードを変更</button>
    <div class="login-actions">
      <a href="#" class="btn-link back-to-login">ログインに戻る</a>
    </div>
  `);
  $('.back-to-login').on('click', e => { e.preventDefault(); backToLogin(); });

  // 비밀번호 일치 실시간 검사
  $('#fpNewPw, #fpNewPwConfirm').on('input', () => {
    const pw1 = $('#fpNewPw').val();
    const pw2 = $('#fpNewPwConfirm').val();
    if (pw1 && pw1 === pw2) {
      $('#fpPwMsg').text('パスワードが一致しました').css('color','white');
    } else {
      $('#fpPwMsg').text('パスワードが一致しません').css('color','red');
    }
  });

  // 최종 변경 클릭
  $('#fpResetBtn').on('click', async () => {
	const csrfToken  = $('meta[name="_csrf"]').attr('content');
	const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
    const pw1 = $('#fpNewPw').val();
    const pw2 = $('#fpNewPwConfirm').val();
    if (!pw1 || pw1 !== pw2) return;
    try {
      const res = await fetch('/user/reset-password', {
        method: 'POST',
        headers: {
			'Content-Type':'application/json',
			[csrfHeader]: csrfToken
		},
        body: JSON.stringify({ username, newPassword: pw1 })
      });
      if (res.ok) {
        $('.forgot-form').html(`
          <p>パスワードの変更が完了しました。</p>
          <div class="login-actions">
            <a href="#" class="btn-link back-to-login">ログインに戻る</a>
          </div>
        `);
        $('.back-to-login').on('click', e => { e.preventDefault(); backToLogin(); });
      } else {
        $('#fpPwMsg').text('変更に失敗しました').css('color','red');
      }
    } catch {
      $('#fpPwMsg').text('エラーが発生しました').css('color','red');
    }
  });
}

