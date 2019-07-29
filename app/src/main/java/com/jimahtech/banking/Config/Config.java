package com.jimahtech.banking.Config;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import Zenoph.SMSLib.Enums.MSGTYPE;
import Zenoph.SMSLib.SMSException;
import Zenoph.SMSLib.ZenophSMS;

/**
 * Created by Chief on 2017-10-28.
 */

public class Config {

    public static final String API_URL = "http://photo.ro-mach.com/susu/api/";
    public static final String IMAGES_URL = "http://photo.ro-mach.com/susu";
    public static final String SMS_USER = "web.fransms@gmail.com";
    public static final String SMS_PASS = "web.fransms1";
    public static boolean isDebug = true;


    public static String imageToBase64(Bitmap bitmap){
        //converting image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    public static Bitmap base64ToImage(String encodedImage){
        byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean sendSMS(String msg,String ID,String number){
        try {
            ZenophSMS sms = new ZenophSMS();
            sms.setUser(SMS_USER);
            sms.setPassword(SMS_PASS);
            sms.authenticate();
            sms.setMessage(msg);
            sms.setMessageType(MSGTYPE.TEXT);
            sms.setSenderId(ID);
            sms.addRecipient(number, true);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return false;
        }
        return true;
    }
}
