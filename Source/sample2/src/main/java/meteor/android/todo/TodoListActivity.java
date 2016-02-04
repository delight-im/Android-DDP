package meteor.android.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;

/**
 * Created by Sadmansamee on 2/3/16.
 */
public class TodoListActivity extends AppCompatActivity implements MeteorCallback {

    public Map<String, Lists> listHashMap
            = new HashMap<>();
    ListsBaseAdapter listsBaseAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//set Call back to this activity
        MeteorSingleton.getInstance().setCallback(this);

        //Subscribe to privateLists , only works when logged in you can also subscribe "publicLists" without login
        MeteorSingleton.getInstance().subscribe("publicLists");

        listView = (ListView) findViewById(R.id.listView);

        listsBaseAdapter = new ListsBaseAdapter(this, listHashMap);
        listView.setAdapter(listsBaseAdapter);

    }


    //UnSet callback
    @Override
    protected void onDestroy() {
        MeteorSingleton.getInstance().unsetCallback(this);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        //getMenuInflater().inflate(R.menu.menu_delivery_boy, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_logout:

                MeteorSingleton.getInstance().unsetCallback(this);

                //logout here
                MeteorSingleton.getInstance().logout();

                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //Eventbus
    // This method will be called when a MessageEvent is posted
    public void onEvent(MeteorEvent event) {

        Log.d("onEvent", "listHashMap " + listHashMap.size());
        listsBaseAdapter = new ListsBaseAdapter(this, listHashMap);
        listView.setAdapter(listsBaseAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    //MeteorCallback
    @Override
    public void onConnect(boolean b) {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onDataAdded(String s, String s1, String s2) {
        //here String collectionName, String documentID, String fieldsJson
        Log.d("onDataAdded", "" + s);
        //lists is collection name
        if (s.contains("lists")) {

            ObjectMapper mapper = new ObjectMapper();
            try {

                //create Object class and put it on
                Lists lists = mapper.readValue(s2, Lists.class);
                listHashMap.put(s1, lists);
                Log.d("onDataAdded", "listHashMap " + listHashMap.size());

                //when this event happens lets fire an event so that listviews dataset can be updated
                EventBus.getDefault().post(new MeteorEvent("onDataAdded!"));
            } catch (IOException e) {

                e.printStackTrace();
            }

        }


    }

    @Override
    public void onDataChanged(String s, String s1, String s2, String s3) {


        if (s.contains("lists")) {

            ObjectMapper mapper = new ObjectMapper();
            try {
                Lists lists = mapper.readValue(s2, Lists.class);
                listHashMap.put(s1, lists);
                Log.d("onDataAdded", "listHashMap " + listHashMap.size());

                //when this event happens lets fire an event so that listviews dataset can be updated
                EventBus.getDefault().post(new MeteorEvent("onDataAdded!"));
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDataRemoved(String s, String s1) {
        if (s.contains("lists")) {
            listHashMap.remove(s1);

            //when this event happens lets fire an event so that listviews dataset can be updated
            EventBus.getDefault().post(new MeteorEvent("onDataAdded!"));
        }
    }
}
