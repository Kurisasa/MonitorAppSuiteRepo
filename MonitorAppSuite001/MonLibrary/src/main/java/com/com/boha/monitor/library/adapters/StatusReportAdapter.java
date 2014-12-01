package com.com.boha.monitor.library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.monitor.library.R;
import com.com.boha.monitor.library.dto.ProjectSiteTaskStatusDTO;
import com.com.boha.monitor.library.dto.TaskStatusDTO;
import com.com.boha.monitor.library.util.Statics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StatusReportAdapter extends ArrayAdapter<ProjectSiteTaskStatusDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<ProjectSiteTaskStatusDTO> mList;
    private Context ctx;

   public StatusReportAdapter(Context context, int textViewResourceId,
                              List<ProjectSiteTaskStatusDTO> list) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    View view;


    static class ViewHolderItem {
        TextView txtTaskName, txtStatus,txtDate;
        TextView txtColor, txtProject, txtSite, txtStaff;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolderItem item;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            item = new ViewHolderItem();
            item.txtTaskName = (TextView) convertView
                    .findViewById(R.id.SRC_txtTask);
            item.txtProject = (TextView) convertView
                    .findViewById(R.id.SRC_txtProject);
            item.txtStatus = (TextView) convertView
                    .findViewById(R.id.SRC_txtStatus);
            item.txtDate = (TextView) convertView
                    .findViewById(R.id.SRC_txtDate);
            item.txtColor = (TextView) convertView
                    .findViewById(R.id.SRC_txtColor);
            item.txtSite = (TextView) convertView
                    .findViewById(R.id.SRC_txtProjectSite);
            item.txtStaff = (TextView) convertView
                    .findViewById(R.id.SRC_txtStaff);

            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }

        final ProjectSiteTaskStatusDTO p = mList.get(position);
        item.txtTaskName.setText(p.getTask().getTaskName());
        item.txtProject.setText(p.getProjectName());
        item.txtDate.setText(sdf.format(p.getStatusDate()));
        item.txtStatus.setText(p.getTaskStatus().getTaskStatusName());
        item.txtSite.setText(p.getProjectSiteName());
        if (p.getStaffName() != null) {
            item.txtStaff.setText(p.getStaffName());
        }
        switch (p.getTaskStatus().getStatusColor()) {
            case TaskStatusDTO.STATUS_COLOR_GREEN:
                item.txtColor.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.xgreen_oval_small));
                break;
            case TaskStatusDTO.STATUS_COLOR_RED:
                item.txtColor.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.xred_oval_small));
                break;
            case TaskStatusDTO.STATUS_COLOR_YELLOW:
                item.txtColor.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.xorange_oval_small));
                break;
        }


        Statics.setRobotoFontLight(ctx,item.txtDate);
        Statics.setRobotoFontLight(ctx, item.txtStatus);

        animateView(convertView);
        return (convertView);
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
        a.setDuration(500);
        if (view == null)
            return;
        view.startAnimation(a);
    }

    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", x);
    static final DecimalFormat df = new DecimalFormat("###,###,##0.0");
}
