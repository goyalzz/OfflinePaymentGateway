package com.offlinepaymentmethods.controller.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.offlinepaymentmethods.R;
import com.offlinepaymentmethods.controller.base.BaseFragment;
import com.offlinepaymentmethods.dto.ClientInfo;
import com.offlinepaymentmethods.interfaces.OnActivityResultListener;
import com.offlinepaymentmethods.interfaces.OnClickListener;
import com.offlinepaymentmethods.utils.Constants;
import com.offlinepaymentmethods.utils.EncryptDecrypt;
import com.offlinepaymentmethods.utils.PermissionChecker;
import com.offlinepaymentmethods.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by aakashsharma on 12/05/17.
 */

public class MerchantFragment extends BaseFragment implements OnActivityResultListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener, SmoothBluetooth.Listener {

    private OnClickListener onClickListener;

    @InjectView(R.id.id_merchant_fragment)
    TextView _clientId;

    @InjectView(R.id.name_merchant_fragment)
    TextView _clientName;

    @InjectView(R.id.email_merchant_fragment)
    TextView _clientEmail;

    @InjectView(R.id.balance_merchant_fragment)
    TextView _merchantBalance;

    @InjectView(R.id.cod_merchant_fragment)
    RadioButton _codPayment;

    @InjectView(R.id.qr_merchant_fragment)
    RadioButton _qrPayment;

    @InjectView(R.id.bt_merchant_fragment)
    RadioButton _btPayment;

    @InjectView(R.id.qr_scan_merchant_fragment)
    Button _qrScanner;

    @InjectView(R.id.received_info_merchant_fragment)
    TextView _receivedInfo;

    @InjectView(R.id.bt_scan_merchant_fragment)
    Button _btScanner;

    private String[] allowPermissions = new String[]{"android.permission.CAMERA"};

    private SmoothBluetooth mSmoothBluetooth;

    private static final int ENABLE_BT_REQUEST = 1;

    private List<Integer> mBuffer = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.merchant_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _merchantBalance.setText("Available Balance: ₹" + PreferenceManager.getInstance(getContext()).getMerchantBalance());
        mSmoothBluetooth = new SmoothBluetooth(getContext(), SmoothBluetooth.ConnectionTo.ANDROID_DEVICE, SmoothBluetooth.Connection.SECURE, this);
        _qrScanner.setOnClickListener(this);
        _btScanner.setOnClickListener(this);
        _codPayment.setOnCheckedChangeListener(this);
        _qrPayment.setOnCheckedChangeListener(this);
        _btPayment.setOnCheckedChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onClickListener = (OnClickListener) context;
    }

    @Override
    public void onActivityResult(String data) {
        try {
            if(data.contains(Constants.SECURITY_KEY)) {
                data = data.substring(0, data.length() - Constants.SECURITY_KEY.length() - 1);
            }
            _receivedInfo.setText(new EncryptDecrypt().decrypt(data));
            ClientInfo info = new Gson().fromJson(data, ClientInfo.class);
            Float totalAmount = info.getPayAmount() + PreferenceManager.getInstance(getContext()).getMerchantBalance();
            PreferenceManager.getInstance(getContext()).setMerchantBalance(totalAmount);
            _merchantBalance.setText("Available Balance: ₹" + PreferenceManager.getInstance(getContext()).getMerchantBalance());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Some thing bad happened !!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.qr_scan_merchant_fragment:
                new PermissionChecker(getActivity(), allowPermissions, new PermissionChecker.PermissionCallback() {
                    @Override
                    public void onSuccess() {
                        onClickListener.openScannerActivity(MerchantFragment.this);
                    }

                    @Override
                    public void onFailure() {
                        Snackbar.make(view, "Camera Permission Required for Scan QR Code", Snackbar.LENGTH_LONG).show();
                    }
                }).checkPermissions();
                break;

            case R.id.bt_scan_merchant_fragment:
                scanBTDevices();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.qr_merchant_fragment:
                if(b)
                    _qrScanner.setVisibility(View.VISIBLE);
                else
                    _qrScanner.setVisibility(View.GONE);
                break;

            case R.id.bt_merchant_fragment:
                if(b)
                    _btScanner.setVisibility(View.VISIBLE);
                else
                    _btScanner.setVisibility(View.GONE);
                break;
        }
    }

    private void scanBTDevices() {
        mSmoothBluetooth.tryConnection();
    }

    @Override
    public void onBluetoothNotSupported() {
        Toast.makeText(getContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBluetoothNotEnabled() {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetooth, ENABLE_BT_REQUEST);
    }

    @Override
    public void onConnecting(Device device) {
        Toast.makeText(getContext(), "Connecting with device - " + device.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Device device) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(Device device) {
        Toast.makeText(getContext(), "Connection Failed with device - " + device.getName(), Toast.LENGTH_SHORT).show();
        if (device.isPaired()) {
            mSmoothBluetooth.doDiscovery();
        }
    }

    @Override
    public void onDiscoveryStarted() {

    }

    @Override
    public void onDiscoveryFinished() {

    }

    @Override
    public void onNoDevicesFound() {
        Toast.makeText(getContext(), "No Devices Found !!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDevicesFound(final List<Device> deviceList, final SmoothBluetooth.ConnectionCallback connectionCallback) {

    }

    @Override
    public void onDataReceived(int data) {
        Toast.makeText(getContext(), "Received Data :" + data, Toast.LENGTH_LONG).show();
        mBuffer.add(data);
        if (data == 62 && !mBuffer.isEmpty()) {
            //if (data == 0x0D && !mBuffer.isEmpty() && mBuffer.get(mBuffer.size()-2) == 0xA0) {
            StringBuilder sb = new StringBuilder();
            for (int integer : mBuffer) {
                sb.append((char)integer);
            }
            mBuffer.clear();
            try {
                String receivedData = sb.toString();
                if(receivedData.contains(Constants.SECURITY_KEY)) {
                    receivedData = receivedData.substring(0, receivedData.length() - Constants.SECURITY_KEY.length() - 1);
                }
                _receivedInfo.setText(new EncryptDecrypt().decrypt(receivedData));
                ClientInfo info = new Gson().fromJson(receivedData, ClientInfo.class);
                Float totalAmount = info.getPayAmount() + PreferenceManager.getInstance(getContext()).getMerchantBalance();
                PreferenceManager.getInstance(getContext()).setMerchantBalance(totalAmount);
                _merchantBalance.setText("Available Balance: ₹" + PreferenceManager.getInstance(getContext()).getMerchantBalance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSmoothBluetooth.disconnect();
        mSmoothBluetooth.stop();
    }
}
