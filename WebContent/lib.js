/**
 * 
 */
function toggle(source) {
	checkboxes = document.querySelectorAll('[id^=feed_id]');
	  for(var i=0, n=checkboxes.length;i<n;i++) {
	    checkboxes[i].checked = source.checked;
	  }
}