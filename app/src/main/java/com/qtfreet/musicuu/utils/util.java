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


    public static String music_type(Context con, int postion) {
        String[] lan = con.getResources().getStringArray(R.array.type);
        String type = lan[postion];
        if (type.equals(lan[0])) {
            type = "wy";
        } else if (type.equals(lan[1])) {
            type = "dx";
        } else if (type.equals(lan[2])) {
            type = "fs";
        } else if (type.equals(lan[3])) {
            type = "bd";
        } else if (type.equals(lan[4])) {
            type = "tt";
        } else if (type.equals(lan[5])) {
            type = "xm";
        } else if (type.equals(lan[6])) {
            type = "kw";
        } else if (type.equals(lan[7])) {
            type = "kg";
        } else if (type.equals(lan[8])) {
            type = "dm";
        } else if (type.equals(lan[9])) {
            type = "mf";
        } else if (type.equals(lan[10])) {
            type = "echo";
        } else if (type.equals(lan[11])) {
            type = "qq";
        }


        return type;

    }

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
