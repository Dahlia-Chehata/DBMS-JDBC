package databaseManager;


import java.util.Scanner;

public class TryParsing {
//	System.out.println("enter desired SQL statement");

	public static void main(String[] args) throws Exception {
		StatementsParser parser = StatementsParser.createObject();
		while(true){
			System.out.println();
			String statement = "";
			do {
				Scanner scan = new Scanner(System.in);
				System.out.print("sql> " );
				statement = statement + "\n" + scan.nextLine();
			} while (!statement.contains(";"));
			System.out.println();
			parser.enterStatement(statement);
		}
		
//		parser.enterStatement("CREATE TABLE DODO"
//				+"("
//				+"PersonsID int,"
//				+"LastName varchar(255)"
//				+");" );
		
//		CREATE TABLE Persons(PersonID int,LastName varchar(255),FirstName varchar(255), Address varchar(255),City varchar(255));

//		CREATE TABLE Persons
//		(
//		City varchar( 255 )
//		);
		
//		sql> create table fadyy
//		sql> (
//		sql> name int,
//		sql> name2 int
//		sql> );
		
		// parser.enterStatement("CREATE TABLE _a$bcdef0ghijklmnopqrstuvwxyz;");
		// parser.enterStatement("SELECT * FROM table;");
		// parser.enterStatement("fady create;");
		// parser.enterStatement("create Table cars (Car_Type String(size) ,
		// Year int(size), model string(size));");
		// parser.enterStatement("DELETE FROM cars WHERE Car_Type=Fiat;");
		// parser.enterStatement("INSERT INTO table_Name Values ( value1,value2
		// ,value3 ,value4 ) ;");
		// parser.enterStatement("UPDATE table_name SET column1=\"value1\",
		// column2='value2',column3=\"value3\", column4='value4', WHERE
		// some_column=some_value;");
		// parser.enterStatement("DROP TABLE 'te5a'");
		// parser.enterStatement("INSERT INTO table_Name VALUES (value1, value2
		// , value3, value4);");
		// parser.enterStatement("INSERT INTO Customers (CustomerName, City,
		// Country) VALUES ('Cardinal', 'Stavanger', 'Norway');");
		// parser.enterStatement("USE FNSY ;");
		// parser.enterStatement("SELECT *FROM 'DROP' WHERE column1>val1;");
		// parser.enterStatement("SELECT dodo,fady FROM FNSY ; ");
		// parser.enterStatement("SELECT * FROM CUSTOMERS ;");
		// parser.enterStatement("SELECT * FROM CUSTOMERS WHERE column1 =
		// val1;");
		// parser.enterStatement("SELECT ID, NAME, SALARY FROM CUSTOMERS dfdsfd
		// dff;");
		// parser.enterStatement("SELECT ID, NAME, SALARY from\n CUSTOMERS WHERE
		// column1 = val1;");
		// Ù�ÙŠ Ø§Ù„ sql Ø§Ù„Ø§ØµÙ„ÙŠ Ù…Ø´ Ø¨ÙŠØ­Ø·
		// spaces
		// Ù‚Ø¨Ù„ Ø§Ù„ = ÙˆØ¨Ø¹Ø¯Ù‡Ø§
		// Ø§Ø­Ù†Ø§ Ù„Ø§Ø²Ù… Ù†Ø­Ø·Ù‡Ø§ Ø¹Ø´Ø§Ù† ÙƒÙ„Ø§Ø³ Ø§Ù„ parser ÙŠØ´ØªØºÙ„ ØµØ­

	}

}
