To switch the database that is being used, find the "DB_TYPE DatabaseType" variable in the four servlet classes, and change it to either ORACLE or MONGODB

---Oracle---
-Build script for Oracle is given as "oracleBuildScript.sql"

-Oracle connection can sometimes glitch when the database is under heavy load (lots of students using it). If you run the project and it doesn't seem to be running any queries it's because of this. To fix it, you'll need to re-run the build script, refresh your database connection, rebuild the project, and clear cache's. 

---Mongo---
-No build script was requested for Mongo. If you'd like to run the Mongo implementation you'll therefore need to recreate the Mongo collections seen in "s4203822 MONGO DEMO"

-To use the Mongo Compass, the connection should automatically establish with localhost. However you may need to use the following commands in command line:
	1) [CHANGE TO YOUR MONGO BIN DIRECTORY] cd C:\Program Files\MongoDB\Server\7.0\bin
	2) mongod