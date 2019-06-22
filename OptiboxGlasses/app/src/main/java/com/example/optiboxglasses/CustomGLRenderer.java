package com.example.optiboxglasses;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


class CustomGLRenderer implements GLSurfaceView.Renderer {

    //Projections and Camera Views Transformations
    // mvpMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mvpMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private float[] rotationMatrix = new float[16];

    public volatile float mAngle;
    private List<Line> mLines = new ArrayList<>();


    static final float volexSize = 0.1f;
    static final int camionLx = 23;
    static final int camionLy = 23;
    static final int camionLz = 60;
    static final float[] camionDiagonalAG = {0.0f,0.0f,0f,camionLx*volexSize,camionLy*volexSize,-camionLz*volexSize};

    static final float[] cameraEye = /*{1f,2f,4f};*/{4.5f*camionDiagonalAG[3] - camionDiagonalAG[0],
                                2.5f*camionDiagonalAG[4] - camionDiagonalAG[1],
                                10f};

    static final float[] cameraViewCenter = { 0.5f*(camionDiagonalAG[0]+camionDiagonalAG[3]),
                                        0.5f*(camionDiagonalAG[1]+camionDiagonalAG[4]),
                                        0.5f*(camionDiagonalAG[2]+camionDiagonalAG[5])};
    static final float[] cameraUp = {0.0f,1.0f,0.0f};

    static final float blue[] = {0.0f,0.0f,1.0f,1f};
    static final float green[] = {0.0f,1.0f,0.0f,1.0f};
    static final float grey[] = {0.4f,0.4f,0.4f,1.0f};

    private List<FullBlock> cardboards = new ArrayList<>();
    private List<FullBlock> fullCardboardsList  = new ArrayList<>();
    private int currentIndex=0;

    public CustomGLRenderer(String cardboardJSON) {

        try {
            JSONArray jsonArray = new JSONArray(cardboardJSON);
            for(int i=0;i<jsonArray.length();i++){
                JSONArray coords = jsonArray.getJSONArray(i);
                FullBlock tempFB = new FullBlock();
                tempFB.setColor(green);
                tempFB.setDiagonalAG(convertDataToCoords(
                        coords.getInt(0),coords.getInt(1),coords.getInt(2),
                        coords.getInt(3),coords.getInt(4),coords.getInt(5)));
                fullCardboardsList.add(tempFB);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        currentIndex=0;

    }


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        cardboards.add(fullCardboardsList.get(currentIndex));
    }

    public void onDrawFrame(GL10 unused) {
        //for rotation
        float[] scratch = new float[16];

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glLineWidth(5f);


        //
        //Matrix.translateM();

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix,0,
                cameraEye[0], cameraEye[1], cameraEye[2],
                cameraViewCenter[0], cameraViewCenter[1], cameraViewCenter[2],
                cameraUp[0], cameraUp[1], cameraUp[2]);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        float translateToCenter[] = new float[16];
        Matrix.translateM(translateToCenter,0,camionDiagonalAG[0]-camionDiagonalAG[3],
                                              camionDiagonalAG[1]-camionDiagonalAG[4],
                                                0);

        // Create a rotation transformation for the triangle
        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0, -1.0f, 0.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.

        //float translateNRotate[] = new float[16];
        //Matrix.multiplyMM(translateNRotate, 0, rotationMatrix, 0, translateToCenter, 0);

        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, rotationMatrix, 0);

        float blue[] = {0.0f,0.0f,1.0f,1.0f};
        LineBlock camion = new LineBlock();
        camion.setDiagonalAG(camionDiagonalAG);
        camion.setColor(blue);
        camion.draw(scratch);

        for(FullBlock cardboard: cardboards){
            cardboard.draw(scratch);
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 50);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
    private float[] convertDataToCoords(int xA,int yA, int zA, int xG, int yG, int zG){
        return new float[]{xA*volexSize, yA*volexSize, -zA*volexSize, xG*volexSize, yG*volexSize, -zG*volexSize};
    }

    public void updateCardboardsList(){
        cardboards.get(cardboards.size() -1).setColor(grey);
        currentIndex++;
        if(currentIndex<fullCardboardsList.size())
            cardboards.add(fullCardboardsList.get(currentIndex));
    }
}
