package com.vt.disposisibandung.fragments;

import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vt.disposisibandung.ForgotActivity_;
import com.vt.disposisibandung.MainActivity_;
import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.Token;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.services.RegistrationIntentService;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import retrofit.RetrofitError;

/**
 * Created by irvan on 6/24/15.
 */
@EFragment(R.layout.fragment_login)
public class LoginFragment extends Fragment {

    @ViewById(R.id.editUsername)
    protected EditText editTextUsername;

    @ViewById(R.id.editPassword)
    protected EditText editTextPassword;

    @ViewById(R.id.checkRemember)
    protected CheckBox checkBoxRemember;

    @ViewById(R.id.buttonLogin)
    protected Button buttonLogin;

    @ViewById(R.id.progressLogin)
    protected ProgressBar progressBarLogin;

    @ViewById(R.id.textError)
    protected TextView textViewError;

    @ViewById(R.id.textForgot)
    protected TextView textViewHintForgot;

    @Click(R.id.buttonLogin)
    protected void loginClicked() {
        performAuth(editTextUsername.getText().toString(), editTextPassword.getText().toString(), checkBoxRemember.isChecked());
    }

    @Click(R.id.textForgot)
    protected void gotoForgot() {
        ForgotActivity_.intent(getActivity()).start();
    }

    private void performAuth(String username, String password, final boolean remember) {
        textViewError.setVisibility(View.GONE);
        buttonLogin.setEnabled(false);
        progressBarLogin.setVisibility(View.VISIBLE);

        String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceType = Build.MANUFACTURER + " " + Build.MODEL + ", Android " + Build.VERSION.RELEASE;

        WebServiceHelper.getInstance().getServices().authenticate(deviceId, username, password, deviceType, "", new Callback<Token>() {
            @Override
            public void success(Token token) {
                token.setRemember(remember);
                WebServiceHelper.getInstance().setAccessToken(token);

                DatabaseHelper.getInstance().getRealm().beginTransaction();
                DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(token);
                DatabaseHelper.getInstance().getRealm().commitTransaction();

                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .remove(RegistrationIntentService.PREF_GCM_TOKEN_UPDATED)
                        .apply();

                MainActivity_.intent(getActivity()).start();
                getActivity().finish();
            }


            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                textViewError.setVisibility(View.VISIBLE);
                buttonLogin.setEnabled(true);
                progressBarLogin.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(getActivity(), "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                buttonLogin.setEnabled(true);
                progressBarLogin.setVisibility(View.GONE);
            }

        });
    }
}
