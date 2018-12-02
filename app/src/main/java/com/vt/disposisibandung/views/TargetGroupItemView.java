package com.vt.disposisibandung.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.User;
import com.vt.disposisibandung.models.UserGroup;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by irvan on 12/2/15.
 */
@EViewGroup(R.layout.layout_group_item_target)
public class TargetGroupItemView extends RelativeLayout {

    @ViewById(R.id.labelDisposisiTargetGroup)
    protected TextView textViewLabelDisposisiTargetGroup;

    @ViewById(R.id.layoutDisposisiTargetList)
    protected LinearLayout layoutDisposisiTargetList;

    private UserGroup userGroup;
    private List<String> kepadaList;
    private TargetListItemView.OnTargetCheckedChangeListener onTargetCheckedChangeListener;

    public TargetGroupItemView(Context context) {
        super(context);
    }

    public TargetGroupItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TargetGroupItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Click(R.id.labelDisposisiTargetGroup)
    protected void labelClicked() {
        layoutDisposisiTargetList.setVisibility(layoutDisposisiTargetList.getVisibility() == VISIBLE ? GONE : VISIBLE);
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;

        textViewLabelDisposisiTargetGroup.setText(userGroup.getGroupName());

        for (User user : userGroup.getUsers()) {
            TargetListItemView view = TargetListItemView_.build(getContext());
            view.setOnTargetCheckedChangeListener(onTargetCheckedChangeListener);
            view.setUser(user);

            if (kepadaList != null && kepadaList.contains(user.getJabatan())) {
                view.setChecked(true);
                onTargetCheckedChangeListener.onTargetCheckedChanged(user, true);
            }

            layoutDisposisiTargetList.addView(view);
        }
    }

    public void setKepadaList(List<String> kepadaList) {
        this.kepadaList = kepadaList;
    }

    public void setOnTargetCheckedChangeListener(TargetListItemView.OnTargetCheckedChangeListener onTargetCheckedChangeListener) {
        this.onTargetCheckedChangeListener = onTargetCheckedChangeListener;
    }
}
