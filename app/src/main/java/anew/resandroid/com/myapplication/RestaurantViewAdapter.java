package anew.resandroid.com.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RestaurantViewAdapter extends RecyclerView.Adapter<RestaurantViewAdapter.ViewHolder> {

    //private ArrayList<Restaurant> restaurants;
    private Restaurant[] restaurants;

    public RestaurantViewAdapter(Restaurant[] r){
        this.restaurants = r;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cv = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.restaurant_card, viewGroup, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {
        CardView cardView = holder.cardView;

        TextView name = (TextView)cardView.findViewById(R.id.restaurantName);
        name.setText(restaurants[i].getRestaurantName());

        TextView address = (TextView) cardView.findViewById(R.id.restaurantAddress);
        address.setText(restaurants[i].getAddress());

        TextView phone = (TextView) cardView.findViewById(R.id.restaurantPhone);
        phone.setText(restaurants[i].getPhoneNumber());

        TextView price = (TextView) cardView.findViewById(R.id.restaurantPrice);
        price.setText(restaurants[i].getPricePoint());

        TextView rating = (TextView) cardView.findViewById(R.id.restaurantRating);
        rating.setText(String.valueOf(restaurants[i].getRating()));

        Button green = (Button) cardView.findViewById(R.id.yesButton);
        View.OnClickListener greenListener;
        green.setOnClickListener(greenListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurants[i].doGreenButton();
            }
        });

        Button red = (Button) cardView.findViewById(R.id.noButton);
        View.OnClickListener redListener;
        red.setOnClickListener(redListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurants[i].doRedButton();
            }
        });

    }

    @Override
    public int getItemCount() {
        return restaurants.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;

        public ViewHolder(CardView cView) {
            super(cView);
            cardView = cView;
        }
    }
}
