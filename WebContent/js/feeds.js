/**
 * 
 */
 
function isFeedPaused(status){
	if(status.toLowerCase()==='paused'){
		return true;
	}
	return false;
}

function isErrorOnLastUpdateStatus(status){
	if(!status){
		return false;
	}
	if(status.toLowerCase()==='null'){
		return true;
	}
	return false;
}