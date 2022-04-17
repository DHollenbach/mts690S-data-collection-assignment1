package com.cput.datacollection.reporting;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Utils {
    private static final int REQUEST_CODE_SHOW_RESPONSE_TEXT = 1;
    private static final String KEY_RESPONSE_TEXT = "KEY_RESPONSE_TEXT";
    private static final String KEY_RESPONSE_URL = "KEY_RESPONSE_URL";
    private static final String TAG_HTTP_URL_CONNECTION = "HTTP_URL_CONNECTION";

    static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    static void downloadAndSaveHtml(List<LinksToDownload> data, Handler uiUpdater, DataCollectionDBHelper dbDataCollection, Integer number) {
        Thread sendHttpRequestThread = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < data.size(); i++) {
                    for (int j = 0; j < data.get(i).data.size(); j++) {
                        Utils.downloadHtml(data.get(i).data.get(j).website, dbDataCollection, number);
                    }
                }

                // Send message to main thread to update response text in TextView after read all.
                Message message = new Message();
                // Set message type.
                message.what = REQUEST_CODE_SHOW_RESPONSE_TEXT;
                // Create a bundle object.
                Bundle bundle = new Bundle();
                // Put response text in the bundle with the special key.
                //bundle.putString(KEY_RESPONSE_TEXT, readTextBuf.toString());
                //bundle.putString(KEY_RESPONSE_URL, urlToGet);
                bundle.putString(KEY_RESPONSE_TEXT, "Done with download*****");
                // Set bundle data in message.
                message.setData(bundle);
                // Send message to main thread Handler to process.
                uiUpdater.sendMessage(message);
            }
        };

        // Start the child thread to request web page.
        sendHttpRequestThread.start();
    }

    static String downloadHtml(String link, DataCollectionDBHelper dbDataCollection, Integer number)  {
        // Maintain http url connection.
        HttpURLConnection httpConn = null;
        // Read text input stream.
        InputStreamReader isReader = null;
        // Read text into buffer.
        BufferedReader bufReader = null;
        // Save server response text.
        StringBuffer readTextBuf = new StringBuffer();
        // Create a URL object use page url.
        String urlToGet = "https://" + link;

        try {
            URL url = new URL(urlToGet);
            // Open http connection to web server.
            httpConn = (HttpURLConnection)url.openConnection();
            // Set http request method to get.
            httpConn.setRequestMethod("GET");
            // Set connection timeout and read timeout value.
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            // Get input stream from web url connection.
            InputStream inputStream = httpConn.getInputStream();
            // Create input stream reader based on url connection input stream.
            isReader = new InputStreamReader(inputStream);
            // Create buffered reader.
            bufReader = new BufferedReader(isReader);
            // Read line of text from server response.
            String line = bufReader.readLine();
            // Loop while return line is not null.
            while(line != null)
            {
                // Append the text to string buffer.
                readTextBuf.append(line);
                // Continue to read text line.
                line = bufReader.readLine();
            }

            dbDataCollection.addNewCollectedData(number, link, readTextBuf.toString());
            Log.i("Saving to the DB:", link + " " + number);
        }catch(MalformedURLException ex)
        {
            Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
        }catch(IOException ex)
        {
            Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
        }finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                    bufReader = null;
                }
                if (isReader != null) {
                    isReader.close();
                    isReader = null;
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                    httpConn = null;
                }
            }catch (IOException ex)
            {
                Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
            }
        }

        return "";
    }
}
