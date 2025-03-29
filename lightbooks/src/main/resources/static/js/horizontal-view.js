/**
 * 회차 상세보기의 가로보기 기능
 */
(function($) { // jQuery를 인자로 받아서 $가 jQuery를 가리키도록 보장
    $(document).ready(function() {
        $('#horizontalViewButton').on('click', function() {
            $('.episode-content').hide();
            $('#book').show();
            loadHorizontalView();
        });
    });

function loadHorizontalView() {
    const content = $('.episode-content').html();
    const screenWidth = $(window).width();
    const screenHeight = $(window).height();
    const fontSize = parseFloat($('.episode-content').css('font-size'));
    const lineHeight = parseFloat($('.episode-content').css('line-height'));
    const avgCharWidth = fontSize * 0.6;
    const avgCharHeight = lineHeight;
    const charsPerPage = Math.floor((screenWidth / 2) / avgCharWidth) * Math.floor(screenHeight / avgCharHeight);

    function splitContentIntoPages(text, charsPerPage) {
        const pages = [];
        let currentPage = '';
        const words = text.split(' ');
        for (const word of words) {
            if ((currentPage + word).length + 1 > charsPerPage) { // 단어 끝에 공백하나 추가해서 계산함
                pages.push(currentPage.trim());
                currentPage = word + ' ';
            } else {
                currentPage += word + ' ';
            }
        }
        pages.push(currentPage.trim());
        return pages;
    }

    const pages = splitContentIntoPages(content, charsPerPage);
    $('#book').empty(); // 기존 내용을 비웁니다.
    pages.forEach(pageContent => {
      let pageDiv = $('<div class="page"></div>'); // 페이지 요소를 생성합니다.
      pageDiv.html(pageContent); // 내용을 페이지 요소에 넣습니다.
      $('#book').append(pageDiv); // 페이지 요소를 #book에 추가합니다.
    });

    $('#book').turn({
        width: screenWidth,
        height: screenHeight,
        autoCenter: true,
        pages: pages.length,
        gradients: true,
        acceleration: true
    });
	}
	})(jQuery);