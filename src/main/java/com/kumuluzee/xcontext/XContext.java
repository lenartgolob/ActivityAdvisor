package com.kumuluzee.xcontext;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class XContext{
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}