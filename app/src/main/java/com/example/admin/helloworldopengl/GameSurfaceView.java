package com.example.admin.helloworldopengl;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;

public class GameSurfaceView extends GLSurfaceView {
    private static final int OPENGL_ES_VERSION = 2;

    private GameRenderer renderer;

    public GameSurfaceView(Context context) {
        super(context);

        Resources res = getResources();
        renderer = new GameRenderer(res, context);

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    public GameRenderer getRenderer() {
        return renderer;
    }
}