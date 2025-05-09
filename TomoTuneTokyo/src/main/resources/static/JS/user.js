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
  if (!email) return $('#emailMsg').text('メールアドレスを入力してください');
  $.post('/user/send-email-code', { email })
    .done(() => {
      $('#emailMsg').text('認証メールを送信しました').css('color','white');
      $('#emailCodeGroup').show();
      $('#email').prop('disabled', true);
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
    .done(async () => {
      // 기존 UI 갱신
      $('#slideMenu').removeClass('open');
      $('#slideMenu .login-form').hide();
      $('#welcomeName').text(username);
      $('#slideMenu .welcome').show();

      // **추가**: 로그인 후 슬라이드 메뉴 헤더만 다시 불러와서
      // 서버 사이드에서 Thymeleaf로 렌더된 상태(인증 정보 반영)를 반영
      $('#slideMenu').load('/user/header-fragment', function() {
        // 닫기 버튼 재바인딩
        $('.close-btn').off('click').on('click', () => {
          $('#slideMenu').removeClass('open');
        });
      });

      // (필요하다면 여기서 채팅 초기화)
      // await initChat(username);
    })
    .fail(xhr => {
      $('#loginError').text(
        xhr.status === 401
          ? 'IDやパスワードが間違っています'
          : 'エラー発生'
      );
    });
});