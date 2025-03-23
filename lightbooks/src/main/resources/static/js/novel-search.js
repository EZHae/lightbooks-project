/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
	
	window.addEventListener('scroll', () => {
	  if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 100) {
	    getAllComments(currentPageNo + 1);
	  }
	});
	
});