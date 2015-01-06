package boh.android.ert.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ERTDefaultLoginActivity extends AccountAuthenticatorActivity {
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";
   
    private String mAuthTokenType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Intent data = getIntent();
		String accType = data.getStringExtra(ARG_ACCOUNT_TYPE);
		mAuthTokenType = data.getStringExtra(ARG_AUTH_TYPE);
		boolean toCreate = data.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);		

        if (mAuthTokenType == null)
            mAuthTokenType = ERTAccountService.ACCOUNT_TYPE;		
		
		findViewById(R.id.btn_login)
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
			
		});
	}

	private void login() {
		
		final String userName = ((TextView) findViewById(R.id.account_username)).getText().toString();
		final String userPass = ((TextView) findViewById(R.id.account_password)).getText().toString();				

		final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

		new AsyncTask<String, Void, Bundle>() {

			@Override
			protected Bundle doInBackground(String... params) {

				String authtoken = null;
				Bundle data = new Bundle();
				try {
					ERTAccountService.Authenticator tokenProvider= ERTAccountService.getAuthenticator();
					authtoken = tokenProvider.login(userName, userPass, null);
					data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
					data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
					data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
					data.putString(PARAM_USER_PASS, userPass);
				} catch (ERTAuthenticatorException e) {
					data.putString(KEY_ERROR_MESSAGE, e.getMessage());
				}

				return data;
			}

			@Override
			protected void onPostExecute(Bundle bundle) {
				
				if (bundle.containsKey( KEY_ERROR_MESSAGE) ) {
					//error occurs during authentication
					Toast.makeText(getBaseContext(), bundle.getString(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
				} else {
					persistAuthToken(bundle);
				}
			}
		}.execute();
	}

	private void persistAuthToken(Bundle accountData) {

		String accountName = accountData.getString(AccountManager.KEY_ACCOUNT_NAME);
		String accountPassword = accountData.getString(PARAM_USER_PASS);
		final Account account = new Account(accountName, accountData.getString(AccountManager.KEY_ACCOUNT_TYPE));

		AccountManager mAccountManager = AccountManager.get(this.getBaseContext());
		AccountManager mAccountManager2 = AccountManager.get(this.getApplicationContext());
		AccountManager mAccountManager3 = AccountManager.get(this);
		AccountManager mAccountManager4 = (AccountManager) this.getSystemService(Context.ACCOUNT_SERVICE);
		
		if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
			
			String authtoken = accountData.getString(AccountManager.KEY_AUTHTOKEN);
			String authtokenType = mAuthTokenType;

			// Creating the account on the device and setting the auth token we got
			// (Not setting the auth token will cause another call to the server to authenticate the user)
	        try {
				if( mAccountManager4.addAccountExplicitly(account, accountPassword, null) ) {
					mAccountManager4.setAuthToken(account, authtokenType, authtoken);
				}
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
		} else {
			//remember the password 
			mAccountManager4.setPassword(account, accountPassword);
		}
		
		Bundle bdl = new Bundle();
		bdl.putParcelable(ERTAccountService.ACCOUNT_TYPE, account);
		Intent intentNew = new Intent();
		intentNew.putExtra(ERTAccountService.ACCOUNT_TYPE, bdl);
		
		setAccountAuthenticatorResult(bdl);		
//		//
//		setResult(RESULT_OK, intentNew);
//		
		finish();
	}
}
