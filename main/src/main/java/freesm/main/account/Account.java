package freesm.main.account;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class Account {

	private com.algorand.algosdk.account.Account algoAccount;
	private Account() throws NoSuchAlgorithmException {
		algoAccount = new com.algorand.algosdk.account.Account();
	}
	private Account(String mnemonic) throws GeneralSecurityException {
		algoAccount = new com.algorand.algosdk.account.Account(mnemonic);
	}
	
	public static Account createAccount() {
		Account newAccount;
		try {
			newAccount = new Account();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create account.");
		}
		return newAccount;
	}
	
	
	public static Account loadAccount(String mnemonic) {
		Account newAccount;
		try {
			newAccount = new Account(mnemonic);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create account.");
		}
		return newAccount;
	}
	
	 
}
