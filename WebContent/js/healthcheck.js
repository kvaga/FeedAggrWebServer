/**
 * 
 */
 
function createTableForCheckZombiFeedsInCompositeFeeds(objData, id){
	//let body = document.getElementsByTagName("body")[0];
	let div = document.getElementById(id);
	let mTable     = document.createElement("table");
  	let mTableBody = document.createElement("tbody");
  	mTableBody.appendChild(createTableHeaderRow('Composite Feed', 'FeedId'));
  	Object.entries(objData).map(item => {
		Object.entries(item[1]).map(it => {
			let row = document.createElement("tr");
			//let cellCompositeFeed = document.createElement("td");
			//cellCompositeFeed.appendChild(document.createTextNode(item[0]));
			row.appendChild(createCell(item[0]));
			//let cellZombieFeed = document.createElement("td");
			//cellZombieFeed.appendChild(document.createTextNode(it[1]));
			row.appendChild(createCell(it[1]));
			mTableBody.appendChild(row);
		});
	});
	mTable.appendChild(mTableBody);
  	//body.appendChild(mTable);
  	div.appendChild(mTable);
 
 	//ourTable.setAttribute("border", "2");
	//document.getElementById(id).innerHTML=obj;
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

function createCell(text){
	const cellC = document.createElement("td");
	cellC.appendChild(document.createTextNode(text));
	return cellC;
}

function createTableForCheckDuplicateFeeds(objData, id){
	let div = document.getElementById(id);
	let mTable     = document.createElement("table");
  	let mTableBody = document.createElement("tbody");

	mTableBody.appendChild(createTableHeaderRow('URL', 'Feeds'));
  	Object.entries(objData).map(item => {
		Object.entries(item[1]).map(it => {
			let row = document.createElement("tr");
			//let cellCompositeFeed = document.createElement("td");
			//cellCompositeFeed.appendChild(document.createTextNode(item[0]));
			//row.appendChild(cellCompositeFeed);
			row.appendChild(createCell(item[0]));
			//let cellZombieFeed = document.createElement("td");
			//cellZombieFeed.appendChild(document.createTextNode(it[1]));
			//row.appendChild(cellZombieFeed);
			row.appendChild(createCell(it[1]));
			mTableBody.appendChild(row);
		});
	});
	mTable.appendChild(mTableBody);
  	div.appendChild(mTable);
}

function createTableForCheckAbandonedFeeds(objData, id){
	let div = document.getElementById(id);
	let mTable     = document.createElement("table");
  	let mTableBody = document.createElement("tbody");

	mTableBody.appendChild(createTableHeaderRow('Feeds'));
  	Object.entries(objData).map(item => {
		let row = document.createElement("tr");
		row.appendChild(createCell(item[1]));
		mTableBody.appendChild(row);
			/*
		Object.entries(item[1]).map(it => {
			let row = document.createElement("tr");
			//row.appendChild(createCell(item[0]));
			console.log('it: ', it);
			row.appendChild(createCell(it[1]));
			mTableBody.appendChild(row);
		});
		*/
	});
	mTable.appendChild(mTableBody);
  	div.appendChild(mTable);
}
