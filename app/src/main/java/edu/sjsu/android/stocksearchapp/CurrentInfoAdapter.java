package edu.sjsu.android.stocksearchapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrentInfoAdapter extends RecyclerView.Adapter<CurrentInfoAdapter.ViewHolder> {
    private List<String> currentInfos;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        public TextView labelAndData;

        public ViewHolder(View view) {
            super(view);
            layout = view;
            labelAndData = (TextView) view.findViewById(R.id.theLabelAndData);
        }
    }

    public void add(int pos, String string) {
        currentInfos.add(pos, string);
        notifyItemInserted(pos);
    }

    public void remove(int pos) {
        currentInfos.remove(pos);
        notifyItemRemoved(pos);
    }

    public CurrentInfoAdapter(List<String> theStrings) {
        currentInfos = theStrings;
    }

    @Override
    public CurrentInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.current_info_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String labelData = currentInfos.get(position);
        holder.labelAndData.setText(labelData);
    }

    @Override
    public int getItemCount() {
        return currentInfos.size();
    }
}
