// If the length of the element's contained string is 0, then set background color and return false 

function Emptyvalidation(hostForm)
{
    console.log("in Emptyvalidation");
    var hostForm = document.forms["host-input"];
    if (hostForm.ip.value.length == 0) 
    {
	hostForm.ip.style.background =   'Yellow'; 
	return false;  
    }
    else
    {
	hostForm.style.background = 'White';
	return true;
    }
}
