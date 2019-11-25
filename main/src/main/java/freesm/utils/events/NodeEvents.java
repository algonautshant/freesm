package freesm.utils.events;

public interface NodeListener {
	
	public void onRegisterRequest();
	
	public void onAssetRequest();
	
	public void onPublishRequest();
	
	public void onCommentRequest();

}
