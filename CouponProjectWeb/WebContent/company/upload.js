//<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
function uploadFile(file,saveAs)
{

	alert('uploadFile : ' + file.name + ' size = ' + file.size + ' saveas = ' + saveAs); 
	// Create a formdata object and add the files
    var data = new FormData();
    data.append('file', file);
    data.append('saveas',saveAs)

    $.ajax({
        url: 'files/upload',
        type: 'POST',
        data: data,
        cache: false,
        dataType: 'html', // return type
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data, textStatus, jqXHR)
        {
        	alert('uploadFile done : ' + data); 
            if(typeof data.error === 'undefined')
            {
                console.log('undefined: ' + data);
            }
            else
            {
                // Handle errors here
                console.log('ERRORS: ' + data.error);
            }
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
        	alert('uploadFile err : ' + jqXHR); 
        	alert('uploadFile err : ' + textStatus); 
        	alert('uploadFile err : ' + errorThrown); 
            // Handle errors here
            console.log('ERRORS: ' + textStatus);
            // STOP LOADING SPINNER
        }
    });
}

function doUpload() {
	alert('doUpload zzzzzzzzzz');
	var saveas = document.getElementById("saveAs").value;
//	alert('doUpload saveas=',saveas);

	var elem = document.getElementById("theFile");
//	alert('doUpload elem=',elem);
	var files = document.getElementById("theFile").files;
//	alert('doUpload files=',files);
	var file = files[0];
	
	var saveas = document.getElementById("saveAs").value;
//	alert('doUpload saveas=',saveas);
	
	uploadFile(file,saveas);
	
}

function refreshImage(fileName) {
	alert("fileName = " + fileName)
	document.getElementById("theImage").src='uploads/' + fileName;
}
