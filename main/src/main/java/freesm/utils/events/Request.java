package freesm.utils.events;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.util.Encoder;

import freesm.utils.messaging.ReportMessage;

public class Request {
	
	private static String RegisterAccount = "r";
	private static String Publish = "p";
	private static String Comment = "c";
	
	public static byte[] requestRegisterAccount() {
		return RegisterAccount.getBytes();
	}

	public static byte[] requestPublish(String address, String msg) {
		ByteBuffer bf = ByteBuffer.allocate(36+1+msg.length());
		Address addr =  null;
		try {
			addr = new Address(address);
		} catch (NoSuchAlgorithmException e) {
			ReportMessage.errorMessageDefaultAction("Could not get address.", e);
			return null;
		}
		byte  [] addressBytes = addr.getBytes();
		assert(addressBytes.length == 32);
		bf.put(addressBytes);
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