/**
 * 
 */

// 스와이퍼 시작
document.addEventListener('DOMContentLoaded', () => {
	// 스와이퍼 시작
	const swiper = new Swiper('.swiper', {
	  // Optional parameters
	  slidesPerView: 1.8,     //  너비
	  centeredSlides: true,
	  spaceBetween: 10, 
	  loop: true,
	  
	  autoplay: {
	    delay: 3000,
	  },
	
	  // If we need pagination
	  pagination: {
	    el: '.swiper-pagination',
		clickable: true,
	  },
	
	  // Navigation arrows
	  navigation: {
	    nextEl: '.swiper-button-next',
	    prevEl: '.swiper-button-prev',
		
	  },
	
	  // And if we need scrollbar
	  scrollbar: {
	    el: '.swiper-scrollbar',
	  },
	});
	
})