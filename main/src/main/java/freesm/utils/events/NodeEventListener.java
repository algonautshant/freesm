package freesm.utils.events;

public interface NodeEventListener {
	
	public void onRegisterRequest();
	
	public void onAssetRequest();
	
	public void onPublishRequest();
	
	public void onCommentRequest();

}
