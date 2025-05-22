// 글 작성 효과적용 스크립트
$(document).on("click", ".write_text-effect", function () {
    effect = $(this).data("effect-type");
    setStyle(effect);
});

$(document).on("change", "#write_font-size", function () {
    setFont(this.value);
});

const fileMap = new Map(); // id → File 객체 저장용

$(document).on("click", "#write_btn-image", function () {
    $("#write_img-selector").click();
});

$(document).on("change", "#write_img-selector", function (e) {
    const files = e.target.files;
    if (files && files.length > 0) {
        Array.from(files).forEach((file) => {
            insertImageWithTracking(file);
        });
    }
    $("#write_img-selector").val(""); // 같은 파일 다시 선택할 수 있게 초기화
});

function insertImageWithTracking(file) {
    const id = crypto.randomUUID();
    const reader = new FileReader();

    reader.addEventListener("load", function () {
        focusEditor();

        // 직접 img 태그 삽입
        const img = document.createElement("img");
        img.src = reader.result;
        img.setAttribute("data-id", id);

        const sel = window.getSelection();
        if (!sel.rangeCount) return;

        const range = sel.getRangeAt(0);
        range.deleteContents();
        range.insertNode(img);

        // 커서 이동
        range.setStartAfter(img);
        range.setEndAfter(img);
        sel.removeAllRanges();
        sel.addRange(range);

        // FileMap에 저장
        fileMap.set(id, file);

        togglePlaceholder();
    });

    reader.readAsDataURL(file);
}

function setStyle(style) {
    document.execCommand(style);
    focusEditor();
}

// 폰트 사이즈 적용 스크립트
function setFont(size) {
    document.execCommand("fontSize", false, size);
    focusEditor();
}

// 버튼 클릭 시 에디터가 포커스를 잃기 때문에 다시 에디터에 포커스를 해줌
function focusEditor() {
    $(".write_note-editable")[0].focus({ preventScroll: true });
}

// 글 카테고리 판단 후 악기 카테고리 표시
function checkOption(sel) {
    option = [1, 3, 4];
    if (!option.includes(sel)) {
        $($(".write_select_box")[1]).addClass("disable");
    } else {
        $($(".write_select_box")[1]).removeClass("disable");
    }
}

$(document).ready(function () {
    const csrfToken = $("meta[name='_csrf']").attr("content");
    const csrfHeader = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(csrfHeader, csrfToken);
    });

    //어느 페이지에서 글쓰기를 했는지 탐색
    boardId = $("#write_board_id").val();

    //위에서 찾은 id를 토대로 카테고리 강조
    $($("li[data-board_id=" + boardId + "]")[0]).addClass("write_on");

    //카테고리에 해당 카테고리 표시
    setText = $($("li[data-board_id=" + boardId + "]")).text();
    $($(".write_select_box")[0]).find(".write_selected span").text(setText);

    checkOption(Number(boardId));
    togglePlaceholder();
});

// 글 쓰기 전에 나오는 경고 문구 표시
function togglePlaceholder() {
    const $editable = $(".write_note-editable")[0]; // jQuery → 실제 DOM
    const html = $editable.innerHTML;

    // 완전히 빈 내용 판단: 태그도 없고 글자도 없을 때만 placeholder 보이게
    const isCompletelyEmpty = html === "" || html === "<br>" || html === "<div><br></div>" || html === "<p><br></p>";

    $(".write_note-placeholder").toggle(isCompletelyEmpty);
}

// 입력이 발생하면 위의 스크립트 실행
$(document).on("input", ".write_note-editable", function () {
    togglePlaceholder();
});

// 카테고리 선택이 발생했을때
$(document).on("click", ".write_select_option li", function () {
    // 모든 항목에서 'on' 클래스 제거
    $(this).siblings(".write_select_option li").removeClass("write_on");

    // 클릭된 항목에 'on' 클래스 추가
    $(this).addClass("write_on");

    // 선택된 카테고리 가져오기
    const selectedText = $(this).text();
    const boardId = $(this).data("board_id");
    const playId = $(this).data("play_id");

    // selected 에 선택된 텍스트 적용
    $(this).closest(".write_select_box").find(".write_selected span").text(selectedText);

    // 주 카테고리일때
    if (boardId) {
        // hidden input에 board_id 값 설정
        $(this).closest(".write_select_box").find('input[name="board_id"]').val(boardId);
        checkOption(boardId);
    }

    // 악기 카테고리 일때
    if (playId) {
        // hidden input에 board_id 값 설정
        $(this).closest(".write_select_box").find('input[name="play_id"]').val(playId);
    }
});

// 옵션 누르면 닫기
$(document).on("click", ".write_select_option li", function () {
    $(".write_select_option").removeClass("show");
});

// 카테고리 옵션 표시
$(document).on("click", ".write_select_box .write_selected", function (e) {
    e.stopPropagation(); // 클릭 전파 방지
    $(".write_select_option").removeClass("show");
    $(this).siblings(".write_select_option").toggleClass("show");
});

// 바깥 누르면 닫기
$(document).on("click", function (e) {
    // 클릭한 요소가 select_box 내부에 있으거나 작성버튼이 아니면 (작성버튼의 경우 입력체크를 위한 용도)닫지 않음
    if (!$(e.target).closest(".write_select_box").length && !$(e.target).closest("#write_submit").length) {
        $(".write_select_option").removeClass("show");
    }
});

// 작성 버튼이 눌렸을때
$(document).on("click", "#write_submit", function () {
    // 제목과 내용이 공백이거나 비어있는지 체크
    titleEmpty = $("#write_post_title").val().trim();
    contentEmpty = $(".write_note-editable").text().trim();

    if (titleEmpty.length < 2) {
        if (titleEmpty == "") {
            alert("タイトルを入力してください。");
        } else {
            alert("二文字以上入力してください。");
        }
        $("#write_post_title").focus();
        return;
    }

    if (contentEmpty.length < 2) {
        if (contentEmpty == "") {
            alert("内容を入力してください。");
        } else {
            alert("二文字以上入力してください。");
        }
        $(".write_note-editable").focus();
        return;
    }

    //부 카테고리가 비활성화가 아닐때.(선택 가능할때)
    title = $("#write_post_title").val();
    if (!$(".write_select_box.play").hasClass("disable") && $("#write_board_id").val() != 7) {
        if ($("#write_play_id").val()) {
            title =
                "[" +
                $(".write_select_box.play")
                    .find("li[data-play_id=" + $("#write_play_id").val() + "]")
                    .text() +
                "]" +
                title;
        } else {
            alert("サブカテゴリーを選択してください。");
            $($(".write_select_box.play")[0]).find(".write_select_option").toggleClass("show");
            return;
        }
    }

    const formData = new FormData();

    const contentHtml = $(".write_note-editable")[0].innerHTML;
    formData.append("content", contentHtml);
    formData.append("title", title);

    const boardId = $("#write_board_id").val();

    formData.append("board_id", boardId);

    const usedIds = new Set(Array.from($(".write_note-editable img")).map((img) => img.getAttribute("data-id")));

    for (let [id, file] of fileMap.entries()) {
        if (usedIds.has(id)) {
            const ext = file.name.split(".").pop();
            formData.append("images", file, id + "." + ext);
        }
    }

    const csrfToken = $("meta[name='_csrf']").attr("content");
    const csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: "./doWrite",
        method: "POST",
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (res) {
            alert("게시물이 등록되었습니다.");
            if (boardId == 7) {
                window.location.href = "./notify";
            } else {
                window.location.href = "./list?board=" + boardId;
            }
        },
        error: function (err) {
            alert("등록 실패");
            console.error(err);
        },
    });
});

//htmx로 동적로드하면 바인딩안되는 망할 상황을 위한 수동바인딩.
document.body.addEventListener("htmx:afterSwap", function (evt) {
    const scripts = evt.detail.target.querySelectorAll("script");
    scripts.forEach((oldScript) => {
        const newScript = document.createElement("script");

        if (oldScript.src) {
            newScript.src = oldScript.src;
            newScript.async = false; // 순서 보장
        } else {
            newScript.textContent = oldScript.textContent;
        }

        document.head.appendChild(newScript);
        document.head.removeChild(newScript); // 깔끔하게 처리
    });
    boardId = $("#write_board_id").val();
    console.log(boardId);
    //카테고리에 해당 카테고리 표시
    setText = $($("li[data-board_id=" + boardId + "]")).text();
    $($(".write_select_box")[0]).find(".write_selected span").text(setText);
    checkOption(Number(boardId));
});
