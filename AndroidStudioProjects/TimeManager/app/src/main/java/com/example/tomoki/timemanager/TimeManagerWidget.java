package com.example.tomoki.timemanager;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class TimeManagerWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Calendar calendar = Calendar.getInstance();
        String year=String.valueOf(calendar.get(Calendar.YEAR));
        String month=String.valueOf(calendar.get(Calendar.MONTH)+1);
        if (month.length()==1){
            month="0"+month;
        }
        String date=String.valueOf(calendar.get(Calendar.DATE));
        if (date.length() == 1) {
            date = "0" + date;
        }
        DatabaseHelper db = new DatabaseHelper(context);
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        cursor=sqLiteDatabase.rawQuery("SELECT * FROM timedb WHERE (year = '"+year+"' AND month = '"+month+"' AND date = '"+date+"')",null);
        Log.d("year", year);
        Log.d("month", month);
        Log.d("date", date);
        cursor.moveToFirst();
        Log.d("cursor", String.valueOf(cursor.getCount()));

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_manager_widget);
        if (cursor.getCount()==0){
            views.setTextViewText(R.id.appwidget_text, "今日の労働時間\n未登録");
        }else {
            int time = cursor.getInt(7);
            views.setTextViewText(R.id.appwidget_text, "今日の労働時間\n" + time / 60 + "時間" + time % 60 + "分");
        }





        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

