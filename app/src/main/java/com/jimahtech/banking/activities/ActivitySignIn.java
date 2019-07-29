package com.jimahtech.banking.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.manager.FingerprintHandler;
import com.jimahtech.banking.manager.PrefManager;

import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ActivitySignIn extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    private PrefManager prf;
    EditText loginText, passText;
    TextView login, new_account,showFingerPrint;
    ImageView fingerPrint;
    CheckBox checkBox;
    boolean isAgent = false;
    RelativeLayout relativeLayout,relativeBackground;


    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "banking";
    private Cipher cipher;
    private TextView textView,textHead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        }

        FingerprintHandler helper;
        textView = (TextView) findViewById(R.id.errorText);

        // Check whether the device has a Fingerprint sensor.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!fingerprintManager.isHardwareDetected()) {
                /**
                 * An error message will be displayed if the device does not contain the fingerprint hardware.
                 * However if you plan to implement a default authentication method,
                 * you can redirect the user to a default authentication activity from here.
                 * Example:
                 * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                 * startActivity(intent);
                 */
                textView.setText("Your Device does not have a Fingerprint Sensor");
                LinearLayout linearLayout = findViewById(R.id.fingerPrint_layout);
                linearLayout.setVisibility(View.GONE);
            } else {
                // Checks whether fingerprint permission is set on manifest
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    textView.setText("Fingerprint authentication permission not enabled");
                } else {
                    // Check whether at least one fingerprint is registered
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        textView.setText("Register at least one fingerprint in Settings");
                    } else {
                        // Checks whether lock screen security is enabled or not
                        if (!keyguardManager.isKeyguardSecure()) {
                            textView.setText("Lock screen security not enabled in Settings");
                        } else {
                            generateKey();


                            if (cipherInit()) {
                                FingerprintManager.CryptoObject cryptoObject = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                }
                                 helper = new FingerprintHandler(this);
                                helper.startAuth(fingerprintManager, cryptoObject);
                            }
                        }
                    }
                }
            }
        }

        loginText = (EditText) findViewById(R.id.sign_in_name);
        passText = (EditText) findViewById(R.id.sign_in_pass);
        login = (TextView) findViewById(R.id.login);
        fingerPrint = findViewById(R.id.useFingerprint);
        showFingerPrint = findViewById(R.id.displayText);
        checkBox = findViewById(R.id.checkbox);
        relativeLayout = findViewById(R.id.relativeLayout);
        relativeBackground = findViewById(R.id.relativeBackground);
        textHead = findViewById(R.id.textHead);

        YoYo.with(Techniques.ZoomInRight).duration(1000).repeat(0).playOn(findViewById(R.id.sign_in_name));
        YoYo.with(Techniques.ZoomInRight).delay(100).duration(1000).repeat(0).playOn(findViewById(R.id.sign_in_pass));
        YoYo.with(Techniques.ZoomInRight).delay(300).duration(1000).repeat(0).playOn(findViewById(R.id.login));
        YoYo.with(Techniques.Pulse).delay(1000).duration(1400).repeat(1000).playOn(findViewById(R.id.displayText));


        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);

        prf = new PrefManager(getApplicationContext());
       checkBox.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(checkBox.isChecked()){
                   isAgent= true;
                   relativeLayout.setBackgroundColor(Color.parseColor("#BAF4511E"));
                   relativeBackground.setBackground(( getResources().getDrawable(R.drawable.back1)));
                   textHead.setText("Bank/Agent Login");
               }else {
                   isAgent = false;
                   relativeLayout.setBackgroundColor(Color.parseColor("#D200695C"));
                   relativeBackground.setBackground(( getResources().getDrawable(R.drawable.background)));
                   textHead.setText("Customer Login");
               }
           }
       });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginText.getText().toString().trim();
                String pass = passText.getText().toString().trim();
               if(isAgent) signInAgent(email, pass);
                   else signIn(email, pass);
            }
        });

        if(prf.getString("IS_LOG_IN").equals("true")){
            Intent intent = new Intent(ActivitySignIn.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }


        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }


        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private void signIn(String email, String password) {
        final String url = Config.API_URL + "signin.php?email=" + email + "&password=" + password;
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Logging In,  Please Wait!!!");
        progressDialog.show();

        if(Config.isDebug)System.out.println("URL: " + url);
        JsonObjectRequest signinRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    boolean login = response.getBoolean("login");

                    if (!login) {
                        Toast.makeText(getApplicationContext(), "Invalid Username or Password! Please Try Again", Toast.LENGTH_SHORT).show();
                    } else {
                        prf.setString("ID", response.getString("CustomerID"));
                        prf.setString("AccountNumber", response.getString("AccountNumber"));
                        prf.setString("Photo", response.getString("Photo"));
                        prf.setString("FirstName", response.getString("FirstName"));
                        prf.setString("LastName", response.getString("LastName"));
                        prf.setString("Gender", response.getString("Gender"));
                        prf.setString("DOB", response.getString("DOB"));
                        prf.setString("Phone", response.getString("Phone"));
                        prf.setString("Email", response.getString("Email"));
                        prf.setString("Address", response.getString("Address"));
                        prf.setString("IS_LOG_IN", "true");
                        // Toast.makeText(getApplicationContext(), "Welcome " + fullname ,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ActivitySignIn.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(signinRequest);
    }

    private void signInAgent(String email, String password) {
        final String url = Config.API_URL + "signinAgent.php?email=" + email + "&password=" + password;
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Logging In,  Please Wait!!!");
        progressDialog.show();

        if(Config.isDebug)System.out.println("URL: " + url);
        JsonObjectRequest signinRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    boolean login = response.getBoolean("login");

                    if (!login) {
                        Toast.makeText(getApplicationContext(), "Invalid Username or Password! Please Try Again", Toast.LENGTH_SHORT).show();
                    } else {
                        prf.setString("ID", response.getString("AdminID"));
                        prf.setString("FullName", response.getString("FullName"));
                        prf.setString("Gender", response.getString("Gender"));
                        prf.setString("Phone", response.getString("phone"));
                        prf.setString("Email", response.getString("Email"));
                        prf.setString("DOB", response.getString("dob"));
                        prf.setString("Address", response.getString("address"));
                        prf.setString("Status", response.getString("Status"));
                        prf.setString("Photo", response.getString("photo"));
                        prf.setString("IS_LOG_IN", "true");
                        Intent intent = new Intent(ActivitySignIn.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(signinRequest);
    }
}
