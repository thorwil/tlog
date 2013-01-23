Disable feed checkbox until response
Reconsider option macros
Disable all styling buttons on Aloha floating menu, when editing a title
Put noscript-warning on article pages
Get strong and emphasis to show up on Aloha floating menu (https://github.com/alohaeditor/Aloha-Editor/issues/861)
Journal
Comments (form, create, edit, delete)
Delete article
Move article
Consider "N minutes/hours/days/... ago" as addition or replacement for current timestamps
Decide on home page content and journal URL
Disable Aloha Save button until there are changes (even better: unless), look into smartContentChange
Use ring-json?
Docstring style, consider to use Marginalia
Consider Typed Clojure
Make sure the slug is valid and free, before PUTing an article
Validate that the slug exists, before accepting a POST for an article
Flash updated timestamps after saving an article and/or other feedback
Note on login form if already logged in
Consider generic XMLHttpRequest function or relying on JQuery for that
Look into minimizing tlog.render.html.script/aloha

# Mostly copied from tlog-gae-clj:


## Features

Thumbnails
Tags
Series
Asynchronously update blob list
Asynchronously update admin article list
Auto-saving articles
Color coded margin for auto-saving, marking edits and pointing to the marks
Handle delete/cancel-delete asynchronously
(Email notifications/subscriptions)
(Pingbacks)
(Blog-wide search)
(Look into offering older articles via the feed)


## Testing

Validate HTML, CSS
Cross-Browser


## Optimization

Memchache/memoization
Minify JS and CSS
Try to load less JS
Etags?
Do not try to remove CSS class empty for every single comment submission


## Correctness

Do something about older IEs lacking getElementsByClassName, used in time.js
Do something about relying on JSON.stringify (http://caniuse.com/json)?
Update slugsInUse asynchronously
Improve defopt macro to not mistake a sole string as body for a docstring
Avoid error on attempting to insert an article_feed_rel with invalid slug and/or feed


## Comments

Try switching box model for better comment section layout
Do not write "Reply" in comment fields, if JS is disabled
(Validate the links)
(Update page with comments from other sessions)
