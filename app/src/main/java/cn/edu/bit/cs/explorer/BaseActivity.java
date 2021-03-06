package cn.edu.bit.cs.explorer;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import cn.edu.bit.cs.explorer.R;

/**
 * Created by entalent on 2015/12/2.
 */
public class BaseActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);

        setContentView(R.layout.activity_base_main);
        frameLayout = (FrameLayout) findViewById(R.id.id_main_content);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setTitle("BaseActivity");
        setToolbarBackground(getResources().getDrawable(R.drawable.bg));
        setSupportActionBar(toolbar);
    }

    protected void setContent(int resId) {
        View view = LayoutInflater.from(this).inflate(resId, null);
        view.setFitsSystemWindows(true);
        frameLayout.addView(view);
    }

    protected void setToolbarBackgroundColor(int color) {
        toolbar.setBackgroundColor(color);
    }

    protected void setToolbarBackground(Drawable drawable) {
        toolbar.setBackground(drawable);
    }
}
