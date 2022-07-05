package jp.or.horih.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class CommonHttpAccessor {

    private HttpClient client;
    private Uri.Builder builder;

    public CommonHttpAccessor(String scheme, String authority, String path) {
        this.client = new DefaultHttpClient();
        this.builder = new Uri.Builder();
        this.builder.scheme(scheme);
        this.builder.authority(authority);
        this.builder.path(path);
    }

    public String getJsonResponseString(Bundle params) {
        String data = null;

        for (String key : params.keySet()) {
            if (TextUtils.equals("_scheme", key)) {
                builder.scheme(params.getString(key));
            } else if (TextUtils.equals("_host", key)) {
                builder.authority(params.getString(key));
            } else if (TextUtils.equals("_path", key)) {
                builder.path(params.getString(key));
            } else {
                builder.appendQueryParameter(key, params.getString(key));
            }
        }

        Log.v(this.getClass().getCanonicalName(), builder.build().toString());

        HttpGet request = new HttpGet(builder.build().toString());
        HttpResponse response = null;

        HttpConnectionParams.setConnectionTimeout(request.getParams(), 10000);
        HttpConnectionParams.setSoTimeout(request.getParams(), 10000);

        try {
            response = client.execute(request);
        } catch (ClientProtocolException cpe) {
            Log.e(this.getClass().getName(), "protocol error.", cpe);
            return data;
        } catch (IOException ioe) {
            Log.e(this.getClass().getName(), "input/output error (connect with WebApi).", ioe);
            return data;
        }

        if (response == null) {
            return data;
        }

        int status = response.getStatusLine().getStatusCode();

        if (HttpStatus.SC_OK == status) {
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                response.getEntity().writeTo(baos);
                data = baos.toString();
                response.getEntity().consumeContent();
            } catch (IOException ioe) {
                Log.e(this.getClass().getName(), "input/output error (get response).", ioe);
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException ioe) {
                        Log.e(this.getClass().getName(), "input/output error (close output stream).", ioe);
                    }
                }
                baos = null;
            }
        } else {
            Log.e(this.getClass().getCanonicalName(), "bad status(STATUS CODE:" + status + ")");
        }

//        if(Build.VERSION.SDK_INT < 11){
//            data = data.substring(1);
//        }
        return data;
//      return data.substring(1);
    }

    public byte[] getBitmapByteArray(Bundle params) {
        byte[] data = null;

        for (String key : params.keySet()) {
            if (TextUtils.equals("_scheme", key)) {
                builder.scheme(params.getString(key));
            } else if (TextUtils.equals("_host", key)) {
                builder.authority(params.getString(key));
            } else if (TextUtils.equals("_path", key)) {
                builder.path(params.getString(key));
            } else {
                builder.appendQueryParameter(key, params.getString(key));
            }
        }

        Log.v(this.getClass().getCanonicalName(), builder.build().toString());

        HttpGet request = new HttpGet(builder.build().toString());
        HttpResponse response = null;

        HttpConnectionParams.setConnectionTimeout(request.getParams(), 100000);
        HttpConnectionParams.setSoTimeout(request.getParams(), 100000);

        try {
            response = client.execute(request);
        } catch (ClientProtocolException cpe) {
            Log.e(this.getClass().getName(), "protocol error.", cpe);
            return data;
        } catch (IOException ioe) {
            Log.e(this.getClass().getName(), "input/output error (connect with WebApi).", ioe);
            return data;
        }

        if (response == null) {
            return data;
        }

        int status = response.getStatusLine().getStatusCode();

        if (HttpStatus.SC_OK == status) {
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                data = EntityUtils.toByteArray(response.getEntity());
            } catch (IOException ioe) {
                Log.e(this.getClass().getName(), "input/output error (get response).", ioe);
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException ioe) {
                        Log.e(this.getClass().getName(), "input/output error (close output stream).", ioe);
                    }
                }
                baos = null;
            }
        } else {
            Log.e(this.getClass().getCanonicalName(), "bad status(STATUS CODE:" + status + ")");
        }

        return data;
    }

    public String getJsonResponseString(Bundle params, String imageKey, File file) {

        for (String key : params.keySet()) {
            if (TextUtils.equals("_scheme", key)) {
                builder.scheme(params.getString(key));
            } else if (TextUtils.equals("_host", key)) {
                builder.authority(params.getString(key));
            } else if (TextUtils.equals("_path", key)) {
                builder.path(params.getString(key));
            } else {
                builder.appendQueryParameter(key, params.getString(key));
            }
        }

        Log.v(this.getClass().getCanonicalName(), builder.build().toString());

        HttpPost request = new HttpPost(builder.build().toString());
        HttpResponse response = null;

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        entity.addPart(imageKey, new FileBody(file));
        request.setEntity(entity.build());


        HttpConnectionParams.setConnectionTimeout(request.getParams(), 10000);
        HttpConnectionParams.setSoTimeout(request.getParams(), 10000);

        String data = null;

        try {
            response = client.execute(request);
        } catch (ClientProtocolException cpe) {
            Log.e(this.getClass().getName(), "protocol error.", cpe);
            return data;
        } catch (IOException ioe) {
            Log.e(this.getClass().getName(), "input/output error (connect with WebApi).", ioe);
            return data;
        }

        if (response == null) {
            return data;
        }

        int status = response.getStatusLine().getStatusCode();

        if (HttpStatus.SC_OK == status) {
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                response.getEntity().writeTo(baos);
                data = baos.toString();
                response.getEntity().consumeContent();
            } catch (IOException ioe) {
                Log.e(this.getClass().getName(), "input/output error (get response).", ioe);
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException ioe) {
                        Log.e(this.getClass().getName(), "input/output error (close output stream).", ioe);
                    }
                }
                baos = null;
            }
        } else {
            Log.e(this.getClass().getCanonicalName(), "bad status(STATUS CODE:" + status + ")");
        }

        if (Build.VERSION.SDK_INT < 11) {
            data = data.substring(1);
        }
        return data;
//        return data.substring(1);
    }

}
