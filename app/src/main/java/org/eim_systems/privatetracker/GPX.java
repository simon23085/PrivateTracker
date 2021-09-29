package org.eim_systems.privatetracker;

import android.location.Location;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * credits to carlosefonseca
 * https://gist.github.com/carlosefonseca/6143182
 */
public class GPX {
    private static final String TAG = GPX.class.getSimpleName();

    public static void write(File file, String n, List<Location> locationList){
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        String name = "<name>" + n + "</name><trkseg>\n";

        String seg = "";
        DateFormat dateFormat = DateFormat.getDateTimeInstance(); //new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        for(Location location : locationList){
            seg += "<trkpt lat=\"" + location.getLatitude() + "\" lon=\"" + location.getLongitude() + "\"><time>" + dateFormat.format(new Date(location.getTime())) + "</time></trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        try {
            FileWriter fileWriter =  new FileWriter(file, false);
            fileWriter.append(header);
            fileWriter.append(name);
            fileWriter.append(seg);
            fileWriter.append(footer);
            fileWriter.flush();
            fileWriter.close();
            Log.d(TAG, locationList.size() + " elements saved in " + file.getName());

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
