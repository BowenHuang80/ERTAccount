ERTAccount
==========
Description
Library for creating account for ERT

Use Guide
After including the ERTAccount.jar in the project, follow these steps:

1. Implement ERTAccountService.Authenticator
2. Register it with ERTAccountService.registerAuthenticator() in your initialization method, ie, onCreate
3. Declare service in the AndroidManifest.xml, or copy following lines
       <activity
            android:name="boh.android.ert.account.ERTDefaultLoginActivity"
            android:label="@string/title_activity_main" >
        </activity>
        <service 
            android:name="boh.android.ert.account.ERTAccountService" >
            <intent-filter>
                <action android:name="android.accounts.AccountAuthenticator" />
            </intent-filter>
            <meta-data android:name="android.accounts.AccountAuthenticator"
                       android:resource="@xml/authenticator" />
        </service>
4. Done
5. In case you are not satisfied with the default Credential input activity, you can create one of your own and register it with ERTAccountService.registerLoginActivity().
6. Done
