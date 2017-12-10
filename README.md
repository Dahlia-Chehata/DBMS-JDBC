# DBMS-JDBC

1. Application Overview
	A XML based SQL- like database management system (DBMS) application
	created by Java programming language, our DBMS supports some simple SQL
	commands as:
	- Use database command (as in mySQL).
	- Creating a database.
	- Creating a table inside a certain database.
	- Inserting data in a table.
	- Updating some data in a table.
	- Deleting data from a table.
	- Selecting (viewing) specified and general data from a table.
	- Dropping a database.
	- Dropping a table.
     • A SQL-like interface that resembles that of real SQL DBMS.
     • Files of .xml and .dtd are used to save data and information about it.

2. Application Design
	The program is divided to 2 main packages, one is the database and tables
	functions were each stage from entering the SQL statement till its execution has its one
	class - following OOP principals in breaking the big task to small independent tasks - ,
	this classes will be explained briefly in the next few lines. The other package is the
	package responsible for drawing table neatly, this package was downloaded from
	GitHub.
	
        SQL Statements Parser Class
	It class follows the Singleton Design Pattern since we only need to create a
	single object to be used in out program and to control synchronization if threads are
	used in order to prevent statements execution conflict.
	The statement is entered to this class where it checks that the statements a right
	SQL statement with right syntax and input and if so the suitable function and the SQL
	command goes to its next step.

	Functions Class
	It also deals with all SQL orders from creating the database directory and table
	files (.xml and .dtd) and their equivalent objects and data-structures till dropping a
	database or table in a certain database, passing through other statements - stated in the
	project statement - from inserting into table, updating an entry, deleting a table or an
	entry ... etc.
	Each single command has its own method to approach final paces in executing
	the entered command.

	XML Handler Class
	Final steps are put to have a well built database management system this class is
	used to handle to / from interaction with table-containing files, as each table has 2
	files .xml and .dtd where:
		1. Each table (columns and rows) are saved in a well formatted human -
		readable .xml file with its suitable tags.
		2. On loading, load function puts the table’s data in a suitable data-structure to
		be ready for performing any modifications .
		3. A .dtd file contains the general overview for each table.
		For validating that a .dtd file matches its .xml file for a certain table, a validation method
		that is implemented for such case to enable tracing an error if it exists.

