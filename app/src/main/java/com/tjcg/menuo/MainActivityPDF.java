package com.tjcg.menuo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivityPDF extends AppCompatActivity {

    ArrayList<String> arr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_p_d_f);

//        PdfConverter converter = PdfConverter.getInstance();
//        File file = new File(Environment.getExternalStorageDirectory().toString(), "file.pdf");
//        String htmlString = "<html><body><p>WHITE (default)</p></body></html>";
//        converter.convert(getApplicationContext(), htmlString, file);


    }
//    public void ConvertHTMLStringToPDF()
//    {
//        // note: be sure to copy the helper function ConvertHTMLStringToPDF() from this webpage
//        String apiKey = "ABCD-1234";
//        String value = "<h1>An <strong>Example</strong>HTML String</h1>"; // an HTML string
//        String apiURL = "https://api.html2pdfrocket.com/pdf";
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("apiKey", apiKey);
//        params.put("value", value);
//
//        // Call the API convert to a PDF
//        InputStreamReader request = new InputStreamReader(Request.Method.Post, apiURL,
//                new Response.Listener<byte[]>(){
//                    @Override
//                    public void onResponse(byte[] response)
//                    {
//                        try
//                        {
//                            if(response != null)
//                            {
//                                File localFolder = new File(Environment.getExternalStorageDirectory(), "WebPageToPDF");
//                                if(!localFolder.exists())
//                                {
//                                    localFolder.mkdirs();
//                                }
//
//                                // Write stream output to local file
//                                File pdfFile =  new File (localFolder, "MySamplePDFFile.pdf");
//                                OutputStream opStream = new FileOutputStream(pdfFile);
//                                pdfFile.setWritable(true);
//                                opStream.write(response);
//                                opStream.flush();
//                                opStream.close();
//                            }
//                        } catch (Exception ex)
//                        {
//                            Toast.makeText(getApplicationContext(), "Error while generating PDF file!!", Toast.LENGTH_LONG).show();
//                        }
//                    }});
//    }

}