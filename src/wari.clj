(ns wari
  (:use compojure))

(defn where-stones-fall [start skip stones]
  (take stones (remove #(= skip %) (drop (inc start) (cycle (range 14))))))

(defn bin-to-skip [turn]
  (if (= turn 1) 13 6))

(defn bin-to-play-again [turn]
  (bin-to-skip (if (= turn 1) 2 1)))

(defn alter-board [board turn move]
  (let [stones (where-stones-fall move (bin-to-skip turn) (nth board move))]
    (map (fn [b i] 
	   (+ (if (= move i) 0 b) 
	      (count (filter #(= i %) stones))))
	 board (range 14))))

(defn next-turn [board turn move]
  (let [last-stone (last (where-stones-fall move (bin-to-skip turn) (nth board move)))]
    (cond (= (bin-to-play-again turn) last-stone) turn
	  (= turn 1) 2
	  (= turn 2) 1)))

(defn move-result [board turn move]
  {:pre [(or (= turn 1) (= turn 2))
	 (or (and (= turn 1) (< move 6))
	     (and (= turn 2) (> move 6) (< move 13)))
	 (< 0 (nth board move))]}
  {:turn (next-turn board turn move) 
   :board (alter-board board turn move)})

(defn board-route [turn board]
  (apply str (concat ["/" turn "/0/"] (interpose "/" board)))) 

(defn render-board [whosturn slots]
  {:pre [(or (= whosturn 1) (= whosturn 2))]}
  (let [vert [:img {:src "/vertical.png"}]
	horiz [:img {:src "/horizontal.png"}]
	small [:img {:src "/small.png"}]
	big [:img {:src "/big.png"}]
	top [:img {:src "/top.png"}]
	bot [:img {:src "/bottom.png"}]
	picname-to-imgcol #(vector :td [:img {:src (str "/" % ".png")}])
	slot-to-link 
	(fn [move] 
	  (let [result (move-result slots whosturn move)]
	    [:td [:a 
		  {:href (board-route (result :turn) (result :board))} 
		  [:img {:src (str "/" (nth slots move) ".png")}]]]))] 
    [:table {:width "50%"}
     [:tr (map #(vector :td %) (interpose horiz (repeat 7 small)))]
     [:tr [:td vert] 
      (map 
       (fn [slot] 
	 (cond (= slot "vertical") (picname-to-imgcol "vertical")
	       (= (nth slots slot) 0) (picname-to-imgcol 0)
	       (= whosturn 2) (picname-to-imgcol (nth slots slot))
	       :default (slot-to-link slot)))
       (interpose "vertical" (reverse (range 6))))
      [:td vert]]
     [:tr [:td vert] [:td top]
      (map #(vector :td %) (interpose big (repeat 5 vert)))
      [:td top] [:td vert]]
     [:tr [:td vert] (picname-to-imgcol (nth slots 6))
      (map #(vector :td %) (interpose big (repeat 5 vert)))
      (picname-to-imgcol (nth slots 13)) [:td vert]]
     [:tr [:td vert] [:td bot]
      (map #(vector :td %) (interpose big (repeat 5 vert)))
      [:td bot] [:td vert]]
     [:tr [:td vert] 
      (map 
       (fn [slot] 
	 (cond (= slot "vertical") (picname-to-imgcol "vertical")
	       (= (nth slots slot) 0) (picname-to-imgcol 0)
	       (= whosturn 1) (picname-to-imgcol (nth slots slot))
	       :default (slot-to-link slot)))
       (interpose "vertical" (range 7 13)))
      [:td vert]]
     [:tr (map #(vector :td %) (interpose horiz (repeat 7 small)))]    ]))

(defroutes wari-routes
  (GET "/*"
       (or (serve-file (params :*)) :next))
  (GET "/"
       (html [:h1 "Welcome to the Island Wari game server."]
	     [:a {:href (board-route 
			 1 
			 (apply concat (repeat 2 (concat (repeat 6 4) [0]))))} 
	      "New game"]))
  (GET "/:whosturn/:computerplays/:x/:x/:x/:x/:x/:x/:x/:x/:x/:x/:x/:x/:x/:x"
       (let [turn (Integer/parseInt (params :whosturn))
	     board (map #(Integer/parseInt %) (params :x))]
	 (html 
	  (if (or (= turn 1) (= turn 2))
	    (render-board turn board)
	    [:p turn]))))
  (ANY "*"
       (page-not-found)))

(run-server {:port 8084}
	    "/*" (servlet wari-routes))
