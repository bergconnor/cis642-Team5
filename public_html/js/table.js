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
var orderType = "desc";
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
		precipitationCell.innerHTML = precipitation;
		concentrationCell.innerHTML = concentration;
		commentCell.innerHTML       = comment;
		typeCell.innerHTML          = type;
		emailCell.innerHTML         = email;
		latCell.innerHTML           = lat;
		logCell.innerHTML           = log;
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
	var pendingSamples = document.getElementById('pendingSamples').checked;
	var precipitationLevel = document.getElementById('precipitationLevel').value;
	var concentrationLevel = document.getElementById('concentrationLevel').value;
	var precipitation = '';
	var concentration = '';
	var verified = " and verified = 1 ";
	var id = '';
  
	if(pendingSamples)
		verified = " and verified > -1 ";
	
	//data validation for precipitation
	if(isNaN(precipitationLevel) || precipitationLevel < 0 ) {
	precipitationLevel = '';
	document.getElementById('precipitationLevel').value = '';
	}

	if(precipitationLevel!='') {
		if(document.getElementById('inequalitySign1').textContent == '<')
			precipitation = ' and precipitation < ' +   document.getElementById('precipitationLevel').value;
		else
			precipitation = ' and precipitation > ' +   document.getElementById('precipitationLevel').value;
	}
	
	//data validation for concentration
	if(isNaN(concentrationLevel) || concentrationLevel < 0 ) {
		concentrationLevel = '';
		document.getElementById('concentrationLevel').value = '';
	}
	if(concentrationLevel!='') {
		if(document.getElementById('inequalitySign2').textContent == '<')
			concentration = ' and concentration < ' +   document.getElementById('concentrationLevel').value;
		else
			concentration = ' and concentration > ' +   document.getElementById('concentrationLevel').value;
	}
  
	var ord;
	switch(orderBy)
	{
		case 1:
		ord = "DATE(date)";
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
			if (orderBy == 1)
			{
				ordElm.setAttribute("class","glyphicon glyphicon-triangle-bottom")
				orderType = "desc";
			}
				
			else
			{
				ordElm.setAttribute("class","glyphicon glyphicon-triangle-top")
				orderType = "asc";
			}
			
		}
	  
	}
	createTable();
}

function changeSign(inequalitySign) {
  if(document.getElementById(inequalitySign).textContent == '<')
    document.getElementById(inequalitySign).textContent = '>';
  else
    document.getElementById(inequalitySign).textContent = '<';
}

function clearBox(numberBox) {
  document.getElementById(numberBox).value = '';
}

function doNothing() {}
