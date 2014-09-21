package com.kozzztya.cycletraining.custom;

import android.content.Context;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A modified Spinner that doesn't automatically select the first entry in the list.
 * <p/>
 * Shows the prompt if nothing is selected.
 * <p/>
 * Limitations: does not display prompt if the entry list is empty.
 */
public class PromptSpinner extends Spinner {

    public PromptSpinner(Context context) {
        super(context);
    }

    public PromptSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PromptSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(SpinnerAdapter orig) {
        final SpinnerAdapter adapter = newProxy(orig);
        super.setAdapter(adapter);

        //Select prompt item
        setSelection(-1);
    }

    protected SpinnerAdapter newProxy(SpinnerAdapter obj) {
        return (SpinnerAdapter) java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                new Class[]{SpinnerAdapter.class},
                new SpinnerAdapterProxy(obj));
    }

    @Override
    public void setSelection(int position) {
        try {
            final Method m = AdapterView.class.getDeclaredMethod(
                    "setNextSelectedPositionInt", int.class);
            m.setAccessible(true);
            m.invoke(this, position);

            final Method n = AdapterView.class.getDeclaredMethod(
                    "setSelectedPositionInt", int.class);
            n.setAccessible(true);
            n.invoke(this, position);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        requestLayout();
        invalidate();
    }

    /**
     * Intercepts setViewText() to display the prompt if position < 0
     */
    protected class SpinnerAdapterProxy implements InvocationHandler {

        protected SpinnerAdapter obj;
        protected Method setViewText;


        protected SpinnerAdapterProxy(SpinnerAdapter obj) {
            this.obj = obj;
            try {
                this.setViewText = SimpleCursorAdapter.class.getMethod(
                        "setViewText", TextView.class, String.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            try {
                return m.equals(setViewText) &&
                        getSelectedItemPosition() < 0 ?
                        setViewText.invoke(obj, args) :
                        m.invoke(obj, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void setViewText(TextView v, String text) {
            if (getSelectedItemPosition() < 0)
                v.setText(getPrompt());
            else
                v.setText(text);
        }
    }
}