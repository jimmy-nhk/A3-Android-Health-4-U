package com.example.clientapp.activity;

import com.example.clientapp.BuildConfig;
import com.example.clientapp.R;
import com.example.clientapp.helper.adapter.BillingStoreRecycleViewAdapter;
import com.example.clientapp.helper.adapter.HistoryRecyclerViewAdapter;
import com.example.clientapp.model.Cart;
import com.example.clientapp.model.Order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BillingActivity extends AppCompatActivity {
    private static final String TAG = "BillingActivity";
    private BillingStoreRecycleViewAdapter mAdapter;
    // views init
    private RecyclerView billingRecycleView;
    private TextView billingstoreDate;
    private TextView billingstoreIsProcessed;
    private Bitmap bitmap;
    // params init
    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        Intent intent = getIntent();
        cart = intent.getParcelableExtra("cart");
        initViews();
        setLayout();
    }

    // init views
    private void initViews() {
        // billings recycler
        billingRecycleView = findViewById(R.id.billingRecycleView);
        billingstoreDate = findViewById(R.id.billingstoreDate);
        billingstoreIsProcessed = findViewById(R.id.billingstoreIsProcessed);
    }

    // save pdf
    public void savePdfOnClick(View view) {
        try {
            LinearLayout llScroll = findViewById(R.id.outputLinearLayout);
            bitmap = loadBitmapFromView(llScroll, llScroll.getWidth(), llScroll.getHeight());
            createPdf();
        } catch (Exception ignored) {

        }
    }

    // save img
    public void saveImageOnClick(View view) {
        try {
            share(screenShot(findViewById(R.id.outputLinearLayout)));
        } catch (Exception ignored) {
        }

    }

    // screenshot
    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // share
    private void share(Bitmap bitmap){
        String pathofBmp=
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        bitmap,"title", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.app_name)));
    }
    // set layout
    public void setLayout() {
        //Set date
        billingstoreDate.setText(cart.getDate());

        //Check to check Processing condition
        if (cart.getIsFinished()) {
            billingstoreIsProcessed.setText("FINISHED");
            billingstoreIsProcessed.setTextColor(getResources().getColor(R.color.green));
        } else {

            billingstoreIsProcessed.setText("PROCESSING");
            billingstoreIsProcessed.setTextColor(getResources().getColor(R.color.red));
        }

        //Embed list view form list order of cart
        try {
            // linear styles
            mAdapter = new BillingStoreRecycleViewAdapter(cart.getOrderList(), this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            billingRecycleView.setLayoutManager(linearLayoutManager);
            billingRecycleView.setItemAnimator(new DefaultItemAnimator());
            billingRecycleView.setNestedScrollingEnabled(true);
            billingRecycleView.setAdapter(mAdapter);
        } catch (Exception ignored) {

        }
    }

    // load bit map
    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    // create pdf
    private void createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // create canvas
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        File filePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), getString(R.string.app_name)+"_billing_"+cart.getId()+".pdf");
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
//        Toast.makeText(this, "PDF of Scroll is created!!!", Toast.LENGTH_SHORT).show();

        openGeneratedPDF(filePath);

    }

    // open generated pdf
    private void openGeneratedPDF(File path ){
        if (path.exists())
        {
            // open intent
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(path);
            Log.d(TAG,"URI: "+uri.getPath());
            intent.setDataAndType(uri, "application/pdf");
            // set attributes
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);

            try
            {
                Toast.makeText(BillingActivity.this, "Export pdf", Toast.LENGTH_LONG).show();
                startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(BillingActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }
}