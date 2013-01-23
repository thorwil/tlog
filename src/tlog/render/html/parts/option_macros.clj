(ns tlog.render.html.parts.option-macros
  "Macros for building HTML fragments that are to be handed to skeleton, as values of keys other
   than :main.")

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

(defmacro defopt
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

(defmacro defopt-fn
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