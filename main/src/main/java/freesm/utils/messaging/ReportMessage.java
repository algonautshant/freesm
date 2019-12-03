package freesm.utils.messaging;

public class ReportMessage {

	public static void errorMessageDefaultAction(String msg) {
		messageNoThrow(msg, null);
	}

	public static void errorMessageDefaultAction(String msg, Exception e) {
		messageNoThrow(msg, e);
	}
	
	public static void messageNoThrow(String msg, Exception e) {
		if (null != e) {
			e.printStackTrace();
		} else {
			System.err.println(Thread.currentThread().getStackTrace());
		}
		System.err.println(msg);
	}
	
	public static void runtimeException(String msg) {
		throw new RuntimeException(msg);
	}
	
	public static void runtimeException(String msg, Exception e) {
		e.printStackTrace();
		throw new RuntimeException(msg);
	}
	
	public static void printMessage(String msg) {
		System.out.println(msg);
	}
}
