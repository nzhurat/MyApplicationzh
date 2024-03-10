package com.example.myapplicationzh;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyApp";

    private EditText editText;
    private TextView outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.input1);
        outputTextView = findViewById(R.id.output);
        Button submitButton = findViewById(R.id.button);
        Button button1 = findViewById(R.id.button1);

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String inputText = editText.getText().toString();
                Threed(inputText);
            }
        });

        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String inputText = editText.getText().toString();
                modifyAsciiAndSubmit(inputText);
            }
        });
    }

    private void Threed(final String studentNumber)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    Log.d(TAG, "Connecting to the server...");
                    Socket socket = new Socket("se2-submission.aau.at", 20080);
                    Log.d(TAG, "Connected to the server.");

                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeBytes(studentNumber + '\n');
                    Log.d(TAG, "Sent student number to the server.");

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    final String result = bufferedReader.readLine();
                    Log.d(TAG, "Received result from the server: " + result);

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            outputTextView.setText(result);
                        }
                    });

                    socket.close();
                    Log.d(TAG, "Socket closed.");

                } catch (IOException e)
                {
                    e.printStackTrace();
                    Log.e(TAG, "Error during network operations: " + e.getMessage());
                }
            }
        }).start();
    }

    private void modifyAsciiAndSubmit(final String studentNumber)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.d(TAG, "Connecting to the server...");
                    Socket socket = new Socket("se2-submission.aau.at", 20080);
                    Log.d(TAG, "Connected to the server.");

                    StringBuilder modifiedStudentNumber = new StringBuilder();
                    for (int i = 0; i < studentNumber.length(); i++)
                    {
                        char currentChar = studentNumber.charAt(i);
                        if (Character.isDigit(currentChar)) {
                            int digit = Character.getNumericValue(currentChar);
                            if (i % 2 == 1)
                            {
                                char asciiChar = (char) ('a' + digit);
                                modifiedStudentNumber.append(asciiChar);
                            } else
                            {
                                modifiedStudentNumber.append(currentChar);
                            }
                        } else
                        {
                            modifiedStudentNumber.append(currentChar);
                        }
                    }

                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    dataOutputStream.writeBytes(modifiedStudentNumber.toString() + '\n');
                    Log.d(TAG, "Sent modified student number to the server.");

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    final String result = bufferedReader.readLine();
                    Log.d(TAG, "Received result from the server: " + result);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            outputTextView.setText("Modified Student Number: " + modifiedStudentNumber.toString() + "\nResult from the server: " + result);
                        }
                    });

                    socket.close();
                    Log.d(TAG, "Socket closed.");

                } catch (IOException e)
                {
                    e.printStackTrace();
                    Log.e(TAG, "Error during network operations: " + e.getMessage());
                }
            }
        }).start();
    }
}
