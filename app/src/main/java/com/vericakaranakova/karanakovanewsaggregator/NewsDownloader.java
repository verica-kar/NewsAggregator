package com.vericakaranakova.karanakovanewsaggregator;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NewsDownloader implements Runnable{

    private static final String TAG = "NewsDownloader";

    public static MainActivity mainActivity;
    public static String source;
    public static RequestQueue queue;

    public static ArrayList<Articles> articles = new ArrayList<>();
    public static ArrayList<Sources> sources = new ArrayList<>();

    public static final String apiKey = "fcd58456564b459da56c2d7f0e778f14";
    public static final String sourceURL = "https://newsapi.org/v2/sources";
    public static final String articleURL = "https://newsapi.org/v2/top-headlines";

    NewsDownloader(MainActivity ma) { mainActivity = ma; }

    @Override
    public void run() {
        try {
            downloadSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadViaSource(MainActivity mainActivityIn, String s) {
        mainActivity = mainActivityIn;
        source = s;
        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder builder = Uri.parse(articleURL).buildUpon();
        builder.appendQueryParameter("sources", source);
        builder.appendQueryParameter("apiKey", apiKey);
        String articleUrlToUse = builder.build().toString();

        Log.d(TAG, "downloadViaSource: " + articleUrlToUse);

        Response.Listener<JSONObject> listener =
                response -> mainActivity.runOnUiThread(() -> mainActivity.updateArticles(parseArticleJSON(response.toString())));

        Response.ErrorListener error =
                error1 -> mainActivity.runOnUiThread(mainActivity::downloadFailed);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, articleUrlToUse,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "News-App");
                        return headers;
                    }
                };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public void downloadSource() {
        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(sourceURL).buildUpon();
        buildURL.appendQueryParameter("apiKey", apiKey);
        String sourceUrlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> mainActivity.runOnUiThread(() -> mainActivity.updateData(parseSourceJSON(response.toString())));

        Response.ErrorListener error =
                error1 -> mainActivity.runOnUiThread(mainActivity::downloadFailed);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, sourceUrlToUse,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "News-App");
                        return headers;
                    }
                };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public static ArrayList<Articles> parseArticleJSON(String s){
        articles.clear();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("articles");
            for (int i = 0; i < 10; i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);

                    String author;
                    try {
                        author = object.getString("author");
                        if (author == null) {
                            author = "null";
                        }
                    } catch (Exception e) {
                        author = "null";
                    }

                    String title;
                    try {
                        title = object.getString("title");
                        if (title == null) {
                            title = "null";
                        }
                    } catch (Exception e) {
                        title = "null";
                    }

                    String description;
                    try {
                        description = object.getString("description");
                        if (description == null) {
                            description = "null";
                        }
                    } catch (Exception e) {
                        description = "null";
                    }

                    String url;
                    try {
                        url = object.getString("url");
                        if (url == null) {
                            url = "null";
                        }
                    } catch (Exception e) {
                        url = "null";
                    }

                    String urlToImage;
                    try {
                        urlToImage = object.getString("urlToImage");
                        if (urlToImage == null) {
                            urlToImage = "null";
                        }
                    } catch (Exception e) {
                        urlToImage = "null";
                    }

                    String publishedAt;
                    try {
                        publishedAt = object.getString("publishedAt");
                        if (publishedAt == null) {
                            publishedAt = "null";
                        }
                    } catch (Exception e) {
                        publishedAt = "null";
                    }

                    articles.add(new Articles(author, title, description, url, urlToImage, publishedAt));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Sources> parseSourceJSON(String s){
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("sources");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject source = jsonArray.getJSONObject(i);
                    String id = source.getString("id");
                    String name = source.getString("name");
                    String category = source.getString("category");

                    sources.add(new Sources(id, name, category));
                }
            }
            Collections.sort(sources);
            return sources;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
