(ns tlog.render.html.parts.script
  "HTML parts dealing with JavaScript. To be handed to skeleton as values of keys other than
   :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-css]]
            [clojure.data.json :as json]))

(def ^:private static-slugs
  "Slugs in use for static routes plus fragments used to build CSS IDs."
  ["logout" "login" "admin" "title_" "content_"])

(def ^:private article-form-js
  "Link JS for article forms."
  (let [slugs-in-use (into static-slugs
                           tlog.data.article/slugs)]
    (html
     [:script  (str "var slugsInUse = " (json/write-str slugs-in-use) ";")]
     [:script {:src "/scripts/article-form.js"}])))

(defn- aloha
  "JS to link and configure Aloha editor."
  [{:keys [aloha-save-plugin aloha-admin-editables]}] 
  (html
   (include-css "/scripts/aloha/css/aloha.css")
   [:script
    ;; Disable Aloha sidebar.
    ;; Get Aloha to use the JQuery to be linked before this, instead of its own local copy.
    ;; Configure the Aloha toolbar.
    "Aloha = window.Aloha || {};
     Aloha.settings = {
	sidebar: {
           disabled: true
	},
        plugins: {
           format: {
              editables: {
                 // no formatting allowed for title
                 '.title': []
              }
           },
           link: {
              editables: {
                 // no links in title
                 '.title': []
              }
           }
        }
     };
     Aloha.settings.jQuery = window.jQuery;
     Aloha.settings.toolbar = {
        tabs: [
           {
              label: 'Format',
              components: [
                 [ 'strong', 'emphasis', 'subscript', 'superscript', 'strikethrough' ],
                 [ 'formatBlock' ],
                 [ 'createTable', 'characterPicker', 'insertLink' ],
                 [ 'tlog_save' ]
              ]
           }
        ],
        exclude: [ 'tab.format.label', 'tab.insert.label']
     };"]
   [:script {:src "/scripts/aloha/lib/require.js"}]
   [:script {:src "/scripts/aloha/lib/aloha.js"
             :data-aloha-plugins (str "common/ui,
                                       common/format,
                                       common/link,
                                       common/list,
                                       common/table,
                                       common/characterpicker,
                                       common/paste,
                                       common/undo"
                                      aloha-save-plugin)}]
    aloha-admin-editables
    ))

(def ^:private jquery
  "Use JQuery 1.7.2 from Google Hosted Libraries."
  "<script src=\"//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js\"></script>")

(def ^:private aloha-ready-admin-editable
  "Fragment to make DOM elements with the .admin-editable CSS class editable via Aloha."
  {:aloha-admin-editables
   (html [:script "Aloha.ready( function() {Aloha.jQuery('.admin-editable').aloha();});"])})

(def ^:private aloha-save-plugin
  "Fragments to load the tlog_save Aloha plugin for a Save button on the floating menu."
  {:aloha-save-plugin
   ",custom/tlog_save"})

(def aloha-admin-create
  "Assemble parts to set up Aloha editor for creating new articles by the admin."
  (str jquery article-form-js (aloha aloha-ready-admin-editable)))

(def aloha-admin-edit
  "Assemble parts to set up Aloha editor for editing articles by the admin."
  (str jquery (aloha (merge aloha-ready-admin-editable aloha-save-plugin))))

(def aloha-guest
  "Assemble parts to set up Aloha editor for guests."
  (str jquery (aloha nil)))

(def client-time-offset
  "Link time.js script for converting UTC to the client's local time."
  (html
   [:script {:src "/scripts/time.js"}]))

(def feed-selection
  "Link JS for posting changes of which feeds an article belongs to."
  (html [:script {:src "/scripts/feed-membership.js"}]))