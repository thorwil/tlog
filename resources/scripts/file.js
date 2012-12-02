// On change of the file input, post chosen files via XMLHttpRequest, one by one

input = $('[name=\"files\"]')[0];

input.addEventListener('change', function () {
			   traverseFiles(this.files);
		       }, false);

function traverseFiles(files) {
    for (var i=0, l=files.length; i<l; i++){
	caseNameUsed(files[i]);
    }
}

var shelvedFiles = [];

function caseNameUsed(file) {
    $.ajax({type: 'GET',
	    url: "/" + file.name,
	    success: function() {
		// There's already a file with that name, ask about replacing it:
		shelvedFiles.push(file);
		addQuestion(file.name, (shelvedFiles.length -1));
	    },
	    error: function() {
		// No file with that name yet, so continue:
		prepareUpload(file);
	    }
	   });
}

function addQuestion(fileName, i) {
    $('<p id="' + fileName + '">There\'s already a '
      + '<a href="/' + fileName + '">' + fileName + '</a> '
      + '<button onclick="replace(shelvedFiles[' + i + ']);">Replace</button></p>').insertAfter('input')[0];
}

function replace(file) {
    // Delete old blob, initiate upload, remove the question HTML:
    $.post("/admin/delete/blob", {slug: file.name});
    prepareUpload(file);
    $('p#' + escapeDot(file.name)).remove();
}

function prepareUpload(file) {
    $.get('/admin/generate_upload_url', function(url){
	      uploadFile(file, url);
	  });
}

function uploadFile(file, url) {
    var fd = new FormData();
    fd.append('fileField', file);

    $('<p id="' + file.name + '">Uploading ' + file.name +
      ': <span id="' + file.name + '"></span>' ).insertAfter('input')[0];
    var escapedFileName = escapeDot(file.name);
    
    var xhr = new XMLHttpRequest();
    xhr.upload.addEventListener('progress', updateProgress(escapedFileName), false);
    xhr.upload.addEventListener('load', transferComplete(file, escapedFileName), false);
    xhr.upload.addEventListener('error', transferFailed, false);
    xhr.upload.addEventListener('abort', transferCanceled, false);
    xhr.open('POST', url);
    xhr.send(fd);
}

function escapeDot(s){
    return s.replace('.', '\\.');
}

function updateProgress(escapedFileName) {
    return function (e) {
	var percentComplete = (e.loaded / e.total) * 100;
	$('span#' + escapedFileName).html(percentComplete + ' %');
    };
}

function transferComplete(file, escapedFileName) {
    return function (e) {
	// Remove the progress <p>:
	$('p#' + escapedFileName).remove();

	// Remove from shelvedFiles, if present:
	if (! unshelve(file)) {
	    // Add item to the #stored-items list:
	    $('<tr>'
	      + '<td><a href="/admin/queue-delete/blob/' + file.name + '">Delete</a></td>'
	      + '<td><a class="view" href="/' + file.name + '">'
	      + file.name + '</a></td></tr>').prependTo('table#stored-items');
	};
    };
}

function unshelve(file) {
    var idx = shelvedFiles.indexOf(file);
    if (idx != -1) {
	shelvedFiles.splice(idx, 1);
	return true;
    } else {
	return false;
    };
}

function transferFailed(e) {
  alert('An error occurred while transferring the file.');
}

function transferCanceled(e) {
  alert('The transfer has been canceled by the user.');
}