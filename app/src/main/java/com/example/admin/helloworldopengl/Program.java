package com.example.admin.helloworldopengl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;

class Program {

    private String sVertexShaderSource;
    private String sFragmentShaderSource;

    private int mProgramId;
    private int[] textureId;

    // private Bitmap bmp;
    private int dwidth, dheight;

    private float[] mViewProjectionMatrix = new float[16];
    private List<Integer> val = new ArrayList<>();
    private List<Integer> aqval = new ArrayList<>();
    private List<String> aqoper = new ArrayList<>();

    private float depth;

    void createProgram() {

        int vertexShader;
        int fragmentShader;

        makeShaderSource();

        // VERTEX コンパイル
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, sVertexShaderSource);
        glCompileShader(vertexShader);

        // FRAGMENT コンパイル
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, sFragmentShaderSource);
        glCompileShader(fragmentShader);

        mProgramId = glCreateProgram();
        glAttachShader(mProgramId, vertexShader);
        glAttachShader(mProgramId, fragmentShader);
        glLinkProgram(mProgramId);
        glUseProgram(mProgramId);
    }

    private void makeShaderSource() {

        sVertexShaderSource =
                        "uniform mat4 vpMatrix;" +
                        "uniform mat4 wMatrix;" +
                        "attribute vec3 position;" +
                        "attribute vec2 uv;" +
                        "varying vec2 vuv;" +
                        "void main() {" +
                        "  gl_Position = vpMatrix * wMatrix * vec4(position, 1.0);" +
                        "  vuv = uv;" +
                        "}";

        sFragmentShaderSource =
                //"precision mediump float;" +
                        "varying vec2 vuv;" +
                        "uniform sampler2D texture;" +
                        "void main() {" +
                        "  gl_FragColor = texture2D(texture, vuv);" +
                        "}";
    }

    void setTexture(Resources res) {
        int count = 4;
        textureId = new int[count];

        glGenTextures(count, textureId, 0);
        glEnable(GL_TEXTURE_2D);

        Bitmap bmp1 = BitmapFactory.decodeResource(res, R.drawable.calcfont);
        bindTextrue(bmp1, 0);
        //bmp1.recycle();

        Bitmap bmp2 = BitmapFactory.decodeResource(res, R.drawable.cristal);
        //Bitmap bmp2 = bitmapTransparent(BitmapFactory.decodeResource(res, R.drawable.cristal));
        bindTextrue(bmp2, 1);
        //bmp2.recycle();

        Bitmap bmp3 = Bitmap.createBitmap(200, 100, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bmp3);
        Paint paint = new Paint();
        paint.setTextSize(18);
        paint.setARGB(0xff, 0x00, 0x00, 0x00);
        canvas.drawText("3D電卓", 0, 50, paint);
        GLUtils.texSubImage2D(GL_TEXTURE_2D, 0, 0, 0, bmp3);
        bindTextrue(bmp3, 2);
        //bmp2.recycle();

        Bitmap bmp4 = BitmapFactory.decodeResource(res, R.drawable.cristal);
        bindTextrue(bmp4, 3);
        //bmp2.recycle();
    }

    private void bindTextrue(Bitmap bmp, int idx) {
        glBindTexture(GL_TEXTURE_2D, textureId[idx]);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0);

        // テクスチャが縮小される時は線型フィルタリング
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // テクスチャが拡大される時は線型フィルタリング
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    void selectTexture(int idx) {
        glBindTexture(GL_TEXTURE_2D, textureId[idx]);
    }

    Bitmap bitmapTransparent(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];
        int color = bmp.getPixel(3, 3);

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        // 0,0 のピクセルと同じ色のピクセルを透明化する．
        Bitmap bmpTrans = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888 );
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if( pixels[x + y * width] == color){
                    pixels[x + y * width] = 0;
                }
            }
        }
        bmpTrans.eraseColor(Color.argb(0, 0, 0, 0));
        bmpTrans.setPixels(pixels, 0, width, 0, 0, width, height);

        return bmpTrans;
    }

    int getProgramId() {
        return mProgramId;
    }

    void setViewProjectionMatrix(int dimension) {

        this.depth = 100;

        float[] viewMatrix       = new float[16];
        float[] projectionMatrix = new float[16];

        float aspectRatio = (float) dwidth / dheight;

        glViewport(0, 0, dwidth, dheight);

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f);

        if(dimension == 2) {
            Matrix.orthoM(projectionMatrix, 0, -dwidth / 2f, dwidth / 2f, -dheight / 2, dheight / 2, 1f, depth);
            // Matrix.frustumM(projectionMatrix, 0, -1, 1, -1, 1, 1, 150);
        } else if(dimension == 3) {
            Matrix.perspectiveM(projectionMatrix, 0, 90f, aspectRatio, 1f, depth);
        }

        Matrix.multiplyMM(mViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

    }

    float[] getViewProjectionMatrix() {
        return mViewProjectionMatrix;
    }

    public void setDisplaySize(int width, int height) {
        this.dwidth = width;
        this.dheight = height;
    }

    private float[] getScreenM(Cube cube) {
        float[] positionM = {0f,0f,0f,1f};
        float[] w_posM = new float[4];
        float[] vp_w_posM = new float[4];
        float[] vp_vp_w_posM = new float[4];

        float[] viewPortMatrix = new float[] {
                dwidth / 2f,             0f, 0f, 0f,
                          0f, -dheight / 2f, 0f, 0f,
                          0f,             0f, 1f, 0f,
                dwidth / 2f,  dheight / 2f, 0f, 1f
        };
        Matrix.multiplyMV(w_posM, 0, cube.getWorldMatrix(), 0, positionM, 0);
        Matrix.multiplyMV(vp_w_posM, 0, mViewProjectionMatrix, 0, w_posM, 0);
        vp_w_posM[0] = vp_w_posM[0] / vp_w_posM[3];
        vp_w_posM[1] = vp_w_posM[1] / vp_w_posM[3];
        vp_w_posM[2] = vp_w_posM[2] / vp_w_posM[3];
        vp_w_posM[3] = vp_w_posM[3] / vp_w_posM[3];
        Matrix.multiplyMV(vp_vp_w_posM, 0, viewPortMatrix, 0, vp_w_posM, 0);

        return vp_vp_w_posM;

    }

    private float[] getScreenM(Menu menu) {
        float[] positionM = {0f,0f,0f,1f};
        float[] w_posM = new float[4];
        float[] vp_w_posM = new float[4];
        float[] vp_vp_w_posM = new float[4];

        float[] viewPortMatrix = new float[] {
                dwidth / 2f,             0f, 0f, 0f,
                0f, -dheight / 2f, 0f, 0f,
                0f,             0f, 1f, 0f,
                dwidth / 2f,  dheight / 2f, 0f, 1f
        };
        Matrix.multiplyMV(w_posM, 0, menu.getWorldMatrix(), 0, positionM, 0);
        Matrix.multiplyMV(vp_w_posM, 0, mViewProjectionMatrix, 0, w_posM, 0);
        vp_w_posM[0] = vp_w_posM[0] / vp_w_posM[3];
        vp_w_posM[1] = vp_w_posM[1] / vp_w_posM[3];
        vp_w_posM[2] = vp_w_posM[2] / vp_w_posM[3];
        vp_w_posM[3] = vp_w_posM[3] / vp_w_posM[3];
        Matrix.multiplyMV(vp_vp_w_posM, 0, viewPortMatrix, 0, vp_w_posM, 0);

        return vp_vp_w_posM;

    }

    boolean touchCollision(Cube cube, float tx, float ty, Context context) {
        float[]  scrnPos = getScreenM(cube);

        float pxlen = scrnPos[0] - tx;
        float pylen = scrnPos[1] - (ty-210); // -200は非描画範囲の厚み分を差し引いている（タイトルバー分）
        float centerDist = (float)Math.sqrt(pxlen * pxlen + pylen * pylen);

        float limitDist = cube.getLength() * 10f;

        if(centerDist < limitDist) {
            return true;
        } else {
            return false;
        }
    }

    boolean touchCollision(Menu menu, float tx, float ty, Context context) {
        float[]  scrnPos = getScreenM(menu);

        float pxlen = scrnPos[0] - tx;
        float pylen = scrnPos[1] - (ty-210); // -200は非描画範囲の厚み分を差し引いている（タイトルバー分）
        float centerDist = (float)Math.sqrt(pxlen * pxlen + pylen * pylen);

        float limitDist = menu.getLength() / 2f * 10f;

        if(centerDist < limitDist) {
            return true;
        } else {
            return false;
        }
    }

    void add(int num) {
        this.val.add(num % 10);
    }

    void assemble(String role) {
        int sum = 0;
        int cnt = val.size();
        if(cnt > 0) {
            for (int n : val) {
                cnt--;
                sum += (int) (n * Math.pow((double) 10, (double) cnt));
            }
            this.aqval.add(sum);
            this.aqoper.add(role);
            this.val = new ArrayList<>();
        }
    }

    void plus(List<Cube> cubeList) {

        String role = "plus";
        assemble(role);

        Cube c = new Cube(role, 12);
        c.setInitPosition(2, 0, -(dheight+1000));
        c.setTDY(0.05f);
        cubeList.add(c);
    }

    void multiply(List<Cube> cubeList) {

        String role = "mult";
        assemble(role);

        Cube c = new Cube(role, 13);
        c.setInitPosition(2, 0, -(dheight+1000));
        c.setTDY(0.05f);
        cubeList.add(c);
    }

    int equal() {

        String role = "equal";
        assemble(role);

        int sum = 0;
        if(aqval.size() > 0) {
            sum = aqval.get(0);
            for(int i = 1; i < aqval.size(); i++) {
                if("plus".equals(aqoper.get(i-1))) {
                    sum += aqval.get(i);
                } else if("mult".equals(aqoper.get(i-1))) {
                    sum *= aqval.get(i);
                } else if("equal".equals(aqoper.get(i-1))) {
                    // equal
                }
            }
        }
        this.aqval = new ArrayList<>();
        this.aqoper = new ArrayList<>();
        return sum;
    }

    void equal(List<Cube> cubeList) {

        for(Cube c : cubeList) {
            c.setTDY(-0.05f);
        }

        Cube.setPositionForm(1, 6, 12);
        int sum = equal();
        int cnt = 1;
        while(sum > 0) {
            int n = sum % 10;
            sum /= 10;

            Cube c;
            if(n == 0) {
                c = new Cube("number", 10);
            } else {
                c = new Cube("number", n);
            }

            int order = Cube.getRowCount() * Cube.getColCount() - cnt;
            c.setInitPosition(order, 0, dheight);
            c.setTDY(-0.05f);
            cubeList.add(c);

            cnt++;
        }

        Cube.setPositionForm(5, 3, 18f);
        {
            Cube c = new Cube("equal", 11);
            c.setInitPosition(1, 0, -(dheight+1000));
            c.setTDY(0.05f);
            cubeList.add(c);
        }
    }

    void removeFarObj(List<Cube> cubeList) {
        for(Cube c : cubeList) {
            if(c.getPZ() > depth) {
                int index = cubeList.indexOf(c);
                cubeList.remove(index);
                break;
            }
        }
    }
}
