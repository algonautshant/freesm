package freesm.utils.account;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;

import freesm.utils.messaging.ReportException;

public class Account {

	private com.algorand.algosdk.account.Account algoAccount;
	private Account() throws NoSuchAlgorithmException {
		algoAccount = new com.algorand.algosdk.account.Account();
	}
	private Account(String mnemonic) throws GeneralSecurityException {
		algoAccount = new com.algorand.algosdk.account.Account(mnemonic);
	}
	
	public static Account loadAccount(String mnemonic) {
		Account newAccount;
		try {
			newAccount = new Account(mnemonic);
		} catch (GeneralSecurityException e) {
			ReportException.errorMessageDefaultAction("Failed to create account.", e);
			return null;
		}
		return newAccount;
	}

	public SignedTransaction signTransaction(Transaction tx) {
		try {
			return algoAccount.signTransactionWithFeePerByte(tx, tx.fee);
		} catch (NoSuchAlgorithmException e) {
			ReportException.errorMessageDefaultAction("Error signing the transaction.", e);
			return null;
		}
	}
	
	public String getAddress() {
		return algoAccount.getAddress().toString();
	}
	 
}
