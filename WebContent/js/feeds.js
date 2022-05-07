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
	console.log(status);
	if(status){
		return true;
	}
	//if(status.toLowerCase()==='null'){
	//	return true;
	//}
	return false;
}