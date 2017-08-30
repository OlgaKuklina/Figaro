package figaro.oklab.com.figaro.fragments;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import figaro.oklab.com.figaro.R;

/**
 * Created by olgakuklina on 8/3/17.
 */

public class AuthenticationFragment extends AppCompatDialogFragment {

    private static final String TAG = AuthenticationFragment.class.getSimpleName();
    private static final String URL_TEMPLATE = "https://api.instagram.com/oauth/authorize/?client_id=%s&redirect_uri=%s&response_type=token";
    private WebView authWebView;
    private String callbackUri;
    private OAuthListener oAuthlistener;

    public void setOAuthlistener(OAuthListener oAuthlistener) {
        this.oAuthlistener = oAuthlistener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        authWebView = (WebView) view.findViewById(R.id.webview_id);

        String clientId = getContext().getString(R.string.client_id);
        callbackUri = getContext().getString(R.string.callback_uri);
        String uri = getString(R.string.url_template);

        final String connectServiceUrl = String.format(uri, clientId, Uri.encode(callbackUri));

        Log.v(TAG, "connect url = " + connectServiceUrl);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean aBoolean) {
                authWebView.getSettings().setJavaScriptEnabled(true);
                authWebView.setWebViewClient(new OAuthWebViewClient());
                authWebView.loadUrl(connectServiceUrl);
            }
        });
        return view;
    }

    public interface OAuthListener {
        void onTokenReceived(String token);

        void onErrorReceived(int errorCode);
    }

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(callbackUri)) {
                String[] urlItems = url.split("#access_token=");
                AuthenticationFragment.this.dismiss();
                oAuthlistener.onTokenReceived(urlItems[1]);
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.e(TAG, "OAuthListener error = " + error.getDescription());
            AuthenticationFragment.this.dismiss();
            oAuthlistener.onErrorReceived(error.getErrorCode());
        }
    }
}
