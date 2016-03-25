package com.qtfreet.musicuu.utils;

import android.content.Context;
import android.util.Log;

import com.qtfreet.musicuu.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qtfreet on 2016/3/20.
 */
public class util {




    public static long getIntTime(String time) {

        Log.e("TAG1",time+"   1111");
        if(time.equals("")){
            return 0;
        }

        Log.e("TAG",time);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        Date date = null;
        Date sDate = null;
        try {
            date = format.parse(time);
            sDate = format.parse("00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (date.getTime() - sDate.getTime()) / 1000;
    }
}
