package com.vt.disposisibandung.fragments;

import android.support.v4.app.Fragment;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.Token;
import com.vt.disposisibandung.utils.DatabaseHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by irvan on 7/5/15.
 */
@EFragment(R.layout.fragment_jadwal)
public class JadwalFragment extends Fragment {

    @ViewById(R.id.webView)
    protected WebView webView;

    @AfterViews
    protected void initViews() {
        // TODO: replace webview
        StringBuilder url = new StringBuilder();
//        http://surat.pareparekota.go.id/api/jadwal/webview/
//        url.append("http://surat-diskominfo.bandung.go.id/api/jadwal/webview/");
        url.append("http://surat.pareparekota.go.id/api/jadwal/webview/");
        Token token = DatabaseHelper.getInstance().getRealm().where(Token.class).findFirst();
        if (token != null) {
            long time = System.currentTimeMillis() / 1000l;
            String key = "";
            try {
                byte[] digest = MessageDigest.getInstance("MD5").digest(("1eO9nzivSNv8UUhEmDf0IIAZJ94hWjCX" + token.getAccessToken() + time).getBytes());
                key = new BigInteger(1, digest).toString(16);
            } catch (Exception e) {
                e.printStackTrace();
            }
            url.append("?device_id=" + token.getDeviceId());
            url.append("&time=" + time);
            url.append("&key=" + key);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url.toString());
    }
}
