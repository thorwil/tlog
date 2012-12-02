"use strict";

/* Send updates on clicking feed selection checkboxes */

var feedCheckboxes = $('input.feed, [type="checkbox"]');

/* Also defined via slug.js:
var currentSlug = slugInput.val(); */

function sendFeedSelectionChange(feed, checked) {
    $.post("/admin/feed-selection-change", {slug: currentSlug,
					    feed: feed,
					    checked: checked});
}

feedCheckboxes.click(function(){
			 sendFeedSelectionChange(this.name, this.checked); /* .checked is the state after the click! */
		     });

