package info.devexchanges.volley;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout imageLayout;
    private ListView listView;
    private ScrollView textLayout;
    private TextView textView;
    private Spinner spinner;
    private View btnRequest;
    private ImageView imageView;
    private NetworkImageView networkImageView;
    private ArrayList<String> stringArrayList;
    private ProgressDialog progressDialog;

    private final static String IMAGE_URL = "http://i.imgur.com/cReBvDB.png";
    private final static String STRING_URL = "http://httpbin.org/html";
    private final static String JSON_URL = "http://www.json-generator.com/api/json/get/cfgOSImXUy?indent=2";
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner) findViewById(R.id.spinner);
        btnRequest = findViewById(R.id.btn_request);
        textLayout = (ScrollView) findViewById(R.id.ll_text);
        imageLayout = (LinearLayout) findViewById(R.id.ll_image);
        listView = (ListView) findViewById(R.id.list_item);
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.text);
        networkImageView = (NetworkImageView) findViewById(R.id.img_network);

        btnRequest.setOnClickListener(onClickListener());
        setSpinnerData();
    }

    private View.OnClickListener onClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = spinner.getSelectedItemPosition();
                if (position == 0) {
                    getStringFromURL();
                } else if (position == 1) {
                    getJSONFromURL();
                } else {
                    getImageFromURL();
                }
            }
        };
    }

    private void setSpinnerData() {
        stringArrayList = new ArrayList<>();
        stringArrayList.add("String request");
        stringArrayList.add("JSON request");
        stringArrayList.add("Image request");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stringArrayList);
        spinner.setAdapter(adapter);
    }

    public void getImageFromURL() {
        showProgressDialog();
        ImageLoader imageLoader = VolleyApplication.getInstance().getImageLoader();
        // Using NetworkImageView
        networkImageView.setImageUrl(IMAGE_URL, imageLoader);
        networkImageView.setDefaultImageResId(R.mipmap.ic_launcher);

        // If you are using normal ImageView
        imageLoader.get(IMAGE_URL, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: " + error.getMessage());
                imageView.setImageResource(R.mipmap.ic_launcher);
                dismissProgressDialog();
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    imageView.setImageBitmap(response.getBitmap());
                    imageLayout.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    textLayout.setVisibility(View.GONE);
                }
                dismissProgressDialog();
            }
        });
    }

    /**
     * Making String request from URL
     */
    private void getStringFromURL() {
        showProgressDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET, STRING_URL, new Response.Listener<String>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                textView.setText("This text have been get from an URL: " + Html.fromHtml(response));
                textView.setVisibility(View.VISIBLE);
                dismissProgressDialog();
                imageLayout.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                textLayout.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                dismissProgressDialog();
                textView.setText("Error loading text!");
                dismissProgressDialog();
            }
        });

        // Adding request to request queue
        VolleyApplication.getInstance().addToRequestQueue(strReq, "string_request");
    }

    private void getJSONFromURL() {
        showProgressDialog();

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, JSON_URL, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> allNames = new ArrayList<>();
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line,
                                allNames);
                        listView.setAdapter(adapter);

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject friend = response.getJSONObject(i);
                                String name = friend.getString("name");
                                allNames.add(name);
                            }
                            //update adapter
                            adapter.notifyDataSetChanged();
                            listView.setVisibility(View.VISIBLE);
                            imageLayout.setVisibility(View.GONE);
                            textLayout.setVisibility(View.GONE);

                            dismissProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dismissProgressDialog();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                dismissProgressDialog();
            }
        });

        VolleyApplication.getInstance().addToRequestQueue(jsonObjReq, "json_request");
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
