package freesm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import freesm.utils.messaging.ReportException;

public class Utils {


	public static String readInput(InputStream in, String errorMsg) {
		InputStreamReader ird = new InputStreamReader(in);			
		BufferedReader br = new BufferedReader(ird);
		try {
			return br.readLine().trim();
		} catch (IOException e) {
			ReportException.errorMessageDefaultAction(errorMsg, e);
			return "";
		}
	}
}
