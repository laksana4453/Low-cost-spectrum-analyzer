package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.colletionArraylist;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.colletionPeakN;

public class AbsorbActivity extends AppCompatActivity {

    private  SQLiteDatabaseHandler db;
    LineChart waveGraph, absorbGraph;
    ImageView spectrum;
    TextView imageName;
    private List<Data> datas;
    private  int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absorb);
        db = new SQLiteDatabaseHandler(this);
        position = getIntent().getIntExtra("position_recyclerView",0);
        waveGraph = findViewById(R.id.waveGraph);
        absorbGraph = findViewById(R.id.absorbGraph);
        spectrum = findViewById(R.id.spectrum);
        imageName = findViewById(R.id.imageName);
        datas = db.allDatas();
        imageName.setText(datas.get(position).getName());
        spectrum.setImageBitmap(decodeImage(datas.get(position).getPosition()));


    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public void showGraph(View view){
        waveGraph.setVisibility(View.VISIBLE);
        List<Double> spectrum = analyzeImageSpectrum();

        createGraphWave(spectrum);
    }

    public void showAbsorbGraph(View view){
        absorbGraph.setVisibility(View.VISIBLE);
        List<Double> spectrum = analyzeImageSpectrum();
        List<Double> absorb = analyzeImageAbsorb();
        List<Double> result = new ArrayList<Double>();
        for(int i = 0;i < spectrum.size();i++){
            result.add(absorb.get(i)/spectrum.get(i)) ;
       }
        Log.d("456" ,absorb.size()+"");
        Log.d("456" ,spectrum.size()+"");
        createGraphAbsorb(result);
    }

    public List<Double> analyzeImageSpectrum() {
        BitmapDrawable drawable = (BitmapDrawable) spectrum.getDrawable();
        Bitmap imageBitmap = drawable.getBitmap();

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
        return Xarr;
    }

    public List<Double> analyzeImageAbsorb() {
        Bitmap defaultImage = null;
        List<Data> datas = db.allDatas();

        for (int i = 0; i < datas.size(); i++){
            if(datas.get(i).getName().equals("Default"))
                defaultImage = decodeImage(datas.get(i).getPosition());
        }

        double R, G, B;
        int colorPixel;
        int width = defaultImage.getWidth();
        int height = defaultImage.getHeight();
        double[][] imageArray = new double[width][3];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                colorPixel = defaultImage.getPixel(x, y);
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
        return Xarr;
    }

    private void createGraphWave(List<Double> Yaxis) {

        waveGraph.setBackgroundColor(Color.WHITE);

        // disable description text
        waveGraph.getDescription().setEnabled(false);

        // enable touch gestures
        waveGraph.setTouchEnabled(true);

        // set listeners

        waveGraph.setDrawGridBackground(false);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // Set the marker to the chart
        mv.setChartView(waveGraph);
        waveGraph.setMarker(mv);

        // enable scaling and dragging
        waveGraph.setDragEnabled(true);
        waveGraph.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        waveGraph.setPinchZoom(true);
        // draw points over time
        waveGraph.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = waveGraph.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
        ArrayList<Entry> values = new ArrayList<>();
        // add point in graph
        Log.d("1234","maxValue"+ Collections.max(Yaxis));

//        List<Double> Xarr= new ArrayList<Double>();
//        double[] peak = new double[1];
//        for(int i = 0 ; i < Yaxis.size() ; i++) {
//            if (Yaxis.get(i) == Collections.max(Yaxis)) {
//                peak[0] = i;
//            }
//        }
//
//        int peak = colletionPeakN.getInstance().getPeak();
//        Log.d("789",peak[0]+"");
//        peak_txt2.setText(String.valueOf(peak[0]));

        for(int i = 0 ; i < Yaxis.size() ; i++){

            Float y = Float.valueOf("" + Yaxis.get(i));
            Double x = Double.valueOf(i);
//            Log.d("123","peak-x : "+peak[0]);

            values.add(new Entry( Float.valueOf((float) calculate(x)),y));

        }

//        values.add(new Entry(0, 4));

        LineDataSet set1;

        if (waveGraph.getData() != null &&
                waveGraph.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) waveGraph.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            waveGraph.getData().notifyDataChanged();
            waveGraph.notifyDataSetChanged();
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
                    return waveGraph.getAxisLeft().getAxisMinimum();
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
            waveGraph.setData(data);


        }
    }

    private void createGraphAbsorb(List<Double> Yaxis) {

        absorbGraph.setBackgroundColor(Color.WHITE);

        // disable description text
        absorbGraph.getDescription().setEnabled(false);

        // enable touch gestures
        absorbGraph.setTouchEnabled(true);

        // set listeners

        absorbGraph.setDrawGridBackground(false);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // Set the marker to the chart
        mv.setChartView(absorbGraph);
        absorbGraph.setMarker(mv);

        // enable scaling and dragging
        absorbGraph.setDragEnabled(true);
        absorbGraph.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        absorbGraph.setPinchZoom(true);
        // draw points over time
        absorbGraph.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = absorbGraph.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
        ArrayList<Entry> values = new ArrayList<>();
        // add point in graph
        Log.d("1234","maxValue"+ Collections.max(Yaxis));

//        List<Double> Xarr= new ArrayList<Double>();
//        double[] peak = new double[1];
//        for(int i = 0 ; i < Yaxis.size() ; i++) {
//            if (Yaxis.get(i) == Collections.max(Yaxis)) {
//                peak[0] = i;
//            }
//        }
//
//        int peak = colletionPeakN.getInstance().getPeak();
//        Log.d("789",peak[0]+"");
//        peak_txt2.setText(String.valueOf(peak[0]));

        for(int i = 0 ; i < Yaxis.size() ; i++){

            Float y = Float.valueOf("" + Yaxis.get(i));
            Double x = Double.valueOf(i);
//            Log.d("123","peak-x : "+peak[0]);

            values.add(new Entry( Float.valueOf((float) calculate(x)),y));

        }

//        values.add(new Entry(0, 4));

        LineDataSet set1;

        if (absorbGraph.getData() != null &&
                absorbGraph.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) absorbGraph.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            absorbGraph.getData().notifyDataChanged();
            absorbGraph.notifyDataSetChanged();
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
                    return absorbGraph.getAxisLeft().getAxisMinimum();
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
            absorbGraph.setData(data);


        }
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
    public double calculate(double x) {
        int peak = datas.get(position).getHeight();
        int n = datas.get(position).getN();
        double d = 1.0/(600*Math.pow(10,3));
        double lamda = 460/Math.pow(10,9);
        double l = Math.sqrt(Math.pow((d*peak)/(n*lamda),2)- Math.pow(peak,2));
        double sin = x/Math.sqrt(Math.pow(x,2)+Math.pow(l,2));
        return   (d*sin)/n *Math.pow(10,9) ;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isDestroyed();
    }

    public void showDefaultGraph(View view) {
        absorbGraph.setVisibility(View.VISIBLE);
        List<Double> absorb = analyzeImageAbsorb();
        createGraphAbsorb(absorb);
    }
}
