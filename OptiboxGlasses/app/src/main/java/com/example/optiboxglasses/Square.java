package com.example.optiboxglasses;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Square {

    private FloatBuffer mVertexBuffer;

    // number of coordinates per vertex
    public static final int COORDS_PER_VERTEX = 3;

    private int mPositionHandle;
    private int mColorHandle;

    private final int mVertexCount = 6; //3 points in a triangle, 2 triangles
    private final int mVertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per float

    // in counterclockwise order:
    private float mPointBL[];
    private float mPointBR[];
    private float mPointTR[];
    private float mPointTL[];

    //openGL parameters
    private final String mVertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String mFragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final int mProgram;

    //Matrix projections
    // Use to access and set the view transformation
    private int mvpMatrixHandle;


    // Set color with red, green, blue and alpha (opacity) values
    private float mFillColor[];

    public Square(){
        this(   new float[]{0.0f, 0.0f, 0.0f},
                new float[]{0.5f, 0.0f, 0.0f},
                new float[]{0.5f, 0.5f, 0.0f},
                new float[]{0.0f, 0.5f, 0.0f},
                new float[]{0.1f, 0.0f, 0.0f, 1.0f}
        ); //RED by default
    }

    public Square(float color[]){
        this();
        mFillColor = color; //RED by default
    }

    public Square(float pA[], float pB[], float pC[], float pD[]){
        this(pA,pB,pC,pD, new float[]{0.1f, 0.0f, 0.0f, 1.0f}); //RED by default
    }

    public Square(float pA[], float pB[], float pC[], float[] pD, float col[] ) throws IllegalArgumentException{

        mPointBL = pA;
        mPointBR = pB;
        mPointTR = pC;
        mPointTL = pD;
        mFillColor = col;

        //RENDER IN OPENGL
        int vertexShader = CustomGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                mVertexShaderCode);
        int fragmentShader = CustomGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                mFragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    public float[] getPointBL() {
        return mPointBL;
    }

    public void setPointBL(float[] p) {
        this.mPointBL = p;
    }

    public float[] getPointBR() {
        return mPointBR;
    }

    public void setPointBR(float[] p) {
        this.mPointBR = p;
    }

    public float[] getPointTR() {
        return mPointTR;
    }

    public void setPointTR(float[] p) {
        this.mPointTR = p;
    }

    public float[] getPointTL() {
        return mPointTL;
    }

    public void setPointTL(float[] p) {
        this.mPointTL = p;
    }

    public void setColor(float[] mColor) {
        this.mFillColor = mColor;
    }


    public void draw(float[] mvpMatrix) {

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                COORDS_PER_VERTEX*6* 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        mVertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        mVertexBuffer.put(mPointBL);
        mVertexBuffer.put(mPointBR);
        mVertexBuffer.put(mPointTL);
        mVertexBuffer.put(mPointTL);
        mVertexBuffer.put(mPointBR);
        mVertexBuffer.put(mPointTR);
        // set the buffer to read the first coordinate
        mVertexBuffer.position(0);

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                mVertexStride, mVertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangles
        GLES20.glUniform4fv(mColorHandle, 1, mFillColor, 0);

        // get handle to shape's transformation matrix
        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangles
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexCount);

        //Lines
        GLES20.glUniform4fv(mColorHandle, 1, new float[]{0f,0f,0f,1f}, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, mVertexCount);



        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


}