"use strict";

/* Each comment form starts as a single Aloha-editable <div>, Prefilled with a "Reply" hint.
 *
 * configureField is called onmouseover, causes highlighting of the comment that will be replied to
 * by adding a CSS class and (re)binds event handlers for the field.
 *
 * prepareCommentForm (then bound to onclick) stores a clone of the field, clears the original and binds
 * expandCommentForm to onkeypress.
 *
 * expandCommentForm adds Author and Link fields and a submit button.
 *
 * The submit POST is answered with a HTML string rendition of the comment and its reply field,
 * which replaces the comment form. The clone is used to restore the comment field for the parent. */


// Highlight comment if its index is given as hash in the URL:

var hash = window.location.hash; // includes leading # or is an empty string

// ... on load:
if (hash) { // empty string is false
    var anchored = $('.index-' + hash.substring(1));
    anchored.addClass('anchored');
}

// ... on clicking an anchor link on the page:
window.onhashchange = function (e) {
    $('.index-' + e.oldURL.split('#')[1]).removeClass('anchored');
    $('.index-' + e.newURL.split('#')[1]).addClass('anchored');
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

// Initially bound to each reply field's onmouseover:
function configureField(parentId, field){
    /* Cause comment-highlighting on hovering the reply field once, then rebind onmouseover for
     * future hovering: */
    var cssClass = 'to-be-replied-to';
    function addCssClass() {$("#" + parentId).addClass(cssClass);};
    addCssClass();
    field.onmouseover = function (){$("#" + parentId).addClass(cssClass);};

    field.onmouseout = function (){$("#" + parentId).removeClass(cssClass);};
    field.onclick = function (){prepareCommentForm(parentId, field);};
}

function prepareCommentForm(parentId, bodyField) {
    var bodyFieldClone = bodyField.parentNode.cloneNode(true);

    bodyField.innerHTML = '';
    bodyField.onclick = '';

    bodyField.onkeypress = function (){
    	expandCommentForm(parentId, bodyField, bodyFieldClone);
    };
}

function updateSubmitButton(bodyId, authorId, button){
    if (document.getElementById(authorId).value != '' &&
	getBody(bodyId) != ''){
	button.disabled = false;
    } else {
	button.disabled = true;
    }
}

function expandCommentForm (parentId, bodyField, bodyFieldClone) {
    // Keep further keypresses from triggering this function:
    bodyField.onkeypress = '';

    // Construct a table for the labels and inputs:

    var authorId = 'author_' + parentId;
    var linkId = 'link_' + parentId;
    var table = document.createElement('table');

    function tr(id, label, title) {
	return "<tr>"
	    + "<td>"
	    + "<label for=\"" + id + "\" title=\"" + title + "\">" + label + "</label></td>"
	    + "<td><input type=\"text\" id=\"" + id + "\" title=\"" + title + "\"></td>"
	    + "</tr>";
    }

    table.innerHTML = tr(authorId, "Name", "Required: Your real name or nickname")
	+ tr(linkId, "Website", "Optional: Link to a website of your choice");

    // Submit button:
    var submitButton = document.createElement('input');
    submitButton.type = 'submit';
    submitButton.value = 'Publish';
    submitButton.disabled = 'true';
    submitButton.onclick = function(){addComment(parentId, bodyField, bodyFieldClone,
					     authorId, linkId);};

    // Initially hide the table and button, prepare for slideDown:
    table.style.display = 'none';
    submitButton.style.display = 'none';
    table.className = 'slide';
    submitButton.className = 'slide';

    // Add the table and button to the document:
    bodyField.parentNode.appendChild(table);
    bodyField.parentNode.appendChild(submitButton);

    // Slide them in, avoid tyring to do it again:
    $('.slide').slideDown('fast');
    table.className = '';
    submitButton.className = '';

    // Mark the field as now being part of an expanded form:
    $(bodyField.parentNode).addClass('expanded');

    // dis/enable button on changes in the editable and Author field:
    var u = function(){updateSubmitButton(bodyField.id, authorId, submitButton);};
    bodyField.onkeyup = u;
    document.getElementById(authorId).onkeyup = u;
}

function getBody(id){
    var editable = GENTICS.Aloha.getEditableById(id);
    return editable.getContents();
}

function htmlStringToNodes(string){
    var div = document.createElement('div');
    div.innerHTML = string;
    return div.firstChild;
}

function addComment(parentId, bodyField, bodyFieldClone, authorId, linkId) {
    var commentData = {parent: parentId,
    		       body: getBody(bodyField.id).replace(/<br>$/, ''), // Drop traling <br>
    		       author: document.getElementById(authorId).value,
    		       link: document.getElementById(linkId).value};

    $.post('/comment',
    	   commentData,
	   // On success, handler answers with HTML for the new comment:
    	   function(commentRendition){
    	       // Remove comment form:
    	       var commentDiv = bodyField.parentNode;
	       var commentDivParent = commentDiv.parentNode;
	       $(commentDiv).fadeOut('slow',
				     function() {afterAddComment(commentDiv, commentDivParent,
								 commentRendition, bodyFieldClone);});
    	   });
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
    // Make sure it will no longer:
    $('div.empty').removeClass('empty');
}