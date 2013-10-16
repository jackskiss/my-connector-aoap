package com.obigo.baidumusic.standard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.obigo.baidumusic.standard.R;

public class MenuButton extends LinearLayout implements OnClickListener {
    private Button mOn, mOff;
    private OnCheckedChangeListener mListener;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean isChecked);
    }

    public MenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MenuButton(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = null;
        
        View v = inflater.inflate(R.layout.menu_button, this);
        if(v instanceof LinearLayout) {
            layout = (LinearLayout)v;
        }
        
        if(layout != null) {
            View v1 = layout.findViewById(R.id.button_on);
            if(v1 instanceof Button) {
                mOn = (Button)v1;
                mOn.setOnClickListener(this);
            }
            
            View v2 = layout.findViewById(R.id.button_off);
            if(v2 instanceof Button) {
                mOff = (Button)v2;
                mOff.setOnClickListener(this);
            }
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mListener = listener;
    }

    public void setChecked(boolean isChecked) {
        if (isChecked) {
            mOn.setVisibility(View.VISIBLE);
            mOff.setVisibility(View.GONE);
        } else {
            mOn.setVisibility(View.GONE);
            mOff.setVisibility(View.VISIBLE);
        }

        if (mListener != null) {
            mListener.onCheckedChanged(isChecked);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mOn) {
            mOn.setVisibility(View.GONE);
            mOff.setVisibility(View.VISIBLE);

            if (mListener != null) {
                mListener.onCheckedChanged(false);
            }
        } else if (view == mOff) {
            mOn.setVisibility(View.VISIBLE);
            mOff.setVisibility(View.GONE);

            if (mListener != null) {
                mListener.onCheckedChanged(true);
            }
        }
    }
}
