package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Random;

public class CompareDataActivity extends AppCompatActivity {
    TextView mItemSelected;
    private String[] listItems;
    private boolean[] checkedItems;
    public SQLiteDatabaseHandler db;
    private ArrayList<Integer> mUserItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private LineChart chart;
    private String[] images;
    private String[] itemsNames;
    private Boolean setData = Boolean.FALSE;
    private List<Double> firstData,secondData;
    private List<List<Double>> listOfLists = new ArrayList<List<Double>>();
    private int width,height;
    private List<Data> datas;
    private int position,peak,n,x;
    double lambda,d,f;
    List<Double> lambdaRange;
    List<Integer> new_x,new_lambda;
    private Button createGraph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_data);
        db = new SQLiteDatabaseHandler(this);
        List<Data> datas = db.allDatas();
        listItems = new String[datas.size()];
        for(int i=0;i < datas.size();i++){
            listItems[i] = datas.get(i).getName();
        }

        checkedItems = new boolean[listItems.length];
        showDialogList();
        //set recyclerView
        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chart = (LineChart) findViewById(R.id.chart1);
        createGraph = findViewById(R.id.btCreateGraph);

        width = decodeImage(datas.get(0).getPosition()).getWidth();
        height = decodeImage(datas.get(0).getPosition()).getHeight();
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

        createGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGraphWithAllLists();
            }
        });

    }

    public void showDialogList() {
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(CompareDataActivity.this);
        mBuilder.setTitle("list data");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                if(isChecked){
                    mUserItems.add(position);
                }else{
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                itemsNames = new String[mUserItems.size()];
                images = new String[mUserItems.size()];
                for (int i = 0; i < mUserItems.size(); i++) {
                    itemsNames[i] = listItems[mUserItems.get(i)];
                    images[i] = search_imageString(listItems[mUserItems.get(i)]);
                    listOfLists.add(setIntervalOfEachImage(decodeImage((images[i]))));

                }
                MyRecyclerAdapter adapter = new MyRecyclerAdapter(itemsNames,images);
                recyclerView.setAdapter(adapter);
//                mItemSelected.setText(item);
            }
        });

        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mUserItems.clear();
                    mItemSelected.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    public String search_imageString(String name){
        List<Data> datas = db.allDatas();
        Data data = null;
        if (datas != null) {

            for (int i = 0; i < datas.size(); i++) {
                if(datas.get(i).getName().equals(name)){
                    data = datas.get(i);
                }
            }
        }
        return data.getPosition();
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private void createMultiGraph(List<List<Double>> Yaxis, String[] nameGraph) {
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
        // add point in graph

        for(int i = 380 ; i <= 740; i++){
            new_lambda.add(i);
        }

//        values.add(new Entry(0, 4));

        LineDataSet set1;
        List<List<Entry>> values = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineData data;
        for(int i = 0;i < Yaxis.size(); i++){
            List<Entry> value = new ArrayList<>();
            Random rnd = new Random();
            int graphColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            for (int j = 0; j < Yaxis.get(i).size(); j++) {
                float y = Float.parseFloat("" + Yaxis.get(i).get(j));
                value.add(new Entry(new_lambda.get(j), y));
            }

            values.add(value);

            if (chart.getData() != null &&
                    chart.getData().getDataSetCount() > 0) {
                set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
                set1.setValues(values.get(i));
                set1.notifyDataSetChanged();
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
            } else {
                // create a dataset and give it a type
                Log.d("set1",nameGraph[i]);
                set1 = new LineDataSet(values.get(i), nameGraph[i]);

                set1.setDrawIcons(false);

                // draw dashed line
                set1.enableDashedLine(10f, 5f, 0f);

                // black lines and points
                set1.setColor(graphColor);
                set1.setCircleColor(graphColor);

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
                dataSets.add(set1); // add the data sets
            }
            // create a data object with the data set

        }
        data = new LineData(dataSets);
        // set data
        chart.setData(data);
    }

    public List<Double> setIntervalOfEachImage(Bitmap image) {
        List<Double> intensity,spec380to740;
        List<Integer> intervalIntensity;
        intensity = findXarr(image);
        intervalIntensity = findNewX();

        spec380to740 = new ArrayList<>();

        for(int i = 0;i < intervalIntensity.size();i++){
            spec380to740.add(intensity.get(intervalIntensity.get(i)-x+peak));
        }
        return spec380to740;
    }

    private void showGraphWithAllLists(){
        List<List<Double>> dataSets = new ArrayList<List<Double>>(listOfLists);
        createMultiGraph(dataSets,itemsNames);
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
