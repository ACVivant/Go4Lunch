package com.vivant.annecharlotte.go4lunch.NeSetPlusARienJeCrois;

import android.net.UrlQuerySanitizer;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Retrieve the data from the URL unsinig http url connection
 */
public class DownloadUrl {

    private final String TAG = "DownloadUrl";

    public String readTheUrl(String placeUrl) throws IOException {

        String data ="";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(placeUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line;
            while ((line=bufferedReader.readLine())!=null) {
                stringBuffer.append(line);
            }
            data = stringBuffer.toString();
            bufferedReader.close();

            Log.d(TAG, "readTheUrl: data: " + data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return data;
    }
}
