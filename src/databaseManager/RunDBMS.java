package databaseManager;

import java.sql.Date;
import java.util.Scanner;

public class RunDBMS {
/*
	public static void main(String[] args) throws Exception {
		StatementsParser parser = StatementsParser.createObject();
		while (true) {
			System.out.println();
			String statement = "";
			do {
				@SuppressWarnings("resource")
				Scanner scan = new Scanner(System.in);
				System.out.print("sql> ");
				statement = statement + "\n" + scan.nextLine();
			} while (!statement.contains(";"));
			System.out.println();
			if (statement.toUpperCase().trim().contains("EXIT;")) {
				System.out.println("GOOD BYE");
				break;
			} else {
				parser.enterStatement(statement);
			}
		}*/
		public static void main(String[] args)  {
	        String myDate = "1000-01-1";
	        if (isSQLDate(myDate)) {
	            System.out.println("VALID DATE");
	        } else {
	            System.out.println("INVALID DATE");
	        }
	    }
		
		private static boolean isSQLDate(String date) {
			try{
				 Date.valueOf(date);
			}catch(Exception e){
				return false;
			}
			return true;
		}//method
	

}
