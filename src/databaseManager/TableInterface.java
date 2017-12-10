package databaseManager;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public interface TableInterface {
   
	/**
	 * This method is called when the SQL TABLE statement is called
	 * @param databaseName
	 * @param tableName
	 */
	 void createTable(String databaseName);
	/**
	 * This method is called when the SQL UPDATE statement is called
	 * 
	 * @param guideColumn : which is mention after WHERE to find the specific record to be updated.HINT: It has a NULL value when the NOTWHERE clause is present.
	 * @param guideValue  :which is mention after WHERE to find the specific record to be updated.HINT: It has a NULL value when the NOTWHERE clause is present.
	 * @param InputcolumnNames :ArrayList which contains the specific columns corresponding to the updated values
	 * @param InputValues : contains the new values
	 * @param Where : If it is "WHERE" it will implement the update with taking WHERE clause in consideration if it is "NOTWHERE" will implement the update with taking WHERE 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	 public void update(String conditionSymbol, String guideColumn, String guideValue,ArrayList<String> InputcolumnNames, ArrayList<String> InputValues, String WhereInsert)throws ParserConfigurationException, SAXException, IOException;

	/**
	 * This method is called when the SQL INSERT statement is called 
	 * @param InputcolumnNames :ArrayList which contains the specific columns corresponding to the inserted values. Hint: Has NULL value when Columns names weren't mentioned
	 * @param InputValues : contains the inserted values
	 * @throws IOException 
	 * @throws SAXException 
	 */
	void Insert(ArrayList<String> InputcolumnNames, ArrayList<String> InputValues)throws ParserConfigurationException, SAXException, IOException ;/*Insert method*/

	/**
	 * select all table with where condition
	 * @param columnName
	 * @param conditionSymbol
	 * @param value
	 */
	public void SelectAllWhere(String columnName, String conditionSymbol, String value);
	/**
	 * selection of the whole table
	 */
	public void SelectAll() ;
	/**
	 * selection of specified columns
	 * @param InputcolumnNames
	 */
	public void SelectSpecified(ArrayList<String> InputcolumnNames) ;
	/**
	 * selection of specified columns with where condition
	 * @param InputcolumnNames
	 * @param columnName
	 * @param conditionSymbol
	 * @param value
	 */
	public void SelectSpecifiedWhere(ArrayList<String> InputcolumnNames, 
			String columnName, String conditionSymbol,String value);
	/**
	 * deletion
	 */
	void delete()throws ParserConfigurationException ;
	/**
	 * deletion with where keyword
	 * @param guideCol
	 * @param condition
	 * @param guideValue
	 * @throws ParserConfigurationException 
	 */
	public void deleteWhere(String guideCol, String condition, String guideValue) throws ParserConfigurationException;

}
