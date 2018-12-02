package com.vt.disposisibandung.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.User;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Irvan on 6/29/2015.
 */
@EViewGroup(R.layout.layout_list_item_target)
public class TargetListItemView extends RelativeLayout {

    @ViewById(R.id.checkDisposisiTarget)
    protected CheckBox checkBoxDisposisiTarget;

    @ViewById(R.id.textDisposisiTarget)
    protected TextView textViewDisposisiTarget;

    private User user;

    private OnTargetCheckedChangeListener onTargetCheckedChangeListener;

    public TargetListItemView(Context context) {
        super(context);
    }

    public TargetListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TargetListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Click(R.id.textDisposisiTarget)
    protected void textClicked() {
        checkBoxDisposisiTarget.performClick();
    }

    public void setUser(final User user) {
        this.user = user;

        checkBoxDisposisiTarget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onTargetCheckedChangeListener != null)
                    onTargetCheckedChangeListener.onTargetCheckedChanged(user, isChecked);
            }
        });

        textViewDisposisiTarget.setText(user.getJabatan() == null ? "-" : user.getJabatan());
    }

    public void setChecked(boolean isChecked) {
        checkBoxDisposisiTarget.setChecked(isChecked);
    }

    public void setOnTargetCheckedChangeListener(OnTargetCheckedChangeListener onTargetCheckedChangeListener) {
        this.onTargetCheckedChangeListener = onTargetCheckedChangeListener;
    }

    public interface OnTargetCheckedChangeListener {
        void onTargetCheckedChanged(User user, boolean isChecked);
    }
}
