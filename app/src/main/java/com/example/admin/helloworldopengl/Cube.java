package com.example.admin.helloworldopengl;

import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;

class Cube {

    private static int oid = 0;
    private static long mFrameCount = 0;
    private static int rowCount, colCount;
    private static int rowTexture, colTexture;
    private static float space;
    private static final float refRate = 0.8f;
    private static boolean stillFlg = true;

    private List<Integer> colliFlg = new ArrayList();

    private FloatBuffer vertexBuffer;
    private FloatBuffer vertexUvBuffer;
    private ShortBuffer indexBuffer;
    private float[] worldMatrix = new float[16];
    private float length;
    private float px, py, pz;
    private float dx, dy, dz;
    private float tdx, tdy;
    private int id;
    private int quant;
    private String role;

    Cube(String role, int quant) {
        this.id = Cube.oid++;
        this.quant = quant;
        this.role = role;
        makeBuffer();
    }

    public int getID() {
        return id;
    }

    public int getQuant() { return quant; }

    public String getRole() { return role; }

    public static boolean getStillFlg() { return stillFlg; }
    public static void setStillFlg(boolean flg) { Cube.stillFlg = flg; Cube.mFrameCount = 0; }

    public static int getRowCount() { return Cube.rowCount; }
    public static int getColCount() { return Cube.colCount; }
    public static void setTexCount(int x, int y) {
        Cube.colTexture = x;
        Cube.rowTexture = y;
    }

    private void makeBuffer() {

        float left, right;
        float top, bottom;
        float back, front;

        this.length = 10f;
        left   = -length / 2f;
        right  =  length / 2f;
        top    = -length / 2f;
        bottom =  length / 2f;
        back   = -length / 2f;
        front  =  length / 2f;

        this.dx = this.dy = this.dz = 0.0f;
        this.tdx = this.tdy = 0.0f;

        float[] vertices;
        float[] vertices_uv;
        short[] indices;

        vertices = new float[] {
                // 前
                left,  top,    front,
                left,  bottom, front,
                right, bottom, front,

                left,  top,    front,
                right, bottom, front,
                right, top,    front,

                // 右
                right, top,    front,
                right, bottom, front,
                right, bottom, back,

                right, top,    front,
                right, bottom, back,
                right, top,    back,

                // 後
                right, top,    back,
                right, bottom, back,
                left,  bottom, back,

                right, top,    back,
                left,  bottom, back,
                left,  top,    back,

                // 左
                left,  top,    back,
                left,  bottom, back,
                left,  bottom, front,

                left,  top,    back,
                left,  bottom, front,
                left,  top,    front,

                // 上
                left,  top,    front,
                right, top,    front,
                right, top,    back,

                left,  top,    front,
                right, top,    back,
                left,  top,    back,

                // 下
                left,  bottom, front,
                left,  bottom, back,
                right, bottom, back,

                left,  bottom, front,
                right, bottom, back,
                right, bottom, front
        };

        float div_x = Cube.colTexture;
        float div_y = Cube.rowTexture;

        float rowpos = 2;

        float startx = (1f / div_x) * ((float)quant - 1f);
        float starty = (1f / div_y) * (rowpos);
        float endx = (1f / div_x) * (float)quant;
        float endy = (1f / div_y) * (rowpos - 1f);

        vertices_uv = new float[] {
                startx, starty, // 左上の頂点に対するUV座標
                startx, endy, // 左下の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                startx, starty, // 左上の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                endx, starty, // 右上の頂点に対するUV座標

                startx, starty, // 左上の頂点に対するUV座標
                startx, endy, // 左下の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                startx, starty, // 左上の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                endx, starty, // 右上の頂点に対するUV座標

                startx, starty, // 左上の頂点に対するUV座標
                startx, endy, // 左下の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                startx, starty, // 左上の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                endx, starty, // 右上の頂点に対するUV座標

                startx, starty, // 左上の頂点に対するUV座標
                startx, endy, // 左下の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                startx, starty, // 左上の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                endx, starty, // 右上の頂点に対するUV座標

                startx, starty, // 左上の頂点に対するUV座標
                startx, endy, // 左下の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                startx, starty, // 左上の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                endx, starty, // 右上の頂点に対するUV座標

                startx, starty, // 左上の頂点に対するUV座標
                startx, endy, // 左下の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                startx, starty, // 左上の頂点に対するUV座標
                endx, endy, // 右下の頂点に対するUV座標
                endx, starty  // 右上の頂点に対するUV座標
        };

        indices = new short[] {
                0,   1,  2,
                3,   4,  5,
                6,   7,  8,
                9,  10, 11,
                12, 13, 14,
                15, 16, 17,
                18, 19, 20,
                21, 22, 23,
                24, 25, 26,
                27, 28, 29,
                30, 31, 32,
                33, 34, 35
        };

        this.vertexBuffer = BufferUtil.convert(vertices);
        this.vertexUvBuffer = BufferUtil.convert(vertices_uv);
        this.indexBuffer  = BufferUtil.convert(indices);
    }

    void draw(Program pgm) {

        wallCollision();
        setWorldMatrix();

        // シェーダ内変数の識別子を取得
        // 何番目のattribute変数か
        int attLoc1 = glGetAttribLocation(pgm.getProgramId(),"position");
        int attLoc2 = glGetAttribLocation(pgm.getProgramId(), "uv");

        int uniLoc1 = glGetUniformLocation(pgm.getProgramId(),"vpMatrix");
        int uniLoc2 = glGetUniformLocation(pgm.getProgramId(),"wMatrix");
        int uniLoc3 = glGetUniformLocation(pgm.getProgramId(), "texture");

        // attribute属性を有効にする
        glEnableVertexAttribArray(attLoc1);
        glEnableVertexAttribArray(attLoc2);

        // uniform属性を設定する
        glUniform1i(uniLoc3, 0);

        // アプリケーション内のメモリから GPU へデータを転送するための処理
        // attribute属性を登録
        glVertexAttribPointer(attLoc1, 3, GL_FLOAT, false, 0, vertexBuffer);
        glVertexAttribPointer(attLoc2, 2, GL_FLOAT, false, 0, vertexUvBuffer);

        glUniformMatrix4fv(uniLoc1, 1, false, pgm.getViewProjectionMatrix(), 0);
        glUniformMatrix4fv(uniLoc2, 1, false, worldMatrix, 0);

        // 描画する API をコール
        pgm.selectTexture(0);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    }

    static void setPositionForm(int rowCount, int colCount, float space) {
        Cube.rowCount = rowCount;
        Cube.colCount = colCount;
        Cube.space = space;
    }

    void setInitPosition(int order, float offsetx, float offsety) {
        this.px = ((float)(order % colCount) - (float)(colCount - 1) / 2f) * space + offsetx;
        this.py = -((float)(order / colCount) - (float)(rowCount - 1) / 2f) * space + offsety;
        this.pz = 0f;
    }

    void setMoveInfo(float dx, float dy, long speed) {
        this.dx = dx / (speed * 30f);
        this.dy = dy / (speed * 30f);
    }

    private void setWorldMatrix() {

        dx += tdx;
        dy += tdy;
        tdx = tdy = 0.0f;

        if(!stillFlg) {
            px += dx;
            py += dy;
            pz += dz;
        }

        Matrix.setIdentityM(worldMatrix, 0);
        Matrix.translateM(worldMatrix, 0, px, py, -50 - pz);

        if(!stillFlg) {
            // 図形が回転するアニメーションをさせるために、回転の座標変換行列を生成
            Matrix.rotateM(worldMatrix, 0, (float) (quant - 6) * (float) -mFrameCount * 0.1f, 0, 1, 0);
            Matrix.rotateM(worldMatrix, 0, (float) -mFrameCount * 0.05f, 0, 0, 1);
        }

    }

    public float[] getWorldMatrix() { return worldMatrix; }

    static void incFrameCount() {
        Cube.mFrameCount++;
    }
    static long getFrameCount() {
        return mFrameCount;
    }

    private void wallCollision() {
        int width = 30;
        int height = 50;

        if(px < -width) {
            dx = -refRate * dx;
            px = -width;
        } else if(width < px) {
            dx = -refRate * dx;
            px = width;
        }
        if(py < -height) {
            dy = -refRate * dy;
            py = -height;
        } else if(height < py) {
            dy = -refRate * dy;
            py = height;
        }
    }

    public float getLength() {
        return length;
    }

    public float getPX() {
        return px;
    }

    public float getPY() {
        return py;
    }

    public float getPZ() {
        return pz;
    }

    public float getDX() {
        return dx;
    }

    public float getDY() {
        return dy;
    }

    public void setTDX(float tdx) {
        this.tdx = tdx;
    }

    public void setTDY(float tdy) {
        this.tdy = tdy;
    }

    public void objCollision(Cube oglobj) {
        float pxlen = px - oglobj.getPX();
        float pylen = py - oglobj.getPY();
        float centerDist = (float)Math.sqrt(pxlen * pxlen + pylen * pylen);

        float dxlen = dx - oglobj.getDX();
        float dylen = dy - oglobj.getDY();
        float vectorDist = (float)Math.sqrt(dxlen * dxlen + dylen * dylen);

        float limitDist = (length + oglobj.getLength()) * 0.6f;

        float dot = 0.0f;

        if(centerDist < limitDist && vectorDist != 0.0f) {
            dot = (dxlen * dxlen + dylen * dylen) / vectorDist;

            if(!colliFlg.contains(oglobj.getID())  ) {
                float massA = (float)oglobj.getQuant() / ((float)quant + (float)oglobj.getQuant());
                this.tdx = -massA * (1f + refRate * refRate) * dot * dxlen / vectorDist;
                this.tdy = -massA * (1f + refRate * refRate) * dot * dylen / vectorDist;

                float massB = (float)quant / ((float)quant + (float)oglobj.getQuant());
                oglobj.setTDX(massB * (1f + refRate * refRate) * dot * dxlen / vectorDist);
                oglobj.setTDY(massB * (1f + refRate * refRate) * dot * dylen / vectorDist);
            }
            colliFlg.add(oglobj.getID());
        } else {
            if(colliFlg.contains(oglobj.getID())) {
                colliFlg.remove(colliFlg.indexOf(oglobj.getID()));
            }
        }
    }

    public void drop(long speed) {
        this.dz = 50f / (speed * 30f);
    }
}
