Update the article timestamp on the page
Handle article feed selection changes
Docstring style
Disable all styling buttons on Aloha floating menu, when editing a title
Put noscript-warning on article pages
Get strong and emphasis to show up on Aloha floating menu
Consider "N minutes/hous/days/... ago" as addition or replacement for current timestamps
Decide on home page content and journal URL
Disable Aloha Save button until there are changes (even better: unless), look into smartContentChange
Look into Drip
Use ring-json?
Consider to use Marginalia
Consider Typed Clojure

# Copied from tlog-gae-clj:

Try switching box model for better comment section layout


## Features

Thumbnails
Tags
Series
G-channel update blob list
G-channel update admin article list
Handle delete/cancel-delete asynchronously
(Email notifications/subscriptions)
(Pingbacks)
(Blog-wide search)
(Look into offering older articles via the feed)
(Pages as sequences of blocks)


## Testing

Validate HTML, CSS
Cross-Browser


## Optimization

Memchache/memoization
Minify JS and CSS
Try to load less JS
Etags?
Do something about older IEs lacking getElementsByClassName, used in time.js
Do something about relying on JSON.stringify (http://caniuse.com/json)?
Do not try to remove CSS class empty for every single comment submission
Update slugsInUse asynchronously
Improve defopt macro to not mistake a sole string as body for a docstring


## Comments

Do not write "Reply" in comment fields, if JS is disabled
(Validate the links)
(Update page with comments from other sessions)
