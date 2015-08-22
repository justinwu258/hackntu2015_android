package project.hackthon.ntu.smartstation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int fragment_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        final MainActivity activity = new MainActivity();





    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = PlaceholderFragment.newInstance(position + 1);
                break;

            case 1:
                fragment = new IntroFragment();
                break;

            case 2:
                fragment = new AboutUS_Fragment();
                break;

            default:
                //ÁÙ¨S»s§@ªº¿ï¶µ¡Afragment ¬O null¡Aª½±µªð¦^
                return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        Log.v("ntu_debug","number = " + number);
        Intent intent;
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
//                intent = new Intent(this,MainActivity.class);
//                startActivity(intent);
//                finish();
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
//                intent = new Intent(this,IntroActivity.class);
//                startActivity(intent);
//                finish();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
//                intent = new Intent(this,AboutUS_Activity.class);
//                startActivity(intent);
//                finish();
                break;
        }
        fragment_page = number;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        //確定content有無內容
        int t = 0;
        //取得content內容
        String row ="";
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //??EditText??
            final EditText postbox_id = (EditText) rootView.findViewById(R.id.postbox_id_editable);
            final EditText ZIPCODE = (EditText) rootView.findViewById(R.id.ZIPCODE_editable);

            //??TextView??
            final TextView ZIPCODE_text = (TextView)rootView.findViewById(R.id.ZIPCODE_post);
            final TextView postbox_id_text = (TextView) rootView.findViewById(R.id.postbox_id_post);
            final TextView postbox_longitude = (TextView) rootView.findViewById(R.id.postbox_longitude_post);
            final TextView postbox_latitude = (TextView) rootView.findViewById(R.id.postbox_latitude_post);
            final TextView send_date = (TextView) rootView.findViewById(R.id.send_date_post);
            final TextView send_time = (TextView) rootView.findViewById(R.id.send_time_post);
            final TextView left_box_mail_num = (TextView) rootView.findViewById(R.id.left_box_mail_num_post);
            final TextView left_box_mail_weight = (TextView) rootView.findViewById(R.id.left_box_mail_weight_post);
            final TextView right_box_mail_num = (TextView) rootView.findViewById(R.id.right_box_mail_num_post);
            final TextView right_box_mail_weight = (TextView) rootView.findViewById(R.id.right_box_mail_weight_post);
            //??search_button??
            if(rootView !=null){
                postbox_id.requestFocus();
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);
            }

            Button search = (Button) rootView.findViewById(R.id.search_button);

            //search_button??
            search.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //???????????
                    String postbox = postbox_id.getText().toString();

                    //??????????
                    String zipcode = ZIPCODE.getText().toString();

                    //??URL
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL("http://52.1.130.19/SmartStation/index.php");
                                //??HttpURLConnection??
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                                //??Post??????
                                httpURLConnection.setDoInput(true);
                                httpURLConnection.setDoOutput(true);

                                // ????????????????
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setUseCaches(false);
                                httpURLConnection.connect();
                                //Post????????????????
                                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                                String postContent = URLEncoder.encode("operation", "UTF-8") + "="
                                        + URLEncoder.encode("get_data", "UTF-8") + "&"
                                        + URLEncoder.encode("is_only_one", "UTF-8") + "="
                                        + URLEncoder.encode("1", "UTF-8") + "&"
                                        + URLEncoder.encode("tablename", "UTF-8") + "="
                                        + URLEncoder.encode("postbox_receiving", "UTF-8");

                                dataOutputStream.write(postContent.getBytes());
                                //??????????????????????????
                                dataOutputStream.flush();
                                //??dataOutputStream.close()??POST????
                                dataOutputStream.close();

                                // ??GET??
                                String encoding = httpURLConnection.getContentEncoding();
                                InputStream is = httpURLConnection.getInputStream();
                                int read = -1;
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                while ((read = is.read()) != -1) {
                                    baos.write(read);
                                }
                                //?ByteArrayOutputStream????????????byte[]
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

                            getActivity().runOnUiThread(new Runnable() {
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
                                    } catch (JSONException e) {
                                    }
                                }
                            });
                        }
                    }).start();

                }
            });
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            Log.v("Justin", "call attach");
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu items for use in the action bar
//        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        restoreActionBar();
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
