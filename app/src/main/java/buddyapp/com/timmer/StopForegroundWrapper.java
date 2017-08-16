package buddyapp.com.timmer;

import android.app.Service;

public class StopForegroundWrapper
{
    public void stopForegroundAndRemoveNotificationIcon(Service service)
	{ 
		// The true argument removes the notification icon. 
		service.stopForeground(true);
	} 
} 
 