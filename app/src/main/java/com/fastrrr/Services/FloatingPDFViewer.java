package com.fastrrr.Services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fastrrr.Adapter.PDFFileAdapter;
import com.fastrrr.R;
import com.fastrrr.Singletone.Constants;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FloatingPDFViewer extends Service {

    WindowManager wm;
    ImageView buttonClose;
    ProgressBar pbar;
    private View mFloatView;
    private RelativeLayout relativeLayoutListing;
    private LinearLayout linearLayoutViewer;
    ArrayList<File> pdfFileArray = new ArrayList<File>();
    ImageView imageViewBack;

    private PDFFileAdapter mAdapter;

    private ListView listViewPDFFile;

    private WebView webViewPdfReader;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        parameters.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View myView = inflater.inflate(R.layout.service_pdf_viewer, null);
        myView.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatedParameters = parameters;
            double x;
            double y;
            double pressedX;
            double pressedY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        pressedX = event.getRawX();
                        pressedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - pressedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - pressedY));

                        wm.updateViewLayout(myView, updatedParameters);

                    default:
                        break;
                }
                return false;
            }
        });
        buttonClose= (ImageView)myView.findViewById(R.id.buttonClose);


        UIReference(myView);
        UIClick();

        Constants.showNotification(FloatingPDFViewer.this,0);

        getPDFFile(Environment.getExternalStorageDirectory());
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(myView);
                stopSelf();
                //System.exit(0);
                Constants.mNotificationManager.cancel(0);
            }
        });

        wm.addView(myView, parameters);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    public void UIReference(View view)
    {
        listViewPDFFile = (ListView) view.findViewById(R.id.listViewPDFFile);
        relativeLayoutListing = (RelativeLayout) view.findViewById(R.id.relativeLayoutListing);
        linearLayoutViewer = (LinearLayout) view.findViewById(R.id.linearLayoutViewer);
        linearLayoutViewer.setVisibility(View.GONE);
        webViewPdfReader = (WebView) view.findViewById(R.id.webViewPdfReader);
        imageViewBack = (ImageView) view.findViewById(R.id.imageViewBack);
    }
    public void UIClick()
    {
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutViewer.setVisibility(View.GONE);
                relativeLayoutListing.setVisibility(View.VISIBLE);
            }
        });
        listViewPDFFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                linearLayoutViewer.setVisibility(View.VISIBLE);
                relativeLayoutListing.setVisibility(View.GONE);

                File filePath = mAdapter.getItem(i);
                String Name = String.valueOf(filePath);
                Name = Name.substring(Name.lastIndexOf("/")+1);
                //webview = (WebView) findViewById(R.id.webview);
               /* WebSettings settings = webViewPdfReader.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setAllowFileAccessFromFileURLs(true);
                settings.setAllowUniversalAccessFromFileURLs(true);
                settings.setBuiltInZoomControls(true);
                webViewPdfReader.setWebChromeClient(new WebChromeClient());
                webViewPdfReader.loadUrl(Name);*/

                webViewPdfReader.getSettings().setJavaScriptEnabled(true);
                webViewPdfReader.getSettings().setPluginState(WebSettings.PluginState.ON);
                webViewPdfReader.getSettings().setBuiltInZoomControls(true);
                webViewPdfReader.getSettings().setAllowFileAccess(true);
                webViewPdfReader.getSettings().setUseWideViewPort(true);
                webViewPdfReader.setWebViewClient(new AppWebViewClients());

                //webview.getSettings().setJavaScriptEnabled(true);
                webViewPdfReader.setContentDescription("application/pdf");
                //webview.loadUrl(path);

                //webViewSoilReport.loadUrl("http://docs.google.com/gview?embedded=true&url=";

                String pdfURL = "http://alliedinfosoftdemo.com/myagriguru/admin/uploads/soilreport/584533eec088dsoilreport.pdf";
                //String file = "http://www.imdagrimet.gov.in/advisory?state=" + StateName+"&district="+DisctrictName+"&language=Local";
                //String myScript="http://docs.google.com/gview?embedded=true&url="+Name+"";
                String myScript= String.valueOf(filePath);
                webViewPdfReader.loadData(myScript, "text/html", "UTF-8");
                webViewPdfReader.loadUrl(myScript);

            }
        });
    }

    public class AppWebViewClients extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

        }
    }

    public void getPDFFile(File dir)
    {
        File[] listFile;
        listFile = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getPDFFile(listFile[i]);
                } else {
                    if (listFile[i].getName().toLowerCase().endsWith(".pdf")){
                        pdfFileArray.add(listFile[i]);
                    }
                    mAdapter = new PDFFileAdapter(getApplicationContext(),pdfFileArray);
                    listViewPDFFile.setAdapter(mAdapter);
                }
            }
        }
    }

}