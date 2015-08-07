# the ten commandments #

01. testability
  * write code that is easy to test.
  * use test data
  * measure code coverage (e.g. EMMA eclipse plugin)
  * minimize the amount of code that is not testable.
  * measure performance in tests to evaluate design decisions.

02. responsive user interface
  * optimize for power users
    * recently used items
    * short cuts
    * autocompletion
    * system-wide search
    * user dependent (list) views
    * saved searches
    * automate whenever possible (e.g. create contract from offering / convert "a" to "b" / inform employee / create report / ...)
    * visualize and support workflows
  * what should be the response times?
  * write tests to measure performance easily.

03. be prepared for db migration issues
  * how can you migrate existing client installations when you changed the db schema? -> document this!

04. reuse mature existing components whenever possible

05. open project quickly to the community
  * tell others about the project / discuss about it
  * do prototypes

06. create prototypes.
  * publish new stable release every n weeks with little improvements.
  * use branches for experiments.

07. localise from the beginning and document how to localisation works.
  * prepare for different languages / currencies / date formattings / character sets

08. do not tie to one specific implementation
  * do not depend on one specific db / framework (e.g. ui framework)

09. automate whenever possible
  * automate tests / generation of documentation / deployment

10. provide interfaces to the outside world
  * other systems should be able to interact with the crm (e.g. webservices, POST/GET over JSON)