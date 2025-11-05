# RENTSTUFF_API



#API for RENTSTUJFF operations.  


#To build the app, you will need eclipse.   Download it here - https://www.eclipse.org/downloads/download.php?file=/oomph/epp/2023-12/R/eclipse-inst-jre-win64.exe

#To run the app you will need a postgres db.  You can run this locally to get one up quickly

sudo docker run -d --name kong-database  -p 5432:5432 -e "POSTGRES_USER=kong" -e "POSTGRES_DB=kong" -e "POSTGRES_PASSWORD=kong" postgres:10.19 -c shared_buffers=512MB -c max_connections=300


#Download and install PgAdmin for your OS so you can interact with the database:  https://www.pgadmin.org/download/

#Run pgAdmin and right click on Servers --> Register 
General Tab:  
Name = RENTSTUFF_API

Connection Tab:
Host: localhost
Port: 5432
Maintenance DB: postgres
username: kong
password: kong

Save password: slide over to true

#Save and Close the popup.
#Expand the Kong entry under servers

#Right Click on Kong under the expanded Kong and choose "Query Tool"

#In the window that pops open paste the following

create database rentstuff;
'password123';
grant all privileges on database rentstuff to rentstuff;

#Highlight each line above in the query tool and click the plat button to execute the line.   

#Now clone the project from github

cd workspaces/rentstuff_api
git clone https://github.com/barryrknapp/rentstuff_api.git

#You should now have a directory workspaces/rentstuff_api/rentstuff_api.   (The project/github code is in the lower one and eclipse will put metadata about the project in the upper rentstuff_api)

#Install lombok for your eclipse installation by downloading this jar: https://search.maven.org/remotecontent?filepath=org/projectlombok/lombok/1.18.4/lombok-1.18.4.jar

#In a terminal navidate to where you have downloaded the jar file and run the following to install lombok.  It will try to find your eclipse installation automatically

 java -jar lombok-1.18.4.jar


# Now you can open eclipse and point to workspaces/rentstuff_api

# In eclipse - Click the "File" menu --> Import
# Type maven project to select it
# Browse to workspaces/rentstuff_api/rentstuff_api and select the project to import


# Create a Debug config to run the project by clicking the menu item "Run" --> "Debug Configuration"
Project  rentstuff_api
Main Class com.rentstuff.Application
# In the Debug Configuration popup click the "Environment" tab and add a new one with the following 2 variable and value entries:
DB_PASSWORD password123
DB_HOST localhost



# Now you can run the application for the first time and it will connect to the database and automatically create the tables.  

Open a browser to http://localhost:8081/api/swagger-ui/index.html
or http://localhost:8081/rentstuff/swagger-ui/index.html



# Now that the tables are created you can go back into pgadmin and expand the Kong -- RENTSTUFF_API database 
# Right click on RENTSTUFF_API and click "Query Tool" 

Paste the following into that window to insert the default configs.  The seeds are test seeds.

insert into config (ID, KEY, VALUE) values (1,"test.config","blah");


# Restart the application






