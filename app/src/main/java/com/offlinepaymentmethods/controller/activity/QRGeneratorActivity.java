package com.offlinepaymentmethods.controller.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.offlinepaymentmethods.R;
import com.offlinepaymentmethods.controller.base.BaseActivity;
import com.offlinepaymentmethods.dto.ClientInfo;
import com.offlinepaymentmethods.utils.Constants;
import com.offlinepaymentmethods.utils.EncryptDecrypt;

import butterknife.InjectView;
import me.ydcool.lib.qrmodule.encoding.QrGenerator;

/**
 * Created by aakashsharma on 12/05/17.
 * Reference URL: https://github.com/Ydcool/QrModule
 */

public class QRGeneratorActivity extends BaseActivity {

    @InjectView(R.id.qrcode_image_activity_qrcode_generator)
    ImageView _qrImage;

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && !bundle.isEmpty()) {
            if(bundle.containsKey("ClientData")) {
                ClientInfo info = (ClientInfo) bundle.getSerializable("ClientData");
                if(info != null) {
                    try {
                        generateQRCode(new EncryptDecrypt().encrypt(info.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        showSnackBar("Unable to generate qr code, some thing bad happened !!!");
                    }
                } else {
                    showSnackBar("Unable to get data");
                }
            }
        }
    }

    private void showSnackBar(String message) {
        if (this.getCurrentFocus() != null)
            Snackbar.make(this.getCurrentFocus(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void initLayout() {
        setContentView(R.layout.activity_qrcode_generator);
    }

    private void generateQRCode(String content) throws WriterException {
        content += Constants.SECURITY_KEY;
        Bitmap qrCode = new QrGenerator.Builder()
                .content(content)
                .qrSize(300)
                .margin(2)
                .color(Color.BLACK)
                .bgColor(Color.WHITE)
                .ecc(ErrorCorrectionLevel.H)
                .overlay(getApplicationContext(), R.mipmap.ic_launcher)
                .overlaySize(100)
                .overlayAlpha(255)
                .overlayXfermode(PorterDuff.Mode.SRC)
                .encode();

        _qrImage.setImageBitmap(qrCode);
    }

}
