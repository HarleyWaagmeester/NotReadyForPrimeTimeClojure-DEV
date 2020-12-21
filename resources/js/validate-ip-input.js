// If the length of the element's contained string is 0, then set background color and return false 

function Emptyvalidation(hostForm)
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
	return true;
    }
}
