Don't accept PUTs of comments with a final level parent or not existing parent
Suppress Publish button tooltip, if comment form input is sufficient
Add Email field to comment form
Check "#comments .updated > div:nth-child(3)" CSS
Validate pagination range with regex in one step
Consider moving slug->resource fns to validate ns
Don't use CSS id for things like "comment-anchor-for_9", use data attributes
Get comments onto basline grid, or hide baselines
Make it easier to find the Reply field for a comment and to keep both in view, or give up on Reply at thread end
When expanding comment form, auto-scroll if necessary to show the complete form

# Pages and entities

Comments (form, create, edit, delete, editable by admin)
Add footer
Delete article
Move article
Note on login form if already logged in


# Editing

Get strong and emphasis to show up on Aloha floating menu (https://github.com/alohaeditor/Aloha-Editor/issues/861)
Disable Aloha Save button until there are changes (even better: unless), look into smartContentChange
Flash updated timestamps after saving an article and/or other feedback


# Comments

Do not write "Reply" in comment fields, if JS is disabled
Validate the links
Update page with comments from other sessions


# Non-essential features

Thumbnails
Tags
Series
Asynchronously update blob list
Asynchronously update admin article list
Auto-saving articles
Color coded margin for auto-saving, marking edits and pointing to the marks
Handle delete/cancel-delete asynchronously
Email notifications/subscriptions
Pingbacks
Blog-wide search
Look into offering older articles via the feed
Consider "N minutes/hours/days/... ago" as addition or replacement for current timestamps
Handle older-newer sorting of articles


# Testing

Validate HTML
Validate CSS
Cross-Browser


# Optimization

Memchache/memoization
Minify JS and CSS files
Minify inline JS (tlog.render.html.parts.script/aloha)
Etags?
Do not try to remove CSS class empty for every single comment submission (Cause not implemented, yet)


# Code

Reconsider option macros
Use ring-json?
Docstring style, consider to use Marginalia
Consider generic XMLHttpRequest function or relying on JQuery for that


# Correctness

Do something about older IEs lacking getElementsByClassName, used in time.js?
Do something about relying on JSON.stringify (http://caniuse.com/json)?
Make sure the slug is valid and free, before PUTing an article
Validate that the slug exists, before accepting a POST for an article
Update slugsInUse asynchronously
Improve defopt macro to not mistake a sole string as body for a docstring
Avoid error on attempting to insert an article_feed_rel with invalid slug and/or feed
Consider Typed Clojure
