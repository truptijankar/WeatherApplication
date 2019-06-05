# WeatherApplication

Technologies used:
JAVA 8
MAVEN
SQL Database

Requirements:
For building and running the application you need:
JDK 1.8
Maven 3

Update below details in Application Properties:
spring.datasource.url
spring.datasource.username
spring.datasource.password

HOW TO deploy the Application:
1. Go to command prompt 
2. Go to path where pom.xml is residing and execute (skip if executing JAR directly)
	mvn package
3. Start application execute
	java -jar target/weatherApplication-0.0.1-SNAPSHOT.jar


HOW TO USE:
Go to browser and type : http://localhost:8080
Login page will be displayed (SCREEN1)
First time user : click on “New User? Click here to Register” button
Registration page will be displayed(SCREEN2)
Provide email address(user name), date of birth and password(minumum 5 characters) and click on register
If User is successfully registered then it will display message “User has been registered successfully”
Click on “Go to Login Page” button
Login using email address(user name)and password
On successfully login, it will display search option (SCREEN3)
Provide the valid city name in the search window and enter.
It will display the Weather Details(City Name, Weather Description, Current Temperature, Min Temperature, Max Temperature, Sunrise and Sunset)
On the same screen, it will also display the Weather History Details for the same user for user’s past search results
13. There are Edit/Delete options provided for the each Weather History Record
14. Edit Function :
				Click on Edit and it will open the Edit form to update the Temperature and Sunrise/Sunset details and click on Update button
15. Delete Function :
				Click on delete to delete the selected record
				
				
Note : Created and updated time are local timings