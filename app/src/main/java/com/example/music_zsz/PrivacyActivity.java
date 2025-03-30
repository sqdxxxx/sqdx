package com.example.music_zsz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PrivacyActivity extends AppCompatActivity {

    private TextView dialogTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        showPrivacyDialog();
    }

    private void showPrivacyDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        View dialogView = getLayoutInflater().inflate(R.layout.activity_privacy, null);
        builder.setView(dialogView);


        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);


        dialogTvContent = dialogView.findViewById(R.id.tv_content);
        Button btnDisagree = dialogView.findViewById(R.id.btn_disagree);
        Button btnAgree = dialogView.findViewById(R.id.btn_agree);
        setupClickableText(dialogTvContent);


        btnDisagree.setOnClickListener(v -> finishAffinity());
        btnAgree.setOnClickListener(v -> navigateToMain());


        dialog.show();
    }

    private void setupClickableText(TextView textView) {
        String fullText = dialogTvContent.getText().toString();
        SpannableString spannableString = new SpannableString(fullText);


        int userAgreementStart = fullText.indexOf("《用户协议》");
        int userAgreementEnd = userAgreementStart + "《用户协议》".length();

        int privacyPolicyStart = fullText.indexOf("《隐私政策》");
        int privacyPolicyEnd = privacyPolicyStart + "《隐私政策》".length();


        ClickableSpan userAgreementSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openWebPage("https://www.mi.com");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan privacyPolicySpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openWebPage("https://www.xiaomiev.com/");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        };


        spannableString.setSpan(userAgreementSpan, userAgreementStart, userAgreementEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(privacyPolicySpan, privacyPolicyStart, privacyPolicyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void openWebPage(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "无法打开链接", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        PrivacyManager privacyManager = PrivacyManager.getInstance();
        Intent intent = new Intent(this, MainActivity.class);
        privacyManager.setAgreed();
        startActivity(intent);
        finish();
    }
}