# Project Management #

  * **Support instant demonstration and participation**: Offer a live demo, source download, screencasts, screenshots, wiki, issue tracker, mailing list, etc.
  * **Advertise:** Publish screencasts on youtube, add your source repository to ohloh, discuss about the project on other mailing lists, etc. Just create as much links to the project page as possible. And even as a nerd you actually have to talk to people about the project face-to-face. Sending out emails will almost certainly have no effect at all.

# Software Architecture #

## General ##

  * **TDD is only applied if it speeds up the development:** People do not test their code because they think it is a punishment. Developers are lazy and will not start writing tests in addition to their other tasks. If a developer writes tests because he/she is forced to do so, the resulting test cases are very likely to have a poor code-coverage and quality and will not be maintained properly. The only way to convince developers of the application of TDD is to demonstrate to them that it results in a noticable speed-up and simplification in the development process. Developers will write tests if it saves them time, makes their lives easier, and more comfortable.

  * **Run tests cases rather than testing manually:** If you write tests as a means of speeding up the development your test suite will gradually grow. Running this large number of test cases gives you confidence about your changes. More importantly it is several orders of magnitude faster than manual testing. Manual testing either has a low code-coverage or takes a lot of time. TODO add link to 70, 20, 10 rule

  * **Untested code contains even more bugs than tested code:** Code that is covered by automatic test cases will still contain bugs. However, the parts of your application that rely on manual testing will contain even more bugs. Those parts of your application will break because you forgot to test all use cases manually. You will detect these kind of bugs on a late Sunday evening and will be forced to fix them to the next morning. Or even worse, your customer will detect the bug in the morning and you will have to fix it within an hour without introducing two more bugs.

  * **Do the important stuff right from the start:** Some of the things you want to get right from the start are the browser's history (the back and forward buttons), URLs representing the state of your application, and the localization. In addition, you want to design your application in such a way that its components love to be tested. Once your application has reached a significant size, you will refuse to implement those features because you expect it to be difficult. If you instead decide to implement those features it will be a pretty error-prone process which will introduce more bugs than you expected.

  * **Don't repeat yourself (DRY):** DRY should be applied to all parts of your code base. Do not forget, to apply DRY in your views as well, e.g. by inheritance. Everytime you implement the same functionality twice, you will forget to update the other place(s) during maintainance activities.

  * **Someone else is smarter than you:** Reuse existing patterns, designs, and code.

## Client Side ##

  * **Indicate loading:** Users love to get feedback. While your application should never distract the user from his actual work, the user has to know when he/she has to wait for a moment. Of course, no user likes waiting. However, worse than waiting at all is not telling the user that he/she has to wait. An in-experienced user will assume that the application is broken and/or repeatedly submit requests from the application resulting in and increased load for the server side.

  * **Reuse your widgets:** Creating, configuring, and attaching widgets to the browser's DOM is expensive. If possible, create and attach the widgets you need only once and hide them if required. Consult tools like yslow to find out whether or not your DOM is considered to be too "complex". While you may think your page is rendered quick enough. But when you open the page on a old machine with an out-dated browser and/or mobile devices.

  * **Offer views for different screen sizes:** Your views should be interchangable to make sure your application works well on big office screens (2560x1920), laptops (1280x800), tablets (1024x800), and smartphones (640x480).

  * **Support power-users:** Everyone who uses your application on a day-to-day basis will love every shortcut that you build into it. For instance, provide keyboard shortcuts for the most common operations, put barcodes on the print-outs that you generate which can be scanned later on to allow finding related documents instantly, put the focus (i.e. the cursor) automatically into the widget where it is most-likely to be expected by the user, etc.

  * **Do not over-engineer the UI:** Do not add features on the basis of what you expect to be important. Start with something ridiculously simple. Get the basics right. Experiment with it and gradually extend it.

  * **Nobody knows what is actually required:** Neither you, nor the users, nor any smart consultant is able to know in advance what the user wants and/or needs. Furthermore, what the user describes as being important may have nothing to do with making him/she more productive. It may simply be very similar to the way he already works. There will never be a single description of the requirements that all users agree on. You have to create an atmosphere and establish a process that allows the exploration of requirements which most users agree on. The rest is experience and intuition.

  * **Use lazy views:** The constructors of your views should not contain expensive operations. When you are using UiBinder all you really have to do within your constructors is to call the initWidget function. Everything else should be done either at the latest point possible or when your user is idling.  However, it can be difficult to predict your user's behavior. Thus, you may simply not know when you can execute the potentially expensive initialization function. Instead you may defer the initialization until the latest time possible within a split-point.

  * **Use dumb views:** Your views should not contain any logic and/or make complex decisions. Since it is very likely that you will never test your views they should just display your widgets and delegate any important decisions to other parts of your code which are easy to test, e.g. your presenters. TODO add link to dumb views pattern of foster

  * **Declarative is better than imperative:** Use UiBinder to express the layout of your widgets declaratively rather than coding it by hand. This results in shorter, better readable code and makes your views easier to maintain. Keep in mind that the maintainability of your views is especially important if you want to provide different views for a range of mobile devices.

## Client/Server communication ##

  * **Accessing data from memcache is about one order of magnitude faster than from BigTable:** In read-mostly scenarios, consider caching parts of the database in memcache. Do not forget, however, to update your cache in the case of updates. Keep in mind that there is no information about how long data will be stored in memcache. Depending on the amount of data you want to put into memcache, the initial bulk-write may take some time. Thus, you may want to use background tasks instead for inserting data into memcache. You use memcache to speed up reads. However, you do not want to introduce a new performance penalty every time users have to wait for data being put into memcache.

  * **Take round-trip times into account:** If you host your application on Google App Engine and access it from Europe you experience a constant performance cost of about 150 ms. TODO Add Traceroute Visualization Link. You can imagine how this number is increased by actually doing work on the server side. Keep in mind, when you are optimizing the performance of your server side code. For instance, if you reduce the run-time for an operation from 20ms to 10ms, the overall time between the request and response will only be reduced from 150+20=170ms to 150+10=160ms.

  * **Keep your server-side hot:** Waking up your application and/or starting new instances of takes between one and several seconds. The actual load time is application-specific and may depend on the current load on the App Engine servers. I my own applications, I already experienced a wake-up penalty of more than six seconds. You cannot advertise your application as being extremely responsive if the first user that tries to login has to wait for such a long time. Your application either needs a constantly high load, you can reserve instances, or you can create a background job that queries the App Engine server side every five minutes (even though

  * **Use split-points, if appropriate:** Yes, you should use split-points to lower the initial load-times of your application. However, have a look at the amount of data which is loaded at run-time. For instance, consider the amount of time required to download the data of a small split-point, e.g. 300 bytes. Downloading the JavaScript code separately will take at least the round-trip time, e.g. 150 ms. Downloading 300 bytes more as part of the original JavaScript source will not be noticable by any user. Besides split-points do not make your code more readable.

  * **Reduce the number of HTTP requests:** In traditional non-AJAX LAMP applications, each of the user's clicks may result in up to 150 separate HTTP request. Only one of them is the request to the PHP script which is executed. The rest accounts for static content, e.g. images, CSS, and JavaScript files. To speed up the rendering and allow your application to scale, you want to lower the number of HTTP request to a minimum. For instance, you can put all of your images into a single file and use CSS sprites and put your CSS code into the widgets XML code. In addition, you can bundle requests of the client and send a single response back instead of responding separately to each request.

  * **Push rather than pull:** Use GWT's server side push approach to notify clients of updates instead of letting clients periodically pull for updates. This saves bandwidth for the clients and lowers the load of your server.

## Additional Resources ##
  * http://www.userfocus.co.uk/articles/4-forgotten-principles-of-usability-testing.html