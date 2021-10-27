------------------------------------------------------------------------
This is the project README file. Here, you will find all the information
you need about this project and how to run it.
------------------------------------------------------------------------

PROJECT TITLE:		OurEdit_group_7


PURPOSE OF PROJECT:	Create and Implement a basic working Client-Server Application


VERSION or DATE:	Last Modified 07/05/2021


AUTHORS: 		Mohammad Mohammadieh, Anthony Sleilaty, Houssein El Fakih


USER INSTRUCTIONS FOR FIRST TIME USE:


Extract all the files to the location of your choice (for example desktop). You should find a folder named OurEdit_group_7.

Next, open an IDE supporting JAVA of your choice (for example ECLIPSE).

Go to File --> Import and search for import project (or import project from folder) then click on it.

You will be asked to enter the Location of your Folder.Press "Browse..." or "Directory". 

Browse in your explorer.Find the file, select it (folder OurEdit_group_7) and press finish (or done).

You shoud now see the name of your project in the browser inside your IDE.

Collapse it and double click the "work" source folder. Inside it press on the default package and all the classes will show up.

Make sure your JDBC is set to the link of the database eece350 (jdbc:mysql://localhost:3306/yourdatabasename) and the password in the dbURL String in the server to your own password and ping the connection to make sure it connects properly.

Execute these two statements in your database.

CREATE TABLE `docs_info` (
  `doc_name` varchar(25) NOT NULL,
  `num_users` int NOT NULL DEFAULT '0',
  `last_modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `num_users_using` int NOT NULL DEFAULT '0',
  `content` text NOT NULL,
  PRIMARY KEY (`doc_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `users` (
  `ID` mediumint NOT NULL AUTO_INCREMENT,
  `doc_name` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`,`doc_name`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

Run the TCPServer class in ECLIPSE.

Run as many GUI's as you want. Log in on each one. Enjoy the app!