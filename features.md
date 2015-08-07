# current feature set #

## user interface niceties ##
  * To start editing of an entity this is currently displayed one just has to click on an attribute label (or click the "Edit" button).
  * The relationship tables can be disclosed per module to save space and to focus the user on the ones that are of interest for the current task.
  * The email addresses are displayed in shortened form to save horizontal space on the screen. But since they are links they can still be clicked to open the users default email client.
  * Website fields are displayed as links, too.

## display relationships and prefill relate fields ##
All items that are reference the entity that is currently open are displayed under its attributes. E.g. all the offerings are shown under the contacts attributes. A "Create" button at the top of the offering relationship table allows to create new offerings and prefill the contact field with the displayed contact (see TODO insert link to screencast).

## caching data on client side ##
to reduce response time information is cached on client side. this reduces the number of callbacks to the server. currently this is only done for relate fields and relationship information.

## contacts and accounts csv import from sugarcrm ##
contact data exported via the default csv export from sugarcrm can be imported into honeycrm using the admin panel. currently not all fields are mapped into honeycrm.

## full text search ##
users can do full text searches globally (across all modules) as well as within the scope of a particular module (e.g., "all contacts containing the word 'hidden' in some field).

## ideas about future features ##

## versioning ##
almost (?) everything should be automatically versioned. it should be possible to track modifications between two versions.

''
The examples above demonstrated using events to update a lastUpdated and dateCreated property to keep track of updates to objects. However, this is actually not necessary. By merely defining a lastUpdated and dateCreated property these will be automatically updated for you by GORM.
''

## reports ##
we need a powerful solution to configure reports that should be automatically generated.
  * jasper reports
    * http://www.jaspersoft.com/
    * open source, free version available, currently version 3.6 from august 09
      * http://jasperforge.org/website/jasperreportswebsite/trunk/highlights.html?group_id=252
      * Report output in PDF, XML, HTML, CSV, XLS, RTF, TXT
    * pro version $250 with promo code JRPRO\_promo\_909
      * supports flash charts like: http://www.jaspersoft.com/gallery/Gallery/Combi2DDY.html http://www.jaspersoft.com/reports-and-charts-gallery
  * crystal reports
    * http://www.crystalreports.com/
    * closed source, since 1984 http://de.wikipedia.org/wiki/Crystal_Reports
    * from sap. current version costs $500
  * birt

## searching ##
fulltext search is one requirement. additionally users may want to store configured searches and to them again similiar to reports. maybe we do not need stored searches if we implement reports properly.
  * fulltext search is easy to setup with the searchable plugin, see http://www.grails.org/dist/screencasts/screencast2.mov
  * problems
    * when searchable is enabled: yui datatable stays empty. elements are only listed with default listview specified in list.gsp
    * when querying for numbers it is not clear why the results are computed
    * the generated searchable controller does not work at all compared to the screencast
  * the fields for advanced searches should appear above the listview of each module. it should be an expandable panel.
  * users should be able to store searches and reuse them. they should appear in the ui as links that open the listview with a query.

## tagging ##
you will collect a lot of information when you run the crm. thus powerful search should be possible that could be supported by tagging.

## some collaborative stuff ##
  * indicate that another user is currently logged in
  * indicate that another user is currently editing the same thing
  * inspiration (?) from http://gobby.0x539.de/trac/