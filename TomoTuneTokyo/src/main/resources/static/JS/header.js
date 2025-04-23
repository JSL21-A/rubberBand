
  document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.header-icon').forEach(img => {
      // 원본 src, hover src 불러오기
      const originalSrc = img.getAttribute('src');
      const hoverSrc    = img.getAttribute('data-hover-src');

      // 마우스 올릴 때
      img.addEventListener('mouseenter', () => {
        img.setAttribute('src', hoverSrc);
      });

      // 마우스 뗄 때
      img.addEventListener('mouseleave', () => {
        img.setAttribute('src', originalSrc);
      });
    });
  });


$(function () {
    $(".gnb > .nav_1depth > li").hover(
      function () {
        $(this).addClass("active");
        $(this).children(".nav_2depth").stop(true, true).slideDown("fast");
      },
      function () {
        $(this).removeClass("active");
        $(this).children(".nav_2depth").stop(true, true).slideUp("fast");
      }
    );
  });
  
//헤더 사이드 메뉴 슬라이드
$('#hamburgerBtn').on('click', function(e) {
  e.stopPropagation();               // 문서 클릭에 의한 즉시 닫힘 방지
  $('#slideMenu').toggleClass('open');
});
$(document).on('click', function(e) {
  if (!$(e.target).closest('#hamburgerBtn, #slideMenu').length) {
    $('#slideMenu').removeClass('open');
  }
});

$('#slideMenu').on('click', function(e) {
  e.stopPropagation();
});

$(document).on('click', function() {
  $('#slideMenu').removeClass('open');
});

document.addEventListener('DOMContentLoaded', () => {
    const slideMenu = document.getElementById('slideMenu');
    const closeBtn  = slideMenu.querySelector('.close-btn');
    closeBtn.addEventListener('click', () => {
      slideMenu.classList.remove('open');
    });
  });

const usernameInput = document.querySelector('input[name="username"]');
usernameInput.addEventListener('invalid', e => {
  e.target.setCustomValidity('IDを入力してください');
});
usernameInput.addEventListener('input', e => {
  e.target.setCustomValidity('');
});
const passwordInput = document.querySelector('input[name="password"]');
passwordInput.addEventListener('invalid', e => {
  e.target.setCustomValidity('パスワードを入力してください');
});
passwordInput.addEventListener('input', e => {
  e.target.setCustomValidity('');
});
const registerUsername = document.querySelector('.register-form input[name="username"]');
registerUsername.addEventListener('invalid', e => {
  e.target.setCustomValidity('IDを入力してください');
});
registerUsername.addEventListener('input', e => {
  e.target.setCustomValidity('');
});
const registerPassword = document.querySelector('.register-form input[name="password"]');
registerPassword.addEventListener('invalid', e => {
  e.target.setCustomValidity('パスワードを入力してください');
});
registerPassword.addEventListener('input', e => {
  e.target.setCustomValidity('');
});
const registerPasswordConfirm = document.querySelector('.register-form input[name="passwordConfirm"]');
registerPasswordConfirm.addEventListener('invalid', e => {
  e.target.setCustomValidity('パスワード確認を入力してください');
});
registerPasswordConfirm.addEventListener('input', e => {
  e.target.setCustomValidity('');
});
const registerNickname = document.querySelector('.register-form input[name="nickname"]');
registerNickname.addEventListener('invalid', e => {
  e.target.setCustomValidity('ニックネームを入力してください');
});
registerNickname.addEventListener('input', e => {
  e.target.setCustomValidity('');
});
const registerEmail = document.querySelector('.register-form input[name="email"]');
registerEmail.addEventListener('invalid', e => {
  e.target.setCustomValidity('ニックネームを入力してください');
});
registerEmail.addEventListener('input', e => {
  e.target.setCustomValidity('');
});

$(function(){
  // 로그인 ↔ 회원가입 폼 전환
  $('.register-link').on('click', function(e){
    e.preventDefault();
    $('.login-form').hide();
    $('.register-form').show();
  });
  $('.back-to-login').on('click', function(e){
    e.preventDefault();
    $('.register-form').hide();
    $('.login-form').show();
  });

  // 비밀번호 일치 여부 실시간 검사
  $('#password, #passwordConfirm').on('input', function(){
    if ($('#password').val() !== $('#passwordConfirm').val()) {
      $('#pwMatchMsg').text('パスワードが一致しません');
    } else {
      $('#pwMatchMsg').text('');
    }
  });
  
  

  // 닉네임 중복 체크 (예시 AJAX)
  $('#checkNickname').on('click', function(){
    const nick = $('#nickname').val().trim();
    if (!nick) return $('#nickMsg').text('ニックネームを入力してください');
    $.get('/user/checkNickname', { nickname: nick })
      .done(res => {
        $('#nickMsg').text(res.available ? '使用可能です' : 'このニックネームは既に使われています');
      })
      .fail(() => {
        $('#nickMsg').text('エラーが発生しました');
      });
  });

  // 이메일 인증번호 발송
  $('#sendEmailCode').on('click', function(){
    const email = $('#email').val().trim();
    if (!email) return $('#emailMsg').text('メールアドレスを入力してください');
    $.post('/user/sendEmailCode', { email })
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
    $.post('/user/verifyEmailCode', { email, code })
      .done(res => {
        $('#codeMsg').text(res.valid ? '認証成功' : '認証番号が正しくありません');
      })
      .fail(() => {
        $('#codeMsg').text('エラーが発生しました');
      });
  });
});