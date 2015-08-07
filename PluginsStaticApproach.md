# Introduction #

In this article we want to explain an approach to integrate plugins statically into the codebase. A sample project based on this idea can be found in the repository https://honeycrm.googlecode.com/svn/branches/PlatformTest. This is deployed in the AppEngine here http://honeyplatform.appspot.com/. It consists of a minimalistic core system that is extended by two plugins that are loaded at runtime and integrated into the UI.

Plugins are part of the platform i.e. the whole system. They can be (in-)activated per user using a front-end which is naturally part of the platform i.e. the **app store**. Thus users never leave the application: They use it for their _usual_ work **and** for browsing through available plugins.

![https://honeycrm.googlecode.com/svn/wiki/plugins/approachstaticplugins.png](https://honeycrm.googlecode.com/svn/wiki/plugins/approachstaticplugins.png)

# Intellectual property #

Companies developing commercial plugins might want to sell their plugins without publishing their sourcecode. They should create JAR files containing the bytecode of their plugins. Hosting companies could then
  * add those JAR files to the classpath of the platform
  * run all test cases locally
  * deploy a new version of the application
  * test the deployed application extensively
  * make the updated application the default (e.g. by using the app engine front end).

# Multi tenancy? #

The plaform architecture will differ between multi and single tenancy plaforms. However, the proposed plugin infrastructure supports both ideas.

## Multi tenancy platform ##

In a multi tenancy platform comments and recommendations about plugins could be shared across different tenants if desired.

## Single tenancy platform ##

In a single tenancy platform the application could even abstract away the fact that plugins are installed in an instance of only one company: If desired comments about plugins (or **apps**) could be stored in a global repository. This would make sure that users can see comments and recommendations about plugins from users/companies/employees around the world.