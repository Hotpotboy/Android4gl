package com.zhanghang.android4gl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zhanghang.android4gl.opengl.ClubDrawer;
import com.zhanghang.android4gl.view.GLTextureView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLTextureView glTextureView = new GLTextureView(getApplicationContext());
        setContentView(glTextureView);

        ClubDrawer clubDrawer = new ClubDrawer(getApplicationContext());
        glTextureView.setRender(clubDrawer);
    }
}
