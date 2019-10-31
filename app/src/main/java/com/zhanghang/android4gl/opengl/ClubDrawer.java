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
    private static final float UNIT = 10f;

    private static final float[] CUBE_TRIANGLES_VERTEXS  =
            {
                    // Front face
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,

                    // Right face
                    1.0f, 1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f, 1.0f, -1.0f,

                    // Back face
                    1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,

                    // Left face
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, 1.0f,
                    -1.0f, 1.0f, 1.0f,

                    // Top face
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,

                    // Bottom face
                    1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
            };

//    private static final float[] CUBE_TRIANGLES_VERTEXS = {
//            0, 0, 0,//V1
//            0, UNIT, 0,//v4
//            UNIT, 0, 0, // v2
//
//            UNIT, 0, 0, // v2
//            0, UNIT, 0,//v4
//            UNIT, UNIT, 0,//V3
//
//            UNIT, 0, 0, // v2
//            UNIT, UNIT, 0,//V3
//            UNIT, UNIT, UNIT,//V7
//
//            UNIT, 0, 0, // v2
//            UNIT, UNIT, UNIT,//V7
//            UNIT, 0, UNIT,//V6
//
//            UNIT, UNIT, UNIT,//V7
//            UNIT, 0, UNIT,//V6
//            0, UNIT, UNIT, // v8
//
//            UNIT, 0, UNIT,//V6
//            0, UNIT, UNIT, // v8
//            0, 0, UNIT, // v5
//
//            0, UNIT, UNIT, // v8
//            0, 0, UNIT, // v5
//            0, 0, 0,//V1
//
//            0, 0, 0,//V1
//            0, UNIT, 0,//v4
//            0, UNIT, UNIT, // v8
//
//            0, UNIT, 0,//v4
//            0, UNIT, UNIT, // v8
//            UNIT, UNIT, UNIT,//V7
//
//            0, UNIT, 0,//v4
//            UNIT, UNIT, UNIT,//V7
//            UNIT, UNIT, 0,//V3
//
//            0, 0, 0,//V1
//            0, 0, UNIT, // v5
//            UNIT, 0, UNIT,//V6
//
//            0, 0, 0,//V1
//            UNIT, 0, UNIT,//V6
//            UNIT, 0, 0, // v2
//    };
    private static final String MVPMATRIX_NAME = "u_mvpMatrix";

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
    private FloatBuffer mMVPMatrixBuffer;
    private float[] mMVPMatrix;
    private float[] mModelMatrix = new float[16];

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
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 0.1f, 10);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(1, 1, 1, 1);


        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -0.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);


        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(mModelMatrix, 0, 90, 1.0f, 1.0f, 0.0f);

        if (mMVPMatrix == null) {
            mMVPMatrix = new float[16];
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mMVPMatrix, 0);

        if (mMVPMatrixBuffer == null) {
            mMVPMatrixBuffer = ByteBuffer.allocateDirect(16 * 4).asFloatBuffer();
        }
        mMVPMatrixBuffer.position(0);
        mMVPMatrixBuffer.put(mMVPMatrix);
        mMVPMatrixBuffer.position(0);

        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mPositionHandler);
        GLES20.glVertexAttribPointer(mPositionHandler, 3, GLES20.GL_FLOAT, false, 0, mCubeTrianglesVertexsBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandler);
        GLES20.glVertexAttribPointer(mColorHandler, 4, GLES20.GL_FLOAT, false, 0, mColorBuffer);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandler, 1, false, mMVPMatrixBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, CUBE_TRIANGLES_VERTEXS.length / 3);

        GLES20.glDisableVertexAttribArray(mPositionHandler);
        GLES20.glDisableVertexAttribArray(mColorHandler);
    }
}
