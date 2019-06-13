package com.example.optiboxglasses;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Square {

    private FloatBuffer mVertexBuffer;

    // number of coordinates per vertex in this array
    public static final int COORDS_PER_VERTEX = 3;
    // in counterclockwise order:
    private float mPointA[];
    private float mPointB[];
    private float mPointC[];
    private float mPointD[];

    // Set color with red, green, blue and alpha (opacity) values
    private float mColor[];

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
        mColor = color; //RED by default
    }

    public Square(float pA[], float pB[], float pC[], float pD[]){
        this(pA,pB,pC,pD, new float[]{0.1f, 0.0f, 0.0f, 1.0f}); //RED by default
    }

    public Square(float pA[], float pB[], float pC[], float[] pD, float col[] ) throws IllegalArgumentException{

        if(pA.length != COORDS_PER_VERTEX || pB.length != COORDS_PER_VERTEX || pC.length != COORDS_PER_VERTEX || pD.length != COORDS_PER_VERTEX)
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
        mPointD = pD;
        mColor = col;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                COORDS_PER_VERTEX*4* 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        mVertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        mVertexBuffer.put(mPointA);
        mVertexBuffer.put(mPointB);
        mVertexBuffer.put(mPointC);
        mVertexBuffer.put(mPointD);
        // set the buffer to read the first coordinate
        mVertexBuffer.position(0);
    }
}