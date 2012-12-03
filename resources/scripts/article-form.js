"use strict";

/* Functions for a form for adding articles.
 *
 * On GAE, this worked as follows:
 *
 * To be defined prior to referencing this file:
 * - channel = new goog.appengine.Channel('token'); with custom token
 * - var slugs as array of slugs in use;
 *
 * Open a channel with the token to receive updates on the slugs in use.
 *
 * Compare current slug field value to switch between "Move" and "Overwrite"
 * text on the submit button. Also change slug field background color.
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
    var feedCheckboxes = document.getElementById("feed-selectors").getElementsByTagName("input");//$('input.feed, [type="checkbox"]');

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

	// if (jQuery.inArray(slug, slugs) > -1) {
	//     // slug in use, warn
	//     slugInput.addClass('warning');
	//     submitButton.val("Can't overwrite existing Article");
	// 	submitButton[0].disabled = true;
	// }
	// else {
	//     // slug not in use
	//     slugInput.removeClass('warning');
	//     submitButton.val(SubmitDefaultValue);
	// }
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

    // Submit button, submit article
    function submit() {
	$.post('/admin/add-article',
    	       {slug: slugInput.value,
    		title: titleInput.value,
    		body: textArea.innerHTML,
		feeds: getCheckedFeedCheckboxesString(feedCheckboxes),
    		redir: slugInput.value},
    	       function(data){location.href = '/' + slugInput.value;});
    }

    submitButton.onclick = function() { submit(); };
};