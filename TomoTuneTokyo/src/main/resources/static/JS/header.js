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
  
//햄버거 메뉴 슬라이드
$('#hamburgerBtn').hover(
  function () {
    $('#slideMenu').addClass('open');
  },
  function () {
    setTimeout(() => {
      if (!$('#slideMenu').is(':hover')) {
        $('#slideMenu').removeClass('open');
      }
    }, 300);
  }
);

$('#slideMenu').mouseleave(function () {
  $(this).removeClass('open');
});