package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util;

import android.graphics.Bitmap;

public class sendImage {


        private Bitmap bitmap = null;
        private  static  final  sendImage instance = new sendImage();

        public sendImage(){

        }
        public  static  sendImage getInstance(){
            return instance;

        }
        public  Bitmap getBitmap(){
            return bitmap;
        }
        public void setBitmap(Bitmap bitmap){
            this.bitmap = bitmap;
        }


}
