package com.innotek.ifieldborad;

import com.innotek.ifieldborad.utils.UpgradeApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SilentInstallerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
			UpgradeApp.installAPK(context);
		}
	}

}
