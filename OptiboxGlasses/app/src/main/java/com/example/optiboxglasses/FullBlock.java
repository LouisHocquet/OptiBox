package com.example.optiboxglasses;

public class FullBlock {

    private float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };
    private static float diagonalAG[] = {
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f
    };

    private Square faces[];
    public FullBlock(){
        faces = new Square[6];
        for(int i=0;i<6;i++)
            faces[i] = new Square();

    }

    private void updateLinesCoords(float xA, float yA, float zA, float xG, float yG, float zG){
        //        H.------.G
        //        /|     / |    // Ox axis is AB
        //      E.------.F |    // Oy axis is AE
        //      | D.----|-.C    // Oz axis is AD
        //      | /     | /     // diagonal  is AG
        //      A.------.B
        //

        float pA[] = {xA,yA,zA};
        float pB[] = {xG,yA,zA};
        float pC[] = {xG,yA,zG};
        float pD[] = {xA,yA,zG};
        float pE[] = {xA,yG,zA};
        float pF[] = {xG,yG,zA};
        float pG[] = {xG,yG,zG};
        float pH[] = {xA,yG,zG};

        //Face ABFE
        faces[0].setPointBL(pA);
        faces[0].setPointBR(pB);
        faces[0].setPointTR(pF);
        faces[0].setPointTL(pE);
        //Face EFGH
        faces[1].setPointBL(pE);
        faces[1].setPointBR(pF);
        faces[1].setPointTR(pG);
        faces[1].setPointTL(pH);
        //Face BCGF
        faces[2].setPointBL(pB);
        faces[2].setPointBR(pC);
        faces[2].setPointTR(pG);
        faces[2].setPointTL(pF);
        //Face CDHG
        faces[3].setPointBL(pC);
        faces[3].setPointBR(pD);
        faces[3].setPointTR(pH);
        faces[3].setPointTL(pG);
        //Face DAEH
        faces[4].setPointBL(pD);
        faces[4].setPointBR(pA);
        faces[4].setPointTR(pE);
        faces[4].setPointTL(pH);
        //Face ABCD
        faces[5].setPointBL(pA);
        faces[5].setPointBR(pB);
        faces[5].setPointTR(pC);
        faces[5].setPointTL(pD);
    }
    private void updateLinesColor(){
        for (Square square:faces )
            square.setColor(color);
    }
    public void setDiagonalAG(float xA, float yA, float zA, float xG, float yG, float zG) {
        diagonalAG[0] = xA;
        diagonalAG[1] = yA;
        diagonalAG[2] = zA;
        diagonalAG[3] = xG;
        diagonalAG[4] = yG;
        diagonalAG[5] = zG;
        updateLinesCoords(xA,yA,zA, xG,yG,zG);
    }
    public void setDiagonalAG(float[] pDiagonalAG) {
        if(pDiagonalAG.length != 6)
            throw new IllegalArgumentException("pDiagonalAG must be a 6 float length array");
        diagonalAG = pDiagonalAG.clone();
        updateLinesCoords(diagonalAG[0],diagonalAG[1],diagonalAG[2],
                diagonalAG[3],diagonalAG[4],diagonalAG[5] );
    }
    public void setColor(float red, float green, float blue, float alpha) {
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        color[3] = alpha;
        updateLinesColor();
    }
    public void setColor(float[] col) {
        if( col.length != 4 ||
                col[0] < 0.0f || col[0] > 1.0f ||
                col[1] < 0.0f || col[1] > 1.0f ||
                col[2] < 0.0f || col[2] > 1.0f ||
                col[3] < 0.0f || col[3] > 1.0f)
            this.setColor(1.0f,1.0f,1.0f,1.0f); //WHITE
        else
            this.setColor(col[0],col[1],col[2],col[3]);
    }

    public void draw(float[] mvpMatrix){
        for(Square square: faces)
            square.draw(mvpMatrix);
    }


}
