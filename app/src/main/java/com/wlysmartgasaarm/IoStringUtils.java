package com.wlysmartgasaarm;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Base64;

@RequiresApi(api = Build.VERSION_CODES.O)
public class IoStringUtils {

    public static String convertOrder(String order) {
        return Base64.getEncoder().encodeToString(order.getBytes());
    }

}