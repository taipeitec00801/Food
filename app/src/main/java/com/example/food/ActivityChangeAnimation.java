package com.example.food;

import android.app.Activity;
import android.transition.Fade;
import android.view.View;

public class ActivityChangeAnimation {


    //修改Activity切換的閃光效果
    public void fadeActivity(Activity activity) {
        Fade fade = new Fade();
        View decor = activity.getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container),true);
        fade.excludeTarget(android.R.id.statusBarBackground,true);
        fade.excludeTarget(android.R.id.navigationBarBackground,true);
        activity.getWindow().setEnterTransition(fade);
        activity.getWindow().setExitTransition(fade);
    }


}
