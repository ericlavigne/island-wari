This [competition](http://www.codetown.us/group/contesttown/forum/topics/wari-contest-1) was announced at noon on December 27, 2009, and contestants had until midnight to create a Wari program. 

Since I won the competition, Michael Levin has asked me to discuss my code at the next [GatorJUG meeting](http://www.codetown.us/events/gatorjug-on-iphone) on January 13.

You can try out the game server on [my website](http://ericlavigne.net:8054).

To build this project yourself, you will need [leiningen](http://zef.me/2470/building-clojure-projects-with-leiningen).

     lein deps
     lein compile
     lein test
     lein uberjar
     java -jar island-wari-standalone.jar

