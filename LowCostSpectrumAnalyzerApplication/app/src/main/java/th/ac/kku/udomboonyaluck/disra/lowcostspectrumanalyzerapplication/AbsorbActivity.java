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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
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
    Button showSpec,showDefault,showAbsorb;
    private List<Data> datas;
    private int position,peak,n,x;
    private int width,height;
    private Bitmap image;
    double lambda,d,f;
    List<Double> lambdaRange;
    List<Integer> new_x,new_lambda;

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
        showSpec = findViewById(R.id.showSpec);
        showDefault = findViewById(R.id.showDefault);
        showAbsorb = findViewById(R.id.showAbsorb);

        datas = db.allDatas();
        imageName.setText(datas.get(position).getName());
        image = decodeImage(datas.get(position).getPosition());
        spectrum.setImageBitmap(image);

        width = image.getWidth();
        height = image.getHeight();
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
                showGraph(waveGraph,image);
            }
        });

        showDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap defaultImage = decodeImage(datas.get(0).getPosition());
                showGraph(absorbGraph,defaultImage);
            }
        });

        showAbsorb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap defaultImage = decodeImage(datas.get(0).getPosition());
                showAbsorbGraph(defaultImage,image);
            }
        });
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

        createAbsorbGraph(spec380to740Ab);
    }

    private void createAbsorbGraph(List<Double> Yaxis) {
        absorbGraph.setVisibility(View.VISIBLE);
        absorbGraph.getXAxis().setLabelCount(9,true);

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
                set1.setFillColor(Color.TRANSPARENT);
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

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isDestroyed();
    }

    public void showGraph(LineChart chart,Bitmap image) {
        List<Double> intensity,spec380to740;
        List<Integer> intervalIntensity;
        intensity = findXarr(image);
        intervalIntensity = findNewX();

        spec380to740 = new ArrayList<>();

        for(int i = 0;i < intervalIntensity.size();i++){
            spec380to740.add(intensity.get(intervalIntensity.get(i)-x+peak));
        }

        createGraph(chart,spec380to740);
    }

    private void createGraph(final LineChart chart, List<Double> Yaxis) {
        chart.setVisibility(View.VISIBLE);
        chart.getXAxis().setLabelCount(9,true);

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
                set1.setFillColor(Color.TRANSPARENT);
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
