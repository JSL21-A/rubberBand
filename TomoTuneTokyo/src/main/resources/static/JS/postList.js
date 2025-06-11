function postListJS() {
    $("#doctor_paging").empty();
    pagingBase($(".post_list").data("count"), 10);
    cateOn();

    $(".sel_cate_card").on("click", function () {
        cateOn();
    });

    $(".pagingBtn").on("click", function () {
        const url = new URL(window.location.href);

        // 기존 파라미터 유지하고 새 파라미터 추가
        url.searchParams.set("page", 2);

        history.pushState({}, "", url.toString());
    });

    function cateOn() {
        const board = new URLSearchParams(window.location.search).get("board");

        // 확실하게 모든 active를 제거
        $(".sel_cate_card").removeClass("active");

        // 정확한 타겟 선택
        const target = $(`a[data-board-id="${board ?? ""}"]`);
        const parent = target.parent(".sel_cate_card");

        if (parent.length) {
            parent.addClass("active");
            $(".cate_title").text(target.text());
        }
    }

    function pagingBase(data, itemsPerPage) {
        //총 페이지 계산
        const totalPages = data == 0 ? 1 : Math.ceil(data / itemsPerPage);
        //현재페이지 기본 1
        let currentPage = 1;
        const url = new URL(window.location.href);

        // page 파라미터가 있는지 확인하고 현재페이지 설정
        if (url.searchParams.has("page")) {
            currentPage = parseInt(url.searchParams.get("page"));
        }

        //페이징 할 div 가져오기
        const pagingContainer = $("#doctor_paging");
        //페이징 박스에 보여줄 시작과 끝 설정
        let startPage = 1;
        if (currentPage > 5) {
            startPage = currentPage - 5;
        }
        let endPage = startPage + 9;
        if (endPage > totalPages) {
            endPage = totalPages;
            startPage = totalPages - 9;
        }
        if (startPage < 1) {
            startPage = 1;
        }

        const moveToFirst = $("<button>", {
            text: "<<",
            class: "pageBtn",
            type: "button",
        })
            .prop("hidden", currentPage === 1)
            .on("click", () => {
                const url = new URL(window.location.href);
                url.searchParams.set("page", 1);
                window.location.href = url.toString();
            });

        pagingContainer.append(moveToFirst);

        for (let i = startPage; i <= endPage; i++) {
            const pagingButton = $("<button>", {
                text: i,
                class: "pageBtn",
                type: "button",
            }).on("click", () => {
                const url = new URL(window.location.href);
                const pageValue = i;
                const pageParam = "page";

                if (!url.searchParams.has(pageParam)) {
                    url.searchParams.append(pageParam, pageValue);
                } else {
                    url.searchParams.set(pageParam, pageValue);
                }

                window.location.href = url.toString();
            });

            // 현재 페이지일 경우 비활성화
            if (i === currentPage) {
                pagingButton.prop("disabled", true);
                pagingButton.addClass("active");
            }

            // DOM에 추가
            pagingContainer.append(pagingButton);
        }
        const moveToEnd = $("<button>", {
            text: ">>",
            class: "pageBtn",
            type: "button",
        })
            .prop("hidden", currentPage === totalPages)
            .on("click", () => {
                const url = new URL(window.location.href);
                url.searchParams.set("page", totalPages);
                window.location.href = url.toString();
            });

        pagingContainer.append(moveToEnd);
    }
}
