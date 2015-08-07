

# introduction #
on this page we want to outline a possible plugin framework for the honeycrm application. users should be able to install plugins i.e. **apps** into honeycrm using an integrated app store.

## what is an app? ##

an app is a piece of software that extends the functionality of honeycrm. it is (un-)installable using the built-in app store like apps on mobile devices e.g. iphone or android devices. vendors should advertise their (non-)free apps using the app store.

## categorisation ##

| category | server side changes | client side changes | description | example |
|:---------|:--------------------|:--------------------|:------------|:--------|
| gadget   |                     | X                   | the simplest plugin. only modifies client side i.e. the user interface. no domain classes are added or modified. the user interface is extended at specified positions. e.g. honeycrm might allow that plugins add themselves to the header widget | a clock gadget might want to appear on top of the screen).  |
| add-on   | X                   | X                   | potentially modifies client **and** server side. domain classes might be added or extended. changes in the domain model might imply client side changes i.e. the user interface. | a booking module might add the booking entity and updates the user interface e.g. by adding fields to the contacts module |
| _more categories?_ |                     |                     |             |         |

## installation of new domain classes ##

apps modifying the server side might want to add or update domain classes. however, just adding the .java or .class files of the POJOs will not be enough - the data nucleus enhancer still has to be run after the domain classes have been added.

the requirement to run the data nucleus enhancer on new domain classes could be removed if we simplify our domain model to property lists. i.e. instead of using domain classes like contact, account, contract and so on only one domain class would exist storing a map of properties.

![https://honeycrm.googlecode.com/svn/wiki/plugins/domain.png](https://honeycrm.googlecode.com/svn/wiki/plugins/domain.png)

**UPDATE 2010/10/07** Since [r271](https://code.google.com/p/honeycrm/source/detail?r=271) we use the low level API instead i.e. we can add whatever domain class we want on the fly since no data nucleus enhancer has to be run. just create a new Entity instance of the desired kind, with the fields you want.

# ideas about installing and using apps #

## installation of plugins ##

to install new apps into honeycrm users go to the app store which is integrated into honeycrm. they browse through the existing apps. finally, they request the installation of an app **foo**. the server then requests a description of the plugin potentially by asking another server (**trusted plugin storage**). the plugin description is a package containing all files necessary to install the plugin into the platform e.g. the source code, localisation files and images.

based on the plugin description the server might have to
  * (compile and) load new classes (to install server side changes)
  * compile client side code to JavaScript (using the GWT compiler)

the new classes (byte code) and the generated javascript code have to be stored in the database. this allows to load the code from there each time the vm is (re-)started.

![https://honeycrm.googlecode.com/svn/wiki/plugins/installation.png](https://honeycrm.googlecode.com/svn/wiki/plugins/installation.png)

## plugin-aware application initialisation ##

during the application startup the byte code and JavaScript source have to be loaded from the database. the classes of the app have to be loaded using their byte code. the javascript source should be stored in memcache and send to the user after logging in.

![https://honeycrm.googlecode.com/svn/wiki/plugins/startup.png](https://honeycrm.googlecode.com/svn/wiki/plugins/startup.png)

## plugin-aware login sequence ##

after login two things have to be done:
  * the generated JavaScript code of all installed plugin has to be loaded: the client side requests the plugin js source. the server then requests it from the database/memcache and sends it to the client. the client then may have to **eval** the new source code to allow overriding existing behaviour (**if this is the desired behaviour?!?**)
  * the execution of client side plugin code might have to be started explicitly e.g. call AbstractPlugin.show() on all gadgets: to make this work plugins have to be detected by the platform (passive registration) or register themselves (active registration) in the platform.

![https://honeycrm.googlecode.com/svn/wiki/plugins/login.png](https://honeycrm.googlecode.com/svn/wiki/plugins/login.png)

# Problems #

there are still a lot of questions. just to name a few:

  * What is part of the plugin description/package?
    * Does the plugin contain source code or bytecode and generated JavaScript source?
  * How can apps be removed?
    * Can we unload the classes which have been loaded as part of the app?
  * How can we restrict/sandbox the behaviour of apps?


# resources #

## OSGi ##
  * introduction to OSGi http://de.wikipedia.org/wiki/OSGi
  * **Embedding GWT into Eclipse/OSGi and accessing other Plugins/Bundles** http://groups.google.com/group/google-web-toolkit/browse_thread/thread/bf6ba94319c46be8/4b1489460f3a0775?lnk=gst&q=plugins#4b1489460f3a0775
  * **Sharing objects among modules running in an OSGi platform**: ideas about building a GWT application based in OSGi modules http://groups.google.com/group/google-web-toolkit/browse_thread/thread/cb2f1eaa36c34eea/b070bbc846eae53f?lnk=gst&q=osgi#b070bbc846eae53f
  * blog entry showing how to create a _hello world_ gwt application within the OSGi framework http://blog.ianbull.com/2008/09/gwt-and-osgi.html
    * Equinox documentation provides details too http://wiki.eclipse.org/index.php/Google_Web_Toolkit_and_Equinox

## GWT and native JavaScript ##
  * GWT JavaScript native interface (JSNI) http://code.google.com/intl/de-DE/webtoolkit/doc/latest/DevGuideCodingBasicsJSNI.html