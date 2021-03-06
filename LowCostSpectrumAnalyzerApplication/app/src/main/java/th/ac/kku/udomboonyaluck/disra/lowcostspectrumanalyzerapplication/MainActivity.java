package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
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

import static th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.Configuration.needConfig;

public class MainActivity extends AppCompatActivity {
    private final int PICK_IMAGE = 1;
    private Uri imageUri;
    private ImageView mImageView;
    private LineChart chart;
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    private TextView peak_txt,n_txt,peak_txt2,descriptionGraph;
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private Button showSpec,showAbsorb;
    final Context context = this;
    public SQLiteDatabaseHandler db;
    Boolean hasData = Boolean.FALSE;
    private int width,height;
    private List<Data> datas;
    private int position,peak,n,x;
    double lambda,d,f;
    List<Double> lambdaRange;
    List<Integer> new_x,new_lambda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMultiplePermissions();
        db = new SQLiteDatabaseHandler(this);

        chart = (LineChart) findViewById(R.id.chart1);
        descriptionGraph = (TextView) findViewById(R.id.scriptGraph);
        showSpec = findViewById(R.id.showSpec);
        showAbsorb = findViewById(R.id.showAbsorb);
        mImageView = findViewById(R.id.spectrum);
        if(imageForAnalyze.getInstance().getBitmap()!= null){
            mImageView.setImageBitmap(imageForAnalyze.getInstance().getBitmap());
            mImageView.setTag("haveImage");

            width = imageForAnalyze.getInstance().getBitmap().getWidth();
            height = imageForAnalyze.getInstance().getBitmap().getHeight();
        }

        datas = db.allDatas();
        peak = datas.get(0).getHeight();
        n = datas.get(0).getN();

        lambda = 460/Math.pow(10,9);
        d = 1.0/(600*Math.pow(10,3));

        lambdaRange = new ArrayList<>();
        new_x = new ArrayList<>();
        new_lambda = new ArrayList<>();

        for(int i = 380; i <= 740; i++){
            lambdaRange.add(i/Math.pow(10,9));
        }

        showSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageForAnalyze.getInstance().getBitmap()!= null) {
                    showGraph(imageForAnalyze.getInstance().getBitmap());
                } else {
                    showDialogImageNull();
                }
            }
        });

        showAbsorb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageForAnalyze.getInstance().getBitmap()!= null) {
                    Bitmap defaultImage = decodeImage(datas.get(0).getPosition());
                    showAbsorbGraph(defaultImage,imageForAnalyze.getInstance().getBitmap());
                } else {
                    showDialogImageNull();
                }
            }
        });

    }
    public String encodeImage(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final boolean compress = image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }
    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
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
        Intent intent = new Intent(this,CameraActivity.class);
        colletionPeakN.getInstance().setCheckActivity_Main(true);
        startActivity(intent);
        finish();

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
                    Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    sendImage.getInstance().setBitmap(rotated(bitmap));
                    openCrop();
//                    img.setImageBitmap(rotated(bitmap));
//                    BitmapHelper.getInstance().setBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    private void openCrop() {
        Intent intent = new Intent(this, CroppingAutoActivity.class);
        startActivity(intent);
        finish();
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

    public void setting_mode(View view){
        Intent intent = new Intent(getApplicationContext(),Configuration.class);
        startActivity(intent);
        finish();
    }
    public int countDB(){
        int id;
        List<Data> datas = db.allDatas();
        id = datas.size() + 1;
        return id;
    }
    public void saveData(View view){
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_save_data);
        final EditText editText = (EditText) dialog.findViewById(R.id.recordName);
        dialog.setTitle("Enter name of data");

        // set the custom dialog components - text, image and button

        Button BtCancel = (Button) dialog.findViewById(R.id.cancelBt);
        Button BtSave = (Button) dialog.findViewById(R.id.saveBt);
        // if button is clicked, close the custom dialog
        BtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        BtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasData = Boolean.FALSE;
                List<Data> datas = db.allDatas();
                String name = editText.getText().toString();

                if(imageForAnalyze.getInstance().getImageStringAnalyze() != null && colletionPeakN.getInstance().getPeak() != 0 && colletionPeakN.getInstance().getN() != 0 && !name.equals("")){
                    for (int i = 0; i < datas.size(); i++){
                        if(datas.get(i).getName().equals(name))
                            hasData = Boolean.TRUE;
                    }

                    if(hasData){
                        Toast.makeText(MainActivity.this,"Please enter another name.",Toast.LENGTH_LONG).show();
                    } else {
                        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
                        Bitmap imageBitmap = drawable.getBitmap();
                        String encodeImage = encodeImage(imageBitmap);
                        Data Datadefault = find_Position_of_Crop();
                        Data data = new Data(countDB(), name, encodeImage, colletionPeakN.getInstance().getPeak(), colletionPeakN.getInstance().getN()
                        ,Datadefault.getX1(),Datadefault.getY1(),Datadefault.getX2(),Datadefault.getY2());
                        db.addData(data);
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(MainActivity.this,"Please choose or take a picture before analyze",Toast.LENGTH_LONG).show();
                }

            }
        });

        dialog.show();
    }
    // find data object of default
    public Data find_Position_of_Crop(){
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
        return dataDefault;

    }
    public void loadData(View view){
        Intent intent =  new Intent(context, ShowDataActivity.class);
        startActivity(intent);
    }

    public void showGraph(Bitmap image) {
        List<Double> intensity,spec380to740;
        List<Integer> intervalIntensity;
        intensity = findXarr(image);
        intervalIntensity = findNewX();

        spec380to740 = new ArrayList<>();

        for(int i = 0;i < intervalIntensity.size();i++){
            spec380to740.add(intensity.get(intervalIntensity.get(i)-x+peak));
        }

        createGraph(spec380to740);
    }

    private void showAbsorbGraph(Bitmap defaultImage, Bitmap image) {
        List<Double> intensityDef,intensityImg,intensityAb,spec380to740Ab;
        List<Integer> intervalIntensity;
        intensityDef = findXarr(defaultImage);
        intensityImg = findXarr(image);
        intervalIntensity = findNewX();

        intensityAb = new ArrayList<>();
        spec380to740Ab = new ArrayList<>();

        for(int i = 0; i < intensityImg.size(); i++) {
            if(intensityImg.get(i) != (double) 0){
                intensityAb.add(intensityDef.get(i)/intensityImg.get(i));
            } else {
                intensityAb.add((double) 0);
            }
        }

        for(int i = 0;i < intervalIntensity.size();i++){
            spec380to740Ab.add(intensityAb.get(intervalIntensity.get(i)-x+peak));
        }

        createGraph(spec380to740Ab);
    }

    private void createGraph(List<Double> Yaxis) {
        chart.setVisibility(View.VISIBLE);
        chart.getXAxis().setLabelCount(9,true);
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners

        chart.setDrawGridBackground(false);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // Set the marker to the chart
        mv.setChartView(chart);
        chart.setMarker(mv);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);
        // draw points over time
        chart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
        ArrayList<Entry> values = new ArrayList<>();
        // add point in graph
        Log.d("1234", "maxValue" + Collections.max(Yaxis));

        for(int i = 380 ; i <= 740; i++){
            new_lambda.add(i);
        }

        for (int i = 0; i < Yaxis.size(); i++) {

            float y = Float.parseFloat("" + Yaxis.get(i));
//            Log.d("123","peak-x : "+peak[0]);

            values.add(new Entry(new_lambda.get(i), y));

        }

//        values.add(new Entry(0, 4));

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                //Not set the graph color
                set1.setFillColor(Color.WHITE);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);


        }
    }

    public List<Double> findXarr(Bitmap image){
        double  R ,G ,B;
        int colorPixel;
        double[][] imageArray= new double[width][3];

        for(int x = 0; x < width; x++ ){
            for(int y = 0; y < height;y++){
                colorPixel = image.getPixel(x,y);
                R = Color.red(colorPixel);
                G = Color.green(colorPixel);
                B = Color.blue(colorPixel);

                R =  ((0.07*R)+(0.52*G)+(0.23*B))/255;
                imageArray[x][0] = R/255;
                imageArray[x][1] = R/255;
                imageArray[x][2] = R/255;
            }
        }

        double[][] M = new double[][] { { 0.49, 0.31, 0.20 }, { 0.17697, 0.81240, 0.01063 }, { 0.00, 0.01, 0.99 } };
        double X;
        List<Double> Xarr= new ArrayList<Double>();

        for(int i = 0;i< width;i++ ){
            X = M[0][0]*imageArray[i][2] + M[0][1]*imageArray[i][1] + M[0][2]*imageArray[i][0];
            Xarr.add(X*1000);
        }

        return Xarr;
    }

    public List<Integer> findNewX(){
        new_x = new ArrayList<>();
        x = width - peak;
        f = Math.sqrt(Math.pow(x*d/lambda,2)-Math.pow(x,2));
        for(int i = 0; i < lambdaRange.size();i++){
            new_x.add((int) Math.round((lambdaRange.get(i) /d)*Math.sqrt(Math.pow(f,2) + Math.pow(x,2))));
        }
        return new_x;
    }

}
