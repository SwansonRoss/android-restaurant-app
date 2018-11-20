package anew.resandroid.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private  RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_results);

        ImageButton back = findViewById(R.id.back_to_search);
        View.OnClickListener backButtonListener;
        back.setOnClickListener(backButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToSearch = new Intent(ResultsActivity.this, SearchActivity.class);
                startActivity(backToSearch);
            }
        });

        ArrayList<Restaurant> restaurantArrayList;

        Intent intent = getIntent();
        restaurantArrayList = intent.getParcelableArrayListExtra("restaurants");

        if(restaurantArrayList.size() == 0)
        {
            String error = "Sorry, we couldn't find any reuslts.";
            Toast toast = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT);
            toast.show();

            Intent backToSearch = new Intent(ResultsActivity.this, SearchActivity.class);
            startActivity(backToSearch);
        }

        Restaurant[] restaurantsArray = new Restaurant[restaurantArrayList.size()];
        for(int i = 0; i < restaurantArrayList.size(); i++) {
            restaurantsArray[i] = restaurantArrayList.get(i);
        }


        mRecyclerView = findViewById(R.id.restaurant_recycler);

        if (mRecyclerView != null) {
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new RestaurantViewAdapter(restaurantsArray);
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            String error = "There was an error loading the results. Sorry.";
            Toast toast = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT);
            toast.show();

            Intent backToSearch = new Intent(ResultsActivity.this, SearchActivity.class);
            startActivity(backToSearch);
        }

    }
}
