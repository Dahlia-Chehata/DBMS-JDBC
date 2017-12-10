package Test;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import databaseManager.Functions;
import databaseManager.StatementsParser;
import databaseManager.XmlHandler;


public class test {
	private StatementsParser statementsParser = StatementsParser.createObject();
	private databaseManager.TableInterface functions = new Functions(null, null, null, null);
	// private Functions func = new Functions(null, null, null, null);
	private ArrayList<String> ColNames = new ArrayList<>();
	private ArrayList<String> Types = new ArrayList<>();
	private ArrayList<String> Col1 = new ArrayList<>();
	private ArrayList<ArrayList<String>> Table = new ArrayList<ArrayList<String>>();
	private ArrayList<String> InputCols = new ArrayList<>();
	private ArrayList<String> Values = new ArrayList<>();
	Functions test = new Functions(null, Table, ColNames, Types, null);

	@org.junit.Test
	public void test1() throws Exception {
		statementsParser.enterStatement("SELECT * FROM table;");
	}

	@org.junit.Test
	public void test2() throws Exception {
		statementsParser.enterStatement("SELECT * FROM CUSTOMERS WHERE column1 = val1;");
	}

	@org.junit.Test
	public void test3() throws Exception {
		statementsParser.enterStatement("SELECT *FROM 'DROP' WHERE column1>val1;");
	}

	@org.junit.Test
	public void test4() throws Exception {
		statementsParser.enterStatement("INSERT INTO table_Name VALUES (value1, value2 , value3, value4);");
	}

	@org.junit.Test
	public void test5() throws Exception {
		statementsParser
				.enterStatement("create Table cars (Car_Type String(size) , Year int(size), model string(size));");
	}

	@org.junit.Test
	public void test6() throws Exception {
		statementsParser.enterStatement("SELECT ID, NAME, SALARY from\n CUSTOMERS WHERE column1 = val1;");
	}

	@org.junit.Test
	public void test7() throws Exception {
		statementsParser.enterStatement(
				"INSERT INTO Customers (CustomerName, City, Country) VALUES ('Cardinal', 'Stavanger', 'Norway');");
	}

	@org.junit.Test
	public void test8() {
		functions.createTable("Name");
	}

	@org.junit.Test
	public void test9() {
		Functions.dropDatabase("Name");
	}

	@org.junit.Test
	public void test10() {
		Functions.dropTable("Name", "table");
	}

	@org.junit.Test
	public void test11() throws FileSystemNotFoundException, ParserConfigurationException, SAXException, IOException {
		Functions.exists("Name", "table");
	}

	@org.junit.Test
	public void test12() throws ParserConfigurationException, SAXException, IOException {
		ColNames.add("ID");
		Types.add("int");
		Col1.add("1");
		Col1.add("2");
		Col1.add("3");
		Table.add(Col1);
		InputCols.add("ID");
		Values.add("5");
		test.Insert(InputCols, Values);
	}

	@org.junit.Test
	public void test13() throws XMLStreamException, IOException, ParserConfigurationException {
		XmlHandler.save(test,null);

	}

	@org.junit.Test
	public void test14() throws Exception {
		statementsParser.enterStatement("CREATE TABLE DODO(PersonsID int,LastName varchar(255));");
	}

	@org.junit.Test
	public void test15() {
		Functions.createDatabase("Grades");
	}

}
