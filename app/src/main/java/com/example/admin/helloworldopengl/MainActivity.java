package com.example.admin.helloworldopengl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    private GameRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameSurfaceView view = new GameSurfaceView(this);

        renderer = view.getRenderer();

        setContentView(view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        renderer.setTouchPoint(event.getAction(), event.getX(), event.getY());
        return true;
    }
}