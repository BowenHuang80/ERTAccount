package boh.android.ert.account;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ERTAccountService extends Service  {

	private static final String TAG = ERTAccountService.class.getSimpleName();
	public static final String ACCOUNT_TYPE = "boh.android.ert_account";
	public static final String AUTH_TYPE_WRITE_ACCESS = "boh.android.ert.auth.full_access";
	public static final String AUTH_TYPE_READ_ACCESS = "boh.android.ert.auth.read_access";
		
	private ERTAccountAuthenticator mAuthenticator;

	@Override
	public void onCreate() {
		Log.i(TAG, "Service created");
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "Service destroyed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		mAuthenticator = new ERTAccountAuthenticator(this);
		return mAuthenticator.getIBinder();
	}
	
	private static Class<?> sAuth;
	private static Class<?> sLogin;
	
	public static void registerAuthenticator(Class<?> auth) 
			throws InstantiationException, IllegalAccessException 
	{
		sAuth = auth; 
	}

	public static void registerLoginActivity(Class<?> loginActivity) 
			throws InstantiationException, IllegalAccessException 
	{
		sLogin = loginActivity; 
	}
	
	public static Class<?> getLoginActivity() {
		if( sLogin == null ) {
			sLogin = ERTDefaultLoginActivity.class;
		}
		return sLogin;
	}
	
	public static Authenticator getAuthenticator() {
		Authenticator auth = null;
		try {
			auth = (Authenticator) sAuth.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return auth;
	}
	
	public interface Authenticator {
		String login(String user, String pass, Bundle options) throws ERTAuthenticatorException;
		String userSignup();
	}	
	
}
