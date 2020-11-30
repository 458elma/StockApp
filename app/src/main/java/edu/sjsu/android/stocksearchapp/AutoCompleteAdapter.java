package edu.sjsu.android.stocksearchapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private List<String> stocks;

    public AutoCompleteAdapter(Context context, int resource, List<String> stocks) {
        super(context, resource, stocks);
        this.stocks = stocks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String stock = stocks.get(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        View row = inflater.inflate(R.layout.auto_complete_view, null);

        TextView stockText = (TextView) row.findViewById(R.id.tickerSymbol);
        stockText.setText(stock);

        return row;
    }
}
