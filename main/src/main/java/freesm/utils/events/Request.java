package freesm.utils.events;

import java.nio.ByteBuffer;

public class Request {
	
	private static String RegisterAccount = "r";
	private static String Publish = "p";
	private static String Comment = "c";
	
	public static byte[] requestRegisterAccount() {
		return RegisterAccount.getBytes();
	}

	public static byte[] requestPublish(String msg) {
		ByteBuffer bf = ByteBuffer.allocate(1+msg.length());
		bf.put(Publish.getBytes());
		bf.put(msg.getBytes());
		return bf.array();
	}
	
	public static byte[] requestComment(long assetId, String articleId) {
		ByteBuffer bf = ByteBuffer.allocate(1+Long.BYTES);
		bf.put(Comment.getBytes());
		bf.putLong(assetId);
		bf.put(articleId.getBytes());
		return bf.array();
	}
}