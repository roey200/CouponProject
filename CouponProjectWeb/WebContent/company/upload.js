//<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
function uploadFile(file,saveAs)
{
	console.log('uploadFile : ' + file.name + ' size = ' + file.size + ' saveas = ' + saveAs);
	//alert('uploadFile : ' + file.name + ' size = ' + file.size + ' saveas = ' + saveAs);
	
	// Create a formdata object and add the files
    var data = new FormData();
    data.append('file', file);
    data.append('saveas',saveAs)

    $.ajax({
        url: '/CouponProjectWeb/files/upload',
        type: 'POST',
        data: data,
        cache: false,
        dataType: 'html', // return type
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data, textStatus, jqXHR)
        {
        	console.log('uploadFile done: ' + data);
        	alert('uploadFile done : ' + data); 
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
        	//alert('uploadFile err : ' + jqXHR); 
        	//alert('uploadFile err : ' + textStatus); 
        	alert('uploadFile err : ' + errorThrown); 
            // Handle errors here
            console.log('uploadFile err : ' + errorThrown);
        }
    });
}

function isFileSelected() {
	var files = document.getElementById("theFile").files;
	var file = files[0];

	if(typeof file === 'undefined'){
	    //alert('NO FILE SELECTED');
	    return false;
	}
	return true;
	
}

function doUpload() {
	var files = document.getElementById("theFile").files;
	var file = files[0];

	if(typeof file === 'undefined'){
	    //alert('NO FILE SELECTED');
	    return;
	}
	
	var saveas = document.getElementById("saveAs").value;
	uploadFile(file,saveas);
	
}

function refreshImage(fileName) {
	alert("fileName = " + fileName)
	document.getElementById("theImage").src='/CouponProjectWeb/uploads/' + fileName;
}
