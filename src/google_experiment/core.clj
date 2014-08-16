(ns google-experiment.core
  (:require [clojure.string :as string])
  (:use [clj-webdriver.taxi]
        [clj-webdriver.driver :only [init-driver]])
  (:import [org.openqa.selenium.phantomjs PhantomJSDriver]
           [org.openqa.selenium.remote DesiredCapabilities]))
 
(set-driver!
 (init-driver
  {:webdriver (PhantomJSDriver.
               (doto (DesiredCapabilities.)
                 (.setCapability "phantomjs.page.settings.userAgent",
                                 "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36")
                 (.setCapability "phantomjs.page.customHeaders.Accept-Language" "en-US")
                 (.setCapability "phantomjs.page.customHeaders.Connection" "keep-alive")
                 (.setCapability
                  "phantomjs.cli.args"
                  (into-array String ["--ignore-ssl-errors=true"
                                      "--webdriver-loglevel=WARN"]))))}))

; (set-driver! {:browser :firefox} "https://github.com")
(defn search-in-clueweb-time
  [site-host]
  (let [out-file (str site-host ".html")

        url (str "https://www.google.com/search?q=site%3A"
                 site-host
                 "&biw=1297&bih=712&source=lnt&tbs=cdr%3A1%2Ccd_min%3A02%2F10%2F2012%2Ccd_max%3A05%2F10%2F2012&tbm=")]
    (do (to url)
        (spit out-file (html "html")))
    url))

(defn load-file
  [filename]
  (string/split-lines
   (slurp filename)))

(defn -main
  [& args]
  (let [hosts (load-file (first args))]
    (doseq [host hosts]
      (search-in-clueweb-time host)
      (take-screenshot :file (str host ".png")))))
