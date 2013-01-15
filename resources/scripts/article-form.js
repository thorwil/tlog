"use strict";

/* Functions for a form for adding articles.
 *
 * To be defined prior to referencing this file:
 * - var slugsInUse as array of slugs in use;
 *
 * Compare current slug field value with slugs array. Disable the submit button, if there's a match.
 *
 * Actions on key input:
 * - Reject invalid chars.
 * - Enable submit button if all 3 fields are filled, else make sure it's disabled.
 */

window.onload=function() {
    var titleInput = document.getElementById("article_title_input");
    var slugInput = document.getElementById("article_slug_input");
    var textArea = document.getElementById("article_text_area");
    var submitButton = document.getElementById("article_submit");
    var feedCheckboxes = document.getElementById("feed-selectors").getElementsByTagName("input");

    function convertToSlug(text){
	return text
            .toLowerCase()
            .replace(/ /g,'_')
            .replace(/_+/g,'_')
            .replace(/[^\w-]+/g,'');
    }

    function updateSubmitButton(slug){
	if (titleInput.value != '' &&
	    slugInput.value != '' &&
	    textArea.innerHTML != ''){
	    submitButton.disabled = false;
	}
	else {
	    submitButton.disabled = true;
	}

	if (jQuery.inArray(slug, slugsInUse) > -1) {
	    // slug in use, warn
	    slugInput.className += ('warning');
	    submitButton.value = "There's already an Article with this slug.";
	    submitButton.disabled = true;
	}
	else {
	    // slug not in use
	    slugInput.className = slugInput.className.replace( /(?:^|\s)warning(?!\S)/g , '' );
	    submitButton.value = SubmitDefaultValue;
	}
    }

    function rejectInvalidChars(o, regexp){
	o.value = o.value.replace(regexp, '');
    }

    var SubmitDefaultValue = submitButton.value;

    titleInput.onkeyup = function(){
	// Fill slug as title is being written
	var text = convertToSlug(this.value);
	slugInput.value = text;
	updateSubmitButton(text);
    };

    slugInput.onkeyup = function(){
	updateSubmitButton(this.value);
	return rejectInvalidChars(this, /[^a-zäöüß0-9_\-]/);
    };

    textArea.onkeyup = function(){
	updateSubmitButton(slugInput.value);
    };

    function getCheckedFeedCheckboxesString(checkboxes) {
	var s = "";
	for (var e = 0; e < checkboxes.length; e++)
	{
            if (checkboxes[e].checked == true) {
		s = s + checkboxes[e].name + " ";
	    }
	}
	return s.slice(0, -1); // Drop last character, as it's a space
    };

    function fixedEncodeURIComponent (str) {
	return encodeURIComponent(str).replace(/[!'()]/g, escape).replace(/\*/g, "%2A");
    }

    function httpRequestPut(url, body, success, failure) {
	var r = new XMLHttpRequest();
	r.open('PUT', url, true);
	r.send(body);
	r.onreadystatechange = function() {
	    if (r.readyState == 4) {
		if (r.status == 201) // Status 201: Created
		    // Navigate to the new article:
		    window.location.href = '/' + slugInput.value;
		else
		    // Bring up error message:
		    alert(r.status + ' ' + r.statusText + ': ' + r.responseText);
	    }
	};
    }

    function submit() {
	var slug = '/' + slugInput.value;
	httpRequestPut(slug,
		    JSON.stringify({title: titleInput.value,
				    content: textArea.innerHTML,
				    feeds: getCheckedFeedCheckboxesString(feedCheckboxes)}));
    }

    submitButton.onclick = function() { submit(); };
};