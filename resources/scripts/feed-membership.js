"use strict";

/* Send updates on clicking feed selection checkboxes */

function postFeedSelectionChange(slug, feed, checked, checkbox) {
    // Disable checkbox, POST to server, enable checkbox on response.

    checkbox.disabled = true;
    var body = JSON.stringify({feed: feed,
			       checked: checked});
    var r = new XMLHttpRequest();
    r.open('POST', '/' + slug, true);
    r.send(body);
    r.onreadystatechange = function() {
    	if (r.readyState == 4) {
    	    if (r.status == 409) { // 409: Conflict
    		alert("Checkbox and stored membership were out of sync. The checkbox will be set to the right state now.");
		if (r.responseText == "true") {
		    checkbox.checked = true;
		} else {
		    checkbox.checked = false;
		}
    	    }
    	}
	checkbox.disabled = false;
    };
}

function armCheckboxes() {
    var checkboxes = document.getElementsByClassName("feed-checkbox");

    for (var i = 0; i < checkboxes.length; i ++) {
	checkboxes[i].disabled = false;
	checkboxes[i].onclick = function(){
	    postFeedSelectionChange(
		// Input in fieldset in header in article with data-slug attribute:
	    	this.parentNode.parentNode.parentNode.getAttribute('data-slug'),
	    	this.name,
	    	this.checked, // the state after the click.
		this);
	};
    }
}

window.addEventListener('load', function(){armCheckboxes();}, false);