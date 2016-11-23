package com.seng480b.bumerang.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneNumberUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.seng480b.bumerang.R;
import com.seng480b.bumerang.models.Request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Utility {

    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isEmpty(EditText eText) {
        return editTextToString(eText).length() == 0;
    }

    public static void showRequestErrorDialog(Context context) {
        longToast(context, R.string.unable_to_display_requests);
    }

    public static void longToast(Context context, int resourceId) {
        Toast.makeText(context, resourceId, Toast.LENGTH_LONG).show();
    }

    public static void longToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void karmaToast(Context context, int karma) {
        Toast.makeText(context, "+"+karma+" Karma", Toast.LENGTH_SHORT).show();
    }

    public static String editTextToString(EditText eText) {
        return eText.getText().toString().trim();
    }

    public static String formatPhoneNumber(String phoneNumber) {
        return PhoneNumberUtils.formatNumber(phoneNumber, "US");
    }


    public static Request.RequestType getCurrentRequestType(ViewPager pager) {
        int currentTab = pager.getCurrentItem();
        Request.RequestType requestType;
        switch(currentTab) {
            case 0:
                requestType = Request.RequestType.BORROW;
                break;
            case 1:
                requestType = Request.RequestType.LEND;
                break;
            default:
                requestType = null;
        }
        return requestType;
    }
}
