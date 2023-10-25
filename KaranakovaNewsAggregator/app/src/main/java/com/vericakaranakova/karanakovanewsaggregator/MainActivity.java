package com.vericakaranakova.karanakovanewsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.vericakaranakova.karanakovanewsaggregator.databinding.ActivityMainBinding;

import com.vericakaranakova.karanakovanewsaggregator.databinding.ActivityMainBinding;
import com.vericakaranakova.karanakovanewsaggregator.databinding.DrawerListItemBinding;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private HashMap<String, ArrayList<Sources>> sourcesData = new HashMap<>();
    private HashMap<String, Integer> colors = new HashMap<>();
    public static ArrayList<Articles> articles = new ArrayList<>();
    public static ArrayList<Sources> sources = new ArrayList<>();
    public ArrayList<SpannableString> colorListIn = new ArrayList<>();
    public String category;
    public int page;
    private ArrayAdapter<SpannableString> arrayAdapter;
    private static Menu mmenu;

    private ActivityMainBinding binding;

    private ViewPager2 viewPager2;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private static MainAdapter mainAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDrawerLayout = binding.drawerLayout;
        mDrawerList = binding.leftDrawer;
        viewPager2 = binding.viewPager;


        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> selectItem(position)
        );

        // shows the drawerToggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                /* host Activity */
                mDrawerLayout,             /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        mainAdapter = new MainAdapter(this, articles);
        viewPager2.setAdapter(mainAdapter);

        new Thread(new NewsDownloader(this)).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (articles.size() > 0) {
            outState.putBoolean("flag", true);
            outState.putString("title", (String) getTitle());
            outState.putInt("page", viewPager2.getCurrentItem());
            outState.putSerializable("sd", sourcesData);
            outState.putSerializable("colors", colors);
            outState.putSerializable("collist", colorListIn);
        } else {
            outState.putBoolean("flag", false);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean("flag")) {
            viewPager2.setBackgroundColor(Color.WHITE);
            page = savedInstanceState.getInt("page");
            sourcesData = (HashMap<String, ArrayList<Sources>>) savedInstanceState.get("sd");
            colors = (HashMap<String, Integer>) savedInstanceState.get("colors");
            colorListIn = (ArrayList<SpannableString>) savedInstanceState.getSerializable("collist");
            String source = savedInstanceState.getString("title");
            setTitle(source);

            for (int i = 0; i < sources.size(); i++) {
                if (sources.get(i).getName().equals(source)) {
                    selectItem(i);
                    break;
                }
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    public void updateData(ArrayList<Sources> listIN) {
        if (sourcesData.isEmpty()) {
            for (Sources source : listIN) {
                if (!sourcesData.containsKey(source.getCategory())) {
                    sourcesData.put(source.getCategory(), new ArrayList<>());
                }
                ArrayList<Sources> slist = sourcesData.get(source.getCategory());//
                if (slist != null) {//
                    sourcesData.get(source.getCategory()).add(source);
                }
            }

            sourcesData.put("All", listIN);

            ArrayList<String> tempList = new ArrayList<>(sourcesData.keySet());
            Collections.sort(tempList);
            setColors(tempList);
            for (int i = 0; i < tempList.size(); i++) {
                mmenu.add(tempList.get(i));
                if (colors.containsKey(tempList.get(i)) && i > 0) {
                    MenuItem item = mmenu.getItem(i);
                    SpannableString spannableString = new SpannableString(item.getTitle());
                    spannableString.setSpan(new ForegroundColorSpan(colors.get(tempList.get(i))), 0, spannableString.length(), 0);
                    item.setTitle(spannableString);
                }
            }

            sources.addAll(listIN);
            setSpannibles(listIN);

            arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, colorListIn);
            mDrawerList.setAdapter(arrayAdapter);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }

            binding.progressBar.setVisibility(View.GONE);
            setTitle("News Gateway (" + sourcesData.get("All").size() + ")");
        } else {
            ArrayList<String> tempList = new ArrayList<>(sourcesData.keySet());
            Collections.sort(tempList);
            for (int i = 0; i < tempList.size(); i++) {
                mmenu.add(tempList.get(i));
                if (colors.containsKey(tempList.get(i)) && i > 0) {
                    MenuItem item = mmenu.getItem(i);
                    SpannableString spannableString = new SpannableString(item.getTitle());
                    spannableString.setSpan(new ForegroundColorSpan(colors.get(tempList.get(i))), 0, spannableString.length(), 0);
                    item.setTitle(spannableString);
                }
            }

            arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, colorListIn);
            mDrawerList.setAdapter(arrayAdapter);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void downloadFailed() {
        Toast.makeText(this, "Download Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mmenu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        sources.clear();
        colorListIn.clear();
        category = item.getTitle().toString();
        ArrayList<Sources> s = sourcesData.get(item.getTitle().toString());
        if (s != null) {
            sources.addAll(s);
            setSpannibles(s);
        }

        if (((String) getTitle()).contains("News Gateway")) {
            setTitle("News Gateway (" + sources.size() + ")");
        }
        arrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @SuppressLint("ResourceAsColor")
    private void selectItem(int position) {
        viewPager2.setBackgroundColor(Color.WHITE);
        String selectedSource = sources.get(position).getName();
        setTitle(selectedSource);

        String ss = sources.get(position).getId();

        NewsDownloader.downloadViaSource(this, ss);

        if (articles == null) {
            Toast.makeText(this,
                    MessageFormat.format("No articles found for {0}", selectedSource),
                    Toast.LENGTH_LONG).show();
            return;
        }

        viewPager2.setCurrentItem(0);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void updateArticles(ArrayList<Articles> a) {
        articles.clear();
        articles.addAll(a);
        Log.d(TAG, "updateArticles: " + a);
        mainAdapter.notifyDataSetChanged();
    }

    public void setSpannibles(ArrayList<Sources> listIN) {
        for (Sources source : listIN) {
            SpannableString ss = new SpannableString(source.getName());
            ss.setSpan(new ForegroundColorSpan(colors.get(source.getCategory())), 0, ss.length(), 0);
            colorListIn.add(ss);
        }
    }

    public void setColors(ArrayList<String> categories) {
        for (String keyCat : categories) {
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            int color = (0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);

            while (colors.containsValue(color)) {
                r = (int) (Math.random() * 255);
                g = (int) (Math.random() * 255);
                b = (int) (Math.random() * 255);
                color = (0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
            }
            colors.put(keyCat, color);
        }
    }
}