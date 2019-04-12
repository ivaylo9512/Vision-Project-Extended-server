# Vision-Project-Extended-server
Vision Project Extended server

Server side for Restaurant app with chat. You can check the functionality and some quick demos here:

## How to run

### From the EXE file

You can run the application from the executable file it has all dependencies, the application is written in Java 8 - jre 1.8.

If you don't have the required jre the exe file will send you to the page where you can download it.

You should download the server-side-Jar folder, because it uses the upload folder in it for the users' images.

If Upload folder is not present it will be created, but all the images for the pre-created users in the database won't be present.

It runs the application on 8080, you should check your ports first and kill any tasks on the port.

The EXE uses inner database so you don't need to execute the sql. It has pre-added data in it you can log with username: admin password: password.

### Without the EXE file

You need jre 1.8.0 editor and a database server:

Execute the sql:
`$ mysql -u root -p < database.sql`
The sql is set to user: root password: 1234. You can change that from the application.properties

Run the app:
`$ ./gradlew bootRun`

Again runs on :8080 port you can change that from application.properties

You can use user with role chef:
`username: George`, `password: password`

And user with role Server:
`username: Peter`, `password: password`
