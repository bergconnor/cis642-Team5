package edu.ksu.cis.waterquality;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.View;

public class CustomCheckbox extends AppCompatCheckBox
{

    public CustomCheckbox(Context context)
    {
        super(context);
    }

    public CustomCheckbox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomCheckbox(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed)
    {
        if (pressed && getParent() instanceof View && ((View) getParent()).isPressed())
        {
            return;
        }
        super.setPressed(pressed);
    }
}