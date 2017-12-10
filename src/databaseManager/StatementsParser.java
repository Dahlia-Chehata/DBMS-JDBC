package databaseManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

/**
 * This class parsers and conquers the SQL statements entered. To check if the
 * entered statement is true, and send the creation order to the creation class
 * or the operations to the operations class
 *
 * @author FNSY
 *
 */
public class StatementsParser {
	// private String statement;
	private static StatementsParser statementsParser = null;
	private String[] reservedWords;

	/**
	 * current database name
	 */
	private String dbName = null;

	public StatementsParser() {
		initializeReservedWords();
	}

	public static synchronized StatementsParser createObject() {
		if (statementsParser == null) {
			statementsParser = new StatementsParser();
		}
		return statementsParser;
	}

	public final void enterStatement(final String statement) throws Exception {
		String checkedStatement;
		checkedStatement = removeWhiteSpaces(statement);

		String upperCaseStatement = checkedStatement.toUpperCase();
		try {
			selectRightAction(upperCaseStatement.split(" "), checkedStatement);
		} catch (Exception x) {
			System.out.println("PLEASE ENTER A VALID STATEMENT");
		}
	}

	private void selectRightAction(String[] upperCaseStatement, String checkedStatement)
			throws ParserConfigurationException, SAXException, IOException, FileSystemNotFoundException,
			XMLStreamException, TransformerException {
		if (isValidStatement(checkedStatement))
			if (upperCaseStatement[0].equals("USE"))
				useStatement(checkedStatement.split(" ")); // done
			else if (upperCaseStatement[0].equals("CREATE"))
				creationStatement(checkedStatement); // done
			else if (upperCaseStatement[0].equals("DELETE"))
				deletionStatement(checkedStatement);
			else if (upperCaseStatement[0].equals("UPDATE"))
				updateStatement(checkedStatement);
			else if (upperCaseStatement[0].equals("DROP"))
				dropStatement(checkedStatement);
			else if (upperCaseStatement[0].equals("INSERT") && upperCaseStatement[1].equals("INTO"))
				insertionStatement(checkedStatement);
			else if (upperCaseStatement[0].equals("SELECT"))
				selectStatement(checkedStatement);
			else if (upperCaseStatement[0].equals("ALTER") && upperCaseStatement[1].equals("TABLE"))
				alterStatement(checkedStatement);
			else
				throw new RuntimeException();
	}

	/**
	 * This method checks if the SQL statement is written in right format. with
	 * no extra spaces and trailed by a semicolon
	 *
	 * @param statement
	 * @return
	 */

	private void useStatement(String[] splitted) { // done check
		splitted = filterSplittedArray(splitted);
		if (splitted.length == 2) {
			dbName = new String(splitted[1]);
		}
	}

	/**
	 * create statement
	 * 
	 * @param statement
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XMLStreamException
	 * @throws TransformerException
	 */
	private final void creationStatement(final String statement)
			throws ParserConfigurationException, SAXException, IOException, XMLStreamException, TransformerException {
		String restOfTheString = statement.substring(7, statement.length() - 1);
		try {
			creationOfDatabase(restOfTheString);

		} catch (RuntimeException x) {
			creationOfTable(restOfTheString);
		}

	}

	final void creationOfTable(String statement)
			throws ParserConfigurationException, SAXException, IOException, XMLStreamException, TransformerException { // done

		statement = replaceVarchar(statement);
		statement = removeWhiteSpaces(statement);
		String tableString = filterString(statement.substring(0, 5));
		if (tableString.compareToIgnoreCase("TABLE") == 0) {
			String[] tableDetails = statement.substring(6, statement.length()).split(" ");
			tableDetails = filterSplittedArrayForTableCreation(tableDetails);
			int length = tableDetails.length;
			if (isValidName(tableDetails[0]) && !isReservedWord(tableDetails[0])) {

				ArrayList<String> columnName = new ArrayList<>();
				ArrayList<String> dataType = new ArrayList<>();
				for (int i = 1; i < length; i++) {
					if (i % 2 == 1) {
						columnName.add(tableDetails[i]);
					} else {
						if (tableDetails[i].replace(",", "").equals("String")
								|| tableDetails[i].replace(",", "").equals("int")
								|| tableDetails[i].replace(",", "").equals("date")
								|| tableDetails[i].replace(",", "").equals("float")) {
							// edited in the JDBC version added new datatypes
							if (tableDetails[i].contains(",")) {
								dataType.add(filterString(tableDetails[i]));

							} else if (!tableDetails[i].contains(",")) {
								try {
									String checkLast = tableDetails[i + 1];
									throw new RuntimeException();
								} catch (ArrayIndexOutOfBoundsException x) {
									dataType.add(tableDetails[i]);
								}
							}
						} else
							throw new RuntimeException();
					}
				}
				if (validateEqualSize(columnName, dataType) && isValidNameInArrayList(columnName)) { // checking
																										// lengths
																									// and
																										// names
					Functions x = new Functions(tableDetails[0], columnName, dataType, dbName);
					x.createTable(dbName);
					XmlHandler.createDtd(columnName, tableDetails[0], dbName, 0);
					XmlHandler.save(x, dbName);
					List<List<String>> rowsList = Arrays.asList();
					TableFormatter.print(columnName, rowsList);
				}
			} else
				throw new RuntimeException();

		} else
			throw new RuntimeException();
	}

	private final void creationOfDatabase(final String statement) { // done
																	// check
		String databaseString = statement.substring(0, 8);
		if (databaseString.compareToIgnoreCase("DATABASE") == 0) {
			String databaseName = filterString(statement.substring(9, statement.length()));
			if (isValidName(databaseName) && !isReservedWord(databaseName)) {
				Functions.createDatabase(databaseName);
			}
		} else
			throw new RuntimeException();
	}

	// end of creation
	private void deletionStatement(String statement) {
		String deleteString = statement.substring(0, 11);
		if (deleteString.compareToIgnoreCase("DELETE FROM") == 0 && statement.toUpperCase().contains("WHERE")) {
			String restOfString = statement.substring(12, statement.length() - 1);
			String tableName, conditionSymbol, columnName, valueIndicatingRow;
			String[] splitted = restOfString.split(" ");
			tableName = splitted[0];
			for (int i = 0; i < splitted.length; i++) {
				if (splitted[i].compareToIgnoreCase("WHERE") == 0 && (splitted[i + 2].equals("=")
						|| splitted[i + 2].equals("<") || splitted[i + 2].equals(">"))) {
					columnName = splitted[i + 1];
					conditionSymbol = splitted[i + 2];
					valueIndicatingRow = splitted[i + 3];
					try {
						if (isValidName(tableName) && isValidName(columnName) && Functions.exists(dbName, tableName)) {
							Functions result;
							try {
								result = XmlHandler.load(tableName, dbName);
								result.deleteWhere(columnName, conditionSymbol, schrodingarString(valueIndicatingRow));
								TableFormatter.print(result.getColumnsNames(),
										TableFormatter.toList(result.getTotalTable()));
							} catch (FileNotFoundException | XMLStreamException e) {
								// TODO Auto-generated catch block
								throw new RuntimeException();
							}
						} else
							throw new RuntimeException();
					} catch (FileSystemNotFoundException | ParserConfigurationException | SAXException
							| IOException e) {
						throw new RuntimeException();
					}
					break;
				}
			}
		} else
			try {
				deleteAllTable(statement);
			} catch (FileSystemNotFoundException | ParserConfigurationException | SAXException | IOException e) {
				throw new RuntimeException();
			}
	}

	private void deleteAllTable(String statement)
			throws FileSystemNotFoundException, ParserConfigurationException, SAXException, IOException {
		String deleteAllString = statement.substring(0, 11);
		String deleteAllAstString = statement.substring(0, 13);
		String tableName;
		if (deleteAllString.compareToIgnoreCase("DELETE FROM") == 0) {
			tableName = statement.substring(12, statement.length() - 1);
			if (isValidName(tableName) && Functions.exists(dbName, tableName)) {
				Functions result = null;
				try {
					result = XmlHandler.load(tableName, dbName);
					result.delete();
					List<List<String>> rowsList = Arrays.asList();
					TableFormatter.print(result.getColumnsNames(), rowsList);
				} catch (FileNotFoundException | XMLStreamException e) {
					throw new RuntimeException();
				}

			} else
				throw new RuntimeException();

		} else if (deleteAllAstString.compareToIgnoreCase("DELETE * FROM") == 0) {
			tableName = statement.substring(14, statement.length() - 1);
			if (isValidName(tableName) && Functions.exists(dbName, tableName)) {
				Functions result = null;
				try {
					result = XmlHandler.load(tableName, dbName);
					result.delete();
					List<List<String>> rowsList = Arrays.asList();
					TableFormatter.print(result.getColumnsNames(), rowsList);
				} catch (FileNotFoundException | XMLStreamException e) {
					throw new RuntimeException();
				}

			} else
				throw new RuntimeException();
		}

	}
	// end of delete

	private void updateStatement(String statement) throws FileSystemNotFoundException, ParserConfigurationException,
			SAXException, IOException, XMLStreamException {
		String[] splitted = statement.substring(7, statement.length()).split(" ");
		splitted = filterSplittedArray(splitted);
		if (statement.toUpperCase().contains("SET") && statement.toUpperCase().contains("WHERE")) {
			containingWhereUpdateStatement(splitted);
		} else if (statement.toUpperCase().contains("SET") && !statement.toUpperCase().contains("WHERE")) {
			notContainingWhereUpdateStatement(splitted);
		}

	}

	private void containingWhereUpdateStatement(String[] splitted)
			throws FileSystemNotFoundException, ParserConfigurationException, SAXException, IOException {
		splitted = filterSplittedArray(splitted);
		String conditionSymbol = null;
		String tableName = splitted[0];
		if (splitted[1].compareToIgnoreCase("SET") == 0) {
			ArrayList<String> columnName = new ArrayList<>();
			ArrayList<String> values = new ArrayList<>();
			String guideColumn = null, guideValue = null;
			for (int i = 2; i < splitted.length; i++) {
				if (splitted[i].compareToIgnoreCase("WHERE") != 0) {
					if (i % 3 == 0)
						if (splitted[i].equals("=")) {
							columnName.add(splitted[i - 1]);
							if (splitted[i + 1].compareToIgnoreCase("WHERE") != 0)
								values.add(splitted[i + 1]);
						} else
							throw new RuntimeException();

				} else if (splitted[i + 2].equals("=") || splitted[i + 2].equals("<") || splitted[i + 2].equals(">")) {

					guideColumn = splitted[i + 1];
					conditionSymbol = splitted[i + 2];
					guideValue = splitted[i + 3];
					break;
				} else {
					throw new RuntimeException();
				}
			}
			values = makeValuesValid(values);
			System.out.println(values.get(0));
			guideValue = schrodingarString(guideValue);
			if (validateEqualSize(columnName, values) && isValidName(tableName) && isValidName(guideColumn)
					&& isValidName(guideValue) && isValidNameInArrayList(columnName)
					&& Functions.exists(dbName, tableName)) {
				Functions result = null;
				try {
					result = XmlHandler.load(tableName, dbName);
					result.update(conditionSymbol, guideColumn, guideValue, columnName, values, "WHERE");
					TableFormatter.print(result.getColumnsNames(), TableFormatter.toList(result.getTotalTable()));
				} catch (FileNotFoundException | XMLStreamException e) {
					throw new RuntimeException();

				}

			} else
				throw new RuntimeException();

		} else
			throw new RuntimeException();

	}

	private void notContainingWhereUpdateStatement(String[] splitted) throws FileSystemNotFoundException,
			FileNotFoundException, ParserConfigurationException, SAXException, IOException, XMLStreamException {
		splitted = filterSplittedArray(splitted);
		String tableName = splitted[0];
		if (splitted[1].compareToIgnoreCase("SET") == 0) {
			ArrayList<String> columnName = new ArrayList<>();
			ArrayList<String> values = new ArrayList<>();
			for (int i = 2; i < splitted.length; i++) {
				if (splitted[i].equals("=")) {
					columnName.add(splitted[i - 1]);
					values.add(schrodingarString(splitted[i + 1])); // fnsy101
				}
			}
			if (validateEqualSize(columnName, values) && isValidName(tableName) && isValidNameInArrayList(columnName)
					&& Functions.exists(dbName, tableName)) {
				Functions result = XmlHandler.load(tableName, dbName);
				result.update(null, null, null, columnName, values, "NOTWHERE");
				TableFormatter.print(result.getColumnsNames(), TableFormatter.toList(result.getTotalTable()));
			} else
				throw new RuntimeException();
		}

	}

	// end of update
	/**
	 * drop Statement
	 * 
	 * @param statement
	 * @throws FileNotFoundException
	 */
	private void dropStatement(final String statement) throws FileNotFoundException {
		String[] splitted = statement.substring(5, statement.length()).split(" ");
		splitted = filterSplittedArray(splitted);
		try {
			dropOfTable(splitted);

		} catch (Exception x) {
			dropOfDatabase(splitted);
		}
	}

	private void dropOfDatabase(String[] splitted) {
		if (splitted.length == 2) {
			if (splitted[0].compareToIgnoreCase("DATABASE") == 0) {
				Functions.dropDatabase(splitted[1]);
			} else
				throw new RuntimeException();
		} else
			throw new RuntimeException();
	}

	private void dropOfTable(String[] splitted) throws FileNotFoundException {
		try {
			if (splitted.length == 2) {
				if (splitted[0].compareToIgnoreCase("TABLE") == 0 && Functions.exists(dbName, splitted[1])) {
					Functions.dropTable(dbName, splitted[1]);
					XmlHandler.deleteDtd(splitted[1], dbName);
				} else
					throw new RuntimeException();
			} else
				throw new RuntimeException();
		} catch (FileSystemNotFoundException | ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException();
		}

	}
	// end of drop

	private void insertionStatement(String statement) throws FileSystemNotFoundException, XMLStreamException,
			ParserConfigurationException, SAXException, IOException {
		if (statement.toUpperCase().contains("VALUES")) {
			String[] splitted = statement.substring(12, statement.length()).split(" ");
			splitted = filterSplittedArray(splitted);
			try { // try catch block fnsy101
				withColumnsNamesInsertionStatement(splitted);

			} catch (Exception x) {
				withoutColumnsNamesInsertionStatement(splitted);

			}

		} else
			throw new RuntimeException();

	}

	private void withColumnsNamesInsertionStatement(String[] splitted) throws FileSystemNotFoundException,
			ParserConfigurationException, SAXException, IOException, XMLStreamException {
		// splitted[0] is the tableName
		ArrayList<String> columnName = new ArrayList<>();
		ArrayList<String> valuesToBeInserted = new ArrayList<>();
		boolean valuesTurn = false;
		for (int i = 1; i < splitted.length; i++) {
			if (splitted[i].toUpperCase().equals("VALUES")) {
				i += 1;
				valuesTurn = true;
			}
			if (!valuesTurn)
				columnName.add(splitted[i]);
			else
				valuesToBeInserted.add(schrodingarString(splitted[i])); // fnsy101
		}
		if (validateEqualSize(columnName, valuesToBeInserted) && isValidNameInArrayList(columnName)
				&& isValidName(splitted[0]) && Functions.exists(dbName, splitted[0])) {
			// fnsy101 removed is valid in// array list
			Functions result = XmlHandler.load(splitted[0], dbName);
			result.Insert(columnName, valuesToBeInserted);

			TableFormatter.print(result.getColumnsNames(), TableFormatter.toList(result.getTotalTable()));

		}

		else {

			throw new RuntimeException();

		}
	}

	private void withoutColumnsNamesInsertionStatement(String[] splitted) throws XMLStreamException,
			FileSystemNotFoundException, ParserConfigurationException, SAXException, IOException {
		// splitted[0] is the tableName // splitted[1] is the word VALUES
		ArrayList<String> valuesToBeInserted = new ArrayList<>();
		for (int i = 2; i < splitted.length; i++) {
			valuesToBeInserted.add(schrodingarString(splitted[i])); // fnsy101
		}
		if (!Functions.exists(dbName, splitted[0])) {
			System.out.println("Ýí ÍÇÌÉ ÛáØ");
		}
		if (isValidName(splitted[0]) && Functions.exists(dbName, splitted[0])) { // fnsy101
			// System.out.println("åäÇÇÇ");
			Functions result = XmlHandler.load(splitted[0], dbName);
			result.Insert(result.getColumnsNames(), valuesToBeInserted);

			TableFormatter.print(result.getColumnsNames(), TableFormatter.toList(result.getTotalTable()));
		} else
			throw new RuntimeException();
	}

	// end insert

	private void selectStatement(String statement) throws FileSystemNotFoundException, XMLStreamException,
			ParserConfigurationException, SAXException, IOException {
		if (statement.toUpperCase().contains("*")) {
			selectAllStatement(statement);
		} else {

			String[] splitted = statement.replace(";", "").split(" ");
			if (splitted[1].toUpperCase().equals("DISTINCT")) {
				if (statement.toUpperCase().contains("FROM"))
					if (statement.toUpperCase().contains("WHERE")) {
						containsWhereSelectDistinctStatement(splitted);

					} else {
						 notContainsWhereSelectDistinctStatement(splitted);

					}

			} else {
				splitted = statement.substring(7, statement.length()).split(" ");
				splitted = filterSplittedArray(splitted);
				dedicatedSelectStatement(splitted);
			}
		} // add throw exception here JDBC

	}

	private void selectAllStatement(String statement) throws XMLStreamException, FileSystemNotFoundException,
			ParserConfigurationException, SAXException, IOException {
		String selectAllFromString = statement.substring(0, 13);
		if (selectAllFromString.compareToIgnoreCase("SELECT * FROM") == 0) {
			String restOfString = filterString(statement.substring(14, statement.length()));// filter
			if (statement.toUpperCase().contains("WHERE")) {
				String[] splitted = filterSplittedArray(restOfString.split(" "));

				containsWhereSelectAllStatement(splitted[0], splitted);
			} else if (!statement.toUpperCase().contains("WHERE")) {
				// send tableName String: restOfString
				if (isValidName(restOfString) && Functions.exists(dbName, restOfString)) {
					Functions result = XmlHandler.load(restOfString, dbName);
					result.SelectAll();
					TableFormatter.print(result.getSelectColumnsNames(),
							TableFormatter.toList(result.getSelectedTable()));
				} else
					throw new RuntimeException();
			}
		} else
			throw new RuntimeException();
	}

	private void dedicatedSelectStatement(String[] splitted) throws FileSystemNotFoundException, FileNotFoundException,
			ParserConfigurationException, SAXException, IOException, XMLStreamException {
		ArrayList<String> columnsName = new ArrayList<>();
		int i = 0;
		int holdIndex;
		while (i < splitted.length && !splitted[i].toUpperCase().equals("FROM")) {
			columnsName.add(splitted[i]);
			i++;
		}
		String tableName = splitted[++i];
		holdIndex = i;
		try {
			if (splitted[++i].compareToIgnoreCase("WHERE") == 0) {
				ArrayList<String> afterFromString = new ArrayList<>();
				for (int j = holdIndex; j < splitted.length; j++) {
					afterFromString.add(splitted[j]);
				}
				String[] sendToWhere = new String[afterFromString.size()];
				for (int j = 0; j < afterFromString.size(); j++)
					sendToWhere[j] = afterFromString.get(j);

				containsWhereDedicatedSelectStatement(tableName, columnsName, splitted);
			} else
				throw new RuntimeException();
		} catch (ArrayIndexOutOfBoundsException x) {
			if (isValidNameInArrayList(columnsName) && isValidName(tableName) && Functions.exists(dbName, tableName)) {
				Functions result = XmlHandler.load(tableName, dbName);
				result.SelectSpecified(columnsName);
				TableFormatter.print(columnsName, TableFormatter.toList(result.getSelectedTable()));
			} else {
				throw new RuntimeException();
			}

			// send ArrayList:columnName String:tableName
		}

	}

	private void containsWhereDedicatedSelectStatement(String tableName, ArrayList<String> columnsName,
			String[] splitted) throws XMLStreamException, FileSystemNotFoundException, ParserConfigurationException,
					SAXException, IOException {
		if (splitted.length == 5) {
			// splitted array starts after the word FROM
			// splitted[0] table name
			String columnName = splitted[2];
			String conditionSymbol = splitted[3];
			String value = schrodingarString(splitted[4]);
			if (isValidName(columnName) && Functions.exists(dbName, tableName)
					&& (conditionSymbol.equals("=") || conditionSymbol.equals("<") || conditionSymbol.equals(">"))) {
				Functions result = XmlHandler.load(tableName, dbName);
				result.SelectSpecifiedWhere(columnsName, columnName, conditionSymbol, value);
				TableFormatter.print(columnsName, TableFormatter.toList(result.getSelectedTable()));

			} else
				throw new RuntimeException();

		} else
			throw new RuntimeException();
	}

	private void containsWhereSelectAllStatement(String tableName, String[] splitted)
			throws FileSystemNotFoundException, ParserConfigurationException, SAXException, IOException,
			XMLStreamException {
		if (splitted.length == 5) {
			// splitted array starts after the word FROM
			// splitted[0] table name
			String columnName = splitted[2];
			String conditionSymbol = splitted[3];
			String value = schrodingarString(splitted[4]);
			if (isValidName(columnName) && Functions.exists(dbName, tableName)
					&& (conditionSymbol.equals("=") || conditionSymbol.equals("<") || conditionSymbol.equals(">"))) {
				Functions result = XmlHandler.load(tableName, dbName);
				result.SelectAllWhere(columnName, conditionSymbol, value);
				TableFormatter.print(result.getSelectColumnsNames(), TableFormatter.toList(result.getSelectedTable()));
			} else
				throw new RuntimeException();

		} else
			throw new RuntimeException();
	}

	private void containsWhereSelectDistinctStatement(String[] splitted) throws FileNotFoundException, XMLStreamException {
		ArrayList<String> columnsNames = new ArrayList<>();
		String tableName = new String();
		String columnName = new String();
		String conditionSymbol = new String();
		String value = new String();

		int i = 2;
		while (!splitted[i].toUpperCase().equals("FROM")) {
			if (splitted[i].contains(",") && !splitted[i + 1].toUpperCase().equals("FROM")) {
				columnsNames.add(splitted[i].replace(",", ""));
			} else if (!splitted[i].contains(",") && splitted[i + 1].toUpperCase().equals("FROM")) {
				columnsNames.add(splitted[i]);
			}
			i++;
		}
		tableName = splitted[++i];
		if (splitted[++i].toUpperCase().equals("WHERE")) {
			columnName = splitted[++i];
			conditionSymbol = splitted[++i];
			value = schrodingarString(splitted[++i]);
		}
		 Functions result = XmlHandler.load(tableName, dbName);
		 result.SelectDistinctionWhere(columnsNames, columnName, conditionSymbol, value);
	     TableFormatter.print(columnsNames, TableFormatter.toList(result.getSelectedTable()));
	}
	
	private void notContainsWhereSelectDistinctStatement(String[] splitted) throws FileNotFoundException, XMLStreamException {
		ArrayList<String> columnsNames = new ArrayList<>();
		String tableName = new String();


		int i = 2;
		while (!splitted[i].toUpperCase().equals("FROM")) {
			if (splitted[i].contains(",") && !splitted[i + 1].toUpperCase().equals("FROM")) {
				columnsNames.add(splitted[i].replace(",", ""));
			} else if (!splitted[i].contains(",") && splitted[i + 1].toUpperCase().equals("FROM")) {
				columnsNames.add(splitted[i]);
			}
			i++;
		}
		tableName = splitted[++i];		
		 Functions result = XmlHandler.load(tableName, dbName);
		 result.SelectDistinction(columnsNames);
	     TableFormatter.print(columnsNames, TableFormatter.toList(result.getSelectedTable()));
	}
	// end select

	private void alterStatement(String statement) throws FileSystemNotFoundException, XMLStreamException,
			ParserConfigurationException, SAXException, IOException {
		statement = replaceVarchar(statement);
		String[] splitted = statement.split(" ");
		splitted = filterSplittedArray(splitted);
		if (splitted.length == 6)
			if (splitted[3].toUpperCase().equals("ADD")) {
				addingColumnALter(splitted);
			} else if (splitted[3].toUpperCase().equals("DROP")) {
				droppingColumnALter(splitted);
			} else {
				throw new RuntimeException();
			}
		else
			throw new RuntimeException();
	}

	private void addingColumnALter(String[] splitted) {
		if (splitted[5].equals("String") || splitted[5].equals("int") || splitted[5].equals("float")
				|| splitted[5].equals("date")) {
			System.out.println(splitted[2]);
			System.out.println(splitted[4]);
			System.out.println(splitted[5]);
			// send to raafat splitted[2] < tableName
			// splitted [4] columnName
			// splitted [5] dataType

		} else {
			throw new RuntimeException();
		}

	}

	private void droppingColumnALter(String[] splitted) {
		if (splitted[4].toUpperCase().equals("COLUMN")) {
			System.out.println(splitted[2]);
			System.out.println(splitted[5]);
			// send to raafat splitted[2] tableName
			// send to raafat splitted[5] columnName
		} else {
			throw new RuntimeException();
		}

	}

	private String[] filterSplittedArray(String[] splitted) {
		String[] filtered = new String[splitted.length];
		for (int i = 0; i < splitted.length; i++)
			filtered[i] = filterString(splitted[i]);
		return filtered;
	}

	private String filterString(String string) {
		string = string.replace("(", "").replace(")", "").replace(";", "").replace(",", ""); // fnsy101
		return string;
	}

	private String[] filterSplittedArrayForTableCreation(String[] splitted) {
		String[] filtered = new String[splitted.length];
		for (int i = 0; i < splitted.length; i++)
			filtered[i] = splitted[i].replace("(", "").replace(")", "").replace(";", "");
		return filtered;
	}

	private boolean validateEqualSize(ArrayList one, ArrayList two) {
		if (one.size() != two.size())
			return false;
		return true;
	}

	@SuppressWarnings("deprecation")
	private String schrodingarString(String check) {
		// added Float.parseFloat(check); in the JDBC version
		check = check.trim();
		if (check.charAt(0) == '\'' && check.charAt(check.length() - 1) == '\''
				|| check.charAt(0) == '\"' && check.charAt(check.length() - 1) == '\"') {

			check = check.replace("#", " ").replace("\"", "").replace("'", "").trim();
			try {
				try {
					Integer.parseInt(check);
				} catch (NumberFormatException dx) {
					try {
						Date.parse(check);
					} catch (IllegalArgumentException fx) {
						Float.parseFloat(check);
					}
				}
				throw new RuntimeException();
			} catch (NumberFormatException x) {
				return check;

			}
		} else {
			try {
				Integer.parseInt(check);
			} catch (NumberFormatException dx) {
				try {
					Date.parse(check);
				} catch (IllegalArgumentException fx) {
					Float.parseFloat(check);
				}
			}
			return check;
		}

	}

	private String handleSingleQuotes(String statement) { // fnsy101
		return handleDoubleQuotes(statement);
	}

	private String handleDoubleQuotes(String statement) { // fnsy101
		char buf[] = statement.toCharArray(), quote = ' ', c;
		for (int i = 0; i < buf.length; i++) {
			if ((c = buf[i]) == '"' || c == '\'')
				quote = (quote == ' ' ? c : quote == c ? ' ' : quote);
			else if (c == ' ' && quote != ' ')
				buf[i] = '#';
		}
		return new String(buf).trim();
	}

	private void initializeReservedWords() {
		this.reservedWords = new String[] { "ALL", "ALTER", "AND", "ANY", "ARRAY", "ARROW", "AS", "ASC", "AT", "BEGIN",
				"BETWEEN", "BY", "CASE", "CHECK", "CLUSTERS", "CLUSTER", "COLAUTH", "COLUMNS", "COMPRESS", "CONNECT",
				"CRASH", "CREATE", "CURRENT", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP",
				"ELSE", "END", "EXCEPTION", "EXCLUSIVE", "EXISTS", "FETCH", "FORM", "FOR", "FROM", "GOTO", "GRANT",
				"GROUP", "HAVING", "IDENTIFIED", "IF", "IN", "INDEXES", "INDEX", "INSERT", "INTERSECT", "INTO", "IS",
				"LIKE", "LOCK", "MINUS", "MODE", "NOCOMPRESS", "NOT", "NOWAIT", "NULL", "OF", "ON", "OPTION", "OR",
				"ORDER", "OVERLAPS", "PRIOR", "PROCEDURE", "RANGE", "RECORD", "RESOURCE", "REVOKE", "SELECT", "SHARE",
				"SIZE", "SQL", "SUBTYPE", "TABAUTH", "TABLE", "THEN", "TO", "TYPE", "UNION", "UNIQUE", "UPDATE", "USE",
				"VALUES", "VIEW", "VIEWS", "WHEN", "WHERE", "WITH" };

	}

	private final boolean isValidStatement(final String statement) {
		int semiColonIndex = statement.length() - 1;
		if (!(statement.charAt(semiColonIndex) == ';'))
			return false;

		return true;
	}

	private final boolean isValidName(String name) {
		name = name.toUpperCase();
		for (int i = 0; i < name.length(); i++) {
			int singleChar = name.charAt(i);
			if ((singleChar < 65 && singleChar != 36) || (singleChar > 90 && singleChar != 95)) {
				if (singleChar < 48 || singleChar > 57)
					return false;
			}
		}
		return true;
	}

	private final boolean isValidNameInArrayList(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			if (!isValidName(list.get(i)))
				return false;
		}
		return true;
	}

	private ArrayList<String> makeValuesValid(ArrayList<String> list) {
		ArrayList<String> ret = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			ret.add(schrodingarString(list.get(i)));
		}
		return ret;

	}

	private boolean isReservedWord(String name) {
		name = name.toUpperCase();
		for (int i = 0; i < this.reservedWords.length; i++)
			if (name.equals(this.reservedWords[i]))
				return true;
		return false;
	}

	private String replaceVarchar(String statement) {
		Pattern pattern = Pattern.compile("\\(\\d+\\)");
		Matcher matcher = pattern.matcher(statement);
		while (matcher.find()) {
			statement = statement.replace(matcher.group(), "");
		}
		return statement.replace("varchar", "String");
	}

	private String removeWhiteSpaces(final String statement) {
		String checkedStatement;
		checkedStatement = statement.replaceAll("=", " = ");
		checkedStatement = checkedStatement.replace("\n", " ");
		checkedStatement = checkedStatement.replaceAll("`", "'"); // fnsy101
		checkedStatement = checkedStatement.replaceAll("\t", " "); // fnsy101
		checkedStatement = handleSingleQuotes(checkedStatement);
		checkedStatement = handleDoubleQuotes(checkedStatement);
		checkedStatement = checkedStatement.replace("(", " (");
		checkedStatement = checkedStatement.replaceAll(">", " > ");
		checkedStatement = checkedStatement.replaceAll("<", " < ");
		checkedStatement = checkedStatement.replaceAll(" +", " ");
		checkedStatement = checkedStatement.replaceAll(" ,", ", ");
		checkedStatement = checkedStatement.replaceAll(",", ", ");
		checkedStatement = checkedStatement.replaceAll(" ;", "; ");
		checkedStatement = checkedStatement.replaceAll("\\( ", "\\(");
		checkedStatement = checkedStatement.replaceAll(" \\)", "\\)");
		checkedStatement = checkedStatement.replaceAll("\\*", " \\* ");
		checkedStatement = checkedStatement.replaceAll("\n", " ");
		checkedStatement = checkedStatement.replaceAll(" +", " ");
		checkedStatement = checkedStatement.trim();
		return checkedStatement;
	}

}