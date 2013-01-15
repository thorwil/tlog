// Aloha plugin for saving edited articles via a 'Save' button on the floating menu.

function httpRequestPost(url, body) {
    var r = new XMLHttpRequest();
    r.open('POST', url, true);
    r.send(body);
    r.onreadystatechange = function() {
	if (r.readyState == 4) {
	    if (r.status == 200) // Status 200: OK
		alert(r.responseText);
	    else
		alert(r.status + ' ' + r.statusText + ': ' + r.responseText);
	}
    };
}

function postArticle(activeID, activeEditable, activeContent, activeModified) {
    if (activeID.match(/title_/)) {
	// The active editable contains the title, so get the body, too:
	var slug = activeID.replace(/title_/, '');
	var contentEditable = Aloha.getEditableById('content_' + slug);
	var content = contentEditable.getContents();
	var otherModified = contentEditable.isModified();
	var title = activeContent;
    } else {
	// The active editable contains the body, so get the title, too:
	var slug = activeID.replace(/content_/, '');
	var titleID = 'title_' + slug;
	var titleEditable = Aloha.getEditableById(titleID);

	if (titleEditable) {
	    // Title is in an editable. Get its content via Aloha.
	    var title = titleEditable.getContents();
	    var otherModified = titleEditable.isModified();
	} else {
	    // Title is not in an editable (because the title is used as link). Get its content via the DOM.
	    var title = document.getElementById(titleID).innerHTML;
	    var otherModified = false;
	}
	var content = activeContent;
    }

    // Post article, if at least one of title or body has been modified:
    if (activeModified || otherModified) {
	httpRequestPost(slug,
			JSON.stringify({title: title,
					content: content}));
    } else {
	alert('No changes to save!'); // Disabling the Save button would be preferable!
    }
}

function postCommentAuthor(activeID, activeEditable, activeContent, activeModified) {
    // Post comment author and optional link, if modified:

    var author = activeContent.replace(/<\/?[^>]+(>|$)|: /g, ''); // strip tags and trailing ': ' away

    var s = activeContent.match(/href=\"([^\"]*)/);
    if (s) {
    	var link = s[1];
    } else {
    	var link = '';
    }

    if (activeModified) {
	var commentData = {id: activeID.replace(/comment-author_/, ''),
			   author: author,
    			   link: link};
	$.post('/admin/update-comment', commentData);
    } else {
	alert('No changes to save!');
    }
}

function postCommentContent(activeID, activeEditable, activeContent, activeModified) {
    // Post comment body, if modified:
    if (activeModified) {
	var commentData = {id: activeID.replace(/comment-body_/, ''),
			   body: activeContent.replace(/<br>$/, '')}; // Drop trailing <br>
	$.post('/admin/update-comment', commentData);
    } else {
	alert('No changes to save!');
    }
}

function post() {
    var activeID = Aloha.getActiveEditable().getId();
    var activeEditable = Aloha.getEditableById(activeID);
    var activeContent = activeEditable.getContents();
    var activeModified = activeEditable.isModified();

    if (activeID.match(/comment-author_/)) {
	// It's a comment author field
	postCommentAuthor(activeID, activeEditable, activeContent, activeModified);
    } else if (activeID.match(/comment-body_/)) {
	// It's comment content
	postCommentContent(activeID, activeEditable, activeContent, activeModified);
    } else { // It's an Article title or content
	postArticle(activeID, activeEditable, activeContent, activeModified);
    }
}

define(['aloha/plugin', 'aloha/console'], function (Plugin, console) {
    'use strict';

    return Plugin.create('tlog_save', {
        defaults: {
            value: 10
        },
        init: function () {
            // Executed on plugin initialization

	    // Create a new button
	    Aloha.require(['ui/ui', 'ui/button'], function(Ui, Button) {
		var button = Ui.adopt("tlog_save", Button, {
		    tooltip: 'Save', // actually button text
		    size: 'large',
		    click: function () {
			post();
		    }
		});
	    });
        }
    });
});