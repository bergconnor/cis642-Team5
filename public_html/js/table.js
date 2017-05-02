//store the marker that was passed by home
//Note: it will only be used if the "more info" was clicked to highlight the clicked marker  
var markerid = sessionStorage.getItem('marker');
sessionStorage.removeItem('marker');

var date          = "none";
var name          = "none";
var orgnization   = "none";
var email         = "none";
var type          = "none";
var lat           = "none";
var log           = "none";
var temperature   = "none";
var precipitation = "none";
var concentration = "none";
var comment       = "none";


var scrollHere = 0;
var orderBy = 1;
var orderType = "asc";
var degree = "f";

function createTable()
{
	
downloadUrl('../lib/create_xml.php', function(data) {
  var xml     = data.responseXML;
  var markers = xml.documentElement.getElementsByTagName('marker');

  var myNode = document.getElementById("table-body");
  while (myNode.firstChild) {
		myNode.removeChild(myNode.firstChild);
  }

  Array.prototype.forEach.call(markers, function(markerElem) {
		date          = markerElem.getAttribute('date');
		name          = markerElem.getAttribute('name');
		orgnization   = markerElem.getAttribute('organization');
		email         = markerElem.getAttribute('email');
		type          = markerElem.getAttribute('type');
		lat           = markerElem.getAttribute('latitude');
		log           = markerElem.getAttribute('longitude');
		temperature   = markerElem.getAttribute('temperature');
		precipitation = markerElem.getAttribute('precipitation');
		concentration = markerElem.getAttribute('concentration');
		comment       = markerElem.getAttribute('comment');

		var dateCell          = document.createElement("td");
		var nameCell          = document.createElement("td");
		var orgnizationCell   = document.createElement("td");
		var emailCell         = document.createElement("td");
		var typeCell          = document.createElement("td");
		var temperatureCell   = document.createElement("td");
		var precipitationCell = document.createElement("td");
		var concentrationCell = document.createElement("td");
		var commentCell       = document.createElement("td");
		var latCell           = document.createElement("td");
		var logCell           = document.createElement("td");

		dateCell.innerHTML          = date;
		nameCell.innerHTML          = name;
		orgnizationCell.innerHTML   = orgnization;
		if (degree == "f")
		{
			var num = Number(temperature);
			temperatureCell.innerHTML = (num).toFixed(2);
			
		}
			
		else
		{
			var num = ((Number(temperature)-32) * 5 / 9);
			temperatureCell.innerHTML = num.toFixed(2);
		}
		precipitationCell.innerHTML = Number(precipitation).toFixed(3);
		concentrationCell.innerHTML = Number(concentration).toFixed(3);
		commentCell.innerHTML       = comment;
		typeCell.innerHTML          = type;
		emailCell.innerHTML         = email;
		latCell.innerHTML           = Number(lat).toFixed(3);
		logCell.innerHTML           = Number(log).toFixed(3);
	var temp  = false
    if(markerid == markerElem.getAttribute('id')) {

      dateCell.style.backgroundColor="yellow";
      nameCell.style.backgroundColor="yellow";
      orgnizationCell.style.backgroundColor="yellow";
      temperatureCell.style.backgroundColor="yellow";
      precipitationCell.style.backgroundColor="yellow";
	  concentrationCell.style.backgroundColor="yellow";
      commentCell.style.backgroundColor="yellow";
      typeCell.style.backgroundColor="yellow";
      emailCell.style.backgroundColor="yellow";
      latCell.style.backgroundColor="yellow";
      logCell.style.backgroundColor="yellow";
	  temp  = true;
    }

    var row = document.createElement("tr");
		row.appendChild(dateCell);
		row.appendChild(nameCell);
		row.appendChild(orgnizationCell);
		row.appendChild(emailCell);
		row.appendChild(typeCell);
		row.appendChild(latCell);
		row.appendChild(logCell);
		row.appendChild(temperatureCell);
		row.appendChild(precipitationCell);
		row.appendChild(concentrationCell);
		row.appendChild(commentCell);

		document.getElementById('table-body').appendChild(row);
		if (temp)
		{
			scrollHere = row.offsetTop; //Getting Y of target element
		}

  });
});

	
}
function createQuery()
{
	
  //Take info from the configuration inputs in the webpage 
  var pendingSamples = document.getElementById('pendingSamples').checked;
  var precipitationLevel = document.getElementById('precipitationLevel').value;
  var phosphateConcentrationLevel = document.getElementById('phosphateConcentrationLevel').value;
  var nitrateConcentrationLevel = document.getElementById('nitrateConcentrationLevel').value;
  var dateType = document.getElementById("dateButton").textContent;
  
  //set up variables to be used to create the query(some of them will remain empty if no data is inserted)
  var precipitation = '';
  var phosphateConcentration = '';
  var nitrateConcentration = '';
  var verified = " and verified = 1 ";
  var id = '';
  if(pendingSamples)
    verified = " and verified > -1 ";

  //data validation for precipitation
  if(isNaN(precipitationLevel) || precipitationLevel < 0 ) {
    precipitationLevel = '';
    document.getElementById('precipitationLevel').value = '';
  }
  //if precipitation level is not empty filter the query using the input 
  if(precipitationLevel!='') {
    if(document.getElementById('precipitationInequalitySign').textContent == '<')
      precipitation = ' and precipitation < ' +   document.getElementById('precipitationLevel').value;
    else
      precipitation = ' and precipitation > ' +   document.getElementById('precipitationLevel').value;
  }
  
  //data validation for Phosphate concentration
  if(isNaN(phosphateConcentrationLevel) || phosphateConcentrationLevel < 0 ) {
    phosphateConcentration = '';
    document.getElementById('phosphateConcentrationLevel').value = '';
	phosphateConcentrationLevel = '';
  }
  //if Phosphate concentration level is not empty filter the query using the input 
  if(phosphateConcentrationLevel!='') {
    if(document.getElementById('phosphateInequalitySign').textContent == '<')
      phosphateConcentration = ' and concentration < ' +  phosphateConcentrationLevel;
    else
      phosphateConcentration = ' and concentration > ' +   phosphateConcentrationLevel;
  }
  
  //data validation for Nitrate concentration
  if(isNaN(nitrateConcentrationLevel) || nitrateConcentrationLevel < 0 ) {
    nitrateConcentration = '';
    document.getElementById('nitrateConcentrationLevel').value = '';
	nitrateConcentrationLevel ='';
  }
  //if Nitrate concentration level is not empty filter the query using the input 
  if(nitrateConcentrationLevel!='') {
    if(document.getElementById('nitrateInequalitySign').textContent == '<')
      nitrateConcentration = ' and concentration < ' +  nitrateConcentrationLevel;
    else
      nitrateConcentration = ' and concentration > ' +   nitrateConcentrationLevel;
  }
  
  var date = "";
  if (document.getElementById("testDate1").value != "")
  {
	  switch (dateType){
	  case "After":
	  date = " and  date > "+" '"+document.getElementById("testDate1").value+"' ";
	  break;
	  
	  case "Before":
	  date = " and  date < "+" '"+document.getElementById("testDate1").value+"' ";
	  break;
	  
	  case "Between":
	  if (document.getElementById("testDate2").value)
	  {
		  date = " and  date > "+" '"+document.getElementById("testDate1").value+"' "+
			 " and  date < "+" '"+document.getElementById("testDate2").value+"' ";
	  }
	  break;
	}
  }
  //this part will be shared by any query
  var querySetUp = "SELECT " +
		" m.id 'id', m.user_id 'userid', DATE_FORMAT(m.date, '%m-%d-%Y') 'date', m.latitude 'latitude' , m.longitude 'longitude'," +
		" m.city 'city', m.state 'state', m.temperature 'temperature', m.precipitation 'precipitation', m.concentration 'concentration', " +
		" m.comment 'comment', m.verified 'verified', u.first 'first', u.last 'last', u.organization 'organization'," +
		" u.email 'email', u.active 'activeUser', u.admin 'admin', t.type 'type'" +

		" FROM markers m "+
		  " JOIN users u ON m.user_id = u.id"+
		  " JOIN tests t ON m.test_id = t.id ";
		  
  var temp = "all";
  var radios  =  document.getElementsByName("shownTest");
  for (var i = 0, length = radios.length; i < length; i++) {
    if (radios[i].checked) {
        temp = radios[i].value;
        // only one radio can be logically checked, don't check the rest
        break;
	}
  }
  
  var ord;
	switch(orderBy)
	{
		case 1:
		ord = "DATE";
		break;
		
		case 2:
		ord = "first";
		break;
		
		case 3:
		ord = "organization"
		break;
		
		case 4:
		ord = "email";
		break;
		
		case 5:
		ord = "type";
		break;
		
		case 6:
		ord = "latitude";
		break;
		
		case 7:
		ord = "longitude";
		break;
		
		case 8:
		ord = "temperature";
		break;
		
		case 9:
		ord = "precipitation";
		break;
		
		case 10:
		ord = "concentration";
		break;
		
		case 11:
		ord = "comment"
		break;
	}
	
    var query;
	switch (temp)
	{
		case 'all':
		query = "select * from ( "+querySetUp + 
		" WHERE true " +"and type = 'phosphate' "+ precipitation + phosphateConcentration+date+verified
		+ " union " +
		querySetUp + 
		" WHERE true " +"and type = 'nitrate' "+ precipitation + nitrateConcentration+date+ verified+
		" ) m "+" ORDER BY "+ord+" "+orderType;
		break;
		case 'phosphate':
		query = querySetUp +
		" WHERE true " +"and type = 'phosphate' "+ precipitation + phosphateConcentration+date+ verified+
		" ORDER BY "+ord+" "+orderType;
		break;
		case 'nitrate':
		query = querySetUp +
		" WHERE true " +"and type = 'nitrate' "+ precipitation + nitrateConcentration+date+ verified +
		" ORDER BY "+ord+" "+orderType;
		break;
	}
	console.log(query);
  return query;
  
  
  
	/*
	console.log("version 1");
	var query = "SELECT " +
    " m.id 'id', m.user_id 'userid', DATE_FORMAT(m.date, '%m-%d-%Y') 'date', m.latitude 'latitude' , m.longitude 'longitude'," +
    " m.city 'city', m.state 'state', m.temperature 'temperature', m.precipitation 'precipitation', m.concentration 'concentration', " +
    " m.comment 'comment', m.verified 'verified', u.first 'first', u.last 'last', u.organization 'organization'," +
    " u.email 'email', u.active 'activeUser', u.admin 'admin', t.type 'type'" +
	" FROM markers m "+
	    
	" JOIN users u ON m.user_id = u.id" +
	" JOIN tests t ON m.test_id = t.id " +
	
	" WHERE true " + precipitation + concentration+ verified +
    " ORDER BY "+ord+" "+orderType;
	return query
	*/
}
createTable();

//delay few seconds before jumping to the highlighted entry
//note: it might need to be longer dpending on how big the database is
if (markerid != null) 
	setTimeout(myScroll, 500)
function myScroll()
{
	window.scrollTo(0, scrollHere);
}

function downloadUrl(url, callback) {
  var request = window.ActiveXObject ?
    new ActiveXObject('Microsoft.XMLHTTP') :
    new XMLHttpRequest;

  request.onreadystatechange = function() {
    if (request.readyState == 4) {
      request.onreadystatechange = doNothing;
      callback(request, request.status);
    }
  };
	var query =  createQuery();
  request.open('GET', url, true);
	request.open("GET", url+"?q=" + query, true);
  request.send(null);
}

function changeDegree(d)
{
	degree = d;
	var txt = document.getElementById("temperature_text");
	
	if (degree == "f")
	{
		txt.innerHTML = "Temperature °F";
	}
	
	else
	{
		txt.innerHTML = "Temperature °C";
	}
	
	createTable();
}

function highlightTest(test)
{
	switch (test)
	{
		case 'all':
		if (!document.getElementById("useRecommendedConcentrationPhosphate").checked)
			document.getElementById("phosphateConcentrationLevel").disabled = false;
		if (!document.getElementById("useRecommendedConcentrationNitrate").checked)
			document.getElementById("nitrateConcentrationLevel").disabled = false;
		break;
		case 'phosphate':
		if (!document.getElementById("useRecommendedConcentrationPhosphate").checked)
			document.getElementById("phosphateConcentrationLevel").disabled = false;
		document.getElementById("nitrateConcentrationLevel").disabled = true;
		break;
		case 'nitrate':
		document.getElementById("phosphateConcentrationLevel").disabled = true;
		if (!document.getElementById("useRecommendedConcentrationNitrate").checked)
			document.getElementById("nitrateConcentrationLevel").disabled = false;
		break;
	}
}

//when a recommended concentration checkbox is highlighted set numbers and lock control 
function useRecommendedConcentration(test)
{
	switch (test)
	{
		case 'phosphate':
		if (!document.getElementById("useRecommendedConcentrationPhosphate").checked && 
			(document.getElementById("showPhosphateTests").checked||document.getElementById("showAllTests").checked))
		{
			document.getElementById("phosphateConcentrationLevel").disabled = false;
		}
		else
		{
			document.getElementById("phosphateConcentrationLevel").value = 50;
			document.getElementById("phosphateConcentrationLevel").disabled = true;
			document.getElementById('phosphateInequalitySign').textContent = ">";
			
		}
		
		break;
		
		case 'nitrate':
		if (!document.getElementById("useRecommendedConcentrationNitrate").checked&& 
			(document.getElementById("showNitrateTests").checked||document.getElementById("showAllTests").checked))
		{
			document.getElementById("nitrateConcentrationLevel").disabled = false;
		}
		
		else
		{
			document.getElementById("nitrateConcentrationLevel").value = 85;
			document.getElementById("nitrateConcentrationLevel").disabled = true;
			document.getElementById('nitrateInequalitySign').textContent = ">";
		}
		
		break;
	}
}
function changeOrder(ord)
{
	console.log(document.getElementById("columns_headers").childNodes)
	if (isNaN(ord))
	{
		
	}
	
	else
	{
		
		if (orderBy == ord)
		{
			ordElm = document.getElementById("order_arrow");
			if (orderType == "desc")
			{
				ordElm.setAttribute("class","glyphicon glyphicon-triangle-top")
				orderType = "asc";
			}
				
			else
			{
				ordElm.setAttribute("class","glyphicon glyphicon-triangle-bottom")
				orderType = "desc";
			}
				
		}
		else
		{
			orderBy = ord;
			ordElm = document.getElementById("order_arrow");
			ordElm.parentElement.removeChild(ordElm);
			ordElm = document.createElement("span");
			ordElm.setAttribute("id","order_arrow");
			
			switch(orderBy)
			{
				case 1:
				document.getElementById("date").appendChild(ordElm);
				break;
				
				case 2:
				document.getElementById("name").appendChild(ordElm);
				break;
				
				case 3:
				document.getElementById("orgnization").appendChild(ordElm);
				break;
				
				case 4:
				document.getElementById("email").appendChild(ordElm);
				break;
				
				case 5:
				document.getElementById("type").appendChild(ordElm);
				break;
				
				case 6:
				document.getElementById("latitude").appendChild(ordElm);
				break;
				
				case 7:
				document.getElementById("longitude").appendChild(ordElm);
				break;
				
				case 8:
				document.getElementById("temperature").appendChild(ordElm);
				break;
				
				case 9:
				document.getElementById("precipitation").appendChild(ordElm);
				break;
				
				case 10:
				document.getElementById("concentration").appendChild(ordElm);
				break;
				
				case 11:
				document.getElementById("comment").appendChild(ordElm);
				break;
			}
			
			{
				ordElm.setAttribute("class","glyphicon glyphicon-triangle-top")
				orderType = "asc";
			}
			
		}
	  
	}
	createTable();
}


function changeDate(){
	if (document.getElementById("dateButton").textContent == "After")
	{
		document.getElementById("dateButton").textContent = "Before";
	}
	else if (document.getElementById("dateButton").textContent == "Before")
	{
		document.getElementById("dateButton").textContent = "Between";
		document.getElementById("testDate2").style.visibility = 'visible';
		document.getElementById("testDate2Label").style.visibility = 'visible';
	}
	
	else 
	{
		document.getElementById("dateButton").textContent = "After";
		document.getElementById("testDate2").style.visibility = 'hidden';
		document.getElementById("testDate2Label").style.visibility = 'hidden';
	}
}

function changeSign(inequalitySign) {
  if(document.getElementById(inequalitySign).textContent == '<')
    document.getElementById(inequalitySign).textContent = '>';
  else
    document.getElementById(inequalitySign).textContent = '<';
}

function clearBox(numberBox) {
  if (!document.getElementById(numberBox).disabled)
	document.getElementById(numberBox).value = '';
}

function doNothing() {}
//setInterval(createTable, 1000/10);