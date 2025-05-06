//CSRF토큰 불러오기
$(function(){
	const token = $('meta[name="_csrf"]').attr('content');
	const header = $('meta[name="_csrf_header"]').attr('content');
	
	$.ajaxSetup({
		beforeSend: (xhr) => {
			xhr.setRequestHeader(header, token);
		}
	})
})

//로그인 요청 시 우측 사이드바 오픈
$(function(){
	const params = new URLSearchParams(window.location.search);
	if(params.has('openLogin')){
		$('#slideMenu').addClass('open');
	}
})


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
  

});

