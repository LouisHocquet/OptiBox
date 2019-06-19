package com.example.optiboxglasses;

public class LineBlock {

    private float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };
    private static float diagonalAG[] = {
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f
    };

    Line edges[];
    public LineBlock(){
        edges = new Line[12];
        for(int i=0;i<12;i++)
            edges[i] = new Line();

    }

    private void updateLinesCoords(float xA, float yA, float zA, float xG, float yG, float zG){
        //        H.------.G
        //        /|     / |    // Ox axis is AB
        //      E.------.F |    // Oy axis is AE
        //      | D.----|-.C    // Oz axis is AD
        //      | /     | /     // diagonal  is AG
        //      A.------.B
        //
        //Face ABCD
        edges[0].setLineCoords(xA,yA,zA, xG,yA,zA); //AB
        edges[1].setLineCoords(xG,yA,zA, xG,yA,zG); //BC
        edges[2].setLineCoords(xG,yA,zG, xA,yA,zG); //CD
        edges[3].setLineCoords(xA,yA,zA, xA,yA,zG); //AD
        //Vertical edges
        edges[4].setLineCoords(xA,yA,zA, xA,yG,zA); //AE
        edges[5].setLineCoords(xG,yA,zA, xG,yG,zA); //BF
        edges[6].setLineCoords(xG,yA,zG, xG,yG,zG); //CG
        edges[7].setLineCoords(xA,yA,zG, xA,yG,zG); //DH
        //Face EFGH
        edges[8].setLineCoords(xA,yG,zA, xG,yG,zA); //EF
        edges[9].setLineCoords(xG,yG,zA, xG,yG,zG); //FG
        edges[10].setLineCoords(xG,yG,zG, xA,yG,zG); //GH
        edges[11].setLineCoords(xA,yG,zG, xA,yG,zA); //EH
    }
    private void updateLinesColor(){
        for (Line line: edges)
            line.setColor(color);
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
        for(Line line: edges)
            line.draw(mvpMatrix);
    }


}
