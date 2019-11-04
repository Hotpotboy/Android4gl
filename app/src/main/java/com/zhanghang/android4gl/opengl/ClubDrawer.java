package com.zhanghang.android4gl.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.zhanghang.android4gl.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ClubDrawer extends BaseDrawer {
    private static final float UNIT = 1f;

    private static final float[] CUBE_TRIANGLES_VERTEXS = {
            0, 0, 0,//V1
            0, UNIT, 0,//v4
            UNIT, 0, 0, // v2

            UNIT, 0, 0, // v2
            0, UNIT, 0,//v4
            UNIT, UNIT, 0,//V3

            UNIT, 0, 0, // v2
            UNIT, UNIT, 0,//V3
            UNIT, UNIT, UNIT,//V7

            UNIT, 0, 0, // v2
            UNIT, UNIT, UNIT,//V7
            UNIT, 0, UNIT,//V6

            UNIT, UNIT, UNIT,//V7
            UNIT, 0, UNIT,//V6
            0, UNIT, UNIT, // v8

            UNIT, 0, UNIT,//V6
            0, UNIT, UNIT, // v8
            0, 0, UNIT, // v5

            0, UNIT, UNIT, // v8
            0, 0, UNIT, // v5
            0, 0, 0,//V1

            0, 0, 0,//V1
            0, UNIT, 0,//v4
            0, UNIT, UNIT, // v8

            0, UNIT, 0,//v4
            0, UNIT, UNIT, // v8
            UNIT, UNIT, UNIT,//V7

            0, UNIT, 0,//v4
            UNIT, UNIT, UNIT,//V7
            UNIT, UNIT, 0,//V3

            0, 0, 0,//V1
            0, 0, UNIT, // v5
            UNIT, 0, UNIT,//V6

            0, 0, 0,//V1
            UNIT, 0, UNIT,//V6
            UNIT, 0, 0, // v2
    };
    private static final String MVPMATRIX_NAME = "mvpMatrix";

    //立方体的顶点颜色
    private float[] COLORS = {
            //背面矩形颜色
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,

            //右侧矩形颜色
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,

            //前面矩形颜色
            1f, 1f, 0.5f, 1f,
            1f, 0.9f, 0.5f, 1f,
            1f, 0.7f, 0.5f, 1f,
            1f, 0.5f, 0.5f, 1f,
            1f, 0.3f, 0.5f, 1f,
            1f, 0.1f, 0.5f, 1f,

            //左侧矩形颜色
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,

            //顶部矩形颜色
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,

            //底部矩形颜色
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f
    };

    private static final String POSITION_NAME = "aPosition";
    private static final String COLORS_NAME = "aColor";

    private int mPositionHandler;

    private int mColorHandler;

    private int mMVPMatrixHandler;

    private FloatBuffer mCubeTrianglesVertexsBuffer;

    private FloatBuffer mColorBuffer;

    private float[] mProjectMatrix = new float[16];

    private float[] mViewMatrix = new float[16];

    private float[] mMVPMatrix;

    public ClubDrawer(Context context) {
        super(context, R.raw.cube_vertex_shader, R.raw.cube_fragment_shader);

        ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(CUBE_TRIANGLES_VERTEXS.length * 4);
        tmpBuffer.order(ByteOrder.nativeOrder());
        mCubeTrianglesVertexsBuffer = tmpBuffer.asFloatBuffer();
        mCubeTrianglesVertexsBuffer.put(CUBE_TRIANGLES_VERTEXS);
        mCubeTrianglesVertexsBuffer.position(0);

        tmpBuffer = ByteBuffer.allocateDirect(COLORS.length * 4);
        tmpBuffer.order(ByteOrder.nativeOrder());
        mColorBuffer = tmpBuffer.asFloatBuffer();
        mColorBuffer.put(COLORS);
        mColorBuffer.position(0);
    }

    @Override
    public void setVertexPoints(float[] sPos) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        mPositionHandler = GLES20.glGetAttribLocation(mProgram, POSITION_NAME);
        mColorHandler = GLES20.glGetAttribLocation(mProgram, COLORS_NAME);
        mMVPMatrixHandler = GLES20.glGetUniformLocation(mProgram, MVPMATRIX_NAME);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3f, 20);
    }

    private float mRotationAngle = -10;

    private long mCurrentTime;

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(1, 1, 0, 1);

        Matrix.setIdentityM(mViewMatrix, 0);
        if (System.currentTimeMillis() - mCurrentTime >= 500) {
            mRotationAngle += 10;
            mCurrentTime = System.currentTimeMillis();
        }
        Matrix.rotateM(mViewMatrix, 0, mRotationAngle, 0, 1, 0);
        Matrix.translateM(mViewMatrix, 0, 0, 0, 10f);
        float[] tmp = new float[16];
        System.arraycopy(mViewMatrix, 0, tmp, 0, 16);
        Matrix.invertM(mViewMatrix, 0, tmp, 0);

        if (mMVPMatrix == null) {
            mMVPMatrix = new float[16];
        }
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mPositionHandler);
        GLES20.glVertexAttribPointer(mPositionHandler, 3, GLES20.GL_FLOAT, false, 0, mCubeTrianglesVertexsBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandler);
        GLES20.glVertexAttribPointer(mColorHandler, 4, GLES20.GL_FLOAT, false, 0, mColorBuffer);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandler, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, CUBE_TRIANGLES_VERTEXS.length / 3);

        GLES20.glDisableVertexAttribArray(mPositionHandler);
        GLES20.glDisableVertexAttribArray(mColorHandler);
    }
}
