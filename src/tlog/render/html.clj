(ns tlog.render.html
  "Build HTML in a modular way."
  (:require [ring.util.response :refer [response]]
            [hiccup.core :refer [html]]
            [hiccup.def :refer [defhtml]]
            [hiccup.page :refer [html5 include-css]]
            [tlog.render.configuration :as conf]
            [tlog.data.feed :as f]))


;; Utilities

(defn- valid-or-alt
  "Take a predicate, alternative and a seq. If the predicate on the first of the seq is
   true, return a new sequence of the first and next of the sequence. Else return a seq of the
   alternative and initial seq."
  [pred alt [x & xs :as x+xs]]
  (if (pred x)
    [x xs]
    [alt x+xs]))

(defn- name-with-attributes
  "To be used in macro definitions. Handles optional docstrings and attribute maps for a name to be
   defined in a list of macro arguments. Return a vector containing the name with its extended
   metadata map and the list of remaining macro arguments."
  [name maybe-docstring+attr+more]
  (let [[docstring maybe-attr+more] (valid-or-alt string? nil maybe-docstring+attr+more)
        [attr more] (valid-or-alt map? {} maybe-attr+more)
        attr (if docstring
               (assoc attr :doc docstring)
               attr)
        attr (if (meta name)
               (conj (meta name) attr)
               attr)]
    [(with-meta name attr) more]))

(defmacro defopt ;; Used only here and should be private, but can't use private macros in tests.
  "Macro for defining static, optional HTML fragments as single-keyword hash-maps, to be passed to
   skeleton.

   Take a name, optional doc-string, body and optional keyname. Use name to create a keyname, if
   none is given. Return the body as value to the keyname in a hash-map."
  [name* & more]
  (let [[name body-and-maybe-keyword] (name-with-attributes name* more)
        keyword* (or (second body-and-maybe-keyword)
                     (keyword name))]
    `(def ~name
       {~keyword* ~(first body-and-maybe-keyword)})))

(defmacro defopt-fn ;; Used only here and should be private, but can't use private macros in tests.
  "Macro for defining functions that return optional HTML fragments as single-keyword hash-maps, to
   be passed to skeleton.

   Take a name, optional doc-string, argument-list, body and optional keyname. Use name to create a
   keyname, if none is given. Return a form that returns the body as value to the keyname in a
   hash-map."
  [name* & more]
  (let [[name args+body+maybe-keyword] (name-with-attributes name* more)
        [args body] args+body+maybe-keyword
        keyword* (or (nth args+body+maybe-keyword 2 nil)
                     (keyword name))]
    `(defn ~name
       ~args
       {~keyword* ~body})))

(def title-with
  "Build string for the <head>, <title> tag."
  #(str %
        (when (not-empty %) conf/title-seperator)
        conf/title-main))


;; HTML page skeleton

(defn skeleton
  "HTML page skeleton."
  [{:keys [title
           scripts
           option-admin-bar
           option-noscript-warning
	   main]}]
  (html5
   [:html {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:title (title-with title)]
     scripts
     [:meta {:name "description" :content conf/meta-description}]
     [:meta {:name "author" :content conf/author}]
     [:link {:href conf/font-link
	    :rel "stylesheet"
	    :type "text/css"}]
     [:link {:rel "stylesheet" :href "/main.css" :type "text/css"}]
     [:body
      option-admin-bar
      option-noscript-warning
      [:div#main
       [:div#content main]]]]]))


;; HTML fragments for :main

(def login-form
  "Form for submitting username and password."
  (html
   [:h2 "Login"]
   [:form {:action "/login" :method "POST"}
    [:table
     [:tr
      [:td
       [:label "Username"]]
      [:td
       [:input {:type "text" :name "username" :value "" :autofocus "autofocus"}]]]
     [:tr
      [:td
       [:label "Password"]]
      [:td
       [:input {:type "password" :name "password" :value ""}]]]]
    [:input {:type "submit" :value "submit"}]]))

(def feed-selector
  "Area for selecting the feeds an article should appear in (checkboxes)."
  (html
   [:fieldset#feed-selectors
    [:legend "Include in the following feeds:"]
    (for [[label checked] f/feeds]
      [:input.feed (into {:type "checkbox" :name label}
                         (when checked {:checked "checked"}))
       [:label label]])]))

(def article-form
  "Form for adding articles."
  (html
   [:h2 "Write Article"]
   [:table.form
    [:tr
     [:td [:label "Title"]]
     [:td [:input#article_title_input {:type "text" :name "title" :autofocus "autofocus"
                               :required "required"}]]]
    [:tr
     [:td [:label "Slug"]]
     [:td [:input#article_slug_input {:type "text" :name "slug" :required "required"
                              :pattern "[a-zäöüß0-9_-]*"}]]]]
   feed-selector
   [:div#article_text_area {:class "article-body hyphenate admin-editable start-blank"} ""]
   [:input#article_submit {:type "submit" :value "Add new article" :disabled "disabled"}]))

(def ^:private aloha-make-admin
  "Fragments to use Aloha editor as admin"
  {:aloha-save-plugin
   ",custom/tlog_save"
   :aloha-admin-editables
   (html ;; Make elements with CSS class 'admin-editable' editable with Aloha:
    [:script "Aloha.ready( function() {Aloha.jQuery('.admin-editable').aloha();});"])})

(def article-form-js
  "Link JS for article forms."
  (html
   [:script "var slugsInUse = ['admin', 'login']"]
   [:script {:src "/scripts/article-form.js"}]))

(defn- aloha
  "JS to link and configure Aloha editor."
  [{:keys [aloha-save-plugin aloha-admin-editables]}] 
  (html
   (include-css "/scripts/aloha/css/aloha.css")
   [:script ;; Get Aloha to use the JQuery to be linked before this, instead of its own local copy.
    "Aloha = window.Aloha || {};
     Aloha.settings = {
	sidebar: {
        	disabled: true
	}
     };
     Aloha.settings.jQuery = window.jQuery;"]
   [:script {:src "/scripts/aloha/lib/require.js"}]
   [:script {:src "/scripts/aloha/lib/aloha.js"
             :data-aloha-plugins (str "common/ui,
                                       common/format,
                                       common/link,
                                       common/list,
                                       common/table,
                                       common/paste,
                                       common/undo"
                                      aloha-save-plugin)}]
    ;;Todo: bring in custom format plugin ^
   ;; [:script
   ;;  "GENTICS.Aloha.settings = {
   ;;             'plugins': {
   ;;               'tlog.Format': {
   ;;                    // all elements with no specific configuration get this configuration
   ;;                    config: ['strong', 'em', 'sub', 'sup', 'ol', 'ul', 'p', 'title', 'h1', 'h2',
   ;;                             'h3', 'h4', 'h5', 'h6', 'pre', 'removeFormat'],
   ;;                    editables: {
   ;;                        // no formatting allowed for title
   ;;                        '.title': [ ]
   ;;                    }
   ;;                },
   ;;                'com.gentics.aloha.plugins.List': {
   ;;                    config: ['ul', 'ol'],
   ;;                    editables: {
   ;;                        // no lists allowed for title
   ;;      	          '.title': [ ]
   ;;                    }
   ;;                }
   ;;           }
   ;;         }
   ;;         "]
    ;; Make elements with CSS class 'editable' editable with Aloha:
    ;; "<script type=\"text/javascript\">
    ;;     Aloha.ready( function() {
    ;;         Aloha.jQuery('.editable').aloha();
    ;;     });
    ;; </script>"
    aloha-admin-editables
    ))

(def ^:private jquery
  "Use JQuery 1.7.2 from Google Hosted Libraries."
  "<script src=\"//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js\"></script>")

(def aloha-admin
  (str jquery article-form-js (aloha aloha-make-admin)))

(def aloha-guest
  (str jquery (aloha nil)))


;; HTML options (fragments for keys other than :main)

(defopt option-noscript-warning
  (html [:noscript [:div#noscript-warning "This won't work with JavaScript disabled ;)"]]))

(defn- map-map-if
  "Take a key, 2 functions and a map. Return a map of the same kind, with one of the functions
   applied to each value. The first function, where the key matches the key argument, the second
   function in all other cases."
  [akey f-true f-false m]
  (into (empty m) (for [[k v] m] [k ((if (= akey k)
                                       f-true
                                       f-false)
                                     v)])))

(defhtml ^:private text+href->link
  [[text href]]
  [:a {:href href} text])

(defhtml ^:private text+href->span
  [[text href]]
  [:span text])

(defopt-fn option-admin-bar
  "Take no argument or a key referring to the current page. Render area with links for the logged in
   admin, with the link, that is associated with the key (if any), turned into plain text."
  [& [current]]
  (let [items-default (array-map :list ["List" "/admin"]
                                 :write ["Write" "/admin/write"]
                                 :file ["File" "/admin/file"]
                                 :logout ["Log out" "/logout"])]
    (if (or (nil? current)
            (some #{current} (keys items-default)))
      ;; No or known key given, render admin-bar:
      (let [items (map-map-if current text+href->span text+href->link items-default)]
        (html
         [:div#top
          [:nav {:id "admin-bar"}
           [:ul
            (for [v (vals items)]
              [:li v])]]]))
      ;; Unrecognized key, render error message:
      "option-admin-bar: mistyped or wrong key!"))
  :option-admin-bar)