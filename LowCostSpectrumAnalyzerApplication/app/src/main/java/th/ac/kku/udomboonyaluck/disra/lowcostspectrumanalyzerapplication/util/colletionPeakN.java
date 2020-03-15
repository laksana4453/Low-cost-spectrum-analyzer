package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util;


public class colletionPeakN {
    private boolean checkActivity_Main = false;
    private int peak = 0;
    private int n = 0;
    private  static  final  colletionPeakN instance = new colletionPeakN();
    private int x1 = 0,y1 = 0,x2 = 0,y2 = 0;
    private boolean stateAddData = false;



    public colletionPeakN(){

    }
    public  static  colletionPeakN getInstance(){
        return instance;

    }
//    public Bitmap getBitmap(){
//        return bitmap;
//    }
//    public void setBitmap(Bitmap bitmap){
//        this.bitmap = bitmap;
//    }
    public void collectX1(int x1){
        this.x1 = x1;
    }
    public void collectY1(int y1){
        this.y1 = y1;
    }
    public void collectX2(int x2){
        this.x2 = x2;
    }
    public void collectY2(int y2){
        this.y2 = y2;
    }

    public int getX1(){
        return x1;
    }
    public int getY1(){
        return y1;
    }
    public int getX2(){
        return x2;
    }
    public int getY2(){
        return y2;
    }

    public void setStateAddData(boolean stateAddData){
        this.stateAddData = stateAddData;
    }
    public boolean getStateAddData(){
        return stateAddData;
    }


    public void setPeak(int peak){
        this.peak = peak;
    }
    public void setN(int n){
        this.n = n;
    }
    public int getPeak(){return peak;}
    public  int getN(){return n;}
    public void setCheckActivity_Main(boolean fromMain){
        this.checkActivity_Main = fromMain;
    }
    public boolean getCheckActivity_Main(){
        return checkActivity_Main;
    }

}
