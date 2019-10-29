package com.zhanghang.android4gl.opengl;

import android.content.Context;
import android.opengl.GLES20;

import androidx.annotation.RawRes;

import com.zhanghang.android4gl.IRender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class BaseDrawer implements IRender {
    int mProgram = -1;

    private int mVertexShaderId;

    private int mFragmentShaderId;

    private Context mContext;

    public BaseDrawer(Context context, @RawRes int vertexResId, @RawRes int fragmentResId) {
        mContext = context;
        mVertexShaderId = vertexResId;
        mFragmentShaderId = fragmentResId;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        final int vertexShader = createShader(GLES20.GL_VERTEX_SHADER, readStringFromRaw(mContext, mVertexShaderId));
        final int fragmentShader = createShader(GLES20.GL_FRAGMENT_SHADER, readStringFromRaw(mContext, mFragmentShaderId));
        mProgram = createProgram(vertexShader, fragmentShader);
    }

    private static String readStringFromRaw(Context context, int resourceId) {
        final InputStream in = context.getResources().openRawResource(resourceId);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int len;
        try {
            while (-1 != (len = in.read(buffer))) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new String(out.toByteArray());
    }

    private int createShader(int type, String code) {
        final int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        final int[] sillyReturns = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, sillyReturns, 0);
        if (GLES20.GL_FALSE == sillyReturns[0]) {
            final String shaderType = type == GLES20.GL_VERTEX_SHADER ? "VertexShader" : "FragmentShader";
            throw new RuntimeException(shaderType + " create failed: " + GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    private int createProgram(int vertexShader, int fragmentShader) {
        final int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        final int[] sillyReturns = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, sillyReturns, 0);
        if (GLES20.GL_FALSE == sillyReturns[0]) {
            throw new RuntimeException("Program link failed:" + GLES20.glGetProgramInfoLog(program));
        }

        return program;
    }

    public abstract void setVertexPoints(float[] sPos);

    @Override
    public void onSurfaceDestroy() {
        if (mProgram > 0) {
            GLES20.glDeleteProgram(mProgram);
            mProgram = -1;
        }
    }
}
