// static/JS/calendar.js

document.addEventListener('DOMContentLoaded', function() {
  // 팝업, 정보 요소
  const popup      = document.getElementById('event-popup');
  const info       = document.getElementById('event-info');
  const closeBtn   = document.querySelector('#event-popup .close-btn');

  // 달력 헤더·그리드·내비 버튼
  const monthLabel = document.querySelector('.calendar-header .month');
  const grid       = document.querySelector('.calendar-grid');
  const prevBtn    = document.querySelector('.calendar-header .nav .prev');
  const nextBtn    = document.querySelector('.calendar-header .nav .next');

  // 월 이름
  const monthNames = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  ];

  // 현재 보여줄 연·월 (초기값: 시스템 날짜)
  let today        = new Date();
  let currentYear  = today.getFullYear();
  let currentMonth = today.getMonth();

  // 팝업 닫기
  function closePopup() {
    popup.style.display = 'none';
  }
  if (closeBtn) closeBtn.addEventListener('click', closePopup);

  // 서버에서 이 달 스케줄을 날짜별로 불러옴
  async function loadEvents(year, month) {
    try {
      // month+1: JS month(0~11) → API month(1~12)
      const res = await fetch(`/api/schedules?year=${year}&month=${month+1}`);
      if (!res.ok) throw new Error(res.status);
      return await res.json();
    } catch (err) {
      console.error('스케줄 로드 실패:', err);
      return {}; // 빈 맵 리턴
    }
  }

  // 달력 렌더링
  async function renderCalendar(year, month) {
    const events = await loadEvents(year, month);

    // 요일 헤더
    const weekdayHeaders = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat']
      .map(d => `<div class="day-name">${d}</div>`).join('');
    grid.innerHTML = weekdayHeaders;

    // 첫째 날 요일, 한 달 일수
    const firstDay   = new Date(year, month, 1).getDay();
    const daysInMonth= new Date(year, month + 1, 0).getDate();

    // 월 레이블
    monthLabel.textContent = `${monthNames[month]} ${year}`;

    // 빈칸 채우기
    for (let i = 0; i < firstDay; i++) {
      const empty = document.createElement('div');
      empty.classList.add('day');
      grid.appendChild(empty);
    }

    // 각 날짜 렌더
    for (let d = 1; d <= daysInMonth; d++) {
      const dateStr = `${year}-${String(month+1).padStart(2,'0')}-${String(d).padStart(2,'0')}`;
      const dayEl   = document.createElement('div');
      dayEl.classList.add('day');
      dayEl.textContent = d;

      // Today 강조
      const now = new Date();
      if (year === now.getFullYear() &&
          month === now.getMonth() &&
          d === now.getDate()) {
        dayEl.classList.add('today');
      }

      // 이벤트 태그 붙이기
      if (events[dateStr]) {
        dayEl.classList.add('event');
        dayEl.dataset.title = events[dateStr].map(e => e.title).join(', ');
        events[dateStr].forEach(evt => {
          const tag = document.createElement('div');
          tag.className = 'event-tag';
          tag.textContent = evt.title;
          if (evt.color) tag.style.background = evt.color;
          dayEl.appendChild(tag);
        });
      }

      grid.appendChild(dayEl);
    }

    attachEventListeners();
  }

  // 각 이벤트 일자에 클릭 핸들러 붙이기
  function attachEventListeners() {
    document.querySelectorAll('.calendar .day.event').forEach(day => {
      day.addEventListener('click', () => {
        info.textContent = day.dataset.title;
        popup.style.display = 'block';
      });
    });
  }

  // 이전 월
  if (prevBtn) prevBtn.addEventListener('click', () => {
    currentMonth--;
    if (currentMonth < 0) {
      currentMonth = 11;
      currentYear--;
    }
    renderCalendar(currentYear, currentMonth);
  });

  // 다음 월
  if (nextBtn) nextBtn.addEventListener('click', () => {
    currentMonth++;
    if (currentMonth > 11) {
      currentMonth = 0;
      currentYear++;
    }
    renderCalendar(currentYear, currentMonth);
  });

  // 최초 렌더링
  renderCalendar(currentYear, currentMonth);
});