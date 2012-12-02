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
		var button = Ui.adopt("myButton", Button, {
		    label: 'Save',
		    size: 'small',
		    click: function () {
			post();
		    }
		});
	    });

	    // Add it to the floating menu
	    // FloatingMenu.addButton(
	    // 	'Aloha.continuoustext',
	    // 	that.button,
	    // 	i18nCore.t('floatingmenu.tab.format'),
	    // 	4
	    // );
        }
    });
});

function post() {
    var activeID = Aloha.getActiveEditable().getId();
    var activeEditable = Aloha.getEditableById(activeID);
    var activeContent = activeEditable.getContents();
    var activeModified = activeEditable.isModified();

    if (activeID.match(/comment-author_/)) {
	postCommentAuthor(activeID, activeEditable, activeContent, activeModified);
    } else if (activeID.match(/comment-body_/)) {
	postCommentBody(activeID, activeEditable, activeContent, activeModified);
    } else { // It's an Article title or body
	postArticle(activeID, activeEditable, activeContent, activeModified);
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
	//alert(author + " " + link);
	$.post('/admin/update-comment', commentData);
    } else {
	alert('No changes to save!');
    }
}

function postCommentBody(activeID, activeEditable, activeContent, activeModified) {
    // Post comment body, if modified:
    if (activeModified) {
	var commentData = {id: activeID.replace(/comment-body_/, ""),
			   body: activeContent.replace(/<br>$/, '')}; // Drop traling <br>
	$.post('/admin/update-comment', commentData);
    } else {
	alert('No changes to save!');
    }
}

function postArticle(activeID, activeEditable, activeContent, activeModified) {
    if (activeID.match(/title_/)) {
	// The active editable contains the title, so get the body, too:
	var otherID = activeID.replace(/title_/, '');
	var postID = otherID;
	var otherEditable = Aloha.getEditableById(otherID);
	var otherContent = otherEditable.getContents();
	var otherModified = otherEditable.isModified();
	var content = [activeContent, otherContent];
    } else {
	// The active editable contains the body, so get the title, too:
    	var otherID = "title_" + activeID;
	var postID = activeID;
	// Title might not be in an editable, so fall back to JQuery, if getEditablebyID returns null:
	var tryOtherEditable = Aloha.getEditableById(otherID);
	if (tryOtherEditable) {
	    var otherContent = tryOtherEditable.getContents();
	    var otherModified = tryOtherEditable.isModified();
	} else {
	    var otherContent = $('#' + otherID).html();
	    var otherModified = false;
	}
	var content = [otherContent, activeContent];
    }

    // Post article, if at least one of title or body have been modified:
    if (activeModified || otherModified) {
	$.post('/admin/update-article', {id: postID,
					 title: content[0],
					 body: content[1]});
    } else {
	alert('No changes to save!');
    }
}