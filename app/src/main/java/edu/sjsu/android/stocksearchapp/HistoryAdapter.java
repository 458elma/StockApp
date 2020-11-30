package edu.sjsu.android.stocksearchapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistViewHolder> {
    private List<HistoryItem> historyItems;

    public class HistViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        public TextView dateText;
        public TextView openText;
        public TextView closeText;
        public TextView highText;
        public TextView lowText;
        public TextView volumeText;

        public HistViewHolder(View v) {
            super(v);
            layout = v;
            dateText = (TextView) v.findViewById(R.id.historyDate);
            openText = (TextView) v.findViewById(R.id.historyOpen);
            closeText = (TextView) v.findViewById(R.id.historyClose);
            highText = (TextView) v.findViewById(R.id.historyHigh);
            lowText = (TextView) v.findViewById(R.id.historyLow);
            volumeText = (TextView) v.findViewById(R.id.historyVolume);
        }
    }

    public void add(int pos, HistoryItem histItem) {
        historyItems.add(pos, histItem);
        notifyItemInserted(pos);
    }

    public void remove(int pos) {
        historyItems.remove(pos);
        notifyItemRemoved(pos);
    }

    public HistoryAdapter(List<HistoryItem> histories) {
        historyItems = histories;
    }

    @Override
    public HistoryAdapter.HistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.history_info_row, parent, false);
        HistViewHolder vh = new HistViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(HistViewHolder holder, final int position) {
        final HistoryItem hItem = historyItems.get(position);

        holder.dateText.setText(hItem.getDate());
        holder.openText.setText(hItem.getOpen());
        holder.closeText.setText(hItem.getClose());
        holder.highText.setText(hItem.getHigh());
        holder.lowText.setText(hItem.getLow());
        holder.volumeText.setText(hItem.getVolume());
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }
}
