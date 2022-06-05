/**
 * 
 */
 
function createTableForCheckZombiFeedsInCompositeFeeds(objData, id){
	//let body = document.getElementsByTagName("body")[0];
	let div = document.getElementById(id);
	let mTable     = document.createElement("table");
  	let mTableBody = document.createElement("tbody");
  	Object.entries(objData).map(item => {
		Object.entries(item[1]).map(it => {
			let row = document.createElement("tr");
			let cellCompositeFeed = document.createElement("td");
			cellCompositeFeed.appendChild(document.createTextNode(item[0]));
			row.appendChild(cellCompositeFeed);
			let cellZombieFeed = document.createElement("td");
			cellZombieFeed.appendChild(document.createTextNode(it[1]));
			row.appendChild(cellZombieFeed);
			mTableBody.appendChild(row);
		});
	});
	mTable.appendChild(mTableBody);
  	//body.appendChild(mTable);
  	div.appendChild(mTable);
 
 	//ourTable.setAttribute("border", "2");
	//document.getElementById(id).innerHTML=obj;
}