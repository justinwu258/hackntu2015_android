package project.hackthon.ntu.newsmartstation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends ActionBarActivity {

    //確定content有無內容
    int t = 0;
   //取得content內容
    String row ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yyyy);

        final MainActivity activity = new MainActivity();

        //取得EditText參照
        final EditText postbox_id = (EditText) findViewById(R.id.postbox_id_editable);
        final EditText ZIPCODE = (EditText) findViewById(R.id.ZIPCODE_editable);

        //取得TextView參照
        final TextView ZIPCODE_text = (TextView)findViewById(R.id.ZIPCODE_post);
        final TextView postbox_id_text = (TextView) findViewById(R.id.postbox_id_post);
        final TextView postbox_longitude = (TextView) findViewById(R.id.postbox_longitude_post);
        final TextView postbox_latitude = (TextView) findViewById(R.id.postbox_latitude_post);
        final TextView send_date = (TextView) findViewById(R.id.send_date_post);
        final TextView send_time = (TextView) findViewById(R.id.send_time_post);
        final TextView left_box_mail_num = (TextView) findViewById(R.id.left_box_mail_num_post);
        final TextView left_box_mail_weight = (TextView) findViewById(R.id.left_box_mail_weight_post);
        final TextView right_box_mail_num = (TextView) findViewById(R.id.right_box_mail_num_post);
        final TextView right_box_mail_weight = (TextView) findViewById(R.id.right_box_mail_weight_post);

        //取的search_button參照
        Button search = (Button) findViewById(R.id.search_button);

        //search_button監聽
        search.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //取得“輸入的所屬郵局”
                String postbox = postbox_id.getText().toString();

                //取得“輸入的行政區”
                String zipcode = ZIPCODE.getText().toString();

                //生成URL
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            try {
                                URL url = new URL("http://52.1.130.19/SmartStation/index.php");
                                //获取HttpURLConnection对象
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                                //使用Post方式提交数据
                                httpURLConnection.setDoInput(true);
                                httpURLConnection.setDoOutput(true);

                                // 此方法在正式链接之前设置才有效。
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setUseCaches(false);
                                httpURLConnection.connect();
                                //Post方式提交数据，需要用到数据输出流
                                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                                String postContent = URLEncoder.encode("operation", "UTF-8") + "="
                                        + URLEncoder.encode("get_data", "UTF-8") + "&"
                                        + URLEncoder.encode("is_only_one", "UTF-8") + "="
                                        + URLEncoder.encode("1", "UTF-8") + "&"
                                        + URLEncoder.encode("tablename", "UTF-8") + "="
                                        + URLEncoder.encode("postbox_receiving", "UTF-8");

                                dataOutputStream.write(postContent.getBytes());
                                //刷新操作，强制将可能未输出的数据及时写入内存缓冲区。
                                dataOutputStream.flush();
                                //執行dataOutputStream.close()后，POST请求结束
                                dataOutputStream.close();

                                // 开始GET数据
                                String encoding = httpURLConnection.getContentEncoding();
                                InputStream is = httpURLConnection.getInputStream();
                                int read = -1;
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                while ((read = is.read()) != -1) {
                                    baos.write(read);
                                }
                                //将ByteArrayOutputStream中的缓冲数据，一次性生成byte[]
                                byte[] data = baos.toByteArray();
                                baos.close();

                                String content = null;
                                if (encoding != null) {
                                    content = new String(data, encoding);
                                } else {
                                    content = new String(data);
                                }
                                row = content;
                                Log.v("ntu", content);
                                t = 1;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                t = 1;
                            } catch (IOException e) {
                                e.printStackTrace();
                                t = 1;
                            }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject(row);
                                    String str_zip_code = json.getString("zip_code");
                                    ZIPCODE_text.setText(str_zip_code);
                                    String str_postbox_id = json.getString("postbox_id");
                                    postbox_id_text.setText(str_postbox_id);
                                    String str_postbox_longitude = json.getString("postbox_longitude");
                                    postbox_longitude.setText(str_postbox_longitude);
                                    String str_postbox_latitude = json.getString("postbox_latitude");
                                    postbox_latitude.setText(str_postbox_latitude);
                                    String str_send_date = json.getString("send_date");
                                    send_date.setText(str_send_date);
                                    String str_send_time = json.getString("send_time");
                                    send_time.setText(str_send_time);
                                    String str_left_box_mail_num = json.getString("left_box_mail_num");
                                    left_box_mail_num.setText(str_left_box_mail_num);
                                    String str_left_box_mail_weight = json.getString("left_box_mail_weight");
                                    left_box_mail_weight.setText(str_left_box_mail_weight);
                                    String str_right_box_mail_num = json.getString("right_box_mail_num");
                                    right_box_mail_num.setText(str_right_box_mail_num);
                                    String str_right_box_mail_weight = json.getString("right_box_mail_weight");
                                    right_box_mail_weight.setText(str_right_box_mail_weight);
                                } catch (JSONException e) {}
                            }
                        });
                    }
                }).start();

//                synchronized (this){
//                    try {
//                        JSONObject json = new JSONObject(row);
//                        String str_zip_code = json.getString("zip_code");
//                        ZIPCODE_text.setText(str_zip_code);
//                        String str_postbox_id = json.getString("postbox_id");
//                        postbox_id_text.setText(str_postbox_id);
//                        String str_postbox_longitude = json.getString("postbox_longitude");
//                        postbox_longitude.setText(str_postbox_longitude);
//                        String str_postbox_latitude = json.getString("postbox_latitude");
//                        postbox_latitude.setText(str_postbox_latitude);
//                        String str_send_date = json.getString("send_date");
//                        send_date.setText(str_send_date);
//                        String str_send_time = json.getString("send_time");
//                        send_time.setText(str_send_time);
//                        String str_left_box_mail_num = json.getString("left_box_mail_num");
//                        left_box_mail_num.setText(str_left_box_mail_num);
//                        String str_left_box_mail_weight = json.getString("left_box_mail_weight");
//                        left_box_mail_weight.setText(str_left_box_mail_weight);
//                        String str_right_box_mail_num = json.getString("right_box_mail_num");
//                        right_box_mail_num.setText(str_right_box_mail_num);
//                        String str_right_box_mail_weight = json.getString("right_box_mail_weight");
//                        right_box_mail_weight.setText(str_right_box_mail_weight);
//                    } catch (JSONException e) {}
//                    if (t==1){break;}
//                }
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu items for use in the action bar
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menumain_activity_actions.xml.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettings() {}
    private void openSearch() {}

}
