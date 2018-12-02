package com.vt.disposisibandung;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vt.disposisibandung.adapters.JenisSuratSpinnerAdapter;
import com.vt.disposisibandung.models.JenisSurat;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.User;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.services.UploadService_;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.UploadFileHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;
import com.vt.disposisibandung.utils.compressor.Compressor;
import com.vt.disposisibandung.utils.compressor.FileUtil;
import com.vt.disposisibandung.views.AddPhotoGridItemView;
import com.vt.disposisibandung.views.AddPhotoGridItemView_;
import com.vt.disposisibandung.views.PreviewPhotoGridItemView;
import com.vt.disposisibandung.views.PreviewPhotoGridItemView_;
import com.vt.disposisibandung.views.TargetListItemView;
import com.vt.disposisibandung.views.TargetListItemView_;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by irvan on 7/2/15.
 */
@EActivity(R.layout.activity_add_surat_masuk)
public class AddSuratMasukActivity extends AppCompatActivity implements TargetListItemView.OnTargetCheckedChangeListener, AddPhotoGridItemView.OnAddPhotoClickListener, PreviewPhotoGridItemView.OnPreviewActionListener {

    private static final int REQUEST_CODE_PICK = 10101;

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;

    @ViewById(R.id.scrollView)
    protected ScrollView scrollView;

    @ViewById(R.id.editTanggalMasukSurat)
    protected EditText editTextTanggalMasukSurat;

    @ViewById(R.id.spinnerJenisSurat)
    protected Spinner spinnerJenisSurat;

    @ViewById(R.id.editTanggalSurat)
    protected EditText editTextTanggalSurat;

    @ViewById(R.id.editNomorSurat)
    protected EditText editTextNomorSurat;

    @ViewById(R.id.editLampiranSurat)
    protected EditText editTextLampiranSurat;

    @ViewById(R.id.editPerihalSurat)
    protected EditText editTextPerihalSurat;

    @ViewById(R.id.editTembusanSurat)
    protected EditText editTextTembusanSurat;

    @ViewById(R.id.targetList)
    protected LinearLayout layoutTargetList;

    @ViewById(R.id.photoPreview)
    protected FlowLayout flowLayoutPhotoPreview;

    @ViewById(R.id.editNamaKegiatan)
    protected EditText editTextNamaKegiatan;

    @ViewById(R.id.editTanggalMulaiKegiatan)
    protected EditText editTextTanggalMulaiKegiatan;

    @ViewById(R.id.editJamMulaiKegiatan)
    protected EditText editTextJamMulaiKegiatan;

    @ViewById(R.id.editTanggalSelesaiKegiatan)
    protected EditText editTextTanggalSelesaiKegiatan;

    @ViewById(R.id.editJamSelesaiKegiatan)
    protected EditText editTextJamSelesaiKegiatan;

    @ViewById(R.id.editTempatKegiatan)
    protected EditText editTextTempatKegiatan;

    @ViewById(R.id.editNamaPengirim)
    protected EditText editTextNamaPengirim;

    @ViewById(R.id.editNoHpPengirim)
    protected EditText editTextNoHpPengirim;

    @ViewById(R.id.editEmailPengirim)
    protected EditText editTextEmailPengirim;

    @ViewById(R.id.textTanggalDisposisi)
    protected TextView textViewTanggalDisposisi;

    @ViewById(R.id.textPerihalDisposisi)
    protected TextView textViewPerihalDisposisi;

    @ViewById(R.id.editNomorDisposisi)
    protected EditText editTextNomorDisposisi;

    @ViewById(R.id.buttonSubmitSuratMasuk)
    protected Button buttonSubmitSuratMasuk;

    @ViewById(R.id.progressSubmit)
    protected ProgressBar progressBarSubmit;

    private Calendar calendarTanggalMasukSurat;
    private Calendar calendarTanggalSurat;
    private Map<String, String> jenisSuratList;
    private JenisSuratSpinnerAdapter spinnerAdapter;
    private Map<Long, Boolean> selectedTargets;
    private List<String> previewFiles;
    private int previewSuffix;
    private Calendar calendarWaktuMulaiKegiatan;
    private Calendar calendarWaktuSelesaiKegiatan;

    @AfterViews
    protected void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_add_surat_masuk);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        calendarTanggalMasukSurat = Calendar.getInstance();
        calendarTanggalSurat = Calendar.getInstance();

        spinnerAdapter = new JenisSuratSpinnerAdapter(this, new ArrayList<String>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJenisSurat.setAdapter(spinnerAdapter);

        UploadFileHelper.getInstance().clearPreviewTempDirectory(this);
        previewFiles = new ArrayList<>();
        previewSuffix = 1;
        AddPhotoGridItemView view = AddPhotoGridItemView_.build(this);
        view.setOnAddPhotoClickListener(this);
        flowLayoutPhotoPreview.addView(view);

        calendarWaktuMulaiKegiatan = Calendar.getInstance();
        calendarWaktuMulaiKegiatan.set(Calendar.HOUR_OF_DAY, 0);
        calendarWaktuMulaiKegiatan.set(Calendar.MINUTE, 0);
        calendarWaktuSelesaiKegiatan = Calendar.getInstance();
        calendarWaktuSelesaiKegiatan.set(Calendar.HOUR_OF_DAY, 0);
        calendarWaktuSelesaiKegiatan.set(Calendar.MINUTE, 0);

        selectedTargets = new HashMap<>();

        RealmResults<JenisSurat> jenisList = DatabaseHelper.getInstance().getRealm().where(JenisSurat.class).findAll();
        if (jenisList != null && !jenisList.isEmpty()) {
            jenisSuratList = new LinkedHashMap<>();
            for (JenisSurat jenis : jenisList) {
                jenisSuratList.put(jenis.getId(), jenis.getTitle());
                spinnerAdapter.add(jenis.getTitle());
            }
            spinnerAdapter.notifyDataSetChanged();
        }

        performGetJenisSuratMasuk();
        performGetTargetList();
    }

    @Click(R.id.editTanggalMasukSurat)
    protected void pickTanggalMasukSurat() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarTanggalMasukSurat.set(year, monthOfYear, dayOfMonth);
                editTextTanggalMasukSurat.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendarTanggalMasukSurat.getTime()));
                textViewTanggalDisposisi.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendarTanggalMasukSurat.getTime()));
            }
        }, calendarTanggalMasukSurat.get(Calendar.YEAR), calendarTanggalMasukSurat.get(Calendar.MONTH), calendarTanggalMasukSurat.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Click(R.id.editTanggalSurat)
    protected void pickTanggalSurat() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarTanggalSurat.set(year, monthOfYear, dayOfMonth);
                editTextTanggalSurat.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendarTanggalSurat.getTime()));
            }
        }, calendarTanggalSurat.get(Calendar.YEAR), calendarTanggalSurat.get(Calendar.MONTH), calendarTanggalSurat.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Click(R.id.editTanggalMulaiKegiatan)
    protected void pickTanggalMulaiKegiatan() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarWaktuMulaiKegiatan.set(year, monthOfYear, dayOfMonth);
                editTextTanggalMulaiKegiatan.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendarWaktuMulaiKegiatan.getTime()));
            }
        }, calendarWaktuMulaiKegiatan.get(Calendar.YEAR), calendarWaktuMulaiKegiatan.get(Calendar.MONTH), calendarWaktuMulaiKegiatan.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Click(R.id.editJamMulaiKegiatan)
    protected void pickJamMulaiKegiatan() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendarWaktuMulaiKegiatan.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarWaktuMulaiKegiatan.set(Calendar.MINUTE, minute);
                editTextJamMulaiKegiatan.setText(new SimpleDateFormat("HH:mm").format(calendarWaktuMulaiKegiatan.getTime()));
            }
        }, calendarWaktuMulaiKegiatan.get(Calendar.HOUR_OF_DAY), calendarWaktuMulaiKegiatan.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    @Click(R.id.editTanggalSelesaiKegiatan)
    protected void pickTanggalSelesaiKegiatan() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarWaktuSelesaiKegiatan.set(year, monthOfYear, dayOfMonth);
                editTextTanggalSelesaiKegiatan.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendarWaktuSelesaiKegiatan.getTime()));
            }
        }, calendarWaktuSelesaiKegiatan.get(Calendar.YEAR), calendarWaktuSelesaiKegiatan.get(Calendar.MONTH), calendarWaktuSelesaiKegiatan.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Click(R.id.editJamSelesaiKegiatan)
    protected void pickJamSelesaiKegiatan() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendarWaktuSelesaiKegiatan.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarWaktuSelesaiKegiatan.set(Calendar.MINUTE, minute);
                editTextJamSelesaiKegiatan.setText(new SimpleDateFormat("HH:mm").format(calendarWaktuSelesaiKegiatan.getTime()));
            }
        }, calendarWaktuSelesaiKegiatan.get(Calendar.HOUR_OF_DAY), calendarWaktuSelesaiKegiatan.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    @Click(R.id.buttonSubmitSuratMasuk)
    protected void submitSuratMasuk() {
        if (editTextTanggalMasukSurat.getText().toString().length() == 0) {
            Toast.makeText(this, "Tanggal masuk surat harus diisi", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(0, 0);
            return;
        }
        if (editTextTanggalSurat.getText().toString().length() == 0) {
            Toast.makeText(this, "Tanggal surat harus diisi", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(0, spinnerJenisSurat.getBottom());
            return;
        }
        if (editTextNomorSurat.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Nomor surat harus diisi", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(0, editTextTanggalSurat.getBottom());
            return;
        }
        if (editTextLampiranSurat.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Lampiran surat harus diisi", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(0, editTextNomorSurat.getBottom());
            return;
        }
        if (editTextPerihalSurat.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Perihal surat harus diisi", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(0, editTextLampiranSurat.getBottom());
            return;
        }

        performSubmitSuratMasuk();
    }

    @AfterTextChange(R.id.editPerihalSurat)
    protected void setPerihalDisposisi(Editable text) {
        textViewPerihalDisposisi.setText(text.toString());
    }

    @OnActivityResult(REQUEST_CODE_PICK)
    protected void photoPickResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            preparePreview(data);
        }
    }

    @Background
    protected void preparePreview(Intent data) {
        try {
            File file = new File(UploadFileHelper.getInstance().getPreviewTempDirectory(this), "Photo-" + previewSuffix + ".jpg");
            if (!file.exists() && data != null) {

                String imagePath = UploadFileHelper.getInstance().getPreviewTempDirectory(this).getPath();
                File image = FileUtil.from(this, data.getData(), "Photo-" + previewSuffix + ".jpg");

                new Compressor.Builder(this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(imagePath)
                        .build()
                        .compressToFile(image);

            }
            previewFiles.add(file.getCanonicalPath());
            addPreview(file.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    protected void addPreview(String filename) {
        PreviewPhotoGridItemView view = PreviewPhotoGridItemView_.build(this);
        view.setPhoto(filename);
        view.setOnPreviewActionListener(this);
        flowLayoutPhotoPreview.addView(view, previewFiles.size() - 1);
        ++previewSuffix;
    }

    @Override
    public void onTargetCheckedChanged(User user, boolean isChecked) {
        selectedTargets.put(user.getId(), isChecked);
    }

    @Override
    public void onAddPhotoClicked() {
        if (!UploadFileHelper.getInstance().isExternalStorageAvailable()) {
            new AlertDialog.Builder(this)
                    .setMessage("Media eksternal tidak tersedia.")
                    .setNegativeButton("OK", null)
                    .show();
            return;
        }
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/jpeg");
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(UploadFileHelper.getInstance().getPreviewTempDirectory(this), "Photo-" + previewSuffix + ".jpg");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            Intent chooserIntent = Intent.createChooser(galleryIntent, "Ambil gambar");
            Intent[] chooserExtra = {cameraIntent};
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, chooserExtra);
            startActivityForResult(chooserIntent, REQUEST_CODE_PICK);
        } else {
            startActivityForResult(galleryIntent, REQUEST_CODE_PICK);
        }
    }

    @Override
    public void onPreviewClicked(View view, String filename) {
        ImageViewerActivity_.intent(this).previewFilename(filename).position(0).start();
    }

    @Override
    public void onPreviewDeleted(View view, String filename) {
        previewFiles.remove(filename);
        ((ViewGroup) view.getParent()).removeView(view);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void performGetJenisSuratMasuk() {
        spinnerJenisSurat.setEnabled(false);

        WebServiceHelper.getInstance().getServices().getJenisSuratMasuk(new Callback<Map<String, String>>() {
            @Override
            public void success(Map<String, String> hashMap) {
                List<JenisSurat> jenisList = new RealmList<>();

                jenisSuratList = hashMap;
                spinnerAdapter.clear();
                for (Map.Entry<String, String> entry : jenisSuratList.entrySet()) {
                    spinnerAdapter.add(entry.getValue());
                    JenisSurat jenis = new JenisSurat();
                    jenis.setId(entry.getKey());
                    jenis.setTitle(entry.getValue());
                    jenisList.add(jenis);
                }
                spinnerAdapter.notifyDataSetChanged();

                DatabaseHelper.getInstance().getRealm().beginTransaction();
                DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(jenisList);
                DatabaseHelper.getInstance().getRealm().commitTransaction();

                spinnerJenisSurat.setEnabled(true);
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                spinnerJenisSurat.setEnabled(true);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(AddSuratMasukActivity.this, "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                spinnerJenisSurat.setEnabled(true);
            }
        });
    }

    private void performGetTargetList() {
        WebServiceHelper.getInstance().getServices().getTargetList(new Callback<HashMap<Long, String>>() {
            @Override
            public void success(HashMap<Long, String> hashMap) {
                for (Map.Entry<Long, String> entry : hashMap.entrySet()) {
                    TargetListItemView view = TargetListItemView_.build(AddSuratMasukActivity.this);
                    view.setOnTargetCheckedChangeListener(AddSuratMasukActivity.this);
                    User user = new User();
                    user.setId(entry.getKey());
                    user.setJabatan(entry.getValue());
                    view.setUser(user);
                    layoutTargetList.addView(view);
                }
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(AddSuratMasukActivity.this, "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSubmitSuratMasuk() {
        Surat newSurat = new Surat();

        newSurat.setTanggalMasuk(calendarTanggalMasukSurat.getTime());
        newSurat.setTanggalSurat(calendarTanggalSurat.getTime());
        newSurat.setNomorSurat(editTextNomorSurat.getText().toString().trim());
        newSurat.setLampiran(editTextLampiranSurat.getText().toString().trim());
        newSurat.setPerihal(editTextPerihalSurat.getText().toString().trim());

        String jenisId = "";
        for (Map.Entry<String, String> entry : jenisSuratList.entrySet()) {
            if (entry.getValue().equals(spinnerJenisSurat.getSelectedItem())) {
                jenisId = entry.getKey();
                break;
            }
        }
        newSurat.setJenisSuratId(jenisId);

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Map.Entry<Long, Boolean> entry : selectedTargets.entrySet()) {
            if (entry.getValue()) {
                builder.append(entry.getKey());
                builder.append(",");
            }
        }
        if (builder.length() > 1) builder.setLength(builder.length() - 1);
        builder.append("]");
        newSurat.setDitujukanKepada(builder.toString());

        if (editTextTembusanSurat.getText().toString().trim().length() > 0)
            newSurat.setTembusan(editTextTembusanSurat.getText().toString().trim());
        if (editTextNamaKegiatan.getText().toString().trim().length() > 0)
            newSurat.setNamaKegiatan(editTextNamaKegiatan.getText().toString().trim());
        if (editTextTempatKegiatan.getText().toString().trim().length() > 0)
            newSurat.setTempatKegiatan(editTextTempatKegiatan.getText().toString().trim());
        if (editTextTanggalMulaiKegiatan.getText().toString().length() > 0 && editTextJamMulaiKegiatan.getText().toString().length() > 0)
            newSurat.setWaktuMulaiKegiatan(calendarWaktuMulaiKegiatan.getTime());
        if (editTextTanggalSelesaiKegiatan.getText().toString().length() > 0 && editTextJamSelesaiKegiatan.getText().toString().length() > 0)
            newSurat.setWaktuSelesaiKegiatan(calendarWaktuSelesaiKegiatan.getTime());
        if (editTextNamaPengirim.getText().toString().trim().length() > 0)
            newSurat.setNamaPengirim(editTextNamaPengirim.getText().toString().trim());
        if (editTextNoHpPengirim.getText().toString().trim().length() > 0)
            newSurat.setNoHpPengirim(editTextNoHpPengirim.getText().toString().trim());
        if (editTextEmailPengirim.getText().toString().trim().length() > 0)
            newSurat.setEmailPengirim(editTextEmailPengirim.getText().toString().trim());
        if (editTextNomorDisposisi.getText().toString().trim().length() > 0)
            newSurat.setNomorDisposisi(editTextNomorDisposisi.getText().toString().trim());

        buttonSubmitSuratMasuk.setEnabled(false);
        progressBarSubmit.setVisibility(View.VISIBLE);

        WebServiceHelper.getInstance().getServices().submitSuratMasuk(newSurat, new Callback<Surat>() {
            @Override
            public void success(Surat surat) {
                Toast.makeText(AddSuratMasukActivity.this, "Surat masuk berhasil disimpan", Toast.LENGTH_SHORT).show();

                UploadFileHelper.getInstance().addToUploadQueue(AddSuratMasukActivity.this, Surat.TYPE_MASUK, surat.getSuratId(), previewFiles);
                UploadService_.intent(AddSuratMasukActivity.this).start();

                finish();
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                buttonSubmitSuratMasuk.setEnabled(true);
                progressBarSubmit.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(AddSuratMasukActivity.this, "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                buttonSubmitSuratMasuk.setEnabled(true);
                progressBarSubmit.setVisibility(View.GONE);
            }
        });
    }

}
