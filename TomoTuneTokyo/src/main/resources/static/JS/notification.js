// notification.js

// (1) SSE 구독
if (window.currentUser && window.currentUser !== 'anonymous') {
  const evtSource = new EventSource('/notify/connect');
  evtSource.addEventListener('notification', function(e) {
    const {message, url} = JSON.parse(e.data);
    showToast(message, url);
  });
}

// (2) 테스트 버튼에 클릭 핸들러 달기 (테스트용, 예시)
//document.getElementById('test-notify-btn').addEventListener('click', () => {
//  fetch('/notify/test/notify-logout')
//    .then(res => {
//      if (!res.ok) throw new Error('알림 요청 실패');
//      console.log('알림 요청 보냄');
//    })
//    .catch(console.error);
//});

function showToast(message, url = null, duration = 7000) {
  const container = document.getElementById('toast-container');
  const el        = document.createElement('div');
  el.className    = 'toast';
  el.textContent  = message;

  // URL이 있으면 클릭 시 이동
  if (url) {
    el.style.cursor = 'pointer';
    el.addEventListener('click', () => {
      // 새 탭으로 열고 싶으면 window.open(url, '_blank');
      window.location.href = url;
    });
  }

  container.append(el);
  // 애니메이션 트리거
  requestAnimationFrame(() => el.classList.add('show'));
  // 일정 시간 후 제거
  setTimeout(() => {
    el.classList.remove('show');
    el.addEventListener('transitionend', () => el.remove(), { once: true });
  }, duration);
}