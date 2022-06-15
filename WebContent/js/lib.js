function createLink(url, label, onClick){
	const link = document.createElement("a");
	link.setAttribute('href', url);
	if(onClick){
		link.setAttribute('onClick', onClick);
	}
	link.append(label);
	return link;
}

function createTableHeaderRow(){
	const rowHeader = document.createElement("tr");
	for(let i=0;i<arguments.length;i++){
		let anyCell = document.createElement("th");
		anyCell.appendChild(document.createTextNode(arguments[i]));
		rowHeader.appendChild(anyCell);
	}
	return rowHeader;
}

function createForm(action, method, id){
	const form = document.createElement("form");
	form.action=action;
	form.method=method;
	if(id){
		form.id=id;
	}
	return form;
}

function createInput(type, name, value){
	const input = document.createElement("input");
	input.type=type;
	input.name=name;
	input.value=value;
	return input;
}

function createTextArea(name, value){
	let textArea = document.createElement("textarea");
	textArea.name=name;
	textArea.value=value;
	return textArea;
}

function createCell(text){
	const cellC = document.createElement("td");
	if(text){
		if(typeof text === 'string' || text instanceof String){
			cellC.appendChild(document.createTextNode(text));	
		}else{
			cellC.append(text);
		}
	}
	//if(text.nodeName=="TEXTAREA"){
	//	cellC.append(text);
	//}else{
	//	cellC.appendChild(document.createTextNode(text));	
	//}
	return cellC;
}


function ReadError(message, cause) {
  this.message = message;
  this.cause = cause;
  this.name = 'ReadError';
  //this.stack = cause.stack;
}

function onErr(error){
	exception(error.error);
}

function createButton(name, value, id){
	const button = document.createElement("button");
	button.innerHTML=name;
	button.value=value;
	if(id)
		button.id=id;
	return button;
}
function getGetJSONContentFromURL(url, onErr, onSuccess, postContent){
	try{
	    var xhr1 = new XMLHttpRequest();
	    //Передаёт правильный заголовок в запросе
	    //if(postContent){
			//xhr1.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			//postContent = new FormData(document.forms.person);
		//}
		
		
		
	    xhr1.onreadystatechange = function() {
	        if (xhr1.readyState == 4) {
				obj = JSON.parse(xhr1.responseText);
				//console.log('getGetJSONContentFromURL obj: ' + typeof obj);
				if(! ('error' in obj)){
					//console.log('getGetJSONContentFromURL: Received object: ', obj); 
					//console.log('getGetJSONContentFromURL: Received object type: ', typeof obj); 
					//Object.entries(obj).map(item => {
					//  console.log(item)
					//});
					onSuccess(obj);
				}else{
					onErr(obj);
				}
	        }
	    }
	    if(postContent){
			console.log('postContent: ' + postContent);
		}
	    xhr1.open(postContent?'POST':'GET', url, true);
	    if(postContent /*instanceof FormData*/){
			xhr1.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			/*
			for(let pair of postContent.entries()) {
   				console.log(pair[0]+ ', '+ pair[1]);
   				postContent.set(pair[0], encodeURIComponent(pair[1]));
			}*/
		}

	    xhr1.send(postContent?postContent:null);
	    // xhr.send("foo=bar&lorem=ipsum");
		// xhr.send('string');
		// xhr.send(new Blob());
		// xhr.send(new Int8Array());
		// xhr.send({ form: 'data' });
		// xhr.send(document);
	}catch(err){
		exception(err.name);
	}
}



function escapeHtml(unsafe)
{
	if(unsafe===null) return unsafe;
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
 }
 
function loadingStart(){
	document.getElementById('loading').innerHTML='<p>Loading...</p>';
	console.info('Loading started...'+document.getElementById('loading'));
}

function loadingStop(){
	document.getElementById('loading').innerHTML='';
	console.info('Loading finished');
}

function exception(text){
	document.getElementById('exception').innerHTML='<p><font color="red">Exception: ' + text + '</font></p>';
	loadingStop();
}

function error_text(text){
	return '<font color="red">' + text + '</font>';
}

function deleteTBody(ttable){
	var rowCount = ttable.rows.length;
	for (var i = rowCount - 1; i > 0; i--) {
		ttable.deleteRow(i);
	}
}

/**
 * 
 */
function toggle(source, id='feed_id') {
	console.log('onClick clicked');
	checkboxes = document.querySelectorAll('[id^='+id+']');
	  for(var i=0, n=checkboxes.length;i<n;i++) {
	    checkboxes[i].checked = source.checked;
	  }
}


 
//function show_hide_column(col_no, do_show) {
//	console.log("show_hide_column: col_no ["+col_no+"], do_show ["+do_show+"]");
//	   var tbl = document.getElementById('table1');
//	   console.log("tbl ["+tbl+"]");
////	   var col = tbl.getElementsByTagName('col')[col_no];
//	   var col = tbl.getElementsByTagName('col'+col_no);
//	   console.log("col ["+col+"]")
//	   if (col) {
//		   col[7].style.visibility="hidden";
////	     col.style.visibility=do_show?"":"collapse";
//	   }
//	}

//function show_hide_column(col_no, do_show) {
//	console.log("show_hide_column: col_no ["+col_no+"], do_show ["+do_show+"]");
//    var rows = document.getElementById('table1').rows;
//	console.log("rows ["+rows+"]");
//    for (var row = 0; row < rows.length; row++) {
//        var cols = rows[row].cells;
//    	console.log("cols ["+cols+"], length ["+cols.length+"]");
//        if (col_no >= 1 && col_no <= cols.length) {
//        	console.log("cols["+col_no+"].style.display="+cols[col_no])
//            cols[col_no].style.display = do_show ? '' : 'none';
//        }
//    }
//}
/*
function show_hide_column() {
	  // document.getElementById("mySidenav").style.width = "250px";
	  targList = document.getElementsByClassName("targ");
	  document.getElementsByClassName("targ")
	  if (targList) {
	    for (var x = 0; x < targList.length; x++) {
	      targList[x].style.visibility = "hidden";
	    }
	  }
	  //document.getElementById("targimg").style.textAlign = "center";
	  var table = document.getElementById ("table1");
      table.
	  
	}
*/

