package com.example.clientapp.activity;

import com.example.clientapp.BuildConfig;
import com.example.clientapp.R;
import com.example.clientapp.helper.adapter.BillingStoreRecycleViewAdapter;
import com.example.clientapp.model.Cart;
import androidx.core.content.FileProvider;

import androidx.appcompat.app.AppCompatActivity;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BillingActivity extends AppCompatActivity {
    private static final String TAG = "BillingActivity";
    private BillingStoreRecycleViewAdapter mAdapter;
    // views init
    private RecyclerView billingRecycleView;
    private TextView billingStoreDate;
    private TextView billingStoreIsProcessed;
    private TextView billingMoneyTotal;
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
        billingStoreDate = findViewById(R.id.billingstoreDate);
        billingStoreIsProcessed = findViewById(R.id.billingstoreIsProcessed);
        billingMoneyTotal = findViewById(R.id.billingMoneyTotal);
    }

    // save pdf
    public void savePdfOnClick(View view) {
        try {

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
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // share
    private void share(Bitmap bitmap){
        String pathofBmp=
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        bitmap, "title", null);
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
        billingStoreDate.setText(cart.getDate());

        //Check to check Processing condition
        if (cart.getIsFinished()) {
            billingStoreIsProcessed.setText("FINISHED");
            billingStoreIsProcessed.setTextColor(getResources().getColor(R.color.green));
        } else {

            billingStoreIsProcessed.setText("PROCESSING");
            billingStoreIsProcessed.setTextColor(getResources().getColor(R.color.red));
        }
        //Set total money
        billingMoneyTotal.setText(cart.getPrice()+" $");
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
     private void createPdf() {
        LinearLayout llScroll = findViewById(R.id.outputLinearLayout);
        bitmap = loadBitmapFromView(llScroll, llScroll.getWidth(), llScroll.getHeight());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = bitmap.getHeight();
        float width = bitmap.getWidth();

        int convertHeight = (int) hight, convertWidth = (int) width;

        // pdf
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // create canvas
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        // write the document content
        File filePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), getString(R.string.app_name) + "_billing_" + cart.getId() + ".pdf");
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();

        // open generated pdf file
        openGeneratedPDF(filePath);

    }

    // Function open generated pdf file
    private void openGeneratedPDF(File path) {
        if (path.exists()) {
            // Create intent to open file
            Intent intent = new Intent(Intent.ACTION_VIEW);

            //Get view from URI using File Provider configured in manifest and filepaths.xml
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID
                    + ".fileprovider", path);
            Log.d(TAG, "URI: " + uri); // Printout the path for checking
            //Set flags for intent, without this, cannot access file
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //set datatype as pdf and uri to pdf file for the content
            intent.setDataAndType(uri, "application/pdf");
            try {
                //condition check if intent has package manager
                if (intent.resolveActivity(getPackageManager()) == null) {
                    // Show an error
                } else {
                    Toast.makeText(BillingActivity.this, "Export pdf", Toast.LENGTH_LONG).show();
                    startActivity(intent); //Start intent to show pdf file
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(BillingActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void finishIntentOnClick(View view) {
        this.finish();
    }
}