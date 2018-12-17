(ns sunjeetsonboardingroot.dashboards.master
  (:require [hyperion.core :as h]
            [hyperion.dashboards :as d]
            [hyperion.alerts :as a]
            [hyperion.atlas :as atlas]
            [sunjeetsonboardingroot.dashboards.sunjeetsonboardingroot :as sunjeetsonboardingroot]
            ))

;; This is the method we invoke to update our dashboards.
(defn update []

  (let [overrides {:max-instances 20 :tomcat :jmx}
        scope (h/nf-cluster "sunjeetsonboardingroot")]
    (atlas/upload-dashboard "sunjeetsonboardingroot"
                            {:owner "sunjeets@netflix.com"
                             :tabs  [
                                     (sunjeetsonboardingroot/sunjeetsonboardingroot-dash "Business Logic" scope)
                                     (d/service-dash "Service" scope overrides)
                                     (d/networking-dash scope overrides)
                                     (d/heap-dash scope overrides)
                                     ]})))
