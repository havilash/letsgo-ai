package ch.letsGo.ai.dal;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import ch.letsGo.ai.helper.FactJsonParser;
import ch.letsGo.ai.model.Fact;

public class FactDao {
    private static final String BASE_API_URL = "https://api.api-ninjas.com/v1";
    private static final String FACT_API_URL = BASE_API_URL + "/facts";
    private static final String JOKE_API_URL = BASE_API_URL + "/jokes";
    private static final String DAD_JOKE_API_URL = BASE_API_URL + "/dadjokes";
    private static final String CHUCK_NORRIS_JOKE_API_URL = BASE_API_URL + "/chucknorris";
    private static final String API_KEY = "API_KEY";

    private final RequestQueue requestQueue;

    public FactDao(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getFact(final FactCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, FACT_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Fact fact = FactJsonParser.parseFact(response);
                            callback.onSuccess(fact);
                        } catch (Exception e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Api-Key", API_KEY);
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void getJoke(final FactCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JOKE_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Fact joke = FactJsonParser.parseJoke(response);
                            callback.onSuccess(joke);
                        } catch (Exception e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Api-Key", API_KEY);
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void getDadJoke(final FactCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DAD_JOKE_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Fact joke = FactJsonParser.parseDadJoke(response);
                            callback.onSuccess(joke);
                        } catch (Exception e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Api-Key", API_KEY);
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void getChuckNorrisJoke(final FactCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CHUCK_NORRIS_JOKE_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Fact joke = FactJsonParser.parseChuckNorrisJoke(response);
                            callback.onSuccess(joke);
                        } catch (Exception e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Api-Key", API_KEY);
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public interface FactCallback {
        void onSuccess(Fact fact);

        void onError(Exception e);
    }
}
