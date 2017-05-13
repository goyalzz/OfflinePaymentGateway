package com.offlinepaymentmethods.controller.fragment;

import android.bluetooth.BluetoothAdapter;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.offlinepaymentmethods.R;
import com.offlinepaymentmethods.controller.activity.QRGeneratorActivity;
import com.offlinepaymentmethods.controller.base.BaseFragment;
import com.offlinepaymentmethods.dto.ClientInfo;
import com.offlinepaymentmethods.dto.PaymentMethod;
import com.offlinepaymentmethods.utils.Constants;
import com.offlinepaymentmethods.utils.EncryptDecrypt;
import com.offlinepaymentmethods.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by aakashsharma on 12/05/17.
 */

public class ClientFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SmoothBluetooth.Listener {

    @InjectView(R.id.id_client_fragment)
    TextView _clientId;

    @InjectView(R.id.name_client_fragment)
    TextView _clientName;

    @InjectView(R.id.email_client_fragment)
    TextView _clientEmail;

    @InjectView(R.id.balance_client_fragment)
    TextView _clientBalance;

    @InjectView(R.id.cod_client_fragment)
    RadioButton _codPayment;

    @InjectView(R.id.qr_client_fragment)
    RadioButton _qrPayment;

    @InjectView(R.id.bt_client_fragment)
    RadioButton _btPayment;

    @InjectView(R.id.amount_ll_client_fragment)
    LinearLayout _qrAmountll;

    @InjectView(R.id.et_amt_client_fragment)
    EditText _qrAmountEt;

    @InjectView(R.id.qr_gen_client_fragment)
    Button _qrGenerator;

    @InjectView(R.id.amount_bt_ll_client_fragment)
    LinearLayout _btAmountll;

    @InjectView(R.id.et_amt_bt_client_fragment)
    EditText _btAmountEt;

    @InjectView(R.id.bt_srch_client_fragment)
    Button _btSearch;

    private SmoothBluetooth mSmoothBluetooth;

    private ClientInfo clientInfo;

    private List<Integer> mBuffer = new ArrayList<>();

    private static final int ENABLE_BT_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.client_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        _clientBalance.setText("Available Balance: ₹" + PreferenceManager.getInstance(getContext()).getClientBalance());
        mSmoothBluetooth = new SmoothBluetooth(getContext(), SmoothBluetooth.ConnectionTo.ANDROID_DEVICE, SmoothBluetooth.Connection.SECURE, this);
        _codPayment.setOnCheckedChangeListener(this);
        _qrPayment.setOnCheckedChangeListener(this);
        _btPayment.setOnCheckedChangeListener(this);
        _qrGenerator.setOnClickListener(this);
        _btSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qr_gen_client_fragment:
                payByQRCode(view);
                break;
            case R.id.bt_srch_client_fragment:
                payByBT(view);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cod_client_fragment:
                break;

            case R.id.qr_client_fragment:
                if(b)
                    _qrAmountll.setVisibility(View.VISIBLE);
                else
                    _qrAmountll.setVisibility(View.GONE);
                break;

            case R.id.bt_client_fragment:
                if(b)
                    _btAmountll.setVisibility(View.VISIBLE);
                else
                    _btAmountll.setVisibility(View.GONE);
                break;
        }
    }

    private void payByQRCode(View view) {
        try {
            clientInfo = validateData(view, PaymentMethod.QR);
            if(clientInfo != null) {
                PreferenceManager.getInstance(getContext()).setClientBalance(PreferenceManager.getInstance(getContext()).getClientBalance() - clientInfo.getPayAmount());
                _clientBalance.setText("Available Balance: ₹" + PreferenceManager.getInstance(getContext()).getClientBalance());
                Intent intent = new Intent(getActivity(), QRGeneratorActivity.class);
                Bundle bdl = new Bundle();
                bdl.putSerializable("ClientData", clientInfo);
                intent.putExtras(bdl);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(view, "Invalid Input !!!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void payByBT(View view) {
        try {
            clientInfo = validateData(view, PaymentMethod.BT);
            if(clientInfo != null) {
                discoverBTDevices();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(view, "Invalid Input !!!", Snackbar.LENGTH_LONG).show();
        }
    }

    private ClientInfo validateData(View view, PaymentMethod paymentMethod) throws Exception {
        String availableBalance = _clientBalance.getText().toString().split(getResources().getString(R.string.rupee_symbol))[1];
        String amountToPay = null;
        if(_qrPayment.isChecked())
            amountToPay = _qrAmountEt.getText().toString();
        else if(_btPayment.isChecked())
            amountToPay = _btAmountEt.getText().toString();
        Float avlBal = Float.parseFloat(availableBalance);
        Float payAmt = Float.parseFloat(amountToPay);
        if (avlBal >= payAmt) {
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setId(Integer.parseInt(_clientId.getText().toString().split(" ")[2]));
            clientInfo.setAvailableAmount(avlBal);
            clientInfo.setPayAmount(payAmt);
            clientInfo.setMerchantId(123);
            clientInfo.setPaymentMethod(paymentMethod);
            return clientInfo;
        } else {
            Snackbar.make(view, "Payment amount should be less or equal to available balance", Snackbar.LENGTH_LONG).show();
        }
        return null;
    }

    private void discoverBTDevices() {
        mSmoothBluetooth.tryConnection();
        mSmoothBluetooth.doDiscovery();
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
        if(device.isPaired()) {
            try {
                mSmoothBluetooth.send(new EncryptDecrypt().encrypt(clientInfo.toString()) + Constants.SECURITY_KEY);
                PreferenceManager.getInstance(getContext()).setClientBalance(PreferenceManager.getInstance(getContext()).getClientBalance() - clientInfo.getPayAmount());
                _clientBalance.setText("Available Balance: ₹" + PreferenceManager.getInstance(getContext()).getClientBalance());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Unable to send data !!!", Toast.LENGTH_LONG).show();
            }
        }
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
        List<String> deviceNameList = new ArrayList<>();
        for (Device device: deviceList) {
            deviceNameList.add(device.getName() + " - " + device.getAddress());
        }

        final CharSequence[] items = deviceNameList.toArray(new CharSequence[deviceNameList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(ClientFragment.this.getActivity());
        builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String deviceName = items[item].toString();
                for (Device device: deviceList) {
                    if((device.getName() + " - " + device.getAddress()).equals(deviceName)) {
                        connectionCallback.connectTo(device);
                    }
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDataReceived(int data) {
        mBuffer.add(data);
        if (data == 62 && !mBuffer.isEmpty()) {
            //if (data == 0x0D && !mBuffer.isEmpty() && mBuffer.get(mBuffer.size()-2) == 0xA0) {
            StringBuilder sb = new StringBuilder();
            for (int integer : mBuffer) {
                sb.append((char)integer);
            }
            mBuffer.clear();
            Toast.makeText(getContext(), "Data Received : " + sb.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSmoothBluetooth.disconnect();
        mSmoothBluetooth.stop();
    }
}
