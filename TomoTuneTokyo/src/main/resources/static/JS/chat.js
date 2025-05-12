// static/JS/chat.js

// ─────────────────────────────────────────
// 전역 변수
// ─────────────────────────────────────────
let sb;
let channelsQuery;
let currentChannel;

// ─────────────────────────────────────────
// 1) 채팅창 토글 버튼
//    HTML에 `<button id="chat-toggle">Chat</button>` 추가 필요
// ─────────────────────────────────────────
$('#chat-toggle').on('click', () => {
  $('#chat-wrapper').toggleClass('open');
});
$('.chat-close').on('click', () => {
    $('#chat-wrapper').removeClass('open');
  });


// ─────────────────────────────────────────
// 2) 로그인 성공 후 호출할 함수
//    기존 로그인 핸들러 맨 끝에 `await initChat(username);` 추가
// ─────────────────────────────────────────
async function initChat(userId) {
  // a) SDK 연결
  sb = new SendBird({ appId: '016A2743-AB66-4FFF-9F77-D08966152ABB' });
  await new Promise((res, rej) =>
    sb.connect(userId, null, (u, err) => err ? rej(err) : res(u))
  );

  // b) 내 그룹채널(1:1 + 그룹) 리스트 쿼리
  channelsQuery = sb.GroupChannel.createMyGroupChannelListQuery();
  channelsQuery.limit = 20;
  const channels = await new Promise((res, rej) =>
    channelsQuery.next((list, err) => err ? rej(err) : res(list))
  );

  // c) 채널 리스트 렌더
  $('#channel-list').empty();
  channels.forEach(ch => {
    const name = ch.name || ch.userIds.filter(id=>id!==userId).join(', ');
    const $item = $(`<div class="channel-item">${name}</div>`);
    $item.on('click', () => openChannel(ch));
    $('#channel-list').append($item);
  });

  // 자동으로 첫 채널 열기
  if (channels.length) openChannel(channels[0]);
}

// ─────────────────────────────────────────
// 3) 채널 열기: 메시지 불러오기 + 핸들러 등록
// ─────────────────────────────────────────
function openChannel(ch) {
  currentChannel = ch;
  $('.channel-item').removeClass('active');
  $(`.channel-item:contains("${ch.name||ch.url}")`).addClass('active');

  // a) 과거 메시지 불러오기
  ch.getPreviousMessages(50, true, (msgs, err) => {
    if (err) return console.error(err);
    $('#message-list').empty();
    msgs.forEach(appendMessageToUI);
    $('#message-list').scrollTop($('#message-list')[0].scrollHeight);
  });

  // b) 실시간 메시지 핸들러
  const handler = new sb.ChannelHandler();
  handler.onMessageReceived = (channel, msg) => {
    if (channel.url === ch.url) appendMessageToUI(msg);
  };
  sb.addChannelHandler('CHAT_HANDLER', handler);
}

// ─────────────────────────────────────────
// 4) 메시지 UI에 추가
// ─────────────────────────────────────────
function appendMessageToUI(msg) {
  const isMe = msg.sender.userId === sb.currentUser.userId;
  const content = msg.message
    ? msg.message
    : `<img src="${msg.url}" style="max-width:200px;">`;
  const html = `
    <div class="msg ${isMe?'my':'their'}">
      <span class="sender">${msg.sender.nickname}</span>
      <p class="text">${content}</p>
    </div>`;
  $('#message-list').append(html);
  $('#message-list').scrollTop($('#message-list')[0].scrollHeight);
}

// ─────────────────────────────────────────
// 5) 텍스트 전송
// ─────────────────────────────────────────
$('#send-btn').on('click', () => {
  const text = $('#message-input').val().trim();
  if (!text || !currentChannel) return;
  currentChannel.sendUserMessage(text, (msg, err) => {
    if (err) return console.error(err);
    appendMessageToUI(msg);
    $('#message-input').val('');
  });
});

// ─────────────────────────────────────────
// 6) 파일 전송 (이미지/동영상/음성)
//    HTML에 `<input type="file" id="file-input">` 추가 필요
// ─────────────────────────────────────────
$('#file-input').on('change', e => {
  const file = e.target.files[0];
  if (!file || !currentChannel) return;
  currentChannel.sendFileMessage(file, (msg, err) => {
    if (err) return console.error(err);
    appendMessageToUI(msg);
  });
});