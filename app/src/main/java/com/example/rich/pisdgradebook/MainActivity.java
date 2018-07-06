package com.example.rich.pisdgradebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.*;
import android.os.*;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.*;
import android.content.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;
import java.io.*;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

public class MainActivity extends AppCompatActivity {
    private Button b;
    EditText etname,etpassword;
    private Button nextPageButton;
    public static String answer;

    public static String result;

    private static final String POST_URL = "https://gradebook.pisd.edu/pinnacle/gradebook/logon.aspx";
    public static final String GRADEBOOK_ROOT = "https://gradebook.pisd.edu/Pinnacle/Gradebook";
    public static final String LOGON = GRADEBOOK_ROOT + "/logon.aspx";
    private final Map<String, String> cookies = new HashMap<>();
    public static Connection.Response resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b=(Button)findViewById(R.id.button);
        result = "where is it?";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etname=(EditText)findViewById(R.id.username);
                etpassword=(EditText)findViewById(R.id.password);
                try {
                    result = login(etname.getText().toString(), etpassword.getText().toString());
                    //result = "ok at least logged in";
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "log in failed";

                }

                TextView tv;
                tv=(TextView)findViewById(R.id.display);
                tv.setMovementMethod(new ScrollingMovementMethod());
                tv.setText(result);
                //tv.setText("Your Input: \n"+etname.getText().toString()+"\n"+"\n"+etpassword.getText().toString()+"\nEnd.");

                /*Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("text", result);
                startActivity(intent);*/
                tv.setText(result);
            }
        });

        /*nextPageButton = (Button) findViewById(R.id.nextpage);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });*/
    }

    public static String login(String username, String password) throws IOException {
        final URL LOAD_URL = new URL(LOGON);
        final String USERNAME_FIELD = "ctl00$ContentPlaceHolder$Username";
        final String PASSWORD_FIELD = "ctl00$ContentPlaceHolder$Password";


        try {
            // Submitting form data.
            Document html = Jsoup.parse(LOAD_URL, 60000);
            FormElement form = (FormElement) html.getElementsByTag("form").get(0);
            Connection conn = form.submit();

            // Find username and password field.
            Connection.KeyVal usernameField = null;
            Connection.KeyVal passwordField = null;
            for (Connection.KeyVal field : conn.request().data())
                if (field.key().equals(USERNAME_FIELD))
                    usernameField = field;
                else if (field.key().equals(PASSWORD_FIELD))
                    passwordField = field;

            if (usernameField == null) {
                System.err.println("EXPECTED: Form field for username.");
                return "-2";
            }

            if (passwordField == null) {
                System.err.println("EXPECTED: Form field for password.");
                return "-2";
            }

            // Submit username and password

            usernameField.value(username);
            passwordField.value(password);
            resp = conn.timeout(60000).execute();

            if (resp.url().equals(LOAD_URL))
                return "-1";
            else {
                /*cookies.putAll(resp.cookies());
                updateExpirationDate();

                students.addAll(Parser.parseStudents(this, resp.body()));
                MULTIPLE_STUDENTS = students.size() > 1;

                loggedIn = true;*/


                Thread downloadThread = new Thread() {
                    public void run() {
                        Document doc;
                        try {
                            // doc = Jsoup.connect("https://gradebook.pisd.edu/Pinnacle/Gradebook/InternetViewer/GradeReport.aspx").get();
                            doc = Jsoup.parse(resp.body());
                            String title = doc.title();
                            System.out.println("Title: " + title);
                            //System.out.println(doc.body().text());
                            Element link = doc.selectFirst("body").selectFirst("form").selectFirst("section");

                            Element gettingCloser = link.select("div").get(31);
                            Element evenCloser = gettingCloser.select("div").first().selectFirst("div");
                            Element moreCloser = evenCloser.getElementsByAttribute("id").get(0);

                            String result = "";

                            result += "----------------------------------------\n";
                            int count = 0;
                            List<Element> list = moreCloser.getElementsByAttributeValue("class", "row");
                            for (Element ele : list) {
                                String course = list.get(count).select("div").get(2).text();
                                result += course + "\n";
                                //System.out.println(list.get(count).text());
                                List<Element> innerList = list.get(count).getElementsByAttributeValue("class", "letter");
                                List<Element> labelList = list.get(count).getElementsByAttributeValue("class", "letter-label");
                                for (int j = 0; j < innerList.size(); j++) {
                                    result += labelList.get(j).text() + "\n";
                                    result += innerList.get(j).text() + "\n";

                                }
                                count++;
                                result += "----------------------------------------\n";
                            }

                            answer = result;
                            System.out.println(result);
                            System.out.println(answer);







                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                downloadThread.start();

                return answer;
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return "-2";
        }
    }

}