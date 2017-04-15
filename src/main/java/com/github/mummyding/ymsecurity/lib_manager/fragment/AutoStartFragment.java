package com.github.mummyding.ymsecurity.lib_manager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mummyding.ymbase.base.BaseFragment;
import com.github.mummyding.ymbase.util.RootUtil;
import com.github.mummyding.ymbase.util.ShellUtils;
import com.github.mummyding.ymbase.util.T;
import com.github.mummyding.ymsecurity.lib_manager.BootStartUtils;
import com.github.mummyding.ymsecurity.lib_manager.R;
import com.github.mummyding.ymsecurity.lib_manager.adapter.AutoStartAdapter;
import com.github.mummyding.ymsecurity.lib_manager.model.AutoStartInfo;

import java.util.ArrayList;
import java.util.List;


public class AutoStartFragment extends BaseFragment {

    Context mContext;
    public static final int REFRESH_BT = 111;
    private static final String ARG_POSITION = "position";
    private int position; // 0:普通软件，2 系统软件
    private View mRootView;
    private AutoStartAdapter mAutoStartAdapter;
    private ListView mListView;
    private LinearLayout mLinearLayoutBottom;

    private Button mDisableButton;
    private TextView mTopText;
    private List<AutoStartInfo> isSystemAuto = null;
    private List<AutoStartInfo> noSystemAuto = null;
    private int canDisableCom;


    private Handler mHandler = new Handler() {


        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_BT:
                    refeshButoom();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
    }

    private void findViewsById() {
        mListView = (ListView) bindView(R.id.listview);
        mLinearLayoutBottom = (LinearLayout) bindView(R.id.bottom_lin);
        mDisableButton = (Button) bindView(R.id.disable_button);
        mTopText = (TextView) bindView(R.id.topText);
    }

    private View bindView(int id) {
        return mRootView.findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_auto_start, container, false);
        mContext = getActivity();
        findViewsById();
        init();
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillData();
    }

    public void onClickDisable() {
        RootUtil.preparezlsu(mContext);
        disableAPP();
    }

    private void init() {
        mDisableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDisable();
            }
        });
    }

    private void disableAPP() {
        List<String> string = new ArrayList<>();
        for (AutoStartInfo auto : noSystemAuto) {
            if (auto.isEnable()) {
                String packageReceiverList[] = auto.getPackageReceiver().toString().split(";");
                for (int j = 0; j < packageReceiverList.length; j++) {
                    String cmd = "pm disable " + packageReceiverList[j];
                    //部分receiver包含$符号，需要做进一步处理，用"$"替换掉$
                    cmd = cmd.replace("$", "\"" + "$" + "\"");
                    string.add(cmd);
                }
            }
        }
        ShellUtils.CommandResult mCommandResult = ShellUtils.execCommand(string, true, true);
        if (mCommandResult.result == 0) {
            T.showLong(mContext, "应用已经全部禁止");
            for (AutoStartInfo auto : noSystemAuto) {
                if (auto.isEnable()) {
                    auto.setEnable(false);
                }
            }
            mAutoStartAdapter.notifyDataSetChanged();
            refeshButoom();
        } else {
            T.showLong(mContext, "该功能需要获取系统root权限，请允许获取root权限");
        }
    }


    private void fillData() {

        if (position == 0) {
            mTopText.setText("禁止下列应用自启,可提升运行速度");

        } else {
            mTopText.setText("禁止系统核心应用自启,将会影响手机的正常使用,请谨慎操作");

        }

        List<AutoStartInfo> mAutoStartInfo = BootStartUtils.fetchStartAutoApps(mContext);

        //   List<AutoStartInfo> mAutoStartInfo = BootStartUtils.fetchInstalledApps(mContext);
        noSystemAuto = new ArrayList<>();
        isSystemAuto = new ArrayList<>();

        for (AutoStartInfo a : mAutoStartInfo) {
            if (a.isSystem()) {
                isSystemAuto.add(a);
            } else {
                noSystemAuto.add(a);
            }
        }

        if (position == 0) {
            mAutoStartAdapter = new AutoStartAdapter(mContext, noSystemAuto, mHandler);
            mListView.setAdapter(mAutoStartAdapter);
            refeshButoom();
        } else {
            mAutoStartAdapter = new AutoStartAdapter(mContext, isSystemAuto, null);
            mListView.setAdapter(mAutoStartAdapter);
        }
    }

    private void refeshButoom() {
        if (position == 0) {
            canDisableCom = 0;
            for (AutoStartInfo autoS : noSystemAuto) {
                if (autoS.isEnable()) {
                    canDisableCom++;
                }
            }
            if (canDisableCom > 0) {
                mLinearLayoutBottom.setVisibility(View.VISIBLE);
                mDisableButton.setText("可优化" + canDisableCom + "款");
            } else {
                mLinearLayoutBottom.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
