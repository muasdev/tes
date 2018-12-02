package com.vt.disposisibandung.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by irvan on 6/26/15.
 */
public class Surat extends RealmObject {

    public static final int TYPE_MASUK = 101;
    public static final int TYPE_UNDANGAN = 102;
    public static final int TYPE_AUDIENSI = 103;
    public static final int TYPE_UMUM = 104;
    public static final int TYPE_KELUAR = 105;
    public static final int TYPE_BELUM_SELESAI = 106;
    public static final int TYPE_SUDAH_SELESAI = 107;

    public static final int TYPE_DISPOSISI_MASUK = 201;
    public static final int TYPE_DISPOSISI_KELUAR = 202;

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("surat_id")
    private long suratId;

    @Expose
    @SerializedName("tanggal_masuk_surat")
    private Date tanggalMasuk;

    @Expose
    @SerializedName("tanggal_surat")
    private Date tanggalSurat;

    @Expose
    @SerializedName("surat_type_id")
    private String jenisSuratId;

    @SerializedName("surat_type_str")
    private String jenisSurat;

    @SerializedName("jenis_surat")
    private int jenisSuratSearch;

    @Expose
    @SerializedName("nomor_surat")
    private String nomorSurat;

    @Expose
    @SerializedName("lampiran")
    private String lampiran;

    @Expose
    @SerializedName("perihal")
    private String perihal;

    @Expose
    @SerializedName("tembusan")
    private String tembusan;

    @Expose
    @SerializedName("kegiatan")
    private String namaKegiatan;

    @Expose
    @SerializedName("waktu_kegiatan")
    private Date waktuMulaiKegiatan;

    @Expose
    @SerializedName("waktu_kegiatan_end")
    private Date waktuSelesaiKegiatan;

    @Expose
    @SerializedName("tempat_kegiatan")
    private String tempatKegiatan;

    @Expose
    @SerializedName("kepada")
    private String ditujukanKepada;

    @Expose
    @SerializedName("nama_sender")
    private String namaPengirim;

    @Expose
    @SerializedName("hp_sender")
    private String noHpPengirim;

    @Expose
    @SerializedName("email_sender")
    private String emailPengirim;

    @Expose
    @SerializedName("nomor_agenda")
    private String nomorDisposisi;

    @SerializedName("files")
    private RealmList<File> files;

    @SerializedName("disposisi_messages")
    private RealmList<Disposisi> disposisiList;

    @SerializedName("disposisi_target")
    private RealmList<UserGroup> disposisiTargetList;

    @SerializedName("comment")
    private RealmList<Komentar> commentList;

    @SerializedName("is_new")
    private boolean isNew;

    @SerializedName("is_kegiatan")
    private int isKegiatan;

    @SerializedName("state_disposisi")
    private int stateDisposisi;

    @SerializedName("isRead")
    private boolean isRead;

    @SerializedName("status_surat")
    private String statusSurat;

    private int type;

    private boolean suratMasuk = false;
    private boolean suratUndangan = false;
    private boolean suratAudiensi = false;
    private boolean suratUmum = false;
    private boolean suratSelesai = false;
    private boolean suratBelum = false;
    private boolean suratKeluar = false;
    private boolean disposisiMasuk = false;
    private boolean disposisiKeluar = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSuratId() {
        return suratId;
    }

    public void setSuratId(long suratId) {
        this.suratId = suratId;
    }

    public Date getTanggalMasuk() {
        return tanggalMasuk;
    }

    public void setTanggalMasuk(Date tanggalMasuk) {
        this.tanggalMasuk = tanggalMasuk;
    }

    public Date getTanggalSurat() {
        return tanggalSurat;
    }

    public void setTanggalSurat(Date tanggalSurat) {
        this.tanggalSurat = tanggalSurat;
    }

    public String getJenisSuratId() {
        return jenisSuratId;
    }

    public void setJenisSuratId(String jenisSuratId) {
        this.jenisSuratId = jenisSuratId;
    }

    public String getJenisSurat() {
        return jenisSurat;
    }

    public void setJenisSurat(String jenisSurat) {
        this.jenisSurat = jenisSurat;
    }

    public int getJenisSuratSearch() {
        return jenisSuratSearch;
    }

    public void setJenisSuratSearch(int jenisSuratSearch) {
        this.jenisSuratSearch = jenisSuratSearch;
    }

    public String getNomorSurat() {
        return nomorSurat;
    }

    public void setNomorSurat(String nomorSurat) {
        this.nomorSurat = nomorSurat;
    }

    public String getLampiran() {
        return lampiran;
    }

    public void setLampiran(String lampiran) {
        this.lampiran = lampiran;
    }

    public String getPerihal() {
        return perihal;
    }

    public void setPerihal(String perihal) {
        this.perihal = perihal;
    }

    public String getTembusan() {
        return tembusan;
    }

    public void setTembusan(String tembusan) {
        this.tembusan = tembusan;
    }

    public String getNamaKegiatan() {
        return namaKegiatan;
    }

    public void setNamaKegiatan(String namaKegiatan) {
        this.namaKegiatan = namaKegiatan;
    }

    public Date getWaktuMulaiKegiatan() {
        return waktuMulaiKegiatan;
    }

    public void setWaktuMulaiKegiatan(Date waktuMulaiKegiatan) {
        this.waktuMulaiKegiatan = waktuMulaiKegiatan;
    }

    public Date getWaktuSelesaiKegiatan() {
        return waktuSelesaiKegiatan;
    }

    public void setWaktuSelesaiKegiatan(Date waktuSelesaiKegiatan) {
        this.waktuSelesaiKegiatan = waktuSelesaiKegiatan;
    }

    public String getTempatKegiatan() {
        return tempatKegiatan;
    }

    public void setTempatKegiatan(String tempatKegiatan) {
        this.tempatKegiatan = tempatKegiatan;
    }

    public String getDitujukanKepada() {
        return ditujukanKepada;
    }

    public void setDitujukanKepada(String ditujukanKepada) {
        this.ditujukanKepada = ditujukanKepada;
    }

    public String getNamaPengirim() {
        return namaPengirim;
    }

    public void setNamaPengirim(String namaPengirim) {
        this.namaPengirim = namaPengirim;
    }

    public String getNoHpPengirim() {
        return noHpPengirim;
    }

    public void setNoHpPengirim(String noHpPengirim) {
        this.noHpPengirim = noHpPengirim;
    }

    public String getEmailPengirim() {
        return emailPengirim;
    }

    public void setEmailPengirim(String emailPengirim) {
        this.emailPengirim = emailPengirim;
    }

    public String getNomorDisposisi() {
        return nomorDisposisi;
    }

    public void setNomorDisposisi(String nomorDisposisi) {
        this.nomorDisposisi = nomorDisposisi;
    }

    public RealmList<File> getFiles() {
        return files;
    }

    public void setFiles(RealmList<File> files) {
        this.files = files;
    }

    public RealmList<Disposisi> getDisposisiList() {
        return disposisiList;
    }

    public void setDisposisiList(RealmList<Disposisi> disposisiList) {
        this.disposisiList = disposisiList;
    }

    public RealmList<UserGroup> getDisposisiTargetList() {
        return disposisiTargetList;
    }

    public void setDisposisiTargetList(RealmList<UserGroup> disposisiTargetList) {
        this.disposisiTargetList = disposisiTargetList;
    }

    public RealmList<Komentar> getCommentList() {
        return commentList;
    }

    public void setCommentList(RealmList<Komentar> commentList) {
        this.commentList = commentList;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public int getIsKegiatan() {
        return isKegiatan;
    }

    public void setIsKegiatan(int isKegiatan) {
        this.isKegiatan = isKegiatan;
    }

    public int getStateDisposisi() {
        return stateDisposisi;
    }

    public void setStateDisposisi(int stateDisposisi) {
        this.stateDisposisi = stateDisposisi;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getStatusSurat() {
        return statusSurat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSuratMasuk() {
        return suratMasuk;
    }

    public void setSuratMasuk(boolean suratMasuk) {
        this.suratMasuk = suratMasuk;
    }

    public boolean isSuratUndangan() {
        return suratUndangan;
    }

    public void setSuratUndangan(boolean suratUdangan) {
        this.suratUndangan = suratUdangan;
    }

    public boolean isSuratAudiensi() {
        return suratAudiensi;
    }

    public void setSuratAudiensi(boolean suratAudiensi) {
        this.suratAudiensi = suratAudiensi;
    }

    public boolean isSuratUmum() {
        return suratUmum;
    }

    public void setSuratUmum(boolean suratUmum) {
        this.suratUmum = suratUmum;
    }

    public boolean isSuratSelesai() {
        return suratSelesai;
    }

    public void setSuratSelesai(boolean suratSelesai) {
        this.suratSelesai = suratSelesai;
    }

    public boolean isSuratBelum() {
        return suratBelum;
    }

    public void setSuratBelum(boolean suratBelum) {
        this.suratBelum = suratBelum;
    }

    public boolean isSuratKeluar() {
        return suratKeluar;
    }

    public void setSuratKeluar(boolean suratKeluar) {
        this.suratKeluar = suratKeluar;
    }

    public boolean isDisposisiMasuk() {
        return disposisiMasuk;
    }

    public void setDisposisiMasuk(boolean disposisiMasuk) {
        this.disposisiMasuk = disposisiMasuk;
    }

    public boolean isDisposisiKeluar() {
        return disposisiKeluar;
    }

    public void setDisposisiKeluar(boolean disposisiKeluar) {
        this.disposisiKeluar = disposisiKeluar;
    }
}
