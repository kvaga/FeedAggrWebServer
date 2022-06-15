/**
 * 
 */

/*
function changeRssItemPropertiesPatternsForDomain(object){
	//let domainId = document.getElementById(domainId);
	let id=object.id;
	let domain = encodeURIComponent(document.getElementById('domainId'+id).innerHTML);
	let pattern = encodeURIComponent(document.getElementById('textArea'+id).innerHTML);
	//let dataForm = new FormData();
	//dataForm.append("domain", encodeURIComponent(domain));
	//dataForm.append("pattern", pattern);
	//dataForm.append("command",encodeURIComponent("changerssetempropertiespatternsfordomain"));
	
	//request = "domain="+encodeURIComponent(domain)
	//			//+"&command="+encodeURIComponent("changerssetempropertiespatternsfordomain")
	//			+"&rssItemPropertiesPatternsByDomains="+encodeURIComponent(pattern)
	//;
	
	request = "domain="+domain
				//+"&command="+encodeURIComponent("changerssetempropertiespatternsfordomain")
				+"&rssItemPropertiesPatternsByDomains="+pattern
	;
	//console.log(`url: ${urlUserFileRssItemPropertiesPatternsByDomains}, id: ${object.id}, domain: ${domain.innerHTML}, pattern: ${pattern.innerHTML}`);
	getGetJSONContentFromURL(urlUserFilechangeRssItemPropertiesPatternsForDomain, onErr, function x(){}, 
	//"domain="+encodeURIComponent(123)+"&command="+encodeURIComponent('qqq')
	// dataForm
	request);	
}
*/

/*
function applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomainsTable(object){
	let id=object.id;
	let domain = encodeURIComponent(document.getElementById('domainId'+id).innerHTML);
	let pattern = encodeURIComponent(document.getElementById('textArea'+id).innerHTML);
	request = "domain="+domain
			+"&rssItemPropertiesPatternsByDomains="+pattern
	;
	getGetJSONContentFromURL(urlApplyToAllCorrespondingRSSItemContentTemplateByDomainsForDomain, onErr, function x(resp){
		document.getElementById(response).innerHTML=resp;
	}, request);	
}
*/
/*
function applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomainsTable(objData, id){
	let counterId=0;
	let div = document.getElementById(id);
	let mTable     = document.createElement("table");
  	let mTableBody = document.createElement("tbody");
  	mTableBody.appendChild(createTableHeaderRow('Domain', 'Change Template', 'Rss Item Content Template', 'Apply Template to All corresponding Feeds'));
  	Object.entries(objData).map(item => {
		let row = document.createElement("tr");
		let domainCell = createCell(item[1].domain);
		domainCell.setAttribute('id','domainId'+counterId); 
		row.appendChild(domainCell);
		//let link = createLink('#url','Change Template', 'changeRssItemPropertiesPatternsForDomain(this)');
		link.setAttribute('id', counterId);
		row.appendChild(createCell(link));
		let textArea = document.createElement("textarea");
		textArea.setAttribute('id','textArea'+counterId);
		textArea.append(item[1].patternDescription);
		row.appendChild(createCell(textArea));
		row.appendChild(createCell(createLink('#url','Apply Template to All corresponding Feeds')));
		mTableBody.appendChild(row);
		counterId++;
	});
	mTable.appendChild(mTableBody);
	mTable.setAttribute("border", "2");

	div.appendChild(mTable);	
}
*/

function createRepeatableSearchPatternsByDomainsTable(objData, divId){
	let counterId=0;
	let div = document.getElementById(divId);
	let mTable     = document.createElement("table");
  	let mTableBody = document.createElement("tbody");
  	mTableBody.appendChild(createTableHeaderRow('Domain', 'Change', 'Repeatable Search Patterns', 'Apply Template to All corresponding Feeds'));
  	Object.entries(objData).map(item => {
		let row = document.createElement("tr");
		let domainCell = createCell(item[1].domain);
		let changeCell = createCell('Change');
		let applyAllCell = createCell();
		let form = createForm(urlChangeRepeatableSearchPatternsForDomain, "POST", 'form-repeatable-search-pattern-'+counterId);
		let buttonApplyAll = createButton('Apply All', counterId);
		buttonApplyAll.className="button";
		buttonApplyAll.addEventListener("click", function (event) {
			console.log('this.id: ' + event.target.value);
			let form = document.getElementById('form-repeatable-search-pattern-'+event.target.value);
			form.action=urlApplyToAllCorrespondingRepeatableSearchPatternsByDomainsForDomain;
			form.append(createInput('hidden', 'command', 'applyToAllCorrespondingDomainsRepeatableSearchPatternByDomains'));
   			form.submit();
		});
			
		form.append(createTextArea("repeatableSearchPatternByDomain", item[1].pattern));
		form.append(createInput("submit", "Change", "Change"));
		form.append(createInput("hidden", "domain", item[1].domain));
		//form.append(button);
		row.appendChild(domainCell);
		row.appendChild(changeCell);
		row.appendChild(createCell(form));
		applyAllCell.append(buttonApplyAll);
		row.appendChild(applyAllCell);

		
		//row.appendChild(createCell(createLink(urlApplyToAllCorrespondingRSSItemContentTemplateByDomainsForDomain,'Apply Template to All corresponding Feeds', 'applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomainsTable(this)')));
		mTableBody.appendChild(row);
		counterId++;
	});
	mTable.appendChild(mTableBody);
	mTable.setAttribute("border", "2");

	div.appendChild(mTable);	
}
function createRSSItemContentTemplateByDomainsTable(objData, divId){
	let counterId=0;
	let div = document.getElementById(divId);
	let mTable     = document.createElement("table");
  	let mTableBody = document.createElement("tbody");
  	mTableBody.appendChild(createTableHeaderRow('Domain', 'Change', 'RSS Item Content Template', 'Apply Template to All corresponding Feeds'));
  	Object.entries(objData).map(item => {
		let row = document.createElement("tr");
		let domainCell = createCell(item[1].domain);
		let changeCell = createCell('Change');
		let applyAllCell = createCell();
		let form = createForm(urlChangeRSSItemContentTemplateByDomainsForDomain, "POST", 'form-rss-item-content-template-'+counterId);
		let buttonApplyAll = createButton('Apply All', counterId);
		buttonApplyAll.className="button";
		buttonApplyAll.addEventListener("click", function (event) {
			console.log('this.id: ' + event.target.value);
			let form = document.getElementById('form-rss-item-content-template-'+event.target.value);
			form.action=urlApplyToAllCorrespondingRSSItemContentTemplateByDomainsForDomain;
			form.append(createInput('hidden', 'command', 'applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomains'));
   			//form.find('input#hidden').attr('value', 'data:image/png;base64,alotoftext');
   			//frm.find('input#changeme').remove();
  			form.submit();
		});
			
		form.append(createTextArea("rssItemPropertiesPatternsByDomains", item[1].patternDescription));
		form.append(createInput("submit", "Change", "Change"));
		form.append(createInput("hidden", "domain", item[1].domain));
		//form.append(button);
		row.appendChild(domainCell);
		row.appendChild(changeCell);
		row.appendChild(createCell(form));
		applyAllCell.append(buttonApplyAll);
		row.appendChild(applyAllCell);

		
		//row.appendChild(createCell(createLink(urlApplyToAllCorrespondingRSSItemContentTemplateByDomainsForDomain,'Apply Template to All corresponding Feeds', 'applyToAllCorrespondingDomainsRssItemPropertiesPatternsByDomainsTable(this)')));
		mTableBody.appendChild(row);
		counterId++;
	});
	mTable.appendChild(mTableBody);
	mTable.setAttribute("border", "2");

	div.appendChild(mTable);	
}