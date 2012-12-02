"use strict";

/* To be defined prior to referencing this file:
 * - channel = new goog.appengine.Channel('token'); with custom token
 * - var slugs as array of slugs in use;
 *
 * Open a channel with the token to receive updates on the slugs in use.
 *
 * Compare current slug field value to enable/disable the 'Move' button.
 * Also change slug field background color accordingly.
 *
 * Actions on key input: Reject invalid chars.
*/

function setSlugs(message){
    slugs = message.split(" ");
}

var titleInput = $('[name="title"]');
var slugInput = $('[name="slug"]');
var textArea = $('[name="body"]');
var submitButton = $('[type="submit"]');

var currentSlug = slugInput.val();
var SubmitDefaultValue = submitButton.val();

function convertToSlug(text){
    return text
        .toLowerCase()
        .replace(/ /g,'_')
        .replace(/_+/g,'_')
        .replace(/[^\w-]+/g,'');
}

function updateMoveButton(slug){
    if (slugInput.val() != ''){
	// .disabled only works with [0], whereas .val only works without!?
	submitButton[0].disabled = false;
    }
    else {
	submitButton[0].disabled = true;
    }

    if (jQuery.inArray(slug, slugs) > -1) {
        // slug in use, warn
        slugInput.addClass('warning');
        submitButton.val("Can't overwrite existing Article");
	submitButton[0].disabled = true;
    }
    else {
        // slug not in use
        slugInput.removeClass('warning');
        submitButton.val(SubmitDefaultValue);
    }
}

function rejectInvalidChars(o, regexp){
    o.value = o.value.replace(regexp, '');
}

slugInput.keyup(function(){
		    updateMoveButton($(this).val());
		    return rejectInvalidChars(this, /[^a-zäöüß0-9_\-]/);
		});


// Submit button, trigger changing the slug
function submit() {
    $.post("/admin/move-article", {from: currentSlug,
				   to: slugInput.val()},
	   function(data){location.href = '/' + slugInput.val();});
}

submitButton[0].onclick = function() { submit(); };


var socket = channel.open();
// socket.onopen = function(){};
socket.onmessage = function(m){setSlugs(m.data);};
socket.onerror = function(){$('body').prepend('<p>Channel error, please reload.</p>');};
socket.onclose = function(){$('body').prepend('<p>Channel closed, please reload.</p>');};