package boh.android.ert.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class ERTAccountAuthenticator extends AbstractAccountAuthenticator {
	
	private static final String TAG = ERTAccountAuthenticator.class.getSimpleName();
	
	protected final Context mContext; 
	public ERTAccountAuthenticator(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
		throws NetworkErrorException 
	{
        Log.d("ERT", "> addAccount");

        //Call our login activity to 
        final Intent intent = new Intent(mContext, ERTAccountService.getLoginActivity());
        intent.putExtra(ERTDefaultLoginActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(ERTDefaultLoginActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(ERTDefaultLoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;		
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException 
	{
		final AccountManager accountM = AccountManager.get(mContext);

		String authToken = accountM.peekAuthToken(account, authTokenType);

		// Lets give another try to authenticate the user
		if (TextUtils.isEmpty(authToken)) {
			String password = accountM.getPassword(account);
			if (password != null) {
				try {
					ERTAccountService.Authenticator auth = ERTAccountService.getAuthenticator(); 
					authToken = auth.login(account.name, password, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// If we get an authToken - we return it
		if (!TextUtils.isEmpty(authToken)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			return result;
		}

		// If we get here, then we couldn't access the user's password - so we
		// need to re-prompt them for their credentials. We do that by creating
		// an intent to display our Login dialog.
		final Intent intent = new Intent(mContext, ERTDefaultLoginActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		intent.putExtra(ERTDefaultLoginActivity.ARG_ACCOUNT_TYPE, account.type);
		intent.putExtra(ERTDefaultLoginActivity.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra(ERTDefaultLoginActivity.ARG_ACCOUNT_NAME, account.name);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;		
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		return "LABLE AUTH TOKEN";
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
	}
}
