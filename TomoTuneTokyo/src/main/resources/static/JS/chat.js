// static/JS/chat.js
(function($){
	
	$(function(){
		console.log('currentUser=', window.currentUser);
		if (window.currentUser && !window.currentUser.startsWith('anonymous')) {
		  initChat(window.currentUser);
		}
	});
	
  const APP_ID = '016A2743-AB66-4FFF-9F77-D08966152ABB';  // 본인 SendBird App ID
  let sb = null, channelsQuery = null, currentChannel = null;

  // ─────────────────────────────────────────
  // 1) 페이지 로드 시 이벤트 바인딩
  // ─────────────────────────────────────────
  $(function(){
    // 채팅창 토글
    $('#chat-toggle').on('click',      () => $('#chat-wrapper').toggleClass('open'));
    $('.chat-close').on('click',       () => $('#chat-wrapper').removeClass('open'));
    // 파일 첨부 버튼
    $('#attach-btn').on('click',       () => $('#file-input').click());
    // 1:1 채팅 생성
    $('#new-chat-btn').on('click',     createOneOnOneChannel);
    // 텍스트 전송
    $('#send-btn').on('click',         sendMessage);
    $('#message-input').on('keypress', e => { if(e.key==='Enter') sendMessage(); });
    // 파일 전송
    $('#file-input').on('change',      function(){ sendFile(this.files[0]); this.value = null; });
  });

  // ─────────────────────────────────────────
  // 2) 로그인 성공 후 initChat(userId)를 호출하세요.
  //    전역 함수로 노출됩니다.
  // ─────────────────────────────────────────
  window.initChat = async function(userId) {
    sb = new SendBird({ appId: APP_ID });
    sb.connect(userId, null, (user, err) => {
      if(err){ console.error('SendBird connect 실패', err); return; }
      $('#new-chat-btn').prop('disabled', false);
      loadChannelList();
	  updateTotalBadge();
	  const handler = new sb.ChannelHandler();
	  handler.onMessageReceived = updateTotalBadge;
	  handler.onReadReceiptUpdated = updateTotalBadge;
	  handler.onChannelChanged = updateTotalBadge;
	  sb.addChannelHandler('UNREAD_HANDLER', handler);
    });
  };
  
  //2-1))읽지 않은 메시지 개수 표시
  function updateTotalBadge(){
	sb.getTotalUnreadMessageCount((count, error) => {
		if (error) return console.error('unread count error', error);
		const $b = $('#chat-badge');
		if(count > 0) {
			$b.text(count).show();
		}else{
			$b.hide();
		}
	})
  }

  // ─────────────────────────────────────────
  // 3) 채널 리스트 로드
  // ─────────────────────────────────────────
  function loadChannelList() {
    channelsQuery = sb.GroupChannel.createMyGroupChannelListQuery();
    channelsQuery.limit = 20;
    channelsQuery.next((channels, err) => {
      if(err){ console.error('채널 조회 실패', err); return; }
      renderChannelList(channels);
      if(channels.length) openChannel(channels[0]);
    });
  }

  // ─────────────────────────────────────────
  // 4) 채널 목록 렌더링
  // ─────────────────────────────────────────
  function renderChannelList(channels) {
    const $list = $('.channel-list').empty();
    channels.forEach(ch => {
      // 상대방 닉네임 또는 채널명
      const others = ch.members
        .filter(m => m.userId !== sb.currentUser.userId)
        .map(m => m.nickname || m.userId)
        .join(', ');
      const name = ch.name || others || '채널';
      const $item = $(`<div class="channel-item">${name}</div>`);
      $item.on('click', () => openChannel(ch));
      $list.append($item);
    });
  }

  // ─────────────────────────────────────────
  // 5) 채널 열기 & 과거 메시지 + 실시간 리스너
  // ─────────────────────────────────────────
  function openChannel(ch) {
    currentChannel = ch;
    $('.channel-item').removeClass('active');
    $(`.channel-item:contains("${ch.name||ch.url}")`).addClass('active');

	$('#message-list').empty();
	
	ch.markAsRead();
	updateTotalBadge();
	
	  // 2) PreviousMessageListQuery 생성
	  const prevQuery = ch.createPreviousMessageListQuery();
	  prevQuery.limit = 50;             // 한 번에 가져올 메시지 수
	  prevQuery.reverse = false;         // true 면 최신→과거 순서, false 면 과거→최신 순서
	
	  // 3) load() 호출로 메시지 조회
	  prevQuery.load((msgs, err) => {
	    if (err) {
	      console.error('메시지 조회 실패', err);
	      return;
	    }
	    // load()가 주는 배열은 이미 reverse=false 상태이므로
	    // 그냥 forEach로 렌더해도 올바른 시간순으로 나옵니다.
	    msgs.forEach(renderMessage);
	    scrollToBottom();
	  });

    const handler = new sb.ChannelHandler();
    handler.onMessageReceived = (channel, msg) => {
      if(channel.url === ch.url) renderMessage(msg);
    };
    sb.addChannelHandler('CHAT_HANDLER', handler);
  }

  // ─────────────────────────────────────────
  // 6) 메시지 렌더링 헬퍼
  // ─────────────────────────────────────────
  function renderMessage(msg) {
    const isMe = msg.sender.userId === sb.currentUser.userId;
    const content = msg.message
      ? msg.message
      : `<img src="${msg.url}" style="max-width:200px;">`;
    const $msg = $(`
      <div class="msg ${isMe ? 'my' : 'their'}">
        <span class="sender">${msg.sender.nickname||msg.sender.userId}</span>
        <p class="text">${content}</p>
      </div>
    `);
    $('#message-list').append($msg);
    scrollToBottom();
  }

  // ─────────────────────────────────────────
  // 7) 스크롤 하단으로
  // ─────────────────────────────────────────
  function scrollToBottom() {
    const $ml = $('#message-list');
    $ml.scrollTop($ml[0].scrollHeight);
  }

  // ─────────────────────────────────────────
  // 8) 텍스트 메시지 전송
  // ─────────────────────────────────────────
  function sendMessage() {
    const text = $('#message-input').val().trim();
    if(!text || !currentChannel) return;
    currentChannel.sendUserMessage(text, (msg, err) => {
      if(err){ console.error('메시지 전송 실패', err); return; }
      renderMessage(msg);
      $('#message-input').val('');
    });
  }

  // ─────────────────────────────────────────
  // 9) 파일 메시지 전송
  // ─────────────────────────────────────────
  function sendFile(file) {
    if(!file || !currentChannel) return;
    currentChannel.sendFileMessage(file, (msg, err) => {
      if(err){ console.error('파일 전송 실패', err); return; }
      renderMessage(msg);
    });
  }

  // ─────────────────────────────────────────
  // 10) 1:1 채널 생성
  // ─────────────────────────────────────────
  function createOneOnOneChannel() {
    const targetId = $('#new-chat-user').val().trim();
    if (!targetId) {
      alert('상대방 ID를 입력하세요');
      return;
    }

    // === 수정 시작 ===
    // 1) GroupChannelParams 생성
    const params = new sb.GroupChannelParams();
    params.isDistinct = true;
    params.addUserId(sb.currentUser.userId);
    params.addUserId(targetId);

    // 2) 이 Params 인스턴스를 넘겨서 채널 생성
    sb.GroupChannel.createChannel(params, (channel, error) => {
      if (error) {
        console.error('채널 생성 실패', error);
        alert('채널 생성 에러: ' + error.message);
        return;
      }
      // 3) 생성 성공 시 리스트 갱신 및 채널 열기
      loadChannelList();
      openChannel(channel);
      $('#new-chat-user').val('');
    });
    // === 수정 끝 ===
  }

})(jQuery);