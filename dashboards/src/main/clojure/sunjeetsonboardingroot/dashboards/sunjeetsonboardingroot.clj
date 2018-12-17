(ns sunjeetsonboardingroot.dashboards.sunjeetsonboardingroot
  (:require [hyperion.core :as h]
            [hyperion.metrics :as x]
            [hyperion.modules :as m]))

;;;; **********************************
;;;;  Metrics
;;;; **********************************

; Our custom business logic metrics
(def greeting-is-set-count "SunjeetsOnboardingRootResource.numTimesGreetingIsSet")

;;;; **********************************
;;;;  Modules
;;;; **********************************
; Greeting
(defn greeting-is-set-count-m [scope]
      {:title "Number of times the greeting is set"
       :lower 0
       :query [(h/line (h/avg scope greeting-is-set-count) "Number of set calls" :blue)]})

;;;; **********************************
;;;;  Tabs / Dashboards
;;;; **********************************

(defn sunjeetsonboardingroot-dash [title scope]
      {:name title
       :rows [
              [(greeting-is-set-count-m scope)]
              ]})
