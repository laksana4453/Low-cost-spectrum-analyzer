package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.colletionArraylist;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.colletionPeakN;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.imageForAnalyze;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.sendImage;

public class Configuration extends AppCompatActivity {
    final Context context = this;
    TextView number_n;
    ImageView imageView;
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    public SQLiteDatabaseHandler db;
    public static Boolean needConfig;
    Boolean hasDefault = Boolean.FALSE;
    private Bitmap image = null;
    private Integer n = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        number_n = (TextView) findViewById(R.id.order);
        imageView = (ImageView) findViewById(R.id.spectrum);
        requestMultiplePermissions();
        db = new SQLiteDatabaseHandler(this);


        if(checkDefault()){
            showDialogAsk();
        }


        if(sendImage.getInstance().getBitmap()!= null){
            imageView.setImageBitmap(sendImage.getInstance().getBitmap());
            imageView.setTag("haveImage");
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.show_image_setting);
            sendImage.getInstance().setBitmap(icon);
        }
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public void add_n (View view){
        int n = Integer.parseInt(number_n.getText().toString());
        n++;
        number_n.setText(String.valueOf(n));

    }
    public void minus_n (View view){
        int n = Integer.parseInt(number_n.getText().toString());
        n--;
        number_n.setText(String.valueOf(n));
    }
    public  void PickImage(View view){

        showPictureDialog();

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }
    private void takePhotoFromCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        colletionPeakN.getInstance().setCheckActivity_Main(false);
        startActivity(intent);
        finish();
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(Configuration.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    sendImage.getInstance().setBitmap(rotated(bitmap));
                    openCrop();
//                    img.setImageBitmap(rotated(bitmap));
//                    BitmapHelper.getInstance().setBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Configuration.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
//        else if (requestCode == CAMERA) {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
////            img.setImageBitmap(thumbnail);
//            sendImage.getInstance().setBitmap(thumbnail);
//            Log.d("123",saveImage(thumbnail));
//            openCrop();
//            Toast.makeText(Configuration.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//        }
    }
    private void openCrop() {
        Intent intent = new Intent(this, CroppingActivity.class);
        startActivity(intent);
        finish();
    }
    public int analyzeImage() {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap imageBitmap = drawable.getBitmap();
        imageForAnalyze.getInstance().setImageStringAnalyze(encodeImage(imageBitmap));

        double R, G, B;
        int colorPixel;
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        double[][] imageArray = new double[width][3];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                colorPixel = imageBitmap.getPixel(x, y);
                R = Color.red(colorPixel);
                G = Color.green(colorPixel);
                B = Color.blue(colorPixel);

                R = ((0.07 * R) + (0.52 * G) + (0.23 * B)) / 255;
                imageArray[x][0] = R / 255;
                imageArray[x][1] = R / 255;
                imageArray[x][2] = R / 255;


            }
        }

        double[][] M = new double[][]{{0.49, 0.31, 0.20}, {0.17697, 0.81240, 0.01063}, {0.00, 0.01, 0.99}};
        double X, Y, Z;
        List<Double> Xarr = new ArrayList<Double>();
        List<Double> Yarr = new ArrayList<Double>();
        List<Double> Zarr = new ArrayList<Double>();

        for (int i = 0; i < width; i++) {
            X = M[0][0] * imageArray[i][2] + M[0][1] * imageArray[i][1] + M[0][2] * imageArray[i][0];
            Y = M[1][0] * imageArray[i][2] + M[1][1] * imageArray[i][1] + M[1][2] * imageArray[i][0];
            Z = M[2][0] * imageArray[i][2] + M[2][1] * imageArray[i][1] + M[2][2] * imageArray[i][0];
            Xarr.add(X * 1000);
            Yarr.add(Y * 10);
            Zarr.add(Z * 10);


        }

        colletionArraylist.getInstance().saveArraylist(Xarr);

        int peak = 0;
        for (int i = 0; i < Xarr.size(); i++) {
            if (Xarr.get(i) == Collections.max(Xarr)) {
                peak = i;
            }
        }


        return peak;
    }
    public void send_parameter(View view){
        Log.d("needConfigConfig",needConfig + " " + hasDefault);
        List<Data> datas = db.allDatas();
        hasDefault = Boolean.FALSE;
        for (int i = 0; i < datas.size(); i++){
            if(datas.get(i).getName().equals("Default"))
                hasDefault = Boolean.TRUE;
        }


        String tagImage = String.valueOf(imageView.getTag());
        if(tagImage.equals("noImage")) {
//           Toast.makeText(this, "Please choose your spectrum line.", Toast.LENGTH_LONG).show();
             showDialogImageNull();
        } else{
            Log.d("needConfigComeIn","Come in!!!");
            int peak = analyzeImage();
            colletionPeakN.getInstance().setN(Integer.parseInt(number_n.getText().toString()) );
            colletionPeakN.getInstance().setPeak(peak);
            for (int i = 0; i < datas.size(); i++){
                if(datas.get(i).getName().equals("Default"))
                    db.deleteOne(datas.get(i));
            }

            Data data = new Data(1, "Default", imageForAnalyze.getInstance().getImageStringAnalyze(), colletionPeakN.getInstance().getPeak(), colletionPeakN.getInstance().getN() ,
                    colletionPeakN.getInstance().getX1(),colletionPeakN.getInstance().getY1(),colletionPeakN.getInstance().getX2(),colletionPeakN.getInstance().getY2());
            db.addData(data);

            Toast.makeText(this, "Peak setting successful!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    public Bitmap rotated(Bitmap bitmapOrg){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        int height = bitmapOrg.getHeight();
        int width = bitmapOrg.getWidth();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, width, height, true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        return rotatedBitmap;

    }

    public String encodeImage(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final boolean compress = image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }
    // when user come to app show dialog option set new default or not;
    public boolean checkDefault(){
        boolean hasDefault = false;
        String name  = "Default";
        List<Data> datas = db.allDatas();
        Data dataDefault = null;
        if (datas != null) {

            for (int i = 0; i < datas.size(); i++) {
                if(datas.get(i).getName().equals(name)){
                    dataDefault = datas.get(i);
                }
            }
//                itemsNames[i] = players.get(i).getName();
        }
        if(dataDefault!= null){
            // Ask 'Do you want to set new default?'
            hasDefault = true;
        }else {
            hasDefault = false;
        }
        return hasDefault;
    }

    private void showDialogImageNull(){
        final Dialog di_warning = new Dialog(context);
        di_warning.setContentView(R.layout.warnning_dialog);
        TextView textView = (TextView) di_warning.findViewById(R.id.warning_description);
        Button BtOk = (Button) di_warning.findViewById(R.id.btOk);
        textView.setText("Please add image by click 'Pick image' button ");
        BtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                di_warning.dismiss();
            }
        });
        di_warning.show();
    }
    private void showDialogAsk() {
        final Dialog dialog = new Dialog(context);
        final Dialog dialog_warning = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete_data);
        Button BtYes = (Button) dialog.findViewById(R.id.btYes);
        Button BtNo = (Button) dialog.findViewById(R.id.btNo);
        final TextView textView = (TextView) dialog.findViewById(R.id.scriptionDialog);
        textView.setText("You have default data."+"Do you want to set new default?");
        BtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        BtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog_warning.setContentView(R.layout.dialog_delete_data);
                TextView textView1 = (TextView) dialog_warning.findViewById(R.id.scriptionDialog);
                textView1.setText("We will delete all saved data." + "Are you sure you want to proceed?");
                Button BtYes = (Button) dialog_warning.findViewById(R.id.btYes);
                Button BtNo = (Button) dialog_warning.findViewById(R.id.btNo);
                BtNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_warning.dismiss();
                        Intent intent = new Intent(context,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                BtYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.setTag("noImage");
                        db.removeAllDatas();
                        dialog_warning.dismiss();
                    }
                });
                dialog_warning.show();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
          super.onBackPressed();
//          isDestroyed();
//        if(!checkDefault()){
//            showDialogDefaultNull();
//        }else {
//            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }
}
