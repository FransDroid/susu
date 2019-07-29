package com.jimahtech.banking.activities.bank;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.manager.PrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateAgentActivity extends AppCompatActivity {
    private static final String TAG = UpdateAgentActivity.class.getSimpleName();
    EditText fname, lname, dob, address, phone, email;
    Button register, btnImage;
    Spinner spinnerGender;
    ImageView imageView;
    String[] data = {"Male", "Female"};
    String gender;
    Calendar calendar;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    private PrefManager prf;
    View focusView = null;
    boolean cancel = false;
    Bitmap bitmap = null;
    private int GALLERY = 1, CAMERA = 2;
    String id = null;
    String name = null;
    String emaill = null;
    String genderl = null;
    String dobl = null;
    String addressl = null;
    String phonel = null;
    String photol = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_agent);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle("UPDATE AGENT");

        Bundle extra = getIntent().getExtras();
        try {
            id = extra.getString("id");
            name = extra.getString("name");
            emaill = extra.getString("email");
            genderl = extra.getString("gender");
            dobl = extra.getString("dob");
            addressl = extra.getString("address");
            phonel = extra.getString("phone");
            photol = extra.getString("photo");
        } catch (Exception e) {
            if(Config.isDebug)System.out.println(e.toString());
        }
        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        prf = new PrefManager(this);
        requestQueue = Volley.newRequestQueue(this);
        fname = findViewById(R.id.fname);
        dob = findViewById(R.id.dob);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        register = findViewById(R.id.register);
        btnImage = findViewById(R.id.buttonChoose);
        imageView = findViewById(R.id.imageView);


        fname.setText(name);
        email.setText(emaill);
        dob.setText(dobl);
        address.setText(addressl);
        phone.setText(phonel);
        Picasso.with(getApplicationContext()).load(Config.IMAGES_URL + photol).placeholder(R.drawable.header).into(imageView);
        if(Config.isDebug)System.out.println("Pic:" +Config.IMAGES_URL + photol );
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updatedate();
            }

        };

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_selected, data);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.spinnerGender);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReadStoragePermissionGranted()) {
                    showPictureDialog();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UpdateAgentActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void updatedate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY && resultCode == RESULT_OK && data != null) {
            Uri filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == CAMERA) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            //  saveImage(thumbnail);
            //Toast.makeText(ShadiRegistrationPart5.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public boolean isReadStoragePermissionGranted() {
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                Log.v(TAG, "Permission is granted1");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted1");
            return true;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted2");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    showPictureDialog();
                } else {
                    //progress.dismiss();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    showPictureDialog();
                } else {
                    // progress.dismiss();
                }
                break;
        }
    }


    public String getStringImage(Bitmap bm) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }

    public void saveData() {
        String fna = fname.getEditableText().toString().trim();
        String dobb = dob.getEditableText().toString().trim();
        String add = address.getEditableText().toString().trim();
        String fone = phone.getEditableText().toString().trim();
        String eml = email.getEditableText().toString().trim();

        if (TextUtils.isEmpty(fna)) {
            fname.setError("Full Name Required!");
            focusView = fname;
            cancel = true;
        } else if (TextUtils.isEmpty(dobb)) {
            dob.setError("Date Of Birth Required!");
            focusView = dob;
            cancel = true;
        } else if (TextUtils.isEmpty(add)) {
            address.setError("Address Required!");
            focusView = address;
            cancel = true;
        } else if (TextUtils.isEmpty(fone)) {
            phone.setError("Phone Number Required!");
            focusView = phone;
            cancel = true;
        } else if (TextUtils.isEmpty(fone)) {
            phone.setError("Phone cannot be empty!");
            focusView = phone;
            cancel = true;
        } else if (fone.length() > 12) {
            phone.setError("Phone number too long!");
            focusView = phone;
            cancel = true;
        } else if (TextUtils.isEmpty(eml)) {
            email.setError("E-mail Address Required!");
            focusView = email;
            cancel = true;
        } else if (!Config.isValidEmail(eml)) {
            email.setError("E-mail Address Invalid!");
            focusView = email;
            cancel = true;
        } else {
            registerAgent(id, fna, gender, dobb, add, fone, eml);
        }

    }

    public void ClearEntry() {
        fname.setText("");
        dob.setText("");
        address.setText("");
        phone.setText("");
        email.setText("");
    }

    private void registerAgent(final String id, final String fname, final String gender, final String dob, final String add, final String fone, final String eml) {
        final String url = Config.API_URL + "updateAgent.php";
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Processing Transaction,  Please Wait!!!");
        progressDialog.show();

        if(Config.isDebug)System.out.println("URL: " + url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(String.valueOf(response));
                            boolean success = obj.getBoolean("Success");
                            String msg = obj.getString("msg");
                            if (success) {
                                progressDialog.dismiss();
                                if(Config.isDebug)System.out.println("ERROE"+ response);
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                ClearEntry();
                                Intent intent = new Intent(UpdateAgentActivity.this, BankRegisterAgent.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                            }else {
                                progressDialog.dismiss();
                                if(Config.isDebug)System.out.println("ERROE"+ response);
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            if(Config.isDebug)System.out.println(e.toString());
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                String image;
                if(bitmap == null){
                    Bitmap bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    image = Config.imageToBase64(bmp);
                }else {
                     image = Config.imageToBase64(bitmap);
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("fname", fname);
                params.put("gender", gender);
                params.put("dob", dob);
                params.put("phone", fone);
                params.put("email", eml);
                params.put("address", add);
                params.put("image", image);
                return params;
            }
        };
        requestQueue.add(postRequest);
    }
}
