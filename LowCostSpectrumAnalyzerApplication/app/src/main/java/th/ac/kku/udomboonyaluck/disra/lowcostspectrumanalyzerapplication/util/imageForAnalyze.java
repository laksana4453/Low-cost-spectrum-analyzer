package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util;

import android.graphics.Bitmap;

public class imageForAnalyze {
    private Bitmap bitmap = null;
    private  static  final  imageForAnalyze instance = new imageForAnalyze();
    private  String imageStringAnalyze = null;

    public imageForAnalyze(){

    }
    public  static  imageForAnalyze getInstance(){
        return instance;

    }
    public  Bitmap getBitmap(){
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
    public  void setImageStringAnalyze(String imageStringAnalyze){
        this.imageStringAnalyze = imageStringAnalyze;
    }
    public String getImageStringAnalyze(){
        return imageStringAnalyze;
    }
//    public void encodeImage(Bitmap image){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        final boolean compress = image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//        this.imageStringAnalyze = imageString;
//    }
}
