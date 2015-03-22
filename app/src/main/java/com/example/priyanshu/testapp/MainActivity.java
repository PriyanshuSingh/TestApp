package com.example.priyanshu.testapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mobile.services.cloudcode.IBMCloudCode;
import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String CLASS_NAME = "MainActivity";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    ArrayList<Item> itemList;
    TextView display;
    Button sendBtn,recvBtn;
    char a;
    HashMap<String, Integer> b;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        //File file = new File(this.getFilesDir(),"requests");
//
//        if(file.exists()){
//            try {
//                FileReader r= new FileReader(file);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        IBMBluemix.initialize(this,"6bcef7fb-eed0-4263-b1e5-b09e313f00ed","fa0c39a356850474078dde994b6c10b070051f02","http://test-tgz-app.mybluemix.net");
        IBMCloudCode.initializeService();
        IBMData.initializeService();
        Item.registerSpecialization(Item.class);


        display = (TextView) findViewById(R.id.textItem);
        sendBtn = (Button) findViewById(R.id.send);
        recvBtn = (Button) findViewById(R.id.receive);
        a = 'a';
        b = new HashMap<String, Integer>();
        load();
        itemList = new ArrayList<Item>();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createItem("1"+a);
             //   display.setText(a);
            }
        });
        recvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItems();
                String str = "";
                for (Item item:itemList){
                    str += item.getName();
                }
                display.setText(str);
            }
        });

    }

    public void load(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("aaa",1);
        editor.commit();
        for(String s:sharedPref.getAll().keySet()){
            Log.i(CLASS_NAME,"kadjfhkdjf::::" + s);
            b.put(s, new Integer(sharedPref.getInt(s,0)));
        }
    }

    public void save(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (String key: b.keySet()){
            editor.putInt(key,b.get(key).intValue());
        }
        editor.commit();
    }
    public void listItems() {

        try{
            IBMQuery<Item> query = IBMQuery.queryForClass(Item.class);
            query.find().continueWith(new Continuation<List<Item>, Object>() {
                @Override
                public Object then(Task<List<Item>> listTask) throws Exception {
                    final List<Item> objects = listTask.getResult();
                    if(listTask.isCancelled()){
                        Log.e(CLASS_NAME,"Exception : Task " + listTask.toString() + " was cancelled.");
                    }else if(listTask.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + listTask.getError().getMessage());
                    }else{
                        itemList.clear();
                        for (IBMDataObject item:objects){
                            itemList.add((Item) item);
                        }
                    }
                    return null;
                }
            },Task.UI_THREAD_EXECUTOR);
        } catch (IBMDataException e) {
            e.printStackTrace();
        }
    }

    public void createItem(String str){
        if(b.get(str) != null && b.get(str).intValue() != 0 ) {
            Toast sid = Toast.makeText(this, "Abhishek khush ho ja", Toast.LENGTH_SHORT);
            sid.show();
            //Log.i(CLASS_NAME,"calling createItem and str = "+ str + b.get(str).intValue());
            return;
        }
        Item item = new Item();
        if(!str.equals("")){
            item.setName(str);
            if(b.get(str) != null)return;
            item.save().continueWith(new Continuation<IBMDataObject, Object>() {
                @Override
                public Object then(Task<IBMDataObject> ibmDataObjectTask) throws Exception {
                    if(ibmDataObjectTask.isCancelled()){
                        Log.e(CLASS_NAME, "Exception : Task " + ibmDataObjectTask.toString() + " was cancelled.");
                    }else if(ibmDataObjectTask.isFaulted()){
                        Log.e(CLASS_NAME, "Exception : " + ibmDataObjectTask.getError().getMessage());
                    }else{
                        listItems();
                    }
                    return null;
                }
            });
        }
        b.put(str,1);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
