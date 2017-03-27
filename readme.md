Known bugs:

- On the mainpage, when you go to the Genre section of the site, clicking on a genre name is supposed to call ajax and load the albums that belong to that genre, instead it reloads the mainpage, the genre section is loaded and the albums are there, if you click on another genre name it loads that genre in the genre section as expected without a reload of the page. For some reason that only happens on the first time. -- i asked ken about this and he does not have any idea what it is.

- On the manager page, if you try to perform any actions with the buttons you have to double click them. A single click does not work. Please help


Helpful hints:
In your Shared With me folder in Google drive you can find the resources for the project.
This is just a simple read me that should be updated with the projects specifications and stuff.
If you the following error when compiling with testing turned on, start the GlassFish server:
"org.jboss.arquillian.container.spi.client.container.LifecycleException: Could not connect to DAS on: http://localhost:4848 | Connection refused: connect"