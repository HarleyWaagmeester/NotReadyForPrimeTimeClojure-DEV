// This is not a robust implementation of validation.
// If the length of the element's contained string is 0, then set background color and return false 

function Hostvalidation(id)
{
    var hostForm = document.forms["host-input"];
    if (hostForm.ip.value.length == 0) 
    {
	hostForm.ip.style.background =   'Yellow'; 
	return false;  
    }
    else
    {
	hostForm.ip.style.background = 'White';
	hostForm.nameserver.style.background = 'White';
	putMessageInDiv(id);
	return true;
    }
}


// put a message in a host-child.html hidden div

function putMessageInDiv(id)
{
    console.log(id);
    document.getElementById(id).innerHTML = "retrieving data from nameservers";
}
