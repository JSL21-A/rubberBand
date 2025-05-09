let isUsernameValid = false;
let isNicknameValid = false;
let isPasswordMatch = false;
let isEmailVerified = false;
//CSRF 헤더 추가
$(function () {
    const token = $('meta[name="_csrf"]').attr("content");
    const header = $('meta[name="_csrf_header"]').attr("content");

    $.ajaxSetup({
        beforeSend: (xhr) => {
            xhr.setRequestHeader(header, token);
        },
    });
});

//로그인
$("#login").on("click", (e) => {
    e.preventDefault();
    const username = $('input[name="username"]').val();
    const password = $('input[name="password"]').val();

    $.post("/user/login", { username, password })
        .done(() => {
            $("#slideMenu").removeClass("open");
            $("#slideMenu .login-form").hide();
            $("#welcomeName").text(username);
            $("#slideMenu .welcome").show();
        })
        .fail((xhr) => {
            $("#loginError").text(xhr.status === 401 ? "IDやパスワードが間違っています" : "エラー発生");
        });
});

//ID 중복 체크
document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("regUsername");
    const msg = document.getElementById("usernameMsg");

    input.addEventListener("blur", () => {
        const v = input.value.trim();

        if (!v) {
            msg.textContent = "";
            return;
        }
        if (v.length < 4 || v.length > 20) {
            msg.textContent = "IDは4文字以上20文字以下で入力してください";
            msg.style.color = "red";
            isUsernameValid = false;
            return;
        }

        fetch(`/user/check-username?username=${encodeURIComponent(v)}`)
            .then((res) => res.json())
            .then((available) => {
                console.log("중복체크 결과:", available, "for", v, "→ msg 엘리먼트:", msg);
                if (available) {
                    msg.textContent = "使用可能なIDです";
                    msg.style.color = "white";
                    isUsernameValid = true;
                } else {
                    msg.textContent = "既に使用中のIDです";
                    msg.style.color = "red";
                    isUsernameValid = false;
                }
            })
            .catch((err) => {
                console.error(err);
                msg.textContent = "通信エラーが発生しました";
                msg.style.color = "red";
                isUsernameValid = false;
            });
    });

    input.addEventListener("input", () => {
        msg.textContent = "";
    });
}); //ID 중복검사 끝

//닉네임 중복검사
document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("nickname");
    const msg = document.getElementById("nickMsg");

    input.addEventListener("blur", () => {
        const v = input.value.trim();

        if (!v) {
            msg.textContent = "";
            return;
        }
        if (v.length < 2 || v.length > 8) {
            msg.textContent = "ニックネームは２文字以上８文字以下で入力してください";
            msg.style.color = "red";
            isNicknameValid = false;
            return;
        }

        fetch(`/user/check-nickname?nickname=${encodeURIComponent(v)}`)
            .then((res) => res.json())
            .then((available) => {
                console.log("중복체크 결과:", available, "for", v, "→ msg 엘리먼트:", msg);
                if (available) {
                    msg.textContent = "使用可能なニックネームです";
                    msg.style.color = "white";
                    isNicknameValid = true;
                } else {
                    msg.textContent = "既に使用中のニックネームです";
                    msg.style.color = "red";
                    isNicknameValid = false;
                }
            })
            .catch((err) => {
                console.error(err);
                msg.textContent = "通信エラーが発生しました";
                msg.style.color = "red";
                isNicknameValid = false;
            });
    });

    input.addEventListener("input", () => {
        msg.textContent = "";
    });
}); //닉네임 중복검사 끝

// 비밀번호 input 요소 가져오기
document.addEventListener("DOMContentLoaded", () => {
    const passwordInput = document.getElementById("password");
    const passwordConfirmInput = document.getElementById("passwordConfirm");
    const pwMatchMsg = document.getElementById("pwMatchMsg");

    isPasswordMatch = false; // 상태 변수 초기화

    // 비밀번호 일치 여부 실시간 검사
    const checkPasswordMatch = () => {
        if (passwordInput.value === passwordConfirmInput.value) {
            pwMatchMsg.textContent = "パスワードが一致しました";
            pwMatchMsg.style.color = "white";
            isPasswordMatch = true;
        } else {
            pwMatchMsg.textContent = "パスワードが一致しません";
            pwMatchMsg.style.color = "red";
            isPasswordMatch = false;
        }
    };

    // 이벤트 리스너 연결
    passwordInput.addEventListener("input", checkPasswordMatch);
    passwordConfirmInput.addEventListener("input", checkPasswordMatch);
});

// 이메일 인증번호 발송
$("#sendEmailCode").on("click", function () {
    const email = $("#email").val().trim();
    if (!email) return $("#emailMsg").text("メールアドレスを入力してください");
    $.post("/user/send-email-code", { email })
        .done(() => {
            $("#emailMsg").text("認証メールを送信しました").css("color", "white");
            $("#emailCodeGroup").show();
            $("#email").prop("disabled", true);
        })
        .fail(() => {
            $("#emailMsg").text("送信に失敗しました");
        });
});

// 이메일 인증번호 검증
$("#verifyEmailCode").on("click", function () {
    const email = $("#email").val().trim();
    const code = $("#emailCode").val().trim();
    if (!code) return $("#codeMsg").text("認証番号を入力してください");
    $.post("/user/verify-email-code", { code })
        .done((res) => {
            if (res === true) {
                $("#codeMsg").text("認証成功").css("color", "white");
                isEmailVerified = true;
                $("#verifyEmailCode").prop("disabled", true);
                $("#emailCode").prop("disabled", true);
            } else {
                $("#codeMsg").text("認証番号が正しくありません").css("color", "red");
                isEmailVerified = false;
            }
        })
        .fail(() => {
            $("#codeMsg").text("エラーが発生しました").css("color", "red");
            isEmailVerified = false;
        });
});

//전체 유효성검사 + 통과시 회원가입 정보 제출

document.addEventListener("DOMContentLoaded", () => {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const registerBtn = document.getElementById("registerBtn");
    registerBtn.addEventListener("click", async (e) => {
        e.preventDefault();

        document.getElementById("regUsername").dispatchEvent(new Event("blur"));
        document.getElementById("nickname").dispatchEvent(new Event("blur"));

        console.log({
            isUsernameValid,
            isNicknameValid,
            isPasswordMatch,
            isEmailVerified,
        });

        if (!(isUsernameValid && isPasswordMatch && isNicknameValid && isEmailVerified)) {
            registerMsg.textContent = "入力内容を確認してください。";
            registerMsg.style.color = "red";
            return;
        }

        const form = document.getElementById("registerForm");
        const formData = new FormData(form);
        const params = new URLSearchParams();

        for (const [key, val] of formData) {
            params.append(key, val);
        }

        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        const res = await fetch("/user/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                [csrfHeader]: csrfToken,
            },
            body: params.toString(),
        });

        if (res.ok) {
            registerMsg.textContent = "登録が完了しました！";
            registerMsg.style.color = "white";
            setTimeout(() => (window.location.href = "/"), 2000);
        } else {
            registerMsg.textContent = "通信エラーが発生しました。";
            registerMsg.style.color = "red";
        }
    });
});
