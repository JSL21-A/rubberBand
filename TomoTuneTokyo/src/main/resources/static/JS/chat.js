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
  function loadChannelList(autoOpen = true, onLoaded = null) {
    channelsQuery = sb.GroupChannel.createMyGroupChannelListQuery();
    channelsQuery.limit = 20;
    channelsQuery.next((channels, err) => {
      if(err){ console.error('채널 조회 실패', err); return; }
	  
	  console.log('▶ fetched channels:', channels.map(c => c.url));   // ← 추가
	  
      renderChannelList(channels);
	  // (1) 먼저 콜백 실행
	     if (onLoaded){
			
			console.log('▶ calling onLoaded callback');                  // ← 추가

		  onLoaded(channels)};
		 

	     // (2) autoOpen 이 true 면 첫 번째 방 열기
	     if (autoOpen && channels.length) {
	       openChannel(channels[0]);
	     }
    });
  }

  // ─────────────────────────────────────────
  // 4) 채널 목록 렌더링
  // ─────────────────────────────────────────
  function renderChannelList(channels) {
    const $list = $('.channel-list').empty();
	channels.forEach(ch => {
	    let displayName;

	    // 1:1 distinct 채널일 경우 → 상대방 이름으로 표시
	    if (ch.isDistinct && ch.memberCount === 2) {
	      const other = ch.members.find(m => m.userId !== sb.currentUser.userId);
	      displayName = other.nickname || other.userId;
	    }
	    // 그렇지 않으면 (그룹채널 등) 채널명 혹은 멤버 이름 조합
	    else {
	      const others = ch.members
	        .filter(m => m.userId !== sb.currentUser.userId)
	        .map(m => m.nickname || m.userId)
	        .join(', ');
	      displayName = ch.name || others || '채널';
	    }

	    const $item = $(`<div class="channel-item">${displayName}</div>`);
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
  async function createOneOnOneChannel() {
    const targetId = $('#new-chat-user').val().trim();
    if (!targetId) return alert('상대방 ID를 입력하세요');

    try {
      const params = new sb.GroupChannelParams();
      params.isDistinct = true;
      params.addUserId(sb.currentUser.userId);
      params.addUserId(targetId);

      // 생성
      const channel = await new Promise((resolve, reject) => {
        sb.GroupChannel.createChannel(params, (ch, err) =>
          err ? reject(err) : resolve(ch)
        );
      });

      // 1:1 채널이라면 상대 이름 표시
      const other = channel.members.find(m => m.userId !== sb.currentUser.userId);
      const displayName = other.nickname || other.userId;

      // 채널 리스트 맨 앞에 “수동으로” 추가
      const $item = $(`<div class="channel-item">${displayName}</div>`);
      $item.on('click', () => openChannel(channel));
      $('.channel-list').prepend($item);

      // 바로 열기
      openChannel(channel);
      $('#new-chat-user').val('');
    } catch (err) {
      console.error('채널 생성 에러', err);
      alert(err.message || '채널 생성에 실패했습니다.');
    }
  }
  
  function bindChatUIEvents() {
    $('#chat-toggle').off('click').on('click', () => $('#chat-wrapper').toggleClass('open'));
    $('.chat-close').off('click').on('click', () => $('#chat-wrapper').removeClass('open'));
    $('#attach-btn').off('click').on('click', () => $('#file-input').click());
    $('#new-chat-btn').off('click').on('click', createOneOnOneChannel);
    $('#send-btn').off('click').on('click', sendMessage);
    $('#message-input').off('keypress').on('keypress', e => { if (e.key==='Enter') sendMessage(); });
    $('#file-input').off('change').on('change', function(){ sendFile(this.files[0]); this.value=null; });
  }
  
  //채팅버튼 눌러서 채팅 연결
  $('#btn-chat').off('click').on('click', function(){
	const targetId = $(this).data('user-id')
	if (!targetId){
		return alert('リーダーのIDが取得できませんでした');
	}
	
	if(!$('#chat-wrapper').hasClass('open')){
		$('#chat-toggle').trigger('click');
	}
	
	createDirectChannel(targetId);
  })
  // 2) createOneOnOneChannel 로직을 재활용한 helper
  async function createDirectChannel(targetId) {
    if (!sb || !sb.currentUser) {
      return alert('チャット初期化中です。しばらくして再度お試しください。');
    }

    const me = sb.currentUser.userId;
    const params = new sb.GroupChannelParams();
    params.isDistinct = true;
    params.addUserId(me);
    params.addUserId(targetId);

    try {
      const channel = await new Promise((resolve, reject) => {
        sb.GroupChannel.createChannel(params, (ch, err) =>
          err ? reject(err) : resolve(ch)
        );
      });

      // displayName 계산
      const other = channel.members.find(m => m.userId !== me);
      const displayName = other.nickname || other.userId;

      // 중복 체크용 data-url 심어서 아이템 생성
      const $item = $(`
        <div class="channel-item" data-url="${channel.url}">
          ${displayName}
        </div>
      `);
      $item.on('click', () => openChannel(channel));

      // 이미 있으면 추가하지 않음
      if (!$(`.channel-item[data-url="${channel.url}"]`).length) {
        $('.channel-list').prepend($item);
      }

      openChannel(channel);
    } catch (err) {
      console.error('ダイレクトチャネル作成失敗', err);
      alert('チャットを開始できませんでした。');
    }
  }
  

})(jQuery);