var dropbox = document.getElementById("dropbox")
 
// init event handlers
dropbox.addEventListener("dragenter", noopHandler, false);
dropbox.addEventListener("dragexit", noopHandler, false);
dropbox.addEventListener("dragover", noopHandler, false);
dropbox.addEventListener("drop", drop, false);



function noopHandler(evt) {
  evt.stopPropagation();
  evt.preventDefault();
}

function drop(evt) {
var files = evt.dataTransfer.files;
var count = files.length;
 
// Only call the handler if 1 or more files was dropped.
if (count &gt; 0)
	handleFiles(files);
}


function handleFiles(files) {
  //var file = files[0];
 
  //document.getElementById("droplabel").innerHTML = "Processing " +       file.name;
 
  //var reader = new FileReader();
 
  // init the reader event handlers
  reader.onload = handleReaderLoad;
 
  // begin the read operation
  //reader.readAsDataURL(file);
}

function handleReaderLoad(evt) {
  //var img = document.getElementById("preview");
  //img.src = evt.target.result;
}