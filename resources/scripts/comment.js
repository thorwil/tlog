"use strict";

/* Each comment form starts as a single Aloha-editable <div>, Prefilled with a "Reply" hint.
 *
 * configureField is called on mouseover. It causes highlighting of the article or comment that will be
 * replied to by adding a CSS class and (re)binds event handlers for the field.
 *
 * prepareCommentForm (then bound to onclick) stores a clone of the field, clears the original and binds
 * expandCommentForm to onkeypress.
 *
 * expandCommentForm adds Author and Link fields and a submit button.
 *
 * The submit POST is answered with a HTML string rendition of the comment and its reply field,
 * which replaces the comment form. The clone is used to restore the comment field for the parent. */

function htmlStringToNodes(string){
    var div = document.createElement('div');
    div.innerHTML = string;
    return div.firstChild;
}

function afterAddComment(commentDiv, commentDivParent, commentRendition, bodyFieldClone){
    commentDivParent.removeChild(commentDiv);

    // Add new comment followed by its reply field to the page:
    var newComment = htmlStringToNodes(commentRendition);
    newComment.style.display = 'none';
    commentDivParent.appendChild(newComment);
    $(newComment).fadeIn('slow');

    // Add back original reply field for the parent, update Aloha:
    commentDivParent.appendChild(bodyFieldClone);
    $(function() {$('.editable').aloha();});
    if ($(newComment).find('.admin-editable').length) { // zero is false
	$(function() {$('.admin-editable').aloha();});
    }

    // If there are no comments initially, the div wrapping all comments has a CSS class 'empty'.
    // Remove it now:
    $('div.empty').removeClass('empty');
}

function addComment(subjectId, bodyField, bodyFieldClone, authorId, linkId) {
    function httpRequestPut(url, body) {
	var r = new XMLHttpRequest();
	r.open('PUT', url, true);
	r.send(body);
	r.onreadystatechange = function() {
	    if (r.readyState == 4) {
		if (r.status == 201) { // Status 201: Created
		    // On success, handler answers with HTML for the new comment.
    		    // Remove comment form:
    		    var commentDiv = bodyField.parentNode;
		    var commentDivParent = commentDiv.parentNode;
		    $(commentDiv).fadeOut('slow',
					  function() {afterAddComment(commentDiv, commentDivParent,
								      r.responseText, bodyFieldClone);});
		} else {
		    // Bring up error message:
		    alert(r.status + ' ' + r.statusText + ': ' + r.responseText);
		}
	    }
	};
    }

    var slug = '/' + document.getElementsByTagName("article")[0].getAttribute("data-id");
    var editable = Aloha.getEditableById(bodyField.id);
    var content = editable.getContents();

    httpRequestPut(slug + '/comment',
		   JSON.stringify({parent: subjectId,
				   author: document.getElementById(authorId).value,
				   email: 'dummy@example.com',
				   link: document.getElementById(linkId).value,
    				   content: content.replace(/<br>$/, '')})); // Drop trailing <br>
}

function editableNotBlank(id){
    /* Return true, iff the editable contains visible content.
       Sadly, it may contain just a <br> or <br style=""> instead of an empty string. */
    var content = Aloha.getEditableById(id).getContents();
    var i = $.inArray(content, ['', '<br>', '<br style="">']); // returns -1, if not in array.

    if (i < 0) {
	return true;
    } else {
	return false;
    }
}

function updateSubmitButton(bodyId, authorId, button){
    // TODO: handle <br> or <br style=""> as sole content

    if (document.getElementById(authorId).value != '' && editableNotBlank(bodyId)) {
	button.disabled = false;
    } else {
	button.disabled = true;
    }
}

function expandCommentForm (subjectId, bodyField, bodyFieldClone) {
    // Keep further keypresses from triggering this function:
    bodyField.onkeypress = '';

    // Construct a table for the labels and inputs:

    var authorId = 'author_' + subjectId;
    var linkId = 'link_' + subjectId;
    var table = document.createElement('table');

    function tr(id, label, tooltip) {
	return "<tr class=\"has-tooltip\" data-tooltip=\"" + tooltip + "\">"
	    + "<td>"
	    + "<label for=\"" + id + "\">" + label + "</label>"
	    + "</td>"
	    + "<td>"
	    + "<input type=\"text\" id=\"" + id + "\">"
	    + "</td>"
	    + "</tr>";
    }

    table.innerHTML = tr(authorId, "Name", "Required: Your real name or nickname")
	+ tr(linkId, "Website", "Optional: Link to a website of your choice");

    // Submit button:
    var submitButton = document.createElement('input');
    submitButton.type = 'submit';
    submitButton.value = 'Publish';
    submitButton.disabled = 'true';
    submitButton.onclick = function(){addComment(subjectId, bodyField, bodyFieldClone, authorId,
						 linkId);};

    /* The submit button shall have a tooltip, but the data-tooltip and content ::after approach
     * does not work with a disabled input. Rather than switching back and forth between a <div>
     * styled as disabled button and an actual button, a <div> around the button is used just for
     * the tooltip: */
    var submitButtonWrapper = document.createElement('div');
    submitButtonWrapper.setAttribute('data-tooltip', 'You have to enter text and a name, before you can publish the comment.');
    submitButtonWrapper.className = 'button-wrapper has-tooltip';
    submitButtonWrapper.appendChild(submitButton);

    // Initially hide the table and button, prepare for slideDown:
    table.style.display = 'none';
    submitButton.style.display = 'none';
    table.className = 'slide';
    submitButton.className = 'slide';

    // Add the table and button to the document:
    bodyField.parentNode.appendChild(table);
    bodyField.parentNode.appendChild(submitButtonWrapper);

    // Slide them in, avoid tyring to do it again:
    $('.slide').slideDown('fast');
    table.className = '';
    submitButton.className = '';

    // Mark the field as now being part of an expanded form:
    $(bodyField.parentNode).addClass('expanded');

    // enable or disable button on changes in the editable and author field:
    var u = function(){updateSubmitButton(bodyField.id, authorId, submitButton);};
    bodyField.onkeyup = u;
    document.getElementById(authorId).onkeyup = u;
}

function prepareCommentForm(subjectId, bodyField) {
    // Clone the whole comment-form before it gets expanded, so it can be restored later on, easily.
    var bodyFieldClone = bodyField.parentNode.cloneNode(true);

    /* Make sure the comment-form clone will not appear as being active once restored, by removing its
     aloha-editable-active CSS class. */
    var c = bodyFieldClone.childNodes[0];
    c.className = c.className.replace( /(?:^|\s)aloha-editable-active(?!\S)/g , '' );

    bodyField.innerHTML = '';
    bodyField.onclick = '';

    bodyField.onkeypress = function (){
    	expandCommentForm(subjectId, bodyField, bodyFieldClone);
    };
}

// Initially bound to each reply field's onmouseover:
function configureField(subjectId, field){
    /* Cause comment-highlighting on hovering the reply field once, then rebind onmouseover for
     * future hovering: */
    var cssClass = 'to-be-replied-to';
    function addCssClass() {$('[data-id=' + subjectId + ']').addClass(cssClass);};
    addCssClass();

    field.onmouseover = function (){addCssClass();};
    field.onmouseout = function (){$('[data-id=' + subjectId + ']').removeClass(cssClass);};
    field.onclick = function (){prepareCommentForm(subjectId, field);};
}

function initializeCommenting() {
    // Highlight comment if its index is given as hash in the URL:
    var hash = window.location.hash; // includes leading # or is an empty string

    if (hash) { // empty string is false
	var anchored = $('.number-' + hash.substring(1));
	anchored.addClass('anchored');
    }

    // Highlight comment after an anchor link on the page is clicked:
    window.onhashchange = function (e) {
	$('#comment-anchor-for_' + e.oldURL.split('#')[1]).removeClass('anchored');
	$('#comment-anchor-for_' + e.newURL.split('#')[1]).addClass('anchored');
    };


    // Bind hover effect to 'Delete' links:
    $('.do-delete').each(function () {
	var branch = this.parentNode.parentNode;
	this.onmouseover = function () {$(branch).addClass('to-be-deleted');};
	this.onmouseout = function () {$(branch).removeClass('to-be-deleted');};
    });

    // Bind hover effect to 'Cancel Delete' links:
    $('.cancel-delete').each(function () {
	var branch = this.parentNode.parentNode;
	this.onmouseover = function () {$(branch).removeClass('to-be-deleted');};
	this.onmouseout = function () {$(branch).addClass('to-be-deleted');};
    });
}

window.addEventListener('load', function(){initializeCommenting();}, false);