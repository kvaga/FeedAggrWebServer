/**
 * 
 */
function toggle(source) {
	checkboxes = document.querySelectorAll('[id^=feed_id]');
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

