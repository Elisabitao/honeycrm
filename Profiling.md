# Profiling client side code using Firebug #

  * First install the Firefox extension Firebug if you have not already installed it: http://getfirebug.com/
  * Open honeycrm in Eclipse and use the GWT Eclipse plugin to compile the project with **output style** pretty or detailed. This may take longer than the usual compilation process.

  * To profile honeycrm on app engine deploy it after it has been completely compiled with your new output style settings. Eclipse will re-compile honeycrm before deployment using the same output style settings and upload the js code as usual. Have a look at the size of your war/gae/...cache.html files. Uploading may take longer that usually due to the source code size increasements.

  * To profile honeycrm locally run it as a web application from eclipse. Then open the application in Firefox using the following URL **http://127.0.0.1:8888/Gae.html**. Note that you have to remove the **?gwt.codesrv** stuff from the URL that you normally put at the end of the URL (e.g. **http://127.0.0.1:8888/Gae.html?gwt.codesvr=127.0.0.1:9997**). This runs the application in JavaScript mode locally. So instead of executing your Java code through the browser plugin Firefox will execute the generated JavaScript code. This allows you to profile the application locally as well.

  * Whether running honeycrm locally or deployed to app engine you can now use the firebug console to start profiling.

## JavaScript code size increase from obfuscated to detailed output style ##

| output style | comment | average size of war/gae/...cache.html files (MB) |
|:-------------|:--------|:-------------------------------------------------|
| obfuscated   | no real mapping between js and java method name possible | 1                                                |
| pretty       | js method names are equal to method names of your classes but you have to guess the class name | 3.1                                              |
| detailed     | js method names contain class name and method name | 6.7                                              |

## More resources on client side performance analysis ##

http://geekycoder.wordpress.com/2010/04/07/gwt-tips-configuring-java-profilers-to-profile-gwt-client-app-in-eclipse/
http://googlewebtoolkit.blogspot.com/2008/11/profiling-gwt-application-performance.html
http://sinnema313.wordpress.com/2008/11/16/performance-tuning-a-gwt-application/

# Profiling server side code using JProfiler #

  * Append the jprofiler agentpath to the vm arguments line of your honeycrm project i.e. the arguments passed to the JVM when honeycrm is run from eclipse (or whatever IDE you use). For OSX append the following
```
-agentpath:/Applications/jprofiler6/bin/macos/libjprofilerti.jnilib=port=8849
```

  * Do not forget to increase the heap size if neccessary. You do not want to slow down the JVM with garbage collection during profiling. Use the following JVM arguments to increase the initial and maximum size of the heap
```
-Xms512m -Xmx1280m
```

  * To see the profiling live in action have a look at the following screencast http://www.youtube.com/watch?v=zUJUSxXOOa4

<a href='http://www.youtube.com/watch?feature=player_embedded&v=zUJUSxXOOa4' target='_blank'><img src='http://img.youtube.com/vi/zUJUSxXOOa4/0.jpg' width='800' height=600 /></a>