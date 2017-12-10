package databaseManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

public class Functions implements TableInterface {
	private String tableName;
	private String dataBaseName;
	private int RecordsNum = 0;// Of all table
	private ArrayList<String> columnsNames; /* of size = ColumnsNo */
	private ArrayList<String> SelectColumnsNames;
	private ArrayList<String> dataType;
	private ArrayList<ArrayList<String>> selectedTable;
	private ArrayList<ArrayList<String>> TotalTable; /*
														 * Each arrayList is an
														 * element of the 2D
														 * ArrayList and the
														 * arrayList of objects
														 * represents a column
														 * The ArrayList is of
														 * type Object as it can
														 * be of type(Integer)
														 * or type (String)
														 */
    //Constructor used in the table creation
	public Functions(String tableName, ArrayList<String> columnsNames, ArrayList<String> dataType,String dataBaseName) { 
		this.tableName = tableName;
		this.columnsNames = columnsNames;
		this.dataBaseName=dataBaseName;
		this.dataType = dataType;
		this.selectedTable = new ArrayList<>();
		this.SelectColumnsNames = new ArrayList<>();
		this.TotalTable = new ArrayList<>();
		for(int i=0; i<columnsNames.size(); i++){
			ArrayList<String> temp = new ArrayList<>();
			TotalTable.add(temp);
		}//for
		
	}/* Constructor */

	public ArrayList<String> getSelectColumnsNames() {
		return SelectColumnsNames;
	}

	// Special constructor for XML parsing
	public Functions(String tableName, ArrayList<ArrayList<String>> TotalTable, ArrayList<String> columnsNames,
			ArrayList<String> dataType,String dataBaseName) {
		this.tableName = tableName;
		this.columnsNames = columnsNames;
		this.dataType = dataType;
		this.dataBaseName = dataBaseName;
		this.selectedTable = new ArrayList<>();
		this.TotalTable = TotalTable;
		this.RecordsNum = TotalTable.get(0).size();
		// this.selectedTable = TotalTable;
		
		
		this.SelectColumnsNames = new ArrayList<>();
		
		
		// XML file creation in database directory
	}/* Constructor */

	

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public int getRecordsNum() {
		return RecordsNum;
	}

	public ArrayList<String> getColumnsNames() {
		return columnsNames;
	}

	public ArrayList<String> getDataType() {
		return dataType;
	}

	public ArrayList<ArrayList<String>> getTotalTable() {
		return TotalTable;
	}

	public static void createDatabase(String databaseName) {
		// TODO Auto-generated method stub
		File dir = new File(databaseName);
		dir.mkdir();
	}

	@Override
	public void createTable(String databaseName) {
		// TODO Auto-generated method stub
		File f = new File(databaseName);
		if (f.exists() && f.isDirectory()) {
			String fileName = tableName + ".xml";
			String absoluteFilePath = databaseName + File.separator + fileName;
			try {
				File file = new File(absoluteFilePath);
				if (!file.exists()) {
					file.createNewFile();
					System.out.println("File is created!");
				} else {
					System.out.println("File already exists!");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void dropDatabase(String databaseName) {
		// TODO Auto-generated method stub
		File f = new File(databaseName);
		if (f.exists() && f.isDirectory()) {
			String[] entries = f.list();
			for (String s : entries) {
				File currentFile = new File(f.getPath(), s);
				currentFile.delete();
			}
			f.delete();
		} else {
			System.out.println("database not found");
		}
	}

	public static void dropTable(String databaseName, String tableName) {
		// TODO Auto-generated method stub
		File f1 = new File(databaseName);
		if (f1.exists() && f1.isDirectory()) {
			String fileName = tableName;
			String absoluteFilePath = databaseName + File.separator + fileName + ".xml";
			File f2 = new File(absoluteFilePath);
			if (f2.exists()) {
				f2.delete();
			} else {
				try {
					throw new FileNotFoundException();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("database not found");
		}
	}

	public static boolean exists(String databaseName, String tableName)
			throws FileSystemNotFoundException, ParserConfigurationException, SAXException, IOException {

		File f1 = new File(databaseName);
		if (f1.exists() && f1.isDirectory()) {
			String fileName = tableName;
			String absoluteFilePath = databaseName + File.separator + fileName;
			File f2 = new File(absoluteFilePath + ".xml");
			File f3 = new File(absoluteFilePath + ".dtd");

			if (f2.exists() && f3.exists()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Main.TableInterfce#update(java.lang.String, java.lang.String,
	 * java.util.ArrayList, java.util.ArrayList, java.lang.String)
	 */
	@Override
	public void update(String conditionSymbol, String guideColumn, String guideValue,
			ArrayList<String> InputcolumnNames, ArrayList<String> InputValues, String WhereInsert) throws ParserConfigurationException, SAXException, IOException {
		switch (WhereInsert) {
		case "WHERE":
			// Searching for the required record in the table
			
			whereCase(conditionSymbol,guideColumn, guideValue,InputcolumnNames, InputValues);

			break;
		case "NOTWHERE":
			NotwhereCase(InputcolumnNames, InputValues);
			break;
		case "INSERT":

			whereCaseInsert(RecordsNum, InputcolumnNames, InputValues);
			
			break;
		}/* Switch */

		try {
			XmlHandler.save(this,dataBaseName);
		} catch (XMLStreamException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}/* Update_where Method */

	/**
	 * This method is used inside UPDATE method to update the cells mentioned to
	 * be update in WHERE case
	 * 
	 * @param guideRecordIndex
	 *            : The index of the record having the cells wanted to be
	 *            updated
	 * @param InputcolumnNames
	 *            :ArrayList which contains the specific columns corresponding
	 *            to the updated values. Hint: it has a null value when INSERT
	 *            without Columns is called
	 * @param InputValues
	 *            : contains the new values
	 */
	private void whereCase(String conditionSym, String guideColumn,String guideValue,ArrayList<String> InputcolumnNames, ArrayList<String> InputValues) {
		ArrayList<Integer> equal = new ArrayList<>(); 
		switch (conditionSym){
		    case "=":
		         
		        equal = getValidRecordsEqual(guideColumn, guideValue);
		    	break;
		    case ">":
		    	 
		        equal = getValidRecordsBigger(guideColumn, guideValue);
		    break;
		    case "<":
		    	//ArrayList<Integer> smaller = new ArrayList<>(); 
		        equal = getValidRecordsSmaller(guideColumn, guideValue);
		    break;
		    }//
		
		
			for (int i = 0; i < InputcolumnNames.size(); i++) {
				int index = getColumnIndexSelect(InputcolumnNames.get(i));
				for(int j=0;j<equal.size();j++){
				  TotalTable.get(index).add(equal.get(j), InputValues.get(i)); // updating
																					// a
																					// cell
				  TotalTable.get(index).remove(equal.get(j) + 1);
				}
			} /* for */
		
		
	}/* method */

	private void whereCaseInsert(int guideRecordIndex, ArrayList<String> InputcolumnNames,
			ArrayList<String> InputValues) {
		if (InputcolumnNames != null) {
			if (!areValidTypes(InputValues, InputcolumnNames)) {
				throw new RuntimeException();
			} //
			for (int i = 0; i < InputcolumnNames.size(); i++) {
				int index = getColumnIndexSelect(InputcolumnNames.get(i));// Searching
																			// in
																			// TotalTable
				TotalTable.get(index).add(guideRecordIndex, InputValues.get(i)); // updating
				TotalTable.get(index).remove(guideRecordIndex+1);																	// a
																					// cell
			} /* for */
		} /* if */
		else {
			// The case of inserting without mentioning columns names
			if (!areValidTypesWithoutColumns(InputValues)) {
				throw new RuntimeException();
			} //

			for (int i = 0; i < InputValues.size(); i++) {

				TotalTable.get(i).add(guideRecordIndex, InputValues.get(i)); // updating
				TotalTable.get(i).remove(guideRecordIndex+1);																// a
																				// cell
			} /* for */
		} /* else */
	}/* method */

	private boolean areValidTypesWithoutColumns(ArrayList<String> inputValues) {
		if (inputValues.size() != dataType.size()) {
			return false;
		} //
		ArrayList<String> dataTypes2 = new ArrayList<>();

		for (int i = 0; i < inputValues.size(); i++) {
			try {
				Integer.parseInt(inputValues.get(i));
				dataTypes2.add("int");
			} catch (Exception e) {
				dataTypes2.add("String");
			}

		} // for
		for (int i = 0; i < dataType.size(); i++) {
			if (!Objects.equals(dataType.get(i), dataTypes2.get(i))) {
				return false;
			}

		} // for
		return true;

	}

	private boolean areValidTypes(ArrayList<String> inputValues, ArrayList<String> InputcolumnNames) {

		ArrayList<String> dataTypes2 = new ArrayList<>();

		for (int i = 0; i < inputValues.size(); i++) {
			try {
				Integer.parseInt(inputValues.get(i));
				dataTypes2.add("int");
			} catch (Exception e) {
				dataTypes2.add("String");
			}

		} // for
		for (int i = 0; i < dataTypes2.size(); i++) {
			int index = getColumnIndexSelect(InputcolumnNames.get(i));
			if (!Objects.equals(dataType.get(index), dataTypes2.get(i))) {
				return false;
			}

		} // for
		return true;

	}
	
	private String type(String value){
		try {
			Integer.parseInt(value);
			return "int";
		}
		catch (Exception e) {
			return "string";
		}//
	}//

	/**
	 * This method is used inside UPDATE method to update the cells mentioned to
	 * be update in (not) WHERE case
	 * 
	 * @param InputcolumnNames
	 *            :ArrayList which contains the specific columns corresponding
	 *            to the updated values
	 * @param InputValues
	 *            : contains the new values
	 */
	private void NotwhereCase(ArrayList<String> InputcolumnNames, ArrayList<String> InputValues) {
		for (int i = 0; i < InputcolumnNames.size(); i++) {
			int index = getColumnIndexSelect(InputcolumnNames.get(i));
			int RecordsNum = TotalTable.get(index).size();
			TotalTable.get(index).clear();
			for (int j = 0; j < RecordsNum; j++) {
				TotalTable.get(index).add(j, InputValues.get(i)); // updating a
																	// cell
			}
		} /* for */
	}/* method */

	private int getColumnIndex(String guideColumn) {
		boolean notFound = true;
		int index = 0;
		for (index = 0; index < SelectColumnsNames.size(); index++) {
			if (Objects.equals(SelectColumnsNames.get(index), guideColumn)) {
				notFound = false;
				break;
			} /* if */
		} /* for */
		if (notFound) {
			return index = SelectColumnsNames.size() + 1; // to indicate that it
															// is not found
		} /* if */
		else
			return index;
	}/* getGuideColumnIndex method */

	/**
	 * This method is used inside UPDATE method to find a column index of the
	 * corresponding input String
	 * 
	 * @param guideColumn
	 *            : Name of the column which we want to have its index.
	 * @return : index of the column of the corresponding input name.
	 */
	private int getColumnIndexSelect(String guideColumn) {
		boolean notFound = true;
		int index = 0;
		for (index = 0; index < columnsNames.size(); index++) {
			if (Objects.equals(columnsNames.get(index), guideColumn)) {
				notFound = false;
				break;
			} /* if */
		} /* for */
		if (notFound) {
			return index = columnsNames.size() + 1; // to indicate that it is
													// not found
		} /* if */
		else
			return index;
	}/* getGuideColumnIndex method */

	/**
	 * This method is used inside UPDATE method to find the record index of the
	 * corresponding input String in the specified input column
	 * 
	 * @param guideValue
	 *            : value of the cell mentioned after WHERE clause
	 * @param ColumnIndex:
	 *            the column corresponding to the guideValue
	 * @return index of the record of the guideValue
	 */
	private int getGuideValueRecord(String guideValue, int ColumnIndex) {
		boolean notFound = true;
		int index;
		for (index = 0; index < selectedTable.get(ColumnIndex).size(); index++) {
			String s = selectedTable.get(ColumnIndex).get(index).toString();
			if (Objects.equals(s, guideValue)) {
				notFound = false;
				break;
			} /* if */
		} /* for */
		if (notFound) {
			return index = selectedTable.get(ColumnIndex).size() + 1; // to
																		// indicate
																		// that
																		// it is
																		// not
																		// found
		} /* if */
		else
			return index;

	}/* getGuideValueRecord */

	private int getGuideValueRecordSelect(String guideValue, int ColumnIndex) {
		boolean notFound = true;
		int index;
		for (index = 0; index < TotalTable.get(ColumnIndex).size(); index++) {
			String s = TotalTable.get(ColumnIndex).get(index).toString();
			if (Objects.equals(s, guideValue)) {
				notFound = false;
				break;
			} /* if */
		} /* for */
		if (notFound) {
			return index = TotalTable.get(ColumnIndex).size() + 1; // to
																	// indicate
																	// that it
																	// is not
																	// found
		} /* if */
		else
			return index;

	}/* getGuideValueRecord */

	/*
	 * (non-Javadoc)
	 * 
	 * @see Main.TableInterfce#Insert(java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	public void Insert(ArrayList<String> InputcolumnNames, ArrayList<String> InputValues) throws ParserConfigurationException, SAXException, IOException {
		addNewEmptyRecord();
		update(null, null, null, InputcolumnNames, InputValues, "INSERT");// Having
																			// a
		
		// case																// to
																			// handle
																			// insert
																			// case
		RecordsNum++;
		
		XmlHandler.deleteDtd(this.getTableName(), dataBaseName);
        XmlHandler.createDtd(this.getColumnsNames(), this.getTableName(), dataBaseName, RecordsNum);	

		try {
			XmlHandler.save(this,dataBaseName);
		} catch (XMLStreamException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}/* Insert method */
	private void addNewEmptyRecord(){
		for(ArrayList<String> s:TotalTable){
			s.add("null");
		}//for
	}//method

	@Override
	public void SelectAllWhere(String columnName, String conditionSymbol, String value) {
		switch (conditionSymbol) {
		case "=":
			equalSelectAll(columnName, value);
			break;
		case ">":
			biggerThanSelectAll(columnName, value);
			break;
		case "<":
			smallerThanSelectAll(columnName, value);
			break;
		}// Switch

	}// method

	@Override
	public void SelectAll() {
		selectedTable.clear();
		SelectColumnsNames.clear();
		selectedTable = TotalTable;
		SelectColumnsNames = columnsNames;

	}// method

	@Override
	public void SelectSpecified(ArrayList<String> InputcolumnNames) {
		selectedTable.clear();
		SelectColumnsNames.clear();
		for (int i = 0; i < InputcolumnNames.size(); i++) {
			for (int j = 0; j < columnsNames.size(); j++) {
				if (Objects.equals(InputcolumnNames.get(i), columnsNames.get(j))) {
					selectedTable.add(TotalTable.get(j));
				} // if
			} // for
		} // for
		SelectColumnsNames = InputcolumnNames;

	}/* SelectMethod */

	@Override
	public void SelectSpecifiedWhere(ArrayList<String> InputcolumnNames, String columnName, String conditionSymbol,
			String value) {
		switch (conditionSymbol) {
		case "=":
			equalSelect(columnName, value, InputcolumnNames);
			break;
		case ">":
			biggerThanSelect(columnName, value, InputcolumnNames);
			break;
		case "<":
			smallerThanSelect(columnName, value, InputcolumnNames);
			break;
		}// Switch
		SelectColumnsNames = InputcolumnNames;
	}/* SelectMethod */

	private void equalSelectAll(String guideColumn, String guideValue) {
		SelectColumnsNames = columnsNames;
		selectedTable.clear();
		
		ArrayList<Integer> ValidRecords = getValidRecordsEqual(guideColumn,guideValue);
		for (int i = 0; i < columnsNames.size(); i++) {
			int index = getColumnIndexSelect(columnsNames.get(i));
			// Loop for all valid records
			ArrayList<String> col = new ArrayList<>();
			for (int j = 0; j < ValidRecords.size(); j++) {
				String cell = TotalTable.get(index).get(ValidRecords.get(j));
				col.add(cell);
			} // for
			selectedTable.add(col);

		} /* for */

	}// method

	public ArrayList<ArrayList<String>> getSelectedTable() {
		return selectedTable;
	}

	private ArrayList<Integer> getValidRecordsEqual(String guideColumn, String guideValue) {
		String type = type(guideValue);
		int guideColumnIndex = getColumnIndexSelect(guideColumn);
		
		
		ArrayList<Integer> ValidRecords = new ArrayList<>();
		switch (type){
		case "int":
			    int refereceValue = Integer.parseInt(guideValue);
				for (int i = 0; i < RecordsNum; i++) {
					int Cvalue = Integer.parseInt(TotalTable.get(guideColumnIndex).get(i));
					if (Cvalue == refereceValue) {
						ValidRecords.add(i);
					} // if
				} // for
			break;
		case "string":
			
			for (int i = 0; i < RecordsNum; i++) {
				String Cvalue = TotalTable.get(guideColumnIndex).get(i);
				if (Cvalue.toUpperCase().compareTo(guideValue.toUpperCase())==0) {
					ValidRecords.add(i);
				} // if
			} // for
			break;
		}//switch
		return ValidRecords;
	}// method

	private ArrayList<Integer> getValidRecordsBigger(String guideColumn, String guideValue) {
		String type = type(guideValue);
		int guideColumnIndex = getColumnIndexSelect(guideColumn);
		
		
		ArrayList<Integer> ValidRecords = new ArrayList<>();
		switch (type){
		case "int":
			    int refereceValue = Integer.parseInt(guideValue);
				for (int i = 0; i < RecordsNum; i++) {
					int Cvalue = Integer.parseInt(TotalTable.get(guideColumnIndex).get(i));
					if (Cvalue > refereceValue) {
						ValidRecords.add(i);
					} // if
				} // for
			break;
		case "string":
			
			for (int i = 0; i < RecordsNum; i++) {
				String Cvalue = TotalTable.get(guideColumnIndex).get(i);
				if (Cvalue.toUpperCase().compareTo(guideValue.toUpperCase())>0) {
					ValidRecords.add(i);
				} // if
			} // for
			break;
		}//switch
		return ValidRecords;
	}// method

	private ArrayList<Integer> getValidRecordsSmaller(String guideColumn, String guideValue) {
		String type = type(guideValue);
		int guideColumnIndex = getColumnIndexSelect(guideColumn);
		
		
		ArrayList<Integer> ValidRecords = new ArrayList<>();
		switch (type){
		case "int":
			    int refereceValue = Integer.parseInt(guideValue);
				for (int i = 0; i < RecordsNum; i++) {
					int Cvalue = Integer.parseInt(TotalTable.get(guideColumnIndex).get(i));
					if (Cvalue < refereceValue) {
						ValidRecords.add(i);
					} // if
				} // for
			break;
		case "string":
			
			for (int i = 0; i < RecordsNum; i++) {
				String Cvalue = TotalTable.get(guideColumnIndex).get(i);
				if (Cvalue.toUpperCase().compareTo(guideValue.toUpperCase())<0) {
					ValidRecords.add(i);
				} // if
			} // for
			break;
		}//switch
		return ValidRecords;
	}// method

	private void equalSelect(String guideColumn, String guideValue, ArrayList<String> InputcolumnNames) {
		SelectColumnsNames = InputcolumnNames;
		selectedTable.clear();	
		
		
		ArrayList<Integer> ValidRecords = getValidRecordsEqual(guideColumn,guideValue);
		
		for (int i = 0; i < InputcolumnNames.size(); i++) {
			int index = getColumnIndexSelect(InputcolumnNames.get(i));
			// Loop for all valid records
			ArrayList<String> col = new ArrayList<>();
			for (int j = 0; j < ValidRecords.size(); j++) {
				String cell = TotalTable.get(index).get(ValidRecords.get(j));
				TotalTable.get(index).add(ValidRecords.get(j),cell);
				TotalTable.get(index).remove(ValidRecords.get(j)+1);
				
				col.add(cell);
			} // for

			selectedTable.add(col);

		} /* for */

	}// method

	private void biggerThanSelect(String guideColumn, String guideValue, ArrayList<String> InputcolumnNames) {
		SelectColumnsNames = InputcolumnNames;
		selectedTable.clear();
		int guideColumnIndex = getColumnIndexSelect(guideColumn);
		if (!Objects.equals("int", dataType.get(guideColumnIndex))) {
			return; // Not comparable
		} /**/
		
		
		ArrayList<Integer> ValidRecords = getValidRecordsBigger(guideColumn,guideValue);
		

		for (int i = 0; i < InputcolumnNames.size(); i++) {
			int index = getColumnIndexSelect(InputcolumnNames.get(i));
			ArrayList<String> col = new ArrayList<>();
			for (int j = 0; j < ValidRecords.size(); j++) {
				String cell = TotalTable.get(index).get(ValidRecords.get(j));
				col.add(cell);
			} // for
			selectedTable.add(col);
		} /* for */
	}// method

	private void biggerThanSelectAll(String guideColumn, String guideValue) {
		SelectColumnsNames = columnsNames;
		selectedTable.clear();
		
		
		ArrayList<Integer> ValidRecords = getValidRecordsBigger(guideColumn,guideValue);

		for (int i = 0; i < columnsNames.size(); i++) {
			ArrayList<String> col = new ArrayList<>();
			for (int j = 0; j < ValidRecords.size(); j++) {
				String cell = TotalTable.get(i).get(ValidRecords.get(j));
				col.add(cell);
			} // for
			selectedTable.add(col);
		} /* for */
	}// method

	private void smallerThanSelect(String guideColumn, String guideValue, ArrayList<String> InputcolumnNames) {
		SelectColumnsNames = InputcolumnNames;
		selectedTable.clear();
		
		
		
		ArrayList<Integer> ValidRecords = getValidRecordsSmaller(guideColumn,guideValue);

		for (int i = 0; i < InputcolumnNames.size(); i++) {
			int index = getColumnIndexSelect(InputcolumnNames.get(i));
			ArrayList<String> col = new ArrayList<>();
			for (int j = 0; j < ValidRecords.size(); j++) {
				String cell = TotalTable.get(index).get(ValidRecords.get(j));
				col.add(cell);
			} // for
			selectedTable.add(col);
		} /* for */
	}// method

	private void smallerThanSelectAll(String guideColumn, String guideValue) {
		SelectColumnsNames = columnsNames;
		selectedTable.clear();
		int guideColumnIndex = getColumnIndexSelect(guideColumn);
		if (!Objects.equals("int", dataType.get(guideColumnIndex))) {
			return; // Not comparable
		} /**/
		
		ArrayList<Integer> ValidRecords = getValidRecordsSmaller(guideColumn,guideValue);

		for (int i = 0; i < columnsNames.size(); i++) {
			ArrayList<String> col = new ArrayList<>();
			for (int j = 0; j < ValidRecords.size(); j++) {
				String cell = TotalTable.get(i).get(ValidRecords.get(j));
				col.add(cell);
			} // for
			selectedTable.add(col);
		} /* for */
	}// method

	@Override
	public void delete() throws ParserConfigurationException {
		TotalTable.clear();			
		RecordsNum=0;

		try {
			XmlHandler.save(this,dataBaseName);
		} catch (XMLStreamException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}/* method */

	@Override
	public void deleteWhere(String guideCol, String condition, String guideValue) throws ParserConfigurationException {
		switch (condition) {
		case "=":
			ArrayList<Integer> records = getValidRecordsEqual(guideCol, guideValue);
			deleteCondition(records);
			break;
		case ">":
			ArrayList<Integer> records2 = getValidRecordsBigger(guideCol, guideValue);
			deleteCondition(records2);
			break;
		case "<":
			ArrayList<Integer> records3 = getValidRecordsSmaller(guideCol, guideValue);
			deleteCondition(records3);
			break;
		}// switch
		
		RecordsNum = TotalTable.get(0).size();
		
		try {
			XmlHandler.save(this,dataBaseName);
		} catch (XMLStreamException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// method

	private void deleteCondition(ArrayList<Integer> deletedRecords) {
		for (int i = 0; i < deletedRecords.size(); i++) {
			for (int j = 0; j < TotalTable.size(); j++) {
				TotalTable.get(j).add((int) deletedRecords.get(i), "DELETED");
				TotalTable.get(j).remove((int) deletedRecords.get(i) + 1);
			} // for
		} // for
		smashing();
		RecordsNum =RecordsNum - deletedRecords.size();

	}// method

	private void smashing() {

		for (int i = 0; i < TotalTable.size(); i++) {
			TotalTable.get(i).removeAll(Collections.singleton("DELETED"));
		} // for
	}// method
	
	public void SelectDistinctionWhere(ArrayList<String> InputcolumnNames, String columnName, String conditionSymbol,String value) {
		this.SelectSpecifiedWhere(InputcolumnNames, columnName, conditionSymbol, value);
		distinction(selectedTable);
	}//method
	
	public void SelectDistinction(ArrayList<String> InputcolumnNames){
		SelectSpecified(InputcolumnNames);
		distinction(selectedTable);
	}//method
	
	private void distinction(ArrayList<ArrayList<String>> selectedTable){
		int RowsNum = 0;
		if(selectedTable.size() != 0){
			RowsNum = selectedTable.get(0).size();			
		
		ArrayList<Integer> repeatedRows = new ArrayList<>(); 
		for(int i=0; i<RowsNum; i++){
			for(int j = i+1; j<RowsNum; j++){
				if(isRepeatedRow(i, j, selectedTable)){
					repeatedRows.add(j);
				}//if
			}//for
		}//for
		removingRepeatedRows(selectedTable, repeatedRows);
		}//if
	}//methods
	
	private boolean isRepeatedRow(int rowIndex1,int rowIndex2,ArrayList<ArrayList<String>> selectedTable){
		ArrayList<String> row1 = new ArrayList<>();
		ArrayList<String> row2 = new ArrayList<>();
		int colsNum = selectedTable.size();
		for(int i=0; i<colsNum; i++){
			row1.add(selectedTable.get(i).get(rowIndex1));
			row2.add(selectedTable.get(i).get(rowIndex2));
		}//for
		
		for(int i=0; i<colsNum; i++){
			if(!Objects.equals(row1.get(i), row2.get(i))){
				return false;
			}//if
		}//for
		return true;
	}//methods
	
	private void removingRepeatedRows (ArrayList<ArrayList<String>> selectedTable,ArrayList<Integer> repeatedRows){
		Collections.sort(repeatedRows);//Ascending sorting		
		
		for(int i=0; i<repeatedRows.size();i++){
			for(int j=0; j<selectedTable.size();j++){
				selectedTable.get(j).remove((int)repeatedRows.get(i));
			}//for
			repeatedRows = decrementIndiciesOf(repeatedRows);
		}//for	
	}//method
	
	private ArrayList<Integer> decrementIndiciesOf(ArrayList<Integer> repeatedRows){
		ArrayList<Integer> result = new ArrayList<>();		
		for(Integer i: repeatedRows){
			result.add(i-1);
		}//for
		return result;
	}//method

}/* Table Class */
