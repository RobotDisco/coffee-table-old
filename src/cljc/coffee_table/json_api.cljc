(ns coffee-table.json-api
  (:require [schema.core :as s]))

(def Error
  "Error object explaining a thing that went wrong"
  {:title s/Str})

(defun JSONAPI
  [schema]
  "Top-level JSON API-compliant response"
  {(s/optional-key :data) (resource schema)
   (s/optional-key :errors) [Error]})

(defn resource
  "Create schemas for objects the end-user actually cares about"
  [schema]
  (let [rootschema {(s/optional-key :id) s/Int
                    :type (s/enum ["visits"])
                    :attributes schema}]
    (cond-pre rootschema [rootschema])))
