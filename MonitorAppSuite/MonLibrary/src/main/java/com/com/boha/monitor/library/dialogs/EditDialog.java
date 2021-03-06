package com.com.boha.monitor.library.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boha.monitor.library.R;
import com.com.boha.monitor.library.dto.CheckPointDTO;
import com.com.boha.monitor.library.dto.CompanyStaffDTO;
import com.com.boha.monitor.library.dto.ProjectDTO;
import com.com.boha.monitor.library.dto.ProjectStatusTypeDTO;
import com.com.boha.monitor.library.dto.TaskStatusDTO;
import com.com.boha.monitor.library.dto.transfer.RequestDTO;
import com.com.boha.monitor.library.dto.transfer.ResponseDTO;
import com.com.boha.monitor.library.util.ErrorUtil;
import com.com.boha.monitor.library.util.SharedUtil;
import com.com.boha.monitor.library.util.Statics;
import com.com.boha.monitor.library.util.ToastUtil;
import com.com.boha.monitor.library.util.WebSocketUtil;

/**
 * Created by aubreyM on 2014/10/18.
 */
public class EditDialog extends DialogFragment {

    Context context;
    TextView txtTitle;
    EditText editName, editDesc;
    ProgressBar progressBar;
    Button btnCancel, btnSave, btnDelete;
    CompanyStaffDTO companyStaff;
    View view;
    int action;
    public static final int
            ACTION_ADD = 1,
            ACTION_UPDATE = 2,
            ACTION_DELETE = 3;
    static final String LOG = EditDialog.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.edit_dialog, container);
        editName = (EditText) view.findViewById(R.id.EDD_edit);
        editDesc = (EditText) view.findViewById(R.id.EDD_desc);
        txtTitle = (TextView) view.findViewById(R.id.EDD_title);
        if (taskStatus != null)
            txtTitle.setText(context.getResources().getString(R.string.task_status));
        if (projectStatusType != null)
            txtTitle.setText(context.getResources().getString(R.string.project_status));
        editDesc.setVisibility(View.GONE);
        progressBar = (ProgressBar) view.findViewById(R.id.EDD_progress);
        progressBar.setVisibility(View.GONE);

        btnCancel = (Button) view.findViewById(R.id.EDD_btnCancel);
        btnSave = (Button) view.findViewById(R.id.EDD_btnChange);
        btnDelete = (Button) view.findViewById(R.id.EDD_btnDelete);
        getDialog().setTitle(context.getResources().getString(R.string.app_name));
        switch (action) {
            case ACTION_ADD:
                btnDelete.setVisibility(View.GONE);
                btnSave.setText(context.getString(R.string.save));
                break;
            case ACTION_UPDATE:
                btnSave.setText(context.getString(R.string.change_item));
                fillForm();
                break;
            case ACTION_DELETE:
                btnDelete.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                fillForm();
                break;
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dismiss();

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                update();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ToastUtil.toast(context, context.getResources().getString(R.string.under_cons));
            }
        });


        return view;
    }

    private TaskStatusDTO taskStatus;
    private ProjectStatusTypeDTO projectStatusType;
    private ProjectDTO project;
    private CheckPointDTO checkPoint;

    private void fillForm() {
        if (taskStatus != null) {
            editName.setText(taskStatus.getTaskStatusName());
            txtTitle.setText(context.getResources().getString(R.string.task_status));
        }
        if (projectStatusType != null) {
            editName.setText(projectStatusType.getProjectStatusName());
            txtTitle.setText(context.getResources().getString(R.string.project_status));
        }
        if (project != null) {
            editName.setText(project.getProjectName());
            editDesc.setText(project.getDescription());
            editDesc.setVisibility(View.VISIBLE);
            txtTitle.setText(context.getResources().getString(R.string.company_projects));

        }

    }

    private void update() {
        RequestDTO w = new RequestDTO();
        if (editName.getText().toString().isEmpty()) {
            ToastUtil.toast(context,context.getString(R.string.enter_name));
            return;
        }
        switch (action) {
            case ACTION_ADD:
                if (taskStatus != null) {
                    w.setRequestType(RequestDTO.ADD_COMPANY_TASK_STATUS);
                    taskStatus.setCompanyID(SharedUtil.getCompany(context).getCompanyID());
                    taskStatus.setTaskStatusName(editName.getText().toString());
                    w.setTaskStatus(taskStatus);
                }
                if (projectStatusType != null) {
                    w.setRequestType(RequestDTO.ADD_COMPANY_PROJECT_STATUS_TYPE);
                    projectStatusType.setCompanyID(SharedUtil.getCompany(context).getCompanyID());
                    projectStatusType.setProjectStatusName(editName.getText().toString());
                    w.setProjectStatusType(projectStatusType);
                }
                if (project != null) {
                    w.setRequestType(RequestDTO.REGISTER_PROJECT);
                    project.setCompanyID(SharedUtil.getCompany(context).getCompanyID());
                    project.setProjectName(editName.getText().toString());
                    project.setDescription(editDesc.getText().toString());
                    w.setProject(project);
                }

                break;
            case ACTION_UPDATE:
                if (taskStatus != null) {
                    w.setRequestType(RequestDTO.UPDATE_COMPANY_TASK_STATUS);
                    w.setTaskStatus(taskStatus);
                }
                if (projectStatusType != null) {
                    w.setRequestType(RequestDTO.UPDATE_COMPANY_PROJECT_STATUS_TYPE);
                    w.setProjectStatusType(projectStatusType);
                }
                break;
        }

        progressBar.setVisibility(View.VISIBLE);
        WebSocketUtil.sendRequest(context, Statics.COMPANY_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (!ErrorUtil.checkServerError(context,response)) {
                            return;
                        }
                        dismiss();
                        listener.onComplete();
                    }
                });

            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
               listener.onError(message);
                //dismiss();
            }
        });

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTaskStatus(TaskStatusDTO taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setProjectStatusType(ProjectStatusTypeDTO projectStatusType) {
        this.projectStatusType = projectStatusType;
    }
    public interface EditDialogListener {
        public void onComplete();
        public void onError(String message);
    }
    EditDialogListener listener;

    public void setListener(EditDialogListener listener) {
        this.listener = listener;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public void setCheckPoint(CheckPointDTO checkPoint) {
        this.checkPoint = checkPoint;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
