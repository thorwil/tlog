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

// function setSlugs(message){
//     slugs = message.split(" ");
// }

// var socket = channel.open();
// // socket.onopen = function(){};
// socket.onmessage = function(m){setSlugs(m.data);};
// socket.onerror = function(){$('body').prepend('<p>Channel error, please reload.</p>');};
// socket.onclose = function(){$('body').prepend('<p>Channel closed, please reload.</p>');};

window.onload=function() {
    var titleInput = $('[name="title"]');
    var slugInput = $('[name="slug"]');
    var textArea = $('div#slug');
    var submitButton = $('[type="submit"]');
    var feedCheckboxes = $('input.feed, [type="checkbox"]');

    function convertToSlug(text){
	return text
            .toLowerCase()
            .replace(/ /g,'_')
            .replace(/_+/g,'_')
            .replace(/[^\w-]+/g,'');
    }

    function updateSubmitButton(slug){
	if (titleInput.val() != '' &&
	    slugInput.val() != '' &&
	    textArea.html() != ''){
	    // .disabled only works with [0], whereas .val only works without!?
	    submitButton[0].disabled = false;
	}
	else {
	    submitButton[0].disabled = true;
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

    var SubmitDefaultValue = submitButton.val();

    titleInput.keyup(function(){
	// Fill slug as title is being written
	var text = convertToSlug($(this).val());
	slugInput.val(text);
	updateSubmitButton(text);
    });

    slugInput.keyup(function(){
	updateSubmitButton($(this).val());
	return rejectInvalidChars(this, /[^a-zäöüß0-9_\-]/);
    });

    textArea.keyup(function(){
	updateSubmitButton(slugInput.val());
    });

    function getFeedCheckboxString() {
	var s = "";
	feedCheckboxes.each(function() {
	    s = s + this.name + " " + this.checked + " ";
	});
	return s.slice(0, -1); // Drop last character, as it's a " "
    }

    // Submit button, submit article
    function submit() {
	$.post('/admin/add-article',
    	       {slug: slugInput.val(),
    		title: titleInput.val(),
    		body: textArea.html(),
		feeds: getFeedCheckboxString(),
    		redir: slugInput.val()},
    	       function(data){location.href = '/' + slugInput.val();});
    }

    // submitButton[0].onclick = function() { submit(); };
    submitButton.onclick = function() { submit(); };
};