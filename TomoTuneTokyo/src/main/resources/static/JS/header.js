function header() {
    //CSRF토큰 불러오기
    $(function () {
        const token = $('meta[name="_csrf"]').attr("content");
        const header = $('meta[name="_csrf_header"]').attr("content");

        $.ajaxSetup({
            beforeSend: (xhr) => {
                xhr.setRequestHeader(header, token);
            },
        });
    });
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    //로그인 요청 시 우측 사이드바 오픈
    $(function () {
        const params = new URLSearchParams(window.location.search);
        if (params.has("openLogin")) {
            $("#slideMenu").addClass("open");

            const urlWithoutParams = window.location.pathname;
            window.history.replaceState({}, document.title, urlWithoutParams);
        }
    });

    document.addEventListener("DOMContentLoaded", () => {
        document.querySelectorAll(".hamburger-icon").forEach((img) => {
            // 원본 src, hover src 불러오기
            const originalSrc = img.getAttribute("src");
            const hoverSrc = img.getAttribute("data-hover-src");

            // 마우스 올릴 때
            img.addEventListener("mouseenter", () => {
                img.setAttribute("src", hoverSrc);
            });

            // 마우스 뗄 때
            img.addEventListener("mouseleave", () => {
                img.setAttribute("src", originalSrc);
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
    $("#hamburgerBtn").on("click", function (e) {
        e.stopPropagation(); // 문서 클릭에 의한 즉시 닫힘 방지
        $("#slideMenu").toggleClass("open");
    });
    $(document).on("click", function (e) {
        if (!$(e.target).closest("#hamburgerBtn, #slideMenu").length) {
            $("#slideMenu").removeClass("open");
        }
    });

    $("#slideMenu").on("click", function (e) {
        e.stopPropagation();
    });

    $(document).on("click", function () {
        $("#slideMenu").removeClass("open");
    });

    document.addEventListener("DOMContentLoaded", () => {
        const slideMenu = document.getElementById("slideMenu");
        const closeBtn = slideMenu.querySelector(".close-btn");
        closeBtn.addEventListener("click", () => {
            slideMenu.classList.remove("open");
        });
    });

    const usernameInput = document.querySelector('input[name="username"]');
    usernameInput.addEventListener("invalid", (e) => {
        e.target.setCustomValidity("IDを入力してください");
    });
    usernameInput.addEventListener("input", (e) => {
        e.target.setCustomValidity("");
    });
    const passwordInput = document.querySelector('input[name="password"]');
    passwordInput.addEventListener("invalid", (e) => {
        e.target.setCustomValidity("パスワードを入力してください");
    });
    passwordInput.addEventListener("input", (e) => {
        e.target.setCustomValidity("");
    });
    const registerUsername = document.querySelector('.register-form input[name="username"]');
    registerUsername.addEventListener("invalid", (e) => {
        e.target.setCustomValidity("IDを入力してください");
    });
    registerUsername.addEventListener("input", (e) => {
        e.target.setCustomValidity("");
    });
    const registerPassword = document.querySelector('.register-form input[name="password"]');
    registerPassword.addEventListener("invalid", (e) => {
        e.target.setCustomValidity("パスワードを入力してください");
    });
    registerPassword.addEventListener("input", (e) => {
        e.target.setCustomValidity("");
    });
    const registerPasswordConfirm = document.querySelector('.register-form input[name="passwordConfirm"]');
    registerPasswordConfirm.addEventListener("invalid", (e) => {
        e.target.setCustomValidity("パスワード確認を入力してください");
    });
    registerPasswordConfirm.addEventListener("input", (e) => {
        e.target.setCustomValidity("");
    });
    const registerNickname = document.querySelector('.register-form input[name="nickname"]');
    registerNickname.addEventListener("invalid", (e) => {
        e.target.setCustomValidity("ニックネームを入力してください");
    });
    registerNickname.addEventListener("input", (e) => {
        e.target.setCustomValidity("");
    });
    const registerEmail = document.querySelector('.register-form input[name="email"]');
    registerEmail.addEventListener("invalid", (e) => {
        e.target.setCustomValidity("ニックネームを入力してください");
    });
    registerEmail.addEventListener("input", (e) => {
        e.target.setCustomValidity("");
    });

    $(function () {
        // 로그인 ↔ 회원가입 폼 전환
        $(".register-link").on("click", function (e) {
            e.preventDefault();
            $(".login-form").hide();
            $(".register-form").show();
        });
        $(".back-to-login").on("click", function (e) {
            e.preventDefault();
            $(".register-form").hide();
            $(".login-form").show();
        });
    });
    function goToNotification() {
        document.getElementById("mainPane").style.display = "none";
        const pane = document.getElementById("notificationPane");
        pane.style.display = "block";

        fetch("/notify/unread")
            .then((res) => res.json())
            .then((list) => {
                const ul = document.getElementById("notificationList");
                ul.innerHTML = "";
                if (list.length === 0) {
                    ul.innerHTML = '<li class="no-notify">新しい通知がありません</li>';
                } else {
                    list.forEach((n) => {
                        const li = document.createElement("li");
                        li.className = "notify-item";

                        const a = document.createElement("a");
                        a.textContent = n.message;
                        a.href = n.url;
                        a.style.display = "block";
                        a.style.padding = "0.5rem 0";
                        a.style.borderBottom = "1px solid #ddd";
                        a.style.color = "#fff";
                        a.addEventListener("mouseover", () => (a.style.textDecoration = "underline"));
                        a.addEventListener("mouseout", () => (a.style.textDecoration = "none"));

                        // 클릭 시: ① 읽음 처리 API 호출 → ② 원래 URL로 이동
                        a.addEventListener("click", (e) => {
                            e.preventDefault();
                            fetch(`/notify/read/${n.notificationId}`, {
                                method: "POST",
                                headers: { [csrfHeader]: csrfToken },
                                credentials: "same-origin",
                            })
                                .then((res) => {
                                    if (!res.ok) throw new Error(res.status);
                                    a.parentElement.remove();
                                })
                                .catch(console.error)
                                .finally(() => {
                                    window.location.href = n.url;
                                });
                        });

                        li.appendChild(a);
                        ul.appendChild(li);
                    });
                }
            })
            .catch(console.error);
    }

    // badge 업데이트 함수
    function updateNotifyBadge() {
        fetch("/notify/unread/count", { credentials: "same-origin" })
            .then((res) => res.json())
            .then((count) => {
                document.querySelectorAll(".notification-badge").forEach((badge) => {
                    if (count > 0) {
                        badge.textContent = count;
                        badge.style.display = "block";
                    } else {
                        badge.style.display = "none";
                    }
                });
            })
            .catch(console.error);
    }

    // (1) 페이지 로드 시 초기 한 번
    document.addEventListener("DOMContentLoaded", () => {
        updateNotifyBadge();
    });

    // (2) SSE로 새로운 알림 받으면 갱신
    if (window.currentUser && window.currentUser !== "anonymous") {
        const evtSource = new EventSource("/notify/connect");
        evtSource.addEventListener("notification", (e) => {
            const n = JSON.parse(e.data);
            showToast(n.message, n.url);
            updateNotifyBadge(); // 새 알림 올 때마다 badge 업데이트
        });
    }

    // 돌아가기 버튼 클릭 시 기본 메뉴로 복귀
    function showMainMenu() {
        document.getElementById("notificationPane").style.display = "none";
        document.getElementById("mainPane").style.display = "block";
    }
}
