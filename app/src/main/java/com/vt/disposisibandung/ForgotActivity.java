package com.vt.disposisibandung;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.WebServiceHelper;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * For VELO TEKNOLOGI
 * Created by Ridwan Ismail on 17 Juni 2016
 * You can contact me at : ismail.ridwan98@gmail.com
 * -------------------------------------------------
 * SIP MOBILE
 * com.vt.disposisibandung
 * or see link for more detail https://gitlab.com/velocite/sip-mobile
 */

@EActivity(R.layout.activity_forgot)
public class ForgotActivity extends AppCompatActivity {

    @ViewById(R.id.editEmail)
    protected EditText edtEmail;

    @ViewById(R.id.buttonSubmit)
    protected Button btnSubmit;

    @ViewById(R.id.textError)
    protected TextView txtError;

    @ViewById(R.id.progressSubmit)
    protected ProgressBar progressBar;

    private static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Click(R.id.textBack)
    protected void back() {
        finish();
    }

    @Click(R.id.buttonSubmit)
    protected void submit() {
        String email = edtEmail.getText().toString();
        if (email.equals("") || email == null) {
            Toast.makeText(this, "Silahkan masukkan alamat email terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else if (!isEmailValid(email)) {
            Toast.makeText(this, "Maaf, alamat email tidak valid.", Toast.LENGTH_SHORT).show();
        } else {
            performSubmit(email);
        }
    }

    private void performSubmit(String email) {
        txtError.setVisibility(View.GONE);
        btnSubmit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        WebServiceHelper.getInstance().getServices().forgotPassword(email, new Callback() {
            @Override
            public void success(Object o) {
                showInfo();
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                txtError.setVisibility(View.VISIBLE);
                btnSubmit.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(ForgotActivity.this, "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                btnSubmit.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void showInfo() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Kata sandi baru telah dikirimkan. Silahkan cek email anda.");

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                LoginActivity_.intent(ForgotActivity.this).start();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
