package com.example.optiboxglasses;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    private FloatBuffer mVertexBuffer;

    // number of coordinates per vertex
    public static final int COORDS_PER_VERTEX = 3;

    private int mPositionHandle;
    private int mColorHandle;

    private final int mVertexCount = 3; //3 points in a triangle
    private final int mVertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per float

    // in counterclockwise order:
    private float mPointA[];
    private float mPointB[];
    private float mPointC[];

    //openGL parameters
    private final String mVertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String mFragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final int mProgram;


    // Set color with red, green, blue and alpha (opacity) values
    private float mColor[];

    public Triangle(){
        this(   new float[]{0.0f, 0.0f, 0.0f},
                new float[]{0.5f, 0.0f, 0.0f},
                new float[]{0.0f, 0.5f, 0.0f},
                new float[]{1.0f, 0.0f, 0.0f, 1.0f}
            ); //RED by default
    }

    public Triangle(float color[]){
        this();
        mColor = color; //RED by default
    }

    public Triangle(float pA[], float pB[], float pC[]){
        this(pA,pB,pC, new float[]{0.1f, 0.0f, 0.0f, 1.0f}); //RED by default
    }

    public Triangle(float pA[], float pB[], float pC[], float col[] ) throws IllegalArgumentException{

        if(pA.length != COORDS_PER_VERTEX || pB.length != COORDS_PER_VERTEX || pC.length != COORDS_PER_VERTEX)
            throw new IllegalArgumentException("Each point of a Triangle must have "+COORDS_PER_VERTEX+" float coordinates");
        if(col.length != 4)
            throw new IllegalArgumentException("A color is a 4 float vertex of RGBA components");
        else if( col[0] < 0.0f || col[0] > 1.0f ||
                col[1] < 0.0f || col[1] > 1.0f ||
                col[2] < 0.0f || col[2] > 1.0f ||
                col[3] < 0.0f || col[3] > 1.0f)
            throw new IllegalArgumentException("Each color component must belong to [0.0f ; 1.0f]");

        mPointA = pA;
        mPointB = pB;
        mPointC = pC;
        mColor = col;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                COORDS_PER_VERTEX*3* 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        mVertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        mVertexBuffer.put(mPointA);
        mVertexBuffer.put(mPointB);
        mVertexBuffer.put(mPointC);
        // set the buffer to read the first coordinate
        mVertexBuffer.position(0);


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

    public String getVertexShaderCode() {
        return mVertexShaderCode;
    }

    public String getFragmentShaderCode() {
        return mFragmentShaderCode;
    }

    public float[] getColor() {
        return mColor;
    }

    public void setColor(float[] mColor) {
        this.mColor = mColor;
    }

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public float[] getPointA() {
        return mPointA;
    }

    public float[] getPointB() {
        return mPointB;
    }

    public float[] getPointC() {
        return mPointC;
    }

    public void draw() {
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

        //TEST
        GLES20.glUniform4fv(mColorHandle, 1, new float[]{0.0f,1.0f,0.0f,1.0f}, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, mColor, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 1, mVertexCount);




        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}

