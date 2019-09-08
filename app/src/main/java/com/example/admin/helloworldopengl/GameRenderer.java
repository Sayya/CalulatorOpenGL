package com.example.admin.helloworldopengl;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class GameRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    Resources res;
    float px, py;
    long pf;

    static final int objNum = 13;

    private Program pgm;
    private List<Cube> cubeList;
    private Menu menu;
    private LogBoard logBoard;
    private Cube cubeSelected;

    GameRenderer(Resources res, Context context) {
        this.res = res;
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, javax.microedition.khronos.egl.EGLConfig eglConfig) {

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glClearColor(0.1f, 0.1f, 0.15f, 1.0f);

        pgm = new Program();

        pgm.createProgram();
        pgm.setTexture(res);

        Cube.setPositionForm(5, 3, 18f);
        Cube.setTexCount(objNum, 2);
        cubeList = new ArrayList<>();
        for(int i = 0; i < objNum; i++) {
            if(i < 10) {
                cubeList.add(new Cube("number", i+1));
            } else if(i == 10) {
                cubeList.add(new Cube("plus", 12));
            } else if(i == 11) {
                cubeList.add(new Cube("mult", 13));
            } else if(i == 12) {
                cubeList.add(new Cube("equal", 11));
            }
            cubeList.get(i).setInitPosition(i, 0, 0);
        }

        Menu.setPositionForm(3, 1, 40f);
        menu = new Menu("menu", 0);
        menu.setInitPosition(2, 0, 0);

        LogBoard.setPositionForm(3, 5, 0f);
        logBoard = new LogBoard("log", 0);
        logBoard.setInitPosition(13, 0, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        pgm.setDisplaySize(width, height);
        pgm.setViewProjectionMatrix(3);
        Cube.setStillFlg(true);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        pgm.removeFarObj(cubeList);

        for(Cube c1 : cubeList) {
            for(Cube c2 : cubeList) {
                if(c1.getID() > c2.getID() &&
                        Math.abs(c1.getPZ() - c2.getPZ()) < 10) {
                    c1.objCollision(c2);
                }
            }
            c1.draw(pgm);
        }

        //menu.draw(pgm);

        logBoard.draw(pgm);

        Cube.incFrameCount();
    }

    public void setTouchPoint(int mode, float tx, float ty) {

        float dx, dy;

        switch (mode) {
            case MotionEvent.ACTION_DOWN:

                this.px = tx;
                this.py = ty;
                this.pf = Cube.getFrameCount();

                if(Cube.getStillFlg()) {
                    Cube.setStillFlg(false);
                } else {
                    for(Cube c : cubeList) {
                        if(pgm.touchCollision(c, tx, ty, context)) {
                            cubeSelected = c;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:

                dx = (tx - px) / 250f;
                dy = 0;

                if(pgm.touchCollision(menu, tx, ty, context)) {
                    menu.setOffsetPosition(dx, dy);
                    if(menu.getPX() < 0f) {
                        menu.setInitPosition(1, 0, 0);
                    } else if(menu.getPX() > 40f) {
                        menu.setInitPosition(2, 0, 0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                dx = tx - px;
                dy = - (ty - py);
                long speed = Cube.getFrameCount() - pf;
                double volume = Math.sqrt(dx*dx + dy*dy);

                if(cubeSelected != null) {
                    if(volume > 60) {
                        cubeSelected.setMoveInfo(dx, dy, speed);
                    } else if(volume > 300) {
                        Log.d("RESULT", "ドラッグしました");
                    } else if(speed < 10) {
                        if(cubeSelected.getPZ() == 0) {
                            cubeSelected.drop(speed);
                            if("number".equals(cubeSelected.getRole())) {
                                pgm.add(cubeSelected.getQuant());
                                break;
                            } else if("equal".equals(cubeSelected.getRole())) {
                                pgm.equal(cubeList);
                                break;
                            } else if("plus".equals(cubeSelected.getRole())) {
                                pgm.plus(cubeList);
                                break;
                            } else if("mult".equals(cubeSelected.getRole())) {
                                pgm.multiply(cubeList);
                                break;
                            }
                        }
                    }
                    cubeSelected = null;
                }
                break;
        }

    }

}
