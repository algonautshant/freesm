package freesm.bot;

import freesm.utils.account.Account;
import freesm.utils.client.AlgodClientApi;

public class FreeSmBot {
	
	private AlgodClientApi api;
	private Account account;
	
	public FreeSmBot(String accountMnemonic, String algodApiAddr, String algodApiToken) {
		account = Account.loadAccount(accountMnemonic);
		api = new AlgodClientApi(algodApiAddr, algodApiToken);
	}
	
	



}
