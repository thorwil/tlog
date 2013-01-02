Make articles editanle by admin, look into Aloha IDs
Consider "N minutes/hous/days/... ago" as addition or replacement for current timestamps
Decide on home page content and journal URL
Look into Drip
Use ring-json?

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

Only check article feed membership if you actually work with the info
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


## But how?

Make sure article content is put in <p>, to avoid <br>s
Disable Aloha Save button until there are changes (even better: unless), look into smartContentChange
Fix Aloha blocking in some new comment fields, when adding several comments in succession
Stay exactly on baseline grid
Automatically avoid collisions between article slugs and fixed routes
