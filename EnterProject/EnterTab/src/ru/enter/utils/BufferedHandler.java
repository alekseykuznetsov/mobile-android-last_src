package ru.enter.utils;

import android.os.Message;

public class BufferedHandler extends PauseHandler{

	public interface OnBufferedHandlerListener{
		void onBufferedHandle(Message msg);
	}
	
    private OnBufferedHandlerListener listener;

    public void setOnBufferedHandlerListener(OnBufferedHandlerListener listener) {
    	this.listener = listener;
    }
    
	@Override
	protected boolean storeMessage(Message message) {
		return true;
	}

	@Override
	protected void processMessage(Message msg) {
		if(listener != null)
			listener.onBufferedHandle(msg);
	}		
}