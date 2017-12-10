package databaseManager;

import java.util.ArrayList;
import java.util.Objects;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

//import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import java.io.StringWriter;
import java.io.StringReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import java.nio.file.FileSystemNotFoundException;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlHandler {

	public static void save(Functions table, String databaseName)
			throws XMLStreamException, IOException, ParserConfigurationException {
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
		xMLOutputFactory.setProperty("escapeCharacters", false);
		// Creating XML file with name of tableName
		XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

		// Starting the document
		xMLStreamWriter.writeStartDocument();

		// Two nested loops : Outer loop is looping over Table 2D arrayList
		// Inner loop is looping over each column
		ArrayList<ArrayList<String>> Table = table.getTotalTable();
		ArrayList<String> columnsNames = table.getColumnsNames();
		ArrayList<String> dataType = table.getDataType();
		int TableSize = Table.size();
		xMLStreamWriter.writeStartElement(table.getTableName());
		xMLStreamWriter.writeStartElement("ColumnNames");
		for (int i = 0; i < columnsNames.size(); i++) {
			xMLStreamWriter.writeStartElement("Col" + (i + 1));
			xMLStreamWriter.writeCharacters(columnsNames.get(i));
			xMLStreamWriter.writeEndElement();// End of colname element

		} // for
		xMLStreamWriter.writeEndElement();// End of columnsNames element
		xMLStreamWriter.writeStartElement("DataTypes");
		for (int i = 0; i < dataType.size(); i++) {
			xMLStreamWriter.writeStartElement("Type" + (i + 1));
			xMLStreamWriter.writeCharacters(dataType.get(i));
			xMLStreamWriter.writeEndElement();// End of type element

		} // for
		xMLStreamWriter.writeEndElement();

		for (int i = 0; i < TableSize; i++) {
			xMLStreamWriter.writeStartElement("Column");
			// xMLStreamWriter.writeAttribute("Name", columnsNames.get(i));
			// xMLStreamWriter.writeAttribute("Type", dataType.get(i));
			ArrayList<String> cells = Table.get(i);
			int cellsNum = cells.size();
			for (int j = 0; j < cellsNum; j++) {
				xMLStreamWriter.writeStartElement("Cell" + (j + 1));
				if (cells.get(j) == null) {
					xMLStreamWriter.writeCharacters("null");
				} else {
					xMLStreamWriter.writeCharacters(cells.get(j));
				}

				xMLStreamWriter.writeEndElement();// End of Cell element
			} /* for */
			xMLStreamWriter.writeEndElement();// End of Column element

		} /* for */
		xMLStreamWriter.writeEndElement();// End of Table element

		String xmlString = stringWriter.getBuffer().toString();
		stringWriter.close();
		xMLStreamWriter.flush();
		xMLStreamWriter.close();
		makeFile(xmlString, table.getTableName(), databaseName);

	}/* save Method */

	public static Functions load(String tableName, String dataBaseName)
			throws FileNotFoundException, XMLStreamException {
		// Instantiating the ArrayLists that will receive the data from the XML
		// file
		ArrayList<ArrayList<String>> Table = new ArrayList<>();
		ArrayList<String> columnsNames = new ArrayList<>(); /*
															 * of size =
															 * ColumnsNo
															 */
		ArrayList<String> dataType = new ArrayList<>();
		// Loading from XML file
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		String absoluteFilePath = dataBaseName + File.separator + tableName;
		XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(new FileReader(absoluteFilePath + ".xml"));
		int ColumnNumberData = -1;
		String tag = "COLNAME";

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			// boolean isCell = false;
			switch (event.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:

				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals("Column")) {
					ColumnNumberData++;
					ArrayList<String> Column = new ArrayList<>();
					Table.add(Column);
					tag = "COL";
				} // if
				else if (startElement.getName().getLocalPart().equals("ColumnNames")) {

					tag = "COLNAME";
				} // else if
				else if (startElement.getName().getLocalPart().equals("DataTypes")) {

					tag = "TYPE";
				} // else if

				break;
			case XMLStreamConstants.CHARACTERS:
				Characters characters = event.asCharacters();
				switch (tag) {
				case "COL":
					String cellData = characters.getData();
					if (Objects.equals("null", cellData)) {
						Table.get(ColumnNumberData).add(null);
					} /* if */
					else if (!Objects.equals("\n", cellData) && !Objects.equals("\n  ", cellData)
							&& !Objects.equals("\n    ", cellData)) {
						Table.get(ColumnNumberData).add(cellData);
					}
					break;
				case "COLNAME":
					String cellData2 = characters.getData();

					if (!Objects.equals("null", cellData2) && !Objects.equals("\n", cellData2)
							&& !Objects.equals("\n  ", cellData2) && !Objects.equals("\n    ", cellData2)) {
						columnsNames.add(cellData2);
					}
					break;
				case "TYPE":
					String cellData3 = characters.getData();

					if (!Objects.equals("null", cellData3) && !Objects.equals("\n", cellData3)
							&& !Objects.equals("\n  ", cellData3) && !Objects.equals("\n    ", cellData3)) {
						dataType.add(cellData3);
					}
					break;
				}// Switch
				break;
			}/* switch */

		} /* While */
		Functions result = new Functions(tableName, Table, columnsNames, dataType, dataBaseName);
		return result;
	}/* Load */

	public static void createDtd(ArrayList<String> columnName, String tableName, String databaseName, int numberOfRows)
			throws ParserConfigurationException, SAXException, IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(databaseName + File.separator + tableName + ".dtd"));
		if (numberOfRows <= 0) {
			writer.write("<!ELEMENT " + tableName + " (ColumnNames,DataTypes)>");
			writer.newLine();
			writer.write("<!ELEMENT ColumnNames (");
			for (int j = 0; j < columnName.size(); j++) {
				if (j == columnName.size() - 1) {
					writer.write("Col" + (j + 1) + ")>");
				} else {
					writer.write("Col" + (j + 1) + ",");
				}
			}
			writer.newLine();
			for (int j = 0; j < columnName.size(); j++) {
				writer.write("<!ELEMENT " + "Col" + (j + 1) + " (#PCDATA)>");
				writer.newLine();
			}
			writer.write("<!ELEMENT DataTypes (");
			for (int j = 0; j < columnName.size(); j++) {
				if (j == columnName.size() - 1) {
					writer.write("Type" + (j + 1) + ")>");
				} else {
					writer.write("Type" + (j + 1) + ",");
				}
			}
			writer.newLine();
			for (int j = 0; j < columnName.size(); j++) {
				writer.write("<!ELEMENT " + "Type" + (j + 1) + " (#PCDATA)>");
				writer.newLine();
			}
		} else {
			writer.write("<!ELEMENT " + tableName + " (ColumnNames,DataTypes,Column");
			if (columnName.size() == 1) {
				writer.write(")>");
			} else {
				writer.write("+)>");
			}
			writer.newLine();
			writer.write("<!ELEMENT ColumnNames (");
			for (int j = 0; j < columnName.size(); j++) {
				if (j == columnName.size() - 1) {
					writer.write("Col" + (j + 1) + ")>");
				} else {
					writer.write("Col" + (j + 1) + ",");
				}
			}
			writer.newLine();
			for (int j = 0; j < columnName.size(); j++) {
				writer.write("<!ELEMENT " + "Col" + (j + 1) + " (#PCDATA)>");
				writer.newLine();
			}
			writer.write("<!ELEMENT DataTypes (");
			for (int j = 0; j < columnName.size(); j++) {
				if (j == columnName.size() - 1) {
					writer.write("Type" + (j + 1) + ")>");
				} else {
					writer.write("Type" + (j + 1) + ",");
				}
			}
			writer.newLine();
			for (int j = 0; j < columnName.size(); j++) {
				writer.write("<!ELEMENT " + "Type" + (j + 1) + " (#PCDATA)>");
				writer.newLine();
			}
			writer.write("<!ELEMENT Column (");
			for (int i = 0; i < numberOfRows; i++) {
				if (i == numberOfRows - 1) {
					writer.write("Cell" + (i + 1) + ")");
				} else {
					writer.write("Cell" + (i + 1) + ",");
				}
			}
			writer.write(">");
			writer.newLine();
			for (int i = 0; i < numberOfRows; i++) {
				writer.write("<!ELEMENT " + "Cell" + (i + 1) + " (#PCDATA)>");
				writer.newLine();
			}
		}

		writer.close();
	}

	public static void deleteDtd(String tableName, String databaseName) throws FileNotFoundException {
		File f1 = new File(databaseName);
		if (f1.exists() && f1.isDirectory()) {
			String fileName = tableName;
			String absoluteFilePath = databaseName + File.separator + fileName + ".dtd";
			File f2 = new File(absoluteFilePath);
			if (f2.exists()) {
				f2.delete();
			} else {
				throw new FileNotFoundException();
			}
		} else {
			System.out.println("database not found");
		}
	}

	private static void validate(String xmlFile)
			throws ParserConfigurationException, FileSystemNotFoundException, SAXException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		documentBuilder.setErrorHandler(new org.xml.sax.ErrorHandler() {

			@Override
			public void error(SAXParseException exception) throws SAXException {
				throw exception;
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				// TODO Auto-generated method stub
				throw exception;
			}

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				// TODO Auto-generated method stub
				throw exception;
			}

		});
		documentBuilder.parse(new FileInputStream(xmlFile));
	}

	public static boolean isValidDTD(String tablename)
			throws FileSystemNotFoundException, ParserConfigurationException, SAXException, IOException {
		boolean flag = true;
		try {
			validate(tablename);
		} catch (FileSystemNotFoundException e) {
			flag = false;
		} catch (ParserConfigurationException e) {
			flag = false;
		} catch (SAXException e) {
			flag = false;
		} catch (IOException e) {
			flag = false;
		}
		return flag;
	}

	public static void makeFile(String XML, String name, String dataBaseName) throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		// dbf.setNamespaceAware(true);
		// dbf.setFeature("http://xml.org/sax/features/namespaces", false);
		// dbf.setFeature("http://xml.org/sax/features/validation", false);
		// dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
		// false);
		// dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
		// false);
		DocumentBuilder builder;
		try {
			builder = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(XML));
			Document doc = builder.parse(is);

			DOMSource source = new DOMSource(doc);
			String absoluteFilePath = dataBaseName + File.separator + name;
			FileWriter writer = new FileWriter(new File(absoluteFilePath + ".xml"));
			StreamResult result = new StreamResult(writer);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, name + ".dtd");

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// method

	public static void emptyFile(String name, String dataBaseName)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		try {

			Document doc = dbf.newDocumentBuilder().newDocument();
			Element element = (Element) doc.createElement(name);
			doc.appendChild((Node) element);

			DOMSource source = new DOMSource(doc);
			String absoluteFilePath = dataBaseName + File.separator + name;
			FileWriter writer = new FileWriter(new File(absoluteFilePath + ".xml"));
			StreamResult result = new StreamResult(writer);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, name + ".dtd");

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.transform(source, result);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// method

	public void makefile(String xml) throws FileNotFoundException {
		PrintWriter out = new PrintWriter("filename.xml");
		out.println(xml);
	}

}/* Class */