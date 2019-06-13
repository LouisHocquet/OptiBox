package com.example.optiboxglasses;

import android.content.Context;
import android.opengl.GLSurfaceView;

class CustomGLSurfaceView extends GLSurfaceView {

    private final CustomGLRenderer renderer;

    public CustomGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new CustomGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }
}
