$(function () {
    // 돌아돌아 돌림판
    gsap.registerPlugin(Draggable);

    var menu = $(".menu-inner");
    let rotation = 0;
    let velocity = 0;
    let isDragging = false;
    let lastRotation = 0;
    let raf;

    // 돌아가게 div태그에 draggable 설정
    const draggable = Draggable.create(menu.get(0), {
        type: "rotation",
        onDragStart: function () {
            isDragging = true;
            cancelAnimationFrame(raf);
            velocity = 0;
        },
        onDrag: function () {
            rotation = gsap.getProperty(menu.get(0), "rotation");
            velocity = rotation - lastRotation;
            lastRotation = rotation;
        },
        onDragEnd: function () {
            isDragging = false;
            animateInertia();
        },
    });

    //관성 설정
    function animateInertia() {
        if (!isDragging) {
            // 점점 속도 줄게 (velocity = 감속 계수)
            velocity *= 0.95;
            rotation += velocity;

            gsap.set(menu.get(0), { rotation });

            // 속도가 거의 0이면 스냅 처리
            if (Math.abs(velocity) > 0.5) {
                raf = requestAnimationFrame(animateInertia);
            } else {
                snapToClosestAngle(); // 가장 가까운 각도로 스냅
            }
        }
    }

    function snapToClosestAngle() {
        let nextMultiple;

        const isClockwise = rotation > lastRotation; // 시계방향 회전 여부 확인

        // 시계방향 (양수로 증가) 일 때
        if (isClockwise) {
            nextMultiple = Math.ceil(rotation / 15) * 15; // 다음 15도 배수
        }
        // 반시계방향 (음수로 감소) 일 때
        else {
            nextMultiple = Math.floor(rotation / 15) * 15; // 이전 15도 배수
        }

        gsap.to(menu.get(0), {
            rotation: nextMultiple,
            duration: 0.7,
            ease: "power1.out",
        });

        $("#rotation").click(function () {
            console.log(gsap.getProperty(menu.get(0), "rotation"), "from element");
            console.log(Draggable.get(menu.get(0)).rotation, "from the Draggable");
        });
    }

    function cardLayout() {
        var $container = $(".menu-inner");
        var $cards = $container.find(".menu-card");
        var totalCards = $cards.length; // 카드 개수에 따라 동적 계산

        // 반지름 및 위치 계산
        const radius = (parseFloat(getComputedStyle(document.documentElement).getPropertyValue("--diameter")) * window.innerWidth) / 100 / 2; // 1vw = window.innerWidth / 100

        var centerX = $container.width() / 2; //가로 중앙
        var centerY = $container.height() / 2; //세로 중앙

        const cardWidth = radius * 0.2; // 카드 너비는 반지름의 10%로 계산
        const cardHeight = cardWidth * 1.4; // 비율 유지 (예: 2:3 → 1.4배 높이)

        // 첫 번째 카드는 컨테이너 12시 방향에 오게 각도 -90도
        $cards.each(function (index) {
            var angle = ((2 * Math.PI) / totalCards) * index - Math.PI / 2;
            var $card = $(this);
            // 카드 중심이 계산된 좌표에 오도록 카드 크기의 절반씩 빼줌
            var x = centerX + radius * Math.cos(angle) - $card.width() / 2;
            var y = centerY + radius * Math.sin(angle) - $card.height() / 2;
            if (window.matchMedia("(max-width: 768px)").matches) {
                // 모바일용 JS 실행
                console.log("모바일 환경입니다");
                gsap.set($container, {
                    transform: "translate3d(0%, -50%, 0px)",
                });
                $card.css({
                    // 카드 배치 위치 조절
                    left: x + "px",
                    top: y + "px",
                    transform: "rotate(" + ((angle * 180) / Math.PI + 180) + "deg)",
                });
                // 모바일 전용 슬라이드 로직 실행
            } else {
                // 데스크탑용 JS 실행
                console.log("데스크탑 환경입니다");
                gsap.set($container, {
                    transform: "translate3d(-50%, -50%, 0px)",
                });
                $card.css({
                    // 카드 배치 위치 조절
                    left: x + "px",
                    top: y + "px",
                    transform: "rotate(" + ((angle * 180) / Math.PI + 90) + "deg)",
                });
                // PC 전용 드래그 로직 실행
            }
        });
    }
    // 창 크기가 변하면 다시 계산
    cardLayout();
    $(window).on("resize", function () {
        // 창 크기가 변하면 다시 계산
        cardLayout();
    });

    $(".arrow").on("click", function () {
        rotation = gsap.getProperty(menu.get(0), "rotation");
        if ($(this).hasClass("right")) {
            RightRotation = rotation - 15;
        } else {
            RightRotation = rotation + 15;
        }
        gsap.to(menu.get(0), { rotation: RightRotation });
    });
});
