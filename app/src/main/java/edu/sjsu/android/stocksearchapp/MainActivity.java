package edu.sjsu.android.stocksearchapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView typeStock;
    SwitchCompat autoRefresher;
    ImageButton refresh;
    Context mainContext;
    ArrayAdapter<String> adapter;
    SharedPreferences sharedFavorites;

    private RecyclerView favoriteRecycler;
    private FavoriteAdapter favAdapter;
    private RecyclerView.LayoutManager favLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = this;
        sharedFavorites = getSharedPreferences("Favorite List", MODE_PRIVATE);

        favoriteRecycler = (RecyclerView) findViewById(R.id.stockRecyclerView);
        favoriteRecycler.setHasFixedSize(true);
        favLayoutManager = new LinearLayoutManager(this);
        favoriteRecycler.setLayoutManager(favLayoutManager);
        List<FavoriteItem> allFavItems = new ArrayList<FavoriteItem>();
        favAdapter = new FavoriteAdapter(allFavItems);
        favoriteRecycler.setAdapter(favAdapter);
        /*
        add the saved favorites from shared preference here
        read from the shared preference and load onto the adapter
         */

        typeStock = (AutoCompleteTextView) findViewById(R.id.stockAutoComplete);
        List<String> stocks = new ArrayList<String>();
        /*stocks.add("AAPL");
        stocks.add("RACE");
        stocks.add("AAPA");
        stocks.add("RACP");
        stocks.add("AAPQ");
        stocks.add("RACV");
        stocks.add("QWER");
        stocks.add("QWET");
        stocks.add("POLE");*/
        /*AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,
                R.layout.auto_complete_view, stocks);*/
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, stocks);


        typeStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String theSymbol = typeStock.getText().toString();
                if (theSymbol.length() >= typeStock.getThreshold()) {
                    SetAutocompleteAdapterClass autoAdapt = new SetAutocompleteAdapterClass(mainContext);
                    autoAdapt.execute(theSymbol);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*String theSymbol = s.toString();
                if (theSymbol.length() >= typeStock.getThreshold()) {
                    SetAutocompleteAdapterClass autoAdapt = new SetAutocompleteAdapterClass();
                    autoAdapt.execute(theSymbol);
                }*/
            }
        });
        typeStock.setAdapter(adapter);

        autoRefresher = (SwitchCompat) findViewById(R.id.autoRefSwitch);
        autoRefresher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoRefresher.isChecked()) {
                    Toast.makeText(v.getContext(), "Switch is ON.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Switch is OFF.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onClickClear(View v) {
        typeStock.setText("");
    }

    public void onClickGetQuote(View v) {
        String input = typeStock.getText().toString();
        if (input.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please enter a stock name symbol.")
            .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog emptyAlert = builder.create();
            emptyAlert.show();
        } else {
            GetDataClass stockCheck = new GetDataClass(mainContext, input);
            stockCheck.execute(input);
        }
    }

    public void onClickAddFavorite(View v) {
        String inputFav = typeStock.getText().toString();
        if (inputFav.equals("")) {
            AlertDialog.Builder favBuilder = new AlertDialog.Builder(this);
            favBuilder.setMessage("Favorite cannot be empty.")
                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog emptyFav = favBuilder.create();
            emptyFav.show();
        } else {
            // call background thread to add favorite
            AddFavoriteClass addFav = new AddFavoriteClass(mainContext, inputFav);
            addFav.execute(inputFav);
        }
    }

    /*
    background thread class for adding to favorites list
     */
    private class AddFavoriteClass extends AsyncTask<String, Void, FavoriteItem> {
        /*
        important checks: check if input is in list, check if input is valid
        To Do:
        -get current data array
        -get historical data array
        -parse current data array
        -make a FavoriteItem instance
        -add into the end of the adapter
         */
        private Context c;
        private String inputTick;

        boolean isValid;

        private final ProgressDialog progressD = new ProgressDialog(MainActivity.this);
        String dataFetch = "Fetching Data...";
        String token = getResources().getString(R.string.apiToken);

        public AddFavoriteClass(Context c, String inputTick) {
            this.c = c;
            this.inputTick = inputTick;
        }

        @Override
        protected void onPreExecute() {
            this.progressD.setMessage(dataFetch);
            this.progressD.setCancelable(false);
            this.progressD.show();
        }

        @Override
        protected FavoriteItem doInBackground(String... args) {
            isValid = false;
            String favItemJSONCurrent = "";
            String favItemJSONHistory = "";
            String favItemTicker = "";
            double favItemPrice = 0.0;
            double favItemPercent = 0.0;
            FavoriteItem theItemFav = null;

            String iexString = "https://api.tiingo.com/iex?tickers=" + args[0] +
                    "&token=" + token;

            String iexJSON = inputToString(iexString);

            JSONArray theIex = null;

            try {
                theIex = new JSONArray(iexJSON);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (theIex != null) {
                if (theIex.length() > 0) {
                    isValid = true;
                    favItemJSONCurrent = iexJSON;

                    String historyString = "https://api.tiingo.com/tiingo/daily/"+ args[0] +
                            "/prices?startDate=2020-08-26&resampleFreq=daily&token=" + token;
                    favItemJSONHistory = inputToString(historyString);

                    JSONObject favObj = null;
                    try {
                        favObj = theIex.getJSONObject(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (favObj != null) {
                        String objTicker = "";
                        try {
                            objTicker = favObj.getString("ticker");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        favItemTicker = objTicker;

                        Double objLast = null;
                        try {
                            objLast = favObj.getDouble("last");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (objLast == null) {
                            favItemPrice = 0.0;
                        } else {
                            favItemPrice = objLast;
                        }

                        Double objPrevClose = null;
                        try {
                            objPrevClose = favObj.getDouble("prevClose");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if ((objLast != null) && (objPrevClose != null)) {
                            favItemPercent = Double.parseDouble(new DecimalFormat(
                                    "####.##").format((objLast - objPrevClose)));
                        } else {
                            favItemPercent = 0.0;
                        }
                    }

                    theItemFav = new FavoriteItem(favItemJSONCurrent, favItemJSONHistory, favItemTicker,
                            favItemPrice, favItemPercent);
                }
            }

            return theItemFav;
        }

        @Override
        protected void onPostExecute(FavoriteItem item) {
            if (this.progressD.isShowing()) {
                this.progressD.dismiss();
            }

            if (isValid) {
                favAdapter.add(favAdapter.getItemCount(), item);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setMessage("Invalid Symbol.")
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog invalidAlert = builder.create();
                invalidAlert.show();
            }
        }
    }

    /*
    background thread class for validation and getting current and historical data
     */
    private class GetDataClass extends AsyncTask<String, Void, Boolean> {
        private Context context;
        private String theInput;

        String sendCurrentInfo = "";
        String sendHistory = "";

        private final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String dataFetch = "Fetching Data...";
        String token = getResources().getString(R.string.apiToken);

        public GetDataClass(Context context, String theInput) {
            this.context = context;
            this.theInput = theInput;
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage(dataFetch);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            String iexString = "https://api.tiingo.com/iex?tickers=" + args[0] +
                    "&token=" + token;
            Log.e("iexRequest", iexString);

            String iexJSON = inputToString(iexString);
            Log.e("iexReturn", iexJSON);

            JSONArray theIex = null;
            boolean notEmpty = false;

            try {
                theIex = new JSONArray(iexJSON);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (theIex != null) {
                if (theIex.length() > 0) {
                    notEmpty = true;
                    sendCurrentInfo = iexJSON;

                    String historyString = "https://api.tiingo.com/tiingo/daily/"+ args[0] +
                            "/prices?startDate=2020-08-26&resampleFreq=daily&token=" + token;
                    Log.e("historyRequest", historyString);
                    sendHistory = inputToString(historyString);
                    Log.e("historyReturn", sendHistory);
                }
            }

            Log.e("isValidStock", "status:" + notEmpty);

            return notEmpty;
        }

        @Override
        protected void onPostExecute(Boolean notFilled) {
            if (this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (notFilled) {
                Toast.makeText(context, theInput + " is valid stock.",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, StockInfoActivity.class);
                Bundle someJSON = new Bundle();
                someJSON.putString("current", sendCurrentInfo);
                someJSON.putString("history", sendHistory);
                intent.putExtras(someJSON);
                startActivity(intent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Invalid Symbol.")
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog invalidAlert = builder.create();
                invalidAlert.show();
            }
        }
    }

    /*
    background thread class to find autocomplete options
     */
    private class SetAutocompleteAdapterClass extends AsyncTask<String, Void, List<String>> {
        /*

        "https://api.tiingo.com/tiingo/utilities/search?query=" + typeStock.getText()
        + "&token=" + token

        1. get JSON array using inputToString with argument of String from typeStock
        2. work with JSONArray, translate it into an ArrayList of Strings
        3. set AutoCompleteTextView Adapter with created ArrayList

         */
        private Context context;
        String token = getResources().getString(R.string.apiToken);

        public SetAutocompleteAdapterClass(Context context) {
            this.context = context;
        }

        @Override
        protected List<String> doInBackground(String... args) {
            String queryString = "https://api.tiingo.com/tiingo/utilities/search?query=" +
                    args[0] + "&token=" + token;
            Log.e("query", queryString);
            List<String> tickers = new ArrayList<String>();


            String arrayJson = inputToString(queryString);
            Log.e("returned", arrayJson);
            /*String arrayJsonThe = "[{ticker: 'AAPL'},{ticker: 'AAPC'},{ticker: 'AAPT'},{" +
                    "ticker: 'RACE'}]";*/

            JSONArray searches = null;
            try {
                searches = new JSONArray(arrayJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (searches != null) {
                for (int i = 0; i < searches.length(); i++) {
                    String toAdd = null;
                    try {
                        toAdd = searches.getJSONObject(i).getString("ticker");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (toAdd != null) {
                        tickers.add(toAdd);
                    }
                }
            }

            return tickers;
        }

        @Override
        protected void onPostExecute(List<String> theList) {
            /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, theList);
            typeStock.setAdapter(adapter);*/
            adapter.clear();
            adapter.addAll(theList);
            adapter.notifyDataSetChanged();
        }
    }

    /*
    Function to get JSON output from URL
    Code from Appendix A of XMLandJSON slides
     */
    private InputStream downloadURL(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    /*
    Function to convert InputStream to String
     */
    private String inputToString(String theUrl) {
        String output = "";
        InputStream stream = null;
        try {
            /*

            Code within try block constructed with help of:
            https://www.tutorialspoint.com/how-to-convert-inputstream-object-to-a-string-in-java

             */
            stream = downloadURL(theUrl);
            InputStreamReader theReader = new InputStreamReader(stream);
            BufferedReader buffReader = new BufferedReader(theReader);
            StringBuilder bobTheBuilder = new StringBuilder();
            String check = "";
            while ((check = buffReader.readLine()) != null) {
                bobTheBuilder.append(check);
            }
            output = bobTheBuilder.toString();
            buffReader.close();
            theReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return output;
    }
}