package com.zhanghang.android4gl.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.HandlerThread;
import android.view.TextureView;
import android.view.View;

import com.zhanghang.android4gl.IRender;

/**
 * <p>
 * 该类主要实现了textureView的open gl环境的搭建;
 * 总体思路与{@link android.opengl.GLSurfaceView}一致，会开辟一条新的线程来处理OPEN GL级的渲染操作;
 * 而该类与{@link android.opengl.GLSurfaceView}依然有以下几个较为明显的区别:
 * <ul>
 * <li>处理open gl的线程是基于@{@link HandlerThread}</li>
 * <li>视图的生命周期与{@link android.opengl.GLSurfaceView}有一些区别，具体可见下面的生命周期说明</li>
 * <li>用来承载open gl的egl surface来源于{@link TextureView}</li>
 * <ul/>
 * 整个渲染线程与该视图的生命周期基本一致，以此来保证渲染线程中的GL环境不会被污染;
 * 具体而言，视图的生命周期可以分为以下8个步骤:
 * <ol>
 * <li>视图创建；该阶段会创建渲染线程，并且会初始化EGL Display及EGL Context对象</li>
 * <li>
 * 当视图的SurfaceTexture可用的时候，会根据该SurfaceTexture创建EGL Surface对象，
 * 并将渲染线程的EGL环境绑定成已创建的EGL Display对象、EGL Context对象、EGL Surface对象.
 * </li>
 * <li>回调{@linkplain SurfaceTextureListener#onSurfaceTextureAvailable(SurfaceTexture, int, int) onSurfaceTextureAvailable}监听器</li>
 * <li>回调{@linkplain SurfaceTextureListener#onSurfaceTextureSizeChanged(SurfaceTexture, int, int) onSurfaceTextureSizeChanged}监听器</li>
 * <li>渲染当前帧；该步骤是一个循环操作；</li>
 * <li>当视图的SurfaceTexture当surface不可用时，销毁EGL Surface对象；</li>
 * <li>回调{@linkplain SurfaceTextureListener#onSurfaceTextureDestroyed(SurfaceTexture) onSurfaceTextureDestroyed}监听器</li>
 * <li>当整个视图不可用时，销毁EGL Display对象和EGL Context对象；该步骤需要上层主动调用{@link GLTextureView#destroy()}方法</li>
 * <ol/>
 * 其他：
 * <ul>
 * <li>一般而言，当视图不可见或者只是从视图树上剥离（有重新被添加到视图树的可能）那么只会重新执行2~7步;</li>
 * <li>此外，一般在视图对应的Activity或者Fragment的onDestroy方法中调用{@link GLTextureView#destroy()}方法</li>
 * </ul>
 * </p>
 */
public class GLTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private RenderThread mRenderThread;

    private TextureView.SurfaceTextureListener mOuterSurfaceTextureListener;

    public GLTextureView(Context context) {
        super(context);
        mRenderThread = new RenderThread(this);
        super.setSurfaceTextureListener(this);
    }

    /**
     * <p>
     * 当TextureView进入不可见状态(例如切后台)的时候，TextureView的surface并不会销毁；
     * 因此，为了防止渲染资源继续被持有，会重写onVisibilityChanged方法，
     * 使得视图不可见时，执行与surface被销毁时相同的逻辑。
     * </p>
     * <p>
     * 此方法会先于onSurfaceTextureAvailable/onSurfaceTextureDestroyed方法执行。
     * </p>
     *
     * @param changedView
     * @param visibility
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mRenderThread != null) {
            if (visibility == View.VISIBLE) {
                mRenderThread.dealSurfaceTextureAvailable();
            } else {
                mRenderThread.dealSurfaceTextureDestroyed();
            }
        }
    }

    @Override
    public void setSurfaceTextureListener(TextureView.SurfaceTextureListener surfaceTextureListener) {
        mOuterSurfaceTextureListener = surfaceTextureListener;
    }

    public void setRender(IRender render) {
        if (mRenderThread != null) {
            mRenderThread.mRender = render;
        }
    }

    public void runOnGLThread(Runnable r) {
        if (mRenderThread != null
                && mRenderThread.mHandler != null) {
            mRenderThread.mHandler.post(new RenderRunnable(r));
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mRenderThread != null) {
            mRenderThread.onSurfaceTextureAvailable(surface, width, height);
            if (mOuterSurfaceTextureListener != null) {
                mOuterSurfaceTextureListener.onSurfaceTextureAvailable(surface, width, height);
            }
            if (mOuterSurfaceTextureListener != null) {
                mOuterSurfaceTextureListener.onSurfaceTextureSizeChanged(surface, width, height);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mRenderThread != null) {
            mRenderThread.onSurfaceTextureDestroyed(surface);
            if (mOuterSurfaceTextureListener != null) {
                return mOuterSurfaceTextureListener.onSurfaceTextureDestroyed(surface);
            }
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (mOuterSurfaceTextureListener != null) {
            mOuterSurfaceTextureListener.onSurfaceTextureUpdated(surface);
        }
    }

    public void destroy() {
        if (mRenderThread != null) {
            mRenderThread.destroy();
            mRenderThread = null;
        }
    }

    private class RenderRunnable implements Runnable {

        private Runnable mRealRunnable;

        private RenderRunnable(Runnable realRunnable) {
            mRealRunnable = realRunnable;
        }

        @Override
        public void run() {
            if (mRenderThread == null
                    || mRenderThread.mHandler == null) {
                return;
            }

            if (!mRenderThread.mIsInit) {
                mRenderThread.mHandler.post(this);
            } else if (mRealRunnable != null) {
                mRealRunnable.run();
            }
        }
    }
}
