(ns tlog.render.html.fragment-script
  "HTML fragments for keys of skeleton, other than :main."
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-css]]))

(def ^:private aloha-make-admin
  "Fragments to use Aloha editor as admin"
  {:aloha-save-plugin
   ",custom/tlog_save"
   :aloha-admin-editables
   (html ;; Make elements with CSS class 'admin-editable' editable with Aloha:
    [:script "Aloha.ready( function() {Aloha.jQuery('.admin-editable').aloha();});"])})

(def ^:private static-slugs
  "Slugs in use for static routes."
  ["logout" "login" "admin"])

(def ^:private article-form-js
  "Link JS for article forms."
  (let [slugs-in-use (into static-slugs
                           tlog.data.article/slugs)]
    (html
     [:script  (str "var slugsInUse = " (clojure.data.json/write-str slugs-in-use) ";")]
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
                 [ 'createTable', 'characterPicker', 'insertLink' ]
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

(def aloha-admin
  "Assemble fragments to set up Aloha editor for the admin."
  (str jquery article-form-js (aloha aloha-make-admin)))

(def aloha-guest
  "Assemble fragments to set up Aloha editor for guests."
  (str jquery (aloha nil)))

(def client-time-offset
  "Link time.js script for converting UTC to the client's local time."
  (html
   [:script {:src "/scripts/time.js"}]))