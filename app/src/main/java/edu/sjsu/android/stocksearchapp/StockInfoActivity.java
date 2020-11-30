package edu.sjsu.android.stocksearchapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockInfoActivity extends AppCompatActivity {
    private RecyclerView recyclerCurrent;
    private RecyclerView.Adapter adapterCurrent;
    private RecyclerView.LayoutManager currentLayoutManager;
    TextView tickerTitle;

    private RecyclerView historyRecycler;
    private RecyclerView.Adapter historyAdapter;
    private RecyclerView.LayoutManager historyLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_info_layout);
        Bundle theInput = this.getIntent().getExtras();
        String currentJSON = theInput.getString("current");
        String historyJSON = theInput.getString("history");
        Log.e("current", currentJSON);
        Log.e("history", historyJSON);
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();

        tickerTitle = (TextView) findViewById(R.id.stockTitle);

        recyclerCurrent = (RecyclerView) findViewById(R.id.currentInfoRecycler);
        recyclerCurrent.setHasFixedSize(true);
        currentLayoutManager = new LinearLayoutManager(this);
        recyclerCurrent.setLayoutManager(currentLayoutManager);
        ParseCurrentJSON currentParse = new ParseCurrentJSON();
        currentParse.execute(currentJSON);

        historyRecycler = (RecyclerView) findViewById(R.id.historyRecycler);
        historyRecycler.setHasFixedSize(true);
        historyLayoutManager = new LinearLayoutManager(this);
        historyRecycler.setLayoutManager(historyLayoutManager);
        ParseHistoryJSON historyParse = new ParseHistoryJSON();
        historyParse.execute(historyJSON);
    }

    /*
    class for background task of parsing the JSON array for history info
     */
    private class ParseHistoryJSON extends AsyncTask<String, Void, List<HistoryItem>> {
        List<HistoryItem> theHistItems  = new ArrayList<HistoryItem>();

        @Override
        protected List<HistoryItem> doInBackground(String... args) {
            JSONArray histJsonArray = null;
            try {
                histJsonArray = new JSONArray(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (histJsonArray != null) {
                for (int i = 0; i < histJsonArray.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = histJsonArray.getJSONObject(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (obj != null) {
                        String inputDate = "";
                        String aDate = null;
                        try {
                            aDate = obj.getString("date");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (aDate == null) {
                            inputDate = "Date: -";
                        } else {
                            inputDate = "Date: " + aDate;
                        }

                        String inputOpen = "";
                        Double anOpen = null;
                        try {
                            anOpen = obj.getDouble("open");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (anOpen == null) {
                            inputOpen = "Open: -";
                        } else {
                            inputOpen = "Open: " + anOpen;
                        }

                        String inputClose = "";
                        Double aClose = null;
                        try {
                            aClose = obj.getDouble("close");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (aClose == null) {
                            inputClose = "Close: -";
                        } else {
                            inputClose = "Close: " + aClose;
                        }

                        String inputHigh = "";
                        Double aHigh = null;
                        try {
                            aHigh = obj.getDouble("high");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (aHigh == null) {
                            inputHigh = "High: -";
                        } else {
                            inputHigh = "High: " + aHigh;
                        }

                        String inputLow = "";
                        Double aLow = null;
                        try {
                            aLow = obj.getDouble("low");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (aLow == null) {
                            inputLow = "Low: -";
                        } else {
                            inputLow = "Low: " + aLow;
                        }

                        String inputVol = "";
                        Long aVol = null;
                        try {
                            aVol = obj.getLong("volume");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (aVol == null) {
                            inputVol = "Volume: -";
                        } else {
                            inputVol = "Volume: " + aVol;
                        }

                        HistoryItem anItem = new HistoryItem(inputDate, inputOpen, inputClose,
                                inputHigh, inputLow, inputVol);
                        theHistItems.add(anItem);
                    }
                }
            }

            return theHistItems;
        }

        @Override
        protected void onPostExecute(List<HistoryItem> outList) {
            historyAdapter = new HistoryAdapter(outList);
            historyRecycler.setAdapter(historyAdapter);
        }
    }

    /*
    class for background task of parsing the JSON array for current info
     */
    private class ParseCurrentJSON extends AsyncTask<String, Void, List<String>> {
        List<String> labeledCurrentData = new ArrayList<String>();

        @Override
        protected List<String> doInBackground(String... args) {
            /*
            Take in JSON array string as arg
            convert string to JSON arg
            take JSON object from JSON array
            parse object info into a String each
            put String into List
             */

            JSONArray currentJArray = null;
            try {
                currentJArray = new JSONArray(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (currentJArray != null) {
                JSONObject currentJObj = null;
                try {
                    currentJObj = currentJArray.getJSONObject(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (currentJObj != null) {
                    String ticker = "-";
                    try {
                        ticker = currentJObj.getString("ticker");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    labeledCurrentData.add(ticker);

                    String timestamp = null;
                    try {
                        timestamp = currentJObj.getString("timestamp");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (timestamp == null) {
                        labeledCurrentData.add("Timestamp: -");
                    } else {
                        if (timestamp.equals("null")) {
                            labeledCurrentData.add("Timestamp: -");
                        } else {
                            labeledCurrentData.add("Timestamp: " + timestamp);
                        }
                    }

                    Double bidPrice = null;
                    try {
                        bidPrice = currentJObj.getDouble("bidPrice");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bidPrice == null) {
                        labeledCurrentData.add("Bid Price: -");
                    } else {
                        labeledCurrentData.add("Bid Price: " + bidPrice);
                    }

                    Double low = null;
                    try {
                        low = currentJObj.getDouble("low");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (low == null) {
                        labeledCurrentData.add("Low: -");
                    } else {
                        labeledCurrentData.add("Low: " + low);
                    }

                    Double bidSize = null;
                    try {
                        bidSize = currentJObj.getDouble("bidSize");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bidSize == null) {
                        labeledCurrentData.add("Bid Size: -");
                    } else {
                        labeledCurrentData.add("Bid Size: " + bidSize);
                    }

                    Double prevClose = null;
                    try {
                        prevClose = currentJObj.getDouble("prevClose");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (prevClose == null) {
                        labeledCurrentData.add("Prev Close: -");
                    } else {
                        labeledCurrentData.add("Prev Close: " + prevClose);
                    }

                    String quoteTimestamp = null;
                    try {
                        quoteTimestamp = currentJObj.getString("quoteTimestamp");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (quoteTimestamp == null) {
                        labeledCurrentData.add("Quote Timestamp: -");
                    } else {
                        if (quoteTimestamp.equals("null")) {
                            labeledCurrentData.add("Quote Timestamp: -");
                        } else {
                            labeledCurrentData.add("Quote Timestamp: " + quoteTimestamp);
                        }
                    }

                    Double last = null;
                    try {
                        last = currentJObj.getDouble("last");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (last == null) {
                        labeledCurrentData.add("Last: -");
                    } else {
                        labeledCurrentData.add("Last: " + last);
                    }

                    Double askSize = null;
                    try {
                        askSize = currentJObj.getDouble("askSize");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (askSize == null) {
                        labeledCurrentData.add("Ask Size: -");
                    } else {
                        labeledCurrentData.add("Ask Size: " + askSize);
                    }

                    Long volume = null;
                    try {
                        volume = currentJObj.getLong("volume");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (volume == null) {
                        labeledCurrentData.add("Volume: -");
                    } else {
                        labeledCurrentData.add("Volume: " + volume);
                    }

                    Integer lastSize = null;
                    try {
                        lastSize = currentJObj.getInt("lastSize");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (lastSize == null) {
                        labeledCurrentData.add("Last Size: -");
                    } else {
                        labeledCurrentData.add("Last Size: " + lastSize);
                    }

                    Double high = null;
                    try {
                        high = currentJObj.getDouble("high");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (high == null) {
                        labeledCurrentData.add("High: -");
                    } else {
                        labeledCurrentData.add("High: " + high);
                    }

                    Double tngoLast = null;
                    try {
                        tngoLast = currentJObj.getDouble("tngoLast");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (tngoLast == null) {
                        labeledCurrentData.add("Tngo Last: -");
                    } else {
                        labeledCurrentData.add("Tngo Last: " + tngoLast);
                    }

                    Double askPrice = null;
                    try {
                        askPrice = currentJObj.getDouble("askPrice");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (askPrice == null) {
                        labeledCurrentData.add("Ask Price: -");
                    } else {
                        labeledCurrentData.add("Ask Price: " + askPrice);
                    }

                    Double open = null;
                    try {
                        open = currentJObj.getDouble("open");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (open == null) {
                        labeledCurrentData.add("Open: -");
                    } else {
                        labeledCurrentData.add("Open: " + open);
                    }

                    String lastSaleTimestamp = null;
                    try {
                        lastSaleTimestamp = currentJObj.getString("lastSaleTimestamp");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (lastSaleTimestamp == null) {
                            labeledCurrentData.add("Last Sale Timestamp: -");
                    } else {
                        if (lastSaleTimestamp.equals("null")) {
                            labeledCurrentData.add("Last Sale Timestamp: -");
                        } else {
                            labeledCurrentData.add("Last Sale Timestamp: " + lastSaleTimestamp);
                        }
                    }

                    Double mid = null;
                    try {
                        mid = currentJObj.getDouble("mid");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mid == null) {
                        labeledCurrentData.add("Mid: -");
                    } else {
                        labeledCurrentData.add("Mid: " + mid);
                    }
                }
            }

            return labeledCurrentData;
        }

        @Override
        protected void onPostExecute(List<String> theData) {
            tickerTitle.setText(theData.get(0));
            theData.remove(0);
            adapterCurrent = new CurrentInfoAdapter(theData);
            recyclerCurrent.setAdapter(adapterCurrent);
        }
    }
}
