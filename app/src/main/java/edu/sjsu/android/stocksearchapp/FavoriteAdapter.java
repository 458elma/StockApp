package edu.sjsu.android.stocksearchapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavViewHolder> {
    private List<FavoriteItem> favItems;

    public class FavViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        public TextView favTickerView;
        public TextView favPriceView;
        public Button favPercentageView;

        public FavViewHolder(View v) {
            super(v);
            layout = v;
            favTickerView = (TextView) v.findViewById(R.id.favTicker);
            favPriceView = (TextView) v.findViewById(R.id.favLastPrice);
            favPercentageView = (Button) v.findViewById(R.id.favPercentage);
        }
    }

    public void add(int pos, FavoriteItem favItem) {
        favItems.add(pos, favItem);
        notifyItemInserted(pos);
    }

    public void remove(int pos) {
        favItems.remove(pos);
        notifyItemRemoved(pos);
    }

    public FavoriteAdapter(List<FavoriteItem> theItems) {
        favItems = theItems;
    }

    @Override
    public FavoriteAdapter.FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.favorite_row, parent, false);
        FavViewHolder vh = new FavViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FavViewHolder holder, final int position) {
        final FavoriteItem theFavItem = favItems.get(position);

        holder.favTickerView.setText(theFavItem.getTickerFav());
        String aLastPrice = "$" + theFavItem.getLastPriceFav();
        holder.favPriceView.setText(aLastPrice);
        double aFavPercent = theFavItem.getPercentageFav();
        if (aFavPercent < 0) {
            String negPerc = aFavPercent + "";
            holder.favPercentageView.setBackgroundColor(Color.RED);
            holder.favPercentageView.setText(negPerc);
        } else {
            String posPerc = "+" + aFavPercent + "";
            holder.favPercentageView.setBackgroundColor(Color.GREEN);
            holder.favPercentageView.setText(posPerc);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent favIntent = new Intent(v.getContext(), StockInfoActivity.class);
                Bundle someJSON = new Bundle();
                someJSON.putString("current", theFavItem.getCurrentJSONFav());
                someJSON.putString("history", theFavItem.getHistoryJSONFav());
                favIntent.putExtras(someJSON);
                v.getContext().startActivity(favIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favItems.size();
    }
}
